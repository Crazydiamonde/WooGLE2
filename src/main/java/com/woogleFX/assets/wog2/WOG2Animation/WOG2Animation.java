package com.woogleFX.assets.wog2.WOG2Animation;

import com.woogleFX.assets.AssetError;
import com.woogleFX.editorObjects.EditorObject;
import com.woogleFX.assets.Asset;
import com.woogleFX.engine.gui.AssetSelector;
import com.woogleFX.engine.gui.alarms.ErrorAlarm;
import com.woogleFX.file.FileManager;
import com.woogleFX.gameData.animation.AnimBinReader;
import com.woogleFX.gameData.animation.SimpleBinAnimation;
import com.woogleFX.assets.GameVersion;
import com.worldOfGoo.addin.addin;
import com.worldOfGoo.resrc.Resources;
import com.worldOfGoo.text.strings;
import com.worldOfGoo2.anim.Animation;
import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.stage.FileChooser;
import javafx.util.Pair;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Stream;

public class WOG2Animation extends Asset {

    private final Animation animation;
    public Animation getAnimation() {
        return animation;
    }

    @Override
    public List<EditorObject> getObjects() {
        List<EditorObject> objects = new ArrayList<>();
        animation.addAllChildren(objects);
        return Collections.unmodifiableList(objects);
    }


    public static final AssetSelector<WOG2Animation> assetSelector = new AssetSelector<>("Animation") {

        private static final Map<Pair<String, GameVersion>, WOG2Animation> importedAnimations = new HashMap<>();

        @Override
        public List<String> getItems(GameVersion version) {
            List<String> animations = new ArrayList<>();
            try (Stream<Path> files = Files.walk(Path.of(FileManager.getGameDir(version) + "/res/anim"))) {
                for (Path child : files.filter(e -> e.getFileName().toString().endsWith(".anim.bin")).toArray(Path[]::new)) {
                    animations.add(child.getFileName().toString().substring(0, child.getFileName().toString().length() - 9));
                }
            } catch (IOException e) {
                ErrorAlarm.show(e);
            }
            return animations;
        }

        @Override
        public boolean isOriginal(String item, GameVersion version) {
            return false;
        }

        @Override
        protected WOG2Animation secretNewInstance(String name, GameVersion version) {
            return null;
        }

        @Override
        protected WOG2Animation secretOpenInstance(File file, String name, GameVersion version) {

            WOG2Animation importedAnimation = importedAnimations.get(new Pair<>(name, version));
            if (importedAnimation != null) return importedAnimation;

            try (Stream<Path> files = Files.walk(Path.of(FileManager.getGameDir(version) + "/res/anim"))){
                for (Path child : files.filter(e -> e.getFileName().toString().endsWith(".anim.bin")).toArray(Path[]::new)) {
                    if (child.getFileName().toString().contains(name)) {
                        // TODO2:
                        WOG2Animation animation = new WOG2Animation(AnimBinReader.readSimpleBinAnimation(child, name), null, null);

                        animation.load();
                        importedAnimations.put(new Pair<>(name, version), animation);
                        return animation;

                    }
                }
            } catch (IOException e) {
                ErrorAlarm.show(e);
                return null;
            }
            return null;
        }

        @Override
        protected FileChooser.ExtensionFilter getCustomExtensionFilter(GameVersion version) {
            return null;
        }

        @Override
        protected File getDefaultFileForName(String name, GameVersion version) {
            return null;
        }

        @Override
        protected String getNameFromFile(File file, GameVersion version) {
            return null;
        }
    };


    public WOG2Animation(SimpleBinAnimation simpleBinAnimation, Resources resources, strings strings) {
        super(GameVersion.VERSION_WOG2, resources, strings);

        // Construct this thing based on the simpleBinAnimation

        animation = new Animation(null, GameVersion.VERSION_WOG2);
        animation.loadFromAnimation(simpleBinAnimation);

        animation.onLoaded(this);

        resetCamera();

    }

