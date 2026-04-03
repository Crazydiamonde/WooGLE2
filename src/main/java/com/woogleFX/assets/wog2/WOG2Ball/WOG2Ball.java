package com.woogleFX.assets.wog2.WOG2Ball;

import com.woogleFX.assets.AssetError;
import com.woogleFX.editorObjects.EditorObject;
import com.woogleFX.assets.Asset;
import com.woogleFX.engine.fx.FXContainers;
import com.woogleFX.engine.fx.propertiesView.FXPropertiesView;
import com.woogleFX.engine.fx.assetSelectPane.FXAssetSelectPane;
import com.woogleFX.engine.fx.hierarchy.FXHierarchy;
import com.woogleFX.engine.fx.hierarchy.FXHierarchySwitcherButtons;
import com.woogleFX.engine.gui.AssetSelector;
import com.woogleFX.engine.gui.LoadingScreen;
import com.woogleFX.engine.gui.alarms.ErrorAlarm;
import com.woogleFX.file.FileManager;
import com.woogleFX.file.fileExport.GOOWriter;
import com.woogleFX.file.resourceManagers.BaseGameResources;
import com.woogleFX.assets.GameVersion;
import com.worldOfGoo.addin.addin;
import com.worldOfGoo.resrc.*;
import com.worldOfGoo.text.strings;
import com.worldOfGoo2.ball.*;
import javafx.concurrent.Task;
import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.control.TreeItem;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;

public class WOG2Ball extends Asset {

    private final Ball ball;
    public Ball getBall() {
        return ball;
    }

    @Override
    public List<EditorObject> getObjects() {
        List<EditorObject> objects = new ArrayList<>();
        ball.addAllChildren(objects);
        return Collections.unmodifiableList(objects);
    }


    public static final AssetSelector<WOG2Ball> assetSelector = new AssetSelector<>("Ball") {

        private static final Map<Pair<String, GameVersion>, WOG2Ball> importedBalls = new HashMap<>();

        @Override
        public List<String> getItems(GameVersion version) {
            List<String> balls = new ArrayList<>();
            File[] ballFiles = new File(FileManager.getGameDir(version) + "/res/balls").listFiles();
            if (ballFiles == null) return balls;
            for (File child : ballFiles) balls.add(child.getName());
            return balls;
        }

        @Override
        public boolean isOriginal(String item, GameVersion version) {
            return BaseGameResources.GOO_BALL_TYPES.get(version).contains(item);
        }

        @Override
        protected WOG2Ball secretNewInstance(String name, GameVersion version) {
            // TODO2: new WOG2Ball
            return new WOG2Ball(null, null, null);
        }

        @Override
        protected WOG2Ball secretOpenInstance(File file, String name, GameVersion version) {

            WOG2Ball importedBall = importedBalls.get(new Pair<>(name, version));
            if (importedBall != null) return importedBall;

            WOG2Ball ball = WOG2BallOpener.open2Ball(name, version);
            if (ball == null) return null;

            ball.load();
            importedBalls.put(new Pair<>(name, version), ball);
            return ball;

        }

        @Override
        protected FileChooser.ExtensionFilter getCustomExtensionFilter(GameVersion version) {
            return null;
        }

        @Override
        protected File getDefaultFileForName(String name, GameVersion version) {
            return new File(FileManager.getGameDir(version) + "/res/balls/" + name);
        }

        @Override
        protected String getNameFromFile(File file, GameVersion version) {
            return null;
        }
    };


    @Override
    public void resetCamera() {

        double canvasWidth = FXContainers.getSplitPane().getDividers().get(0).getPosition() * FXContainers.getSplitPane().getWidth();
        double canvasHeight = FXContainers.getSplitPane().getHeight();

        setZoom(canvasWidth / 6);

        setOffsetX(-1.5 * getZoom() + canvasWidth / 2);
        setOffsetY(-1.6 * getZoom() + canvasHeight / 2);

    }


    private String shapeType;
    public String getShapeType() {
        return shapeType;
    }
    public void setShapeType(String shapeType) {
        this.shapeType = shapeType;
    }


