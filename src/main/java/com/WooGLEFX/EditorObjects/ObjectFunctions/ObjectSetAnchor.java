package com.WooGLEFX.EditorObjects.ObjectFunctions;

import com.WooGLEFX.Engine.SelectionManager;

public class ObjectSetAnchor {

    public static void setAnchor(double mouseX, double mouseY, double anchorStartX, double anchorStartY) {

        double deltaX = mouseX - anchorStartX;
        double deltaY = mouseY - anchorStartY;

        SelectionManager.getDragSettings().getObjectPosition().setAnchor(deltaX, deltaY);

    }

}
