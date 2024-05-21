package com.WooGLEFX.EditorObjects.ObjectFunctions;

import com.WooGLEFX.EditorObjects.objectcomponents.AnchorComponent;
import com.WooGLEFX.EditorObjects.objectcomponents.ObjectComponent;
import com.WooGLEFX.Engine.SelectionManager;

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
