package com.woogleFX.engine;

import com.woogleFX.assets.Asset;
import com.woogleFX.assets.GameVersion;
import com.woogleFX.editorObjects.EditorObject;
import com.woogleFX.engine.fx.*;
import com.woogleFX.engine.fx.editorButtons.FXEditorButtons;
import com.woogleFX.engine.fx.hierarchy.FXHierarchySwitcherButtons;
import com.woogleFX.engine.fx.menu.FXMenu;
import com.woogleFX.engine.fx.propertiesView.FXPropertiesView;
import com.woogleFX.engine.renderer.Renderer;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.TabPane;
import javafx.scene.transform.Affine;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Keeps track of the current asset. */
public class AssetManager {

    /** The current asset. */
    private static Asset asset = null;
    public static Asset getAsset() {
        return asset;
    }
    public static void setAsset(Asset asset) {
        AssetManager.asset = asset;
        FXEditorButtons.updateAllButtons();
        FXMenu.updateAllButtons();

        if (asset != null) {

            // Transform the canvas according to the updated translation and scale.
            Renderer.t = new Affine();
            Renderer.t.appendTranslation(asset.getOffsetX(), asset.getOffsetY());
            Renderer.t.appendScale(asset.getZoom(), asset.getZoom());

        }

        onSetAsset(asset);
        Renderer.draw();
    }


    public static void onSetAsset(Asset asset) {

        FXEditorButtons.refreshToolbars();
        if (asset != null) {
            int i = 0;
            for (Node node : asset.getGUIElements()) {
                FXContainers.getvBox().getChildren().add(2 + i, node);
                i++;
            }
        }
        FXEditorButtons.fillShowHideToolbar(FXEditorButtons.getFunctionsToolbar());

        if (asset == null) {
            FXStage.getStage().setTitle("World of Goo Everything Editor");
            return;
        }

        asset.onSet();

        TabPane hierarchySwitcherButtons = FXHierarchySwitcherButtons.getHierarchySwitcherButtons();
        hierarchySwitcherButtons.getTabs().clear();

        hierarchySwitcherButtons.getTabs().addAll(asset.getTabs());
        hierarchySwitcherButtons.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        hierarchySwitcherButtons.setMinHeight(30);
        hierarchySwitcherButtons.setMaxHeight(30);
        hierarchySwitcherButtons.setPrefHeight(30);
        hierarchySwitcherButtons.setPadding(new Insets(-6, -6, -6, -6));

        hierarchySwitcherButtons.getSelectionModel().selectedItemProperty().addListener((observableValue, tab, t1) -> asset.onSetTab(t1));

        String levelName = asset.getName() + " (" + asset.getClass().getSimpleName().substring(4) + ", version " + asset.getVersion() + ")";
        FXStage.getStage().setTitle(levelName + " — World of Goo Everything Editor (source: \"" + asset.getFile() + "\")");

        if (asset.getSelectedComponents().length == 0) FXPropertiesView.changeTableView(new EditorObject[]{});
        else FXPropertiesView.changeTableView(asset.getSelectedObjects());

        SelectionManager.goToSelectedInHierarchy();

        if (asset.getFile() == null) {
            System.err.println("Asset file is null: [" + levelName + "]");
        }
        addRecentlyOpenedAsset(new AssetDescription(asset.getClass(), asset.getFile(), asset.getName(), asset.getVersion()));

    }


    public static int getVisibility(String key) {
        return asset.getVisibilitySettings().getVisibilityStatus(key);
    }

    public record AssetDescription(Class<? extends Asset> type, File file, String name, GameVersion version) {

    }

    private static final List<AssetDescription> recentlyOpenedAssets = new ArrayList<>();
    public static List<AssetDescription> getRecentlyOpenedAssets() {
        return Collections.unmodifiableList(recentlyOpenedAssets);
    }
    public static void addRecentlyOpenedAsset(AssetDescription asset) {
        recentlyOpenedAssets.removeIf(e -> e.file != null && e.file.equals(asset.file));
        recentlyOpenedAssets.add(0, asset);
    }

}
