package com.WooGLEFX.Functions.InputEvents;

import com.WooGLEFX.EditorObjects.ObjectDetection.MouseIntersectingCorners;
import com.WooGLEFX.EditorObjects.ObjectDetection.MouseIntersection;
import com.WooGLEFX.Engine.FX.FXCanvas;
import com.WooGLEFX.Engine.FX.FXContainers;
import com.WooGLEFX.Engine.FX.FXHierarchy;
import com.WooGLEFX.Engine.FX.FXPropertiesView;
import com.WooGLEFX.Engine.Renderer;
import com.WooGLEFX.Engine.SelectionManager;
import com.WooGLEFX.Functions.LevelManager;
import com.WooGLEFX.Structures.EditorObject;
import com.WooGLEFX.Structures.SimpleStructures.DragSettings;
import com.WooGLEFX.Structures.WorldLevel;
import com.WorldOfGoo.Level.BallInstance;
import javafx.scene.Cursor;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

import java.util.ArrayList;

public class MousePressedManager {

    /** Called whenever the mouse is pressed. */
    public static void eventMousePressed(MouseEvent event) {

        WorldLevel level = LevelManager.getLevel();
        if (level == null) return;

        double editorViewWidth = FXContainers.getSplitPane().getDividerPositions()[0] * FXContainers.getSplitPane().getWidth();

        if (event.getButton().equals(MouseButton.PRIMARY)) {

            if (level.getSelected() != null) {
                if (FXPropertiesView.getPropertiesView().getEditingCell() == null
                        || FXPropertiesView.getPropertiesView().getFocusModel().focusedIndexProperty().get() == -1) {
                    FXPropertiesView.getPropertiesView().edit(-1, null);
                }
                SelectionManager.setOldAttributes(level.getSelected().cloneAttributes());
                SelectionManager.setOldSelected(level.getSelected());
            }

            if (SelectionManager.getMode() == SelectionManager.SELECTION) {
                if (event.getX() > editorViewWidth || event.getY() < FXCanvas.getMouseYOffset()) return;

                double mouseX = (event.getX() - level.getOffsetX()) / level.getZoom();
                double mouseY = (event.getY() - FXCanvas.getMouseYOffset() - level.getOffsetY()) / level.getZoom();

                if (level.getSelected() != null) {
                    SelectionManager.setDragSettings(MouseIntersectingCorners.mouseIntersectingCorners(level.getSelected(), mouseX, mouseY));
                    if (SelectionManager.getDragSettings() != DragSettings.NULL) return;
                }

                EditorObject prevSelected = level.getSelected();
                SelectionManager.setSelected(null);
                ArrayList<EditorObject> byDepth = Renderer.orderObjectsBySelectionDepth(level);
                for (int i = byDepth.size() - 1; i >= 0; i--) {
                    EditorObject object = byDepth.get(i);
                    if (MouseIntersection.mouseIntersection(object, mouseX, mouseY) != DragSettings.NULL) {
                        SelectionManager.setSelected(object);
                        FXPropertiesView.changeTableView(object);
                        object.getParent().getTreeItem().setExpanded(true);
                        FXHierarchy.getHierarchy().getSelectionModel().select(object.getTreeItem());
                        // Scroll to this position in the selection model
                        FXHierarchy.getHierarchy().scrollTo(FXHierarchy.getHierarchy().getRow(object.getTreeItem()));
                        break;
                    }
                }
                if (level.getSelected() != null && level.getSelected() == prevSelected) {
                    DragSettings thisSettings = MouseIntersection.mouseIntersection(level.getSelected(), mouseX, mouseY);
                    if (thisSettings != DragSettings.NULL && thisSettings.getType() != DragSettings.NONE) {
                        FXContainers.getStage().getScene().setCursor(Cursor.MOVE);
                        SelectionManager.setDragSettings(thisSettings);
                    }
                }
            } else if (SelectionManager.getMode() == SelectionManager.STRAND) {

                double mouseX = (event.getX() - level.getOffsetX()) / level.getZoom();
                double mouseY = (event.getY() - FXCanvas.getMouseYOffset() - level.getOffsetY()) / level.getZoom();

                for (EditorObject editorObject : level.getLevel().toArray(new EditorObject[0])) {
                    if (editorObject instanceof BallInstance ballInstance) {
                        if (MouseIntersection.mouseIntersection(ballInstance, mouseX, mouseY) != DragSettings.NULL) {
                            if (SelectionManager.getStrand1Gooball() == null) {
                                SelectionManager.setStrand1Gooball(ballInstance);
                                break;
                            }
                        }
                    }
                }
            }
        } else if (event.getButton() == MouseButton.SECONDARY) {
            // Mouse pan with right-click
            if (event.getX() < editorViewWidth && event.getY() > FXCanvas.getMouseYOffset()) {
                FXContainers.getStage().getScene().setCursor(Cursor.CLOSED_HAND);
                SelectionManager.setMouseStartX(event.getScreenX());
                SelectionManager.setMouseStartY(event.getScreenY());
            }
        }
    }

}
