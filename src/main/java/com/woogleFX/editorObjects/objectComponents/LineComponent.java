package com.woogleFX.editorObjects.objectComponents;

import com.woogleFX.editorObjects.objectComponents.generic.ColoredProperty;
import com.woogleFX.editorObjects.objectComponents.generic.RotatableProperty;
import com.woogleFX.engine.Renderer;
import com.woogleFX.functions.LevelManager;
import com.woogleFX.structures.simpleStructures.DragSettings;
import javafx.scene.canvas.GraphicsContext;

public abstract class LineComponent extends ObjectComponent implements RotatableProperty, ColoredProperty {

    public abstract double getLineWidth();


    @Override
    public void draw(GraphicsContext graphicsContext, boolean selected) {

        double x = getX();
        double y = getY();

        double rotation = getRotation();

        double offsetX = LevelManager.getLevel().getOffsetX();
        double offsetY = LevelManager.getLevel().getOffsetY();
        double zoom = LevelManager.getLevel().getZoom();

        double screenX = x * zoom + offsetX;
        double screenY = y * zoom + offsetY;
        graphicsContext.setLineWidth(zoom * getLineWidth());

        double xComponent = Math.cos(rotation);
        double yComponent = Math.sin(rotation);

        double deltaX = 10000 * xComponent * zoom;
        double deltaY = 10000 * yComponent * zoom;

        // TODO make lines render infinitely far

        graphicsContext.setStroke(getColor());
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


    @Override
    public DragSettings mouseIntersection(double mouseX, double mouseY) {

        double x = getX();
        double y = getY();

        double rotation = getRotation();

        double xComponent = Math.cos(-rotation);
        double yComponent = Math.sin(-rotation);

        // Line equation: (y - y_0) = m(x - x_0)
        //                (y - y_0) = (sin/cos)(x - x_0)
        //                (y - y_0)*cos = (x - x_0)*sin

        // (y - y_0)*cos - (x - x_0)*sin = 0

        // This can be used to check if a position is above or below the line.

        double deltaX = getLineWidth() / 2 * xComponent;
        double deltaY = getLineWidth() / 2 * yComponent;

        double x1 = x - deltaY;
        double y1 = y - deltaX;
        double mouseValue1 = (mouseY - y1) * xComponent + (mouseX - x1) * yComponent;

        double x2 = x + deltaY;
        double y2 = y + deltaX;
        double mouseValue2 = (mouseY - y2) * xComponent + (mouseX - x2) * yComponent;

        if (mouseValue1 > 0 && mouseValue2 > 0 || mouseValue1 < 0 && mouseValue2 < 0) return DragSettings.NULL;

        DragSettings dragSettings = new DragSettings(DragSettings.MOVE, this);
        dragSettings.setInitialSourceX(mouseX - x);
        dragSettings.setInitialSourceY(mouseY - y);
        return dragSettings;

    }


    @Override
    public DragSettings mouseIntersectingCorners(double mouseX, double mouseY) {
        return DragSettings.NULL;
    }

}
