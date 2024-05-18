package com.WooGLEFX.EditorObjects.ObjectCollision;

import com.WooGLEFX.EditorObjects.Components.ObjectPosition;
import com.WooGLEFX.EditorObjects.ObjectUtil;
import com.WooGLEFX.Engine.Renderer;
import com.WooGLEFX.Functions.LevelManager;
import com.WooGLEFX.Structures.EditorObject;
import com.WooGLEFX.Structures.SimpleStructures.DragSettings;
import javafx.geometry.Point2D;

public class AnchorCollider {

    public static DragSettings mouseIntersection(ObjectPosition objectPosition, double mouseX, double mouseY) {

        double x = objectPosition.getX();
        double y = objectPosition.getY();
        double anchorX = objectPosition.getAnchorX();
        double anchorY = objectPosition.getAnchorY();

        double drawWidth = 6;

        double angle = Renderer.angleTo(new Point2D(0, 0), new Point2D(anchorX, anchorY));

        Point2D rotatedMouse = ObjectUtil.rotate(new Point2D(mouseX, mouseY), -angle, new Point2D(x, y));

        double forceScale = 20;

        double forceMagnitude = Math.hypot(anchorX, anchorY) * forceScale;

        if (rotatedMouse.getX() > x - drawWidth / 2 && rotatedMouse.getX() < x + forceMagnitude + drawWidth / 2 && rotatedMouse.getY() > y - drawWidth / 2 && rotatedMouse.getY() < y + drawWidth / 2) {
            DragSettings anchorSettings = new DragSettings(DragSettings.MOVE);
            anchorSettings.setInitialSourceX(mouseX - x);
            anchorSettings.setInitialSourceY(mouseY - y);
            anchorSettings.setObjectPosition(objectPosition);
            return anchorSettings;
        } else {
            return DragSettings.NULL;
        }

    }


    public static DragSettings mouseIntersectingCorners(ObjectPosition objectPosition, double mouseX, double mouseY) {

        double x = objectPosition.getX();
        double y = objectPosition.getY();
        double anchorX = objectPosition.getAnchorX();
        double anchorY = objectPosition.getAnchorY();

        double angle = Renderer.angleTo(new Point2D(0, 0), new Point2D(anchorX, anchorY));

        double forceMagnitude = Math.hypot(anchorX, anchorY);

        Point2D right = new Point2D(x + forceMagnitude, y);

        Point2D rotated = ObjectUtil.rotate(new Point2D(mouseX, mouseY), -angle, new Point2D(x, y));
        if (forceMagnitude != 0 && rotated.getX() > right.getX() - 4 / LevelManager.getLevel().getZoom() &&
                rotated.getX() < right.getX() + 4 / LevelManager.getLevel().getZoom() &&
                rotated.getY() > right.getY() - 4 / LevelManager.getLevel().getZoom() &&
                rotated.getY() < right.getY() + 4 / LevelManager.getLevel().getZoom()) {
            DragSettings anchorSettings = new DragSettings(DragSettings.SETANCHOR);
            anchorSettings.setInitialSourceX(mouseX - anchorX);
            anchorSettings.setInitialSourceY(mouseY - anchorY);
            anchorSettings.setObjectPosition(objectPosition);
            return anchorSettings;
        } else return DragSettings.NULL;

    }

}
