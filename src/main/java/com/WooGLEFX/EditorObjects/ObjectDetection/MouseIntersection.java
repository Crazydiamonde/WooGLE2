package com.WooGLEFX.EditorObjects.ObjectDetection;

import com.WooGLEFX.EditorObjects.Components.ObjectPosition;
import com.WooGLEFX.EditorObjects.ObjectCollision.*;
import com.WooGLEFX.Structures.EditorObject;
import com.WooGLEFX.Structures.SimpleStructures.DragSettings;
import com.WorldOfGoo.Level.BallInstance;
import javafx.scene.image.Image;

public class MouseIntersection {

    public static DragSettings mouseIntersection(EditorObject editorObject, double mouseX, double mouseY) {

        if (!editorObject.isVisible()) return DragSettings.NULL;

        if (editorObject instanceof BallInstance ballInstance) {
            return ballInstance.mouseIntersection(mouseX, mouseY);
        }

        for (ObjectPosition objectPosition : editorObject.getObjectPositions()) {

            DragSettings dragSettings = forPosition(objectPosition, mouseX, mouseY, editorObject.getImage());
            if (dragSettings != DragSettings.NULL) return dragSettings;

        }

        return DragSettings.NULL;

    }


    public static DragSettings forPosition(ObjectPosition objectPosition, double mouseX, double mouseY, Image image) {

        switch (objectPosition.getId()) {

            case ObjectPosition.POINT -> {
                return DragSettings.NULL;
            }
            case ObjectPosition.RECTANGLE -> {
                return RectangleCollider.mouseIntersection(objectPosition, mouseX, mouseY, 0);
            }
            case ObjectPosition.RECTANGLE_HOLLOW -> {
                return RectangleCollider.mouseEdgeIntersection(objectPosition, mouseX, mouseY);
            }
            case ObjectPosition.CIRCLE -> {
                return CircleCollider.mouseIntersection(objectPosition, mouseX, mouseY, 0);
            }
            case ObjectPosition.CIRCLE_HOLLOW -> {
                return CircleCollider.mouseEdgeIntersection(objectPosition, mouseX, mouseY);
            }
            case ObjectPosition.ANCHOR -> {
                return AnchorCollider.mouseIntersection(objectPosition, mouseX, mouseY);
            }
            case ObjectPosition.IMAGE -> {
                if (image == null) return DragSettings.NULL;
                return ImageCollider.mouseIntersection(objectPosition, mouseX, mouseY, image);
            }
            case ObjectPosition.LINE -> {
                return LineCollider.mouseIntersection(objectPosition, mouseX, mouseY);
            }

        }

        throw new RuntimeException("Invalid object position id: " + objectPosition.getId());

    }

}
