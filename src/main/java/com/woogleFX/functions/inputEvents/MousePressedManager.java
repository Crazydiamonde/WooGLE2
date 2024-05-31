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
        for (EditorObject editorObject : level.getResrc()) {
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

        if (level.getSelected().length != 0) ifSelectedAlreadyExists(level);

        if (event.getY() < FXCanvas.getMouseYOffset()) return;

        if (SelectionManager.getMode() == SelectionManager.SELECTION) manageSelection(event, level);
        else if (SelectionManager.getMode() == SelectionManager.STRAND) tryToPlaceStrand(event, level);

    }


    private static void secondaryMouseButton(MouseEvent event) {

        SplitPane splitPane = FXContainers.getSplitPane();
        double editorViewWidth = splitPane.getDividerPositions()[0] * splitPane.getWidth() - 6;

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

        SplitPane splitPane = FXContainers.getSplitPane();
        double editorViewWidth = splitPane.getDividerPositions()[0] * splitPane.getWidth() - 6;

        if (event.getX() > editorViewWidth || event.getY() < FXCanvas.getMouseYOffset()) return;

        DragSettings dragSettings = tryToSelectSomething(event, level);
        if (dragSettings == DragSettings.NULL) {
            level.clearSelection();
            return;
        }
        for (EditorObject selected : level.getSelected())

            if (selected.containsObjectPosition(dragSettings.getObjectComponent())) {

                if (!dragSettings.getObjectComponent().isDraggable()) return;

                if (dragSettings.getType() == DragSettings.MOVE) FXScene.getScene().setCursor(Cursor.MOVE);
                SelectionManager.setDragSettings(dragSettings);
                return;


        }

        EditorObject selectedObject = getEditorObjectThatHasThis(dragSettings.getObjectComponent(), level);
        if (selectedObject == null) return;

        EditorObject[] selectedList;
        if (event.isControlDown()) {
            selectedList = new EditorObject[level.getSelected().length + 1];
            System.arraycopy(level.getSelected(), 0, selectedList, 0, level.getSelected().length);
            selectedList[level.getSelected().length] = selectedObject;
        } else {
            selectedList = new EditorObject[]{selectedObject};
        }

        level.setSelected(selectedList);
        FXPropertiesView.changeTableView(selectedList);
        if (selectedObject.getParent() != null) selectedObject.getParent().getTreeItem().setExpanded(true);

        int[] indices = new int[selectedList.length - 1];
        for (int i = 0; i < indices.length; i++) indices[i] = FXHierarchy.getHierarchy().getRow(selectedList[i + 1].getTreeItem());

        FXHierarchy.getHierarchy().getSelectionModel().clearSelection();
        FXHierarchy.getHierarchy().getSelectionModel().selectIndices(FXHierarchy.getHierarchy().getRow(selectedList[0].getTreeItem()), indices);
        FXHierarchy.getHierarchy().scrollTo(FXHierarchy.getHierarchy().getRow(selectedObject.getTreeItem()));


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

        EditorAttribute[][] oldAttributes = new EditorAttribute[level.getSelected().length][];

        for (int i = 0; i < level.getSelected().length; i++) {

            ArrayList<EditorAttribute> output = new ArrayList<>();

            EditorObject selected = level.getSelected()[i];

            for (EditorAttribute attribute : selected.getAttributes()) {

                EditorAttribute newAttribute = new EditorAttribute(attribute.getName(), attribute.getType(), selected);

                if (!attribute.getDefaultValue().isEmpty()) newAttribute.setDefaultValue(attribute.getDefaultValue());
                newAttribute.setValue(attribute.stringValue());
                if (attribute.getRequiredInFile()) newAttribute.assertRequired();
                output.add(newAttribute);

            }

            oldAttributes[i] = output.toArray(new EditorAttribute[0]);

        }

        SelectionManager.setOldAttributes(oldAttributes);
        SelectionManager.setOldSelected(level.getSelected());

    }


    public static DragSettings tryToSelectSomething(MouseEvent event, WorldLevel level) {

        double mouseX = (event.getX() - level.getOffsetX()) / level.getZoom();
        double mouseY = (event.getY() - FXCanvas.getMouseYOffset() - level.getOffsetY()) / level.getZoom();

        EditorObject[] selectedList = level.getSelected();
        for (EditorObject selected : selectedList) for (ObjectComponent objectComponent : selected.getObjectComponents()) {
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
