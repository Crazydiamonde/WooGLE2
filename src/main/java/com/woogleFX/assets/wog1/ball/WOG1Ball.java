package com.woogleFX.assets.wog1.ball;

import com.woogleFX.assets.*;
import com.woogleFX.assets.wog1.level.WOG1Level;
import com.woogleFX.editorObjects.EditorObject;
import com.woogleFX.editorObjects.ObjectManager;
import com.woogleFX.editorObjects.ObjectUtil;
import com.woogleFX.editorObjects.attributes.EditorAttribute;
import com.woogleFX.editorObjects.attributes.InputField;
import com.woogleFX.editorObjects.objectCreators.ObjectCreator;
import com.woogleFX.engine.AssetManager;
import com.woogleFX.engine.fx.hierarchy.FXHierarchy;
import com.woogleFX.engine.gui.AssetSelector;
import com.woogleFX.engine.gui.alarms.ErrorAlarm;
import com.woogleFX.file.FileManager;
import com.woogleFX.file.fileExport.GoomodExporter;
import com.woogleFX.file.resourceManagers.BaseGameResources;
import com.worldOfGoo.ball.ball;
import com.worldOfGoo.level.BallInstance;
import com.worldOfGoo.resrc.*;
import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.*;

/** Represents a Goo Ball in the original World of Goo.
 * File = "/res/balls/[ball name]" */
public class WOG1Ball extends Asset implements HasBackground {

    public WOG1Ball(GameVersion version, ball ball, Resources resources) {
        super(version, resources, null);

        this.ball = ball;

        getVisibilitySettings().addVisibilityStatus("bounds", 1);
        getVisibilitySettings().addVisibilityStatus("parts", 1);
        getVisibilitySettings().addVisibilityStatus("strand", 1);
        getVisibilitySettings().addVisibilityStatus("marker", 1);
        getVisibilitySettings().addVisibilityStatus("labels", 1);

        reAssignSetDefaultsToAllResources();

    }

    // ==========================================================================
    //  Special ball-specific things
    // ==========================================================================

    private final ball ball;
    public ball getBall() {
        return ball;
    }

