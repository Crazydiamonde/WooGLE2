package com.woogleFX.engine.inputEvents;

import com.woogleFX.editorObjects.objectComponents.ObjectComponent;
import com.woogleFX.editorObjects.splineGeom.SplineGeometryPlacer;
import com.woogleFX.editorObjects.splineGeom.SplineManager;
import com.woogleFX.engine.fx.*;
import com.woogleFX.engine.fx.hierarchy.FXHierarchy;
import com.woogleFX.engine.renderer.Renderer;
import com.woogleFX.engine.SelectionManager;
import com.woogleFX.engine.LevelManager;
import com.woogleFX.editorObjects.attributes.EditorAttribute;
import com.woogleFX.editorObjects.EditorObject;
import com.woogleFX.editorObjects.DragSettings;
import com.woogleFX.engine.undoHandling.UndoManager;
import com.woogleFX.engine.undoHandling.userActions.CreateSplinePointAction;
import com.woogleFX.gameData.level._Level;
import com.worldOfGoo.level.BallInstance;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TreeTableView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

import java.awt.geom.QuadCurve2D;
import java.util.ArrayList;
import java.util.List;

public class MousePressedManager {

    private static EditorObject getEditorObjectThatHasThis(ObjectComponent objectComponent, _Level level) {

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

        _Level level = LevelManager.getLevel();
        if (level == null) return;

        if (level.getSelected().length != 0) ifSelectedAlreadyExists(level);

        if (event.getY() < FXCanvas.getMouseYOffset()) return;

        if (SelectionManager.getMode() == SelectionManager.SELECTION) manageSelection(event, level);
        else if (SelectionManager.getMode() == SelectionManager.STRAND) tryToPlaceStrand(event, level);
        else if (SelectionManager.getMode() == SelectionManager.GEOMETRY) manageSplinePlacement(event, level);

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


    private static void ifSelectedAlreadyExists(_Level level) {

        TreeTableView<EditorAttribute> propertiesView = FXPropertiesView.getPropertiesView();

        if (propertiesView.getEditingCell() == null
                || propertiesView.getFocusModel().focusedIndexProperty().get() == -1) {
            propertiesView.edit(-1, null);
        }

        updateOldAttributes(level);

    }


    private static void manageSelection(MouseEvent event, _Level level) {

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


    private static void tryToPlaceStrand(MouseEvent event, _Level level) {

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


    private static void updateOldAttributes(_Level level) {

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


    public static DragSettings tryToSelectSomething(MouseEvent event, _Level level) {

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


    private static void manageSplinePlacement(MouseEvent event, _Level level) {

        double mouseX = (event.getX() - level.getOffsetX()) / level.getZoom();
        double mouseY = (event.getY() - FXCanvas.getMouseYOffset() - level.getOffsetY()) / level.getZoom();

        for (int i = 0; i < SplineManager.getPointCount(); i++) {
            Point2D splinePoint = SplineManager.getSplinePoint(i);
            if (mouseIntersection(mouseX, mouseY, splinePoint.getX(), splinePoint.getY())) {
                if (i % 3 != 0) {
                    int otherI = (i % 3 == 1) ? i - 1 : i + 1;
                    Point2D sp2 = SplineManager.getSplinePoint(otherI);
                    if (splinePoint.getX() == sp2.getX() && splinePoint.getY() == sp2.getY()) {
                        SplineManager.select(otherI, 0);
                        return;
                    }
                }
                SplineManager.select(i, 0);
                return;
            }
        }

        for (int i = 0; i < SplineManager.getQuadCurveCount(); i++) {
            QuadCurve2D splineSegment = SplineManager.getQuadCurve(i);
            double length = SplineGeometryPlacer.getSplineSegmentLength(splineSegment);
            for (int j = 0; j < length; j++) {
                double s = j / length;
                Point2D p = SplineGeometryPlacer.getPointOnSplineSegment(splineSegment, s);
                if (Math.hypot(p.getX() - mouseX, p.getY() - mouseY) < 4) {
                    SplineManager.select((i / 2) * 3 + 1, (s + i % 2) / 2);
                    return;
                }
            }
        }

        SplineManager.select(-1, -1);

        if (SplineManager.getPointCount() == 0) {
            UndoManager.registerChange(new CreateSplinePointAction(0, 0, 0, 0, mouseX, mouseY, 0));
            SplineManager.addPoint(0, 0, 0, 0, mouseX, mouseY, 0);
        } else {
            int i = SplineManager.getPointCount() + 2;
            Point2D p0 = SplineManager.getSplinePoint(SplineManager.getPointCount() - 1);
            UndoManager.registerChange(new CreateSplinePointAction(p0.getX(), p0.getY(), mouseX, mouseY, mouseX, mouseY, i));
            SplineManager.addPoint(p0.getX(), p0.getY(), mouseX, mouseY, mouseX, mouseY, i);
        }


    }


    private static boolean mouseIntersection(double mouseX, double mouseY, double x, double y) {
        double width = 6 / LevelManager.getLevel().getZoom();
        return Math.hypot(mouseX - x, mouseY - y) < width;
    }

}
