package com.woogleFX.file.fileExport;

import com.woogleFX.editorObjects.EditorObject;
import com.woogleFX.editorObjects.attributes.EditorAttribute;
import com.woogleFX.editorObjects.attributes.InputField;
import com.woogleFX.editorObjects.objectCreators.ObjectCreator;
import com.woogleFX.assets.GameVersion;
import com.worldOfGoo2.level.TerrainBall;

import java.util.ArrayList;
import java.util.List;

public class GOOWriter {

    private static void exportGooObjectAttributes(StringBuilder output, EditorObject object, int spaces) {

        ArrayList<EditorAttribute> attributes = new ArrayList<>(List.of(object.getAttributes()));

        for (EditorAttribute attribute : attributes.toArray(EditorAttribute[]::new)) {
            if (attribute.actualValue().isEmpty() && (!(attribute.getType() == InputField._2_CHILD || attribute.getType() == InputField._2_CHILD_HIDDEN || attribute.getType() == InputField._2_LIST_CHILD || attribute.getType() == InputField._2_LIST_CHILD_HIDDEN) || object.getChildren(attribute.getName()).isEmpty()) && !attribute.getRequired()) attributes.remove(attribute);
            if (attribute.getName().equals("terrainGroup")) attributes.remove(attribute);
        }

        for (EditorAttribute attribute : attributes) {

            // Name
            output.append("\t".repeat(spaces));
            output.append("\"");
            output.append(attribute.getName());
            output.append("\"");

            // :
            output.append(":");
            output.append("\t");

            // Value
            if (
                attribute.getType() == InputField._2_CHILD ||
                attribute.getType() == InputField._2_CHILD_HIDDEN
            ) {

                output.append("{");
                output.append("\n");

                exportGooObjectAttributes(output, object.getChild(attribute.getName()), spaces + 1);

                output.append("\t".repeat(spaces));
                output.append("}");

            } else if (
                attribute.getType() == InputField._2_LIST_CHILD ||
                attribute.getType() == InputField._2_LIST_CHILD_HIDDEN
            ) {

                output.append("[");

                ArrayList<EditorObject> children = new ArrayList<>(object.getChildren(attribute.getName()));
                if (attribute.getName().equals("terrainBalls")) {
                    for (EditorObject ball : object.getChildren("balls")) {
                        EditorObject terrainBall = ObjectCreator.create(TerrainBall.class, null, "terrainBalls", GameVersion.VERSION_WOG2);
                        terrainBall.setAttribute("group", ball.getAttribute("terrainGroup").stringValue());
                        children.add(terrainBall);
                    }
                }

                for (EditorObject child : children) {
                    output.append("{");
                    output.append("\n");
                    exportGooObjectAttributes(output, child, spaces + 2);
                    output.append("\t".repeat(spaces + 1));
                    output.append("}");
                    if (child != children.get(children.size() - 1)) {
                        output.append(", ");
                    }
                }

                output.append("]");

            } else if (attribute.getType() == InputField.STRING ||
                    attribute.getType() == InputField._2_UUID ||
                    attribute.getType() == InputField._2_ENVIRONMENT_ID ||
                    attribute.getType() == InputField._2_MUSIC_ID ||
                    attribute.getType() == InputField._2_SOUND_ID ||
                    attribute.getType() == InputField._2_ITEM_TYPE ||
                    attribute.getType() == InputField._2_BACKGROUND_ID ||
                    attribute.getType() == InputField._2_AMBIENCE_ID
            ) {

                output.append("\"");
                output.append(attribute.stringValue());
                output.append("\"");

            } else if (
                    attribute.getType() == InputField._2_LIST_STRING ||
                    attribute.getType() == InputField._2_LIST_NUMBER
            ) {

                output.append("[");

                String[] values = attribute.listValue();
                for (int i = 0; i < values.length; i++) {

                    String value = values[i];

                    if (attribute.getType() == InputField._2_LIST_STRING) {
                        output.append("\"");
                        output.append(value);
                        output.append("\"");
                    } else {
                        output.append(value);
                    }

                    if (i != values.length - 1) {
                        output.append(", ");
                    }

                }

                output.append("]");

            } else {

                if (attribute.stringValue().isEmpty()) {
                    throw new RuntimeException("Invalid attribute value: " + object.getType() + "." + attribute.getName());
                }

                output.append(attribute.stringValue());

            }

            if (attribute != attributes.get(attributes.size() - 1)) {
                output.append(",");
            }

            output.append("\n");

        }

    }

    public static void recursiveGOOExport(StringBuilder exportBuilder, EditorObject object, int spaces) {

        exportBuilder.append("{");
        exportBuilder.append("\n");
        exportGooObjectAttributes(exportBuilder, object, spaces + 1);
        exportBuilder.append("}");
        exportBuilder.append("\n");

    }

}
