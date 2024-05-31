package com.woogleFX.functions;

import com.woogleFX.editorObjects.EditorAttribute;
import com.woogleFX.editorObjects.InputField;
import com.woogleFX.editorObjects._Ball;
import com.woogleFX.engine.fx.FXHierarchy;
import com.woogleFX.engine.fx.FXLevelSelectPane;
import com.woogleFX.engine.fx.FXPropertiesView;
import com.woogleFX.engine.fx.FXStage;
import com.woogleFX.file.FileManager;
import com.woogleFX.file.fileExport.GoomodExporter;
import com.woogleFX.file.fileExport.LevelWriter;
import com.woogleFX.engine.gui.Alarms;
import com.woogleFX.editorObjects.EditorObject;
import com.woogleFX.structures.GameVersion;
import com.woogleFX.structures.simpleStructures.LevelTab;
import com.woogleFX.structures.WorldLevel;
import com.worldOfGoo.level.BallInstance;
import com.worldOfGoo.resrc.Resources;
import com.worldOfGoo.resrc.ResrcImage;
import com.worldOfGoo.resrc.Sound;
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
        if (saveSpecificLevel(level, version)) {
            level.setLastSavedUndoPosition(level.undoActions.size());
            if (level.getEditingStatus() != LevelTab.NO_UNSAVED_CHANGES) {
                level.setEditingStatus(LevelTab.NO_UNSAVED_CHANGES, true);
            }
        }
    }


    private static boolean verifySingleObject(EditorObject object) {
        for (EditorAttribute attribute : object.getAttributes())
            if (!InputField.verify(object, attribute.getType(), attribute.stringValue()))
                return false;
        return true;
    }


    private static boolean verifyAllObjects(ArrayList<EditorObject> editorObjects) {
        for (EditorObject object : editorObjects) if (!verifySingleObject(object)) return false;
        return true;
    }


    private static boolean verifyEntireLevel(WorldLevel level) {
        return verifyAllObjects(level.getScene()) &&
                verifyAllObjects(level.getLevel()) &&
                verifyAllObjects(level.getResrc()) &&
                verifyAllObjects(level.getText()) &&
                verifyAllObjects(level.getAddin());
    }


    public static boolean saveSpecificLevel(WorldLevel level, GameVersion version) {

        boolean okayToSave = true;

        // Check for errors in level objects
        if (!verifyEntireLevel(level)) {
            // Fail to save
            Alarms.errorMessage("Level could not be verified");
            okayToSave = false;
        }

        // TODO: check for game errors (stuff like there being a levelexit but no pipe)

        if (!okayToSave) return false;

        try {
            LevelWriter.saveAsXML(level, FileManager.getGameDir(version) + "\\res\\levels\\" + level.getLevelName(),
                    version, false, true);
            return true;
        } catch (IOException e) {
            Alarms.errorMessage(e);
            return false;
        }

    }


    public static void saveAll() {
        int selectedIndex = FXLevelSelectPane.getLevelSelectPane().getSelectionModel().getSelectedIndex();
        for (Tab tab : FXLevelSelectPane.getLevelSelectPane().getTabs().toArray(new Tab[0])) {
            LevelTab levelTab = (LevelTab) tab;
            if (levelTab.getLevel().getEditingStatus() == LevelTab.UNSAVED_CHANGES) {
                if (saveSpecificLevel(levelTab.getLevel(), levelTab.getLevel().getVersion())) {
                    levelTab.getLevel().setEditingStatus(LevelTab.NO_UNSAVED_CHANGES, false);
                }
            }
        }
        FXLevelSelectPane.getLevelSelectPane().getSelectionModel().select(selectedIndex);
    }

    public static void playLevel(WorldLevel level) {
        if (level.getVersion() == GameVersion.OLD) {
            try {
                ProcessBuilder processBuilder = new ProcessBuilder(
                        FileManager.getGameDir(GameVersion.OLD) + "\\WorldOfGoo.exe", level.getLevelName());
                processBuilder.directory(new File(FileManager.getGameDir(GameVersion.OLD)));
                processBuilder.start();
            } catch (Exception e) {
                Alarms.errorMessage(e);
            }
        } else {

            // TODO figure something out to play in 1.5
            Alarms.errorMessage(new RuntimeException("Playing is only supported for 1.3. :("));

        }

    }

    public static void renameLevel(WorldLevel level) {
        if (level != null) {
            Alarms.askForLevelName("changeName", level.getVersion());
        }
    }

    public static void renameLevel(WorldLevel level, String text) {

        logger.info("Renaming " + level.getLevelName() + " to " + text);

        String start = FileManager.getGameDir(level.getVersion());

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
        for (EditorObject resource : level.getResrc()) {

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
        try {
            nuke(new File(FileManager.getGameDir(level.getVersion()) + "\\res\\levels\\" + level.getLevelName()));
            TabPane levelSelectPane = FXLevelSelectPane.getLevelSelectPane();
            if (levelSelectPane.getTabs().size() == 1) {
                FXLevelSelectPane.getLevelSelectPane().setMinHeight(0);
                FXLevelSelectPane.getLevelSelectPane().setMaxHeight(0);
                // If all tabs are closed, clear the side pane
                FXHierarchy.getHierarchy().setRoot(null);
                // Clear the properties pane too
                FXPropertiesView.changeTableView(new EditorObject[]{});
            }
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

        String dir = FileManager.getGameDir(level.getVersion());

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

        ArrayList<WorldLevel> levels = new ArrayList<>();
        levels.add(level);

        ArrayList<_Ball> balls = new ArrayList<>();
        for (EditorObject object : level.getLevel()) if (object instanceof BallInstance ballInstance)
            if (!balls.contains(ballInstance.getBall())) balls.add(ballInstance.getBall());

        if (export != null) {
            try {
                GoomodExporter.exportGoomod(export, levels, balls, level.getVersion(), includeAddinInfo);
            } catch (IOException e) {
                logger.error("", e);
            }
        }
    }

}
