package com.woogleFX.engine.inputEvents;

import com.woogleFX.assets.Asset;
import com.woogleFX.editorObjects.objectComponents.ObjectComponent;
import com.woogleFX.engine.fx.FXCanvas;
import com.woogleFX.engine.fx.FXScene;
import com.woogleFX.engine.SelectionManager;
import com.woogleFX.engine.AssetManager;
import com.woogleFX.editorObjects.DragSettings;
import javafx.scene.Cursor;
import javafx.scene.input.MouseEvent;

import java.util.Arrays;

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

        Asset level = AssetManager.getAsset();
        if (level == null) return;

        double x = (event.getX() - level.getOffsetX()) / level.getZoom();
        double y = (event.getY() - FXCanvas.getMouseYOffset() - level.getOffsetY()) / level.getZoom();

        ObjectComponent[] selectedList = level.getSelectedComponents();
        if (selectedList.length == 0) {
            // If nothing is selected, set the cursor to the default cursor and don't do anything else.
            FXScene.getScene().setCursor(Cursor.DEFAULT);
            return;
        }

        // Check all the selected objects to see if the mouse intersects them.
        DragSettings cornerHit = DragSettings.NULL;
        DragSettings generalHit = DragSettings.NULL;
        for (ObjectComponent component : selectedList) {

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

            // If this component isn't selected (the user is hovering over a component that overshadows it):
            if (Arrays.stream(selectedList).noneMatch(e -> e == objectComponent)) {
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
