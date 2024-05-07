package com.WooGLEFX.Functions.InputEvents;

import com.WooGLEFX.Engine.FX.FXHierarchy;
import com.WooGLEFX.Engine.FX.FXPropertiesView;
import com.WooGLEFX.Engine.Main;
import com.WooGLEFX.Engine.Renderer;
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

        if (level != null) {

            double editorViewWidth = Main.getSplitPane().getDividerPositions()[0] * Main.getSplitPane().getWidth();

            if (event.getButton().equals(MouseButton.PRIMARY)) {

                if (level.getSelected() != null) {
                    if (FXPropertiesView.getPropertiesView().getEditingCell() == null
                            || FXPropertiesView.getPropertiesView().getFocusModel().focusedIndexProperty().get() == -1) {
                        FXPropertiesView.getPropertiesView().edit(-1, null);
                    }
                    Main.setOldAttributes(level.getSelected().cloneAttributes());
                    Main.setOldSelected(level.getSelected());
                }

                if (Main.getMode() == Main.SELECTION) {
                    if (event.getX() < editorViewWidth && event.getY() > Main.getMouseYOffset()) {
                        if (level.getSelected() != null) {
                            Main.setDragSettings(level.getSelected().mouseIntersectingCorners(
                                    (event.getX() - level.getOffsetX()) / level.getZoom(),
                                    (event.getY() - Main.getMouseYOffset() - level.getOffsetY()) / level.getZoom()));

                            /* Dragging of already selected object that might be behind something else */
                            DragSettings thisSettings = level.getSelected().mouseIntersection(
                                    (event.getX() - level.getOffsetX()) / level.getZoom(),
                                    (event.getY() - Main.getMouseYOffset() - level.getOffsetY()) / level.getZoom());
                            DragSettings thisImageSettings = level.getSelected().mouseImageIntersection(
                                    (event.getX() - level.getOffsetX()) / level.getZoom(),
                                    (event.getY() - Main.getMouseYOffset() - level.getOffsetY()) / level.getZoom());
                            if (thisSettings.getType() != DragSettings.NONE) {
                                Main.getStage().getScene().setCursor(Cursor.MOVE);
                                Main.setDragSettings(thisSettings);
                            } else if (thisImageSettings.getType() != DragSettings.NONE) {
                                Main.getStage().getScene().setCursor(Cursor.MOVE);
                                Main.setDragSettings(thisImageSettings);
                            }

                        }
                        if (Main.getDragSettings().getType() == DragSettings.NONE) {

                            EditorObject prevSelected = level.getSelected();
                            Main.setSelected(null);
                            ArrayList<EditorObject> byDepth = Renderer.orderObjectsBySelectionDepth(level);
                            for (int i = byDepth.size() - 1; i >= 0; i--) {
                                EditorObject object = byDepth.get(i);
                                if (object.mouseIntersection((event.getX() - level.getOffsetX()) / level.getZoom(),
                                                (event.getY() - Main.getMouseYOffset() - level.getOffsetY()) / level.getZoom())
                                        .getType() != DragSettings.NONE) {
                                    Main.changeTableView(object);
                                    Main.setSelected(object);
                                    object.getParent().getTreeItem().setExpanded(true);
                                    FXHierarchy.getHierarchy().getSelectionModel().select(object.getTreeItem());
                                    // Scroll to this position in the selection model
                                    FXHierarchy.getHierarchy().scrollTo(FXHierarchy.getHierarchy().getRow(object.getTreeItem()));
                                    break;
                                    // } else if ((object instanceof Circle || object instanceof Rectangle || object
                                    // instanceof Compositegeom || object instanceof Signpost) &&
                                    // object.getAttribute("image").length() > 0) {
                                } else if (object.mouseImageIntersection(
                                                (event.getX() - level.getOffsetX()) / level.getZoom(),
                                                (event.getY() - Main.getMouseYOffset() - level.getOffsetY()) / level.getZoom())
                                        .getType() != DragSettings.NONE) {
                                    Main.changeTableView(object);
                                    Main.setSelected(object);
                                    object.getParent().getTreeItem().setExpanded(true);
                                    FXHierarchy.getHierarchy().getSelectionModel().select(object.getTreeItem());
                                    break;
                                }
                            }
                            if (level.getSelected() != null && level.getSelected() == prevSelected) {
                                DragSettings thisSettings = level.getSelected().mouseIntersection(
                                        (event.getX() - level.getOffsetX()) / level.getZoom(),
                                        (event.getY() - Main.getMouseYOffset() - level.getOffsetY()) / level.getZoom());
                                DragSettings thisImageSettings = level.getSelected().mouseImageIntersection(
                                        (event.getX() - level.getOffsetX()) / level.getZoom(),
                                        (event.getY() - Main.getMouseYOffset() - level.getOffsetY()) / level.getZoom());
                                if (thisSettings.getType() != DragSettings.NONE) {
                                    Main.getStage().getScene().setCursor(Cursor.MOVE);
                                    Main.setDragSettings(thisSettings);
                                } else if (thisImageSettings.getType() != DragSettings.NONE) {
                                    Main.getStage().getScene().setCursor(Cursor.MOVE);
                                    Main.setDragSettings(thisImageSettings);
                                }
                            }
                        }
                    }
                } else if (Main.getMode() == Main.STRAND) {
                    for (EditorObject ball : level.getLevel().toArray(new EditorObject[0])) {
                        if (ball instanceof BallInstance && ball
                                .mouseIntersection((event.getX() - level.getOffsetX()) / level.getZoom(),
                                        (event.getY() - Main.getMouseYOffset() - level.getOffsetY()) / level.getZoom())
                                .getType() != DragSettings.NONE) {
                            if (Main.getStrand1Gooball() == null) {
                                Main.setStrand1Gooball(ball);
                                break;
                            }
                        }
                    }
                }
            } else if (event.getButton() == MouseButton.SECONDARY) {
                // Mouse pan with right-click
                if (event.getX() < editorViewWidth && event.getY() > Main.getMouseYOffset()) {
                    Main.getStage().getScene().setCursor(Cursor.CLOSED_HAND);
                    Main.setMouseStartX(event.getScreenX());
                    Main.setMouseStartY(event.getScreenY());
                }
            }
        }
    }

}
