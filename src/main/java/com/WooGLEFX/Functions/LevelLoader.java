package com.WooGLEFX.Functions;

import com.WooGLEFX.EditorObjects._Ball;
import com.WooGLEFX.Engine.FX.*;
import com.WooGLEFX.Engine.Main;
import com.WooGLEFX.File.FileManager;
import com.WooGLEFX.File.GlobalResourceManager;
import com.WooGLEFX.GUI.Alarms;
import com.WooGLEFX.GUI.LevelSelector;
import com.WooGLEFX.Structures.EditorAttribute;
import com.WooGLEFX.Structures.EditorObject;
import com.WooGLEFX.Structures.WorldLevel;
import com.WorldOfGoo.Level.BallInstance;
import com.WorldOfGoo.Level.Signpost;
import com.WorldOfGoo.Resrc.Resources;
import com.WorldOfGoo.Resrc.ResrcImage;
import com.WorldOfGoo.Resrc.Sound;
import com.WorldOfGoo.Scene.SceneLayer;
import javafx.scene.control.Tab;
import javafx.scene.control.TreeItem;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.ArrayList;

public class LevelLoader {

    private static final Logger logger = LoggerFactory.getLogger(LevelLoader.class);


    public static void newLevel(double version) {
        Alarms.askForLevelName("new", version);
    }


    public static void openLevel(double version) {
        new LevelSelector(version).start(new Stage());
    }


    /** Creates a new, default level. */
    public static void newLevel(String name, double version) {
        logger.debug("New level");

        FXLevelSelectPane.getLevelSelectPane().setMinHeight(30);
        FXLevelSelectPane.getLevelSelectPane().setMaxHeight(30);

        ArrayList<EditorObject> sceneList = new ArrayList<>();
        sceneList.add(EditorObject.create("scene", new EditorAttribute[0], null));
        ArrayList<EditorObject> levelList = new ArrayList<>();
        levelList.add(EditorObject.create("level", new EditorAttribute[0], null));
        ArrayList<EditorObject> resourcesList = new ArrayList<>();
        resourcesList.add(EditorObject.create("ResourceManifest", new EditorAttribute[0], null));
        ArrayList<EditorObject> addinList = new ArrayList<>();
        addinList.add(BlankObjectGenerator.generateBlankAddinObject(name));
        ArrayList<EditorObject> textList = new ArrayList<>();
        textList.add(EditorObject.create("strings", new EditorAttribute[0], null));

        WorldLevel level = new WorldLevel(sceneList, levelList, resourcesList, addinList, textList, version);
        LevelManager.setLevel(level);
        level.setLevelName(name);
        FXEditorButtons.enableAllButtons(false);

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
        FXHierarchy.getHierarchy().setRoot(level.getSceneObject().getTreeItem());

        // Add items from the Scene to it
        FXPropertiesView.getPropertiesView().setRoot(level.getSceneObject().getPropertiesTreeItem());

        Tab levelSelectButton = FXLevelSelectPane.levelSelectButton(level);
        FXLevelSelectPane.getLevelSelectPane().getTabs().add(levelSelectButton);

        int numTabs = FXLevelSelectPane.getLevelSelectPane().getTabs().size();
        double tabSize = 1 / (numTabs + 1.0);
        FXLevelSelectPane.getLevelSelectPane().setTabMaxWidth(tabSize * (FXLevelSelectPane.getLevelSelectPane().getWidth() - 15) - 15);
        FXLevelSelectPane.getLevelSelectPane().setTabMinWidth(tabSize * (FXLevelSelectPane.getLevelSelectPane().getWidth() - 15) - 15);

        LevelUpdater.saveLevel(level);

        level.setLevelTab(levelSelectButton);
        level.setEditingStatus(WorldLevel.NO_UNSAVED_CHANGES, true);
        FXLevelSelectPane.getLevelSelectPane().getSelectionModel().select(levelSelectButton);
        LevelManager.onSetLevel(level);
    }



