package com.WooGLEFX.Functions.InputEvents;

import com.WooGLEFX.EditorObjects.ObjectFunctions.ObjectDrag;
import com.WooGLEFX.EditorObjects.ObjectFunctions.ObjectResize;
import com.WooGLEFX.EditorObjects.ObjectFunctions.ObjectRotate;
import com.WooGLEFX.EditorObjects.ObjectFunctions.ObjectSetAnchor;
import com.WooGLEFX.Engine.FX.FXCanvas;
import com.WooGLEFX.Engine.FX.FXPropertiesView;
import com.WooGLEFX.Engine.Renderer;
import com.WooGLEFX.Engine.SelectionManager;
import com.WooGLEFX.Functions.LevelManager;
import com.WooGLEFX.Structures.SimpleStructures.DragSettings;
import com.WooGLEFX.Structures.WorldLevel;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.transform.Affine;

public class MouseDraggedManager {

    /** Called whenever the mouse is dragged.*/
    public static void eventMouseDragged(MouseEvent event) {

        WorldLevel level = LevelManager.getLevel();
        if (level == null) return;

        SelectionManager.setMouseX(event.getX());
        SelectionManager.setMouseY(event.getY() - FXCanvas.getMouseYOffset());
        if (level.getSelected() != null && SelectionManager.getDragSettings() != null) {

            // Calculate game-relative mouse coordinates.
            double gameRelativeMouseX = (SelectionManager.getMouseX() - level.getOffsetX()) / level.getZoom();
            double gameRelativeMouseY = (SelectionManager.getMouseY() - level.getOffsetY()) / level.getZoom();

            // Update the selected object according to what kind of operation is being
            // performed.
            switch (SelectionManager.getDragSettings().getType()) {
                case DragSettings.MOVE -> ObjectDrag.dragFromMouse(gameRelativeMouseX, gameRelativeMouseY, SelectionManager.getDragSettings().getInitialSourceX(), SelectionManager.getDragSettings().getInitialSourceY());
                case DragSettings.RESIZE -> ObjectResize.resizeFromMouse(gameRelativeMouseX, gameRelativeMouseY, SelectionManager.getDragSettings().getAnchorX(), SelectionManager.getDragSettings().getAnchorY());
                case DragSettings.ROTATE -> ObjectRotate.rotateFromMouse(gameRelativeMouseX, gameRelativeMouseY, SelectionManager.getDragSettings().getInitialSourceX(), SelectionManager.getDragSettings().getInitialSourceY(), SelectionManager.getDragSettings().getRotateAngleOffset());
                case DragSettings.SETANCHOR -> ObjectSetAnchor.setAnchor(gameRelativeMouseX, gameRelativeMouseY, SelectionManager.getDragSettings().getInitialSourceX(), SelectionManager.getDragSettings().getInitialSourceY());
            }

            FXPropertiesView.getPropertiesView().refresh();
        }

        if (event.getButton() == MouseButton.SECONDARY) {
            // Pan the canvas according to the mouse's movement.
            level.setOffsetX(level.getOffsetX() + event.getScreenX() - SelectionManager.getMouseStartX());
            level.setOffsetY(level.getOffsetY() + event.getScreenY() - SelectionManager.getMouseStartY());
            SelectionManager.setMouseStartX(event.getScreenX());
            SelectionManager.setMouseStartY(event.getScreenY());

            // Apply the transformation to the canvas.
            Renderer.t = new Affine();
            Renderer.t.appendTranslation(level.getOffsetX(), level.getOffsetY());
            Renderer.t.appendScale(level.getZoom(), level.getZoom());

            // Redraw the canvas.
            Renderer.drawLevelToCanvas(level, FXCanvas.getCanvas());
        }

    }

}
