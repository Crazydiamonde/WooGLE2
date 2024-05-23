package com.woogleFX.functions.inputEvents;

import com.woogleFX.editorObjects.objectFunctions.ObjectDrag;
import com.woogleFX.editorObjects.objectFunctions.ObjectResize;
import com.woogleFX.editorObjects.objectFunctions.ObjectRotate;
import com.woogleFX.editorObjects.objectComponents.ObjectComponent;
import com.woogleFX.engine.fx.FXCanvas;
import com.woogleFX.engine.fx.FXContainers;
import com.woogleFX.engine.fx.FXPropertiesView;
import com.woogleFX.engine.fx.FXScene;
import com.woogleFX.engine.Renderer;
import com.woogleFX.engine.SelectionManager;
import com.woogleFX.functions.LevelManager;
import com.woogleFX.structures.simpleStructures.DragSettings;
import com.woogleFX.structures.WorldLevel;
import javafx.scene.Cursor;
import javafx.scene.input.ScrollEvent;
import javafx.scene.transform.Affine;

public class MouseWheelMovedManager {

    /** Called whenever the mouse wheel is scrolled. */
    public static void mouseWheelMoved(ScrollEvent e) {

        WorldLevel level = LevelManager.getLevel();

        // If the mouse was scrolled inside the editor window:
        if (e.getX() < FXContainers.getSplitPane().getDividerPositions()[0] * FXContainers.getSplitPane().getWidth() && level != null) {

            // Calculate the new translation and scale.
            double amt = Math.pow(1.25, (e.getDeltaY() / 40));

            double oldTranslateX = level.getOffsetX();
            double oldTranslateY = level.getOffsetY();

            double oldScaleX = level.getZoom();
            double oldScaleY = level.getZoom();

            double mouseX = e.getX();
            double mouseY = e.getY() - FXCanvas.getMouseYOffset();

            if (oldScaleX * amt > 0.001 && oldScaleX * amt < 1000 && oldScaleY * amt > 0.001
                    && oldScaleY * amt < 1000) {

                double newScaleX = oldScaleX * amt;
                double newScaleY = oldScaleY * amt;

                double newTranslateX = ((oldTranslateX - mouseX) * amt + mouseX);
                double newTranslateY = ((oldTranslateY - mouseY) * amt + mouseY);

                // Transform the canvas according to the updated translation and scale.
                Renderer.t = new Affine();
                Renderer.t.appendTranslation(newTranslateX, newTranslateY);
                Renderer.t.appendScale(newScaleX, newScaleY);

                level.setOffsetX(newTranslateX);
                level.setOffsetY(newTranslateY);
                level.setZoom(newScaleX);

                if (level.getSelected() != null) {
                    DragSettings hit = DragSettings.NULL;

                    double x = (e.getX() - level.getOffsetX()) / level.getZoom();
                    double y = (e.getY() - FXCanvas.getMouseYOffset() - level.getOffsetY()) / level.getZoom();

                    for (ObjectComponent objectComponent : level.getSelected().getObjectComponents()) {

                        if (!objectComponent.isVisible()) continue;

                        DragSettings result = objectComponent.mouseIntersectingCorners(x, y);
                        if (result == DragSettings.NULL) continue;

                        if (result.getType() != DragSettings.MOVE || hit == DragSettings.NULL) {
                            hit = result;
                        }

                    }
                    switch (hit.getType()) {
                        case -1 -> FXScene.getScene().setCursor(Cursor.DEFAULT);
                        case DragSettings.MOVE -> FXScene.getScene().setCursor(Cursor.MOVE);
                        case DragSettings.RESIZE -> FXScene.getScene().setCursor(Cursor.NE_RESIZE);
                        case DragSettings.ROTATE -> FXScene.getScene().setCursor(Cursor.OPEN_HAND);
                    }

                    // Calculate game-relative mouse coordinates.
                    double gameRelativeMouseX = (mouseX - newTranslateX) / newScaleX;
                    double gameRelativeMouseY = (mouseY - newTranslateY) / newScaleX;

                    // Update the selected object according to what kind of operation is being
                    // performed.

                    DragSettings dragSettings = SelectionManager.getDragSettings();

                    switch (dragSettings.getType()) {

                        case DragSettings.MOVE -> ObjectDrag.dragFromMouse(gameRelativeMouseX, gameRelativeMouseY,
                                dragSettings.getInitialSourceX(), dragSettings.getInitialSourceY());

                        case DragSettings.RESIZE -> ObjectResize.resizeFromMouse(
                                gameRelativeMouseX, gameRelativeMouseY,
                                dragSettings.getInitialSourceX(), dragSettings.getInitialSourceY(),
                                dragSettings.getAnchorX(), dragSettings.getAnchorY(),
                                SelectionManager.getDragSettings().getInitialScaleX(), SelectionManager.getDragSettings().getInitialScaleY());

                        case DragSettings.ROTATE -> ObjectRotate.rotateFromMouse(
                                gameRelativeMouseX, gameRelativeMouseY, dragSettings.getInitialSourceX(), dragSettings.getInitialSourceY(), dragSettings.getRotateAngleOffset());

                    }

                    FXPropertiesView.getPropertiesView().refresh();
                }

                // Redraw the canvas.
                Renderer.drawLevelToCanvas(level, FXCanvas.getCanvas());
            }
        }

    }

}
