package com.WooGLEFX.EditorObjects.ObjectFunctions;

import com.WooGLEFX.EditorObjects.Components.ObjectPosition;
import com.WooGLEFX.Engine.SelectionManager;
import com.WooGLEFX.Structures.EditorObject;
import javafx.geometry.Point2D;

public class ObjectResize {

    public static void resizeFromMouse(double mouseX, double mouseY,
                                       double resizeDragAnchorX, double resizeDragAnchorY) {

        ObjectPosition objectPosition = SelectionManager.getDragSettings().getObjectPosition();

        if (objectPosition.getId() == ObjectPosition.CIRCLE || objectPosition.getId() == ObjectPosition.CIRCLE_HOLLOW) {

            objectPosition.setRadius(Math.hypot(mouseX - objectPosition.getX(), mouseY - objectPosition.getY()));

        } else {

            double rotation = objectPosition.getRotation();

            Point2D center = new Point2D((mouseX + resizeDragAnchorX) / 2, (mouseY + resizeDragAnchorY) / 2);

            Point2D rotatedReal = EditorObject.rotate(new Point2D(mouseX, mouseY), -rotation, center);
            Point2D rotatedAnchor = EditorObject.rotate(new Point2D(resizeDragAnchorX, resizeDragAnchorY), -rotation, center);

            double deltaX = rotatedReal.getX() - rotatedAnchor.getX();
            double deltaY = rotatedReal.getY() - rotatedAnchor.getY();

            objectPosition.setWidth(Math.abs(deltaX));
            objectPosition.setHeight(Math.abs(deltaY));

            objectPosition.setX(center.getX());
            objectPosition.setY(center.getY());

        }

    }

}
