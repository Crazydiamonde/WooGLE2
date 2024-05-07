package com.WooGLEFX.Functions.InputEvents;

import com.WooGLEFX.Engine.FX.FXPropertiesView;
import com.WooGLEFX.Engine.Main;
import com.WooGLEFX.Engine.Renderer;
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

        Main.setMouseX(event.getX());
        Main.setMouseY(event.getY() - Main.getMouseYOffset());
        if (level != null && level.getSelected() != null && Main.getDragSettings() != null) {

            // Calculate game-relative mouse coordinates.
            double gameRelativeMouseX = (Main.getMouseX() - level.getOffsetX()) / level.getZoom();
            double gameRelativeMouseY = (Main.getMouseY() - level.getOffsetY()) / level.getZoom();

            // Update the selected object according to what kind of operation is being
            // performed.
            switch (Main.getDragSettings().getType()) {
                case DragSettings.MOVE -> level.getSelected().dragFromMouse(gameRelativeMouseX, gameRelativeMouseY,
                        Main.getDragSettings().getInitialSourceX(), Main.getDragSettings().getInitialSourceY());
                case DragSettings.RESIZE -> level.getSelected().resizeFromMouse(gameRelativeMouseX, gameRelativeMouseY,
                        Main.getDragSettings().getInitialSourceX(), Main.getDragSettings().getInitialSourceY(), Main.getDragSettings().getAnchorX(),
                        Main.getDragSettings().getAnchorY());
                case DragSettings.ROTATE -> level.getSelected().rotateFromMouse(gameRelativeMouseX, gameRelativeMouseY,
                        Main.getDragSettings().getRotateAngleOffset());
                case DragSettings.SETANCHOR -> level.getSelected().setAnchor(gameRelativeMouseX, gameRelativeMouseY,
                        Main.getDragSettings().getInitialSourceX(), Main.getDragSettings().getInitialSourceY());
            }

            FXPropertiesView.getPropertiesView().refresh();
        }
        if (level != null && event.getButton() == MouseButton.SECONDARY) {
            // Pan the canvas according to the mouse's movement.
            level.setOffsetX(level.getOffsetX() + event.getScreenX() - Main.getMouseStartX());
            level.setOffsetY(level.getOffsetY() + event.getScreenY() - Main.getMouseStartY());
            Main.setMouseStartX(event.getScreenX());
            Main.setMouseStartY(event.getScreenY());

            // Apply the transformation to the canvas.
            Main.t = new Affine();
            Main.t.appendTranslation(level.getOffsetX(), level.getOffsetY());
            Main.t.appendScale(level.getZoom(), level.getZoom());
            Main.getImageCanvas().getGraphicsContext2D().setTransform(Main.t);

            // Redraw the canvas.
            Renderer.drawEverything(level, Main.getCanvas(), Main.getImageCanvas());
        }
    }

}
