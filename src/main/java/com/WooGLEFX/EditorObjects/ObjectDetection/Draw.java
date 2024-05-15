package com.WooGLEFX.EditorObjects.ObjectDetection;

import com.WooGLEFX.EditorObjects.Components.ObjectPosition;
import com.WooGLEFX.EditorObjects.ObjectDrawing.*;
import com.WooGLEFX.Engine.SelectionManager;
import com.WooGLEFX.Structures.EditorObject;
import com.WorldOfGoo.Scene.Linearforcefield;
import com.WorldOfGoo.Scene.Radialforcefield;
import javafx.scene.canvas.GraphicsContext;

public class Draw {

    public static void draw(GraphicsContext graphicsContext, GraphicsContext imageGraphicsContext, EditorObject editorObject) {

        for (ObjectPosition objectPosition : editorObject.getObjectPositions()) {

            boolean selected = editorObject == SelectionManager.getSelected();
            forPosition(graphicsContext, imageGraphicsContext, objectPosition, selected);

        }

    }


    public static void forPosition(GraphicsContext graphicsContext, GraphicsContext imageGraphicsContext,
                                   ObjectPosition objectPosition, boolean selected) {

        if (!objectPosition.isVisible()) return;

        graphicsContext.save();
        imageGraphicsContext.save();

        switch (objectPosition.getId()) {

            case ObjectPosition.RECTANGLE, ObjectPosition.RECTANGLE_HOLLOW -> RectangleDrawer.draw(graphicsContext, objectPosition, selected);
            case ObjectPosition.CIRCLE, ObjectPosition.CIRCLE_HOLLOW -> CircleDrawer.draw(graphicsContext, objectPosition, selected);
            case ObjectPosition.ANCHOR -> AnchorDrawer.draw(graphicsContext, objectPosition, selected);
            case ObjectPosition.IMAGE -> ImageDrawer.draw(graphicsContext, imageGraphicsContext, objectPosition, selected);
            case ObjectPosition.LINE -> LineDrawer.draw(graphicsContext, objectPosition, selected);

        }

        graphicsContext.restore();
        imageGraphicsContext.restore();

    }

}
