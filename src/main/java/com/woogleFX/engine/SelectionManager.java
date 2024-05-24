package com.woogleFX.engine;

import com.woogleFX.engine.fx.FXContainers;
import com.woogleFX.engine.fx.FXEditorButtons;
import com.woogleFX.engine.fx.FXHierarchy;
import com.woogleFX.functions.LevelManager;
import com.woogleFX.editorObjects.EditorAttribute;
import com.woogleFX.editorObjects.EditorObject;
import com.woogleFX.structures.simpleStructures.DragSettings;
import javafx.scene.control.SplitPane;

public class SelectionManager {


    private static double mouseStartX;
    public static double getMouseStartX() {
        return mouseStartX;
    }
    public static void setMouseStartX(double mouseStartX) {
        SelectionManager.mouseStartX = mouseStartX;
    }


    private static double mouseStartY;
    public static double getMouseStartY() {
        return mouseStartY;
    }
    public static void setMouseStartY(double mouseStartY) {
        SelectionManager.mouseStartY = mouseStartY;
    }


    public static EditorObject getSelected() {
        return LevelManager.getLevel().getSelected();
    }
    public static void setSelected(EditorObject _selected) {
        LevelManager.getLevel().setSelected(_selected);
        goToSelectedInHierarchy();
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


    private static EditorAttribute[] oldAttributes;
    public static EditorAttribute[] getOldAttributes() {
        return oldAttributes;
    }
    public static void setOldAttributes(EditorAttribute[] _oldAttributes) {
        oldAttributes = _oldAttributes;
    }


    private static EditorObject oldSelected;
    public static EditorObject getOldSelected() {
        return oldSelected;
    }
    public static void setOldSelected(EditorObject oldSelected) {
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

    private static int mode = SELECTION;

    public static int getMode() {
        return mode;
    }

    public static void selectionMode() {
        mode = SELECTION;
        // Highlight selection button blue
        FXEditorButtons.buttonSelectMoveAndResize.setStyle("-fx-background-color: #9999ff;");
        // Un-highlight strand button
        FXEditorButtons.buttonStrandMode.setStyle("");
    }

    public static void strandMode() {
        mode = STRAND;
        // Highlight strand button
        FXEditorButtons.buttonStrandMode.setStyle("-fx-background-color: #9999ff;");
        // Un-highlight selection button
        FXEditorButtons.buttonSelectMoveAndResize.setStyle("");
    }

    private static EditorObject strand1Gooball;
    public static EditorObject getStrand1Gooball() {
        return strand1Gooball;
    }
    public static void setStrand1Gooball(EditorObject strand1Gooball) {
        SelectionManager.strand1Gooball = strand1Gooball;
    }

    public static void goToSelectedInHierarchy() {

        EditorObject selected = LevelManager.getLevel().getSelected();
        if (selected == null) {

            SplitPane splitPane = FXContainers.getSplitPane();
            double editorViewWidth = splitPane.getDividerPositions()[0] * splitPane.getWidth();

            if (SelectionManager.getMouseX() < editorViewWidth)
                FXHierarchy.getHierarchy().getSelectionModel().clearSelection();

            return;
        }

        EditorObject absoluteParent = LevelManager.getLevel().getSelected();
        while (absoluteParent.getParent() != null) absoluteParent = absoluteParent.getParent();

        if (LevelManager.getLevel().getSelected() != null && LevelManager.getLevel().getSelected().getParent() != null) {

            FXHierarchy.getHierarchy().setRoot(absoluteParent.getTreeItem());
            FXHierarchy.getHierarchy().setShowRoot(true);

            switch (LevelManager.getLevel().getSelected().getClass().getPackage().getName()) {
                case "com.WorldOfGoo.Scene" -> FXHierarchy.getHierarchySwitcherButtons().getSelectionModel().select(0);
                case "com.WorldOfGoo.Level" -> FXHierarchy.getHierarchySwitcherButtons().getSelectionModel().select(1);
                case "com.WorldOfGoo.Resrc" -> FXHierarchy.getHierarchySwitcherButtons().getSelectionModel().select(2);
                case "com.WorldOfGoo.Text" -> FXHierarchy.getHierarchySwitcherButtons().getSelectionModel().select(3);
                case "com.WorldOfGoo.Addin" -> FXHierarchy.getHierarchySwitcherButtons().getSelectionModel().select(4);
            }
        }
    }

}
