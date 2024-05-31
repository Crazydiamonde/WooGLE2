package com.woogleFX.engine.inputEvents;

import com.woogleFX.editorObjects.EditorObject;
import com.woogleFX.editorObjects.objectComponents.ObjectComponent;
import com.woogleFX.engine.fx.FXCanvas;
import com.woogleFX.engine.fx.FXScene;
import com.woogleFX.engine.SelectionManager;
import com.woogleFX.engine.LevelManager;
import com.woogleFX.editorObjects.DragSettings;
import com.woogleFX.gameData.level._Level;
import javafx.scene.Cursor;
import javafx.scene.input.MouseEvent;

public class MouseMovedManager {

    /** Called whenever the mouse is moved.*/
    public static void eventMouseMoved(MouseEvent event) {

        /* Mouse moved logic overview:
            - If the mouse is touching a selected object's corner, change the cursor accordingly.
            - Otherwise, if the mouse is touching a selected object AT ALL, change the cursor accordingly.
            - Otherwise, set the cursor back to default. */

        // Update the internal mouse position tracker to reflect the new mouse coordinates.
        SelectionManager.setMouseX(event.getX());
        SelectionManager.setMouseY(event.getY() - FXCanvas.getMouseYOffset());

        _Level level = LevelManager.getLevel();
        if (level == null) return;

        double x = (event.getX() - level.getOffsetX()) / level.getZoom();
        double y = (event.getY() - FXCanvas.getMouseYOffset() - level.getOffsetY()) / level.getZoom();

        EditorObject[] selectedList = level.getSelected();
        if (selectedList.length == 0) {
            // If nothing is selected, set the cursor to the default cursor and don't do anything else.
            FXScene.getScene().setCursor(Cursor.DEFAULT);
            return;
        }

        // Check all the selected objects to see if the mouse intersects them.
        DragSettings cornerHit = DragSettings.NULL;
        DragSettings generalHit = DragSettings.NULL;
        for (EditorObject selected : selectedList) for (ObjectComponent component : selected.getObjectComponents()) {

            // If the component isn't visible, don't do anything with it.
            if (!component.isVisible()) continue;

            // Check for corner mouse intersection (resizing, rotating, setting anchor.)
            if (component.isResizable() || component.isRotatable()) {
                DragSettings cornerResult = component.mouseIntersectingCorners(x, y);
                if (cornerResult != DragSettings.NULL) cornerHit = cornerResult;
            }

            // Check for general mouse intersection (dragging.)
            if (component.isDraggable()) {
                DragSettings generalResult = component.mouseIntersection(x, y);
                if (generalResult != DragSettings.NULL) generalHit = generalResult;
            }

        }

        DragSettings overshadowingSettings = MousePressedManager.tryToSelectSomething(event, level);
        if (overshadowingSettings != null) {
            ObjectComponent objectComponent = overshadowingSettings.getObjectComponent();

            boolean isOvershadowed = true;
            for (EditorObject selected : selectedList) if (selected.containsObjectPosition(objectComponent)) {
                isOvershadowed = false;
                break;
            }

            if (isOvershadowed) {
                FXScene.getScene().setCursor(Cursor.DEFAULT);
                return;
            }

        }

        if (cornerHit != DragSettings.NULL) FXScene.getScene().setCursor(switch (cornerHit.getType()) {
            case DragSettings.RESIZE -> Cursor.NE_RESIZE;
            case DragSettings.ROTATE, DragSettings.SETANCHOR -> Cursor.OPEN_HAND;
            default -> Cursor.DEFAULT;
        });

        else if (generalHit != DragSettings.NULL) FXScene.getScene().setCursor(Cursor.MOVE);

        else FXScene.getScene().setCursor(Cursor.DEFAULT);

    }

}
