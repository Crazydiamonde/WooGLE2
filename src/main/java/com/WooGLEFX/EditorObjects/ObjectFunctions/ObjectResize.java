package com.WooGLEFX.EditorObjects.ObjectFunctions;

import com.WooGLEFX.EditorObjects.objectcomponents.CircleComponent;
import com.WooGLEFX.EditorObjects.objectcomponents.ImageComponent;
import com.WooGLEFX.EditorObjects.objectcomponents.ObjectComponent;
import com.WooGLEFX.EditorObjects.ObjectUtil;
import com.WooGLEFX.EditorObjects.objectcomponents.RectangleComponent;
import com.WooGLEFX.Engine.SelectionManager;
import javafx.geometry.Point2D;

public class ObjectResize {

    public static void resizeFromMouse(double mouseX, double mouseY,
                                       double resizeDragAnchorX, double resizeDragAnchorY) {

        ObjectComponent objectComponent = SelectionManager.getDragSettings().getObjectComponent();

        if (objectComponent instanceof CircleComponent circleComponent) {

            circleComponent.setRadius(Math.hypot(mouseX - objectComponent.getX(), mouseY - objectComponent.getY()));

        } else if (objectComponent instanceof RectangleComponent rectangleComponent) {

            double rotation = rectangleComponent.getRotation();

            Point2D center = new Point2D((mouseX + resizeDragAnchorX) / 2, (mouseY + resizeDragAnchorY) / 2);

            Point2D rotatedReal = ObjectUtil.rotate(new Point2D(mouseX, mouseY), -rotation, center);
            Point2D rotatedAnchor = ObjectUtil.rotate(new Point2D(resizeDragAnchorX, resizeDragAnchorY), -rotation, center);

            double deltaX = rotatedReal.getX() - rotatedAnchor.getX();
            double deltaY = rotatedReal.getY() - rotatedAnchor.getY();

            rectangleComponent.setWidth(Math.abs(deltaX));
            rectangleComponent.setHeight(Math.abs(deltaY));

            objectComponent.setX(center.getX());
            objectComponent.setY(center.getY());

        } else if (objectComponent instanceof ImageComponent imageComponent) {

            double rotation = imageComponent.getRotation();

            Point2D center = new Point2D((mouseX + resizeDragAnchorX) / 2, (mouseY + resizeDragAnchorY) / 2);

            Point2D rotatedReal = ObjectUtil.rotate(new Point2D(mouseX, mouseY), -rotation, center);
            Point2D rotatedAnchor = ObjectUtil.rotate(new Point2D(resizeDragAnchorX, resizeDragAnchorY), -rotation, center);

            double deltaX = rotatedReal.getX() - rotatedAnchor.getX();
            double deltaY = rotatedReal.getY() - rotatedAnchor.getY();

            imageComponent.setScaleX(Math.abs(deltaX) / imageComponent.getImage().getWidth());
            imageComponent.setScaleY(Math.abs(deltaY) / imageComponent.getImage().getHeight());

            objectComponent.setX(center.getX());
            objectComponent.setY(center.getY());

        }

    }

}
