package com.WooGLEFX.Functions.InputEvents;

import com.WooGLEFX.Engine.FX.FXCanvas;
import com.WooGLEFX.Engine.FX.FXContainers;
import com.WooGLEFX.Engine.FX.FXPropertiesView;
import com.WooGLEFX.Engine.Main;
import com.WooGLEFX.Engine.Renderer;
import com.WooGLEFX.Engine.SelectionManager;
import com.WooGLEFX.Functions.LevelManager;
import com.WooGLEFX.Structures.SimpleStructures.DragSettings;
import com.WooGLEFX.Structures.WorldLevel;
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

                double newTranslateX = (int) ((oldTranslateX - mouseX) * amt + mouseX);
                double newTranslateY = (int) ((oldTranslateY - mouseY) * amt + mouseY);

                // Transform the canvas according to the updated translation and scale.
                Renderer.t = new Affine();
                Renderer.t.appendTranslation(newTranslateX, newTranslateY);
                Renderer.t.appendScale(newScaleX, newScaleY);
                FXCanvas.getImageCanvas().getGraphicsContext2D().setTransform(Renderer.t);

                level.setOffsetX(newTranslateX);
                level.setOffsetY(newTranslateY);
                level.setZoom(newScaleX);

                if (level.getSelected() != null) {
                    DragSettings hit = level.getSelected().mouseIntersectingCorners(mouseX, mouseY);
                    switch (hit.getType()) {
                        case DragSettings.NONE -> FXContainers.getStage().getScene().setCursor(Cursor.DEFAULT);
                        case DragSettings.RESIZE -> FXContainers.getStage().getScene().setCursor(Cursor.NE_RESIZE);
                        case DragSettings.ROTATE -> FXContainers.getStage().getScene().setCursor(Cursor.OPEN_HAND);
                    }

                    // Calculate game-relative mouse coordinates.
                    double gameRelativeMouseX = (mouseX - newTranslateX) / newScaleX;
                    double gameRelativeMouseY = (mouseY - newTranslateY) / newScaleX;

                    // Update the selected object according to what kind of operation is being
                    // performed.
                    switch (SelectionManager.getDragSettings().getType()) {
                        case DragSettings.MOVE -> level.getSelected().dragFromMouse(gameRelativeMouseX,
                                gameRelativeMouseY, SelectionManager.getDragSettings().getInitialSourceX(), SelectionManager.getDragSettings().getInitialSourceY());
                        case DragSettings.RESIZE -> level.getSelected().resizeFromMouse(gameRelativeMouseX,
                                gameRelativeMouseY, SelectionManager.getDragSettings().getInitialSourceX(), SelectionManager.getDragSettings().getInitialSourceY(),
                                SelectionManager.getDragSettings().getAnchorX(), SelectionManager.getDragSettings().getAnchorY());
                        case DragSettings.ROTATE -> level.getSelected().rotateFromMouse(gameRelativeMouseX,
                                gameRelativeMouseY, SelectionManager.getDragSettings().getRotateAngleOffset());
                    }

                    FXPropertiesView.getPropertiesView().refresh();
                }

                // Redraw the canvas.
                Renderer.drawEverything(level, FXCanvas.getCanvas(), FXCanvas.getImageCanvas());
            }
        }

    }

}
