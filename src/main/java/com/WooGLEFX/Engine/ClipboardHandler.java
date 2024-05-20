package com.WooGLEFX.Engine;

import com.WooGLEFX.EditorObjects.ObjectCreators.ObjectCreator;
import com.WooGLEFX.EditorObjects.EditorAttribute;
import com.WooGLEFX.EditorObjects.EditorObject;

public class ClipboardHandler {

    public static EditorObject importFromClipboardString(String clipboard) {

        //WOGEditor:circle<id=wheel;x=2;y=1024.9834;radius=120;material=machine;tag=mostlydeadly>

        EditorObject object = null;

        String currentWord = "";
        String attributeName = "";
        boolean settingAttribute = false;

        for (int i = 0; i < clipboard.length(); i++){
            char part = clipboard.charAt(i);

            if (settingAttribute) {
                if (part == '=') {
                    attributeName = currentWord;
                    currentWord = "";
                } else if (part == ';') {
                    object.setAttribute(attributeName, currentWord);
                    currentWord = "";
                } else if (part == '>') {
                    settingAttribute = false;
                    object.setAttribute(attributeName, currentWord);
                    currentWord = "";
                } else {
                    currentWord += part;
                }
            } else {
                if (part == ':') {
                    if (!currentWord.equals("WOGEditor")) {
                        return null;
                    }
                    currentWord = "";
                } else if (part == '<') {
                    object = ObjectCreator.create(currentWord, null);
                    currentWord = "";
                    settingAttribute = true;
                } else {
                    currentWord += part;
                }
            }
        }

        return object;

    }

    public static String exportToClipBoardString(EditorObject object) {

        StringBuilder clipboard = new StringBuilder("WOGEditor:");

        clipboard.append(object.getType());

        clipboard.append("<");

        for (int i = 0; i < object.getAttributes().length; i++){
            EditorAttribute attribute = object.getAttributes()[i];
            if (attribute.stringValue() != null && !attribute.stringValue().equals(attribute.getDefaultValue()) && !attribute.stringValue().isEmpty()){
                clipboard.append(attribute.getName()).append("=").append(attribute.stringValue());
                clipboard.append(";");
            }
        }

        clipboard.deleteCharAt(clipboard.length() - 1);

        clipboard.append(">");

        return clipboard.toString();

    }


}
