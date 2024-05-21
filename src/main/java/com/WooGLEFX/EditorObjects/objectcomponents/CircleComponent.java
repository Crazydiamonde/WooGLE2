package com.WooGLEFX.EditorObjects.objectcomponents;

import com.WooGLEFX.EditorObjects.objectcomponents.generic.BorderProperty;
import com.WooGLEFX.EditorObjects.objectcomponents.generic.ColoredProperty;
import com.WooGLEFX.EditorObjects.objectcomponents.generic.RotatableProperty;
import com.WooGLEFX.Engine.Renderer;
import com.WooGLEFX.Functions.LevelManager;
import com.WooGLEFX.Structures.SimpleStructures.DragSettings;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;

/** Represents a circle component in any object. */
public abstract class CircleComponent extends ObjectComponent
        implements BorderProperty, RotatableProperty, ColoredProperty {

    /** Returns this circle component's radius. */
    public abstract double getRadius();


    /** Sets this circle component's radius.
     * @param radius The circle's radius. */
    public void setRadius(double radius) {

    }


    @Override
    public void draw(GraphicsContext graphicsContext, boolean selected) {

        double x = getX();
        double y = getY();
        double radius = getRadius();

        double offsetX = LevelManager.getLevel().getOffsetX();
        double offsetY = LevelManager.getLevel().getOffsetY();
        double zoom = LevelManager.getLevel().getZoom();

        double screenX = (x - radius) * zoom + offsetX;
        double screenY = (y - radius) * zoom + offsetY;

        graphicsContext.setFill(getColor());

        graphicsContext.fillOval(screenX + zoom / 2, screenY + zoom / 2,
                (radius - 0.5) * 2 * zoom, (radius - 0.5) * 2 * zoom);

        graphicsContext.setStroke(getBorderColor());

        double woag = Math.min(getEdgeSize(), Math.abs(radius) / 2) / 2;

        graphicsContext.setLineWidth(woag * 2 * zoom);
        if (getEdgeSize() != 0)
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

            if (isResizable()) {
                graphicsContext.strokeRect(screenX - 4, screenY + radius * zoom - 4, 8, 8);
                graphicsContext.strokeRect(screenX + radius * zoom - 4, screenY + radius * 2 * zoom - 4, 8, 8);
                graphicsContext.strokeRect(screenX + radius * zoom - 4, screenY - 4, 8, 8);
                graphicsContext.strokeRect(screenX + radius * 2 * zoom - 4, screenY + radius * zoom - 4, 8, 8);
            }

            if (isRotatable()) {
                graphicsContext.strokeOval(screenX - 4, screenY + radius * zoom - 4, 8, 8);
                graphicsContext.strokeOval(screenX + radius * 2 * zoom - 4, screenY + radius * zoom - 4, 8, 8);
            }

        }

    }


    @Override
    public DragSettings mouseIntersection(double mouseX, double mouseY) {

        double x = getX();
        double y = getY();
        double radius = getRadius();

        double distance = Math.hypot(mouseX - x, mouseY - y);

        if (distance < radius && (!isEdgeOnly() || distance > radius - getEdgeSize())) {
            DragSettings dragSettings = new DragSettings(DragSettings.MOVE, this);
            dragSettings.setInitialSourceX(mouseX - x);
            dragSettings.setInitialSourceY(mouseY - y);
            return dragSettings;
        } else {
            return DragSettings.NULL;
        }

    }


    @Override
    public DragSettings mouseIntersectingCorners(double mouseX, double mouseY) {

        double x = getX();
        double y = getY();
        double radius = getRadius();

        Point2D left = new Point2D(x - radius, y);
        Point2D top = new Point2D(x, y - radius);
        Point2D right = new Point2D(x + radius, y);
        Point2D bottom = new Point2D(x, y + radius);
        double distance = 4 / LevelManager.getLevel().getZoom();

        if (isResizable() && (
                rectangleIntersection(mouseX, mouseY, left, distance) ||
                rectangleIntersection(mouseX, mouseY, top, distance) ||
                rectangleIntersection(mouseX, mouseY, right, distance) ||
                rectangleIntersection(mouseX, mouseY, bottom, distance)
        )) {

            DragSettings resizeSettings = new DragSettings(DragSettings.RESIZE, this);
            resizeSettings.setInitialSourceX(0);
            resizeSettings.setInitialSourceY(0);
            resizeSettings.setAnchorX(0);
            resizeSettings.setAnchorY(0);
            return resizeSettings;

        }

        if (isRotatable() && (
                rectangleIntersection(mouseX, mouseY, left, distance) ||
                rectangleIntersection(mouseX, mouseY, right, distance)
        )) {

            DragSettings rotateSettings = new DragSettings(DragSettings.ROTATE, this);
            rotateSettings.setInitialSourceX(0);
            rotateSettings.setInitialSourceY(0);
            return rotateSettings;

        }

        return DragSettings.NULL;

    }

}
