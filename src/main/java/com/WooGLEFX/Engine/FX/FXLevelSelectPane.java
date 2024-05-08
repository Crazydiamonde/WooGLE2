package com.WooGLEFX.Engine.FX;

import com.WooGLEFX.Engine.Main;
import com.WooGLEFX.Functions.LevelManager;
import com.WooGLEFX.GUI.Alarms;
import com.WooGLEFX.Structures.SimpleStructures.LevelTab;
import com.WooGLEFX.Structures.WorldLevel;
import javafx.scene.control.TabPane;

public class FXLevelSelectPane {

    private static TabPane levelSelectPane;
    public static TabPane getLevelSelectPane() {
        return levelSelectPane;
    }
    public static void setLevelSelectPane(TabPane _levelSelectPane) {
        levelSelectPane = _levelSelectPane;
    }


    public static LevelTab levelSelectButton(WorldLevel level) {

        // Instantiate the tab.
        LevelTab tab = new LevelTab(level.getLevelName(), level);

        // Override the default close operation of the tab.
        tab.setOnCloseRequest(event -> {
            event.consume();
            // If the level has unsaved changes:
            if (level.getEditingStatus() == WorldLevel.UNSAVED_CHANGES) {
                // Show a dialogue asking the user if they want to close the level without
                // saving changes first.
                Alarms.closeTabMessage(tab, level);
            } else {
                // Close the tab.
                if (tab.getTabPane().getTabs().size() == 1) {
                    FXLevelSelectPane.getLevelSelectPane().setMinHeight(0);
                    FXLevelSelectPane.getLevelSelectPane().setMaxHeight(0);
                    // If all tabs are closed, clear the side pane
                    FXHierarchy.getHierarchy().setRoot(null);
                    // Clear the properties pane too
                    FXPropertiesView.changeTableView(null);
                }
                tab.getTabPane().getTabs().remove(tab);
            }
        });

        tab.selectedProperty().addListener((observableValue, aBoolean, t1) -> {
            // If the user has just selected this tab:
            if (t1) {
                // Set this tab's level to be the currently selected level.
                LevelManager.setLevel(level);
                switch (level.getCurrentlySelectedSection()) {
                    case "Scene" -> FXHierarchy.getHierarchy().setRoot(level.getSceneObject().getTreeItem());
                    case "Level" -> FXHierarchy.getHierarchy().setRoot(level.getLevelObject().getTreeItem());
                    case "Resrc" -> FXHierarchy.getHierarchy().setRoot(level.getResourcesObject().getTreeItem());
                    case "Text" -> FXHierarchy.getHierarchy().setRoot(level.getTextObject().getTreeItem());
                    case "Addin" -> FXHierarchy.getHierarchy().setRoot(level.getAddinObject().getTreeItem());
                }

                switch (level.getCurrentlySelectedSection()) {
                    case "Scene" -> FXHierarchy.getHierarchySwitcherButtons().getSelectionModel().select(0);
                    case "Level" -> FXHierarchy.getHierarchySwitcherButtons().getSelectionModel().select(1);
                    case "Resrc" -> FXHierarchy.getHierarchySwitcherButtons().getSelectionModel().select(2);
                    case "Text" -> FXHierarchy.getHierarchySwitcherButtons().getSelectionModel().select(3);
                    case "Addin" -> FXHierarchy.getHierarchySwitcherButtons().getSelectionModel().select(4);
                }

            } else {
                // Destroy and replace the level tab to prevent an unknown freezing issue.
                if (level.getLevelTab() != null && level.getLevelTab().getTabPane() != null
                        && level.getLevelTab().getTabPane().getTabs().contains(level.getLevelTab())
                        && level.getLevelTab().getTabPane().getTabs().size() != 0) {
                    level.setEditingStatus(level.getEditingStatus(), false);
                }
            }
        });

        return tab;
    }



}
