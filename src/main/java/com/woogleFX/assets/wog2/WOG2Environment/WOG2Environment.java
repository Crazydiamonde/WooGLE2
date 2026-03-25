package com.woogleFX.assets.wog2.WOG2Environment;

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
import com.worldOfGoo2.environments.Environment;
import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.stage.FileChooser;
import javafx.util.Pair;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;

public class WOG2Environment extends Asset {

    private final Environment environment;
    public Environment getEnvironment() {
        return environment;
    }

    @Override
    public List<EditorObject> getObjects() {
        List<EditorObject> objects = new ArrayList<>();
        environment.addAllChildren(objects);
        return Collections.unmodifiableList(objects);
    }


    public static final AssetSelector<WOG2Environment> assetSelector = new AssetSelector<>("Environment") {

        private static final Map<Pair<String, GameVersion>, WOG2Environment> importedEnvironments = new HashMap<>();

        @Override
        public List<String> getItems(GameVersion version) {
            return null;
        }

        @Override
        public boolean isOriginal(String item, GameVersion version) {
            return false;
        }

        @Override
        protected WOG2Environment secretNewInstance(String name, GameVersion version) {
            return null;
        }

        @Override
        protected WOG2Environment secretOpenInstance(File file, String name, GameVersion version) {
            WOG2Environment environment = importedEnvironments.get(name);
            if (environment != null) return environment;

            try {
                File itemFile = new File(FileManager.getGameDir(GameVersion.VERSION_WOG2) + "/res/environments/" + name + ".wog2");
                Environment environment2 = ObjectGOOParser.read(Environment.class, Files.readString(itemFile.toPath()), "environment");
                Stack<EditorObject> stack = new Stack<>();
                stack.add(environment2);
                while (!stack.empty()) {
                    EditorObject item = stack.pop();
                    item.update();
                    stack.addAll(item.getChildren());
                }
                // TODO2:
                WOG2Environment wog2Environment = new WOG2Environment(version, environment2, null, null);
                importedEnvironments.put(new Pair<>(name, version), wog2Environment);
                return wog2Environment;
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


    public WOG2Environment(GameVersion version, Environment environment, Resources resources, strings strings) {
        super(version, resources, strings);

        this.environment = environment;

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
