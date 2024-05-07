package com.WooGLEFX.Functions.InputEvents;

import com.WooGLEFX.Engine.Main;
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
            Main.setMouseX(event.getX());
            Main.setMouseY(event.getY() - Main.getMouseYOffset());
            if (level.getSelected() != null) {
                DragSettings hit = level.getSelected().mouseIntersectingCorners(
                        (event.getX() - level.getOffsetX()) / level.getZoom(),
                        (event.getY() - Main.getMouseYOffset() - level.getOffsetY()) / level.getZoom());
                switch (hit.getType()) {
                    case DragSettings.NONE -> Main.getStage().getScene().setCursor(Cursor.DEFAULT);
                    case DragSettings.RESIZE -> Main.getStage().getScene().setCursor(Cursor.NE_RESIZE);
                    case DragSettings.ROTATE, DragSettings.SETANCHOR -> Main.getStage().getScene().setCursor(Cursor.OPEN_HAND);
                }
            }
        }
    }

}
