package com.WooGLEFX.EditorObjects.ObjectDetection;

import com.WooGLEFX.EditorObjects.Components.ObjectPosition;
import com.WooGLEFX.EditorObjects.ObjectDrawing.*;
import com.WooGLEFX.Engine.SelectionManager;
import com.WooGLEFX.Structures.EditorObject;
import com.WorldOfGoo.Level.BallInstance;
import com.WorldOfGoo.Level.Strand;
import com.WorldOfGoo.Scene.Linearforcefield;
import com.WorldOfGoo.Scene.Radialforcefield;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class Draw {

    public static void draw(GraphicsContext graphicsContext, GraphicsContext imageGraphicsContext, EditorObject editorObject) {

        if (!editorObject.isVisible()) return;

        if (editorObject instanceof BallInstance ballInstance) {
            ballInstance.draw(graphicsContext, imageGraphicsContext);
            return;
        }

        if (editorObject instanceof Strand strand) {
            strand.draw(graphicsContext, imageGraphicsContext);
            return;
        }

        for (ObjectPosition objectPosition : editorObject.getObjectPositions()) {

            forPosition(graphicsContext, imageGraphicsContext, objectPosition, editorObject.getImage(), editorObject == SelectionManager.getSelected(), editorObject instanceof Linearforcefield || editorObject instanceof Radialforcefield);

        }

    }


    public static void forPosition(GraphicsContext graphicsContext, GraphicsContext imageGraphicsContext, ObjectPosition objectPosition, Image image, boolean selected, boolean isForceField) {

        switch (objectPosition.getId()) {

            case ObjectPosition.RECTANGLE, ObjectPosition.RECTANGLE_HOLLOW -> RectangleDrawer.draw(graphicsContext, objectPosition, selected, isForceField);
            case ObjectPosition.CIRCLE, ObjectPosition.CIRCLE_HOLLOW -> CircleDrawer.draw(graphicsContext, objectPosition, selected);
            case ObjectPosition.ANCHOR -> AnchorDrawer.draw(graphicsContext, objectPosition, selected);
            case ObjectPosition.IMAGE -> {
                if (image == null) return;
                ImageDrawer.draw(graphicsContext, imageGraphicsContext, objectPosition, image, selected, isForceField);
            }
            case ObjectPosition.LINE -> LineDrawer.draw(graphicsContext, objectPosition, selected);

        }

    }

}
