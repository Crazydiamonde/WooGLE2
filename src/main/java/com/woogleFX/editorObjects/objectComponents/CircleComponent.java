package com.woogleFX.editorObjects.objectComponents;

import com.woogleFX.editorObjects.ObjectUtil;
import com.woogleFX.editorObjects.objectComponents.generic.BorderProperty;
import com.woogleFX.editorObjects.objectComponents.generic.ColoredProperty;
import com.woogleFX.editorObjects.objectComponents.generic.RotatableProperty;
import com.woogleFX.engine.Renderer;
import com.woogleFX.functions.LevelManager;
import com.woogleFX.structures.simpleStructures.DragSettings;
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
        double rotation = getRotation();

        double offsetX = LevelManager.getLevel().getOffsetX();
        double offsetY = LevelManager.getLevel().getOffsetY();
        double zoom = LevelManager.getLevel().getZoom();

        double screenX = (x - radius) * zoom + offsetX;
        double screenY = (y - radius) * zoom + offsetY;

        graphicsContext.setFill(getColor());

        graphicsContext.fillOval(screenX + zoom / 2, screenY + zoom / 2,
                (radius - 0.5) * 2 * zoom, (radius - 0.5) * 2 * zoom);

        graphicsContext.setStroke(getBorderColor());

        double woag = Math.min(getEdgeSize(), Math.abs(radius)) / 2;

        graphicsContext.setLineWidth(woag * 2 * zoom);
        if (getEdgeSize() != 0)
            graphicsContext.strokeOval(screenX + woag * zoom, screenY + woag * zoom,
                    (radius - woag) * 2 * zoom, (radius - woag) * 2 * zoom);

        Point2D center = new Point2D(x, y);

        Point2D right = new Point2D(x + radius, y);
        right = ObjectUtil.rotate(right, rotation, center);
        right = right.multiply(zoom).add(offsetX, offsetY);

        Point2D left = new Point2D(x - radius, y);
        left = ObjectUtil.rotate(left, rotation, center);
        left = new Point2D(left.getX(), left.getY());
        left = left.multiply(zoom).add(offsetX, offsetY);

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
                graphicsContext.strokeOval(left.getX() - 4, left.getY() - 4, 8, 8);
                graphicsContext.strokeOval(right.getX() - 4, right.getY() - 4, 8, 8);
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
        double rotation = getRotation();

        Point2D center = new Point2D(x, y);

        Point2D left = new Point2D(x - radius, y);
        Point2D top = new Point2D(x, y - radius);
        Point2D right = new Point2D(x + radius, y);
        Point2D bottom = new Point2D(x, y + radius);
        double distance = 4 / LevelManager.getLevel().getZoom();


        Point2D rotateLeft = new Point2D(x - radius, y);
        rotateLeft = ObjectUtil.rotate(rotateLeft, rotation, center);

        Point2D rotateRight = new Point2D(x + radius, y);
        rotateRight = ObjectUtil.rotate(rotateRight, rotation, center);

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


        boolean rotating = false;

        double dragSourceX = 0;
        double dragSourceY = 0;

        if (isRotatable() && rectangleIntersection(mouseX, mouseY, rotateLeft, distance)) {
            rotating = true;
            dragSourceX = x - radius;
            dragSourceY = y;
        }

        if (isRotatable() && rectangleIntersection(mouseX, mouseY, rotateRight, distance)) {
            rotating = true;
            dragSourceX = x + radius;
            dragSourceY = y;
        }

        if (rotating) {

            DragSettings rotateSettings = new DragSettings(DragSettings.ROTATE, this);
            Point2D dragSourceRotated = ObjectUtil.rotate(new Point2D(dragSourceX, dragSourceY), rotation, new Point2D(x, y));
            rotateSettings.setInitialSourceX(dragSourceRotated.getX());
            rotateSettings.setInitialSourceY(dragSourceRotated.getY());
            rotateSettings.setRotateAngleOffset(rotation);
            return rotateSettings;

        }

        return DragSettings.NULL;

    }

}
