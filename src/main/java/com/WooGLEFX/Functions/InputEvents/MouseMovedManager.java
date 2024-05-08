package com.WooGLEFX.Functions.InputEvents;

import com.WooGLEFX.Engine.FX.FXCanvas;
import com.WooGLEFX.Engine.FX.FXContainers;
import com.WooGLEFX.Engine.Main;
import com.WooGLEFX.Engine.SelectionManager;
import com.WooGLEFX.Functions.LevelManager;
import com.WooGLEFX.Structures.SimpleStructures.DragSettings;
import com.WooGLEFX.Structures.WorldLevel;
import javafx.scene.Cursor;
import javafx.scene.input.MouseEvent;

public class MouseMovedManager {

    /** Called whenever the mouse is moved.*/
    public static void eventMouseMoved(MouseEvent event) {
        WorldLevel level = LevelManager.getLevel();
        if (level != null) {
            SelectionManager.setMouseX(event.getX());
            SelectionManager.setMouseY(event.getY() - FXCanvas.getMouseYOffset());
            if (level.getSelected() != null) {
                DragSettings hit = level.getSelected().mouseIntersectingCorners(
                        (event.getX() - level.getOffsetX()) / level.getZoom(),
                        (event.getY() - FXCanvas.getMouseYOffset() - level.getOffsetY()) / level.getZoom());
                switch (hit.getType()) {
                    case DragSettings.NONE -> FXContainers.getStage().getScene().setCursor(Cursor.DEFAULT);
                    case DragSettings.RESIZE -> FXContainers.getStage().getScene().setCursor(Cursor.NE_RESIZE);
                    case DragSettings.ROTATE, DragSettings.SETANCHOR -> FXContainers.getStage().getScene().setCursor(Cursor.OPEN_HAND);
                }
            }
        }
    }

}
