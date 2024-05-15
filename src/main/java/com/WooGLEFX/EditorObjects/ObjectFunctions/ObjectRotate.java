package com.WooGLEFX.EditorObjects.ObjectFunctions;

import com.WooGLEFX.EditorObjects.Components.ObjectPosition;
import com.WooGLEFX.Engine.Renderer;
import com.WooGLEFX.Engine.SelectionManager;
import javafx.geometry.Point2D;

public class ObjectRotate {

    public static void rotateFromMouse(double mouseX, double mouseY, double dragSourceX, double dragSourceY, double rotateAngleOffset) {

        ObjectPosition objectPosition = SelectionManager.getDragSettings().getObjectPosition();

        Point2D object = new Point2D(objectPosition.getX(), objectPosition.getY());

        Point2D source = new Point2D(dragSourceX, dragSourceY);
        Point2D mouse = new Point2D(mouseX, mouseY);

        double angleToSource = Renderer.angleTo(object, source);
        double angleToMouse = Renderer.angleTo(object, mouse);

        double deltaAngle = angleToMouse - angleToSource;

        objectPosition.setRotation(deltaAngle + rotateAngleOffset);

    }

}
