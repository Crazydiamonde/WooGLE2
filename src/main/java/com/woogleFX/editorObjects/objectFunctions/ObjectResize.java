package com.woogleFX.editorObjects.objectFunctions;

import com.woogleFX.editorObjects.DragSettings;
import com.woogleFX.editorObjects.objectComponents.CircleComponent;
import com.woogleFX.editorObjects.objectComponents.ObjectComponent;
import com.woogleFX.editorObjects.ObjectUtil;
import com.woogleFX.editorObjects.objectComponents.generic.BoundedProperty;
import com.woogleFX.editorObjects.objectComponents.generic.RotatableProperty;
import com.woogleFX.engine.AssetManager;
import javafx.geometry.Point2D;

public class ObjectResize {

    public static void resizeFromMouse(Point2D mousePos, DragSettings dragSettings) {

        ObjectComponent objectComponent = dragSettings.getObjectComponent();

        if (objectComponent instanceof CircleComponent circleComponent) {
            circleComponent.setRadius(mousePos.distance(new Point2D(objectComponent.getX(), objectComponent.getY())));
            return;
        }

        double rotation = 0;
        if (objectComponent instanceof RotatableProperty rotatable) rotation = rotatable.getRotation();

        Point2D center = mousePos.midpoint(dragSettings.getAnchor());

        Point2D rotatedSource = ObjectUtil.rotate(dragSettings.getInitialSource(), -rotation, center);
        Point2D rotatedReal = ObjectUtil.rotate(mousePos, -rotation, center);
        Point2D rotatedAnchor = ObjectUtil.rotate(dragSettings.getAnchor(), -rotation, center);

        int selectedI = 0;
        for (ObjectComponent objectComponent1 : AssetManager.getAsset().getSelectedComponents()) {
            if (objectComponent1 == objectComponent) break;
            selectedI++;
        }

        double selectedComponentX = dragSettings.getOriginalPositions()[selectedI].getX();
        double selectedComponentY = dragSettings.getOriginalPositions()[selectedI].getY();
        double selectedComponentWidth = dragSettings.getOriginalSizes()[selectedI].getX();
        double selectedComponentHeight = dragSettings.getOriginalSizes()[selectedI].getY();

        double deltaX = rotatedReal.getX() - rotatedAnchor.getX();
        deltaX *= Math.signum(dragSettings.getInitialScale().getX());
        deltaX *= Math.signum(rotatedSource.getX() - rotatedAnchor.getX());
        double rescaleX = deltaX / selectedComponentWidth;

        double deltaY = rotatedReal.getY() - rotatedAnchor.getY();
        deltaY *= Math.signum(dragSettings.getInitialScale().getY());
        deltaY *= Math.signum(rotatedSource.getY() - rotatedAnchor.getY());
        double rescaleY = deltaY / selectedComponentHeight;

        for (int i = 0; i < AssetManager.getAsset().getSelectedComponents().length; i++) {

            ObjectComponent component = AssetManager.getAsset().getSelectedComponents()[i];
            if (!(component instanceof BoundedProperty rectangle)) continue;

            double thisComponentX = dragSettings.getOriginalPositions()[i].getX();
            double thisComponentY = dragSettings.getOriginalPositions()[i].getY();
            double thisComponentWidth = dragSettings.getOriginalSizes()[i].getX();
            double thisComponentHeight = dragSettings.getOriginalSizes()[i].getY();

            // TODO: fix behavior when rotated
            rectangle.setWidth(thisComponentWidth * rescaleX);
            rectangle.setHeight(thisComponentHeight * rescaleY);

            component.setX(center.getX() + (thisComponentX - selectedComponentX) * rescaleX);
            component.setY(center.getY() + (thisComponentY - selectedComponentY) * rescaleY);

        }

    }

}
