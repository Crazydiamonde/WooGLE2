package com.WooGLEFX.Functions.InputEvents;

import com.WooGLEFX.Engine.FX.FXCanvas;
import com.WooGLEFX.Engine.FX.FXContainers;
import com.WooGLEFX.Engine.Main;
import com.WooGLEFX.Engine.SelectionManager;
import com.WooGLEFX.Functions.LevelManager;
import com.WooGLEFX.Functions.ObjectAdder;
import com.WooGLEFX.Functions.UndoManager;
import com.WooGLEFX.Structures.EditorAttribute;
import com.WooGLEFX.Structures.EditorObject;
import com.WooGLEFX.Structures.SimpleStructures.DragSettings;
import com.WooGLEFX.Structures.UserActions.UserAction;
import com.WooGLEFX.Structures.WorldLevel;
import com.WorldOfGoo.Level.BallInstance;
import javafx.scene.Cursor;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

public class MouseReleasedManager {

    /** Called whenever the mouse is released. */
    public static void eventMouseReleased(MouseEvent event) {

        WorldLevel level = LevelManager.getLevel();

        // If the mouse was released inside the editor window:
        if (event.getButton() == MouseButton.PRIMARY
                && event.getX() < FXContainers.getSplitPane().getDividerPositions()[0] * FXContainers.getSplitPane().getWidth() && level != null) {
            // Record the changes made to the selected object.
            // Clear all possible redos if changes have been made.
            if (level.getSelected() != null && level.getSelected() == SelectionManager.getOldSelected() && SelectionManager.getOldAttributes() != null) {
                UserAction[] changes = level.getSelected().getUserActions(SelectionManager.getOldAttributes());
                if (changes.length > 0) {
                    UndoManager.registerChange(changes);
                    level.redoActions.clear();
                }
            }

            // Reset the cursor's appearance.
            FXContainers.getStage().getScene().setCursor(Cursor.DEFAULT);

            // Clear all drag settings now that the mouse has been released.
            SelectionManager.setDragSettings(DragSettings.NULL);
            // If we have started placing a strand, attempt to complete the strand.
            if (SelectionManager.getMode() == SelectionManager.STRAND && SelectionManager.getStrand1Gooball() != null) {
                for (EditorObject ball : level.getLevel().toArray(new EditorObject[0])) {
                    if (ball != SelectionManager.getStrand1Gooball()) {
                        if (ball instanceof BallInstance ballInstance && ballInstance
                                .mouseIntersection((event.getX() - level.getOffsetX()) / level.getZoom(),
                                        (event.getY() - FXCanvas.getMouseYOffset() - level.getOffsetY()) / level.getZoom())
                                 != DragSettings.NULL) {

                            EditorObject strand = EditorObject.create("Strand", level.getLevelObject());

                            strand.setAttribute("gb1", SelectionManager.getStrand1Gooball().getAttribute("id"));
                            strand.setAttribute("gb2", ball.getAttribute("id"));

                            strand.setRealName("Strand");

                            level.getLevel().add(strand);
                            ObjectAdder.addAnything(strand, level.getLevelObject());
                            break;
                        }
                    }
                }
                SelectionManager.setStrand1Gooball(null);
            }
        } else if (event.getButton() == MouseButton.SECONDARY) {
            FXContainers.getStage().getScene().setCursor(Cursor.DEFAULT);
        }
    }

}
