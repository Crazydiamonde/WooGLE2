package com.woogleFX.assets.wog1.movie;

import com.woogleFX.assets.*;
import com.woogleFX.editorObjects.EditorObject;
import com.woogleFX.editorObjects.ObjectUtil;
import com.woogleFX.editorObjects.objectCreators.ObjectCreator;
import com.woogleFX.engine.AssetManager;
import com.woogleFX.engine.fx.hierarchy.FXHierarchy;
import com.woogleFX.engine.gui.AssetSelector;
import com.woogleFX.engine.gui.alarms.ErrorAlarm;
import com.woogleFX.file.FileManager;
import com.woogleFX.file.resourceManagers.BaseGameResources;
import com.worldOfGoo.movie.Actor;
import com.worldOfGoo.movie.Movie;
import com.worldOfGoo.resrc.Resources;
import com.worldOfGoo.text.strings;
import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class WOG1Movie extends Asset implements HasTime {

    public WOG1Movie(GameVersion version, Movie movie, Resources resources, strings strings) {
        super(version, resources, strings);

        this.movie = movie;

        for (EditorObject editorObject : getObjects()) {
            if (editorObject instanceof Actor actor) {
                String label = actor.getAttribute("label").stringValue();
                if (!label.isEmpty()) {
                    tryToAddText(label.substring(1));
                }
            }
        }

    }

    // ==========================================================================
    //  Special movie-specific things
    // ==========================================================================

    private final Movie movie;
    public Movie getMovie() {
        return movie;
    }

    private boolean isPlaying = false;
    @Override public boolean isPlaying() {
        return isPlaying;
    }
    @Override public void setPlaying(boolean playing) {
        isPlaying = playing;
    }

    private double time = 0;
    @Override public double getTime() {
        return time;
    }
    @Override public void setTime(double time) {
        this.time = time;
        onSetTime();
    }
    public void onSetTime() {
        double length = movie.getAttribute("length").doubleValue();
        while (time > length) time -= length;
        WOG1MovieGUI.updateTimeline(length == 0 ? 0 : time / length);
    }

    // ==========================================================================
    //  Asset selector
    // ==========================================================================

    public static final AssetSelector<WOG1Movie> assetSelector = new AssetSelector<>("Animation") {

        public List<String> getItems(GameVersion version) {
            List<String> items = new ArrayList<>();
            File[] animations = new File(FileManager.getGameDir(version) + "/res/movie").listFiles();
            if (animations == null) return items;
            for (File file : animations) {
                String fileName = file.getName();
                items.add(fileName);
            }
            return items;
        }

        public boolean isOriginal(String item, GameVersion version) {
            return BaseGameResources.MOVIE.get(version).contains(item);
        }

        protected WOG1Movie secretNewInstance(String name, GameVersion version) {
            Movie movie = ObjectCreator.create(Movie.class, null, version);
            Resources resources = ObjectCreator.create(Resources.class, null, version);
            com.worldOfGoo.text.strings strings = ObjectCreator.create(com.worldOfGoo.text.strings.class, null, version);
            return new WOG1Movie(version, movie, resources, strings);
        }

        protected WOG1Movie secretOpenInstance(File file, String name, GameVersion version) {
            return WOG1MovieOpener.openMovie(file, version);
        }

        protected FileChooser.ExtensionFilter getCustomExtensionFilter(GameVersion version) {
            return new FileChooser.ExtensionFilter("WoG Movie", (version == GameVersion.VERSION_WOG1_OLD ? "*.movie.binltl" : "*.movie.binuni"));
        }

        protected File getDefaultFileForName(String name, GameVersion version) {
            return new File(FileManager.getGameDir(version) + "/res/movie/" + name + "/" + name + ".movie." + (version == GameVersion.VERSION_WOG1_OLD ? "binltl" : "binuni"));
        }

        protected String getNameFromFile(File file, GameVersion version) {
            // Remove .movie.binltl or .movie.binuni extension from file name
            return file.getName().substring(0, file.getName().length() - 13);
        }
    };

    // ==========================================================================
    //  Common asset functions
    // ==========================================================================

    @Override
    public EditorObject getDefaultParent(Class<? extends EditorObject> toAdd) {
        // TODO: this
        return null;
    }

    @Override
    public ArrayList<Node> getGUIElements() {
        return WOG1MovieGUI.getGUIElements();
    }

    @Override
    public List<EditorObject> getObjects() {
        List<EditorObject> objects = new ArrayList<>();
        movie.addAllChildren(objects);
        return Collections.unmodifiableList(objects);
    }

    @Override
    public boolean isBaseGame() {
        return BaseGameResources.MOVIE.get(getVersion()).contains(getName());
    }

    @Override
    public boolean isScaleTooFar(double scaleX, double scaleY) {
        return false;
    }

    @Override
    public void onSet() {
        onSetTime();
        WOG1MovieGUI.updatePlaying(isPlaying);
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

    // ==========================================================================
    //  Asset management
    // ==========================================================================

    @Override
    public boolean save(File file) {
        return WOG1MovieWriter.saveMovie(this, file.getPath(), getVersion());
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
        // TODO: export WOG1Movie
    }

    @Override
    public void delete() {
        try {
            AssetUpdater.nuke(getFile());
        } catch (IOException e) {
            ErrorAlarm.show(e);
        }
    }

    @Override
    public Asset clone(String name) {

        Movie movie_ = ObjectUtil.deepClone(movie, null);

        Resources resources_ = ObjectUtil.deepClone(getResources(), null);
        resources_.setAttribute("id", "scene_" + name);

        com.worldOfGoo.text.strings strings_ = ObjectUtil.deepClone(getStrings(), null);

        WOG1Movie newMovie = new WOG1Movie(getVersion(), movie_, resources_, strings_);
        newMovie.setName(name);
        newMovie.setFile(new File(getFile().getParent() + "/" + name));
        return newMovie;
    }

    // ==========================================================================
    //  Tabs
    // ==========================================================================

    Tab tabAll = new Tab("Elements");
    Tab tabText = new Tab("Text");

    @Override
    public List<Tab> getTabs() {
        return List.of(tabAll, tabText);
    }

    @Override
    public void onSetTab(Tab tab) {
        if (this != AssetManager.getAsset() || tab == null) return;

        if (tab == tabAll) FXHierarchy.getHierarchy().setRoot(movie.getTreeItem());
        if (tab == tabText) FXHierarchy.getHierarchy().setRoot(getStrings().getTreeItem());

        FXHierarchy.getHierarchy().refresh();
        FXHierarchy.getHierarchy().getRoot().setExpanded(true);
        FXHierarchy.getHierarchy().setShowRoot(true);

    }

    @Override
    public Tab getTabForObject(EditorObject editorObject) {
        // TODO: this
        return null;
    }

}
