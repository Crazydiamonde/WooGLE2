package com.woogleFX.engine.fx.assetSelectPane;

import com.woogleFX.assets.Asset;
import com.woogleFX.editorObjects.EditorObject;
import com.woogleFX.engine.AssetManager;
import com.woogleFX.engine.fx.AssetTab;
import com.woogleFX.engine.fx.editorButtons.FXEditorButtons;
import com.woogleFX.engine.fx.propertiesView.FXPropertiesView;
import com.woogleFX.engine.fx.hierarchy.FXHierarchy;
import com.woogleFX.engine.fx.menu.FXMenu;
import com.woogleFX.engine.gui.alarms.CloseTabAlarm;
import javafx.event.Event;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

public class FXAssetSelectPane {

    private static final TabPane assetSelectPane = new TabPane();
    public static TabPane getAssetSelectPane() {
        return assetSelectPane;
    }


    public static AssetTab createAssetTab(Asset asset) {

        // Instantiate the tab.
        AssetTab tab = new AssetTab(asset.getName(), asset);

        // Override the default close operation of the tab.
        tab.setOnCloseRequest(event -> {
            event.consume();
            // If the asset has unsaved changes:
            if (asset.getEditingStatus() == AssetTab.UNSAVED_CHANGES) {
                // Show a dialogue asking the user if they want to close the asset without
                // saving changes first.
                CloseTabAlarm.show(tab, asset);
            } else {
                // Close the tab.
                if (tab.getTabPane().getTabs().size() == 1) {
                    FXAssetSelectPane.getAssetSelectPane().setMinHeight(0);
                    FXAssetSelectPane.getAssetSelectPane().setMaxHeight(0);
                    // If all tabs are closed, clear the side pane
                    FXHierarchy.getHierarchy().setRoot(null);
                    // Clear the properties pane too
                    FXPropertiesView.changeTableView(new EditorObject[]{});
                }
                tab.getTabPane().getTabs().remove(tab);
            }
        });

        tab.selectedProperty().addListener((observableValue, aBoolean, t1) -> {
            // If the user has just selected this tab:
            if (!t1) {
                // Destroy and replace the asset tab to prevent an unknown freezing issue.
                // TODO: What's up with that?
                if (asset.getAssetTab() != null && asset.getAssetTab().getTabPane() != null
                        && asset.getAssetTab().getTabPane().getTabs().contains(asset.getAssetTab())
                        && !asset.getAssetTab().getTabPane().getTabs().isEmpty()) {
                    asset.setEditingStatus(asset.getEditingStatus(), false);
                }

            }
        });

        MenuItem closeTab = new MenuItem("Close");
        closeTab.setOnAction(actionEvent -> tab.getOnCloseRequest().handle(new Event(AssetTab.CLOSED_EVENT)));
        MenuItem closeOtherTabs = new MenuItem("Close Other Tabs");
        closeOtherTabs.setOnAction(actionEvent -> {
            for (Tab tab2 : assetSelectPane.getTabs().toArray(Tab[]::new)) {
                if (tab2 == tab) continue;
                tab2.getOnCloseRequest().handle(new Event(AssetTab.CLOSED_EVENT));
            }
        });
        MenuItem closeAllTabs = new MenuItem("Close All Tabs");
        closeAllTabs.setOnAction(actionEvent -> {
            for (Tab tab2 : assetSelectPane.getTabs().toArray(Tab[]::new)) {
                tab2.getOnCloseRequest().handle(new Event(AssetTab.CLOSED_EVENT));
            }
        });
        MenuItem closeTabsToTheRight = new MenuItem("Close Tabs to the Right");
        closeTabsToTheRight.setOnAction(actionEvent -> {
            for (Tab tab2 : assetSelectPane.getTabs().toArray(Tab[]::new)) {
                if (assetSelectPane.getTabs().indexOf(tab2) <= assetSelectPane.getTabs().indexOf(tab)) continue;
                tab2.getOnCloseRequest().handle(new Event(AssetTab.CLOSED_EVENT));
            }
        });

        tab.setContextMenu(new ContextMenu(closeTab, closeOtherTabs, closeAllTabs, closeTabsToTheRight));

        return tab;
    }


    public static void closeCurrentTab() {
        if (assetSelectPane.getTabs().size() == 1) {
            assetSelectPane.setMinHeight(0);
            assetSelectPane.setMaxHeight(0);
            // If all tabs are closed, clear the side pane
            FXHierarchy.getHierarchy().setRoot(null);
            // Clear the properties pane too
            FXPropertiesView.changeTableView(new EditorObject[]{});
        }
        assetSelectPane.getTabs().remove(assetSelectPane.getSelectionModel().getSelectedItem());

    }


    public static void init() {

        assetSelectPane.getSelectionModel().selectedItemProperty().addListener((observableValue, tab, t1) -> {
            AssetManager.setAsset(t1 instanceof AssetTab assetTab ? assetTab.getAsset() : null);
            FXEditorButtons.updateAllButtons();
            FXMenu.updateAllButtons();
        });

        assetSelectPane.widthProperty().addListener((observableValue, number, t1) -> {
            int numTabs = assetSelectPane.getTabs().size();
            double tabSize = 1 / (numTabs + 1.0);
            assetSelectPane.setTabMaxWidth(tabSize * (assetSelectPane.getWidth() - 15) - 15);
            assetSelectPane.setTabMinWidth(tabSize * (assetSelectPane.getWidth() - 15) - 15);
        });

        assetSelectPane.setTabClosingPolicy(TabPane.TabClosingPolicy.ALL_TABS);

        assetSelectPane.setTabDragPolicy(TabPane.TabDragPolicy.REORDER);
        assetSelectPane.setStyle("-fx-open-tab-animation: NONE");

    }

}
