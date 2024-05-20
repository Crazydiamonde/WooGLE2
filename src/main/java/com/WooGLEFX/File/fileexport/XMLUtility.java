package com.WooGLEFX.File.fileexport;

import com.WooGLEFX.EditorObjects.EditorAttribute;
import com.WooGLEFX.EditorObjects.EditorObject;
import com.WorldOfGoo.Addin.AddinLevelOCD;
import com.WorldOfGoo.Level.Level;
import com.WorldOfGoo.Level.Levelexit;
import com.WorldOfGoo.Resrc.*;
import com.WorldOfGoo.Scene.Scene;
import com.WorldOfGoo.Text.TextStrings;

public class XMLUtility {

    private static void addAttributeToExport(StringBuilder exportBuilder, EditorAttribute attribute, String defaultsPath, String defaultsPrefix, boolean isResource) {

        String attributeName = attribute.getName();

        if (attribute.stringValue().isEmpty() && !attribute.getRequiredInFile()) return;

        if ((attributeName.equals("up") || attributeName.equals("over") || attributeName.equals("disabled"))) {
            exportBuilder.append(attributeName).append("=\"\" ");
            return;
        }

        if (isResource) {
            // Remove prefixes from id and path on save
            String attributeValue = attribute.stringValue();
            if (attributeName.equals("id")) {
                // Remove defaultsPrefix from id
                if (attributeValue.startsWith(defaultsPrefix)) {
                    attributeValue = attributeValue.substring(defaultsPrefix.length());
                }
            }
            if (attributeName.equals("path")) {
                // Remove defaultsPath from path
                if (attributeValue.startsWith(defaultsPath)) {
                    attributeValue = attributeValue.substring(defaultsPath.length());
                }
            }
            exportBuilder.append(attributeName).append("=\"").append(attributeValue).append("\" ");
        } else {
            if (attribute.stringValue().isEmpty() && attribute.getRequiredInFile()) {
                exportBuilder.append(attributeName).append("=\"").append(attribute.getDefaultValue()).append("\" ");
            } else {
                exportBuilder.append(attributeName).append("=\"").append(attribute.stringValue()).append("\" ");
            }
        }

    }


    public static String recursiveXMLexport(String export, EditorObject object, int spaces, boolean children) {
        return recursiveXMLexport(export, object, spaces, children, "", "");
    }


    private static String recursiveXMLexport(String export, EditorObject object, int spaces, boolean children, String defaultsPath, String defaultsPrefix) {

        export += "\t".repeat(spaces) + "<" + object.getType() + " ";
        StringBuilder exportBuilder = new StringBuilder(export);

        boolean isResource = object instanceof Sound || object instanceof ResrcImage;

        for (EditorAttribute attribute : object.getAttributes())
            addAttributeToExport(exportBuilder, attribute, defaultsPath, defaultsPrefix, isResource);

        export = exportBuilder.toString();
        export = export.substring(0, export.length() - 1);

        boolean shouldAlwaysDisplayChildren = (
                object instanceof Levelexit ||
                object instanceof Scene ||
                object instanceof Level ||
                object instanceof ResourceManifest ||
                object instanceof Resources ||
                object instanceof TextStrings
        );
        if (object.getChildren().size() > 0 || shouldAlwaysDisplayChildren) {
            export += " >\n";
            if (children) {
                for (EditorObject child : object.getChildren()) {
                    String curDefaultsPath = "";
                    String curDefaultsPrefix = "";
                    if(child instanceof SetDefaults) {
                        curDefaultsPath = child.getAttribute("path").stringValue();
                        curDefaultsPrefix = child.getAttribute("idprefix").stringValue();
                    }
                    export = recursiveXMLexport(export, child, spaces + 1, true, curDefaultsPath, curDefaultsPrefix) + "\n";
                }
                export += "\t".repeat(spaces) + "</" + object.getType() + ">";
            }
        } else {
            export += " />";
        }

        return export;

    }


    public static String fullAddinXMLExport(String export, EditorObject object, int spaces) {
        if (object instanceof AddinLevelOCD) {
            if (object.getAttribute("type").stringValue().isEmpty() && object.getAttribute("value").stringValue().isEmpty()) {
                export += "\t".repeat(spaces) + "<ocd />";
            } else {
                export += "\t".repeat(spaces) + "<ocd>" + object.getAttribute("type") + "," + object.getAttribute("value") + "</ocd>";
            }
        } else if (object.getAttributes().length == 1 && object.getChildren().size() == 0) {
            if (object.getAttributes()[0].stringValue().equals("")) {
                export += "\t".repeat(spaces) + "<" + object.getType() + " />";
            } else {
                export += "\t".repeat(spaces) + "<" + object.getType() + ">" + object.getAttributes()[0].stringValue() + "</" + object.getType() + ">";
            }
        } else {
            export += "\t".repeat(spaces) + "<" + object.getType() + " ";
            StringBuilder exportBuilder = new StringBuilder(export);
            for (EditorAttribute attribute : object.getAttributes()) {
                if (!attribute.stringValue().equals("") || attribute.getRequiredInFile()) {
                    if ((attribute.getName().equals("up") || attribute.getName().equals("over") || attribute.getName().equals("disabled"))) {
                        exportBuilder.append(attribute.getName()).append("=\"\" ");
                    } else {
                        exportBuilder.append(attribute.getName()).append("=\"").append(attribute.stringValue()).append("\" ");
                    }
                }
            }
            export = exportBuilder.toString();
            export = export.substring(0, export.length() - 1);

            if (object.getChildren().size() > 0) {
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
