package com.WooGLEFX.EditorObjects.ObjectDrawing;

import com.WooGLEFX.EditorObjects.ObjectPosition;
import com.WooGLEFX.Engine.Renderer;
import com.WooGLEFX.Functions.LevelManager;
import javafx.scene.canvas.GraphicsContext;

public class LineDrawer {

    public static void draw(GraphicsContext graphicsContext, ObjectPosition objectPosition, boolean selected) {

        double x = objectPosition.getX();
        double y = objectPosition.getY();

        double rotation = objectPosition.getRotation();

        double offsetX = LevelManager.getLevel().getOffsetX();
        double offsetY = LevelManager.getLevel().getOffsetY();
        double zoom = LevelManager.getLevel().getZoom();

        double screenX = x * zoom + offsetX;
        double screenY = y * zoom + offsetY;
        graphicsContext.setLineWidth(zoom * objectPosition.getEdgeSize());

        double xComponent = Math.cos(rotation);
        double yComponent = Math.sin(rotation);

        double deltaX = 10000 * xComponent * zoom;
        double deltaY = 10000 * yComponent * zoom;

        // TODO make lines render infinitely far

        graphicsContext.setStroke(objectPosition.getBorderColor());
        graphicsContext.strokeLine(screenX - deltaX, screenY - deltaY, screenX + deltaX, screenY + deltaY);

        if (selected) {
            double screenX2 = screenX - 20 * zoom * yComponent;
            double screenY2 = screenY + 20 * zoom * xComponent;

            graphicsContext.setLineDashes(0);

            graphicsContext.setStroke(Renderer.selectionOutline);
            graphicsContext.setLineWidth(1);

            graphicsContext.strokeLine(screenX2 - deltaX, screenY2 - deltaY, screenX2 + deltaX, screenY2 + deltaY);

            graphicsContext.setStroke(Renderer.selectionOutline);
            graphicsContext.setLineWidth(1);
            graphicsContext.setLineDashes(3);
        }

    }

}
