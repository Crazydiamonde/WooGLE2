package com.woogleFX.assets.wog2.WOG2Item;

import com.woogleFX.assets.AssetError;
import com.woogleFX.editorObjects.EditorObject;
import com.woogleFX.assets.Asset;
import com.woogleFX.engine.gui.AssetSelector;
import com.woogleFX.engine.gui.alarms.ErrorAlarm;
import com.woogleFX.file.FileManager;
import com.woogleFX.file.fileImport.ObjectGOOParser;
import com.woogleFX.assets.GameVersion;
import com.worldOfGoo.resrc.Resources;
import com.worldOfGoo.text.strings;
import com.worldOfGoo2.items._2_Item;
import com.worldOfGoo2.items._2_Item_Collection;
import com.worldOfGoo2.util.ItemHelper;
import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.stage.FileChooser;
import javafx.util.Pair;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;

public class WOG2Item extends Asset {

    private final _2_Item item;
    public _2_Item getItem() {
        return item;
    }

    @Override
    public List<EditorObject> getObjects() {
        List<EditorObject> objects = new ArrayList<>();
        item.addAllChildren(objects);
        return Collections.unmodifiableList(objects);
    }


    public static final AssetSelector<WOG2Item> assetSelector = new AssetSelector<>("Item") {

        private static final Map<Pair<String, GameVersion>, WOG2Item> importedItems = new HashMap<>();

        @Override
        public List<String> getItems(GameVersion version) {
            return null;
        }

        @Override
        public boolean isOriginal(String item, GameVersion version) {
            return false;
        }

        @Override
        protected WOG2Item secretNewInstance(String name, GameVersion version) {
            return null;
        }

        @Override
        protected WOG2Item secretOpenInstance(File file, String name, GameVersion version) {

            WOG2Item item = importedItems.get(new Pair<>(name, version));
            if (item != null) return item;

            try {
                for (File itemFile : new File(FileManager.getGameDir(GameVersion.VERSION_WOG2) + "/res/items").listFiles())
                    if (itemFile.getName().endsWith(".wog2")) {
                        _2_Item_Collection item2 = ObjectGOOParser.read(_2_Item_Collection.class, Files.readString(itemFile.toPath()), "items");
                        Stack<EditorObject> stack = new Stack<>();
                        stack.addAll(item2.getChildren());
                        while (!stack.empty()) {
                            EditorObject item3 = stack.pop();
                            item3.update();
                            stack.addAll(item3.getChildren());
                        }
                        ItemHelper.itemNameMap.put(item2.getChildren().get(0).getAttribute("uuid").stringValue(), item2.getChildren().get(0).getAttribute("name").stringValue());
                        // TODO2:
                        WOG2Item wog2Item = new WOG2Item(version, (_2_Item) item2.getChildren().get(0), null, null);
                        // wog2Item.getObjects().add(item2.getChildren().get(0));
                        importedItems.put(new Pair<>(item2.getChildren().get(0).getAttribute("name").stringValue(), version), wog2Item);
                    }
                // System.out.println(ItemHelper.itemNameMap);
                return importedItems.get(new Pair<>(name, version));
            } catch (IOException e) {
                ErrorAlarm.show(e);
                return null;
            }
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


    public WOG2Item(GameVersion version, _2_Item item, Resources resources, strings strings) {
        super(version, resources, strings);

        this.item = item;

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
