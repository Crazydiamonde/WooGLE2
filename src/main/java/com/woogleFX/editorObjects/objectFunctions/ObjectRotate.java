package com.woogleFX.editorObjects.objectFunctions;

import com.woogleFX.editorObjects.DragSettings;
import com.woogleFX.editorObjects.objectComponents.ObjectComponent;
import com.woogleFX.editorObjects.objectComponents.generic.RotatableProperty;
import com.woogleFX.engine.AssetManager;
import com.woogleFX.engine.renderer.Renderer;
import javafx.geometry.Point2D;

public class ObjectRotate {

    public static void rotateFromMouse(Point2D mousePos, DragSettings dragSettings) {

        ObjectComponent objectComponent = dragSettings.getObjectComponent();

        Point2D object = new Point2D(objectComponent.getX(), objectComponent.getY());

        double angleToSource = Renderer.angleTo(object, dragSettings.getInitialSource());
        double angleToMouse = Renderer.angleTo(object, mousePos);

        double deltaAngle = angleToMouse - angleToSource;

        int selectedI = 0;
        for (ObjectComponent objectComponent1 : AssetManager.getAsset().getSelectedComponents()) {
            if (objectComponent1 == objectComponent) break;
            selectedI++;
        }

        double deltaRotation = 0;
        if (objectComponent instanceof RotatableProperty) {
            deltaRotation = deltaAngle + dragSettings.getRotateAngleOffset() - dragSettings.getOriginalRotations()[selectedI];
        }


        double realOriginalX = dragSettings.getOriginalPositions()[selectedI].getX();
        double realOriginalY = dragSettings.getOriginalPositions()[selectedI].getY();

        for (int i = 0; i < AssetManager.getAsset().getSelectedComponents().length; i++) {
            ObjectComponent objectComponent1 = AssetManager.getAsset().getSelectedComponents()[i];

            double dx = dragSettings.getOriginalPositions()[i].getX() - realOriginalX;
            double dy = dragSettings.getOriginalPositions()[i].getY() - realOriginalY;

            double rotatedDX = dx * Math.cos(deltaAngle) - dy * Math.sin(deltaAngle);
            double rotatedDY = dx * Math.sin(deltaAngle) + dy * Math.cos(deltaAngle);

            objectComponent1.setX(realOriginalX + rotatedDX);
            objectComponent1.setY(realOriginalY + rotatedDY);

            if (objectComponent1 instanceof RotatableProperty rotatableProperty) {
                rotatableProperty.setRotation(dragSettings.getOriginalRotations()[i] + deltaRotation);
            }

        }

    }

}
