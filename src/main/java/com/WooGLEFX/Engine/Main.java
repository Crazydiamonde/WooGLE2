package com.WooGLEFX.Engine;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import javax.imageio.ImageIO;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import com.WooGLEFX.EditorObjects._Ball;
import com.WooGLEFX.File.AnimationReader;
import com.WooGLEFX.File.FileManager;
import com.WooGLEFX.File.GlobalResourceManager;
import com.WooGLEFX.File.LevelExporter;
import com.WooGLEFX.GUI.Alarms;
import com.WooGLEFX.GUI.BallSelector;
import com.WooGLEFX.GUI.LevelSelector;
import com.WooGLEFX.Structures.EditorAttribute;
import com.WooGLEFX.Structures.EditorObject;
import com.WooGLEFX.Structures.InputField;
import com.WooGLEFX.Structures.WorldLevel;
import com.WooGLEFX.Structures.SimpleStructures.DragSettings;
import com.WooGLEFX.Structures.SimpleStructures.LevelTab;
import com.WooGLEFX.Structures.SimpleStructures.WoGAnimation;
import com.WooGLEFX.Structures.UserActions.AttributeChangeAction;
import com.WooGLEFX.Structures.UserActions.ImportResourceAction;
import com.WooGLEFX.Structures.UserActions.ObjectCreationAction;
import com.WooGLEFX.Structures.UserActions.ObjectDestructionAction;
import com.WooGLEFX.Structures.UserActions.UserAction;
import com.WorldOfGoo.Addin.AddinLevelName;
import com.WorldOfGoo.Level.BallInstance;
import com.WorldOfGoo.Level.Camera;
import com.WorldOfGoo.Level.Endoncollision;
import com.WorldOfGoo.Level.Endonmessage;
import com.WorldOfGoo.Level.Endonnogeom;
import com.WorldOfGoo.Level.Fire;
import com.WorldOfGoo.Level.Levelexit;
import com.WorldOfGoo.Level.Loopsound;
import com.WorldOfGoo.Level.Music;
import com.WorldOfGoo.Level.Pipe;
import com.WorldOfGoo.Level.Poi;
import com.WorldOfGoo.Level.Signpost;
import com.WorldOfGoo.Level.Strand;
import com.WorldOfGoo.Level.Targetheight;
import com.WorldOfGoo.Level.Vertex;
import com.WorldOfGoo.Particle._Particle;
import com.WorldOfGoo.Resrc.ResourceManifest;
import com.WorldOfGoo.Resrc.Resources;
import com.WorldOfGoo.Resrc.ResrcImage;
import com.WorldOfGoo.Resrc.SetDefaults;
import com.WorldOfGoo.Resrc.Sound;
import com.WorldOfGoo.Scene.Button;
import com.WorldOfGoo.Scene.Buttongroup;
import com.WorldOfGoo.Scene.Circle;
import com.WorldOfGoo.Scene.Compositegeom;
import com.WorldOfGoo.Scene.Hinge;
import com.WorldOfGoo.Scene.Label;
import com.WorldOfGoo.Scene.Line;
import com.WorldOfGoo.Scene.Linearforcefield;
import com.WorldOfGoo.Scene.Motor;
import com.WorldOfGoo.Scene.Particles;
import com.WorldOfGoo.Scene.Radialforcefield;
import com.WorldOfGoo.Scene.Rectangle;
import com.WorldOfGoo.Scene.SceneLayer;
import com.WorldOfGoo.Scene.Slider;
import com.WorldOfGoo.Text.TextString;
import com.WorldOfGoo.Text.TextStrings;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Separator;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.ToolBar;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableView;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.scene.transform.Affine;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class Main extends Application {

    private static String[] launchArguments;

    public static Affine t;

    private static EditorObject strand1Gooball;

    public static EditorObject getStrand1Gooball() {
        return strand1Gooball;
    }

    private static TabPane levelSelectPane;

    public static Point2D getScreenCenter() {
        return new Point2D((thingPane.getWidth() / 2 - level.getOffsetX()) / level.getZoom(),
                (thingPane.getHeight() / 2 - level.getOffsetY()) / level.getZoom());
    }

    public static double getMouseYOffset() {
        return levelSelectPane.getHeight() + vBox.getChildren().get(4).getLayoutY();
    }

    public static void reloadWorldOfGoo(double version) {

        FileManager.getPaletteBalls().clear();
        FileManager.getPaletteVersions().clear();

        try {
            FileManager.readWOGdirs();
        } catch (ParserConfigurationException | SAXException | IOException e) {
            Alarms.errorMessage(e);
        }

        importedBalls.removeIf(ball -> ball.getVersion() == version);

        importGameResources(version);

        for (int i = 0; i < FileManager.getPaletteBalls().size(); i++) {

            String ballName = FileManager.getPaletteBalls().get(i);
            double ballVersion = FileManager.getPaletteVersions().get(i);

            System.out.println(ballName + ", " + ballVersion);

            if (ballVersion == version) {

                try {
                    _Ball ball = FileManager.openBall(ballName, ballVersion);

                    for (EditorObject resrc : FileManager.commonBallResrcData) {
                        GlobalResourceManager.addResource(resrc, ballVersion);
                    }

                    if (ball != null) {
                        ball.makeImages(ballVersion);
                        ball.setVersion(ballVersion);
                        importedBalls.add(ball);
                    }
                } catch (ParserConfigurationException | SAXException | IOException e) {
                    Alarms.errorMessage(e);
                }
            }
        }
    }

    public static boolean changeWorldOfGooDirectory(double version) {
        FileChooser findWorldOfGoo = new FileChooser();
        findWorldOfGoo.getExtensionFilters()
                .add(new FileChooser.ExtensionFilter("World of Goo executable", "WorldOfGoo.exe"));
        File worldOfGoo = findWorldOfGoo.showOpenDialog(new Stage());
        if (worldOfGoo == null) {
            return false;
        } else {
            if (version == 1.3) {
                FileManager.setOldWOGdir(worldOfGoo.getParent());
                try {
                    FileManager.saveProperties();
                } catch (IOException e) {
                    Alarms.errorMessage(e);
                }
                reloadWorldOfGoo(1.3);
                FXCreator.buttonOpenOld.setDisable(false);
                FXCreator.buttonNewOld.setDisable(false);
                FXCreator.newLevelOldItem.setDisable(false);
                FXCreator.openLevelOldItem.setDisable(false);
                if (level != null) {
                    FXCreator.buttonSave.setDisable(false);
                    FXCreator.cloneLevelItem.setDisable(false);
                    FXCreator.saveLevelItem.setDisable(false);
                }
            } else {
                FileManager.setNewWOGdir(worldOfGoo.getParent() + "\\game");
                try {
                    FileManager.saveProperties();
                } catch (IOException e) {
                    Alarms.errorMessage(e);
                }
                reloadWorldOfGoo(1.5);
                FXCreator.buttonOpenNew.setDisable(false);
                FXCreator.buttonNewNew.setDisable(false);
                FXCreator.newLevelNewItem.setDisable(false);
                FXCreator.openLevelNewItem.setDisable(false);
                if (level != null) {
                    FXCreator.buttonClone.setDisable(false);
                    FXCreator.buttonSave.setDisable(false);
                    FXCreator.cloneLevelItem.setDisable(false);
                    FXCreator.saveLevelItem.setDisable(false);
                }
            }
            if(FXCreator.getOldGooballsToolbar() != null) {
                FXCreator.getOldGooballsToolbar().getItems().clear();
            }
            if(FXCreator.getNewGooballsToolbar() != null) {
                FXCreator.getNewGooballsToolbar().getItems().clear();
            }
            FXCreator.addBallsTo();
            return true;
        }
    }

    public static void quit() {
        resumeLevelClosing();
    }

    public static void newLevel(double version) {
        Alarms.askForLevelName("new", version);
    }

    public static void newLevel(String name, double version) {
        System.out.println("New level");

        levelSelectPane.setMinHeight(30);
        levelSelectPane.setMaxHeight(30);

        ArrayList<EditorObject> sceneList = new ArrayList<>();
        sceneList.add(EditorObject.create("scene", new EditorAttribute[0], null));
        ArrayList<EditorObject> levelList = new ArrayList<>();
        levelList.add(EditorObject.create("level", new EditorAttribute[0], null));
        ArrayList<EditorObject> resourcesList = new ArrayList<>();
        resourcesList.add(EditorObject.create("ResourceManifest", new EditorAttribute[0], null));
        ArrayList<EditorObject> addinList = new ArrayList<>();
        addinList.add(generateBlankAddinObject(name));
        ArrayList<EditorObject> textList = new ArrayList<>();
        textList.add(EditorObject.create("strings", new EditorAttribute[0], null));

        level = new WorldLevel(sceneList, levelList, resourcesList, addinList, textList, version);
        level.setLevelName(name);
        enableAllButtons(false);

        level.getSceneObject().setRealName("scene");
        level.getSceneObject().setTreeItem(new TreeItem<>(level.getSceneObject()));
        level.getSceneObject().setAttribute("backgroundcolor", "255,255,255");
        level.getLevelObject().setRealName("level");
        level.getLevelObject().setTreeItem(new TreeItem<>(level.getLevelObject()));
        level.getResourcesObject().setRealName("ResourceManifest");
        level.getResourcesObject().setTreeItem(new TreeItem<>(level.getResourcesObject()));

        EditorObject resourcesThing = EditorObject.create("Resources", new EditorAttribute[0],
                level.getResourcesObject());
        resourcesThing.setTreeItem(new TreeItem<>(resourcesThing));
        resourcesThing.setAttribute("id", "scene_" + level.getLevelName());
        resourcesThing.getTreeItem().setExpanded(true);
        level.getResourcesObject().getTreeItem().getChildren().add(resourcesThing.getTreeItem());

        for (EditorObject object : level.getScene()) {
            object.update();
        }

        for (EditorObject object : level.getLevel()) {
            object.update();
        }

        for (EditorObject object : level.getResources()) {
            object.update();
        }

        // Put everything in the hierarchy
        level.getSceneObject().getTreeItem().setExpanded(true);
        hierarchy.setRoot(level.getSceneObject().getTreeItem());

        // Add items from the Scene to it
        propertiesView.setRoot(level.getSceneObject().getPropertiesTreeItem());

        Tab levelSelectButton = FXCreator.levelSelectButton(level);
        levelSelectPane.getTabs().add(levelSelectButton);

        int numTabs = levelSelectPane.getTabs().size();
        double tabSize = 1 / (numTabs + 1.0);
        levelSelectPane.setTabMaxWidth(tabSize * (levelSelectPane.getWidth() - 15) - 15);
        levelSelectPane.setTabMinWidth(tabSize * (levelSelectPane.getWidth() - 15) - 15);

        saveLevel();

        level.setLevelTab(levelSelectButton);
        level.setEditingStatus(WorldLevel.NO_UNSAVED_CHANGES, true);
        levelSelectPane.getSelectionModel().select(levelSelectButton);
        onSetLevel();
    }

    public static void openLevel(double version) {
        new LevelSelector(version).start(new Stage());
    }

    public static void enableAllButtons(boolean disable) {
        for (int i : new int[] { 1, 3 }) {
            if (vBox.getChildren().get(i) instanceof ToolBar toolBar) {
                for (Node node : toolBar.getItems()) {
                    node.setDisable(disable);
                }
            }
        }
        for (MenuItem menuItem : FXCreator.editMenu.getItems()) {
            menuItem.setDisable(disable);
        }
        for (MenuItem menuItem : FXCreator.levelMenu.getItems()) {
            menuItem.setDisable(disable);
        }
        for (MenuItem menuItem : FXCreator.resourcesMenu.getItems()) {
            menuItem.setDisable(disable);
        }
        if (!FileManager.isHasOldWOG()) {
            FXCreator.buttonNewOld.setDisable(true);
            FXCreator.buttonOpenOld.setDisable(true);
            FXCreator.buttonSave.setDisable(true);
            FXCreator.buttonClone.setDisable(true);
            if (level != null) {
                FXCreator.newLevelOldItem.setDisable(true);
                FXCreator.openLevelOldItem.setDisable(true);
                FXCreator.saveLevelItem.setDisable(true);
                FXCreator.cloneLevelItem.setDisable(true);
            } else {
                FXCreator.newLevelOldItem.setDisable(false);
                FXCreator.openLevelOldItem.setDisable(false);
                FXCreator.saveLevelItem.setDisable(false);
                FXCreator.cloneLevelItem.setDisable(false);
            }
        }
        if (!FileManager.isHasNewWOG()) {
            FXCreator.buttonNewNew.setDisable(true);
            FXCreator.buttonOpenNew.setDisable(true);
            FXCreator.buttonSave.setDisable(true);
            FXCreator.buttonClone.setDisable(true);
            if (level != null) {
                FXCreator.newLevelNewItem.setDisable(true);
                FXCreator.openLevelNewItem.setDisable(true);
                FXCreator.saveLevelItem.setDisable(true);
                FXCreator.cloneLevelItem.setDisable(true);
            } else {
                FXCreator.newLevelNewItem.setDisable(false);
                FXCreator.openLevelNewItem.setDisable(false);
                FXCreator.saveLevelItem.setDisable(false);
                FXCreator.cloneLevelItem.setDisable(false);
            }
        }
        if ((FileManager.isHasOldWOG() || FileManager.isHasNewWOG()) && level != null) {
            FXCreator.buttonSaveAndPlay.setDisable(false);
            FXCreator.saveAndPlayLevelItem.setDisable(false);
        }
    }

    public static final ArrayList<String> failedResources = new ArrayList<>();

    public static void openLevel(String levelName, double version) {
        failedResources.clear();

        try {
            level = FileManager.openLevel(levelName, version);
        } catch (ParserConfigurationException | SAXException | IOException e) {
            Alarms.errorMessage(e);
            return;
        }

        level.setLevelName(levelName);
        enableAllButtons(false);

        levelSelectPane.setMinHeight(30);
        levelSelectPane.setMaxHeight(30);

        for (EditorObject object : level.getLevel()) {
            if (object instanceof BallInstance) {
                boolean alreadyIn = false;
                for (_Ball ball : importedBalls) {
                    if (ball.getObjects().get(0).getAttribute("name").equals(object.getAttribute("type"))
                            && ball.getVersion() == level.getVersion()) {
                        alreadyIn = true;
                        break;
                    }
                }
                if (!alreadyIn) {
                    try {
                        _Ball ball2 = FileManager.openBall(object.getAttribute("type"), version);

                        for (EditorObject resrc : FileManager.commonBallResrcData) {
                            GlobalResourceManager.addResource(resrc, version);
                        }

                        if (ball2 != null) {
                            ball2.makeImages(version);
                            ball2.setVersion(version);

                            importedBalls.add(ball2);
                        }
                    } catch (ParserConfigurationException | SAXException | IOException e) {
                        if (!failedResources
                                .contains("Ball: " + object.getAttribute("type") + " (version " + version + ")")) {
                            failedResources.add("Ball: " + object.getAttribute("type") + " (version " + version + ")");
                        }
                    }
                }
            }
        }

        for (EditorObject object : level.getResources()) {
            if (object instanceof ResrcImage) {
                GlobalResourceManager.addResource(object, level.getVersion());
            }
        }

        for (EditorObject object : level.getScene()) {
            object.setLevel(level);
            object.update();
        }

        for (EditorObject object : level.getLevel()) {
            object.setLevel(level);
            object.update();
        }

        for (EditorObject object : level.getResources()) {
            object.setLevel(level);
            object.update();
        }

        // Put everything in the hierarchy
        level.getSceneObject().getTreeItem().setExpanded(true);
        hierarchy.setRoot(level.getSceneObject().getTreeItem());

        // Add items from the Scene to it
        propertiesView.setRoot(level.getSceneObject().getPropertiesTreeItem());

        Tab levelSelectButton = FXCreator.levelSelectButton(level);
        levelSelectPane.getTabs().add(levelSelectButton);

        int numTabs = levelSelectPane.getTabs().size();
        double tabSize = 1 / (numTabs + 1.0);
        levelSelectPane.setTabMaxWidth(tabSize * (levelSelectPane.getWidth() - 15) - 15);
        levelSelectPane.setTabMinWidth(tabSize * (levelSelectPane.getWidth() - 15) - 15);

        if (failedResources.size() > 0) {
            StringBuilder fullError = new StringBuilder();
            for (String resource : failedResources) {
                fullError.append("\n").append(resource);
            }
            Alarms.loadingResourcesError(fullError.toString().substring(1));
        }

        level.setLevelTab(levelSelectButton);
        level.setEditingStatus(WorldLevel.NO_UNSAVED_CHANGES, true);
        levelSelectPane.getSelectionModel().select(levelSelectButton);
        onSetLevel();
    }

    public static Canvas getCanvas() {
        return canvas;
    }

    public static void setCanvas(Canvas canvas) {
        Main.canvas = canvas;
    }

    public static void supremeAddToList(ArrayList<EditorObject> list, EditorObject object) {
        list.add(object);
        for (EditorObject child : object.getChildren()) {
            supremeAddToList(list, child);
        }
    }

    public static void cloneLevel() {
        double version = level.getVersion();
        Alarms.askForLevelName("clone", version);
    }

    public static void cloneLevel(String name, double version) {
        levelSelectPane.setMinHeight(30);
        levelSelectPane.setMaxHeight(30);

        ArrayList<EditorObject> sceneList = new ArrayList<>();
        ArrayList<EditorObject> levelList = new ArrayList<>();
        ArrayList<EditorObject> resourcesList = new ArrayList<>();
        ArrayList<EditorObject> addinList = new ArrayList<>();
        ArrayList<EditorObject> textList = new ArrayList<>();

        supremeAddToList(resourcesList, level.getResourcesObject().deepClone(null));
        supremeAddToList(sceneList, level.getSceneObject().deepClone(null));
        supremeAddToList(levelList, level.getLevelObject().deepClone(null));
        // Generate new addin object. idk why cloning it doesn't work, but this is arguably better anyway
        supremeAddToList(addinList, generateBlankAddinObject(name));
        supremeAddToList(textList, level.getTextObject().deepClone(null));

        String oldLevelName = level.getLevelName();
        level = new WorldLevel(sceneList, levelList, resourcesList, addinList, textList, version);

        level.setLevelName(name);
        enableAllButtons(false);

        for (EditorObject object : level.getResources()) {
            if (object instanceof Resources) {
                object.setAttribute("id", "scene_" + name);
            } else if (object instanceof ResrcImage || object instanceof Sound) {
                object.setAttribute("id", object.getAttribute("id").replaceAll(oldLevelName.toUpperCase(), name.toUpperCase()));
                object.setAttribute("REALid", object.getAttribute("REALid").replaceAll(oldLevelName.toUpperCase(), name.toUpperCase()));
                object.setNameAttribute(object.getAttribute2("id"));
            }
            GlobalResourceManager.addResource(object, version);
            object.setLevel(level);
            object.update();
        }

        for (EditorObject object : level.getScene()) {
            if (object instanceof SceneLayer) {
                object.setAttribute("image", object.getAttribute("image").replaceAll(oldLevelName, name));
            }
            object.setLevel(level);
            object.update();
        }

        for (EditorObject object : level.getLevel()) {
            if (object instanceof Signpost) {
                object.setAttribute("image", object.getAttribute("image").replaceAll(oldLevelName, name));
            }
            object.setLevel(level);
            object.update();
        }

        // Put everything in the hierarchy
        level.getSceneObject().getTreeItem().setExpanded(true);
        hierarchy.setRoot(level.getSceneObject().getTreeItem());

        // Add items from the Scene to it
        propertiesView.setRoot(level.getSceneObject().getPropertiesTreeItem());

        Tab levelSelectButton = FXCreator.levelSelectButton(level);
        levelSelectPane.getTabs().add(levelSelectButton);

        int numTabs = levelSelectPane.getTabs().size();
        double tabSize = 1 / (numTabs + 1.0);
        levelSelectPane.setTabMaxWidth(tabSize * (levelSelectPane.getWidth() - 15) - 15);
        levelSelectPane.setTabMinWidth(tabSize * (levelSelectPane.getWidth() - 15) - 15);

        saveLevel();

        level.setLevelTab(levelSelectButton);
        level.setEditingStatus(WorldLevel.NO_UNSAVED_CHANGES, true);
        levelSelectPane.getSelectionModel().select(levelSelectButton);
        onSetLevel();
    }

    public static void saveLevel() {
        double version = level.getVersion();
        saveSpecificLevel(level, version);
        if (level.getEditingStatus() != WorldLevel.NO_UNSAVED_CHANGES) {
            level.setEditingStatus(WorldLevel.NO_UNSAVED_CHANGES, true);
        }
    }

    public static void saveSpecificLevel(WorldLevel level, double version) {
        try {
            if (version == 1.3) {
                LevelExporter.saveAsXML(level, FileManager.getOldWOGdir() + "\\res\\levels\\" + level.getLevelName(),
                        version, false, true);
            } else {
                LevelExporter.saveAsXML(level, FileManager.getNewWOGdir() + "\\res\\levels\\" + level.getLevelName(),
                        version, false, true);
            }
        } catch (IOException e) {
            Alarms.errorMessage(e);
        }
    }

    public static void saveAll() {
        int selectedIndex = levelSelectPane.getSelectionModel().getSelectedIndex();
        for (Tab tab : levelSelectPane.getTabs().toArray(new Tab[0])) {
            LevelTab levelTab = (LevelTab) tab;
            if (levelTab.getLevel().getEditingStatus() == WorldLevel.UNSAVED_CHANGES) {
                saveSpecificLevel(levelTab.getLevel(), levelTab.getLevel().getVersion());
                levelTab.getLevel().setEditingStatus(WorldLevel.NO_UNSAVED_CHANGES, false);
            }
        }
        levelSelectPane.getSelectionModel().select(selectedIndex);
    }

    public static void playLevel() {
        if (level.getVersion() == 1.3) {
            try {
                ProcessBuilder processBuilder = new ProcessBuilder(FileManager.getOldWOGdir() + "\\WorldOfGoo.exe",
                        level.getLevelName());
                processBuilder.directory(new File(FileManager.getOldWOGdir()));
                processBuilder.redirectErrorStream(true);
                /* Process process = */processBuilder.start();
                // BufferedReader reader = new BufferedReader(new
                // InputStreamReader(process.getInputStream()));
                // String line;
                // while ((line = reader.readLine()) != null) {
                // System.out.println(line);
                // }
            } catch (Exception e) {
                Alarms.errorMessage(e);
            }
        }

        // TODO figure something out to play in 1.5
        Alarms.errorMessage(new RuntimeException(
                "Sorry, I have no idea how to make Steam/GOG/EPIC play specific World of Goo levels. It works for the 1.3 version though."));

    }

    public static void renameLevel() {
        if (level != null) {
            Alarms.askForLevelName("changename", level.getVersion());
        }
    }

    public static void renameLevel(String text) {

        System.out.println("Renaming " + level.getLevelName() + " to " + text);

        String start = level.getVersion() == 1.3 ? FileManager.getOldWOGdir() : FileManager.getNewWOGdir();

        /* Change level name in directory */
        new File(start + "\\res\\levels\\" + level.getLevelName()).renameTo(new File(start + "\\res\\levels\\" + text));

        /* Change the names of the scene, level, resrc, addin, text files */
        for (File levelPart : new File(start + "\\res\\levels\\" + text).listFiles()) {
            if (levelPart.getName().length() >= level.getLevelName().length()
                    && levelPart.getName().startsWith(level.getLevelName())) {
                levelPart.renameTo(new File(start + "\\res\\levels\\" + text + "\\" + text
                        + levelPart.getName().substring(level.getLevelName().length())));
            }
        }

        /* Edit every resource */
        for (EditorObject resource : level.getResources()) {
            if (resource instanceof Resources) {
                resource.setAttribute("id", "scene_" + text);
            } else if (resource instanceof ResrcImage || resource instanceof Sound) {
                /* Change ID */
                resource.setAttribute("id",
                        resource.getAttribute("id").replaceAll(level.getLevelName().toUpperCase(), text.toUpperCase()));
                resource.setAttribute("path", resource.getAttribute("path").replaceAll(level.getLevelName(), text));
            }
        }

        level.setLevelName(text);
        level.setEditingStatus(level.getEditingStatus(), true);

        saveLevel();

    }

    public static void deleteLevel() {
        if (level != null) {
            Alarms.askForLevelName("delete", level.getVersion());
        }
    }

    public static void deleteLevelForReal() {
        String start = level.getVersion() == 1.3 ? FileManager.getOldWOGdir() : FileManager.getNewWOGdir();
        try {
            nuke(new File(start + "\\res\\levels\\" + level.getLevelName()));
            levelSelectPane.getTabs().remove(levelSelectPane.getSelectionModel().getSelectedItem());
        } catch (Exception e) {
            Alarms.errorMessage(e);
        }
    }

    public static void nuke(File file) throws Exception {
        if (file.isDirectory()) {
            for (File child : file.listFiles()) {
                nuke(child);
            }
        }
        Files.delete(file.toPath());
    }

    public static void exportLevel(boolean includeAddinInfo) {
        FileChooser fileChooser = new FileChooser();
        if (!Files.exists(Path.of((level.getVersion() == 1.3 ? FileManager.getOldWOGdir() : FileManager.getNewWOGdir())
                + "\\res\\levels\\" + level.getLevelName() + "\\goomod"))) {
            try {
                Files.createDirectories(
                        Path.of((level.getVersion() == 1.3 ? FileManager.getOldWOGdir() : FileManager.getNewWOGdir())
                                + "\\res\\levels\\" + level.getLevelName() + "\\goomod"));
            } catch (Exception e) {
                Alarms.errorMessage(e);
            }
        }
        fileChooser.setInitialDirectory(
                new File((level.getVersion() == 1.3 ? FileManager.getOldWOGdir() : FileManager.getNewWOGdir())
                        + "\\res\\levels\\" + level.getLevelName() + "\\goomod"));
        fileChooser.setInitialFileName(level.getLevelName());
        fileChooser.getExtensionFilters()
                .add(new FileChooser.ExtensionFilter("World of Goo mod (*.goomod)", "*.goomod"));
        File export = fileChooser.showSaveDialog(stage);

        ArrayList<_Ball> balls = new ArrayList<>();
        for (EditorObject object : level.getLevel()) {
            if (object instanceof BallInstance ballInstance) {
                if (!balls.contains(ballInstance.getBall())) {
                    balls.add(ballInstance.getBall());
                }
            }
        }
        if (export != null) {
            LevelExporter.exportGoomod(export, level, balls, includeAddinInfo);
        }
    }

    public static void undo() {
        if (userActions.size() != 0) {
            UserAction[] changes = userActions.pop();
            redoActions.add(changes);
            for (UserAction change : changes) {
                if (change instanceof AttributeChangeAction) {
                    change.getObject().setAttribute(((AttributeChangeAction) change).getAttributeName(),
                            ((AttributeChangeAction) change).getOldValue());
                    propertiesView.refresh();
                } else if (change instanceof ObjectCreationAction) {
                    deleteItem(change.getObject(), false);
                } else if (change instanceof ObjectDestructionAction) {
                    create(change.getObject(), ((ObjectDestructionAction) change).getPosition());
                } else if (change instanceof ImportResourceAction) {
                    deleteResource(change.getObject().getAttribute("path"));
                    deleteItem(change.getObject(), false);
                }
                hierarchy.refresh();
            }
        }
        if (userActions.size() == 0) {
            level.setEditingStatus(WorldLevel.NO_UNSAVED_CHANGES, true);
        }
    }

    private static final Stack<UserAction[]> redoActions = new Stack<>();

    public static void redo() {
        if (redoActions.size() != 0) {
            UserAction[] changes = redoActions.pop();
            registerChange(changes);
            for (UserAction change : changes) {
                if (change instanceof AttributeChangeAction) {
                    change.getObject().setAttribute(((AttributeChangeAction) change).getAttributeName(),
                            ((AttributeChangeAction) change).getNewValue());
                } else if (change instanceof ObjectCreationAction) {
                    // TODO bug here with the index being out of bounds
                    create(change.getObject(), ((ObjectCreationAction) change).getPosition());
                } else if (change instanceof ObjectDestructionAction) {
                    deleteItem(change.getObject(), false);
                } else if (change instanceof ImportResourceAction) {
                    if (change.getObject() instanceof ResrcImage) {
                        importImage(new File(((ImportResourceAction) change).getPath()));
                    } else if (change.getObject() instanceof Sound) {
                        // TODO make this work with loopsounds instead of just music
                        importMusic(new File(((ImportResourceAction) change).getPath()), false);
                    }
                }
                hierarchy.refresh();
            }
        }
    }

    public static void cut() {
        if (level.getSelected() != null) {
            copy();
            delete();
        }
    }

    public static void copy() {
        if (level.getSelected() != null && propertiesView.getEditingCell() == null) {
            String clipboard = ClipboardHandler.exportToClipBoardString(level.getSelected());
            ClipboardContent clipboardContent = new ClipboardContent();
            clipboardContent.putString(clipboard);
            Clipboard.getSystemClipboard().setContent(clipboardContent);
        }
    }

    public static void create(EditorObject object, int row) {
        object.update();

        if (object.getTreeItem() == null) {
            object.setTreeItem(new TreeItem<>(object));
        }

        if (!object.getParent().getChildren().contains(object)) {
            object.getParent().getChildren().add(object);
        }

        if (!object.getParent().getTreeItem().getChildren().contains(object.getTreeItem())) {
            object.getParent().getTreeItem().getChildren().add(row, object.getTreeItem());
        }

        if (object.getChildren().size() > 0) {
            ArrayList<EditorObject> children = object.getChildren();
            EditorObject[] dontBreak = new EditorObject[children.size()];
            int i = 0;
            for (EditorObject child : children) {
                dontBreak[i] = child;
                i++;
            }
            i = 0;
            for (EditorObject child : dontBreak) {
                create(child, i);
                i++;
            }
        }

        TreeItem<EditorObject> thing = object.getTreeItem();
        while (thing.getParent() != null) {
            thing = thing.getParent();
        }
        if (thing.getValue().getRealName().equals("scene")) {
            level.getScene().add(object);
        }
        if (thing.getValue().getRealName().equals("level")) {
            level.getLevel().add(object);
        }

        if (object instanceof BallInstance) {
            for (EditorObject strand : level.getLevel()) {
                if (strand instanceof Strand) {
                    if (object.getAttribute("id").equals(strand.getAttribute("gb1"))) {
                        ((Strand) strand).setGoo1((BallInstance) object);
                    }
                    if (object.getAttribute("id").equals(strand.getAttribute("gb2"))) {
                        ((Strand) strand).setGoo2((BallInstance) object);
                    }
                }
            }
        }
    }

    public static void paste() {
        if (propertiesView.getEditingCell() == null) {
            String clipboard = Clipboard.getSystemClipboard().getString();
            if (clipboard != null) {
                EditorObject object = ClipboardHandler.importFromClipboardString(clipboard);
                if (object != null) {
                    object.setLevel(level);

                    boolean okayToBeChild = level.getSelected() != null && level.getSelected().getParent() != null;

                    if (okayToBeChild) {
                        okayToBeChild = false;
                        for (String possibleChild : level.getSelected().getParent().getPossibleChildren()) {
                            if (possibleChild.equals(object.getRealName())) {
                                okayToBeChild = true;
                                break;
                            }
                        }
                    }

                    if (okayToBeChild) {
                        object.setParent(level.getSelected().getParent());
                    } else {
                        if (object.getClass().getPackage().getName().equals("com.WorldOfGoo.Scene")) {
                            object.setParent(level.getSceneObject());
                        } else if (object.getClass().getPackage().getName().equals("com.WorldOfGoo.Level")) {
                            object.setParent(level.getLevelObject());
                        } else if (object.getClass().getPackage().getName().equals("com.WorldOfGoo.Resrc")) {
                            object.setParent(level.getResourcesObject());
                        }
                    }
                    if (object instanceof BallInstance) {
                        fixGooball(object);
                    }
                    // object.getParent().getChildren().add(0, object);
                    create(object, 0);
                    setSelected(object);
                    registerChange(new ObjectCreationAction(object, hierarchy.getRow(object.getTreeItem())
                            - hierarchy.getRow(object.getParent().getTreeItem()) - 1));
                    redoActions.clear();
                    hierarchy.refresh();
                }
            }
        }
    }

    public static void deleteItem(EditorObject item, boolean parentDeleted) {
        if (item instanceof Strand) {
            for (EditorObject ball : level.getLevel()) {
                if (ball instanceof BallInstance) {
                    ((BallInstance) ball).getStrands().remove(item);
                }
            }
        }
        if (item instanceof BallInstance) {
            for (Strand strand : ((BallInstance) item).getStrands()) {
                if (strand.getGoo1() == item) {
                    strand.setGoo1(null);
                }
                if (strand.getGoo2() == item) {
                    strand.setGoo2(null);
                }
            }
        }
        if (item.getChildren().size() > 0) {
            List<EditorObject> children = item.getChildren();
            EditorObject[] dontBreak = new EditorObject[children.size()];
            int i = 0;
            for (EditorObject child : children) {
                dontBreak[i] = child;
                i++;
            }
            for (EditorObject child : dontBreak) {
                deleteItem(child, true);
            }
        }
        if (level.getScene().contains(item)) {
            level.getScene().remove(item);
        } else if (level.getLevel().contains(item)) {
            level.getLevel().remove(item);
        } else if (level.getResources().contains(item)) {
            level.getResources().remove(item);
        } else if (level.getAddin().contains(item)) {
            level.getAddin().remove(item);
        } else {
            level.getText().remove(item);
        }
        item.getParent().getTreeItem().getChildren().remove(item.getTreeItem());
        if (!parentDeleted) {
            item.getParent().getChildren().remove(item);
        }
    }

    public static void delete() {
        if (level.getSelected() != null) {
            EditorObject parent = level.getSelected().getParent();
            int row = parent.getChildren().indexOf(level.getSelected());
            registerChange(new ObjectDestructionAction(level.getSelected(), row));
            redoActions.clear();
            deleteItem(level.getSelected(), false);
            if (row == 0) {
                setSelected(parent);
            } else {
                setSelected(parent.getChildren().get(row - 1));
            }
            changeTableView(level.getSelected());
            // hierarchy.getFocusModel().focus(row);
            hierarchy.getSelectionModel().select(hierarchy.getRow(level.getSelected().getTreeItem()));
            hierarchy.refresh();
        }
    }

    public static void registerChange(UserAction... actions) {
        userActions.add(actions);
        if (level.getEditingStatus() == WorldLevel.NO_UNSAVED_CHANGES) {
            level.setEditingStatus(WorldLevel.UNSAVED_CHANGES, true);
        }
    }

    public static void deleteResource(String file) {
        String path = new File(file).getName();
        String startPath = level.getVersion() == 1.5 ? FileManager.getNewWOGdir() : FileManager.getOldWOGdir();
        File levelFile = new File(startPath + "\\res\\levels\\" + level.getLevelName());
        File[] levelChildren = levelFile.listFiles();
        if (levelChildren != null) {
            for (File resourceFile : levelChildren) {
                if (resourceFile.getName().split("\\.")[0].equals(path)) {
                    // noinspection ResultOfMethodCallIgnored
                    resourceFile.delete();
                }
            }
        }
    }

    public static void updateLevelResources() {
        StringBuilder failedToLoad = new StringBuilder();

        /* Loop through all the images in the level's resources */
        for (EditorObject editorObject : level.getResources()) {
            if (editorObject instanceof ResrcImage) {
                /*
                 * Ask the global resource manager to update the images to whatever's in the
                 * level file
                 */
                try {
                    GlobalResourceManager.updateResource(editorObject.getAttribute("REALid"), level.getVersion());
                } catch (FileNotFoundException e) {
                    failedToLoad.append(editorObject.getAttribute("REALid")).append("\n");
                    e.printStackTrace();
                }
            }
        }

        /* Update every object in the level */
        /* I hope this doesn't break anything */
        for (EditorObject editorObject : level.getScene()) {
            editorObject.update();
        }
        for (EditorObject editorObject : level.getLevel()) {
            editorObject.update();
        }

        if (!failedToLoad.toString().equals("")) {
            Alarms.loadingResourcesError(failedToLoad.toString());
        }

    }

    public static void importImage() {

        FileChooser fileChooser = new FileChooser();

        File resrcFile = fileChooser.showOpenDialog(new Stage());

        if (resrcFile != null) {
            importImage(resrcFile);
        }

    }

    public static void importImage(File resrcFile) {
        try {
            BufferedImage image = ImageIO.read(resrcFile);
            String normalizedFilename = resrcFile.getName().split("\\.")[0].replace(' ', '_');
            String path = "";
            if (level.getVersion() == 1.3) {
                path = FileManager.getOldWOGdir();
            } else if (level.getVersion() == 1.5) {
                path = FileManager.getNewWOGdir();
            }

            String imgPath = resrcFile.getPath();

            if (!resrcFile.getPath().contains("\\res\\")) {
                ImageIO.write(image, "png", new File(path + "\\res\\levels\\" + level.getLevelName() + "\\"
                        + normalizedFilename + ".png"));
                imgPath = path + "\\res\\levels\\" + level.getLevelName() + "\\" + normalizedFilename
                        + ".png";
            }

            String imageResourceName = "IMAGE_SCENE_" + level.getLevelName().toUpperCase() + "_"
                    + normalizedFilename.toUpperCase();
            EditorObject imageResourceObject = EditorObject.create("Image", new EditorAttribute[0], null);

            imageResourceObject.setAttribute("id", imageResourceName);
            imageResourceObject.setAttribute(
                "path",
                ("res\\levels\\" + level.getLevelName() + "\\" + normalizedFilename).replace("\\", "/")
            );
            imageResourceObject.setAttribute("REALid", imageResourceName);
            imageResourceObject.setAttribute(
                "REALpath",
                ("res\\levels\\" + level.getLevelName() + "\\" + normalizedFilename).replace("\\", "/")
            );

            int whereToPlaceResource = 0;
            int count = 0;
            for (EditorObject resourceThing : level.getResourcesObject().getChildren().get(0).getChildren()) {
                count++;
                if (resourceThing instanceof ResrcImage) {
                    whereToPlaceResource = count;
                }
            }

            level.getResources().add(imageResourceObject);
            imageResourceObject.setParent(level.getResourcesObject().getChildren().get(0), whereToPlaceResource);
            level.getResourcesObject().getChildren().get(0).getTreeItem().getChildren().add(whereToPlaceResource,
                    imageResourceObject.getTreeItem());

            registerChange(new ImportResourceAction(imageResourceObject, imgPath));
            redoActions.clear();

            GlobalResourceManager.addResource(imageResourceObject, level.getVersion());
        } catch (IOException e) {
            Alarms.errorMessage(e);
        }
    }

    public static void newTextResource() {
        EditorObject newTextObject = EditorObject.create("string", new EditorAttribute[0], level.getTextObject());
        fixString(newTextObject);
        level.getText().add(newTextObject);
        setSelected(newTextObject);
        registerChange(
                new ObjectCreationAction(newTextObject, level.getTextObject().getChildren().indexOf(newTextObject)));
        redoActions.clear();
    }

    public static void cleanLevelResources() {
        ArrayList<EditorObject> possiblyUnusedResources = new ArrayList<>();

        for (EditorObject editorObject : level.getResources()) {
            if (editorObject instanceof ResrcImage || editorObject instanceof Sound) {
                possiblyUnusedResources.add(editorObject);
            }
        }

        for (EditorObject editorObject : level.getScene()) {
            for (EditorAttribute attribute : editorObject.getAttributes()) {
                if (!attribute.getValue().equals("")) {
                    for (EditorObject resourceObject : possiblyUnusedResources) {
                        if (resourceObject.getAttribute("id").equals(attribute.getValue())) {
                            possiblyUnusedResources.remove(resourceObject);
                            break;
                        }
                    }
                }
            }
        }

        for (EditorObject editorObject : level.getLevel()) {
            for (EditorAttribute attribute : editorObject.getAttributes()) {
                if (!attribute.getValue().equals("")) {
                    for (EditorObject resourceObject : possiblyUnusedResources) {
                        if (resourceObject.getAttribute("id").equals(attribute.getValue())) {
                            possiblyUnusedResources.remove(resourceObject);
                            break;
                        }
                    }
                }
            }
        }

        if (possiblyUnusedResources.size() > 0) {
            Alarms.confirmCleanResourcesMessage(possiblyUnusedResources);
        }

    }

    public static void confirmedCleanLevelResources(ArrayList<EditorObject> toClean) {
        // TODO possibly add an undo event for this
        for (EditorObject object : toClean) {
            deleteItem(object, false);
        }
    }

    public static void importMusic() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("OGG sound file", "*.ogg"));

        File resrcFile = fileChooser.showOpenDialog(new Stage());

        if (resrcFile != null) {
            importMusic(resrcFile, true);
        }
    }

    public static void importMusic(File resrcFile, boolean fromUser) {

        /*
         * If resrcFile is not already present in res\music, copy resrcFile into
         * res\music.
         */

        boolean alreadyInstalled = false;
        /* check for file in 1.3 version */
        if (level.getVersion() == 1.3
                && new File(FileManager.getOldWOGdir() + "\\res\\music\\" + resrcFile.getName()).exists()) {
            alreadyInstalled = true;
        }
        /* check for file in 1.5 version */
        if (level.getVersion() == 1.5
                && new File(FileManager.getNewWOGdir() + "\\res\\music\\" + resrcFile.getName()).exists()) {
            alreadyInstalled = true;
        }
        /* copy file */
        if (!alreadyInstalled) {
            try {
                if (level.getVersion() == 1.3) {
                    Files.copy(resrcFile.toPath(),
                            Paths.get(FileManager.getOldWOGdir() + "\\res\\music\\" + resrcFile.getName()));
                } else if (level.getVersion() == 1.5) {
                    Files.copy(resrcFile.toPath(),
                            Paths.get(FileManager.getNewWOGdir() + "\\res\\music\\" + resrcFile.getName()));
                }
            } catch (Exception e) {
                Alarms.errorMessage(e);
            }
        }

        /*
         * Add a new sound resource with a default ID and path leading to resrcFile in
         * res\music.
         */
        String normalizedFilename = resrcFile.getName().split("\\.")[0].replace(' ', '_');
        String soundResourceName = "SOUND_LEVEL_" + level.getLevelName().toUpperCase() + "_"
                + normalizedFilename.toUpperCase();
        EditorObject soundResourceObject = EditorObject.create("Sound", new EditorAttribute[0], null);

        soundResourceObject.setAttribute("id", soundResourceName);
        soundResourceObject.setAttribute("path",
                "res\\levels\\" + level.getLevelName() + "\\" + normalizedFilename);

        int whereToPlaceResource = 0;
        int count = 0;
        for (EditorObject resourceThing : level.getResourcesObject().getChildren().get(0).getChildren()) {
            count++;
            if (resourceThing instanceof Sound) {
                whereToPlaceResource = count;
            }
        }

        level.getResources().add(soundResourceObject);
        soundResourceObject.setParent(level.getResourcesObject().getChildren().get(0), whereToPlaceResource);
        level.getResourcesObject().getChildren().get(0).getTreeItem().getChildren().add(whereToPlaceResource,
                soundResourceObject.getTreeItem());

        /* If a music object already exists, change its sound attribute. */
        for (EditorObject music : level.getLevel()) {
            if (music instanceof Music) {
                String oldID = music.getAttribute("id");
                music.setAttribute("id", soundResourceName);
                registerChange(new ImportResourceAction(soundResourceObject, resrcFile.getPath()),
                        new AttributeChangeAction(music, "id", oldID, soundResourceName));
                redoActions.clear();
                return;
            }
        }

        /* Otherwise, create a new music object set to the sound resource's ID. */
        EditorObject musicObject = EditorObject.create("music", new EditorAttribute[0], level.getLevelObject());
        musicObject.setAttribute("id", soundResourceName);
        level.getLevel().add(musicObject);
        registerChange(new ImportResourceAction(soundResourceObject, resrcFile.getPath()),
                new ObjectCreationAction(soundResourceObject, whereToPlaceResource),
                new ObjectCreationAction(musicObject, level.getLevel().size() - 1));
        redoActions.clear();

    }

    public static void importLoopsound() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("OGG sound file", "*.ogg"));

        File resrcFile = fileChooser.showOpenDialog(new Stage());

        if (resrcFile != null) {
            importLoopsound(resrcFile, true);
        }
    }

    public static void importLoopsound(File resrcFile, boolean fromUser) {

        /*
         * If resrcFile is not already present in res\music, copy resrcFile into
         * res\music.
         */

        boolean alreadyInstalled = false;
        /* check for file in 1.3 version */
        if (level.getVersion() == 1.3
                && new File(FileManager.getOldWOGdir() + "\\res\\sounds\\" + resrcFile.getName()).exists()) {
            alreadyInstalled = true;
        }
        /* check for file in 1.5 version */
        if (level.getVersion() == 1.5
                && new File(FileManager.getNewWOGdir() + "\\res\\sounds\\" + resrcFile.getName()).exists()) {
            alreadyInstalled = true;
        }
        /* copy file */
        if (!alreadyInstalled) {
            try {
                if (level.getVersion() == 1.3) {
                    Files.copy(resrcFile.toPath(),
                            Paths.get(FileManager.getOldWOGdir() + "\\res\\sounds\\" + resrcFile.getName()));
                } else if (level.getVersion() == 1.5) {
                    Files.copy(resrcFile.toPath(),
                            Paths.get(FileManager.getNewWOGdir() + "\\res\\sounds\\" + resrcFile.getName()));
                }
            } catch (Exception e) {
                Alarms.errorMessage(e);
            }
        }

        /*
         * Add a new sound resource with a default ID and path leading to resrcFile in
         * res\music.
         */
        String normalizedFilename = resrcFile.getName().split("\\.")[0].replace(' ', '_');
        String soundResourceName = "SOUND_LEVEL_" + level.getLevelName().toUpperCase() + "_"
                + normalizedFilename.toUpperCase();
        EditorObject soundResourceObject = EditorObject.create("Sound", new EditorAttribute[0], null);

        soundResourceObject.setAttribute("id", soundResourceName);
        soundResourceObject.setAttribute("path",
                "res\\levels\\" + level.getLevelName() + "\\" + normalizedFilename);

        int whereToPlaceResource = 0;
        int count = 0;
        for (EditorObject resourceThing : level.getResourcesObject().getChildren().get(0).getChildren()) {
            count++;
            if (resourceThing instanceof Sound) {
                whereToPlaceResource = count;
            }
        }

        level.getResources().add(soundResourceObject);
        soundResourceObject.setParent(level.getResourcesObject().getChildren().get(0), whereToPlaceResource);
        level.getResourcesObject().getChildren().get(0).getTreeItem().getChildren().add(whereToPlaceResource,
                soundResourceObject.getTreeItem());

        /* If a music object already exists, change its sound attribute. */
        for (EditorObject music : level.getLevel()) {
            if (music instanceof Loopsound) {
                String oldID = music.getAttribute("id");
                music.setAttribute("id", soundResourceName);
                registerChange(new ImportResourceAction(soundResourceObject, resrcFile.getPath()),
                        new AttributeChangeAction(music, "id", oldID, soundResourceName));
                redoActions.clear();
                return;
            }
        }

        /* Otherwise, create a new music object set to the sound resource's ID. */
        EditorObject musicObject = EditorObject.create("loopsound", new EditorAttribute[0], level.getLevelObject());
        musicObject.setAttribute("id", soundResourceName);
        level.getLevel().add(musicObject);
        registerChange(new ImportResourceAction(soundResourceObject, resrcFile.getPath()),
                new ObjectCreationAction(soundResourceObject, whereToPlaceResource),
                new ObjectCreationAction(musicObject, level.getLevel().size() - 1));
        redoActions.clear();

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
        FXCreator.buttonSelectMoveAndResize.setStyle("-fx-background-color: #9999ff;");
        // Un-highlight strand button
        FXCreator.buttonStrandMode.setStyle("");
    }

    public static void strandMode() {
        mode = STRAND;
        // Highlight strand button
        FXCreator.buttonStrandMode.setStyle("-fx-background-color: #9999ff;");
        // Un-highlight selection button
        FXCreator.buttonSelectMoveAndResize.setStyle("");
    }

    public static void showHideCameras() {
        level.setShowCameras(!level.isShowCameras());
    }

    public static void showHideForcefields() {
        level.setShowForcefields(!level.isShowForcefields());
    }

    public static void showHideGeometry() {
        level.setShowGeometry(!level.isShowGeometry());
    }

    public static void showHideGraphics() {
        level.setShowGraphics(!level.isShowGraphics());
    }

    public static void showHideGoos() {
        level.setShowGoos(level.getShowGoos() - 1);
        if (level.getShowGoos() < 0) {
            level.setShowGoos(2);
        }
    }

    public static void showHideParticles() {
        level.setShowParticles(!level.isShowParticles());
    }

    public static void showHideLabels() {
        level.setShowLabels(!level.isShowLabels());
    }

    public static void showHideAnim() {
        level.setShowAnimations(!level.isShowAnimations());
    }

    public static void showHideSceneBGColor() {
        level.setShowSceneBGColor(!level.isShowSceneBGColor());
    }

    public static void addAnything(EditorObject obj, EditorObject parent) {
        // obj.setTreeItem(new TreeItem<>(obj));
        // parent.getTreeItem().getChildren().add(obj.getTreeItem());
        hierarchy.scrollTo(hierarchy.getRow(obj.getTreeItem()));
        hierarchy.getSelectionModel().select(hierarchy.getRow(obj.getTreeItem()));
        obj.setLevel(level);
        obj.update();
        setSelected(obj);
        changeTableView(level.getSelected());

        registerChange(new ObjectCreationAction(obj,
                hierarchy.getRow(obj.getTreeItem()) - hierarchy.getRow(obj.getParent().getTreeItem())));
        redoActions.clear();
    }

    /**
     * Changes the id attribute of a BallInstance to give it a unique ID.
     * IDs are given in the form of "goo[number]".
     *
     * @param obj The BallInstance to modify.
     */
    public static void fixGooball(EditorObject obj) {

        // Create an array to store which id numbers are already taken by BallInstances.
        boolean[] taken = new boolean[level.getLevel().size()];

        // Loop over all BallInstances in the level.
        for (EditorObject ball : level.getLevel()) {
            if (ball instanceof BallInstance) {

                // Check if the ball's ID is "goo[number]".
                // If it is, flag that number as already taken.
                String id = ball.getAttribute("id");
                if (id.length() > 3 && id.startsWith("goo")) {
                    try {
                        taken[Integer.parseInt(id.substring(3))] = true;
                    } catch (Exception ignored) {
                    }
                }
            }
        }

        // Find the smallest available number to use as an ID and set the ball's ID
        // attribute accordingly.
        int count = 0;
        while (taken[count]) {
            count++;
        }
        obj.setAttribute("id", "goo" + count);
    }

    /**
     * Changes the id attribute of a text string to give it a unique ID.
     * IDs are given in the form of "TEXT_[level name]_STR[number]".
     *
     * @param obj The string to modify.
     */
    public static void fixString(EditorObject obj) {

        // Create an array to store which id numbers are already taken by strings.
        boolean[] taken = new boolean[level.getText().size()];

        // Loop over all text strings in the level.
        for (EditorObject string : level.getText()) {
            if (string instanceof TextString) {

                // Check if the string's ID is "TEXT_[level name]_STR[number]".
                // If it is, flag that number as already taken.
                String id = string.getAttribute("id");
                if (id.length() > 9 + level.getLevelName().length()
                        && id.startsWith("TEXT_" + level.getLevelName().toUpperCase() + "_STR")) {
                    try {
                        taken[Integer.parseInt(id.substring(9 + level.getLevelName().length()))] = true;
                    } catch (Exception ignored) {
                    }
                }
            }
        }

        // Find the smallest available number to use as an ID and set the string's ID
        // attribute accordingly.
        int count = 0;
        while (taken[count]) {
            count++;
        }
        obj.setAttribute("id", "TEXT_" + level.getLevelName().toUpperCase() + "_STR" + count);
    }

    public static void addBall(EditorObject parent, String type) {
        BallInstance obj = (BallInstance) EditorObject.create("BallInstance", new EditorAttribute[0], parent);
        obj.setAttribute("type", type);
        obj.setAttribute("x", getScreenCenter().getX());
        obj.setAttribute("y", -getScreenCenter().getY());
        fixGooball(obj);
        obj.setRealName("BallInstance");
        level.getLevel().add(obj);
        addAnything(obj, parent);
    }

    public static void addLine(EditorObject parent) {
        Line obj = (Line) EditorObject.create("line", new EditorAttribute[0], parent);
        obj.setAttribute("anchor", getScreenCenter().getX() + "," + getScreenCenter().getY());
        obj.setRealName("line");
        level.getScene().add(obj);
        addAnything(obj, parent);
    }

    public static void addRectangle(EditorObject parent) {
        Rectangle obj = (Rectangle) EditorObject.create("rectangle", new EditorAttribute[0], parent);
        obj.setAttribute("x", getScreenCenter().getX());
        obj.setAttribute("y", -getScreenCenter().getY());
        obj.setAttribute("static", true);
        obj.setRealName("rectangle");
        level.getScene().add(obj);
        addAnything(obj, parent);
    }

    public static void addCircle(EditorObject parent) {
        Circle obj = (Circle) EditorObject.create("circle", new EditorAttribute[0], parent);
        obj.setAttribute("x", getScreenCenter().getX());
        obj.setAttribute("y", -getScreenCenter().getY());
        obj.setRealName("circle");
        obj.setAttribute("static", true);
        level.getScene().add(obj);
        addAnything(obj, parent);
    }

    public static void addSceneLayer(EditorObject parent) {
        SceneLayer obj = (SceneLayer) EditorObject.create("SceneLayer", new EditorAttribute[0], parent);
        obj.setAttribute("x", getScreenCenter().getX());
        obj.setAttribute("y", -getScreenCenter().getY());
        obj.setRealName("SceneLayer");
        level.getScene().add(obj);
        addAnything(obj, parent);
    }

    public static void addCompositegeom(EditorObject parent) {
        Compositegeom obj = (Compositegeom) EditorObject.create("compositegeom", new EditorAttribute[0], parent);
        obj.setAttribute("x", getScreenCenter().getX());
        obj.setAttribute("y", -getScreenCenter().getY());
        obj.setRealName("compositegeom");
        obj.setAttribute("static", true);
        level.getScene().add(obj);
        addAnything(obj, parent);
    }

    public static void addHinge(EditorObject parent) {
        Hinge obj = (Hinge) EditorObject.create("hinge", new EditorAttribute[0], parent);
        obj.setAttribute("anchor", getScreenCenter().getX() + "," + getScreenCenter().getY());
        obj.setRealName("hinge");
        level.getScene().add(obj);
        addAnything(obj, parent);
    }

    public static void autoPipe() {
        // TODO add undo events for the whole pipe

        /* Identify the level exit. If there is none, don't auto pipe. */
        for (EditorObject editorObject : level.getLevel().toArray(new EditorObject[0])) {
            if (editorObject instanceof Levelexit levelexit) {

                /* Calculate the point closest to the scene from the level exit. */
                double distanceToLeft = Math
                        .abs(levelexit.getPosition("pos").getX() - level.getSceneObject().getDouble("minx"));
                double distanceToRight = Math
                        .abs(levelexit.getPosition("pos").getX() - level.getSceneObject().getDouble("maxx"));
                double distanceToTop = Math
                        .abs(levelexit.getPosition("pos").getY() - level.getSceneObject().getDouble("miny"));
                double distanceToBottom = Math
                        .abs(levelexit.getPosition("pos").getY() - level.getSceneObject().getDouble("maxy"));

                Point2D closestPoint;
                if (distanceToLeft <= distanceToRight && distanceToLeft <= distanceToTop
                        && distanceToLeft <= distanceToBottom) {
                    closestPoint = new Point2D(level.getSceneObject().getDouble("minx"),
                            levelexit.getPosition("pos").getY());
                } else if (distanceToRight <= distanceToTop && distanceToRight <= distanceToBottom) {
                    closestPoint = new Point2D(level.getSceneObject().getDouble("maxx"),
                            levelexit.getPosition("pos").getY());
                } else if (distanceToTop <= distanceToBottom) {
                    closestPoint = new Point2D(levelexit.getPosition("pos").getX(),
                            level.getSceneObject().getDouble("miny"));
                } else {
                    closestPoint = new Point2D(levelexit.getPosition("pos").getX(),
                            level.getSceneObject().getDouble("maxy"));
                }

                /* Delete the old pipe. */
                for (EditorObject maybePipe : level.getLevel().toArray(new EditorObject[0])) {
                    if (maybePipe instanceof Pipe) {
                        deleteItem(maybePipe, false);
                    }
                }

                /*
                 * Create a pipe with a vertex at the level exit and at the scene intersection.
                 */
                EditorObject pipe = EditorObject.create("pipe", new EditorAttribute[0], level.getLevelObject());
                EditorObject vertex1 = EditorObject.create("Vertex", new EditorAttribute[0], pipe);
                vertex1.setAttribute("x", levelexit.getPosition("pos").getX());
                vertex1.setAttribute("y", levelexit.getPosition("pos").getY());
                EditorObject vertex2 = EditorObject.create("Vertex", new EditorAttribute[0], pipe);
                vertex2.setAttribute("x", closestPoint.getX());
                vertex2.setAttribute("y", closestPoint.getY());
                ((Vertex) vertex2).setPrevious((Vertex) vertex1);

                level.getLevel().add(pipe);
                level.getLevel().add(vertex1);
                level.getLevel().add(vertex2);

            }
        }
    }

    public static void addPipeVertex(EditorObject parent) {
        // get the previous vertex before this one
        Vertex previous = null;
        for (EditorObject child : parent.getChildren()) {
            if (child instanceof Vertex) {
                previous = (Vertex) child;
            }
        }
        Vertex obj = (Vertex) EditorObject.create("Vertex", new EditorAttribute[0], parent);
        obj.setPrevious(previous);
        obj.setAttribute("x", getScreenCenter().getX());
        obj.setAttribute("y", -getScreenCenter().getY());
        obj.setRealName("Vertex");
        level.getLevel().add(obj);
        addAnything(obj, parent);
    }

    public static void addFire(EditorObject parent) {
        Fire obj = (Fire) EditorObject.create("fire", new EditorAttribute[0], parent);
        obj.setAttribute("x", getScreenCenter().getX());
        obj.setAttribute("y", -getScreenCenter().getY());
        obj.setRealName("fire");
        level.getLevel().add(obj);
        addAnything(obj, parent);
    }

    public static void addLinearForcefield(EditorObject parent) {
        Linearforcefield obj = (Linearforcefield) EditorObject.create("linearforcefield", new EditorAttribute[0],
                parent);
        obj.setAttribute("center", getScreenCenter().getX() + "," + getScreenCenter().getY());
        obj.setRealName("linearforcefield");
        level.getScene().add(obj);
        addAnything(obj, parent);
    }

    public static void addRadialForcefield(EditorObject parent) {
        Radialforcefield obj = (Radialforcefield) EditorObject.create("radialforcefield", new EditorAttribute[0],
                parent);
        obj.setAttribute("center", getScreenCenter().getX() + "," + getScreenCenter().getY());
        obj.setRealName("radialforcefield");
        level.getScene().add(obj);
        addAnything(obj, parent);
    }

    public static void addParticle(EditorObject parent) {
        Particles obj = (Particles) EditorObject.create("particles", new EditorAttribute[0], parent);
        obj.setAttribute("pos", getScreenCenter().getX() + "," + -getScreenCenter().getY());
        obj.setRealName("particles");
        level.getScene().add(obj);
        addAnything(obj, parent);
    }

    public static void addSign(EditorObject parent) {
        Signpost obj = (Signpost) EditorObject.create("signpost", new EditorAttribute[0], parent);
        obj.setAttribute("x", getScreenCenter().getX());
        obj.setAttribute("y", -getScreenCenter().getY());
        obj.setRealName("signpost");
        level.getLevel().add(obj);
        addAnything(obj, parent);
    }

    public static void addString(EditorObject parent) {
        TextString obj = (TextString) EditorObject.create("string", new EditorAttribute[0], parent);
        obj.setAttribute("id", "TEXT_" + level.getLevelName().toUpperCase() + "_STR0");
        obj.setAttribute("text", "");
        fixString(obj);
        obj.setRealName("string");
        level.getText().add(obj);
        addAnything(obj, parent);
    }

    public static void addResrcImage(EditorObject parent) {
        ResrcImage obj = (ResrcImage) EditorObject.create("Image", new EditorAttribute[0], parent);
        obj.setAttribute("id", "IMAGE_SCENE_" + level.getLevelName().toUpperCase() + "_IMG0");
        obj.setAttribute("path", "");
        obj.setAttribute("REALid", "IMAGE_SCENE_" + level.getLevelName().toUpperCase() + "_IMG0");
        obj.setAttribute("REALpath", "");
        obj.setRealName("Image");
        level.getResources().add(obj);
        addAnything(obj, parent);
    }

    public static void addSound(EditorObject parent) {
        Sound obj = (Sound) EditorObject.create("Sound", new EditorAttribute[0], parent);
        obj.setAttribute("id", "SOUND_LEVEL_" + level.getLevelName().toUpperCase() + "_SND0");
        obj.setAttribute("path", "");
        obj.setAttribute("REALid", "SOUND_LEVEL_" + level.getLevelName().toUpperCase() + "_SND0");
        obj.setAttribute("REALpath", "");
        obj.setRealName("Sound");
        level.getResources().add(obj);
        addAnything(obj, parent);
    }

    public static void addSetDefaults(EditorObject parent) {
        SetDefaults obj = (SetDefaults) EditorObject.create("SetDefaults", new EditorAttribute[0], parent);
        obj.setAttribute("path", "./");
        obj.setAttribute("idprefix", "");
        obj.setRealName("SetDefaults");
        level.getLevel().add(obj);
        addAnything(obj, parent);
    }

    public static void addLabel(EditorObject parent) {
        Label obj = (Label) EditorObject.create("label", new EditorAttribute[0], parent);
        obj.setAttribute("x", getScreenCenter().getX());
        obj.setAttribute("y", -getScreenCenter().getY());
        obj.setRealName("label");
        level.getScene().add(obj);
        addAnything(obj, parent);
    }

    public static void addStrand(EditorObject parent) {
        Strand obj = (Strand) EditorObject.create("Strand", new EditorAttribute[0], parent);
        obj.setRealName("Strand");
        level.getLevel().add(obj);
        addAnything(obj, parent);
    }

    public static void addCamera(EditorObject parent) {
        Camera obj = (Camera) EditorObject.create("camera", new EditorAttribute[0], parent);
        obj.setAttribute("endpos", getScreenCenter().getX() + "," + getScreenCenter().getY());
        obj.setRealName("camera");
        level.getLevel().add(obj);
        addAnything(obj, parent);
    }

    public static void addPoi(EditorObject parent) {
        Poi obj = (Poi) EditorObject.create("poi", new EditorAttribute[0], parent);
        obj.setAttribute("pos", getScreenCenter().getX() + "," + getScreenCenter().getY());
        obj.setRealName("poi");
        level.getLevel().add(obj);
        addAnything(obj, parent);
    }

    public static void addMusic(EditorObject parent) {
        Music obj = (Music) EditorObject.create("music", new EditorAttribute[0], parent);
        obj.setRealName("music");
        level.getLevel().add(obj);
        addAnything(obj, parent);
        // possibly rework music logic?
    }

    public static void addLoopsound(EditorObject parent) {
        Loopsound obj = (Loopsound) EditorObject.create("loopsound", new EditorAttribute[0], parent);
        obj.setRealName("loopsound");
        level.getLevel().add(obj);
        addAnything(obj, parent);
    }

    public static void addButton(EditorObject parent) {
        Button obj = (Button) EditorObject.create("button", new EditorAttribute[0], parent);
        obj.setAttribute("x", getScreenCenter().getX());
        obj.setAttribute("y", -getScreenCenter().getY());
        obj.setRealName("button");
        level.getScene().add(obj);
        addAnything(obj, parent);
    }

    public static void addButtongroup(EditorObject parent) {
        Buttongroup obj = (Buttongroup) EditorObject.create("buttongroup", new EditorAttribute[0], parent);
        obj.setRealName("buttongroup");
        level.getScene().add(obj);
        addAnything(obj, parent);
    }

    public static void addMotor(EditorObject parent) {
        Motor obj = (Motor) EditorObject.create("motor", new EditorAttribute[0], parent);
        obj.setRealName("motor");
        level.getScene().add(obj);
        addAnything(obj, parent);
    }

    public static void addSlider(EditorObject parent) {
        Slider obj = (Slider) EditorObject.create("slider", new EditorAttribute[0], parent);
        obj.setRealName("slider");
        level.getScene().add(obj);
        addAnything(obj, parent);
    }

    public static void addEndoncollision(EditorObject parent) {
        Endoncollision obj = (Endoncollision) EditorObject.create("endoncollision", new EditorAttribute[0], parent);
        obj.setRealName("endoncollision");
        level.getLevel().add(obj);
        addAnything(obj, parent);
    }

    public static void addEndonnogeom(EditorObject parent) {
        Endonnogeom obj = (Endonnogeom) EditorObject.create("endonnogeom", new EditorAttribute[0], parent);
        obj.setRealName("endonnogeom");
        level.getLevel().add(obj);
        addAnything(obj, parent);
    }

    public static void addEndonmessage(EditorObject parent) {
        Endonmessage obj = (Endonmessage) EditorObject.create("endonmessage", new EditorAttribute[0], parent);
        obj.setRealName("endonmessage");
        level.getLevel().add(obj);
        addAnything(obj, parent);
    }

    public static void addTargetheight(EditorObject parent) {
        Targetheight obj = (Targetheight) EditorObject.create("targetheight", new EditorAttribute[0], parent);
        obj.setAttribute("pos", getScreenCenter().getX() + ", " + -getScreenCenter().getY());
        level.getLevel().add(obj);
        addAnything(obj, parent);
    }

    public static void addLevelexit(EditorObject parent) {
        Levelexit obj = (Levelexit) EditorObject.create("levelexit", new EditorAttribute[0], parent);
        obj.setAttribute("y", -getScreenCenter().getY());
        level.getLevel().add(obj);
        addAnything(obj, parent);
    }

    public static void addPipe(EditorObject parent) {
        Pipe obj = (Pipe) EditorObject.create("pipe", new EditorAttribute[0], parent);
        obj.setRealName("endonmessage");
        level.getLevel().add(obj);
        addAnything(obj, parent);
    }

    public static void saveBallInVersion(double oldVersion, double newVersion) {
        new BallSelector(oldVersion, newVersion).start(new Stage());
    }

    public static void saveBallInVersion(String ball, double oldVersion, double newVersion) {
        try {
            if (newVersion == 1.3) {
                LevelExporter.exportBallAsXML(FileManager.openBall(ball, oldVersion),
                        FileManager.getOldWOGdir() + "\\res\\balls\\" + ball, 1.3, false);
            } else if (newVersion == 1.5) {
                LevelExporter.exportBallAsXML(FileManager.openBall(ball, oldVersion),
                        FileManager.getNewWOGdir() + "\\res\\balls\\" + ball, 1.5, false);
            }
        } catch (Exception e) {
            Alarms.errorMessage(e);
        }
    }

    public static EditorObject getSelected() {
        return level.getSelected();
    }

    public static void setSelected(EditorObject _selected) {
        level.setSelected(_selected);
        goToSelectedInHierarchy();
    }

    public static EditorObject getMoving() {
        return moving;
    }

    public static void setMoving(EditorObject moving) {
        Main.moving = moving;
    }

    public static int getOldDropIndex() {
        return oldDropIndex;
    }

    public static void setOldDropIndex(int oldDropIndex) {
        Main.oldDropIndex = oldDropIndex;
    }

    private static double mouseX = 0;
    private static double mouseY = 0;

    public static double getMouseX() {
        return mouseX;
    }

    public static double getMouseY() {
        return mouseY;
    }

    private static EditorAttribute[] oldAttributes;
    private static EditorObject oldSelected;

    /**
     * Called whenever the mouse is pressed.
     */
    public static void eventMousePressed(MouseEvent event) {

        if (level != null) {

            double editorViewWidth = splitPane.getDividerPositions()[0] * splitPane.getWidth();

            if (event.getButton().equals(MouseButton.PRIMARY)) {

                if (level.getSelected() != null) {
                    if (propertiesView.getEditingCell() == null
                            || propertiesView.getFocusModel().focusedIndexProperty().get() == -1) {
                        propertiesView.edit(-1, null);
                    }
                    oldAttributes = level.getSelected().cloneAttributes();
                    oldSelected = level.getSelected();
                }

                if (mode == SELECTION) {
                    if (event.getX() < editorViewWidth && event.getY() > getMouseYOffset()) {
                        if (level.getSelected() != null) {
                            dragSettings = level.getSelected().mouseIntersectingCorners(
                                    (event.getX() - level.getOffsetX()) / level.getZoom(),
                                    (event.getY() - getMouseYOffset() - level.getOffsetY()) / level.getZoom());

                            /* Dragging of already selected object that might be behind something else */
                            DragSettings thisSettings = level.getSelected().mouseIntersection(
                                    (event.getX() - level.getOffsetX()) / level.getZoom(),
                                    (event.getY() - getMouseYOffset() - level.getOffsetY()) / level.getZoom());
                            DragSettings thisImageSettings = level.getSelected().mouseImageIntersection(
                                    (event.getX() - level.getOffsetX()) / level.getZoom(),
                                    (event.getY() - getMouseYOffset() - level.getOffsetY()) / level.getZoom());
                            if (thisSettings.getType() != DragSettings.NONE) {
                                scene.setCursor(Cursor.MOVE);
                                dragSettings = thisSettings;
                            } else if (thisImageSettings.getType() != DragSettings.NONE) {
                                scene.setCursor(Cursor.MOVE);
                                dragSettings = thisImageSettings;
                            }

                        }
                        if (dragSettings.getType() == DragSettings.NONE) {

                            EditorObject prevSelected = level.getSelected();
                            setSelected(null);
                            ArrayList<EditorObject> byDepth = Renderer.orderObjectsBySelectionDepth(level);
                            for (int i = byDepth.size() - 1; i >= 0; i--) {
                                EditorObject object = byDepth.get(i);
                                if (object.mouseIntersection((event.getX() - level.getOffsetX()) / level.getZoom(),
                                        (event.getY() - getMouseYOffset() - level.getOffsetY()) / level.getZoom())
                                        .getType() != DragSettings.NONE) {
                                    changeTableView(object);
                                    setSelected(object);
                                    object.getParent().getTreeItem().setExpanded(true);
                                    hierarchy.getSelectionModel().select(object.getTreeItem());
                                    // Scroll to this position in the selection model
                                    hierarchy.scrollTo(hierarchy.getRow(object.getTreeItem()));
                                    break;
                                    // } else if ((object instanceof Circle || object instanceof Rectangle || object
                                    // instanceof Compositegeom || object instanceof Signpost) &&
                                    // object.getAttribute("image").length() > 0) {
                                } else if (object.mouseImageIntersection(
                                        (event.getX() - level.getOffsetX()) / level.getZoom(),
                                        (event.getY() - getMouseYOffset() - level.getOffsetY()) / level.getZoom())
                                        .getType() != DragSettings.NONE) {
                                    changeTableView(object);
                                    setSelected(object);
                                    object.getParent().getTreeItem().setExpanded(true);
                                    hierarchy.getSelectionModel().select(object.getTreeItem());
                                    break;
                                }
                            }
                            if (level.getSelected() != null && level.getSelected() == prevSelected) {
                                DragSettings thisSettings = level.getSelected().mouseIntersection(
                                        (event.getX() - level.getOffsetX()) / level.getZoom(),
                                        (event.getY() - getMouseYOffset() - level.getOffsetY()) / level.getZoom());
                                DragSettings thisImageSettings = level.getSelected().mouseImageIntersection(
                                        (event.getX() - level.getOffsetX()) / level.getZoom(),
                                        (event.getY() - getMouseYOffset() - level.getOffsetY()) / level.getZoom());
                                if (thisSettings.getType() != DragSettings.NONE) {
                                    scene.setCursor(Cursor.MOVE);
                                    dragSettings = thisSettings;
                                } else if (thisImageSettings.getType() != DragSettings.NONE) {
                                    scene.setCursor(Cursor.MOVE);
                                    dragSettings = thisImageSettings;
                                }
                            }
                        }
                    }
                } else if (mode == STRAND) {
                    for (EditorObject ball : level.getLevel().toArray(new EditorObject[0])) {
                        if (ball instanceof BallInstance && ball
                                .mouseIntersection((event.getX() - level.getOffsetX()) / level.getZoom(),
                                        (event.getY() - getMouseYOffset() - level.getOffsetY()) / level.getZoom())
                                .getType() != DragSettings.NONE) {
                            if (strand1Gooball == null) {
                                strand1Gooball = ball;
                                break;
                            }
                        }
                    }
                }
            } else if (event.getButton() == MouseButton.SECONDARY) {
                // Mouse pan with right-click
                if (event.getX() < editorViewWidth && event.getY() > getMouseYOffset()) {
                    scene.setCursor(Cursor.CLOSED_HAND);
                    mouseStartX = event.getScreenX();
                    mouseStartY = event.getScreenY();
                }
            }
        }
    }

    /**
     * Called whenever the mouse is dragged.
     */
    public static void eventMouseDragged(MouseEvent event) {
        mouseX = event.getX();
        mouseY = event.getY() - getMouseYOffset();
        if (level != null && level.getSelected() != null && dragSettings != null) {

            // Calculate game-relative mouse coordinates.
            double gameRelativeMouseX = (mouseX - level.getOffsetX()) / level.getZoom();
            double gameRelativeMouseY = (mouseY - level.getOffsetY()) / level.getZoom();

            // Update the selected object according to what kind of operation is being
            // performed.
            switch (dragSettings.getType()) {
                case DragSettings.MOVE -> level.getSelected().dragFromMouse(gameRelativeMouseX, gameRelativeMouseY,
                        dragSettings.getInitialSourceX(), dragSettings.getInitialSourceY());
                case DragSettings.RESIZE -> level.getSelected().resizeFromMouse(gameRelativeMouseX, gameRelativeMouseY,
                        dragSettings.getInitialSourceX(), dragSettings.getInitialSourceY(), dragSettings.getAnchorX(),
                        dragSettings.getAnchorY());
                case DragSettings.ROTATE -> level.getSelected().rotateFromMouse(gameRelativeMouseX, gameRelativeMouseY,
                        dragSettings.getRotateAngleOffset());
                case DragSettings.SETANCHOR -> level.getSelected().setAnchor(gameRelativeMouseX, gameRelativeMouseY,
                        dragSettings.getInitialSourceX(), dragSettings.getInitialSourceY());
            }

            propertiesView.refresh();
        }
        if (level != null && event.getButton() == MouseButton.SECONDARY) {
            // Pan the canvas according to the mouse's movement.
            level.setOffsetY(level.getOffsetY() + event.getScreenY() - mouseStartY);
            level.setOffsetX(level.getOffsetX() + event.getScreenX() - mouseStartX);
            mouseStartX = event.getScreenX();
            mouseStartY = event.getScreenY();

            // Apply the transformation to the canvas.
            t = new Affine();
            t.appendTranslation(level.getOffsetX(), level.getOffsetY());
            t.appendScale(level.getZoom(), level.getZoom());
            imageCanvas.getGraphicsContext2D().setTransform(t);

            // Redraw the canvas.
            Renderer.drawEverything(level, canvas, imageCanvas);
        }
    }

    /**
     * Called whenever the mouse is moved.
     */
    public static void eventMouseMoved(MouseEvent event) {
        if (level != null) {
            mouseX = event.getX();
            mouseY = event.getY() - getMouseYOffset();
            if (level.getSelected() != null) {
                DragSettings hit = level.getSelected().mouseIntersectingCorners(
                        (event.getX() - level.getOffsetX()) / level.getZoom(),
                        (event.getY() - getMouseYOffset() - level.getOffsetY()) / level.getZoom());
                switch (hit.getType()) {
                    case DragSettings.NONE -> scene.setCursor(Cursor.DEFAULT);
                    case DragSettings.RESIZE -> scene.setCursor(Cursor.NE_RESIZE);
                    case DragSettings.ROTATE, DragSettings.SETANCHOR -> scene.setCursor(Cursor.OPEN_HAND);
                }
            }
        }
    }

    private static DragSettings dragSettings = new DragSettings(DragSettings.NONE);

    public static DragSettings getDragSettings() {
        return dragSettings;
    }

    /**
     * Called whenever the mouse is released.
     */
    public static void eventMouseReleased(MouseEvent event) {
        // If the mouse was released inside the editor window:
        if (event.getButton() == MouseButton.PRIMARY
                && event.getX() < splitPane.getDividerPositions()[0] * splitPane.getWidth() && level != null) {
            // Record the changes made to the selected object.
            // Clear all possible redos if changes have been made.
            if (level.getSelected() != null && level.getSelected() == oldSelected && oldAttributes != null) {
                UserAction[] changes = level.getSelected().getUserActions(oldAttributes);
                if (changes.length > 0) {
                    registerChange(changes);
                    redoActions.clear();
                }
            }

            // Reset the cursor's appearance.
            scene.setCursor(Cursor.DEFAULT);

            // Clear all drag settings now that the mouse has been released.
            dragSettings = new DragSettings(DragSettings.NONE);
            // If we have started placing a strand, attempt to complete the strand.
            if (mode == STRAND && strand1Gooball != null) {
                for (EditorObject ball : level.getLevel().toArray(new EditorObject[0])) {
                    if (ball != strand1Gooball) {
                        if (ball instanceof BallInstance && ball
                                .mouseIntersection((event.getX() - level.getOffsetX()) / level.getZoom(),
                                        (event.getY() - getMouseYOffset() - level.getOffsetY()) / level.getZoom())
                                .getType() != DragSettings.NONE) {

                            EditorObject strand = EditorObject.create("Strand", new EditorAttribute[0],
                                    level.getLevelObject());

                            strand.setAttribute("gb1", strand1Gooball.getAttribute("id"));
                            strand.setAttribute("gb2", ball.getAttribute("id"));

                            strand.setRealName("Strand");

                            level.getLevel().add(strand);
                            addAnything(strand, level.getLevelObject());
                            break;
                        }
                    }
                }
                strand1Gooball = null;
            }
        } else if (event.getButton() == MouseButton.SECONDARY) {
            scene.setCursor(Cursor.DEFAULT);
        }
    }

    /**
     * Called whenever the mouse wheel is scrolled.
     */
    public static void mouse_wheel_moved(ScrollEvent e) {
        // If the mouse was scrolled inside the editor window:
        if (e.getX() < splitPane.getDividerPositions()[0] * splitPane.getWidth() && level != null) {

            // Calculate the new translation and scale.
            double amt = Math.pow(1.25, (e.getDeltaY() / 40));

            double oldTranslateX = level.getOffsetX();
            double oldTranslateY = level.getOffsetY();

            double oldScaleX = level.getZoom();
            double oldScaleY = level.getZoom();

            double mouseX = e.getX();
            double mouseY = e.getY() - getMouseYOffset();

            if (oldScaleX * amt > 0.001 && oldScaleX * amt < 1000 && oldScaleY * amt > 0.001
                    && oldScaleY * amt < 1000) {

                double newScaleX = oldScaleX * amt;
                double newScaleY = oldScaleY * amt;

                double newTranslateX = (int) ((oldTranslateX - mouseX) * amt + mouseX);
                double newTranslateY = (int) ((oldTranslateY - mouseY) * amt + mouseY);

                // Transform the canvas according to the updated translation and scale.
                t = new Affine();
                t.appendTranslation(newTranslateX, newTranslateY);
                t.appendScale(newScaleX, newScaleY);
                imageCanvas.getGraphicsContext2D().setTransform(t);

                level.setOffsetX(newTranslateX);
                level.setOffsetY(newTranslateY);
                level.setZoom(newScaleX);

                if (level.getSelected() != null) {
                    DragSettings hit = level.getSelected().mouseIntersectingCorners(mouseX, mouseY);
                    switch (hit.getType()) {
                        case DragSettings.NONE -> scene.setCursor(Cursor.DEFAULT);
                        case DragSettings.RESIZE -> scene.setCursor(Cursor.NE_RESIZE);
                        case DragSettings.ROTATE -> scene.setCursor(Cursor.OPEN_HAND);
                    }

                    // Calculate game-relative mouse coordinates.
                    double gameRelativeMouseX = (mouseX - newTranslateX) / newScaleX;
                    double gameRelativeMouseY = (mouseY - newTranslateY) / newScaleX;

                    // Update the selected object according to what kind of operation is being
                    // performed.
                    switch (dragSettings.getType()) {
                        case DragSettings.MOVE -> level.getSelected().dragFromMouse(gameRelativeMouseX,
                                gameRelativeMouseY, dragSettings.getInitialSourceX(), dragSettings.getInitialSourceY());
                        case DragSettings.RESIZE -> level.getSelected().resizeFromMouse(gameRelativeMouseX,
                                gameRelativeMouseY, dragSettings.getInitialSourceX(), dragSettings.getInitialSourceY(),
                                dragSettings.getAnchorX(), dragSettings.getAnchorY());
                        case DragSettings.ROTATE -> level.getSelected().rotateFromMouse(gameRelativeMouseX,
                                gameRelativeMouseY, dragSettings.getRotateAngleOffset());
                    }

                    propertiesView.refresh();
                }

                // Redraw the canvas.
                Renderer.drawEverything(level, canvas, imageCanvas);
            }
        }

    }

    public static void keyPressed(KeyEvent event) {
        if (event.getCode() == KeyCode.CONTROL) {
            CTRL = true;
        }
        if (event.getCode() == KeyCode.SHIFT) {
            SHIFT = true;
        }
        if (event.getCode() == KeyCode.DELETE) {
            delete();
        }
        if (CTRL) {
            if (event.getCode() == KeyCode.S) {
                saveLevel();
            }
            if (event.getCode() == KeyCode.Z) {
                undo();
            }
            if (event.getCode() == KeyCode.X) {
                cut();
            }
            if (event.getCode() == KeyCode.C) {
                copy();
            }
            if (event.getCode() == KeyCode.V) {
                paste();
            }
        }
    }

    public static void keyReleased(KeyEvent event) {
        if (event.getCode() == KeyCode.CONTROL) {
            CTRL = false;
        }
        if (event.getCode() == KeyCode.SHIFT) {
            SHIFT = false;
        }
    }

    public static Stage getStage() {
        return stage;
    }

    private static Canvas canvas;
    private static Canvas imageCanvas;
    private static WorldLevel level = null;

    private static final ArrayList<_Ball> importedBalls = new ArrayList<>();

    private static final ArrayList<WoGAnimation> animations = new ArrayList<>();;

    private static SplitPane splitPane;

    private static TreeTableView<EditorAttribute> propertiesView;

    private static Stage stage;

    private static Scene scene;

    private static EditorObject moving;
    private static int oldDropIndex;

    private static Pane thingPane;

    private static boolean SHIFT;
    private static boolean CTRL;

    private static double mouseStartX;
    private static double mouseStartY;

    public static WorldLevel getLevel() {
        return level;
    }

    public static void setLevel(WorldLevel _level) {
        level = _level;
        enableAllButtons(false);

        // Transform the canvas according to the updated translation and scale.
        t = new Affine();
        t.appendTranslation(level.getOffsetX(), level.getOffsetY());
        t.appendScale(level.getZoom(), level.getZoom());
        imageCanvas.getGraphicsContext2D().setTransform(t);
        onSetLevel();
        draw();
    }

    public static void onSetLevel() {
        vBox.getChildren().remove(2);
        if (level == null) {
            stage.setTitle("World of Goo Anniversary Editor");
            vBox.getChildren().add(2, FXCreator.getNullGooballsToolbar());
        } else {
            if (level.getVersion() == 1.3) {
                vBox.getChildren().add(2, FXCreator.getOldGooballsToolbar());
            } else {
                vBox.getChildren().add(2, FXCreator.getNewGooballsToolbar());
            }
            stage.setTitle(
                    level.getLevelName() + " (version " + level.getVersion() + ") — World of Goo Anniversary Editor");
            FXCreator.buttonShowHideAnim.setGraphic(
                    new ImageView(level.isShowAnimations() ? WorldLevel.showHideAnim : WorldLevel.showHideAnim0));
            FXCreator.buttonShowHideCamera.setGraphic(
                    new ImageView(level.isShowCameras() ? WorldLevel.showHideCam1 : WorldLevel.showHideCam0));
            FXCreator.buttonShowHideForcefields.setGraphic(new ImageView(
                    level.isShowForcefields() ? WorldLevel.showHideForcefields1 : WorldLevel.showHideForcefields0));
            FXCreator.buttonShowHideGeometry.setGraphic(new ImageView(
                    level.isShowGeometry() ? WorldLevel.showHideGeometry1 : WorldLevel.showHideGeometry0));
            switch (level.getShowGoos()) {
                case 0 -> FXCreator.buttonShowHideGoos.setGraphic(new ImageView(WorldLevel.showHideGoobs0));
                case 1 -> FXCreator.buttonShowHideGoos.setGraphic(new ImageView(WorldLevel.showHideGoobs1));
                case 2 -> FXCreator.buttonShowHideGoos.setGraphic(new ImageView(WorldLevel.showHideGoobs2));
            }
            FXCreator.buttonShowHideGraphics.setGraphic(
                    new ImageView(level.isShowGraphics() ? WorldLevel.showHideImages1 : WorldLevel.showHideImages0));
            FXCreator.buttonShowHideLabels.setGraphic(
                    new ImageView(level.isShowLabels() ? WorldLevel.showHideLabels1 : WorldLevel.showHideLabels0));
            FXCreator.buttonShowHideParticles.setGraphic(new ImageView(
                    level.isShowParticles() ? WorldLevel.showHideParticles1 : WorldLevel.showHideParticles0));
            FXCreator.buttonShowHideSceneBGColor.setGraphic(new ImageView(
                    level.isShowSceneBGColor() ? WorldLevel.showHideSceneBGColor1 : WorldLevel.showHideSceneBGColor0));
            changeTableView(level.getSelected());
            goToSelectedInHierarchy();
        }
    }

    public static void goToSelectedInHierarchy() {
        if (level.getSelected() != null && level.getSelected().getParent() != null) {
            if (level.getSelected().getAbsoluteParent() instanceof ResourceManifest) {
                hierarchy.setRoot(level.getSelected().getAbsoluteParent().getChildren().get(0).getTreeItem());
                hierarchy.setShowRoot(true);
            } else if (level.getSelected().getAbsoluteParent() instanceof TextStrings) {
                hierarchy.setRoot(level.getSelected().getAbsoluteParent().getTreeItem());
                hierarchy.setShowRoot(true);
            } else {
                hierarchy.setRoot(level.getSelected().getAbsoluteParent().getTreeItem());
                hierarchy.setShowRoot(true);
            }

            switch (level.getSelected().getClass().getPackage().getName()) {
                case "com.WorldOfGoo.Scene" -> Main.hierarchySwitcherButtons.getSelectionModel().select(0);
                case "com.WorldOfGoo.Level" -> Main.hierarchySwitcherButtons.getSelectionModel().select(1);
                case "com.WorldOfGoo.Resrc" -> Main.hierarchySwitcherButtons.getSelectionModel().select(2);
                case "com.WorldOfGoo.Text" -> Main.hierarchySwitcherButtons.getSelectionModel().select(3);
                case "com.WorldOfGoo.Addin" -> Main.hierarchySwitcherButtons.getSelectionModel().select(4);
            }
        }
    }

    public static TabPane getLevelSelectPane() {
        return levelSelectPane;
    }

    public static void changeTableView(EditorObject obj) {
        if (obj == null) {
            propertiesView.setRoot(null);
        } else {
            propertiesView.setRoot(obj.getPropertiesTreeItem());
        }
    }

    public static VBox viewPane;

    public static double lerp(double a, double b, double c) {
        return a + (b - a) * c;
    }

    public static Point2D lineBoxIntersection(double x1, double y1, double theta, double x2, double y2, double sizeX,
            double sizeY, double rotation) {

        Point2D topLeft = EditorObject.rotate(new Point2D(x2 - sizeX / 2, y2 - sizeY / 2), -rotation,
                new Point2D(x2, y2));
        Point2D topRight = EditorObject.rotate(new Point2D(x2 + sizeX / 2, y2 - sizeY / 2), -rotation,
                new Point2D(x2, y2));
        Point2D bottomLeft = EditorObject.rotate(new Point2D(x2 - sizeX / 2, y2 + sizeY / 2), -rotation,
                new Point2D(x2, y2));
        Point2D bottomRight = EditorObject.rotate(new Point2D(x2 + sizeX / 2, y2 + sizeY / 2), -rotation,
                new Point2D(x2, y2));

        Point2D top = lineLineSegmentIntersection(x1, y1, -theta, topLeft.getX(), topLeft.getY(), topRight.getX(),
                topRight.getY());
        Point2D bottom = lineLineSegmentIntersection(x1, y1, -theta, bottomLeft.getX(), bottomLeft.getY(),
                bottomRight.getX(), bottomRight.getY());
        Point2D left = lineLineSegmentIntersection(x1, y1, -theta, topLeft.getX(), topLeft.getY(), bottomLeft.getX(),
                bottomLeft.getY());
        Point2D right = lineLineSegmentIntersection(x1, y1, -theta, topRight.getX(), topRight.getY(),
                bottomRight.getX(), bottomRight.getY());

        Point2D origin = new Point2D(x1, y1);

        double topDistance = top == null ? 100000000 : top.distance(origin);
        double bottomDistance = bottom == null ? 100000000 : bottom.distance(origin);
        double leftDistance = left == null ? 100000000 : left.distance(origin);
        double rightDistance = right == null ? 100000000 : right.distance(origin);

        if (topDistance < bottomDistance && topDistance < leftDistance && topDistance < rightDistance) {
            return top;
        }

        if (bottomDistance < leftDistance && bottomDistance < rightDistance) {
            return bottom;
        }

        if (leftDistance < rightDistance) {
            return left;
        }

        if (right == null) {
            return new Point2D(0, 0);
        }
        return right;
    }

    public static Point2D lineLineSegmentIntersection(double x1, double y1, double theta, double x2, double y2,
            double x3, double y3) {
        // System.out.println(x1 + ", " + y1 + ", " + theta + ", " + x2 + ", " + y2 + ",
        // " + x3 + ", " + y3);
        if (y3 == y2) {
            y3 += 0.00001;
        }
        if (x3 == x2) {
            x3 += 0.00001;
        }
        double m = (y3 - y2) / (x3 - x2);
        double x = (y2 - x2 * m + x1 * Math.tan(theta) - y1) / (Math.tan(theta) - m);
        double y = (x - x1) * Math.tan(theta) + y1;

        double bruh = 0.01;
        // System.out.println(x + ", " + y);
        // System.out.println(y + ", " + ((x - x2) * m + y2));
        // 385.94690307546693, 682.9469030754669
        if (x > Math.min(x2, x3) - bruh && x < Math.max(x2, x3) + bruh && y > Math.min(y2, y3) - bruh
                && y < Math.max(y2, y3) + bruh) {
            // System.out.println("e");
            return new Point2D(x, y);
        } else {
            return null;
        }
    }

    public static ArrayList<_Ball> getImportedBalls() {
        return importedBalls;
    }

    public static void updateAnimations(float timeElapsed) {
        if (level != null) {
            for (EditorObject object : level.getScene()) {
                if (object instanceof SceneLayer) {
                    String anim = object.getAttribute("anim");
                    if (!anim.equals("")) {
                        for (WoGAnimation animation : animations) {
                            if (animation.getName().equals(anim + ".anim.binuni")
                                    || animation.getName().equals(anim + ".anim.binltl")) {
                                object.updateWithAnimation(animation, timeElapsed);
                            }
                        }
                    }
                }
            }
        }
    }

    public static boolean hasAnimation(String potential) {
        for (WoGAnimation animation : animations) {
            if (animation.getName().equals(potential + ".anim.binuni")
                    || animation.getName().equals(potential + ".anim.binltl")) {
                return true;
            }
        }
        return false;
    }

    public static TreeTableView<EditorObject> getHierarchy() {
        return hierarchy;
    }

    public static void draw() {

        if (level != null) {
            if (level.isShowSceneBGColor()) {
                imageCanvas.getGraphicsContext2D()
                        .setFill(Paint.valueOf(level.getSceneObject().getColor("backgroundcolor").toHexRGBA()));
                imageCanvas.getGraphicsContext2D().fillRect(-5000000, -5000000, 10000000, 10000000);
            } else {
                imageCanvas.getGraphicsContext2D().clearRect(-5000000, -5000000, 10000000, 10000000);
            }
            canvas.getGraphicsContext2D().clearRect(-5000000, -5000000, 10000000, 10000000);
            Renderer.drawEverything(level, canvas, imageCanvas);
        } else {
            Renderer.clear(level, canvas, imageCanvas);
        }
    }

    private static final ArrayList<EditorObject> particles = new ArrayList<>();

    public static ArrayList<EditorObject> getParticles() {
        return particles;
    }

    // TODO okay somehow a scenelayer got stuck to my cursor, fix that ok thanks

    public static EditorObject generateBlankAddinObject(String levelName) {
        EditorObject addin = EditorObject.create("Addin_addin", new EditorAttribute[0], null);
        EditorObject.create("Addin_id", new EditorAttribute[0], addin);
        EditorObject.create("Addin_name", new EditorAttribute[0], addin);
        EditorObject.create("Addin_type", new EditorAttribute[0], addin);
        EditorObject.create("Addin_version", new EditorAttribute[0], addin);
        EditorObject.create("Addin_description", new EditorAttribute[0], addin);
        EditorObject.create("Addin_author", new EditorAttribute[0], addin);
        EditorObject levels = EditorObject.create("Addin_levels", new EditorAttribute[0], addin);
        EditorObject level = EditorObject.create("Addin_level", new EditorAttribute[0], levels);
        EditorObject addinLevelDir = EditorObject.create("Addin_dir", new EditorAttribute[0], level);
        EditorObject addinLevelName = EditorObject.create("Addin_wtf_name", new EditorAttribute[0], level);
        EditorObject.create("Addin_subtitle", new EditorAttribute[0], level);
        EditorObject.create("Addin_ocd", new EditorAttribute[0], level);

        addinLevelDir.setAttribute("value", levelName);

        return addin;
    }

    public static EditorObject generateBlankTextObject() {
        return EditorObject.create("strings", new EditorAttribute[0], null);
    }

    private static VBox vBox;

    public static TreeTableView<EditorObject> hierarchy;

    private final static Stack<UserAction[]> userActions = new Stack<>();

    private static void importGameResources(double version) {
        ArrayList<String> allFailedResources = new ArrayList<>();

        // Open resources.xml for 1.3
        // This takes forever to finish
        if (version == 1.3 && FileManager.isHasOldWOG()) {
            try {
                ArrayList<EditorObject> resources = FileManager.openResources(1.3);
                if (resources != null) {
                    for (EditorObject resrc : resources) {
                        if (resrc instanceof ResrcImage) {
                            GlobalResourceManager.addResource(resrc, 1.3);
                        }
                    }
                }
            } catch (ParserConfigurationException | SAXException | IOException e) {
                Alarms.errorMessage(e);
            }
        }

        // Open resources.xml for 1.5
        // This happens instantly
        if (version == 1.5 && FileManager.isHasNewWOG()) {
            try {
                ArrayList<EditorObject> resources = FileManager.openResources(1.5);
                if (resources != null) {
                    for (EditorObject resrc : resources) {
                        if (resrc instanceof ResrcImage) {
                            GlobalResourceManager.addResource(resrc, 1.5);
                        }
                    }
                }
            } catch (ParserConfigurationException | SAXException | IOException e) {
                Alarms.errorMessage(e);
            }
        }

        if (version == 1.3 && FileManager.isHasOldWOG()) {
            try {
                FileManager.openParticles(1.3);
                ArrayList<EditorObject> particles2 = FileManager.commonBallData;
                particles.addAll(particles2);
                for (EditorObject particle : particles2) {
                    try {
                        if (particle instanceof _Particle) {
                            ((_Particle) particle).update(1.3);
                        } else {
                            particle.update();
                        }
                    } catch (Exception e) {
                        allFailedResources
                                .add("Particle: " + particle.getParent().getAttribute("name") + " (version 1.3)");
                    }
                }
            } catch (ParserConfigurationException | SAXException | IOException e) {
                Alarms.errorMessage(e);
            }
        }

        if (version == 1.5 && FileManager.isHasNewWOG()) {
            try {
                FileManager.openParticles(1.5);
                ArrayList<EditorObject> particles2 = FileManager.commonBallData;
                particles.addAll(particles2);
                for (EditorObject particle : particles2) {
                    try {
                        if (particle instanceof _Particle) {
                            ((_Particle) particle).update(1.5);
                        } else {
                            particle.update();
                        }
                    } catch (Exception e) {
                        allFailedResources
                                .add("Particle: " + particle.getParent().getAttribute("name") + " (version 1.5)");
                    }
                }
            } catch (ParserConfigurationException | SAXException | IOException e) {
                Alarms.errorMessage(e);
            }
        }

        // Load all animations from the game files
        try {
            if (version == 1.3 && FileManager.isHasOldWOG()) {
                File bruh1 = new File(FileManager.getOldWOGdir() + "\\res\\anim");
                File[] animationsArray = bruh1.listFiles();
                if (animationsArray != null) {
                    for (File second : animationsArray) {
                        if (!second.getName().substring(second.getName().lastIndexOf(".")).equals(".binltl64")) {
                            try (FileInputStream test2 = new FileInputStream(second)) {
                                byte[] allBytes = test2.readAllBytes();
                                animations.add(AnimationReader.readBinltl(allBytes, second.getName()));
                            } catch (Exception e) {
                                allFailedResources.add("Animation: " + second.getName() + " (version 1.3)");
                            }
                        }
                    }
                }
            }
            if (version == 1.5 && FileManager.isHasNewWOG()) {
                File bruh2 = new File(FileManager.getNewWOGdir() + "\\res\\anim");
                File[] animationsArray = bruh2.listFiles();
                if (animationsArray != null) {
                    for (File second : animationsArray) {
                        try (FileInputStream test2 = new FileInputStream(second)) {
                            byte[] allBytes = test2.readAllBytes();
                            animations.add(AnimationReader.readBinuni(allBytes, second.getName()));
                        } catch (Exception e) {
                            allFailedResources.add("Animation: " + second.getName() + " (version 1.5)");
                        }
                    }
                }
            }
        } catch (Exception e) {
            Alarms.errorMessage(e);
        }

        if (version == 1.3 && FileManager.isHasOldWOG()) {
            try {
                ArrayList<EditorObject> textList = FileManager.openText(1.3);
                if (textList != null) {
                    for (EditorObject text : textList) {
                        if (text instanceof TextString) {
                            GlobalResourceManager.addResource(text, 1.3);
                        }
                    }
                }
            } catch (ParserConfigurationException | SAXException | IOException e) {
                Alarms.errorMessage(e);
            }
        }
        if (version == 1.5 && FileManager.isHasNewWOG()) {
            try {
                ArrayList<EditorObject> textList = FileManager.openText(1.5);
                if (textList != null) {
                    for (EditorObject text : textList) {
                        if (text instanceof TextString) {
                            GlobalResourceManager.addResource(text, 1.5);
                        }
                    }
                }
            } catch (ParserConfigurationException | SAXException | IOException e) {
                Alarms.errorMessage(e);
            }
        }

        if (allFailedResources.size() > 0) {
            StringBuilder fullError = new StringBuilder();
            for (String resource : allFailedResources) {
                fullError.append("\n").append(resource);
            }
            Alarms.loadingInitialResourcesError(fullError.toString().substring(1));
        }
    }

    public static SplitPane getSplitPane() {
        return splitPane;
    }

    @Override
    public void start(Stage stage2) {

        System.out.println(this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath());

        stage = stage2;

        try {
            FileManager.readWOGdirs();

            /* Check that the world of goo directory from properties.txt actually points to a file */
            /* This might require users to reset their WoG directory */
            if (!FileManager.getOldWOGdir().equals("") && !Files.exists(Path.of(FileManager.getOldWOGdir()))) {
                FileManager.setOldWOGdir("");
            }
            if (!FileManager.getNewWOGdir().equals("") && !Files.exists(Path.of(FileManager.getNewWOGdir()))) {
                FileManager.setNewWOGdir("");
            }

            if (FileManager.getOldWOGdir().equals("") && FileManager.getNewWOGdir().equals("")) {
                Alarms.missingWOG();
            } else {
                startWithWorldOfGooVersion();
            }
        } catch (ParserConfigurationException | SAXException | IOException e) {
            Alarms.errorMessage(e);
        }
    }

    public static final TabPane hierarchySwitcherButtons = FXCreator.hierarchySwitcherButtons();

    public static void startWithWorldOfGooVersion() {
        System.out.println("1.3 = " + FileManager.getOldWOGdir());
        System.out.println("1.5 = " + FileManager.getNewWOGdir());

        levelSelectPane = new TabPane();

        // Initialize stage name/icon
        stage.setTitle("World of Goo Anniversary Editor");

        stage.setScene(new Scene(new Pane()));
        stage.show();
        try {
            stage.getIcons().add(FileManager.getIcon("ButtonIcons\\icon.png"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Make menu that currently does nothing
        try {
            MenuBar bar = FXCreator.createMenu();

            // Import all goo balls and all misc resources from the game files

            if (!FileManager.getOldWOGdir().equals("")) {
                importGameResources(1.3);
            }
            if (!FileManager.getNewWOGdir().equals("")) {
                importGameResources(1.5);
            }

            // System.out.println(FileManager.isHasOldWOG());

            for (int i = 0; i < FileManager.getPaletteBalls().size(); i++) {

                String ballName = FileManager.getPaletteBalls().get(i);
                double ballVersion = FileManager.getPaletteVersions().get(i);

                try {
                    _Ball ball = FileManager.openBall(ballName, ballVersion);

                    for (EditorObject resrc : FileManager.commonBallResrcData) {
                        GlobalResourceManager.addResource(resrc, ballVersion);
                    }

                    if (ball != null) {
                        ball.makeImages(ballVersion);
                        ball.setVersion(ballVersion);
                        importedBalls.add(ball);
                    }
                } catch (ParserConfigurationException | SAXException | IOException e) {
                    Alarms.errorMessage(e);
                }

            }

            // Initialize both canvasses
            canvas = new Canvas();
            canvas.setWidth(1400);
            canvas.setHeight(1100);

            imageCanvas = new Canvas();
            imageCanvas.setWidth(1400);
            imageCanvas.setHeight(1100);

            // Ask FXCreator for both TreeTableViews
            hierarchy = FXCreator.makeHierarchy();
            propertiesView = FXCreator.makePropertiesView();

            // Configure PropertiesView
            propertiesView.prefWidthProperty().bind(hierarchy.widthProperty());
            propertiesView.setRoot(
                    new TreeItem<>(new EditorAttribute(null, "", "", "", new InputField("", InputField.ANY), false)));

            // Combine everything weirdly
            splitPane = new SplitPane();
            thingPane = new Pane(imageCanvas);
            StackPane pane = new StackPane(thingPane, new Pane(canvas));
            Separator separator = new Separator();
            viewPane = new VBox(hierarchy, separator, hierarchySwitcherButtons, propertiesView);
            separator.hoverProperty().addListener(new ChangeListener<Boolean>() {
                @Override
                public void changed(ObservableValue<? extends Boolean> observableValue, Boolean aBoolean, Boolean t1) {
                    if (t1) {
                        scene.setCursor(Cursor.N_RESIZE);
                    } else {
                        scene.setCursor(Cursor.DEFAULT);
                    }
                }
            });

            separator.setOnMouseDragged(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    double height = mouseY + getMouseYOffset() - vBox.getChildren().get(4).getLayoutY() - 2;
                    hierarchy.setMinHeight(height);
                    hierarchy.setMaxHeight(height);
                    hierarchy.setPrefHeight(height);
                }
            });
            splitPane.getItems().addAll(new VBox(levelSelectPane, pane), viewPane);
            stage.addEventFilter(ScrollEvent.SCROLL, Main::mouse_wheel_moved);

            levelSelectPane.getSelectionModel().selectedItemProperty().addListener((observableValue, tab, t1) -> {
                if (t1 == null) {
                    level = null;
                    onSetLevel();
                    enableAllButtons(true);
                    if (FileManager.isHasOldWOG()) {
                        FXCreator.buttonNewOld.setDisable(false);
                        FXCreator.buttonOpenOld.setDisable(false);
                        FXCreator.newLevelOldItem.setDisable(false);
                        FXCreator.openLevelOldItem.setDisable(false);
                    }
                    if (FileManager.isHasNewWOG()) {
                        FXCreator.buttonNewNew.setDisable(false);
                        FXCreator.buttonOpenNew.setDisable(false);
                        FXCreator.newLevelNewItem.setDisable(false);
                        FXCreator.openLevelNewItem.setDisable(false);
                    }
                }
            });

            levelSelectPane.widthProperty().addListener(new ChangeListener<Number>() {
                @Override
                public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                    int numTabs = Main.getLevelSelectPane().getTabs().size();
                    double tabSize = 1 / (numTabs + 1.0);
                    Main.getLevelSelectPane()
                            .setTabMaxWidth(tabSize * (Main.getLevelSelectPane().getWidth() - 15) - 15);
                    Main.getLevelSelectPane()
                            .setTabMinWidth(tabSize * (Main.getLevelSelectPane().getWidth() - 15) - 15);
                }
            });

            // Combine everything inside a VBox
            vBox = new VBox(bar);
            try {
                FXCreator.buttons(vBox);
            } catch (IOException e) {
                Alarms.errorMessage(e);
            }

            levelSelectPane.setMinHeight(0);
            levelSelectPane.setMaxHeight(0);

            levelSelectPane.setTabClosingPolicy(TabPane.TabClosingPolicy.ALL_TABS);

            vBox.getChildren().add(splitPane);

            // Event handlers
            stage.addEventFilter(MouseEvent.MOUSE_PRESSED, Main::eventMousePressed);
            stage.addEventFilter(MouseEvent.MOUSE_RELEASED, Main::eventMouseReleased);
            stage.addEventFilter(MouseEvent.MOUSE_DRAGGED, Main::eventMouseDragged);
            stage.addEventFilter(MouseEvent.MOUSE_MOVED, Main::eventMouseMoved);

            stage.addEventFilter(KeyEvent.KEY_PRESSED, Main::keyPressed);
            stage.addEventFilter(KeyEvent.KEY_RELEASED, Main::keyReleased);

            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            scene = new Scene(vBox, screenSize.width * 0.75, screenSize.height * 0.75 - 30);
            scene.getStylesheets().add("style.css");
            stage.setScene(scene);

            stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                @Override
                public void handle(WindowEvent windowEvent) {
                    windowEvent.consume();
                    stage.close();
                }
            });

            stage.show();

            canvas.widthProperty().bind(stage.widthProperty());
            canvas.heightProperty().bind(splitPane.heightProperty());
            imageCanvas.widthProperty().bind(stage.widthProperty());
            imageCanvas.heightProperty().bind(splitPane.heightProperty());
            splitPane.maxHeightProperty().bind(stage.heightProperty());
            splitPane.prefHeightProperty().bind(stage.heightProperty());
            propertiesView.prefHeightProperty()
                    .bind(viewPane.heightProperty().subtract(propertiesView.layoutYProperty()));

            levelSelectPane.setTabDragPolicy(TabPane.TabDragPolicy.REORDER);
            levelSelectPane.setStyle("-fx-open-tab-animation: NONE");

            splitPane.getDividers().get(0).setPosition(0.8);

            stage.heightProperty().addListener(new ChangeListener<Number>() {
                @Override
                public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                }
            });

            new AddinLevelName(null);

            FileManager.openFailedImage();

            enableAllButtons(true);

            if (FileManager.isHasOldWOG()) {
                FXCreator.buttonNewOld.setDisable(false);
                FXCreator.buttonOpenOld.setDisable(false);
                FXCreator.newLevelOldItem.setDisable(false);
                FXCreator.openLevelOldItem.setDisable(false);
            }
            if (FileManager.isHasNewWOG()) {
                FXCreator.buttonNewNew.setDisable(false);
                FXCreator.buttonOpenNew.setDisable(false);
                FXCreator.newLevelNewItem.setDisable(false);
                FXCreator.openLevelNewItem.setDisable(false);
            }

            hierarchy.sort();

            EditorWindow editorWindow = new EditorWindow();
            editorWindow.start();

            stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                @Override
                public void handle(WindowEvent event) {
                    event.consume();
                    resumeLevelClosing();
                }
            });
            // LevelExporter.exportBallAsXML(Main.getImportedBalls().get(0), "");

            if (launchArguments.length > 0) {
                System.out.println("Opening level " + launchArguments[0]);
                if (FileManager.isHasNewWOG()) {
                    openLevel(launchArguments[0], 1.5);
                } else {
                    openLevel(launchArguments[0], 1.3);
                }
            }
        } catch (FileNotFoundException e) {
            Alarms.errorMessage(e);
        }
    }

    public static void resumeLevelClosing() {
        if (levelSelectPane.getTabs().size() == 0) {
            stage.close();
        } else {
            try {
                LevelTab levelTab = (LevelTab) levelSelectPane.getTabs().get(levelSelectPane.getTabs().size() - 1);
                if (levelTab.getLevel().getEditingStatus() == WorldLevel.UNSAVED_CHANGES) {
                    Alarms.closeTabMessage2(levelTab, levelTab.getLevel());
                } else {
                    levelTab.getTabPane().getTabs().remove(levelTab);
                    resumeLevelClosing();
                }
            } catch (Exception e) {
                Alarms.errorMessage(e);
            }
        }
    }

    public static void main(String[] args) {
        launchArguments = args;
        launch();
    }
}