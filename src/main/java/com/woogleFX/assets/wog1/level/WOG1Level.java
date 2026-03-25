package com.woogleFX.assets.wog1.level;

import com.woogleFX.assets.AssetError;
import com.woogleFX.assets.wog1.animation.WOG1Animation;
import com.woogleFX.editorObjects.EditorObject;
import com.woogleFX.assets.Asset;
import com.woogleFX.assets.wog1.ball.WOG1Ball;
import com.woogleFX.editorObjects.ObjectUtil;
import com.woogleFX.engine.AssetManager;
import com.woogleFX.engine.fx.FXContainers;
import com.woogleFX.engine.fx.hierarchy.FXHierarchy;
import com.woogleFX.engine.gui.AssetSelector;
import com.woogleFX.engine.gui.alarms.ErrorAlarm;
import com.woogleFX.file.FileManager;
import com.woogleFX.file.fileExport.GoomodExporter;
import com.woogleFX.file.resourceManagers.BaseGameResources;
import com.woogleFX.assets.GameVersion;
import com.woogleFX.assets.AssetUpdater;
import com.worldOfGoo.addin.addin;
import com.worldOfGoo.level.*;
import com.worldOfGoo.resrc.Resources;
import com.worldOfGoo.scene.*;
import com.worldOfGoo.text.strings;
import javafx.scene.Node;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.util.*;

/** A level from World of Goo 1.
 * Has scene, level, and resrc as different files.
 * File = "/res/levels/[level name]" */
public class WOG1Level extends Asset {

    public WOG1Level(GameVersion version, scene scene, level level, Resources resources, strings strings) {
        super(version, resources, strings);

        this.scene = scene;
        this.level = level;

        getVisibilitySettings().addVisibilityStatus("goos", 2);
        getVisibilitySettings().addVisibilityStatus("cameras", 0, 1);
        getVisibilitySettings().addVisibilityStatus("geometry", 2);
        getVisibilitySettings().addVisibilityStatus("graphics", 1);
        getVisibilitySettings().addVisibilityStatus("forcefields", 1);
        getVisibilitySettings().addVisibilityStatus("scene", 1, 2);
        getVisibilitySettings().addVisibilityStatus("particles", 1);
        getVisibilitySettings().addVisibilityStatus("labels", 1);
        getVisibilitySettings().addVisibilityStatus("animations", 1);

        reAssignSetDefaultsToAllResources();
        resetCamera();

        for (EditorObject editorObject : getObjects()) {
            if (editorObject instanceof label label) {
                tryToAddText(label.getAttribute("text").stringValue());
            }
            if (editorObject instanceof signpost signpost) {
                tryToAddText(signpost.getAttribute("text").stringValue());
            }
        }

    }

    // ==========================================================================
    //  Special level-specific things
    // ==========================================================================

    private final scene scene;
    public EditorObject getScene() {
        return scene;
    }

    private final level level;
    public EditorObject getLevel() {
        return level;
    }

    public void saveAndPlay() {
        if (!save(getFile())) return;
        try {
            ProcessBuilder processBuilder = new ProcessBuilder(
                    FileManager.getGameDir(GameVersion.VERSION_WOG1_OLD) + "/WorldOfGoo.exe", getName());
            processBuilder.directory(new File(FileManager.getGameDir(GameVersion.VERSION_WOG1_OLD)));
            processBuilder.start();
        } catch (Exception e) {
            ErrorAlarm.show(e);
        }
    }

    // ==========================================================================
    //  Asset selector
    // ==========================================================================