    private double width;
    public double getWidth() {
        return width;
    }
    public void setWidth(double width) {
        this.width = width;
    }

    private double height;
    public double getHeight() {
        return height;
    }
    public void setHeight(double height) {
        this.height = height;
    }


    public WOG2Ball(ArrayList<EditorObject> objects, Resources resources, strings strings) {
        super(GameVersion.VERSION_WOG2, resources, strings);

        ball = (Ball) objects.get(0);

        shapeType = "circle";

        width = objects.get(0).getAttribute("width").doubleValue();
        height = objects.get(0).getAttribute("height").doubleValue();

        reAssignSetDefaultsToAllResources();
        resetCamera();

    }

    @Override
    public List<AssetError> verify() {
        return new ArrayList<>();
    }

    @Override
    public boolean save(File file) {

        StringBuilder export = new StringBuilder();
        GOOWriter.recursiveGOOExport(export, ball, 0);

        try {
            Files.writeString(file.toPath(), export.toString());
            return true;
        } catch (IOException e) {
            ErrorAlarm.show(e);
            return false;
        }

    }

    @Override
    public void export(boolean includeAddinInfo) {
        System.out.println("Exporting ball");
    }

    @Override
    public void delete() {

    }

    @Override
    public boolean isScaleTooFar(double scaleX, double scaleY) {
        return false;
    }


    Tab parts = new Tab("Parts");
    Tab stateAnimations = new Tab("State Animations");
    Tab soundEvents = new Tab("Sound Events");
    Tab particleEffects = new Tab("Particle Effects");

    @Override
    public List<Tab> getTabs() {
        return List.of(parts, stateAnimations, soundEvents, particleEffects);
    }

    @Override
    public void onSetTab(Tab tab) {

        TreeItem<EditorObject> root = ball.getTreeItem();
        FXHierarchy.getHierarchy().setRoot(root);

        root.getChildren().clear();

        for (EditorObject child : ball.getChildren()) {
            if (tab == parts && child instanceof Part) root.getChildren().add(child.getTreeItem());
            if (tab == stateAnimations && child instanceof StateAnimation) root.getChildren().add(child.getTreeItem());
            if (tab == soundEvents && child instanceof SoundEvent) root.getChildren().add(child.getTreeItem());
            if (tab == particleEffects && child instanceof ParticleEffect) root.getChildren().add(child.getTreeItem());
        }

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
        LoadingScreen loadingScreen = new LoadingScreen();

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() {

                long count = getObjects().size();

                long i = 0;
                try {
                    for (EditorObject object : getObjects().toArray(new EditorObject[0])) {
                        object.onLoaded(WOG2Ball.this);
                        object.update();
                        i++;
                        updateProgress(i, count);
                    }
                } catch (Exception e) {
                    // logger.error("", e);
                }

                return null;
            }
        };

        loadingScreen.setAssetName("Ball");
        loadingScreen.setTask(task);
        // TODO: send stage to main application thread so it works
        //Stage stage = new Stage();
        //loadingScreen.start(stage);
        //task.setOnSucceeded(event -> stage.close());
        //task.setOnCancelled(event -> stage.close());
        //task.setOnFailed(event -> stage.close());
        new Thread(task).start();

        //stage.setOnCloseRequest(event -> {
        //    task.cancel();
        //    FXAssetSelectPane.getAssetSelectPane().getTabs().remove(getAssetTab());
        //});

        // ball.getTreeItem().setExpanded(true);
        // TODO: do this only when the ball is the main thing being loaded!!
        //FXHierarchy.getHierarchy().setRoot(ball.getTreeItem());

        // FXPropertiesView.getPropertiesView().setRoot(FXPropertiesView.makePropertiesViewTreeItem(new EditorObject[]{ ball }));

        // FXHierarchySwitcherButtons.getHierarchySwitcherButtons().getSelectionModel().select(0);

    }

    @Override
    public boolean isBaseGame() {
        // TODO2: Wait for custom Goo Balls to be possible
        return false; // BaseGameResources.GOO_BALL_TYPES.get(getVersion()).contains(getLevelName());
    }

    @Override
    public ArrayList<Node> getGUIElements() {
        return new ArrayList<>();
    }

}