    @Override
    public void resetCamera() {
        setOffsetX(0);
        setOffsetY(0);
        setZoom(1.0);
    }

    @Override
    public List<AssetError> verify() {
        return new ArrayList<>();
    }

    @Override
    public boolean save(File file) {
        return false;
    }

    @Override
    public void export(boolean includeAddinInfo) {

    }

    @Override
    public void delete() {

    }

    @Override
    public boolean isScaleTooFar(double scaleX, double scaleY) {
        return false;
    }

    Tab terrain = new Tab("Terrain");
    Tab terrainGroups = new Tab("Terrain Groups");
    Tab balls = new Tab("Balls");
    Tab items = new Tab("Items");
    Tab pins = new Tab("Pins");
    Tab camera = new Tab("Camera");
    Tab _addin = new Tab("Addin");

    @Override
    public List<Tab> getTabs() {
        return List.of(terrain, terrainGroups, balls, items, pins, camera, _addin);
    }

    @Override
    public Tab getTabForObject(EditorObject editorObject) {
        return null;
    }

    // TODO2:
    /*
    TabPane hierarchySwitcherButtons = FXHierarchySwitcherButtons.getHierarchySwitcherButtons();
        hierarchySwitcherButtons.getTabs().clear();

        // Create the three buttons.

        hierarchySwitcherButtons.getTabs().addAll(terrain, terrainGroups, balls, items, pins, camera, addin);
        hierarchySwitcherButtons.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        hierarchySwitcherButtons.setMinHeight(30);
        hierarchySwitcherButtons.setMaxHeight(30);
        hierarchySwitcherButtons.setPrefHeight(30);
        hierarchySwitcherButtons.setPadding(new Insets(-6, -6, -6, -6));

        hierarchySwitcherButtons.getSelectionModel().selectedItemProperty().addListener((observableValue, tab, t1) -> {

            if (t1 == null) return;

            TreeItem<EditorObject> root = animation.getTreeItem();
            FXHierarchy.getHierarchy().setRoot(root);

            root.getChildren().clear();

            for (EditorObject child : animation.getChildren()) {

                if (true || (child.getType().equals("BallInstance") && child.getAttribute("type").stringValue().equals("Terrain")) && t1 == terrain) root.getChildren().add(child.getTreeItem());
                else if (child instanceof _2_Level_TerrainGroup && t1 == terrainGroups) root.getChildren().add(child.getTreeItem());
                else if (((child.getType().equals("BallInstance") && !child.getAttribute("type").stringValue().equals("Terrain")) || child.getType().equals("Strand")) && t1 == balls) root.getChildren().add(child.getTreeItem());
                else if (child.getType().equals("Item") && t1 == items) root.getChildren().add(child.getTreeItem());
                else if (child.getType().equals("Pin") && t1 == pins) root.getChildren().add(child.getTreeItem());
                else if (child.getType().equals("CameraKeyFrame") && t1 == camera) root.getChildren().add(child.getTreeItem());

            }

            if (t1 == addin) root.getChildren().add(getAddin().get(0).getTreeItem());

            FXHierarchy.getHierarchy().refresh();
            FXHierarchy.getHierarchy().getRoot().setExpanded(true);
            setCurrentlySelectedSection(t1.getText());
            FXHierarchy.getHierarchy().setShowRoot(true);

        });

    }

     */

    @Override
    public void onSetTab(Tab tab) {

    }

    @Override
    public EditorObject getDefaultParent(Class<? extends EditorObject> toAdd) {
        return null;
    }

    @Override
    public Asset clone(String name) {
        return null;
    }

    @Override
    public void load() {
        super.load();

    }

    @Override
    public boolean isBaseGame() {
        return false;
    }

    @Override
    public ArrayList<Node> getGUIElements() {
        return new ArrayList<>();
    }

}
