package com.WooGLEFX.EditorObjects.ObjectCollision;

import com.WooGLEFX.EditorObjects.ObjectPosition;
import com.WooGLEFX.Structures.SimpleStructures.DragSettings;

public class LineCollider {

    public static DragSettings mouseIntersection(ObjectPosition objectPosition, double mouseX, double mouseY) {

        double x = objectPosition.getX();
        double y = objectPosition.getY();

        double rotation = objectPosition.getRotation();

        double xComponent = Math.cos(-rotation);
        double yComponent = Math.sin(-rotation);

        // Line equation: (y - y_0) = m(x - x_0)
        //                (y - y_0) = (sin/cos)(x - x_0)
        //                (y - y_0)*cos = (x - x_0)*sin

        // (y - y_0)*cos - (x - x_0)*sin = 0

        // This can be used to check if a position is above or below the line.

        double deltaX = objectPosition.getEdgeSize() / 2 * xComponent;
        double deltaY = objectPosition.getEdgeSize() / 2 * yComponent;

        double x1 = x - deltaY;
        double y1 = y - deltaX;
        double mouseValue1 = (mouseY - y1) * xComponent + (mouseX - x1) * yComponent;

        double x2 = x + deltaY;
        double y2 = y + deltaX;
        double mouseValue2 = (mouseY - y2) * xComponent + (mouseX - x2) * yComponent;

        if (mouseValue1 > 0 && mouseValue2 > 0 || mouseValue1 < 0 && mouseValue2 < 0) return DragSettings.NULL;

        DragSettings dragSettings = new DragSettings(DragSettings.MOVE);
        dragSettings.setInitialSourceX(mouseX - x);
        dragSettings.setInitialSourceY(mouseY - y);
        dragSettings.setObjectPosition(objectPosition);
        return dragSettings;

    }

}
