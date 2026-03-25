package com.woogleFX.editorObjects.objectFunctions;

import com.woogleFX.editorObjects.DragSettings;
import com.woogleFX.editorObjects.objectComponents.ObjectComponent;
import com.woogleFX.engine.AssetManager;
import com.woogleFX.engine.SelectionManager;
import javafx.geometry.Point2D;

public class ObjectDrag {

    public static void dragFromMouse(Point2D mousePos, DragSettings dragSettings) {

        ObjectComponent objectComponent = SelectionManager.getDragSettings().getObjectComponent();

        double originalX = objectComponent.getX();
        double originalY = objectComponent.getY();

        double[] originalXs = new double[AssetManager.getAsset().getSelectedComponents().length];
        double[] originalYs = new double[AssetManager.getAsset().getSelectedComponents().length];
        for (int i = 0; i < AssetManager.getAsset().getSelectedComponents().length; i++) {
            originalXs[i] = AssetManager.getAsset().getSelectedComponents()[i].getX();
            originalYs[i] = AssetManager.getAsset().getSelectedComponents()[i].getY();
        }

        for (int i = 0; i < AssetManager.getAsset().getSelectedComponents().length; i++) {
            ObjectComponent object = AssetManager.getAsset().getSelectedComponents()[i];
            object.setX(originalXs[i] - originalX + mousePos.getX() - dragSettings.getInitialSource().getX());
            object.setY(originalYs[i] - originalY + mousePos.getY() - dragSettings.getInitialSource().getY());
        }
        
    }

}
