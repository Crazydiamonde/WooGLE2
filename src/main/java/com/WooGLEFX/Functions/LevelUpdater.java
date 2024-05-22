package com.WooGLEFX.Functions;

import com.WooGLEFX.EditorObjects._Ball;
import com.WooGLEFX.Engine.FX.FXLevelSelectPane;
import com.WooGLEFX.Engine.FX.FXStage;
import com.WooGLEFX.File.FileManager;
import com.WooGLEFX.File.fileexport.GoomodExporter;
import com.WooGLEFX.File.fileexport.LevelWriter;
import com.WooGLEFX.Engine.GUI.Alarms;
import com.WooGLEFX.EditorObjects.EditorObject;
import com.WooGLEFX.Structures.GameVersion;
import com.WooGLEFX.Structures.SimpleStructures.LevelTab;
import com.WooGLEFX.Structures.WorldLevel;
import com.WorldOfGoo.Level.BallInstance;
import com.WorldOfGoo.Resrc.Resources;
import com.WorldOfGoo.Resrc.ResrcImage;
import com.WorldOfGoo.Resrc.Sound;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

public class LevelUpdater {

    private static final Logger logger = LoggerFactory.getLogger(LevelLoader.class);


    public static void saveLevel(WorldLevel level) {
        GameVersion version = level.getVersion();
        saveSpecificLevel(level, version);
        if (level.getEditingStatus() != LevelTab.NO_UNSAVED_CHANGES) {
            level.setEditingStatus(LevelTab.NO_UNSAVED_CHANGES, true);
        }
    }


    public static void saveSpecificLevel(WorldLevel level, GameVersion version) {
        try {
            String dir = version == GameVersion.OLD ? FileManager.getOldWOGdir() : FileManager.getNewWOGdir();
            LevelWriter.saveAsXML(level, dir + "\\res\\levels\\" + level.getLevelName(),
                    version, false, true);
        } catch (IOException e) {
            Alarms.errorMessage(e);
        }
    }


    public static void saveAll() {
        int selectedIndex = FXLevelSelectPane.getLevelSelectPane().getSelectionModel().getSelectedIndex();
        for (Tab tab : FXLevelSelectPane.getLevelSelectPane().getTabs().toArray(new Tab[0])) {
            LevelTab levelTab = (LevelTab) tab;
            if (levelTab.getLevel().getEditingStatus() == LevelTab.UNSAVED_CHANGES) {
                saveSpecificLevel(levelTab.getLevel(), levelTab.getLevel().getVersion());
                levelTab.getLevel().setEditingStatus(LevelTab.NO_UNSAVED_CHANGES, false);
            }
        }
        FXLevelSelectPane.getLevelSelectPane().getSelectionModel().select(selectedIndex);
    }

    public static void playLevel(WorldLevel level) {
        if (level.getVersion() == GameVersion.OLD) {
            try {
                ProcessBuilder processBuilder = new ProcessBuilder(
                        FileManager.getOldWOGdir() + "\\WorldOfGoo.exe", level.getLevelName());
                processBuilder.directory(new File(FileManager.getOldWOGdir()));
                processBuilder.start();
            } catch (Exception e) {
                Alarms.errorMessage(e);
            }
        } else {

            // TODO figure something out to play in 1.5
            Alarms.errorMessage(new RuntimeException("Playing is currently unsupported for 1.5; try 1.3 instead. :("));

        }

    }

    public static void renameLevel(WorldLevel level) {
        if (level != null) {
            Alarms.askForLevelName("changeName", level.getVersion());
        }
    }

