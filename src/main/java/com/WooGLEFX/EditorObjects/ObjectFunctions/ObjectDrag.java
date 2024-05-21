package com.WooGLEFX.EditorObjects.ObjectFunctions;

import com.WooGLEFX.EditorObjects.objectcomponents.ObjectComponent;
import com.WooGLEFX.Engine.SelectionManager;

public class ObjectDrag {

    public static void dragFromMouse(double mouseX, double mouseY, double dragSourceX, double dragSourceY) {

        ObjectComponent objectComponent = SelectionManager.getDragSettings().getObjectComponent();

        objectComponent.setX(mouseX - dragSourceX);
        objectComponent.setY(mouseY - dragSourceY);

    }

}
