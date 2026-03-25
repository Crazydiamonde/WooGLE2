package com.woogleFX.editorObjects.objectFunctions;

import com.woogleFX.editorObjects.DragSettings;
import com.woogleFX.editorObjects.objectComponents.AnchorComponent;
import com.woogleFX.editorObjects.objectComponents.ObjectComponent;
import com.woogleFX.engine.SelectionManager;
import javafx.geometry.Point2D;

public class ObjectSetAnchor {

    public static void setAnchor(Point2D mousePos, DragSettings dragSettings) {

        double deltaX = mousePos.getX() - dragSettings.getInitialSource().getX();
        double deltaY = mousePos.getY() - dragSettings.getInitialSource().getY();

        ObjectComponent objectComponent = dragSettings.getObjectComponent();

        if (objectComponent instanceof AnchorComponent anchorComponent) {
            anchorComponent.setAnchor(deltaX, deltaY);
        }

    }

}
