package com.woogleFX.functions.inputEvents;

import com.woogleFX.editorObjects.objectFunctions.ObjectDrag;
import com.woogleFX.editorObjects.objectFunctions.ObjectResize;
import com.woogleFX.editorObjects.objectFunctions.ObjectRotate;
import com.woogleFX.editorObjects.objectFunctions.ObjectSetAnchor;
import com.woogleFX.engine.fx.FXCanvas;
import com.woogleFX.engine.fx.FXPropertiesView;
import com.woogleFX.engine.Renderer;
import com.woogleFX.engine.SelectionManager;
import com.woogleFX.functions.LevelManager;
import com.woogleFX.structures.simpleStructures.DragSettings;
import com.woogleFX.structures.WorldLevel;
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
        if (level.getSelected().length != 0 && SelectionManager.getDragSettings() != null) {

            // Calculate game-relative mouse coordinates.
            double gameRelativeMouseX = (SelectionManager.getMouseX() - level.getOffsetX()) / level.getZoom();
            double gameRelativeMouseY = (SelectionManager.getMouseY() - level.getOffsetY()) / level.getZoom();

            // Update the selected object according to what kind of operation is being
            // performed.
            switch (SelectionManager.getDragSettings().getType()) {
                case DragSettings.MOVE -> ObjectDrag.dragFromMouse(gameRelativeMouseX, gameRelativeMouseY, SelectionManager.getDragSettings().getInitialSourceX(), SelectionManager.getDragSettings().getInitialSourceY());
                case DragSettings.RESIZE -> ObjectResize.resizeFromMouse(gameRelativeMouseX, gameRelativeMouseY, SelectionManager.getDragSettings().getInitialSourceX(), SelectionManager.getDragSettings().getInitialSourceY(), SelectionManager.getDragSettings().getAnchorX(), SelectionManager.getDragSettings().getAnchorY(), SelectionManager.getDragSettings().getInitialScaleX(), SelectionManager.getDragSettings().getInitialScaleY());
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
