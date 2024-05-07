package com.WooGLEFX.Functions.InputEvents;

import com.WooGLEFX.Engine.FX.FXPropertiesView;
import com.WooGLEFX.Engine.Main;
import com.WooGLEFX.Engine.Renderer;
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
        if (e.getX() < Main.getSplitPane().getDividerPositions()[0] * Main.getSplitPane().getWidth() && level != null) {

            // Calculate the new translation and scale.
            double amt = Math.pow(1.25, (e.getDeltaY() / 40));

            double oldTranslateX = level.getOffsetX();
            double oldTranslateY = level.getOffsetY();

            double oldScaleX = level.getZoom();
            double oldScaleY = level.getZoom();

            double mouseX = e.getX();
            double mouseY = e.getY() - Main.getMouseYOffset();

            if (oldScaleX * amt > 0.001 && oldScaleX * amt < 1000 && oldScaleY * amt > 0.001
                    && oldScaleY * amt < 1000) {

                double newScaleX = oldScaleX * amt;
                double newScaleY = oldScaleY * amt;

                double newTranslateX = (int) ((oldTranslateX - mouseX) * amt + mouseX);
                double newTranslateY = (int) ((oldTranslateY - mouseY) * amt + mouseY);

                // Transform the canvas according to the updated translation and scale.
                Main.t = new Affine();
                Main.t.appendTranslation(newTranslateX, newTranslateY);
                Main.t.appendScale(newScaleX, newScaleY);
                Main.getImageCanvas().getGraphicsContext2D().setTransform(Main.t);

                level.setOffsetX(newTranslateX);
                level.setOffsetY(newTranslateY);
                level.setZoom(newScaleX);

                if (level.getSelected() != null) {
                    DragSettings hit = level.getSelected().mouseIntersectingCorners(mouseX, mouseY);
                    switch (hit.getType()) {
                        case DragSettings.NONE -> Main.getStage().getScene().setCursor(Cursor.DEFAULT);
                        case DragSettings.RESIZE -> Main.getStage().getScene().setCursor(Cursor.NE_RESIZE);
                        case DragSettings.ROTATE -> Main.getStage().getScene().setCursor(Cursor.OPEN_HAND);
                    }

                    // Calculate game-relative mouse coordinates.
                    double gameRelativeMouseX = (mouseX - newTranslateX) / newScaleX;
                    double gameRelativeMouseY = (mouseY - newTranslateY) / newScaleX;

                    // Update the selected object according to what kind of operation is being
                    // performed.
                    switch (Main.getDragSettings().getType()) {
                        case DragSettings.MOVE -> level.getSelected().dragFromMouse(gameRelativeMouseX,
                                gameRelativeMouseY, Main.getDragSettings().getInitialSourceX(), Main.getDragSettings().getInitialSourceY());
                        case DragSettings.RESIZE -> level.getSelected().resizeFromMouse(gameRelativeMouseX,
                                gameRelativeMouseY, Main.getDragSettings().getInitialSourceX(), Main.getDragSettings().getInitialSourceY(),
                                Main.getDragSettings().getAnchorX(), Main.getDragSettings().getAnchorY());
                        case DragSettings.ROTATE -> level.getSelected().rotateFromMouse(gameRelativeMouseX,
                                gameRelativeMouseY, Main.getDragSettings().getRotateAngleOffset());
                    }

                    FXPropertiesView.getPropertiesView().refresh();
                }

                // Redraw the canvas.
                Renderer.drawEverything(level, Main.getCanvas(), Main.getImageCanvas());
            }
        }

    }

}
