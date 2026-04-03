package com.woogleFX.assets;

import com.woogleFX.editorObjects.EditorObject;
import com.woogleFX.editorObjects.objectCreators.ObjectCreator;
import com.woogleFX.engine.fx.AssetTab;
import com.woogleFX.engine.gui.alarms.AskForLevelNameAlarm;
import com.woogleFX.engine.gui.alarms.ErrorAlarm;
import com.woogleFX.engine.gui.alarms.LevelIssuesAlarm;
import com.woogleFX.engine.fx.assetSelectPane.FXAssetSelectPane;
import com.woogleFX.file.FileManager;
import com.woogleFX.file.fileExport.XMLUtility;
import com.woogleFX.file.fileImport.EditorObjectXMLReader;
import com.worldOfGoo.addin.addins;
import com.worldOfGoo.resrc.Resources;
import com.worldOfGoo.resrc.Image;
import com.worldOfGoo.resrc.Sound;
import javafx.scene.control.Tab;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.util.List;

public class AssetUpdater {

    private static final Logger logger = LoggerFactory.getLogger(AssetLoader.class);


    public static void saveAsset(Asset asset) {
        try {
            if (!saveSpecificAsset(asset, asset.getFile())) return;
        } catch (IOException e) {
            ErrorAlarm.show(e);
            return;
        }
        asset.setLastSavedUndoPosition(asset.undoActions.size());
        if (asset.getEditingStatus() != AssetTab.NO_UNSAVED_CHANGES)
            asset.setEditingStatus(AssetTab.NO_UNSAVED_CHANGES, true);
    }


    public static void saveAssetAs(Asset asset) {
        // TODO: select file to save to
        File file = asset.getFile();
        try {
            if (!saveSpecificAsset(asset, file)) return;
        } catch (IOException e) {
            ErrorAlarm.show(e);
            return;
        }
        asset.setLastSavedUndoPosition(asset.undoActions.size());
        if (asset.getEditingStatus() != AssetTab.NO_UNSAVED_CHANGES)
            asset.setEditingStatus(AssetTab.NO_UNSAVED_CHANGES, true);
    }


    public static boolean saveSpecificAsset(Asset asset, File file) throws IOException {

        // Make sure it's not unmodifiable
        if (asset.getEditingStatus() == AssetTab.UNSAVED_CHANGES_UNMODIFIABLE) return false;

        boolean okayToSave = true;

        List<AssetError> errors = asset.verify();

        // Check for errors in level objects
        errors.addAll(AssetVerifier.verifyAllObjects(asset.getObjects()));

        if (!errors.isEmpty()) {
            if (LevelIssuesAlarm.show(errors)) return false;
        }

        if (!okayToSave) return false;

        if (!asset.save(file)) {
            return false;
        }

        saveAddinData(asset);
        return true;

    }


    public static void saveAll() {
        int selectedIndex = FXAssetSelectPane.getAssetSelectPane().getSelectionModel().getSelectedIndex();
        for (Tab tab : FXAssetSelectPane.getAssetSelectPane().getTabs().toArray(new Tab[0])) {
            AssetTab assetTab = (AssetTab) tab;
            if (assetTab.getAsset().getEditingStatus() == AssetTab.UNSAVED_CHANGES) {
                try {
                    if (saveSpecificAsset(assetTab.getAsset(), assetTab.getAsset().getFile())) {
                        assetTab.getAsset().setEditingStatus(AssetTab.NO_UNSAVED_CHANGES, false);
                    }
                } catch (IOException e) {
                    ErrorAlarm.show(e);
                }
            }
        }
        FXAssetSelectPane.getAssetSelectPane().getSelectionModel().select(selectedIndex);
    }

    public static void renameLevel(Asset level) {
        if (level != null) {
            AskForLevelNameAlarm.show("changeName", level.getVersion());
        }
    }

    public static void renameLevel(Asset level, String text) throws IOException {

        logger.info("Renaming " + level.getName() + " to " + text);

        String start = FileManager.getGameDir(level.getVersion());

        /* Change level name in directory */
        File originalLevelDirectory = new File(start + "/res/levels/" + level.getName());
        File levelDirectory = new File(start + "/res/levels/" + text);
        if (!originalLevelDirectory.renameTo(levelDirectory)) {
            ErrorAlarm.show("Could not rename level! (" + level.getName() + " to " + text + ")");
            return;
        }

        /* Change the names of the scene, level, resrc, addin, text files */
        File[] levelParts = levelDirectory.listFiles();
        if (levelParts == null) return;

        for (File levelPart : levelParts) {
            if (levelPart.getName().length() >= level.getName().length()
                    && levelPart.getName().startsWith(level.getName())) {
                if (!levelPart.renameTo(new File(start + "/res/levels/" + text + "/" + text
                        + levelPart.getName().substring(level.getName().length())))) {
                    ErrorAlarm.show("Could not rename level! (" + level.getName() + " to " + text + ")");
                    return;
                }
            }
        }

        /* Edit every resource */
        for (EditorObject resource : level.getResources().getChildren()) {

            if (resource instanceof Resources) {

                resource.setAttribute("id", "scene_" + text);

            } else if (resource instanceof Image || resource instanceof Sound) {

                String previousID = resource.getAttribute("id").stringValue();
                String newID = previousID.replaceAll(level.getName().toUpperCase(), text.toUpperCase());
                resource.setAttribute("id", newID);

                String previousPath = resource.getAttribute("path").stringValue();
                String newPath = previousPath.replaceAll(level.getName(), text);
                resource.setAttribute("path", newPath);

            }

        }

        level.setName(text);
        level.setEditingStatus(level.getEditingStatus(), true);

        saveAsset(level);

    }


    public static void saveAddinData(Asset asset) throws IOException {

        File addinDataFile = new File(FileManager.getEditorLocation() + "/addin.xml");

        addins addins;
        if (!addinDataFile.exists()) {
            try {
                Files.createFile(addinDataFile.toPath());
            } catch (IOException e) {
                ErrorAlarm.show(e);
                return;
            }
            addins = ObjectCreator.create(com.worldOfGoo.addin.addins.class, null, null);
        } else {
            addins = EditorObjectXMLReader.readEditorObject(asset.getVersion(), addinDataFile, addins.class);
        }
        boolean wasThereAlready = false;
        for (EditorObject addin : addins.getChildren()) {
            if (addin.getChildren().get(0).getAttribute("value").stringValue().equals(asset.getAddin().getChildren().get(0).getAttribute("value").stringValue())) {
                wasThereAlready = true;
                addins.getChildren().add(addins.getChildren().indexOf(addin), asset.getAddin());
                addins.getChildren().remove(addin);
            }
        }
        if (!wasThereAlready) {
            addins.getChildren().add(asset.getAddin());
        }
        try {
            StringBuilder output = new StringBuilder();
            XMLUtility.recursiveXMLExport(output, addins, 0);
            Files.writeString(addinDataFile.toPath(), output.toString());
        } catch (IOException e) {
            ErrorAlarm.show(e);
        }

    }


    public static void deleteLevel(Asset level) {
        if (level == null) return;
        AskForLevelNameAlarm.show("delete", level.getVersion());
    }

    public static void nuke(File file) throws IOException {
        if (file.isDirectory()) {
            File[] children = file.listFiles();
            if (children != null) for (File child : children) {
                nuke(child);
            }
        }
        Files.delete(file.toPath());
    }

}
