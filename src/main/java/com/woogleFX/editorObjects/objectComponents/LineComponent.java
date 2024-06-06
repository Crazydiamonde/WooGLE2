package com.woogleFX.editorObjects.objectComponents;

import com.woogleFX.editorObjects.objectComponents.generic.ColoredProperty;
import com.woogleFX.editorObjects.objectComponents.generic.RotatableProperty;
import com.woogleFX.engine.fx.FXCanvas;
import com.woogleFX.engine.fx.FXContainers;
import com.woogleFX.engine.fx.FXLevelSelectPane;
import com.woogleFX.engine.renderer.Renderer;
import com.woogleFX.engine.LevelManager;
import com.woogleFX.editorObjects.DragSettings;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.SplitPane;

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

        SplitPane splitPane = FXContainers.getSplitPane();
        double WIDTH = splitPane.getDividerPositions()[0] * splitPane.getWidth() - 6;
        double HEIGHT = FXCanvas.getCanvas().getHeight() - FXLevelSelectPane.getLevelSelectPane().getHeight();

        Point2D top = new Point2D(screenX - screenY / Math.tan(rotation) - 4, 0);
        boolean topPossible = top.getX() >= 0 && top.getX() <= WIDTH;

        Point2D bottom = new Point2D(screenX + (HEIGHT - screenY) / Math.tan(rotation) + 4, HEIGHT);
        boolean bottomPossible = bottom.getX() >= 0 && bottom.getX() <= WIDTH;

        Point2D left = new Point2D(0, screenY - screenX * Math.tan(rotation) - 4);
        boolean leftPossible = left.getY() >= 0 && left.getY() <= HEIGHT;

        Point2D right = new Point2D(WIDTH, screenY + (WIDTH - screenX) * Math.tan(rotation) + 4);

        Point2D point1;
        Point2D point2;

        if (topPossible) {
            point1 = top;
            if (bottomPossible) point2 = bottom;
            else if (leftPossible) point2 = left;
            else point2 = right;
        } else if (bottomPossible) {
            point1 = bottom;
            if (leftPossible) point2 = left;
            else point2 = right;
        } else {
            point1 = left;
            point2 = right;
        }

        graphicsContext.setStroke(getColor());
        graphicsContext.strokeLine(point1.getX(), point1.getY(), point2.getX(), point2.getY());

        if (selected) {
            double screenX2 = screenX - 20 * zoom * yComponent;
            double screenY2 = screenY + 20 * zoom * xComponent;

            Point2D top2 = new Point2D(screenX2 - screenY2 / Math.tan(rotation), 0);
            boolean top2Possible = top2.getX() >= 0 && top2.getX() <= WIDTH;

            Point2D bottom2 = new Point2D(screenX2 + (HEIGHT - screenY2) / Math.tan(rotation), HEIGHT);
            boolean bottom2Possible = bottom2.getX() >= 0 && bottom2.getX() <= WIDTH;

            Point2D left2 = new Point2D(0, screenY2 - screenX2 * Math.tan(rotation));
            boolean left2Possible = left2.getY() >= 0 && left2.getY() <= HEIGHT;

            Point2D right2 = new Point2D(WIDTH, screenY2 + (WIDTH - screenX2) * Math.tan(rotation));

            Point2D point21;
            Point2D point22;

            if (top2Possible) {
                point21 = top2;
                if (bottom2Possible) point22 = bottom2;
                else if (left2Possible) point22 = left2;
                else point22 = right2;
            } else if (bottom2Possible) {
                point21 = bottom2;
                if (left2Possible) point22 = left2;
                else point22 = right2;
            } else {
                point21 = left2;
                point22 = right2;
            }

            graphicsContext.setLineDashes(0);

            graphicsContext.setStroke(Renderer.selectionOutline);
            graphicsContext.setLineWidth(1);
            graphicsContext.setStroke(getColor());
            graphicsContext.strokeLine(point21.getX(), point21.getY(), point22.getX(), point22.getY());

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
