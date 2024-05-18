package com.WooGLEFX.EditorObjects.ObjectCollision;

import com.WooGLEFX.EditorObjects.Components.ObjectPosition;
import com.WooGLEFX.Functions.LevelManager;
import com.WooGLEFX.Structures.SimpleStructures.DragSettings;
import javafx.geometry.Point2D;

public class CircleCollider {

    public static DragSettings mouseIntersection(ObjectPosition objectPosition, double mouseX, double mouseY, double edge) {

        double x = objectPosition.getX();
        double y = objectPosition.getY();
        double radius = objectPosition.getRadius() - 2 * edge;

        if (Math.hypot(mouseX - x, mouseY - y) < radius) {
            DragSettings dragSettings = new DragSettings(DragSettings.MOVE);
            dragSettings.setInitialSourceX(mouseX - x);
            dragSettings.setInitialSourceY(mouseY - y);
            dragSettings.setObjectPosition(objectPosition);
            return dragSettings;
        } else {
            return DragSettings.NULL;
        }

    }


    private static boolean rectangleIntersection(double mouseX, double mouseY, Point2D point, double distance) {

        return mouseX > point.getX() - distance &&
                mouseX < point.getX() + distance &&
                mouseY > point.getY() - distance &&
                mouseY < point.getY() + distance;

    }


    public static DragSettings mouseIntersectingCorners(ObjectPosition objectPosition, double mouseX, double mouseY) {

        double x = objectPosition.getX();
        double y = objectPosition.getY();
        double radius = objectPosition.getRadius();

        Point2D left = new Point2D(x - radius, y);
        Point2D top = new Point2D(x, y - radius);
        Point2D right = new Point2D(x + radius, y);
        Point2D bottom = new Point2D(x, y + radius);
        double distance = 4 / LevelManager.getLevel().getZoom();

        if (objectPosition.isResizable() && (
                rectangleIntersection(mouseX, mouseY, left, distance) ||
                rectangleIntersection(mouseX, mouseY, top, distance) ||
                rectangleIntersection(mouseX, mouseY, right, distance) ||
                rectangleIntersection(mouseX, mouseY, bottom, distance)
        )) {

            DragSettings resizeSettings = new DragSettings(DragSettings.RESIZE);
            resizeSettings.setObjectPosition(objectPosition);
            resizeSettings.setInitialSourceX(0);
            resizeSettings.setInitialSourceY(0);
            resizeSettings.setAnchorX(0);
            resizeSettings.setAnchorY(0);
            return resizeSettings;

        }

        if (objectPosition.isRotatable() && (
                rectangleIntersection(mouseX, mouseY, left, distance) ||
                rectangleIntersection(mouseX, mouseY, right, distance)
        )) {

            DragSettings rotateSettings = new DragSettings(DragSettings.ROTATE);
            rotateSettings.setObjectPosition(objectPosition);
            rotateSettings.setInitialSourceX(0);
            rotateSettings.setInitialSourceY(0);
            return rotateSettings;

        }

        return DragSettings.NULL;

    }


    public static DragSettings mouseEdgeIntersection(ObjectPosition objectPosition, double mouseX, double mouseY) {

        DragSettings inside = mouseIntersection(objectPosition, mouseX, mouseY, objectPosition.getEdgeSize());
        if (inside != DragSettings.NULL) return DragSettings.NULL;

        return mouseIntersection(objectPosition, mouseX, mouseY, 0);

    }

}
