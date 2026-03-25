package com.woogleFX.assets.wog1.animation;

import com.woogleFX.assets.AssetError;
import com.woogleFX.assets.HasTime;
import com.woogleFX.editorObjects.EditorObject;
import com.woogleFX.assets.Asset;
import com.woogleFX.editorObjects.ObjectUtil;
import com.woogleFX.engine.AssetManager;
import com.woogleFX.engine.fx.hierarchy.FXHierarchy;
import com.woogleFX.engine.gui.AssetSelector;
import com.woogleFX.assets.GameVersion;
import com.woogleFX.engine.gui.alarms.ErrorAlarm;
import com.woogleFX.file.FileManager;
import com.woogleFX.file.fileExport.GoomodExporter;
import com.woogleFX.file.resourceManagers.BaseGameResources;
import com.worldOfGoo.anim.Animation;
import com.worldOfGoo.resrc.Resources;
import com.worldOfGoo.text.strings;
import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.image.Image;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;

/** The animations found in anim.binltl and anim.binuni files. */
public class WOG1Animation extends Asset implements HasTime {

    public WOG1Animation(GameVersion version, Animation animation, Resources resources, strings strings) {
        super(version, resources, strings);
        this.animation = animation;
    }

    // ==========================================================================
    //  Special animation-specific things
    // ==========================================================================

    private double time = 0;
    @Override public double getTime() { return time; }
    @Override public void setTime(double time) { this.time = time; onSetTime(); }

    private boolean isPlaying = false;
    @Override public boolean isPlaying() { return isPlaying; }
    @Override public void setPlaying(boolean playing) { isPlaying = playing; }

    /** Used to preview what the animation does to images.
     * The default image is this weird UV map gooball thing. */
    private Image previewImage = FileManager.getIcon("/ObjectIcons/texture.png");
    public Image getPreviewImage() { return previewImage; }
    public void setPreviewImage(Image previewImage) { this.previewImage = previewImage; }

    /** The Animation EditorObject with which this WOG1Animation is associated. */
    private final Animation animation;
    public Animation getAnimation() {
        return animation;
    }

    private void onSetTime() {
        // Update the timeline based on the time

        // Get the length of the animation based on the keyframe times
        double length = 0;
        for (EditorObject keyframe : animation.getChildren()) {
            double time = keyframe.getAttribute("time").doubleValue();
            if (time > length) length = time;
        }
        while (time > length) time -= length;

        WOG1AnimationGUI.updateTimeline(length == 0 ? 0 : time / length);

    }

    // ==========================================================================
    //  Asset selector
    // ==========================================================================

    public static final AssetSelector<WOG1Animation> assetSelector = new AssetSelector<>("Animation") {

        public List<String> getItems(GameVersion version) {
            return WOG1AnimationOpener.getAnimationNames(version);
        }

        public boolean isOriginal(String item, GameVersion version) {
            return BaseGameResources.ANIMATIONS.get(version).contains(item);
        }

        protected WOG1Animation secretNewInstance(String name, GameVersion version) {
            return WOG1AnimationOpener.newAnimation(name, version);
        }

        protected WOG1Animation secretOpenInstance(File file, String name, GameVersion version) {
            return WOG1AnimationOpener.openAnimation(file, version);
        }

        protected FileChooser.ExtensionFilter getCustomExtensionFilter(GameVersion version) {
            String extension = (version == GameVersion.VERSION_WOG1_OLD ? "*.anim.binltl" : "*.anim.binuni");
            return new FileChooser.ExtensionFilter("WoG Animation", extension);
        }

        protected File getDefaultFileForName(String name, GameVersion version) {
            // <game dir>/res/anim/<name>.anim.bin***
            String extension = (version == GameVersion.VERSION_WOG1_OLD ? ".anim.binltl" : ".anim.binuni");
            return new File(FileManager.getGameDir(version) + "/res/anim/" + name + extension);
        }

        protected String getNameFromFile(File file, GameVersion version) {
            // Remove .anim.binltl or .anim.binuni extension from file name
            return file.getName().substring(0, file.getName().length() - 12);
        }

    };

    // ==========================================================================
    //  Common asset functions
    // ==========================================================================

