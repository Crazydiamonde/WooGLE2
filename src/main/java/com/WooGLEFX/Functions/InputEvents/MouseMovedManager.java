package com.WooGLEFX.Functions.InputEvents;

import com.WooGLEFX.EditorObjects.ObjectDetection.MouseIntersectingCorners;
import com.WooGLEFX.EditorObjects.ObjectDetection.MouseIntersection;
import com.WooGLEFX.Engine.FX.FXCanvas;
import com.WooGLEFX.Engine.FX.FXContainers;
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
        DragSettings hit = MouseIntersectingCorners.mouseIntersectingCorners(level.getSelected(), x, y);
        Cursor cursor = null;
        switch (hit.getType()) {
            case DragSettings.NONE -> cursor = Cursor.DEFAULT;
            case DragSettings.RESIZE -> cursor = Cursor.NE_RESIZE;
            case DragSettings.ROTATE, DragSettings.SETANCHOR -> cursor = Cursor.OPEN_HAND;
        }
        if (cursor != null) FXContainers.getStage().getScene().setCursor(cursor);
        else {
            DragSettings intersectionSettings = MouseIntersection.mouseIntersection(level.getSelected(), x, y);
            if (intersectionSettings != DragSettings.NULL && intersectionSettings.getType() != DragSettings.NONE) {
                FXContainers.getStage().getScene().setCursor(Cursor.MOVE);
            } else FXContainers.getStage().getScene().setCursor(Cursor.DEFAULT);
        }

    }

}
