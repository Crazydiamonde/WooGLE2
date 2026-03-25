package com.woogleFX.engine;

import com.woogleFX.editorObjects.EditorObject;
import com.woogleFX.editorObjects.attributes.EditorAttribute;
import com.woogleFX.editorObjects.objectComponents.ObjectComponent;
import com.woogleFX.engine.fx.propertiesView.FXPropertiesView;
import com.woogleFX.engine.fx.editorButtons.FXEditorButtons_Edit;
import com.woogleFX.engine.fx.hierarchy.FXHierarchy;
import com.woogleFX.editorObjects.DragSettings;
import com.woogleFX.engine.fx.hierarchy.FXHierarchySwitcherButtons;
import com.woogleFX.assets.GameVersion;

public class SelectionManager {

    private static double mouseStartX = 0;
    public static double getMouseStartX() {
        return mouseStartX;
    }
    public static void setMouseStartX(double mouseStartX) {
        SelectionManager.mouseStartX = mouseStartX;
    }


    private static double mouseStartY = 0;
    public static double getMouseStartY() {
        return mouseStartY;
    }
    public static void setMouseStartY(double mouseStartY) {
        SelectionManager.mouseStartY = mouseStartY;
    }


    private static double mouseX = 0;
    public static double getMouseX() {
        return mouseX;
    }
    public static void setMouseX(double mouseX) {
        SelectionManager.mouseX = mouseX;
    }


    private static double mouseY = 0;
    public static double getMouseY() {
        return mouseY;
    }
    public static void setMouseY(double mouseY) {
        SelectionManager.mouseY = mouseY;
    }


    private static EditorAttribute[][] oldAttributes;
    public static EditorAttribute[][] getOldAttributes() {
        return oldAttributes;
    }
    public static void setOldAttributes(EditorAttribute[][] _oldAttributes) {
        oldAttributes = _oldAttributes;
    }


    private static ObjectComponent[] oldSelected;
    public static ObjectComponent[] getOldSelected() {
        return oldSelected;
    }
    public static void setOldSelected(ObjectComponent[] oldSelected) {
        SelectionManager.oldSelected = oldSelected;
    }


    private static DragSettings dragSettings = DragSettings.NULL;
    public static DragSettings getDragSettings() {
        return dragSettings;
    }
    public static void setDragSettings(DragSettings dragSettings) {
        SelectionManager.dragSettings = dragSettings;
    }


    public static final int SELECTION = 0;
    public static final int STRAND = 1;
    public static final int GEOMETRY = 2;

    private static int mode = SELECTION;
    public static int getMode() {
        return mode;
    }

    public static void selectionMode() {
        mode = SELECTION;
        // Highlight selection button blue
        FXEditorButtons_Edit.buttonSelectMoveAndResize.setStyle("-fx-background-color: #9999ff;");
        // Un-highlight strand button
        FXEditorButtons_Edit.buttonStrandMode.setStyle("");
        // Un-highlight geometry button
        FXEditorButtons_Edit.buttonGeometryMode.setStyle("");
    }

    public static void strandMode() {
        mode = STRAND;
        // Highlight strand button blue
        FXEditorButtons_Edit.buttonStrandMode.setStyle("-fx-background-color: #9999ff;");
        // Un-highlight selection button
        FXEditorButtons_Edit.buttonSelectMoveAndResize.setStyle("");
        // Un-highlight geometry button
        FXEditorButtons_Edit.buttonGeometryMode.setStyle("");
    }

    public static void geometryMode() {
        mode = GEOMETRY;
        // Highlight geometry button blue
        FXEditorButtons_Edit.buttonGeometryMode.setStyle("-fx-background-color: #9999ff;");
        // Un-highlight selection button
        FXEditorButtons_Edit.buttonSelectMoveAndResize.setStyle("");
        // Un-highlight strand button
        FXEditorButtons_Edit.buttonStrandMode.setStyle("");
    }


    private static EditorObject strand1Gooball;
    public static EditorObject getStrand1Gooball() {
        return strand1Gooball;
    }
    public static void setStrand1Gooball(EditorObject strand1Gooball) {
        SelectionManager.strand1Gooball = strand1Gooball;
    }

    public static void goToSelectedInHierarchy() {

        EditorObject[] selectedArray = AssetManager.getAsset().getSelectedObjects();

        if (selectedArray.length == 0 || selectedArray[0] == null) {
            FXHierarchy.getHierarchy().getSelectionModel().clearSelection();
            return;
        }

        EditorObject selected = selectedArray[0];

        EditorObject absoluteParent = selected;
        if (selected.getVersion() != GameVersion.VERSION_WOG2)
            while (absoluteParent.getParent() != null) absoluteParent = absoluteParent.getParent();

        FXHierarchySwitcherButtons.getHierarchySwitcherButtons().getSelectionModel().select(AssetManager.getAsset().getTabForObject(selected));
        FXHierarchy.getHierarchy().getSelectionModel().clearSelection();
        FXPropertiesView.changeTableView(selectedArray);
        FXHierarchy.scrollTo(selected);
        int[] indices = new int[selectedArray.length - 1];
        for (int i = 0; i < selectedArray.length - 1; i++)
            indices[i] = FXHierarchy.getHierarchy().getRow(selectedArray[i + 1].getTreeItem());
        FXHierarchy.getHierarchy().getSelectionModel().selectIndices(FXHierarchy.getHierarchy().getRow(selected.getTreeItem()), indices);
        FXHierarchy.getHierarchy().refresh();

    }

}
