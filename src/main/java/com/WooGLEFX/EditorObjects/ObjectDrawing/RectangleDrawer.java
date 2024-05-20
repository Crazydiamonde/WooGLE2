package com.WooGLEFX.EditorObjects.ObjectDrawing;

import com.WooGLEFX.EditorObjects.ObjectPosition;
import com.WooGLEFX.EditorObjects.ObjectUtil;
import com.WooGLEFX.Engine.Renderer;
import com.WooGLEFX.Functions.LevelManager;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;

public class RectangleDrawer {

    public static void draw(GraphicsContext graphicsContext, ObjectPosition objectPosition, boolean selected) {

        double x = objectPosition.getX();
        double y = objectPosition.getY();
        double rotation = objectPosition.getRotation();
        double width = objectPosition.getWidth();
        double height = objectPosition.getHeight();

        Point2D center = new Point2D(x, y);

        double woag = Math.min(Math.min(objectPosition.getEdgeSize(), Math.abs(width) / 2), Math.abs(height) / 2) / 2;

        double offsetX = LevelManager.getLevel().getOffsetX();
        double offsetY = LevelManager.getLevel().getOffsetY();
        double zoom = LevelManager.getLevel().getZoom();

        Point2D topLeft = new Point2D(x - width / 2 + woag, y - height / 2 + woag);
        topLeft = ObjectUtil.rotate(topLeft, rotation, center);
        topLeft = topLeft.multiply(zoom).add(offsetX, offsetY);

        Point2D topRight = new Point2D(x + width / 2 - woag, y - height / 2 + woag);
        topRight = ObjectUtil.rotate(topRight, rotation, center);
        topRight = topRight.multiply(zoom).add(offsetX, offsetY);

        Point2D bottomLeft = new Point2D(x - width / 2 + woag, y + height / 2 - woag);
        bottomLeft = ObjectUtil.rotate(bottomLeft, rotation, center);
        bottomLeft = bottomLeft.multiply(zoom).add(offsetX, offsetY);

        Point2D bottomRight = new Point2D(x + width / 2 - woag, y + height / 2 - woag);
        bottomRight = ObjectUtil.rotate(bottomRight, rotation, center);
        bottomRight = bottomRight.multiply(zoom).add(offsetX, offsetY);

        Point2D topRight2 = new Point2D(x + width / 2, y - height / 2);
        topRight2 = ObjectUtil.rotate(topRight2, rotation, center);
        topRight2 = topRight2.multiply(zoom).add(offsetX, offsetY);

        Point2D topLeft2 = new Point2D(x - width / 2, y - height / 2);
        topLeft2 = ObjectUtil.rotate(topLeft2, rotation, center);
        topLeft2 = topLeft2.multiply(zoom).add(offsetX, offsetY);

        Point2D bottomLeft2 = new Point2D(x - width / 2, y + height / 2);
        bottomLeft2 = ObjectUtil.rotate(bottomLeft2, rotation, center);
        bottomLeft2 = bottomLeft2.multiply(zoom).add(offsetX, offsetY);

        Point2D bottomRight2 = new Point2D(x + width / 2, y + height / 2);
        bottomRight2 = ObjectUtil.rotate(bottomRight2, rotation, center);
        bottomRight2 = bottomRight2.multiply(zoom).add(offsetX, offsetY);

        graphicsContext.setFill(objectPosition.getFillColor());

        graphicsContext.fillPolygon(
                new double[]{ topRight.getX(), topLeft.getX(), bottomLeft.getX(), bottomRight.getX() },
                new double[]{ topRight.getY(), topLeft.getY(), bottomLeft.getY(), bottomRight.getY() },
                4);

        graphicsContext.setStroke(objectPosition.getBorderColor());

        graphicsContext.setLineWidth(2 * woag * zoom);
        if (objectPosition.getEdgeSize() != 0) {
            graphicsContext.strokeLine(topRight.getX(), topRight.getY(), topLeft.getX(), topLeft.getY());
            graphicsContext.strokeLine(bottomLeft.getX(), bottomLeft.getY(), bottomRight.getX(), bottomRight.getY());
            graphicsContext.strokeLine(topLeft.getX(), topLeft.getY(), bottomLeft.getX(), bottomLeft.getY());
            graphicsContext.strokeLine(bottomRight.getX(), bottomRight.getY(), topRight.getX(), topRight.getY());
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
            if (objectPosition.isResizable()) {
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

            if (objectPosition.isRotatable()) {
                graphicsContext.strokeOval(middleLeft.getX() - 4, middleLeft.getY() - 4, 8, 8);
                graphicsContext.strokeOval(middleRight.getX() - 4, middleRight.getY() - 4, 8, 8);
            }

        }

    }

}
