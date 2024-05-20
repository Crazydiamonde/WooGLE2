package com.WooGLEFX.EditorObjects.ObjectDrawing;

import com.WooGLEFX.EditorObjects.ObjectPosition;
import com.WooGLEFX.EditorObjects.ObjectUtil;
import com.WooGLEFX.Engine.Renderer;
import com.WooGLEFX.Functions.LevelManager;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;

public class AnchorDrawer {

    public static void draw(GraphicsContext graphicsContext, ObjectPosition objectPosition, boolean selected) {

        double x = objectPosition.getX();
        double y = objectPosition.getY();
        double anchorX = objectPosition.getAnchorX();
        double anchorY = objectPosition.getAnchorY();

        double offsetX = LevelManager.getLevel().getOffsetX();
        double offsetY = LevelManager.getLevel().getOffsetY();
        double zoom = LevelManager.getLevel().getZoom();

        double angle = Renderer.angleTo(new Point2D(0, 0), new Point2D(anchorX, anchorY));

        graphicsContext.setStroke(objectPosition.getBorderColor());
        graphicsContext.setLineWidth(zoom * objectPosition.getEdgeSize());

        if (anchorX != 0 || anchorY != 0) {

            double screenX = x * zoom + offsetX;
            double screenY = y * zoom + offsetY;

            graphicsContext.strokeLine(screenX, screenY, screenX + anchorX * zoom, screenY + anchorY * zoom);

        }

        if (selected) {

            graphicsContext.setStroke(Renderer.selectionOutline);
            graphicsContext.setLineWidth(1);
            graphicsContext.setLineDashes(3);

            double forceMagnitude = Math.hypot(anchorX, anchorY);

            Point2D forceRight = new Point2D(x + forceMagnitude, y);

            forceRight = ObjectUtil.rotate(forceRight, angle, new Point2D(x, y));

            forceRight = forceRight.multiply(zoom).add(offsetX, offsetY);

            graphicsContext.setLineDashes(0);

            graphicsContext.setLineWidth(1);
            if (forceMagnitude != 0) {
                graphicsContext.strokeRect(forceRight.getX() - 4, forceRight.getY() - 4, 8, 8);
            }

        }

    }

}
