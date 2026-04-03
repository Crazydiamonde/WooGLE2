package com.woogleFX.assets;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.woogleFX.editorObjects.EditorObject;
import com.woogleFX.editorObjects.objectCreators.BlankObjectGenerator;
import com.woogleFX.engine.fx.*;
import com.woogleFX.engine.fx.assetSelectPane.FXAssetSelectPane;
import com.woogleFX.engine.fx.editorButtons.FXEditorButtons;
import com.woogleFX.engine.fx.menu.FXMenu;
import com.woogleFX.engine.gui.AssetSelector;
import com.woogleFX.engine.gui.alarms.AskForLevelNameAlarm;
import com.woogleFX.engine.gui.alarms.ErrorAlarm;
import com.woogleFX.engine.gui.alarms.LoadingResourcesAlarm;
import com.woogleFX.engine.AssetManager;
import com.woogleFX.file.FileManager;
import com.worldOfGoo.addin.addin;
import com.worldOfGoo.addin.addins;
import javafx.scene.control.Tab;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;

public class AssetLoader {

    private static final Logger logger = LoggerFactory.getLogger(AssetLoader.class);

    public static final ArrayList<String> failedResources = new ArrayList<>();


    public static void openAsset(AssetSelector<?> assetSelector, File file, String levelName, GameVersion version) throws IOException {

        // Don't open a level if none selected
        if (levelName == null || levelName.isEmpty()) return;

        // Don't open a level if it's already open
        for (Tab tab : FXAssetSelectPane.getAssetSelectPane().getTabs()) {
            if (tab.getText() != null && tab.getText().equals(levelName) && ((AssetTab)tab).getAsset().getVersion() == version) {
                FXAssetSelectPane.getAssetSelectPane().getSelectionModel().select(tab);
                return;
            }
        }

        failedResources.clear();

        System.out.println("Performance check 1.");

        Asset level = assetSelector.openInstance(file, levelName, version);
        if (level == null) return;

        System.out.println("Performance check 2.");

        FXEditorButtons.updateAllButtons();
        FXMenu.updateAllButtons();

        FXAssetSelectPane.getAssetSelectPane().setMinHeight(30);
        FXAssetSelectPane.getAssetSelectPane().setMaxHeight(30);

        AssetManager.setAsset(level);

        level.load();

        if (!failedResources.isEmpty()) {
            StringBuilder fullError = new StringBuilder();
            for (String resource : failedResources) {
                fullError.append("\n").append(resource);
            }
            LoadingResourcesAlarm.show(fullError.substring(1));
        }

        finishOpeningAsset(level);

    }


    public static addin getAddinInfo(Asset asset) {

        addin blankAddin = BlankObjectGenerator.generateBlankAddinObject(asset);

        File addinDataFile = new File(FileManager.getEditorLocation() + "/addin.xml");
        if (!addinDataFile.exists()) return blankAddin;

        addins addins;
        try {
            addins = new XmlMapper().readValue(Files.readString(addinDataFile.toPath()), addins.class);
        } catch (IOException e) {
            return blankAddin;
        }

        for (EditorObject addinManifest : addins.getChildren()) {
            if (addinManifest.getAttribute("type").stringValue().equals(asset.getClass().getName())
                && addinManifest.getAttribute("version").stringValue().equals(asset.getVersion().toString())) {
                return (addin)addinManifest.getChildren().get(0);
            }
        }

        return blankAddin;

    }


    public static void cloneLevel(String name) throws IOException {
        FXAssetSelectPane.getAssetSelectPane().setMinHeight(30);
        FXAssetSelectPane.getAssetSelectPane().setMaxHeight(30);

        Asset _level = AssetManager.getAsset().clone(name);

        AssetUpdater.saveAsset(_level);

        finishOpeningAsset(_level);

    }


    public static void cloneLevel() {
        GameVersion version = AssetManager.getAsset().getVersion();
        AskForLevelNameAlarm.show("clone", version);
    }


    public static void finishOpeningAsset(Asset asset) {

        AssetTab assetSelectButton = FXAssetSelectPane.createAssetTab(asset);
        FXAssetSelectPane.getAssetSelectPane().getTabs().add(assetSelectButton);

        int numTabs = FXAssetSelectPane.getAssetSelectPane().getTabs().size();
        double tabSize = 1 / (numTabs + 1.0);
        double tabWidth = tabSize * (FXAssetSelectPane.getAssetSelectPane().getWidth() - 15) - 15;
        FXAssetSelectPane.getAssetSelectPane().setTabMaxWidth(tabWidth);
        FXAssetSelectPane.getAssetSelectPane().setTabMinWidth(tabWidth);

        asset.resetCamera();

        asset.setAssetTab(assetSelectButton);
        asset.setEditingStatus(AssetTab.NO_UNSAVED_CHANGES, true);
        FXAssetSelectPane.getAssetSelectPane().getSelectionModel().select(assetSelectButton);
        AssetManager.onSetAsset(asset);

    }


}
