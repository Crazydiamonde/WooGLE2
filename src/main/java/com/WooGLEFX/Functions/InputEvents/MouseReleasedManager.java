package com.WooGLEFX.Functions.InputEvents;

import com.WooGLEFX.Engine.Main;
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
                && event.getX() < Main.getSplitPane().getDividerPositions()[0] * Main.getSplitPane().getWidth() && level != null) {
            // Record the changes made to the selected object.
            // Clear all possible redos if changes have been made.
            if (level.getSelected() != null && level.getSelected() == Main.getOldSelected() && Main.getOldAttributes() != null) {
                UserAction[] changes = level.getSelected().getUserActions(Main.getOldAttributes());
                if (changes.length > 0) {
                    UndoManager.registerChange(changes);
                    level.redoActions.clear();
                }
            }

            // Reset the cursor's appearance.
            Main.getStage().getScene().setCursor(Cursor.DEFAULT);

            // Clear all drag settings now that the mouse has been released.
            Main.setDragSettings(new DragSettings(DragSettings.NONE));
            // If we have started placing a strand, attempt to complete the strand.
            if (Main.getMode() == Main.STRAND && Main.getStrand1Gooball() != null) {
                for (EditorObject ball : level.getLevel().toArray(new EditorObject[0])) {
                    if (ball != Main.getStrand1Gooball()) {
                        if (ball instanceof BallInstance && ball
                                .mouseIntersection((event.getX() - level.getOffsetX()) / level.getZoom(),
                                        (event.getY() - Main.getMouseYOffset() - level.getOffsetY()) / level.getZoom())
                                .getType() != DragSettings.NONE) {

                            EditorObject strand = EditorObject.create("Strand", new EditorAttribute[0],
                                    level.getLevelObject());

                            strand.setAttribute("gb1", Main.getStrand1Gooball().getAttribute("id"));
                            strand.setAttribute("gb2", ball.getAttribute("id"));

                            strand.setRealName("Strand");

                            level.getLevel().add(strand);
                            ObjectAdder.addAnything(strand, level.getLevelObject());
                            break;
                        }
                    }
                }
                Main.setStrand1Gooball(null);
            }
        } else if (event.getButton() == MouseButton.SECONDARY) {
            Main.getStage().getScene().setCursor(Cursor.DEFAULT);
        }
    }

}
