package com.woogleFX.functions.inputEvents;

import com.woogleFX.editorObjects.objectCreators.ObjectCreator;
import com.woogleFX.editorObjects.objectComponents.ObjectComponent;
import com.woogleFX.engine.fx.FXCanvas;
import com.woogleFX.engine.fx.FXContainers;
import com.woogleFX.engine.fx.FXScene;
import com.woogleFX.engine.SelectionManager;
import com.woogleFX.functions.LevelManager;
import com.woogleFX.editorObjects.objectCreators.ObjectAdder;
import com.woogleFX.functions.undoHandling.UndoManager;
import com.woogleFX.editorObjects.EditorAttribute;
import com.woogleFX.editorObjects.EditorObject;
import com.woogleFX.structures.simpleStructures.DragSettings;
import com.woogleFX.functions.undoHandling.userActions.AttributeChangeAction;
import com.woogleFX.structures.WorldLevel;
import com.worldOfGoo.level.BallInstance;
import javafx.scene.Cursor;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

import java.util.ArrayList;

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

                EditorObject selected = level.getSelected();

                ArrayList<AttributeChangeAction> attributeChangeActions = new ArrayList<>();

                for (EditorAttribute attribute : selected.getAttributes()) {
                    for (EditorAttribute oldAttribute : SelectionManager.getOldAttributes()) {
                        if (attribute.getName().equals(oldAttribute.getName()) && !attribute.stringValue().equals(oldAttribute.stringValue())) {
                            attributeChangeActions.add(new AttributeChangeAction(selected, attribute.getName(), oldAttribute.stringValue(), attribute.stringValue()));
                            level.redoActions.clear();
                        }
                    }
                }

                if (!attributeChangeActions.isEmpty()) UndoManager.registerChange(attributeChangeActions.toArray(new AttributeChangeAction[0]));

            }

            // Reset the cursor's appearance.
            //FXScene.getScene().setCursor(Cursor.DEFAULT);

            // Clear all drag settings now that the mouse has been released.
            SelectionManager.setDragSettings(DragSettings.NULL);
            // If we have started placing a strand, attempt to complete the strand.
            if (SelectionManager.getMode() == SelectionManager.STRAND && SelectionManager.getStrand1Gooball() != null) {
                for (EditorObject ball : level.getLevel().toArray(new EditorObject[0])) {
                    if (ball instanceof BallInstance ballInstance) {
                        if (ball != SelectionManager.getStrand1Gooball()) {

                            double mouseX = (event.getX() - level.getOffsetX()) / level.getZoom();
                            double mouseY = (event.getY() - FXCanvas.getMouseYOffset() - level.getOffsetY()) / level.getZoom();

                            for (ObjectComponent objectComponent : ballInstance.getObjectComponents()) {
                                if (!objectComponent.isVisible()) continue;
                                if (objectComponent.mouseIntersection(mouseX, mouseY) != DragSettings.NULL) {

                                    EditorObject strand = ObjectCreator.create("Strand", level.getLevelObject());

                                    strand.setAttribute("gb1", SelectionManager.getStrand1Gooball().getAttribute("id").stringValue());
                                    strand.setAttribute("gb2", ball.getAttribute("id").stringValue());

                                    level.getLevel().add(strand);
                                    ObjectAdder.addAnything(strand, level.getLevelObject());
                                    break;

                                }
                            }
                        }
                    }
                }
                SelectionManager.setStrand1Gooball(null);
            }
        } else if (event.getButton() == MouseButton.SECONDARY) {
            FXScene.getScene().setCursor(Cursor.DEFAULT);
        }
    }

}
