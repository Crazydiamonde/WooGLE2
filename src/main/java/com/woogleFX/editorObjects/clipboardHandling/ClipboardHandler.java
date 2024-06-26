package com.woogleFX.editorObjects.clipboardHandling;

import com.woogleFX.editorObjects.objectCreators.ObjectCreator;
import com.woogleFX.editorObjects.attributes.EditorAttribute;
import com.woogleFX.editorObjects.EditorObject;
import com.woogleFX.engine.LevelManager;
import com.woogleFX.gameData.level._Level;

import java.util.ArrayList;

public class ClipboardHandler {

    public static EditorObject[] importFromClipboardString(String clipboard) {

        //WOGEditor:circle<id=wheel;x=2;y=1024.9834;radius=120;material=machine;tag=mostlydeadly>

        EditorObject object = null;

        StringBuilder currentWord = new StringBuilder();
        String attributeName = "";
        boolean settingAttribute = false;

        EditorObject selected;
        EditorObject[] selectedList = LevelManager.getLevel().getSelected();
        if (selectedList.length == 1) selected = selectedList[0];
        else selected = null;

        ArrayList<EditorObject> selectionBuilder = new ArrayList<>();

        for (int i = 0; i < clipboard.length(); i++){
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

                    _Level level = LevelManager.getLevel();

                    boolean okayToBeChild = selected != null && selected.getParent() != null;

                    if (okayToBeChild) {
                        okayToBeChild = false;
                        for (String possibleChild : selected.getParent().getPossibleChildren()) {
                            if (possibleChild.contentEquals(currentWord)) {
                                okayToBeChild = true;
                                break;
                            }
                        }
                    }

                    EditorObject parent = okayToBeChild ? selected.getParent() : null;
                    object = ObjectCreator.create(currentWord.toString(), parent, level.getVersion());
                    currentWord = new StringBuilder();
                    settingAttribute = true;
                } else {
                    currentWord.append(part);
                }
            }
        }

        return selectionBuilder.toArray(new EditorObject[0]);

    }

    public static String exportToClipBoardString(EditorObject[] selectedList) {

        StringBuilder clipboard = new StringBuilder("WOGEditor:");

        for (EditorObject object : selectedList) {

            clipboard.append(object.getType());

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
