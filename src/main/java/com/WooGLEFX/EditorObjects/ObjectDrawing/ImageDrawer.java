package com.WooGLEFX.EditorObjects.ObjectDrawing;

import com.WooGLEFX.EditorObjects.ObjectPosition;
import com.WooGLEFX.EditorObjects.ObjectUtil;
import com.WooGLEFX.Engine.Renderer;
import com.WooGLEFX.Functions.LevelManager;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.transform.Affine;

public class ImageDrawer {

    public static void draw(GraphicsContext graphicsContext, ObjectPosition objectPosition, boolean selected) {

        Image image = objectPosition.getImage();
        if (image == null) return;

        double x = objectPosition.getX();
        double y = objectPosition.getY();
        double rotation = objectPosition.getRotation();
        double width = objectPosition.getWidth();
        double height = objectPosition.getHeight();

        double offsetX = LevelManager.getLevel().getOffsetX();
        double offsetY = LevelManager.getLevel().getOffsetY();
        double zoom = LevelManager.getLevel().getZoom();

        graphicsContext.save();

        Affine t = graphicsContext.getTransform();
        t.appendTranslation(offsetX, offsetY);
        t.appendScale(zoom, zoom);
        t.appendRotation(Math.toDegrees(rotation), x, y);
        graphicsContext.setTransform(t);

        graphicsContext.drawImage(image, x - width / 2.0, y - height / 2.0, width, height);

        graphicsContext.restore();

        if (selected) {

            Point2D topLeft = ObjectUtil.rotate(new Point2D(x - width / 2, y - height / 2), rotation, new Point2D(x, y));
            topLeft = topLeft.multiply(zoom).add(offsetX, offsetY);

            Point2D bottomRight = ObjectUtil.rotate(new Point2D(x + width / 2, y + height / 2), rotation, new Point2D(x, y));
            bottomRight = bottomRight.multiply(zoom).add(offsetX, offsetY);

            Point2D left = ObjectUtil.rotate(new Point2D(x - width / 2, y), rotation, new Point2D(x, y));
            left = left.multiply(zoom).add(offsetX, offsetY);

            Point2D right = ObjectUtil.rotate(new Point2D(x + width / 2, y), rotation, new Point2D(x, y));
            right = right.multiply(zoom).add(offsetX, offsetY);

            Point2D bottomLeft = ObjectUtil.rotate(new Point2D(x - width / 2, y + height / 2), rotation, new Point2D(x, y));
            bottomLeft = bottomLeft.multiply(zoom).add(offsetX, offsetY);

            Point2D topRight = ObjectUtil.rotate(new Point2D(x + width / 2, y - height / 2), rotation, new Point2D(x, y));
            topRight = topRight.multiply(zoom).add(offsetX, offsetY);

            double screenX2 = x * zoom + offsetX;
            double screenY2 = y * zoom + offsetY;

            graphicsContext.setLineWidth(1);
            if (objectPosition.isResizable()) {

                graphicsContext.setStroke(Renderer.selectionOutline);
                if (!true) {
                    graphicsContext.fillRect(topLeft.getX() - 4, topLeft.getY() - 4, 8, 8);
                    graphicsContext.fillRect(bottomRight.getX() - 4, bottomRight.getY() - 4, 8, 8);
                    graphicsContext.fillRect(bottomLeft.getX() - 4, bottomLeft.getY() - 4, 8, 8);
                    graphicsContext.fillRect(topRight.getX() - 4, topRight.getY() - 4, 8, 8);
                    graphicsContext.setStroke(Renderer.selectionOutline2);
                }
                graphicsContext.strokeRect(topLeft.getX() - 4, topLeft.getY() - 4, 8, 8);
                graphicsContext.strokeRect(bottomRight.getX() - 4, bottomRight.getY() - 4, 8, 8);
                graphicsContext.strokeRect(bottomLeft.getX() - 4, bottomLeft.getY() - 4, 8, 8);
                graphicsContext.strokeRect(topRight.getX() - 4, topRight.getY() - 4, 8, 8);

            }

            if (objectPosition.isRotatable()) {

                graphicsContext.setStroke(Renderer.selectionOutline);
                if (!true) {
                    graphicsContext.fillOval(left.getX() - 4, left.getY() - 4, 8, 8);
                    graphicsContext.fillOval(right.getX() - 4, right.getY() - 4, 8, 8);
                    graphicsContext.setStroke(Renderer.selectionOutline2);
                }
                graphicsContext.strokeOval(left.getX() - 4, left.getY() - 4, 8, 8);
                graphicsContext.strokeOval(right.getX() - 4, right.getY() - 4, 8, 8);

            }

            Affine t2 = graphicsContext.getTransform();
            t2.appendRotation(Math.toDegrees(rotation), screenX2, screenY2);
            graphicsContext.setTransform(t2);

            if (objectPosition.isSelectable()) {

                graphicsContext.setStroke(Renderer.selectionOutline2);
                graphicsContext.setLineWidth(1);
                graphicsContext.setLineDashes(3);
                graphicsContext.setLineDashOffset(0);
                graphicsContext.strokeRect(screenX2 - width * zoom / 2, screenY2 - height * zoom / 2, width * zoom, height * zoom);

                graphicsContext.setStroke(Renderer.selectionOutline);
                graphicsContext.setLineWidth(1);
                graphicsContext.setLineDashOffset(3);
                graphicsContext.strokeRect(screenX2 - width * zoom / 2, screenY2 - height * zoom / 2, width * zoom, height * zoom);
                graphicsContext.setLineDashes(0);

            }

        }

    }

}