    private Color backgroundColor = new Color(0.75, 0.75, 0.75, 1.0);
    @Override public Color getBackgroundColor() {
        return backgroundColor;
    }
    @Override public void setBackgroundColor(Color backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public void saveAndPlay(int numberOfBalls) {

        // place the balls in WooBLE

        // first off, open WooBLE
        WOG1Level ballEditorLevel;
        try {
            if (!WOG1Level.assetSelector.getItems(getVersion()).contains("WooBLE")) {

                // install the level ourselves.
                // first, get the level folder from the editor's directory
                Path wooBLEPath = Path.of(FileManager.getEditorLocation() + "/WooBLE");

                // then copy it into the game!
                Path destinationPath = Path.of(FileManager.getGameDir(getVersion()) + "/res/levels/WooBLE");

                try {
                    Files.copy(wooBLEPath, destinationPath, StandardCopyOption.REPLACE_EXISTING);
                    File[] children = wooBLEPath.toFile().listFiles();
                    if (children != null) for (File child : children) {
                        Files.copy(child.toPath(), Path.of(destinationPath + child.getPath().substring(wooBLEPath.toString().length())), StandardCopyOption.REPLACE_EXISTING);
                    }
                } catch (IOException e) {
                    ErrorAlarm.show(e);
                    return;
                    // TODO: better error alarm (like "an error occurred while trying to copy WooBLE: <error message>")
                }

                // TODO: make WooBLE levels for versions 1.5 and 2

            }
        } catch (IOException e) {
            ErrorAlarm.show(e);
            return;
        }

        try {
            ballEditorLevel = WOG1Level.assetSelector.openInstance("WooBLE", getVersion());
        } catch (IOException e) {
            ErrorAlarm.show(e);
            return;
        }
        // remove all BallInstances whose names start with "testgoo"
        for (EditorObject objInWooBLE : ballEditorLevel.getObjects()) {
            if (objInWooBLE instanceof BallInstance &&
                    objInWooBLE.getAttribute("id").stringValue().startsWith("testgoo")) {
                ObjectManager.deleteItem(ballEditorLevel, objInWooBLE);
            }
        }

        // add ours!
        // temporary method: place in a grid from (100, -1000) to (400, -700) in increments of 10 units
        int i = 0;
        for (int y = 700; y < 1000 && i < numberOfBalls; y += 10) {
            for (int x = 100; x < 400 && i < numberOfBalls; x += 10) {
                BallInstance testGoo = ObjectCreator.create(BallInstance.class, ballEditorLevel.getLevel(), ballEditorLevel.getVersion());
                testGoo.setAttribute("id", "testgoo" + i);
                testGoo.setAttribute("type", getBall().getAttribute("name").stringValue());
                testGoo.setAttribute("x", x);
                testGoo.setAttribute("y", y);
                i++;
            }
        }

        // now save the level
        ballEditorLevel.save(ballEditorLevel.getFile());

        try {
            ProcessBuilder processBuilder = new ProcessBuilder(
                    FileManager.getGameDir(GameVersion.VERSION_WOG1_OLD) + "/WorldOfGoo.exe", "WooBLE");
            processBuilder.directory(new File(FileManager.getGameDir(GameVersion.VERSION_WOG1_OLD)));
            processBuilder.start();
        } catch (Exception e) {
            ErrorAlarm.show(e);
        }

    }

    // ==========================================================================
    //  Asset selector
    // ==========================================================================

    public static final AssetSelector<WOG1Ball> assetSelector = new AssetSelector<>("Ball") {

        public List<String> getItems(GameVersion version) {
            List<String> balls = new ArrayList<>();
            File[] ballFiles = new File(FileManager.getGameDir(version) + "/res/balls").listFiles();
            if (ballFiles == null) return balls;
            for (File child : ballFiles) balls.add(child.getName());
            return balls;
        }

        public boolean isOriginal(String item, GameVersion version) {
            return BaseGameResources.GOO_BALL_TYPES.get(version).contains(item);
        }

        protected WOG1Ball secretNewInstance(String name, GameVersion version) {
            return WOG1BallOpener.newBall(name, version);
        }

        protected WOG1Ball secretOpenInstance(File file, String name, GameVersion version) throws IOException {
            return WOG1BallOpener.openBall(file, version);
        }

        protected FileChooser.ExtensionFilter getCustomExtensionFilter(GameVersion version) {
            String suffix = (version == GameVersion.VERSION_WOG1_OLD) ? ".xml.bin" : ".xml";
            return new FileChooser.ExtensionFilter("WoG Ball", "balls" + suffix);
        }

        protected File getDefaultFileForName(String name, GameVersion version) {
            return new File(FileManager.getGameDir(version) + "/res/balls/" + name);
        }

        protected String getNameFromFile(File file, GameVersion version) {
            return file.getParent().substring(file.getParent().lastIndexOf("/") + 1);
        }

    };

    // ==========================================================================
    //  Common asset functions
    // ==========================================================================

    @Override
    public EditorObject getDefaultParent(Class<? extends EditorObject> toAdd) {
        if (toAdd.getPackage().getName().endsWith("resrc")) {
            return getResources();
        } else {
            return ball;
        }
    }

    @Override
    public ArrayList<Node> getGUIElements() {
        return WOG1BallGUI.getGUIElements();
    }

    @Override
    public List<EditorObject> getObjects() {
        List<EditorObject> objects = new ArrayList<>();
        ball.addAllChildren(objects);
        return Collections.unmodifiableList(objects);
    }

    @Override
    public void resetCamera() {
        setOffsetX(250);
        setOffsetY(200);
        setZoom(50.0 / Double.parseDouble(ball.getAttribute("shape").listValue()[1]));
    }

    @Override
    public List<AssetError> verify() {

        List<AssetError> errors = new ArrayList<>();

        // zero mass
        if (ball.getAttribute("mass").doubleValue() == 0) {
            errors.add(AssetVerifier.buildFromText(ball,
                    "",
                    "Ball",
                    "has zero mass!"));
        }



        return errors;

    }

    @Override
    public boolean isBaseGame() {
        return BaseGameResources.GOO_BALL_TYPES.get(getVersion()).contains(getName());
    }

    @Override
    public boolean isScaleTooFar(double scaleX, double scaleY) {
        return false;
    }

    // ==========================================================================
    //  Asset management
    // ==========================================================================

    @Override
    public boolean save(File file) {
        try {
            WOG1BallWriter.saveAsXML(this, file.getPath(), getVersion(), false);
            return true;
        } catch (IOException e) {
            ErrorAlarm.show(e);
            return false;
        }
    }

    @Override
    public void load() {
        super.load();

        for (EditorObject editorObject : getObjects()) {
            editorObject.onLoaded(this);
        }

        for (EditorObject editorObject : getResources().getChildren()) {
            editorObject.onLoaded(this);
        }

    }

    @Override
    public void export(boolean includeAddinInfo) {
        GoomodExporter.export(this, includeAddinInfo);
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

        ball ball_ = ObjectUtil.deepClone(ball, null);
        ball_.setAttribute("name", name);

        Resources resources = ObjectUtil.deepClone(getResources(), null);
        resources.setAttribute("id", "ball_" + name);

        File newBallFile = new File(getFile().getParent() + "/" + name);
        if (!Files.exists(newBallFile.toPath())) {
            try {
                Files.createDirectory(newBallFile.toPath());
            } catch (IOException e) {
                ErrorAlarm.show(e);
                return null;
            }
        }

        // copy all of the used resources in this directory to the new location
        // do this by looking at what the resource objects use
        for (EditorObject resourcesChild : getResources().getChildren()) {
            if (resourcesChild instanceof ResourceInterface resourceInterface) {
                Path resourcePath = Path.of(FileManager.getGameDir(getVersion()) +
                        "/" + resourceInterface.getAdjustedPath());
                // look for a file starting with that name
                File[] files = resourcePath.getParent().toFile().listFiles();
                if (files == null) break; // not sure what to do in this case
                for (File file : files) {
                    if (file.getPath().substring(0, file.getPath().lastIndexOf('.'))
                            .equals(resourcePath.toString()) && file.getPath().startsWith(getFile().getPath())) {
                        // copy it over
                        try {
                            Path destPath = Path.of(newBallFile + "/" +
                                    file.getPath().substring(getFile().getPath().length()));
                            if (!Files.exists(destPath.getParent()))
                                Files.createDirectory(destPath.getParent());
                            Files.copy(file.toPath(), destPath);
                        } catch (IOException e) {
                            ErrorAlarm.show(e);
                        }
                        break;
                    }
                }
            }
        }

        // modify the resource objects
        for (EditorObject editorObject : resources.getChildren()) {
            if (editorObject instanceof SetDefaults setDefaults) {
                // this technically doesnt work cause someone could have res/balls/<name>
                // arbitrarily in the file path somewhere else but for the time being i dont care

                String newPath = setDefaults.getAttribute("path").stringValue()
                        .replace("res/balls/" + getName(),
                                "res/balls/" + name);
                setDefaults.setAttribute("path", newPath);

                String newIdPrefix = setDefaults.getAttribute("idprefix").stringValue()
                        .replace("res/balls/" + getName().toUpperCase(),
                                "res/balls/" + name.toUpperCase());
                setDefaults.setAttribute("idprefix", newIdPrefix);

            }
        }

        // now change EVERY SINGLE OBJECT
        // search every attribute
        List<EditorObject> children = new ArrayList<>();
        ball_.addAllChildren(children);
        resources.addAllChildren(children);
        for (EditorObject editorObject : children) {
            for (EditorAttribute editorAttribute : editorObject.getAttributes()) {
                InputField type = editorAttribute.getType();

                if (type == InputField._1_IMAGE || (editorObject instanceof SetDefaults)) {
                    if (editorAttribute.stringValue().contains("IMAGE_BALL_" + getName().toUpperCase())) {
                        editorAttribute.setValue(editorAttribute.stringValue()
                                .replace("IMAGE_BALL_" + getName().toUpperCase(),
                                        "IMAGE_BALL_" + name.toUpperCase()));
                    }
                }

                if (type == InputField._1_SOUND || (editorObject instanceof SetDefaults)) {
                    if (editorAttribute.stringValue().contains("SOUND_BALL_" + getName().toUpperCase())) {
                        editorAttribute.setValue(editorAttribute.stringValue()
                                .replace("SOUND_BALL_" + getName().toUpperCase(),
                                        "SOUND_BALL_" + name.toUpperCase()));
                    }
                }

            }
        }

        WOG1Ball newBall = new WOG1Ball(getVersion(), ball_, resources);
        newBall.setName(name);
        newBall.setFile(newBallFile);
        newBall.load();
        return newBall;

    }

    // ==========================================================================
    //  Tabs
    // ==========================================================================

    Tab tabBall = new Tab("Ball");
    Tab tabResrc = new Tab("Resrc");
    Tab tabAddin = new Tab("Addin");

    @Override
    public List<Tab> getTabs() {
        return List.of(tabBall, tabResrc, tabAddin);
    }

    @Override
    public void onSetTab(Tab tab) {
        if (this != AssetManager.getAsset()) return;

        EditorObject rootObject;
        if (tab == tabBall) rootObject = ball;
        else if (tab == tabResrc) rootObject = getResources();
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
        while (absoluteParent.getParent() != null) {
            absoluteParent = absoluteParent.getParent();
        }

        if (absoluteParent instanceof ball) return tabBall;
        else if (absoluteParent instanceof ResourceManifest) return tabResrc;
        else if (absoluteParent instanceof com.worldOfGoo.addin.addin) return tabAddin;
        else return null;
    }

}
