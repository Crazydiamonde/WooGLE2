package com.woogleFX.editorObjects.objectFunctions;

import com.woogleFX.editorObjects.objectComponents.CircleComponent;
import com.woogleFX.editorObjects.objectComponents.ImageComponent;
import com.woogleFX.editorObjects.objectComponents.ObjectComponent;
import com.woogleFX.editorObjects.objectComponents.RectangleComponent;
import com.woogleFX.engine.renderer.Renderer;
import com.woogleFX.engine.SelectionManager;
import javafx.geometry.Point2D;

public class ObjectRotate {

    public static void rotateFromMouse(double mouseX, double mouseY, double dragSourceX, double dragSourceY, double rotateAngleOffset) {

        ObjectComponent objectComponent = SelectionManager.getDragSettings().getObjectComponent();

        Point2D object = new Point2D(objectComponent.getX(), objectComponent.getY());

        Point2D source = new Point2D(dragSourceX, dragSourceY);
        Point2D mouse = new Point2D(mouseX, mouseY);

        double angleToSource = Renderer.angleTo(object, source);
        double angleToMouse = Renderer.angleTo(object, mouse);

        double deltaAngle = angleToMouse - angleToSource;

        if (objectComponent instanceof RectangleComponent RC) RC.setRotation(deltaAngle + rotateAngleOffset);
        else if (objectComponent instanceof ImageComponent IC) IC.setRotation(deltaAngle + rotateAngleOffset);
        else if (objectComponent instanceof CircleComponent CC) CC.setRotation(deltaAngle + rotateAngleOffset);

    }

}