    public static final AssetSelector<WOG1Level> assetSelector = new AssetSelector<>("Level") {

        public List<String> getItems(GameVersion version) {
            // List the names of all directories in res/levels
            List<String> levels = new ArrayList<>();
            File[] levelFiles = new File(FileManager.getGameDir(version) + "/res/levels").listFiles();
            for (File child : Objects.requireNonNull(levelFiles)) levels.add(child.getName());
            return levels;
        }

        public boolean isOriginal(String item, GameVersion version) {
            return BaseGameResources.LEVELS.get(version).contains(item);
        }

        protected WOG1Level secretNewInstance(String name, GameVersion version) {
            return WOG1LevelOpener.newLevel(name, version);
        }

        protected WOG1Level secretOpenInstance(File file, String name, GameVersion version) {
            return WOG1LevelOpener.openLevel(file, version);
        }

        protected FileChooser.ExtensionFilter getCustomExtensionFilter(GameVersion version) {
            String suffix = (version == GameVersion.VERSION_WOG1_OLD) ? ".bin" : "";
            return new FileChooser.ExtensionFilter("WoG Level", "*.scene" + suffix);
        }

        protected File getDefaultFileForName(String name, GameVersion version) {
            return new File(FileManager.getGameDir(version) + "/res/levels/" + name);
        }

        protected String getNameFromFile(File file, GameVersion version) {
            String suffix = (version == GameVersion.VERSION_WOG1_OLD) ? ".bin" : "";
            String name = file.getName();
            return name.substring(name.lastIndexOf("/") + 1, name.length() - suffix.length());
        }

    };

    // ==========================================================================
    //  Common asset functions
    // ==========================================================================

    @Override
    public List<Asset> getAllCurrentDependencies() {
        List<Asset> dependencies = new ArrayList<>();

        // Custom Goo Balls
        for (EditorObject editorObject : level.getChildren()) {
            if (editorObject instanceof BallInstance ballInstance) {
                WOG1Ball ball = ballInstance.getBall();
                if (ball != null && !ball.isBaseGame()) {
                    if (!dependencies.contains(ball)) dependencies.add(ball);
                }
            }
        }

        // Custom animations
        for (EditorObject editorObject : scene.getChildren()) {
            if (editorObject instanceof SceneLayer sceneLayer) {
                WOG1Animation animation = sceneLayer.getAnimation();
                if (animation != null && !animation.isBaseGame()) {
                    if (!dependencies.contains(animation)) dependencies.add(animation);
                }
            }
        }

        return dependencies;

    }

    @Override
    public EditorObject getDefaultParent(Class<? extends EditorObject> toAdd) {

        String pckg = toAdd.getPackage().getName();

        if (pckg.endsWith("scene")) return getScene();
        else if (pckg.endsWith("level")) return getLevel();
        else if (pckg.endsWith("resrc")) return getResources();
        else if (pckg.endsWith("text")) return getStrings();
        else return null;

    }

    @Override
    public ArrayList<Node> getGUIElements() {
        return WOG1LevelGUI.getGUIElements();
    }

    @Override
    public List<EditorObject> getObjects() {
        List<EditorObject> objects = new ArrayList<>();
        scene.addAllChildren(objects);
        level.addAllChildren(objects);
        return Collections.unmodifiableList(objects);
    }

    @Override
    public boolean isBaseGame() {
        return BaseGameResources.LEVELS.get(getVersion()).contains(getName());
    }

    @Override
    public boolean isScaleTooFar(double scaleX, double scaleY) {
        return (scaleX < 0.001 || scaleX > 1000 || scaleY < 0.001 || scaleY > 1000);
    }

    @Override
    public void onSet() {
        super.onSet();
        WOG1LevelGUI.refreshBallPalette(getVersion());
    }

    @Override
    public void resetCamera() {

        double minx = scene.getAttribute("minx").doubleValue();
        double maxx = scene.getAttribute("maxx").doubleValue();
        double miny = scene.getAttribute("miny").doubleValue();
        double maxy = scene.getAttribute("maxy").doubleValue();

        double sceneWidth = maxx - minx;
        double sceneHeight = maxy - miny;

        double middleX = (minx + maxx) / 2;
        double middleY = (miny + maxy) / 2;

        SplitPane splitPane = FXContainers.getSplitPane();
        double canvasWidth = splitPane.getDividers().get(0).getPosition() * splitPane.getWidth();
        double canvasHeight = splitPane.getHeight();

        setOffsetX(canvasWidth / 2 - middleX);
        setOffsetY(canvasHeight / 2 + middleY);

        double zoomX = canvasWidth / sceneWidth;
        double zoomY = canvasHeight / sceneHeight;

        setZoom(Math.min(Math.abs(zoomX), Math.abs(zoomY)));

        setOffsetX((getOffsetX() - canvasWidth / 2) * getZoom() + canvasWidth / 2);
        setOffsetY((getOffsetY() - canvasHeight / 2) * getZoom() + canvasHeight / 2);
    }

