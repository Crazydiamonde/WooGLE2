package com.woogleFX.editorObjects.clipboardHandling;

import com.woogleFX.assets.Asset;
import com.woogleFX.editorObjects.EditorObject;
import com.woogleFX.editorObjects.attributes.EditorAttribute;
import com.woogleFX.editorObjects.objectComponents.ObjectComponent;
import com.woogleFX.editorObjects.objectCreators.ObjectCreator;
import com.woogleFX.engine.AssetManager;
import com.woogleFX.file.fileExport.GOOWriter;
import com.woogleFX.file.fileImport.ObjectGOOParser;
import com.woogleFX.assets.GameVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;

@SuppressWarnings("unchecked")
public class ClipboardHandler {

    private static final Logger logger = LoggerFactory.getLogger(ClipboardHandler.class);


    public static EditorObject[] importFromClipboardString(String clipboard) {

        //WOGEditor:circle<id=wheel;x=2;y=1024.9834;radius=120;material=machine;tag=mostlydeadly>

        EditorObject object = null;

        StringBuilder currentWord = new StringBuilder();
        String attributeName = "";
        boolean settingAttribute = false;

        EditorObject selected;
        ObjectComponent[] selectedList = AssetManager.getAsset().getSelectedComponents();
        if (selectedList.length == 1) selected = selectedList[0].getEditorObject();
        else selected = null;

        ArrayList<EditorObject> selectionBuilder = new ArrayList<>();

        if (AssetManager.getAsset().getVersion() == GameVersion.VERSION_WOG1_OLD || AssetManager.getAsset().getVersion() == GameVersion.VERSION_WOG1_NEW) {

            for (int i = 0; i < clipboard.length(); i++) {
                char part = clipboard.charAt(i);

                if (settingAttribute) {
                    if (part == '=') {
                        attributeName = currentWord.toString();
                        currentWord = new StringBuilder();
                    } else if (part == ';') {
                        object.setAttribute(attributeName, currentWord.toString());
                        currentWord = new StringBuilder();
                    } else if (part == '>') {
                        settingAttribute = false;
                        object.setAttribute(attributeName, currentWord.toString());
                        selectionBuilder.add(object);
                        currentWord = new StringBuilder();
                    } else {
                        currentWord.append(part);
                    }
                } else {
                    if (part == ':') {
                        if (!currentWord.toString().equals("WOGEditor")) {
                            return null;
                        }
                        currentWord = new StringBuilder();
                    } else if (part == '<') {

                        Asset level = AssetManager.getAsset();

                        boolean okayToBeChild = selected != null && selected.getParent() != null;

                        if (okayToBeChild) {
                            okayToBeChild = false;
                            for (Class<? extends EditorObject> possibleChild : selected.getParent().getPossibleChildren()) {
                                if (possibleChild.getName().contentEquals(currentWord)) {
                                    okayToBeChild = true;
                                    break;
                                }
                            }
                        }
                        Class<? extends EditorObject> editorObjectClass;
                        try {
                            editorObjectClass = (Class<? extends EditorObject>) Class.forName(currentWord.toString());
                        } catch (ClassNotFoundException e) {
                            throw new RuntimeException(e);
                        }
                        EditorObject parent = okayToBeChild ? selected.getParent() : level.getDefaultParent(editorObjectClass);
                        // Make sure the object can actually exist here
                        if (parent == null || Arrays.stream(parent.getPossibleChildren()).noneMatch(e -> e == editorObjectClass)) {
                            continue;
                        }
                        object = ObjectCreator.create(editorObjectClass, parent, level.getVersion());
                        currentWord = new StringBuilder();
                        settingAttribute = true;
                    } else {
                        currentWord.append(part);
                    }
                }
            }

        } else {

            String type = clipboard.substring(10, clipboard.indexOf(";"));
            String type2 = clipboard.substring(clipboard.indexOf(";") + 1, clipboard.indexOf("<"));
            String content = clipboard.substring(clipboard.indexOf("<") + 1);
            try {
                selectionBuilder.add(ObjectGOOParser.read((Class<? extends EditorObject>) Class.forName(type), content, type2));
            } catch (ClassNotFoundException e) {
                logger.error("", e);
            }
            return selectionBuilder.toArray(new EditorObject[0]);

        }

        return selectionBuilder.toArray(new EditorObject[0]);

    }

    public static String exportToClipBoardString(EditorObject[] selectedList) {

        if (selectedList[0].getVersion() == GameVersion.VERSION_WOG2) {

            StringBuilder export = new StringBuilder();
            GOOWriter.recursiveGOOExport(export, selectedList[0], 0);
            return "WOGEditor:" + selectedList[0].getParent().getAttribute(selectedList[0].getTypeID()).getChildAlias().getName() + ";" + selectedList[0].getTypeID() + "<" + export;

        } else {

            StringBuilder clipboard = new StringBuilder("WOGEditor:");

            for (EditorObject object : selectedList) {
                clipboard.append(object.getClass().getName());

                clipboard.append("<");

                for (int i = 0; i < object.getAttributes().length; i++) {
                    EditorAttribute attribute = object.getAttributes()[i];
                    if (attribute.stringValue() != null && !attribute.stringValue().equals(attribute.getDefaultValue()) && !attribute.stringValue().isEmpty()) {
                        clipboard.append(attribute.getName()).append("=").append(attribute.stringValue());
                        clipboard.append(";");
                    }
                }

                clipboard.deleteCharAt(clipboard.length() - 1);

                clipboard.append(">");

            }

            return clipboard.toString();

        }

    }


}
