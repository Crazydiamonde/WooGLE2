package com.WooGLEFX.EditorObjects;

import com.WooGLEFX.Structures.EditorAttribute;
import com.WooGLEFX.Structures.EditorObject;
import javafx.geometry.Point2D;

import java.util.Arrays;

public class ObjectUtil {

    public static Point2D rotate(Point2D input, double theta, Point2D center){

        double rotatedX = (input.getX() - center.getX()) * Math.cos(theta) - (input.getY() - center.getY()) * Math.sin(theta);
        double rotatedY = (input.getX() - center.getX()) * Math.sin(theta) + (input.getY() - center.getY()) * Math.cos(theta);

        return new Point2D(rotatedX + center.getX(), rotatedY + center.getY());

    }


    public static EditorObject deepClone(EditorObject editorObject, EditorObject parent) {
        EditorObject clone = ObjectCreator.create(editorObject.getType(), parent);

        for (EditorAttribute attribute : editorObject.getAttributes()) {
            clone.setAttribute(attribute.getName(), attribute.stringValue());
        }

        for (EditorObject child : editorObject.getChildren()) {
            deepClone(child, clone);
        }
        return clone;
    }


    public static boolean attributeContainsTag(String[] list, String tag) {
        return Arrays.asList(list).contains(tag);
    }

}