package com.woogleFX.file.fileExport;

import com.woogleFX.editorObjects.EditorAttribute;
import com.woogleFX.editorObjects.EditorObject;
import com.woogleFX.editorObjects.InputField;
import com.worldOfGoo.addin.AddinLevelOCD;
import com.worldOfGoo.level.Level;
import com.worldOfGoo.level.Levelexit;
import com.worldOfGoo.resrc.*;
import com.worldOfGoo.scene.Scene;
import com.worldOfGoo.text.TextStrings;

public class XMLUtility {

    /** Replaces all " with &quot;. */
    private static String handleDoubleQuotes(String input) {
        return input.replace("\"", "&quot;");
    }


    private static void addAttributeToExport(StringBuilder exportBuilder, EditorAttribute attribute) {

        // If the attribute is both empty and not required, don't add it to the file.
        if (attribute.actualValue().isEmpty() && !attribute.getRequiredInFile()) return;

        String name = attribute.getName();
        String value = handleDoubleQuotes(attribute.stringValue());
        String completeAttribute = name + "=\"" + value + "\"";
        exportBuilder.append(completeAttribute);

    }


    public static String XMLExport(EditorObject object) {
        StringBuilder exportBuilder = new StringBuilder();
        recursiveXMLExport(exportBuilder, object);
        return exportBuilder.toString();
    }


    private static void recursiveXMLExport(StringBuilder exportBuilder, EditorObject object) {

        // Count the number of parents this object has - this will correspond to the number of spaces it should be at
        int spaces = 0;
        EditorObject absoluteParent = object;
        while ((absoluteParent = absoluteParent.getParent()) != null) spaces++;

        exportBuilder.append("\t".repeat(spaces)).append("<").append(object.getType()).append(" ");

        for (int i = 0; i < object.getAttributes().length; i++) {

            EditorAttribute attribute = object.getAttributes()[i];

            if (attribute.getName().equals("tag")) {

                String breakValue = object.getAttribute("break").stringValue();
                if (!breakValue.isEmpty()) {
                    breakValue = "break=" + breakValue;
                    if (!attribute.stringValue().isEmpty()) breakValue = "," + breakValue;
                    EditorAttribute secretTagAttribute = new EditorAttribute("tag", InputField.TAG, object);
                    secretTagAttribute.setValue(attribute.stringValue() + breakValue);
                    addAttributeToExport(exportBuilder, secretTagAttribute);
                    continue;
                }

            }

            if (!attribute.getName().equals("break")) addAttributeToExport(exportBuilder, attribute);

            if (i != object.getAttributes().length - 1) exportBuilder.append(" ");

        }

        boolean shouldAlwaysDisplayChildren = (
                object instanceof Levelexit ||
                object instanceof Scene ||
                object instanceof Level ||
                object instanceof ResourceManifest ||
                object instanceof Resources ||
                object instanceof TextStrings
        );
        if (!object.getChildren().isEmpty() || shouldAlwaysDisplayChildren) {
            exportBuilder.append(" >\n");
            if (!(object instanceof Scene || object instanceof Level)) {
                for (EditorObject child : object.getChildren()) {
                    recursiveXMLExport(exportBuilder, child);
                    exportBuilder.append("\n");
                }
                exportBuilder.append("\t".repeat(spaces)).append("</").append(object.getType()).append(">");
            }
        } else {
            exportBuilder.append(" />");
        }

    }


    public static String fullAddinXMLExport(String export, EditorObject object, int spaces) {
        if (object instanceof AddinLevelOCD) {
            if (object.getAttribute("type").stringValue().isEmpty() && object.getAttribute("value").stringValue().isEmpty()) {
                export += "\t".repeat(spaces) + "<ocd />";
            } else {
                export += "\t".repeat(spaces) + "<ocd>" + object.getAttribute("type").stringValue() + "," + object.getAttribute("value").stringValue() + "</ocd>";
            }
        } else if (object.getAttributes().length == 1 && object.getChildren().isEmpty()) {
            if (object.getAttributes()[0].stringValue().isEmpty()) {
                export += "\t".repeat(spaces) + "<" + object.getType() + " />";
            } else {
                export += "\t".repeat(spaces) + "<" + object.getType() + ">" + object.getAttributes()[0].stringValue() + "</" + object.getType() + ">";
            }
        } else {
            export += "\t".repeat(spaces) + "<" + object.getType() + " ";
            StringBuilder exportBuilder = new StringBuilder(export);
            for (EditorAttribute attribute : object.getAttributes()) {
                if (!attribute.stringValue().isEmpty() || attribute.getRequiredInFile() || attribute.getName().equals("tag") && !object.getAttribute("break").stringValue().isEmpty()) {
                    exportBuilder.append(attribute.getName()).append("=\"").append(attribute.stringValue()).append("\" ");
                }
            }
            export = exportBuilder.toString();
            export = export.substring(0, export.length() - 1);

            if (!object.getChildren().isEmpty()) {
                export += ">\n";
                for (EditorObject child : object.getChildren()) {
                    export = fullAddinXMLExport(export, child, spaces + 1) + "\n";
                }
                export += "\t".repeat(spaces) + "</" + object.getType() + ">";
            } else {
                export += " />";
            }
        }
        return export;
    }

}
