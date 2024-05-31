package com.woogleFX.editorObjects.objectComponents;

import com.woogleFX.editorObjects.ObjectUtil;
import com.woogleFX.editorObjects.objectComponents.generic.ColoredProperty;
import com.woogleFX.engine.renderer.Renderer;
import com.woogleFX.engine.LevelManager;
import com.woogleFX.editorObjects.DragSettings;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;

/** Represents any object component with an "anchor" (ex. Linearforcefield).
 * Note that objects with an anchor have their own unrelated positions. */
public abstract class AnchorComponent extends ObjectComponent implements ColoredProperty {

    /** Returns the X component of the anchor. */
    public abstract double getAnchorX();


    /** Returns the Y component of the anchor. */
    public abstract double getAnchorY();


    /** Sets both the X and Y components of the anchor.
     * This needs to be done for both at once because of normalized anchors like those in Line.
     * @param anchorX The new X component of the anchor.
     * @param anchorY The new Y component of the anchor. */
    public void setAnchor(double anchorX, double anchorY) {

    }


    /** Returns the width of the anchor line. */
    public abstract double getLineWidth();


    @Override
    public void draw(GraphicsContext graphicsContext, boolean selected) {

        double x = getX();
        double y = getY();
        double anchorX = getAnchorX();
        double anchorY = getAnchorY();

        double offsetX = LevelManager.getLevel().getOffsetX();
        double offsetY = LevelManager.getLevel().getOffsetY();
        double zoom = LevelManager.getLevel().getZoom();

        double angle = Renderer.angleTo(new Point2D(0, 0), new Point2D(anchorX, anchorY));

        graphicsContext.setStroke(getColor());
        graphicsContext.setLineWidth(zoom * getLineWidth());

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


    @Override
    public DragSettings mouseIntersection(double mouseX, double mouseY) {

        double x = getX();
        double y = getY();
        double anchorX = getAnchorX();
        double anchorY = getAnchorY();

        double drawWidth = 6;

        double angle = Renderer.angleTo(new Point2D(0, 0), new Point2D(anchorX, anchorY));

        Point2D rotatedMouse = ObjectUtil.rotate(new Point2D(mouseX, mouseY), -angle, new Point2D(x, y));

        double forceMagnitude = Math.hypot(anchorX, anchorY);

        if (rotatedMouse.getX() > x - drawWidth / 2 && rotatedMouse.getX() < x + forceMagnitude + drawWidth / 2 && rotatedMouse.getY() > y - drawWidth / 2 && rotatedMouse.getY() < y + drawWidth / 2) {
            DragSettings anchorSettings = new DragSettings(DragSettings.MOVE, this);
            anchorSettings.setInitialSourceX(mouseX - x);
            anchorSettings.setInitialSourceY(mouseY - y);
            return anchorSettings;
        } else {
            return DragSettings.NULL;
        }

    }


    @Override
    public DragSettings mouseIntersectingCorners(double mouseX, double mouseY) {

        double x = getX();
        double y = getY();
        double anchorX = getAnchorX();
        double anchorY = getAnchorY();

        double angle = Renderer.angleTo(new Point2D(0, 0), new Point2D(anchorX, anchorY));

        double forceMagnitude = Math.hypot(anchorX, anchorY);

        Point2D right = new Point2D(x + forceMagnitude, y);

        Point2D rotated = ObjectUtil.rotate(new Point2D(mouseX, mouseY), -angle, new Point2D(x, y));
        if (forceMagnitude != 0 && rotated.getX() > right.getX() - 4 / LevelManager.getLevel().getZoom() &&
                rotated.getX() < right.getX() + 4 / LevelManager.getLevel().getZoom() &&
                rotated.getY() > right.getY() - 4 / LevelManager.getLevel().getZoom() &&
                rotated.getY() < right.getY() + 4 / LevelManager.getLevel().getZoom()) {
            DragSettings anchorSettings = new DragSettings(DragSettings.SETANCHOR, this);
            anchorSettings.setInitialSourceX(mouseX - anchorX);
            anchorSettings.setInitialSourceY(mouseY - anchorY);
            return anchorSettings;
        } else return DragSettings.NULL;

    }

}
