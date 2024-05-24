package com.woogleFX.functions.inputEvents;

import com.woogleFX.editorObjects.objectComponents.ObjectComponent;
import com.woogleFX.engine.fx.*;
import com.woogleFX.engine.Renderer;
import com.woogleFX.engine.SelectionManager;
import com.woogleFX.functions.LevelManager;
import com.woogleFX.editorObjects.EditorAttribute;
import com.woogleFX.editorObjects.EditorObject;
import com.woogleFX.structures.simpleStructures.DragSettings;
import com.woogleFX.structures.WorldLevel;
import com.worldOfGoo.level.BallInstance;
import javafx.scene.Cursor;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TreeTableView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

import java.util.ArrayList;
import java.util.List;

public class MousePressedManager {

    private static EditorObject getEditorObjectThatHasThis(ObjectComponent objectComponent, WorldLevel level) {

        for (EditorObject editorObject : level.getLevel()) {
            if (List.of(editorObject.getObjectComponents()).contains(objectComponent)) return editorObject;
        }
        for (EditorObject editorObject : level.getScene()) {
            if (List.of(editorObject.getObjectComponents()).contains(objectComponent)) return editorObject;
        }
        for (EditorObject editorObject : level.getResources()) {
            if (List.of(editorObject.getObjectComponents()).contains(objectComponent)) return editorObject;
        }
        for (EditorObject editorObject : level.getAddin()) {
            if (List.of(editorObject.getObjectComponents()).contains(objectComponent)) return editorObject;
        }
        for (EditorObject editorObject : level.getText()) {
            if (List.of(editorObject.getObjectComponents()).contains(objectComponent)) return editorObject;
        }

        return null;

    }


    /** Called whenever the mouse is pressed. */
    public static void eventMousePressed(MouseEvent event) {
        if (event.getButton() == MouseButton.PRIMARY) primaryMouseButton(event);
        else if (event.getButton() == MouseButton.SECONDARY) secondaryMouseButton(event);
    }


    private static void primaryMouseButton(MouseEvent event) {

        WorldLevel level = LevelManager.getLevel();
        if (level == null) return;

        if (level.getSelected() != null) ifSelectedAlreadyExists(level);

        if (event.getY() < FXCanvas.getMouseYOffset()) return;

        if (SelectionManager.getMode() == SelectionManager.SELECTION) manageSelection(event, level);
        else if (SelectionManager.getMode() == SelectionManager.STRAND) tryToPlaceStrand(event, level);

    }


    private static void secondaryMouseButton(MouseEvent event) {

        SplitPane splitPane = FXContainers.getSplitPane();
        double editorViewWidth = splitPane.getDividerPositions()[0] * splitPane.getWidth();

        // Mouse pan with right-click
        if (event.getX() < editorViewWidth && event.getY() > FXCanvas.getMouseYOffset()) {
            FXScene.getScene().setCursor(Cursor.CLOSED_HAND);
            SelectionManager.setMouseStartX(event.getScreenX());
            SelectionManager.setMouseStartY(event.getScreenY());
        }

    }


    private static void ifSelectedAlreadyExists(WorldLevel level) {

        TreeTableView<EditorAttribute> propertiesView = FXPropertiesView.getPropertiesView();

        if (propertiesView.getEditingCell() == null
                || propertiesView.getFocusModel().focusedIndexProperty().get() == -1) {
            propertiesView.edit(-1, null);
        }

        updateOldAttributes(level);

    }


    private static void manageSelection(MouseEvent event, WorldLevel level) {

        DragSettings dragSettings = tryToSelectSomething(event, level);
        if (dragSettings == DragSettings.NULL) {
            SelectionManager.setSelected(null);
            return;
        }

        if (level.getSelected() != null &&
                level.getSelected().containsObjectPosition(dragSettings.getObjectComponent())) {

            if (!dragSettings.getObjectComponent().isDraggable()) return;

            if (dragSettings.getType() == DragSettings.MOVE) FXScene.getScene().setCursor(Cursor.MOVE);
            SelectionManager.setDragSettings(dragSettings);

        } else {

            EditorObject selected = getEditorObjectThatHasThis(dragSettings.getObjectComponent(), level);
            if (selected == null) return;

            SelectionManager.setSelected(selected);
            FXPropertiesView.changeTableView(selected);
            if (selected.getParent() != null) selected.getParent().getTreeItem().setExpanded(true);
            FXHierarchy.getHierarchy().getSelectionModel().select(selected.getTreeItem());
            FXHierarchy.getHierarchy().scrollTo(FXHierarchy.getHierarchy().getRow(selected.getTreeItem()));

        }

    }


    private static void tryToPlaceStrand(MouseEvent event, WorldLevel level) {

        double mouseX = (event.getX() - level.getOffsetX()) / level.getZoom();
        double mouseY = (event.getY() - FXCanvas.getMouseYOffset() - level.getOffsetY()) / level.getZoom();

        for (EditorObject editorObject : level.getLevel()) if (editorObject instanceof BallInstance ballInstance) {
            for (ObjectComponent objectComponent : ballInstance.getObjectComponents()) {
                if (objectComponent.mouseIntersection(mouseX, mouseY) != DragSettings.NULL) {
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


    public static DragSettings tryToSelectSomething(MouseEvent event, WorldLevel level) {

        SplitPane splitPane = FXContainers.getSplitPane();
        double editorViewWidth = splitPane.getDividerPositions()[0] * splitPane.getWidth();

        if (event.getX() > editorViewWidth || event.getY() < FXCanvas.getMouseYOffset()) return DragSettings.NULL;

        double mouseX = (event.getX() - level.getOffsetX()) / level.getZoom();
        double mouseY = (event.getY() - FXCanvas.getMouseYOffset() - level.getOffsetY()) / level.getZoom();

        EditorObject selected = level.getSelected();
        if (selected != null) for (ObjectComponent objectComponent : selected.getObjectComponents()) {
            if (!objectComponent.isVisible()) continue;
            if (!objectComponent.isSelectable()) continue;
            DragSettings dragSettings = objectComponent.mouseIntersectingCorners(mouseX, mouseY);
            if (dragSettings != DragSettings.NULL) return dragSettings;
        }

        ArrayList<ObjectComponent> byDepth = Renderer.orderObjectPositionsByDepth(level);
        for (int i = byDepth.size() - 1; i >= 0; i--) {
            ObjectComponent object = byDepth.get(i);
            if (!object.isVisible()) continue;
            if (!object.isSelectable()) continue;
            DragSettings dragSettings = object.mouseIntersection(mouseX, mouseY);
            if (dragSettings != DragSettings.NULL) return dragSettings;
        }

        return DragSettings.NULL;

    }

}
