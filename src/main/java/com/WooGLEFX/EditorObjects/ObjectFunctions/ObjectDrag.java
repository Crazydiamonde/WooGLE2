package com.WooGLEFX.EditorObjects.ObjectFunctions;

import com.WooGLEFX.EditorObjects.Components.ObjectPosition;
import com.WooGLEFX.Engine.SelectionManager;
import com.WooGLEFX.Structures.EditorObject;

public class ObjectDrag {

    public static void dragFromMouse(double mouseX, double mouseY, double dragSourceX, double dragSourceY) {

        ObjectPosition objectPosition = SelectionManager.getDragSettings().getObjectPosition();

        objectPosition.setX(mouseX - dragSourceX);
        objectPosition.setY(mouseY - dragSourceY);

    }

}
