package com.woogleFX.assets.wog2.WOG2TerrainType;

import com.woogleFX.assets.AssetError;
import com.woogleFX.editorObjects.EditorObject;
import com.woogleFX.assets.Asset;
import com.woogleFX.engine.gui.AssetSelector;
import com.woogleFX.assets.GameVersion;
import com.worldOfGoo2.terrain._2_Terrain_TerrainType;
import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.stage.FileChooser;

import java.io.File;
import java.util.*;

public class WOG2TerrainType extends Asset {

    private final _2_Terrain_TerrainType terrainType;
    public _2_Terrain_TerrainType getTerrainType() {
        return terrainType;
    }

    @Override
    public List<EditorObject> getObjects() {
        List<EditorObject> objects = new ArrayList<>();
        terrainType.addAllChildren(objects);
        return Collections.unmodifiableList(objects);
    }


    public static final AssetSelector<WOG2TerrainType> assetSelector = new AssetSelector<>("TerrainType") {

        private static final Map<String, WOG2TerrainType> importedTerrainTypes = new HashMap<>();

        @Override
        public List<String> getItems(GameVersion version) {
            return null;
        }

        @Override
        public boolean isOriginal(String item, GameVersion version) {
            return false;
        }

        @Override
        protected WOG2TerrainType secretNewInstance(String name, GameVersion version) {
            return null;
        }

        @Override
        protected WOG2TerrainType secretOpenInstance(File file, String name, GameVersion version) {

            WOG2TerrainType importedTerrainType = importedTerrainTypes.get(name);
            if (importedTerrainType != null) return importedTerrainType;

            WOG2TerrainType terrainType = WOG2TerrainTypeOpener.openTerrainType(name, version);
            if (terrainType == null) return null;

            terrainType.load();
            importedTerrainTypes.put(name, terrainType);
            return terrainType;

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


    public WOG2TerrainType(GameVersion version, ArrayList<EditorObject> objects, ArrayList<EditorObject> resources) {
        super(version, null, null); // TODO2:

        terrainType = (_2_Terrain_TerrainType) objects.get(0);

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
