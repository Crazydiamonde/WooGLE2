package com.WooGLEFX.EditorObjects.ObjectDrawing;

import com.WooGLEFX.EditorObjects.Components.ObjectPosition;
import com.WooGLEFX.Engine.Renderer;
import com.WooGLEFX.Functions.LevelManager;
import javafx.scene.canvas.GraphicsContext;

public class CircleDrawer {

    public static void draw(GraphicsContext graphicsContext, ObjectPosition objectPosition, boolean selected) {

        double x = objectPosition.getX();
        double y = objectPosition.getY();
        double radius = objectPosition.getRadius();

        double offsetX = LevelManager.getLevel().getOffsetX();
        double offsetY = LevelManager.getLevel().getOffsetY();
        double zoom = LevelManager.getLevel().getZoom();

        double screenX = (x - radius) * zoom + offsetX;
        double screenY = (y - radius) * zoom + offsetY;

        graphicsContext.setFill(objectPosition.getFillColor());

        graphicsContext.fillOval(screenX + zoom / 2, screenY + zoom / 2,
                (radius - 0.5) * 2 * zoom, (radius - 0.5) * 2 * zoom);

        graphicsContext.setStroke(objectPosition.getBorderColor());

        double woag = Math.min(objectPosition.getEdgeSize(), Math.abs(radius) / 2) / 2;

        graphicsContext.setLineWidth(woag * 2 * zoom);
        graphicsContext.strokeOval(screenX + woag * zoom, screenY + woag * zoom,
                (radius - woag) * 2 * zoom, (radius - woag) * 2 * zoom);

        if (selected) {

            graphicsContext.setStroke(Renderer.selectionOutline2);
            graphicsContext.setLineWidth(1);
            graphicsContext.setLineDashes(3);
            graphicsContext.setLineDashOffset(0);
            graphicsContext.strokeRect(screenX, screenY, radius * 2 * zoom, radius * 2 * zoom);

            graphicsContext.setStroke(Renderer.selectionOutline);
            graphicsContext.setLineWidth(1);
            graphicsContext.setLineDashOffset(3);
            graphicsContext.strokeRect(screenX, screenY, radius * 2 * zoom, radius * 2 * zoom);
            graphicsContext.setLineDashes(0);

            if (objectPosition.isResizable()) {
                graphicsContext.strokeRect(screenX - 4, screenY + radius * zoom - 4, 8, 8);
                graphicsContext.strokeRect(screenX + radius * zoom - 4, screenY + radius * 2 * zoom - 4, 8, 8);
                graphicsContext.strokeRect(screenX + radius * zoom - 4, screenY - 4, 8, 8);
                graphicsContext.strokeRect(screenX + radius * 2 * zoom - 4, screenY + radius * zoom - 4, 8, 8);
            }

            if (objectPosition.isRotatable()) {
                graphicsContext.strokeOval(screenX - 4, screenY + radius * zoom - 4, 8, 8);
                graphicsContext.strokeOval(screenX + radius * 2 * zoom - 4, screenY + radius * zoom - 4, 8, 8);
            }

        }

    }

}
