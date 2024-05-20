package com.WooGLEFX.EditorObjects.ObjectDetection;

import com.WooGLEFX.EditorObjects.ObjectPosition;
import com.WooGLEFX.EditorObjects.ObjectDrawing.*;
import javafx.scene.canvas.GraphicsContext;

public class Draw {

    public static void draw(GraphicsContext graphicsContext, ObjectPosition objectPosition, boolean selected) {

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
