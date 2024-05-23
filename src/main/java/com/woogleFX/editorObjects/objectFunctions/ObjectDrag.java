package com.woogleFX.editorObjects.objectFunctions;

import com.woogleFX.editorObjects.objectComponents.ObjectComponent;
import com.woogleFX.engine.SelectionManager;

public class ObjectDrag {

    public static void dragFromMouse(double mouseX, double mouseY, double dragSourceX, double dragSourceY) {

        ObjectComponent objectComponent = SelectionManager.getDragSettings().getObjectComponent();

        objectComponent.setX(mouseX - dragSourceX);
        objectComponent.setY(mouseY - dragSourceY);

    }

}
