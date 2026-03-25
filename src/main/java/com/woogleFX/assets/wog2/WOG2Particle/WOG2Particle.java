package com.woogleFX.assets.wog2.WOG2Particle;

import com.woogleFX.assets.AssetError;
import com.woogleFX.editorObjects.EditorObject;
import com.woogleFX.assets.Asset;
import com.woogleFX.engine.gui.AssetSelector;
import com.woogleFX.assets.GameVersion;
import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.stage.FileChooser;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class WOG2Particle extends Asset {

    @Override
    public List<EditorObject> getObjects() {
        List<EditorObject> objects = new ArrayList<>();
        // TODO2:
        return Collections.unmodifiableList(objects);
    }


    public static final AssetSelector<WOG2Particle> assetSelector = new AssetSelector<>("Particle") {

        @Override
        public List<String> getItems(GameVersion version) {
            return null;
        }

        @Override
        public boolean isOriginal(String item, GameVersion version) {
            return false;
        }

        @Override
        protected WOG2Particle secretNewInstance(String name, GameVersion version) {
            return null;
        }

        @Override
        protected WOG2Particle secretOpenInstance(File file, String name, GameVersion version) {
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


    public WOG2Particle(GameVersion version, ArrayList<EditorObject> objects, ArrayList<EditorObject> resources) {
        super(version, null, null);
    }

    @Override
    public void resetCamera() {

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

    @Override
    public List<Tab> getTabs() {
        return null;
    }

    @Override
    public void onSetTab(Tab tab) {

    }

    @Override
    public Tab getTabForObject(EditorObject editorObject) {
        return null;
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
