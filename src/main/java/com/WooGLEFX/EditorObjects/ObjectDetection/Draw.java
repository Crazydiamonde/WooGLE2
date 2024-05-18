package com.WooGLEFX.EditorObjects.ObjectDetection;

import com.WooGLEFX.EditorObjects.Components.ObjectPosition;
import com.WooGLEFX.EditorObjects.ObjectDrawing.*;
import com.WooGLEFX.Engine.SelectionManager;
import com.WooGLEFX.Structures.EditorObject;
import javafx.scene.canvas.GraphicsContext;

public class Draw {

    public static void draw(GraphicsContext graphicsContext, EditorObject editorObject) {

        for (ObjectPosition objectPosition : editorObject.getObjectPositions()) {

            boolean selected = editorObject == SelectionManager.getSelected();
            forPosition(graphicsContext, objectPosition, selected);

        }

    }


    public static void forPosition(GraphicsContext graphicsContext, ObjectPosition objectPosition, boolean selected) {

        if (!objectPosition.isVisible()) return;

        graphicsContext.save();

        switch (objectPosition.getId()) {

            case ObjectPosition.RECTANGLE, ObjectPosition.RECTANGLE_HOLLOW -> RectangleDrawer.draw(graphicsContext, objectPosition, selected);
            case ObjectPosition.CIRCLE, ObjectPosition.CIRCLE_HOLLOW -> CircleDrawer.draw(graphicsContext, objectPosition, selected);
            case ObjectPosition.ANCHOR -> AnchorDrawer.draw(graphicsContext, objectPosition, selected);
            case ObjectPosition.IMAGE -> ImageDrawer.draw(graphicsContext, objectPosition, selected);
            case ObjectPosition.LINE -> LineDrawer.draw(graphicsContext, objectPosition, selected);

        }

        graphicsContext.restore();

    }

}
