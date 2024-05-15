package com.WooGLEFX.EditorObjects.ObjectDrawing;

import com.WooGLEFX.EditorObjects.Components.ObjectPosition;
import com.WooGLEFX.Engine.Renderer;
import com.WooGLEFX.Functions.LevelManager;
import com.WooGLEFX.Structures.EditorObject;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;

public class RectangleDrawer {

    public static void draw(GraphicsContext graphicsContext, ObjectPosition objectPosition, boolean selected, boolean isForceField) {

        double x = objectPosition.getX();
        double y = objectPosition.getY();
        double rotation = objectPosition.getRotation();
        double width = objectPosition.getWidth();
        double height = objectPosition.getHeight();

        Point2D center = new Point2D(x, y);

        double woag = Math.min(Math.min(objectPosition.getEdgeSize(), Math.abs(width) / 4), Math.abs(height) / 4);

        double offsetX = LevelManager.getLevel().getOffsetX();
        double offsetY = LevelManager.getLevel().getOffsetY();
        double zoom = LevelManager.getLevel().getZoom();

        Point2D topLeft = new Point2D(x - width / 2 + woag, y - height / 2 + woag);
        topLeft = EditorObject.rotate(topLeft, rotation, center);
        topLeft = topLeft.multiply(zoom).add(offsetX, offsetY);

        Point2D topRight = new Point2D(x + width / 2 - woag, y - height / 2 + woag);
        topRight = EditorObject.rotate(topRight, rotation, center);
        topRight = topRight.multiply(zoom).add(offsetX, offsetY);

        Point2D bottomLeft = new Point2D(x - width / 2 + woag, y + height / 2 - woag);
        bottomLeft = EditorObject.rotate(bottomLeft, rotation, center);
        bottomLeft = bottomLeft.multiply(zoom).add(offsetX, offsetY);

        Point2D bottomRight = new Point2D(x + width / 2 - woag, y + height / 2 - woag);
        bottomRight = EditorObject.rotate(bottomRight, rotation, center);
        bottomRight = bottomRight.multiply(zoom).add(offsetX, offsetY);

        Point2D topRight2 = new Point2D(x + width / 2, y - height / 2);
        topRight2 = EditorObject.rotate(topRight2, rotation, center);
        topRight2 = topRight2.multiply(zoom).add(offsetX, offsetY);

        Point2D topLeft2 = new Point2D(x - width / 2, y - height / 2);
        topLeft2 = EditorObject.rotate(topLeft2, rotation, center);
        topLeft2 = topLeft2.multiply(zoom).add(offsetX, offsetY);

        Point2D bottomLeft2 = new Point2D(x - width / 2, y + height / 2);
        bottomLeft2 = EditorObject.rotate(bottomLeft2, rotation, center);
        bottomLeft2 = bottomLeft2.multiply(zoom).add(offsetX, offsetY);

        Point2D bottomRight2 = new Point2D(x + width / 2, y + height / 2);
        bottomRight2 = EditorObject.rotate(bottomRight2, rotation, center);
        bottomRight2 = bottomRight2.multiply(zoom).add(offsetX, offsetY);

        graphicsContext.setFill(objectPosition.getFillColor());

        graphicsContext.fillPolygon(
                new double[]{ topRight.getX(), topLeft.getX(), bottomLeft.getX(), bottomRight.getX() },
                new double[]{ topRight.getY(), topLeft.getY(), bottomLeft.getY(), bottomRight.getY() },
                4);

        graphicsContext.setStroke(objectPosition.getBorderColor());

        graphicsContext.setLineWidth(2 * woag * zoom);
        graphicsContext.strokeLine(topRight.getX(), topRight.getY(), topLeft.getX(), topLeft.getY());
        graphicsContext.strokeLine(bottomLeft.getX(), bottomLeft.getY(), bottomRight.getX(), bottomRight.getY());
        graphicsContext.strokeLine(topLeft.getX(), topLeft.getY(), bottomLeft.getX(), bottomLeft.getY());
        graphicsContext.strokeLine(bottomRight.getX(), bottomRight.getY(), topRight.getX(), topRight.getY());

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
            graphicsContext.strokeRect(topRight2.getX() - 4, topRight2.getY() - 4, 8, 8);
            graphicsContext.strokeRect(topLeft2.getX() - 4, topLeft2.getY() - 4, 8, 8);
            graphicsContext.strokeRect(bottomLeft2.getX() - 4, bottomLeft2.getY() - 4, 8, 8);
            graphicsContext.strokeRect(bottomRight2.getX() - 4, bottomRight2.getY() - 4, 8, 8);

            Point2D middleLeft = new Point2D(x - width / 2, y);
            middleLeft = EditorObject.rotate(middleLeft, rotation, center);
            middleLeft = new Point2D(middleLeft.getX() * zoom + offsetX, middleLeft.getY() * zoom + offsetY);

            Point2D middleRight = new Point2D(x + width / 2, y);
            middleRight = EditorObject.rotate(middleRight, rotation, center);
            middleRight = new Point2D(middleRight.getX() * zoom + offsetX, middleRight.getY() * zoom + offsetY);

            if (!isForceField) graphicsContext.strokeOval(middleLeft.getX() - 4, middleLeft.getY() - 4, 8, 8);
            if (!isForceField) graphicsContext.strokeOval(middleRight.getX() - 4, middleRight.getY() - 4, 8, 8);

        }

    }

}
