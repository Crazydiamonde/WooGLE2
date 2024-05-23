package com.woogleFX.editorObjects.objectFunctions;

import com.woogleFX.editorObjects.objectComponents.AnchorComponent;
import com.woogleFX.editorObjects.objectComponents.ObjectComponent;
import com.woogleFX.engine.SelectionManager;

public class ObjectSetAnchor {

    public static void setAnchor(double mouseX, double mouseY, double anchorStartX, double anchorStartY) {

        double deltaX = mouseX - anchorStartX;
        double deltaY = mouseY - anchorStartY;

        ObjectComponent objectComponent = SelectionManager.getDragSettings().getObjectComponent();

        if (objectComponent instanceof AnchorComponent anchorComponent) {
            anchorComponent.setAnchor(deltaX, deltaY);
        }

    }

}