    @Override
    public List<AssetError> verify() {
        return new ArrayList<>();
    }

    // ==========================================================================
    //  Asset management
    // ==========================================================================

    @Override
    public boolean save(File file) {
        try {
            WOG1LevelWriter.saveAsXML(this, file.getPath(), getVersion(), false, true);
            return true;
        } catch (IOException e) {
            ErrorAlarm.show(e);
            return false;
        }
    }

    @Override
    public void load() {
        super.load();

        for (EditorObject object : getObjects()) {
            object.onLoaded(this);
        }

        for (EditorObject object : getResources().getChildren()) {
            object.onLoaded(this);
        }

        for (EditorObject object : getAddin().getChildren()) {
            object.onLoaded(this);
        }

    }

    @Override
    public void export(boolean includeAddinInfo) {
        GoomodExporter.export(this, includeAddinInfo);
    }

    @Override
    public void delete() {
        // we can simply delete the level's file
        try {
            AssetUpdater.nuke(getFile());
        } catch (IOException e) {
            ErrorAlarm.show(e);
        }
    }

    @Override
    public Asset clone(String name) {

        scene scene_ = ObjectUtil.deepClone(scene, null);
        level level_ = ObjectUtil.deepClone(level, null);

        Resources resources_ = ObjectUtil.deepClone(getResources(), null);
        resources_.setAttribute("id", "scene_" + name);

        com.worldOfGoo.text.strings strings_ = ObjectUtil.deepClone(getStrings(), null);

        WOG1Level newLevel = new WOG1Level(getVersion(), scene_, level_, resources_, strings_);
        newLevel.setName(name);
        newLevel.setFile(new File(getFile().getParent() + "/" + name));
        return newLevel;

    }

    // ==========================================================================
    //  Tabs
    // ==========================================================================

    Tab tabScene = new Tab("Scene");
    Tab tabLevel = new Tab("Level");
    Tab tabResrc = new Tab("Resrc");
    Tab tabText = new Tab("Text");
    Tab tabAddin = new Tab("Addin");

    @Override
    public List<Tab> getTabs() {
        return List.of(tabScene, tabLevel, tabResrc, tabText, tabAddin);
    }

    @Override
    public void onSetTab(Tab tab) {
        if (this != AssetManager.getAsset()) return;

        EditorObject rootObject;
        if      (tab == tabScene) rootObject = scene;
        else if (tab == tabLevel) rootObject = level;
        else if (tab == tabResrc) rootObject = getResources();
        else if (tab == tabText)  rootObject = getStrings();
        else if (tab == tabAddin) rootObject = getAddin();
        else return;

        assert rootObject != null;

        FXHierarchy.getHierarchy().setRoot(rootObject.getTreeItem());
        FXHierarchy.getHierarchy().refresh();
        FXHierarchy.getHierarchy().getRoot().setExpanded(true);
        FXHierarchy.getHierarchy().setShowRoot(true);
    }

    @Override
    public Tab getTabForObject(EditorObject editorObject) {
        EditorObject absoluteParent = editorObject;
        while (absoluteParent.getParent() != null) absoluteParent = absoluteParent.getParent();
        if      (absoluteParent instanceof scene)     return tabScene;
        else if (absoluteParent instanceof level)     return tabLevel;
        else if (absoluteParent instanceof Resources) return tabResrc;
        else if (absoluteParent instanceof strings)   return tabText;
        else if (absoluteParent instanceof addin)     return tabAddin;
        else return null;
    }

}