    public static void openLevel(String levelName, double version) {
        // Don't open a level if none selected
        if (levelName == null || levelName.equals("")) {
            return;
        }
        // Don't open a level if it's already open
        for (Tab tab : FXLevelSelectPane.getLevelSelectPane().getTabs()) {
            if (tab.getText().equals(levelName)) {
                FXLevelSelectPane.getLevelSelectPane().getSelectionModel().select(tab);
                return;
            }
        }
        Main.failedResources.clear();

        WorldLevel level;

        try {
            level = FileManager.openLevel(levelName, version);
            LevelManager.setLevel(level);
        } catch (ParserConfigurationException | SAXException | IOException e) {
            Alarms.errorMessage(e);
            return;
        }

        level.setLevelName(levelName);
        FXEditorButtons.enableAllButtons(false);

        FXLevelSelectPane.getLevelSelectPane().setMinHeight(30);
        FXLevelSelectPane.getLevelSelectPane().setMaxHeight(30);

        for (EditorObject object : level.getLevel()) {
            if (object instanceof BallInstance) {
                boolean alreadyIn = false;
                for (_Ball ball : Main.getImportedBalls()) {
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

                            Main.getImportedBalls().add(ball2);
                        }
                    } catch (ParserConfigurationException | SAXException | IOException e) {
                        if (!Main.failedResources
                                .contains("Ball: " + object.getAttribute("type") + " (version " + version + ")")) {
                            Main.failedResources.add("Ball: " + object.getAttribute("type") + " (version " + version + ")");
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
        FXHierarchy.getHierarchy().setRoot(level.getSceneObject().getTreeItem());

        // Add items from the Scene to it
        FXPropertiesView.getPropertiesView().setRoot(level.getSceneObject().getPropertiesTreeItem());

        Tab levelSelectButton = FXLevelSelectPane.levelSelectButton(level);
        FXLevelSelectPane.getLevelSelectPane().getTabs().add(levelSelectButton);

        int numTabs = FXLevelSelectPane.getLevelSelectPane().getTabs().size();
        double tabSize = 1 / (numTabs + 1.0);
        FXLevelSelectPane.getLevelSelectPane().setTabMaxWidth(tabSize * (FXLevelSelectPane.getLevelSelectPane().getWidth() - 15) - 15);
        FXLevelSelectPane.getLevelSelectPane().setTabMinWidth(tabSize * (FXLevelSelectPane.getLevelSelectPane().getWidth() - 15) - 15);

        if (Main.failedResources.size() > 0) {
            StringBuilder fullError = new StringBuilder();
            for (String resource : Main.failedResources) {
                fullError.append("\n").append(resource);
            }
            Alarms.loadingResourcesError(fullError.toString().substring(1));
        }

        level.setLevelTab(levelSelectButton);
        level.setEditingStatus(WorldLevel.NO_UNSAVED_CHANGES, true);
        FXLevelSelectPane.getLevelSelectPane().getSelectionModel().select(levelSelectButton);
        LevelManager.onSetLevel(level);
    }


    public static void cloneLevel(String name, double version) {
        FXLevelSelectPane.getLevelSelectPane().setMinHeight(30);
        FXLevelSelectPane.getLevelSelectPane().setMaxHeight(30);

        ArrayList<EditorObject> sceneList = new ArrayList<>();
        ArrayList<EditorObject> levelList = new ArrayList<>();
        ArrayList<EditorObject> resourcesList = new ArrayList<>();
        ArrayList<EditorObject> addinList = new ArrayList<>();
        ArrayList<EditorObject> textList = new ArrayList<>();

        Main.supremeAddToList(resourcesList, LevelManager.getLevel().getResourcesObject().deepClone(null));
        Main.supremeAddToList(sceneList, LevelManager.getLevel().getSceneObject().deepClone(null));
        Main.supremeAddToList(levelList, LevelManager.getLevel().getLevelObject().deepClone(null));
        // Generate new addin object. idk why cloning it doesn't work, but this is arguably better anyway
        Main.supremeAddToList(addinList, BlankObjectGenerator.generateBlankAddinObject(name));
        Main.supremeAddToList(textList, LevelManager.getLevel().getTextObject().deepClone(null));

        String oldLevelName = LevelManager.getLevel().getLevelName();
        WorldLevel level = new WorldLevel(sceneList, levelList, resourcesList, addinList, textList, version);
        LevelManager.setLevel(level);

        level.setLevelName(name);
        FXEditorButtons.enableAllButtons(false);

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
        FXHierarchy.getHierarchy().setRoot(level.getSceneObject().getTreeItem());

        // Add items from the Scene to it
        FXPropertiesView.getPropertiesView().setRoot(level.getSceneObject().getPropertiesTreeItem());

        Tab levelSelectButton = FXLevelSelectPane.levelSelectButton(level);
        FXLevelSelectPane.getLevelSelectPane().getTabs().add(levelSelectButton);

        int numTabs = FXLevelSelectPane.getLevelSelectPane().getTabs().size();
        double tabSize = 1 / (numTabs + 1.0);
        FXLevelSelectPane.getLevelSelectPane().setTabMaxWidth(tabSize * (FXLevelSelectPane.getLevelSelectPane().getWidth() - 15) - 15);
        FXLevelSelectPane.getLevelSelectPane().setTabMinWidth(tabSize * (FXLevelSelectPane.getLevelSelectPane().getWidth() - 15) - 15);

        LevelUpdater.saveLevel(level);

        level.setLevelTab(levelSelectButton);
        level.setEditingStatus(WorldLevel.NO_UNSAVED_CHANGES, true);
        FXLevelSelectPane.getLevelSelectPane().getSelectionModel().select(levelSelectButton);
        LevelManager.onSetLevel(level);
    }


}
