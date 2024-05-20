package com.WooGLEFX.EditorObjects.ObjectFunctions;

import com.WooGLEFX.EditorObjects.ObjectPosition;
import com.WooGLEFX.EditorObjects.ObjectUtil;
import com.WooGLEFX.Engine.SelectionManager;
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

            Point2D rotatedReal = ObjectUtil.rotate(new Point2D(mouseX, mouseY), -rotation, center);
            Point2D rotatedAnchor = ObjectUtil.rotate(new Point2D(resizeDragAnchorX, resizeDragAnchorY), -rotation, center);

            double deltaX = rotatedReal.getX() - rotatedAnchor.getX();
            double deltaY = rotatedReal.getY() - rotatedAnchor.getY();

            objectPosition.setWidth(Math.abs(deltaX));
            objectPosition.setHeight(Math.abs(deltaY));

            objectPosition.setX(center.getX());
            objectPosition.setY(center.getY());

        }

    }

}
