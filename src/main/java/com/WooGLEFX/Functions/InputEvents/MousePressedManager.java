package com.WooGLEFX.Functions.InputEvents;

import com.WooGLEFX.EditorObjects.Components.ObjectPosition;
import com.WooGLEFX.EditorObjects.ObjectDetection.MouseIntersectingCorners;
import com.WooGLEFX.EditorObjects.ObjectDetection.MouseIntersection;
import com.WooGLEFX.Engine.FX.*;
import com.WooGLEFX.Engine.Renderer;
import com.WooGLEFX.Engine.SelectionManager;
import com.WooGLEFX.Functions.LevelManager;
import com.WooGLEFX.Structures.EditorAttribute;
import com.WooGLEFX.Structures.EditorObject;
import com.WooGLEFX.Structures.SimpleStructures.DragSettings;
import com.WooGLEFX.Structures.WorldLevel;
import com.WorldOfGoo.Level.BallInstance;
import javafx.scene.Cursor;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

import java.util.ArrayList;
import java.util.List;

public class MousePressedManager {

    private static EditorObject getEditorObjectThatHasThis(ObjectPosition objectPosition, WorldLevel level) {

        for (EditorObject editorObject : level.getLevel()) {
            if (List.of(editorObject.getObjectPositions()).contains(objectPosition)) return editorObject;
        }
        for (EditorObject editorObject : level.getScene()) {
            if (List.of(editorObject.getObjectPositions()).contains(objectPosition)) return editorObject;
        }
        for (EditorObject editorObject : level.getResources()) {
            if (List.of(editorObject.getObjectPositions()).contains(objectPosition)) return editorObject;
        }
        for (EditorObject editorObject : level.getAddin()) {
            if (List.of(editorObject.getObjectPositions()).contains(objectPosition)) return editorObject;
        }
        for (EditorObject editorObject : level.getText()) {
            if (List.of(editorObject.getObjectPositions()).contains(objectPosition)) return editorObject;
        }

        return null;

    }

    /** Called whenever the mouse is pressed. */
    public static void eventMousePressed(MouseEvent event) {

        if (event.getButton().equals(MouseButton.PRIMARY)) {

            primaryMouseButton(event);

        } else if (event.getButton() == MouseButton.SECONDARY) {

            double editorViewWidth = FXContainers.getSplitPane().getDividerPositions()[0] * FXContainers.getSplitPane().getWidth();

            // Mouse pan with right-click
            if (event.getX() < editorViewWidth && event.getY() > FXCanvas.getMouseYOffset()) {
                FXScene.getScene().setCursor(Cursor.CLOSED_HAND);
                SelectionManager.setMouseStartX(event.getScreenX());
                SelectionManager.setMouseStartY(event.getScreenY());
            }

        }
    }


    private static void primaryMouseButton(MouseEvent event) {

        WorldLevel level = LevelManager.getLevel();
        if (level == null) return;

        if (level.getSelected() != null) {

            if (FXPropertiesView.getPropertiesView().getEditingCell() == null
                    || FXPropertiesView.getPropertiesView().getFocusModel().focusedIndexProperty().get() == -1) {
                FXPropertiesView.getPropertiesView().edit(-1, null);
            }

            updateOldAttributes(level);

        }

        if (SelectionManager.getMode() == SelectionManager.SELECTION) tryToSelectSomething(event, level);
        else if (SelectionManager.getMode() == SelectionManager.STRAND) tryToPlaceStrand(event, level);

    }


    private static void tryToPlaceStrand(MouseEvent event, WorldLevel level) {

        double mouseX = (event.getX() - level.getOffsetX()) / level.getZoom();
        double mouseY = (event.getY() - FXCanvas.getMouseYOffset() - level.getOffsetY()) / level.getZoom();

        for (EditorObject editorObject : level.getLevel().toArray(new EditorObject[0])) {
            if (editorObject instanceof BallInstance ballInstance) {
                if (MouseIntersection.mouseIntersection(ballInstance, mouseX, mouseY) != DragSettings.NULL) {
                    if (SelectionManager.getStrand1Gooball() == null) {
                        SelectionManager.setStrand1Gooball(ballInstance);
                        return;
                    }
                }
            }
        }

    }


    private static void updateOldAttributes(WorldLevel level) {

        ArrayList<EditorAttribute> output = new ArrayList<>();

        for (EditorAttribute attribute : level.getSelected().getAttributes()) {

            EditorAttribute newAttribute = new EditorAttribute(attribute.getName(), attribute.getType());

            if (!attribute.getDefaultValue().isEmpty()) newAttribute.setDefaultValue(attribute.getDefaultValue());
            newAttribute.setValue(attribute.stringValue());
            if (attribute.getRequiredInFile()) newAttribute.assertRequired();
            output.add(newAttribute);

        }

        SelectionManager.setOldAttributes(output.toArray(new EditorAttribute[0]));
        SelectionManager.setOldSelected(level.getSelected());

    }


    private static void tryToSelectSomething(MouseEvent event, WorldLevel level) {

        double editorViewWidth = FXContainers.getSplitPane().getDividerPositions()[0] * FXContainers.getSplitPane().getWidth();

        if (event.getX() > editorViewWidth || event.getY() < FXCanvas.getMouseYOffset()) return;

        double mouseX = (event.getX() - level.getOffsetX()) / level.getZoom();
        double mouseY = (event.getY() - FXCanvas.getMouseYOffset() - level.getOffsetY()) / level.getZoom();

        if (level.getSelected() != null) {
            SelectionManager.setDragSettings(MouseIntersectingCorners.mouseIntersectingCorners(level.getSelected(), mouseX, mouseY));
            if (SelectionManager.getDragSettings() != DragSettings.NULL) return;
        }

        EditorObject prevSelected = level.getSelected();
        SelectionManager.setSelected(null);
        ArrayList<ObjectPosition> byDepth = Renderer.orderObjectPositionsByDepth(level);
        for (int i = byDepth.size() - 1; i >= 0; i--) {
            ObjectPosition object = byDepth.get(i);
            if (MouseIntersection.forPosition(object, mouseX, mouseY) != DragSettings.NULL) {

                EditorObject selected = getEditorObjectThatHasThis(object, level);
                assert selected != null;

                SelectionManager.setSelected(selected);
                FXPropertiesView.changeTableView(selected);
                selected.getParent().getTreeItem().setExpanded(true);
                FXHierarchy.getHierarchy().getSelectionModel().select(selected.getTreeItem());
                // Scroll to this position in the selection model
                FXHierarchy.getHierarchy().scrollTo(FXHierarchy.getHierarchy().getRow(selected.getTreeItem()));
                break;
            }
        }
        if (level.getSelected() != null && level.getSelected() == prevSelected) {
            DragSettings thisSettings = MouseIntersection.mouseIntersection(level.getSelected(), mouseX, mouseY);
            if (thisSettings != DragSettings.NULL && thisSettings.getType() != DragSettings.NONE) {
                FXStage.getStage().getScene().setCursor(Cursor.MOVE);
                SelectionManager.setDragSettings(thisSettings);
            }
        }

    }

}
