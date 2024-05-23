package com.woogleFX.editorObjects.objectFunctions;

import com.woogleFX.editorObjects.objectComponents.CircleComponent;
import com.woogleFX.editorObjects.objectComponents.ImageComponent;
import com.woogleFX.editorObjects.objectComponents.ObjectComponent;
import com.woogleFX.editorObjects.ObjectUtil;
import com.woogleFX.editorObjects.objectComponents.RectangleComponent;
import com.woogleFX.engine.SelectionManager;
import javafx.geometry.Point2D;

public class ObjectResize {

    public static void resizeFromMouse(double mouseX, double mouseY,
                                       double resizeDragSourceX, double resizeDragSourceY,
                                       double resizeDragAnchorX, double resizeDragAnchorY,
                                       double resizeInitialScaleX, double resizeInitialScaleY) {

        ObjectComponent objectComponent = SelectionManager.getDragSettings().getObjectComponent();

        if (objectComponent instanceof CircleComponent circleComponent) {
            circleComponent.setRadius(Math.hypot(mouseX - objectComponent.getX(), mouseY - objectComponent.getY()));
            return;
        }

        double rotation = 0;
        if (objectComponent instanceof RectangleComponent RC) rotation = RC.getRotation();
        else if (objectComponent instanceof ImageComponent IC) rotation = IC.getRotation();

        Point2D center = new Point2D((mouseX + resizeDragAnchorX) / 2, (mouseY + resizeDragAnchorY) / 2);

        Point2D rotatedReal = ObjectUtil.rotate(new Point2D(mouseX, mouseY), -rotation, center);
        Point2D rotatedAnchor = ObjectUtil.rotate(new Point2D(resizeDragAnchorX, resizeDragAnchorY), -rotation, center);

        double deltaX = rotatedReal.getX() - rotatedAnchor.getX();
        double deltaY = rotatedReal.getY() - rotatedAnchor.getY();

        deltaX *= Math.signum(resizeInitialScaleX);
        deltaX *= Math.signum(resizeDragSourceX - resizeDragAnchorX);

        deltaY *= Math.signum(resizeInitialScaleY);
        deltaY *= Math.signum(resizeDragSourceY - resizeDragAnchorY);

        if (objectComponent instanceof RectangleComponent rectangleComponent) {
            rectangleComponent.setWidth(Math.abs(deltaX));
            rectangleComponent.setHeight(Math.abs(deltaY));
        } else if (objectComponent instanceof ImageComponent imageComponent) {
            imageComponent.setScaleX(deltaX / imageComponent.getImage().getWidth());
            imageComponent.setScaleY(deltaY / imageComponent.getImage().getHeight());
        }

        objectComponent.setX(center.getX());
        objectComponent.setY(center.getY());

    }

}