    @Override
    public EditorObject getDefaultParent(Class<? extends EditorObject> toAdd) {
        return switch (toAdd.getPackage().getName()) {
            case "com.worldOfGoo.anim" -> animation;
            case "com.worldOfGoo.addin" -> getAddin();
            case "com.worldOfGoo.text" -> getStrings();
            default -> throw new RuntimeException("Invalid object: " + toAdd);
        };
    }

    @Override
    public ArrayList<Node> getGUIElements() {
        return WOG1AnimationGUI.getGUIElements();
    }

    @Override
    public List<EditorObject> getObjects() {
        List<EditorObject> objects = new ArrayList<>();
        animation.addAllChildren(objects); // animation serves as root object
        return Collections.unmodifiableList(objects);
    }

    @Override
    public boolean isBaseGame() {
        return BaseGameResources.ANIMATIONS.get(getVersion()).contains(getName());
    }

    @Override
    public boolean isScaleTooFar(double scaleX, double scaleY) {
        return scaleX < 0.01 || scaleX > 100 || scaleY < 0.01 || scaleY > 100;
    }

    @Override
    public void onSet() {
        onSetTime();
        WOG1AnimationGUI.updatePlaying(isPlaying);
    }

    @Override
    public void resetCamera() {
        setOffsetX(0);
        setOffsetY(0);
        setZoom(1.0);
    }

    @Override
    public List<AssetError> verify() {
        // currently no specific errors to look for. just return an empty list
        return new ArrayList<>();
    }

    // ==========================================================================
    //  Asset management
    // ==========================================================================

    @Override
    public boolean save(File file) {
        return WOG1AnimationWriter.saveAnimation(this, file.getPath(), getVersion());
    }

    @Override
    public void load() {
        super.load();
        for (EditorObject object : getObjects()) {
            object.onLoaded(this);
        }
    }

    @Override
    public void export(boolean includeAddinInfo) {
        GoomodExporter.export(this, includeAddinInfo);
    }

    @Override
    public void delete() {
        try {
            // just delete the corresponding .anim.bin*** file
            Files.delete(getFile().toPath());
        } catch (IOException e) {
            ErrorAlarm.show(e);
        }
    }

    @Override
    public Asset clone(String name) {

        Animation animation_ = ObjectUtil.deepClone(animation, null);

        Resources resources_ = ObjectUtil.deepClone(getResources(), null);
        resources_.setAttribute("id", "scene_" + name);

        com.worldOfGoo.text.strings strings_ = ObjectUtil.deepClone(getStrings(), null);

        WOG1Animation newAnimation = new WOG1Animation(getVersion(), animation_, resources_, strings_);
        newAnimation.setName(name);
        newAnimation.setFile(new File(getFile().getParent() + "/" + name));
        return newAnimation;

    }

    // ==========================================================================
    //  Tabs
    // ==========================================================================

    Tab tabKeyframes = new Tab("Keyframes");
    Tab tabText = new Tab("Text");
    Tab tabAddin = new Tab("Addin");

    @Override
    public List<Tab> getTabs() {
        return List.of(tabKeyframes, tabText, tabAddin);
    }

    @Override
    public void onSetTab(Tab tab) {
        if (this != AssetManager.getAsset() || tab == null) return;

        EditorObject rootObject = null;
        if (tab == tabKeyframes) rootObject = animation;
        else if (tab == tabText) rootObject = getStrings();
        else if (tab == tabAddin) rootObject = getAddin();
        assert rootObject != null;
        FXHierarchy.getHierarchy().setRoot(rootObject.getTreeItem());

        FXHierarchy.getHierarchy().refresh();
        FXHierarchy.getHierarchy().getRoot().setExpanded(true);
        FXHierarchy.getHierarchy().setShowRoot(true);
    }

    @Override
    public Tab getTabForObject(EditorObject editorObject) {
        return switch (editorObject.getClass().getPackage().getName()) {
            case "com.worldOfGoo.anim" -> tabKeyframes;
            case "com.worldOfGoo.addin" -> tabAddin;
            case "com.worldOfGoo.text" -> tabText;
            default -> throw new RuntimeException("Invalid object: " + editorObject);
        };
    }

}
