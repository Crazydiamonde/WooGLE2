package com.woogleFX.editorObjects.objectComponents;

import com.woogleFX.editorObjects.ObjectUtil;
import com.woogleFX.editorObjects.objectComponents.generic.BorderProperty;
import com.woogleFX.editorObjects.objectComponents.generic.ColoredProperty;
import com.woogleFX.editorObjects.objectComponents.generic.RotatableProperty;
import com.woogleFX.engine.renderer.Renderer;
import com.woogleFX.engine.LevelManager;
import com.woogleFX.editorObjects.DragSettings;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.shape.StrokeLineCap;

/** Represents a rectangle component in any object. */
public abstract class RectangleComponent extends ObjectComponent
        implements BorderProperty, RotatableProperty, ColoredProperty {

    /** Returns this component's width. */
    public abstract double getWidth();


    /** Sets this component's width.
     * @param width This component's width. */
    public void setWidth(double width) {

    }


    /** Returns this component's height. */
    public abstract double getHeight();


    /** Sets this component's height.
     * @param height This component's height. */
    public void setHeight(double height) {

    }


    public double getHorizontalEdgeSize() {
        return getEdgeSize();
    }


    public double getVerticalEdgeSize() {
        return getEdgeSize();
    }


    @Override
    public void draw(GraphicsContext graphicsContext, boolean selected) {

        double x = getX();
        double y = getY();
        double rotation = getRotation();
        double width = getWidth();
        double height = getHeight();

        Point2D center = new Point2D(x, y);

        double woag1 = Math.min(Math.min(getHorizontalEdgeSize(), Math.abs(width) / 2), Math.abs(height) / 2) / 2;
        double woag2 = Math.min(Math.min(getVerticalEdgeSize(), Math.abs(width) / 2), Math.abs(height) / 2) / 2;

        double offsetX = LevelManager.getLevel().getOffsetX();
        double offsetY = LevelManager.getLevel().getOffsetY();
        double zoom = LevelManager.getLevel().getZoom();

        Point2D topLeft = new Point2D(x - width / 2 + woag2, y - height / 2 + woag1);
        topLeft = ObjectUtil.rotate(topLeft, rotation, center);
        topLeft = topLeft.multiply(zoom).add(offsetX, offsetY);

        Point2D topRight = new Point2D(x + width / 2 - woag2, y - height / 2 + woag1);
        topRight = ObjectUtil.rotate(topRight, rotation, center);
        topRight = topRight.multiply(zoom).add(offsetX, offsetY);

        Point2D bottomLeft = new Point2D(x - width / 2 + woag2, y + height / 2 - woag1);
        bottomLeft = ObjectUtil.rotate(bottomLeft, rotation, center);
        bottomLeft = bottomLeft.multiply(zoom).add(offsetX, offsetY);

        Point2D bottomRight = new Point2D(x + width / 2 - woag2, y + height / 2 - woag1);
        bottomRight = ObjectUtil.rotate(bottomRight, rotation, center);
        bottomRight = bottomRight.multiply(zoom).add(offsetX, offsetY);

        Point2D topLeft2 = new Point2D(x - width / 2, y - height / 2);
        topLeft2 = ObjectUtil.rotate(topLeft2, rotation, center);
        topLeft2 = topLeft2.multiply(zoom).add(offsetX, offsetY);

        Point2D topRight2 = new Point2D(x + width / 2, y - height / 2);
        topRight2 = ObjectUtil.rotate(topRight2, rotation, center);
        topRight2 = topRight2.multiply(zoom).add(offsetX, offsetY);

        Point2D bottomLeft2 = new Point2D(x - width / 2, y + height / 2);
        bottomLeft2 = ObjectUtil.rotate(bottomLeft2, rotation, center);
        bottomLeft2 = bottomLeft2.multiply(zoom).add(offsetX, offsetY);

        Point2D bottomRight2 = new Point2D(x + width / 2, y + height / 2);
        bottomRight2 = ObjectUtil.rotate(bottomRight2, rotation, center);
        bottomRight2 = bottomRight2.multiply(zoom).add(offsetX, offsetY);

        graphicsContext.setFill(getColor());

        graphicsContext.fillPolygon(
                new double[]{ topRight.getX(), topLeft.getX(), bottomLeft.getX(), bottomRight.getX() },
                new double[]{ topRight.getY(), topLeft.getY(), bottomLeft.getY(), bottomRight.getY() },
                4);

        graphicsContext.setStroke(getBorderColor());

        if (getHorizontalEdgeSize() != 0) {
            graphicsContext.setLineWidth(2 * woag1 * zoom);
            graphicsContext.setLineCap(StrokeLineCap.BUTT);

            Point2D topLeftH = new Point2D(x - width / 2, y - height / 2 + woag1);
            topLeftH = ObjectUtil.rotate(topLeftH, rotation, center);
            topLeftH = topLeftH.multiply(zoom).add(offsetX, offsetY);

            Point2D topRightH = new Point2D(x + width / 2, y - height / 2 + woag1);
            topRightH = ObjectUtil.rotate(topRightH, rotation, center);
            topRightH = topRightH.multiply(zoom).add(offsetX, offsetY);

            Point2D bottomLeftH = new Point2D(x - width / 2, y + height / 2 - woag1);
            bottomLeftH = ObjectUtil.rotate(bottomLeftH, rotation, center);
            bottomLeftH = bottomLeftH.multiply(zoom).add(offsetX, offsetY);

            Point2D bottomRightH = new Point2D(x + width / 2, y + height / 2 - woag1);
            bottomRightH = ObjectUtil.rotate(bottomRightH, rotation, center);
            bottomRightH = bottomRightH.multiply(zoom).add(offsetX, offsetY);

            graphicsContext.strokeLine(topRightH.getX(), topRightH.getY(), topLeftH.getX(), topLeftH.getY());
            graphicsContext.strokeLine(bottomLeftH.getX(), bottomLeftH.getY(), bottomRightH.getX(), bottomRightH.getY());
        }
        if (getVerticalEdgeSize() != 0) {
            graphicsContext.setLineWidth(2 * woag2 * zoom);
            graphicsContext.setLineCap(StrokeLineCap.BUTT);

            Point2D topLeftV = new Point2D(x - width / 2 + woag2, y - height / 2);
            topLeftV = ObjectUtil.rotate(topLeftV, rotation, center);
            topLeftV = topLeftV.multiply(zoom).add(offsetX, offsetY);

            Point2D topRightV = new Point2D(x + width / 2 - woag2, y - height / 2);
            topRightV = ObjectUtil.rotate(topRightV, rotation, center);
            topRightV = topRightV.multiply(zoom).add(offsetX, offsetY);

            Point2D bottomLeftV = new Point2D(x - width / 2 + woag2, y + height / 2);
            bottomLeftV = ObjectUtil.rotate(bottomLeftV, rotation, center);
            bottomLeftV = bottomLeftV.multiply(zoom).add(offsetX, offsetY);

            Point2D bottomRightV = new Point2D(x + width / 2 - woag2, y + height / 2);
            bottomRightV = ObjectUtil.rotate(bottomRightV, rotation, center);
            bottomRightV = bottomRightV.multiply(zoom).add(offsetX, offsetY);

            graphicsContext.strokeLine(bottomLeftV.getX(), bottomLeftV.getY(), topLeftV.getX(), topLeftV.getY());
            graphicsContext.strokeLine(topRightV.getX(), topRightV.getY(), bottomRightV.getX(), bottomRightV.getY());
        }

        if (selected) {

            graphicsContext.setStroke(Renderer.selectionOutline2);
            graphicsContext.setLineWidth(1);
            graphicsContext.setLineDashes(3);
            graphicsContext.setLineDashOffset(0);
            graphicsContext.strokePolygon(
                    new double[]{ topRight2.getX(), topLeft2.getX(), bottomLeft2.getX(), bottomRight2.getX() },
                    new double[]{ topRight2.getY(), topLeft2.getY(), bottomLeft2.getY(), bottomRight2.getY() },
                    4);

            graphicsContext.setStroke(Renderer.selectionOutline);
            graphicsContext.setLineWidth(1);
            graphicsContext.setLineDashOffset(3);
            graphicsContext.strokePolygon(
                    new double[]{ topRight2.getX(), topLeft2.getX(), bottomLeft2.getX(), bottomRight2.getX() },
                    new double[]{ topRight2.getY(), topLeft2.getY(), bottomLeft2.getY(), bottomRight2.getY() },
                    4);
            graphicsContext.setLineDashes(0);

            graphicsContext.setLineWidth(1);
            if (isResizable()) {
                graphicsContext.strokeRect(topRight2.getX() - 4, topRight2.getY() - 4, 8, 8);
                graphicsContext.strokeRect(topLeft2.getX() - 4, topLeft2.getY() - 4, 8, 8);
                graphicsContext.strokeRect(bottomLeft2.getX() - 4, bottomLeft2.getY() - 4, 8, 8);
                graphicsContext.strokeRect(bottomRight2.getX() - 4, bottomRight2.getY() - 4, 8, 8);
            }

            Point2D middleLeft = new Point2D(x - width / 2, y);
            middleLeft = ObjectUtil.rotate(middleLeft, rotation, center);
            middleLeft = new Point2D(middleLeft.getX() * zoom + offsetX, middleLeft.getY() * zoom + offsetY);

            Point2D middleRight = new Point2D(x + width / 2, y);
            middleRight = ObjectUtil.rotate(middleRight, rotation, center);
            middleRight = new Point2D(middleRight.getX() * zoom + offsetX, middleRight.getY() * zoom + offsetY);

            if (isRotatable()) {
                graphicsContext.strokeOval(middleLeft.getX() - 4, middleLeft.getY() - 4, 8, 8);
                graphicsContext.strokeOval(middleRight.getX() - 4, middleRight.getY() - 4, 8, 8);
            }

        }

    }


    @Override
    public DragSettings mouseIntersection(double mouseX, double mouseY) {

        double x = getX();
        double y = getY();
        double rotation = getRotation();
        double width = getWidth();
        double height = getHeight();

        Point2D rotated = ObjectUtil.rotate(new Point2D(mouseX, mouseY), -rotation, new Point2D(x, y));

        double mX = rotated.getX();
        double mY = rotated.getY();

        double edgeSizeH = getHorizontalEdgeSize();
        double edgeSizeV = getVerticalEdgeSize();

        if (
                mX < x - width / 2 ||
                mX > x + width / 2 ||
                mY < y - height / 2 ||
                mY > y + height / 2
        ) return DragSettings.NULL;

        if (isEdgeOnly() && (
                mX > x - width / 2 + edgeSizeV &&
                mX < x + width / 2 - edgeSizeV &&
                mY > y - height / 2 + edgeSizeH &&
                mY < y + height / 2 - edgeSizeH
        )) return DragSettings.NULL;

        DragSettings dragSettings = new DragSettings(isDraggable() ? DragSettings.MOVE : DragSettings.NONE, this);
        dragSettings.setInitialSourceX(mouseX - x);
        dragSettings.setInitialSourceY(mouseY - y);
        return dragSettings;

    }


    @Override
    public DragSettings mouseIntersectingCorners(double mouseX, double mouseY) {

        double x = getX();
        double y = getY();
        double rotation = getRotation();
        double width = getWidth();
        double height = getHeight();

        Point2D center = new Point2D(x, y);

        Point2D right = new Point2D(x + width / 2, y);
        right = ObjectUtil.rotate(right, rotation, center);

        Point2D left = new Point2D(x - width / 2, y);
        left = ObjectUtil.rotate(left, rotation, center);

        Point2D topRight = new Point2D(x + width / 2, y + height / 2);
        topRight = ObjectUtil.rotate(topRight, rotation, center);

        Point2D topLeft = new Point2D(x - width / 2, y + height / 2);
        topLeft = ObjectUtil.rotate(topLeft, rotation, center);

        Point2D bottomLeft = new Point2D(x - width / 2, y - height / 2);
        bottomLeft = ObjectUtil.rotate(bottomLeft, rotation, center);

        Point2D bottomRight = new Point2D(x + width / 2, y - height / 2);
        bottomRight = ObjectUtil.rotate(bottomRight, rotation, center);

        double distance = 4 / LevelManager.getLevel().getZoom();

        DragSettings resizeSettings = new DragSettings(DragSettings.RESIZE, this);

        DragSettings rotateSettings = new DragSettings(DragSettings.ROTATE, this);

        double dragSourceX = 0;
        double dragSourceY = 0;
        double dragAnchorX = 0;
        double dragAnchorY = 0;
        double rotateAngleOffset = 0;

        boolean resize = false;
        boolean rotate = false;

        if (isResizable() && mouseX > topLeft.getX() - distance && mouseX < topLeft.getX() + distance && mouseY > topLeft.getY() - distance && mouseY < topLeft.getY() + distance) {
            resize  = true;
            dragSourceX = x - width / 2;
            dragSourceY = y + height / 2;
            dragAnchorX = x + width / 2;
            dragAnchorY = y - height / 2;
        }
        if (isResizable() && mouseX > topRight.getX() - distance && mouseX < topRight.getX() + distance && mouseY > topRight.getY() - distance && mouseY < topRight.getY() + distance) {
            resize  = true;
            dragSourceX = x + width / 2;
            dragSourceY = y + height / 2;
            dragAnchorX = x - width / 2;
            dragAnchorY = y - height / 2;
        }
        if (isResizable() && mouseX > bottomLeft.getX() - distance && mouseX < bottomLeft.getX() + distance && mouseY > bottomLeft.getY() - distance && mouseY < bottomLeft.getY() + distance) {
            resize  = true;
            dragSourceX = x - width / 2;
            dragSourceY = y - height / 2;
            dragAnchorX = x + width / 2;
            dragAnchorY = y + height / 2;
        }
        if (isResizable() && mouseX > bottomRight.getX() - distance && mouseX < bottomRight.getX() + distance && mouseY > bottomRight.getY() - distance && mouseY < bottomRight.getY() + distance) {
            resize  = true;
            dragSourceX = x + width / 2;
            dragSourceY = y - height / 2;
            dragAnchorX = x - width / 2;
            dragAnchorY = y + height / 2;
        }

        if (isRotatable() && mouseX > left.getX() - distance && mouseX < left.getX() + distance && mouseY > left.getY() - distance && mouseY < left.getY() + distance) {
            rotate = true;
            dragSourceX = x - width / 2;
            dragSourceY = y;
            rotateAngleOffset = rotation;
        }

        if (isRotatable() && mouseX > right.getX() - distance && mouseX < right.getX() + distance && mouseY > right.getY() - distance && mouseY < right.getY() + distance) {
            rotate = true;
            dragSourceX = x + width / 2;
            dragSourceY = y;
            rotateAngleOffset = rotation;
        }

        if (resize) {
            Point2D dragSourceRotated = ObjectUtil.rotate(new Point2D(dragSourceX, dragSourceY), rotation, new Point2D(x, y));
            Point2D dragAnchorRotated = ObjectUtil.rotate(new Point2D(dragAnchorX, dragAnchorY), rotation, new Point2D(x, y));
            resizeSettings.setInitialSourceX(dragSourceRotated.getX());
            resizeSettings.setInitialSourceY(dragSourceRotated.getY());
            resizeSettings.setAnchorX(dragAnchorRotated.getX());
            resizeSettings.setAnchorY(dragAnchorRotated.getY());
            return resizeSettings;
        }

        if (rotate) {
            Point2D dragSourceRotated = ObjectUtil.rotate(new Point2D(dragSourceX, dragSourceY), rotation, new Point2D(x, y));
            rotateSettings.setInitialSourceX(dragSourceRotated.getX());
            rotateSettings.setInitialSourceY(dragSourceRotated.getY());
            rotateSettings.setRotateAngleOffset(rotateAngleOffset);
            return rotateSettings;
        }

        return DragSettings.NULL;

    }

}
