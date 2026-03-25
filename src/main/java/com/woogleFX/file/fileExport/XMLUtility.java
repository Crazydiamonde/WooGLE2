package com.woogleFX.file.fileExport;

import com.woogleFX.editorObjects.EditorObject;
import com.woogleFX.editorObjects.attributes.EditorAttribute;
import com.woogleFX.editorObjects.attributes.InputField;
import com.worldOfGoo.addin.ocd;
import com.worldOfGoo.level.level;
import com.worldOfGoo.level.levelexit;
import com.worldOfGoo.particle.particle;
import com.worldOfGoo.resrc.*;
import com.worldOfGoo.scene.scene;
import com.worldOfGoo.text.strings;

public class XMLUtility {

    /** Replaces all " with &quot;. */
    private static String handleDoubleQuotes(String input) {
        return input.replace("\"", "&quot;");
    }


    private static boolean addAttributeToExport(StringBuilder exportBuilder, EditorAttribute attribute, boolean spacedOut, int spaces) {

        // If the attribute is both empty and not required, don't add it to the file.
        if ((attribute.actualValue().isEmpty() || attribute.actualValue().equals(attribute.getDefaultValue())) && !attribute.getRequired()) return false;

        String name = attribute.getName();
        String value = handleDoubleQuotes(attribute.stringValue());
        String completeAttribute = name + "=\"" + value + "\"";
        if (spacedOut && attribute != attribute.getObject().getAttributes()[0]) completeAttribute = "\t".repeat(spaces) + completeAttribute;
        if (spacedOut) completeAttribute += "\n"; // TODO: make this less obviously hardcoded
        exportBuilder.append(completeAttribute);

        return true;

    }


    public static String XMLExport(EditorObject object) {
        StringBuilder exportBuilder = new StringBuilder();
        recursiveXMLExport(exportBuilder, object, 0);
        return exportBuilder.toString();
    }


    public static void recursiveXMLExport(StringBuilder exportBuilder, EditorObject object, int spaces) {

        exportBuilder.append("\t".repeat(spaces)).append("<").append(object.getType()).append(" ");

        for (int i = 0; i < object.getAttributes().length; i++) {

            EditorAttribute attribute = object.getAttributes()[i];

            if (attribute.getName().equals("tag")) {

                String breakValue = object.getAttribute("break").stringValue();
                if (!breakValue.isEmpty()) {
                    breakValue = "break=" + breakValue;
                    if (!attribute.stringValue().isEmpty()) breakValue = "," + breakValue;
                    EditorAttribute secretTagAttribute = new EditorAttribute("tag", InputField._1_TAG, object);
                    secretTagAttribute.setValue(attribute.stringValue() + breakValue);
                    addAttributeToExport(exportBuilder, secretTagAttribute, object instanceof particle, spaces + 2);
                    continue;
                }

            }

            if (!attribute.getName().equals("break")) {
                if (addAttributeToExport(exportBuilder, attribute, object instanceof particle, spaces + 2)) {
                    exportBuilder.append(" ");
                }
            }

        }

        if (object instanceof particle) exportBuilder.deleteCharAt(exportBuilder.length() - 1);
        exportBuilder.deleteCharAt(exportBuilder.length() - 1);

        boolean shouldAlwaysDisplayChildren = (
                object instanceof levelexit ||
                object instanceof scene ||
                object instanceof level ||
                object instanceof ResourceManifest ||
                object instanceof Resources ||
                object instanceof strings
        );
        if (!object.getChildren().isEmpty() || shouldAlwaysDisplayChildren) {
            exportBuilder.append(">\n");
            if (!(object instanceof scene || object instanceof level)) {
                for (EditorObject child : object.getChildren()) {
                    recursiveXMLExport(exportBuilder, child, spaces + 1);
                    exportBuilder.append("\n");
                }
                exportBuilder.append("\t".repeat(spaces)).append("</").append(object.getType()).append(">");
            }
        } else {
            exportBuilder.append("/>");
        }

    }


    public static String fullAddinXMLExport(String export, EditorObject object, int spaces) {
        if (object instanceof ocd) {
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
                if (!attribute.stringValue().isEmpty() || attribute.getRequired() || attribute.getName().equals("tag") && !object.getAttribute("break").stringValue().isEmpty()) {
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
