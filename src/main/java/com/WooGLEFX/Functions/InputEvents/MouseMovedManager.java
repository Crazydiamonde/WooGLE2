package com.WooGLEFX.Functions.InputEvents;

import com.WooGLEFX.EditorObjects.EditorObject;
import com.WooGLEFX.EditorObjects.objectcomponents.ObjectComponent;
import com.WooGLEFX.Engine.FX.FXCanvas;
import com.WooGLEFX.Engine.FX.FXScene;
import com.WooGLEFX.Engine.SelectionManager;
import com.WooGLEFX.Functions.LevelManager;
import com.WooGLEFX.Structures.SimpleStructures.DragSettings;
import com.WooGLEFX.Structures.WorldLevel;
import javafx.scene.Cursor;
import javafx.scene.input.MouseEvent;

public class MouseMovedManager {

    /** Called whenever the mouse is moved.*/
    public static void eventMouseMoved(MouseEvent event) {

        SelectionManager.setMouseX(event.getX());
        SelectionManager.setMouseY(event.getY() - FXCanvas.getMouseYOffset());

        WorldLevel level = LevelManager.getLevel();
        if (level == null || level.getSelected() == null) return;

        double x = (event.getX() - level.getOffsetX()) / level.getZoom();
        double y = (event.getY() - FXCanvas.getMouseYOffset() - level.getOffsetY()) / level.getZoom();

        EditorObject selected = level.getSelected();
        if (selected == null) {
            FXScene.getScene().setCursor(Cursor.DEFAULT);
            return;
        }

        DragSettings hit = DragSettings.NULL;

        for (ObjectComponent objectComponent : selected.getObjectComponents()) {

            if (!objectComponent.isVisible()) continue;

            DragSettings result = objectComponent.mouseIntersectingCorners(x, y);
            if (result == DragSettings.NULL) continue;

            if (result.getType() != DragSettings.MOVE || hit == DragSettings.NULL) {
                hit = result;
            }

        }

        Cursor cursor = null;
        switch (hit.getType()) {
            case DragSettings.NONE -> cursor = Cursor.DEFAULT;
            case DragSettings.RESIZE -> cursor = Cursor.NE_RESIZE;
            case DragSettings.ROTATE, DragSettings.SETANCHOR -> cursor = Cursor.OPEN_HAND;
        }
        if (cursor != null) FXScene.getScene().setCursor(cursor);
        else {
            DragSettings dragSettings = MousePressedManager.tryToSelectSomething(event, level);
            if (dragSettings != DragSettings.NULL &&
                    !level.getSelected().containsObjectPosition(dragSettings.getObjectComponent())) {
                FXScene.getScene().setCursor(Cursor.DEFAULT);
                return;
            }
            for (ObjectComponent objectComponent : selected.getObjectComponents()) {
                if (objectComponent.mouseIntersection(x, y) != DragSettings.NULL) {
                    FXScene.getScene().setCursor(Cursor.MOVE);
                    return;
                }
            }
            FXScene.getScene().setCursor(Cursor.DEFAULT);
        }

    }

}
