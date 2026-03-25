package com.woogleFX.editorObjects;

import com.woogleFX.editorObjects.attributes.EditorAttribute;
import com.woogleFX.editorObjects.attributes.InputField;
import com.woogleFX.editorObjects.objectCreators.ObjectCreator;
import com.woogleFX.assets.GameVersion;
import com.worldOfGoo2.level._2_Level;
import javafx.geometry.Point2D;

import java.util.Arrays;

public class ObjectUtil {

    public static Point2D rotate(Point2D input, double theta, Point2D center){

        double rotatedX = (input.getX() - center.getX()) * Math.cos(theta) - (input.getY() - center.getY()) * Math.sin(theta);
        double rotatedY = (input.getX() - center.getX()) * Math.sin(theta) + (input.getY() - center.getY()) * Math.cos(theta);

        return new Point2D(rotatedX + center.getX(), rotatedY + center.getY());

    }


    public static <T extends EditorObject> T deepClone(T editorObject, EditorObject parent) {

        T clone;
        if (editorObject.getVersion() == GameVersion.VERSION_WOG2)
            clone = ObjectCreator.create(parent == null ? _2_Level.class : parent.getAttribute(editorObject.getTypeID()).getChildAlias(), parent, editorObject.getTypeID(), editorObject.getVersion());
        else clone = ObjectCreator.create(editorObject.getClass(), parent, editorObject.getVersion());

        for (EditorObject child : editorObject.getChildren()) {
            deepClone(child, clone);
        }

        cloneAllAttributes(editorObject, clone);

        return clone;
    }


    public static void cloneAllAttributes(EditorObject editorObject, EditorObject clone) {

        for (EditorAttribute attribute : editorObject.getAttributes()) {
            if (attribute.getType() != InputField._2_CHILD &&
                    attribute.getType() != InputField._2_CHILD_HIDDEN &&
                    attribute.getType() != InputField._2_LIST_CHILD &&
                    attribute.getType() != InputField._2_LIST_CHILD_HIDDEN)
                clone.setAttribute(attribute.getName(), attribute.stringValue());
        }

        int i = 0;
        for (EditorObject child : editorObject.getChildren()) {
            cloneAllAttributes(child, clone.getChildren().get(i));
            i++;
        }

    }


    public static boolean attributeContainsTag(String[] list, String tag) {
        return Arrays.asList(list).contains(tag);
    }

}