    public static void renameLevel(WorldLevel level, String text) {

        logger.info("Renaming " + level.getLevelName() + " to " + text);

        String start = level.getVersion() == GameVersion.OLD ? FileManager.getOldWOGdir() : FileManager.getNewWOGdir();

        /* Change level name in directory */
        File originalLevelDirectory = new File(start + "\\res\\levels\\" + level.getLevelName());
        File levelDirectory = new File(start + "\\res\\levels\\" + text);
        if (!originalLevelDirectory.renameTo(levelDirectory)) {
            Alarms.errorMessage("Could not rename level! (" + level.getLevelName() + " to " + text + ")");
            return;
        }

        /* Change the names of the scene, level, resrc, addin, text files */
        File[] levelParts = levelDirectory.listFiles();
        if (levelParts == null) return;

        for (File levelPart : levelParts) {
            if (levelPart.getName().length() >= level.getLevelName().length()
                    && levelPart.getName().startsWith(level.getLevelName())) {
                if (!levelPart.renameTo(new File(start + "\\res\\levels\\" + text + "\\" + text
                        + levelPart.getName().substring(level.getLevelName().length())))) {
                    Alarms.errorMessage("Could not rename level! (" + level.getLevelName() + " to " + text + ")");
                    return;
                }
            }
        }

        /* Edit every resource */
        for (EditorObject resource : level.getResources()) {

            if (resource instanceof Resources) {

                resource.setAttribute("id", "scene_" + text);

            } else if (resource instanceof ResrcImage || resource instanceof Sound) {

                String previousID = resource.getAttribute("id").stringValue();
                String newID = previousID.replaceAll(level.getLevelName().toUpperCase(), text.toUpperCase());
                resource.setAttribute("id", newID);

                String previousPath = resource.getAttribute("path").stringValue();
                String newPath = previousPath.replaceAll(level.getLevelName(), text);
                resource.setAttribute("path", newPath);

            }

        }

        level.setLevelName(text);
        level.setEditingStatus(level.getEditingStatus(), true);

        saveLevel(level);

    }

    public static void deleteLevel(WorldLevel level) {
        if (level != null) {
            Alarms.askForLevelName("delete", level.getVersion());
        }
    }

    public static void deleteLevelForReal(WorldLevel level) {
        String start = level.getVersion() == GameVersion.OLD ? FileManager.getOldWOGdir() : FileManager.getNewWOGdir();
        try {
            nuke(new File(start + "\\res\\levels\\" + level.getLevelName()));
            TabPane levelSelectPane = FXLevelSelectPane.getLevelSelectPane();
            levelSelectPane.getTabs().remove(levelSelectPane.getSelectionModel().getSelectedItem());
        } catch (Exception e) {
            Alarms.errorMessage(e);
        }
    }

    public static void nuke(File file) throws Exception {
        if (file.isDirectory()) {
            File[] children = file.listFiles();
            if (children != null) for (File child : children) {
                nuke(child);
            }
        }
        Files.delete(file.toPath());
    }

    public static void exportLevel(WorldLevel level, boolean includeAddinInfo) {

        String dir = level.getVersion() == GameVersion.OLD ? FileManager.getOldWOGdir() : FileManager.getNewWOGdir();

        FileChooser fileChooser = new FileChooser();
        if (!Files.exists(Path.of((dir + "\\res\\levels\\" + level.getLevelName() + "\\goomod")))) {
            try {
                Files.createDirectories(Path.of((dir + "\\res\\levels\\" + level.getLevelName() + "\\goomod")));
            } catch (Exception e) {
                Alarms.errorMessage(e);
            }
        }
        fileChooser.setInitialDirectory(new File((dir + "\\res\\levels\\" + level.getLevelName() + "\\goomod")));
        fileChooser.setInitialFileName(level.getLevelName());

        ExtensionFilter goomodFilter = new ExtensionFilter("World of Goo mod (*.goomod)", "*.goomod");
        fileChooser.getExtensionFilters().add(goomodFilter);
        File export = fileChooser.showSaveDialog(FXStage.getStage());

        ArrayList<_Ball> balls = new ArrayList<>();
        for (EditorObject object : level.getLevel()) {
            if (object instanceof BallInstance ballInstance) {
                if (!balls.contains(ballInstance.getBall())) {
                    balls.add(ballInstance.getBall());
                }
            }
        }
        if (export != null) {
            GoomodExporter.exportGoomod(export, level, balls, includeAddinInfo);
        }
    }

}
