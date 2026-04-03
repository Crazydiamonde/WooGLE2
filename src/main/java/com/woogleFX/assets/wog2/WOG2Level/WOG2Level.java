package com.woogleFX.assets.wog2.WOG2Level;

import com.woogleFX.assets.AssetError;
import com.woogleFX.editorObjects.EditorObject;
import com.woogleFX.assets.Asset;
import com.woogleFX.editorObjects.objectCreators.ObjectCreator;
import com.woogleFX.engine.AssetManager;
import com.woogleFX.engine.fx.FXContainers;
import com.woogleFX.engine.fx.editorButtons.FXEditorButtons;
import com.woogleFX.engine.fx.propertiesView.FXPropertiesView;
import com.woogleFX.engine.fx.assetSelectPane.FXAssetSelectPane;
import com.woogleFX.engine.fx.hierarchy.FXHierarchy;
import com.woogleFX.engine.fx.hierarchy.FXHierarchySwitcherButtons;
import com.woogleFX.engine.fx.menu.FXMenu;
import com.woogleFX.engine.gui.AssetSelector;
import com.woogleFX.engine.gui.LoadingScreen;
import com.woogleFX.engine.gui.alarms.ErrorAlarm;
import com.woogleFX.file.FileManager;
import com.woogleFX.file.fileExport.GOOWriter;
import com.woogleFX.file.fileExport.Goo2modExporter;
import com.woogleFX.file.resourceManagers.BaseGameResources;
import com.woogleFX.assets.GameVersion;
import com.worldOfGoo.resrc.Resources;
import com.worldOfGoo.text.strings;
import com.worldOfGoo2.level._2_Level;
import com.worldOfGoo2.level.Strand;
import com.worldOfGoo2.level._2_Level_TerrainGroup;
import com.worldOfGoo2.misc.Point;
import com.worldOfGoo2.util.ItemHelper;
import javafx.concurrent.Task;
import javafx.event.EventType;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

/** A level from World of Goo 2.
 * What... is this? */
public class WOG2Level extends Asset {

    private static final Logger logger = LoggerFactory.getLogger(WOG2Level.class);


    private final _2_Level level;
    public _2_Level getLevel() {
        return level;
    }

    @Override
    public List<EditorObject> getObjects() {
        List<EditorObject> objects = new ArrayList<>();
        level.addAllChildren(objects);
        return Collections.unmodifiableList(objects);
    }


    public static final AssetSelector<WOG2Level> assetSelector = new AssetSelector<>("Level") {

        @Override
        public List<String> getItems(GameVersion version) {
            List<String> levels = new ArrayList<>();
            File[] levelFiles = new File(FileManager.getGameDir(version) + "/res/levels").listFiles();
            if (levelFiles == null) return levels;
            for (File child : levelFiles) {
                String childName = child.getName();
                if (childName.endsWith(".wog2")) {
                    levels.add(childName.substring(0, childName.length() - 5));
                }
            }
            return levels;
        }

        @Override
        public boolean isOriginal(String item, GameVersion version) {
            return BaseGameResources.LEVELS.get(version).contains(item);
        }

        @Override
        protected WOG2Level secretNewInstance(String name, GameVersion version) {
            _2_Level levelObject = ObjectCreator.create(_2_Level.class, null, "level", version);
            EditorObject topRight = ObjectCreator.create(Point.class, levelObject, "boundsTopRight", version);
            topRight.setAttribute("x", 5);
            topRight.setAttribute("y", 5);
            EditorObject bottomLeft = ObjectCreator.create(Point.class, levelObject, "boundsBottomLeft", version);
            bottomLeft.setAttribute("x", -5);
            bottomLeft.setAttribute("y", -5);
            levelObject.setAttribute("boundsBottomLeft", "-5,-5");
            levelObject.setAttribute("boundsTopRight", "5,5");
            levelObject.setAttribute("version", 2);
            levelObject.setAttribute("type", 0);
            levelObject.setAttribute("uuid", "12345");
            levelObject.setAttribute("title", "My Test Level");
            levelObject.setAttribute("environmentId", 0);
            levelObject.setAttribute("backgroundId", "");

            EditorObject gravity = ObjectCreator.create(Point.class, levelObject, "gravity", version);
            gravity.setAttribute("x", 0);
            gravity.setAttribute("y", -10);
            levelObject.setAttribute("gravity", "0,-10");

            EditorObject initialCameraPos = ObjectCreator.create(Point.class, levelObject, "initialCameraPos", version);
            initialCameraPos.setAttribute("x", 0);
            initialCameraPos.setAttribute("y", 0);
            levelObject.setAttribute("initialCameraPos", "0,0");

            levelObject.setAttribute("initialCameraZoom", 1);
            levelObject.setAttribute("cameraAutoBounds", "false");
            levelObject.setAttribute("ballsRateRequired", 50);
            levelObject.setAttribute("musicId", "");
            levelObject.setAttribute("ambienceId", "");
            levelObject.setAttribute("musicOffset", 0);
            levelObject.setAttribute("ambienceOffset", 0);
            levelObject.setAttribute("pretickSeconds", 0);
            levelObject.setAttribute("enableTimeBugs", false);

            // TODO2:
            WOG2Level level = new WOG2Level(levelObject, null, null);
            FXEditorButtons.updateAllButtons();
            FXMenu.updateAllButtons();

            for (EditorObject object : level.getObjects()) {
                object.update();
                object.onLoaded(level);
            }

            // Put everything in the hierarchy
            level.getLevel().getTreeItem().setExpanded(true);
            FXHierarchy.getHierarchy().setRoot(level.getLevel().getTreeItem());

            // Add items from the Scene to it
            FXPropertiesView.getPropertiesView().setRoot(FXPropertiesView.makePropertiesViewTreeItem(new EditorObject[]{level.getLevel()}));

            return level;
        }

        @Override
        protected WOG2Level secretOpenInstance(File file, String name, GameVersion version) {
            try {
                return WOG2LevelOpener.openLevel(name, version);
            } catch (ParserConfigurationException | SAXException | IOException e) {
                return null;
            }
        }

        @Override
        protected FileChooser.ExtensionFilter getCustomExtensionFilter(GameVersion version) {
            return null;
        }

        @Override
        protected File getDefaultFileForName(String name, GameVersion version) {
            return new File(FileManager.getGameDir(version) + "/res/levels/" + name + ".wog2");
        }

        @Override
        protected String getNameFromFile(File file, GameVersion version) {
            return null;
        }
    };

    public WOG2Level(_2_Level level, Resources resources, strings strings) {
        super(GameVersion.VERSION_WOG2, resources, strings);

        this.level = level;

        getVisibilitySettings().addVisibilityStatus("cameras", 1);
        getVisibilitySettings().addVisibilityStatus("goos", 2);
        getVisibilitySettings().addVisibilityStatus("graphics", 1);
        getVisibilitySettings().addVisibilityStatus("scene", 1);

        // AssetManager.setAsset(this);

        resetCamera();

    }

    @Override
    public void resetCamera() {
        EditorObject boundsBottomLeft = level.getChildren("boundsBottomLeft").get(0);
        EditorObject boundsTopRight = level.getChildren("boundsTopRight").get(0);

        double sceneWidth = boundsTopRight.getAttribute("x").doubleValue() - boundsBottomLeft.getAttribute("x").doubleValue();
        double sceneHeight = boundsTopRight.getAttribute("y").doubleValue() - boundsBottomLeft.getAttribute("y").doubleValue();
        double middleX = (boundsBottomLeft.getAttribute("x").doubleValue() + boundsTopRight.getAttribute("x").doubleValue()) / 2;
        double middleY = (boundsBottomLeft.getAttribute("y").doubleValue() + boundsTopRight.getAttribute("y").doubleValue()) / 2;

        double canvasWidth = FXContainers.getSplitPane().getDividers().get(0).getPosition() * FXContainers.getSplitPane().getWidth();
        double canvasHeight = FXContainers.getSplitPane().getHeight();

        double zoomX = canvasWidth / sceneWidth;
        double zoomY = canvasHeight / sceneHeight;

        setZoom(Math.min(Math.abs(zoomX), Math.abs(zoomY)));

        setOffsetX(-middleX * getZoom() + canvasWidth / 2);
        setOffsetY(middleY * getZoom() + canvasHeight / 2);
    }

    @Override
    public List<AssetError> verify() {
        ArrayList<String> errors = new ArrayList<>();

        // Check strands for type=""
        for (EditorObject editorObject : getObjects()) if (editorObject instanceof Strand strand) {
            if (strand.getAttribute("type").stringValue().isEmpty()) {
                errors.add("Strand (" + strand.getAttribute("ball1UID").stringValue() +
                        "-" + strand.getAttribute("ball2UID") + ") has no type set");
                break;
            }
        }

        // Check level for initialCameraZoom=0
        if (level.getAttribute("initialCameraZoom").doubleValue() == 0) {
            errors.add("Level initialCameraZoom is zero");
        }

        return new ArrayList<>();

    }

    @Override
    public boolean save(File file) {

        StringBuilder export = new StringBuilder();
        GOOWriter.recursiveGOOExport(export, getLevel(), 0);

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
        try {
            Goo2modExporter.exportGoo2mod(this, includeAddinInfo);
        } catch (IOException e) {
            logger.error("", e);
        }
    }

    @Override
    public void delete() {
        try {
            Files.delete(Path.of(FileManager.getGameDir(GameVersion.VERSION_WOG2) + "/res/levels/" + getName() + ".wog2"));
        } catch (IOException e) {
            ErrorAlarm.show(e);
        }
    }

    @Override
    public boolean isScaleTooFar(double scaleX, double scaleY) {
        return (scaleX < 3 || scaleX > 100000 || scaleY < 3 || scaleY > 100000);
    }


    Tab terrain = new Tab("Terrain");
    Tab terrainGroups = new Tab("Terrain Groups");
    Tab balls = new Tab("Balls");
    Tab items = new Tab("Items");
    Tab pins = new Tab("Pins");
    Tab camera = new Tab("Camera");
    Tab addin = new Tab("Addin");

    @Override
    public List<Tab> getTabs() {
        return List.of(terrain, terrainGroups, balls, items, pins, camera, addin);
    }

    @Override
    public void onSetTab(Tab tab) {

        TreeItem<EditorObject> root = level.getTreeItem();
        FXHierarchy.getHierarchy().setRoot(root);

        root.getChildren().clear();

        for (EditorObject child : level.getChildren()) {

            if (((child.getType().equals("BallInstance") || child.getType().equals("Strand")) && child.getAttribute("type").stringValue().equals("Terrain")) && tab == terrain) root.getChildren().add(child.getTreeItem());
            else if (child instanceof _2_Level_TerrainGroup && tab == terrainGroups) root.getChildren().add(child.getTreeItem());
            else if (((child.getType().equals("BallInstance") || child.getType().equals("Strand")) && !child.getAttribute("type").stringValue().equals("Terrain")) && tab == balls) root.getChildren().add(child.getTreeItem());
            else if (child.getType().equals("Item") && tab == items) root.getChildren().add(child.getTreeItem());
            else if (child.getType().equals("Pin") && tab == pins) root.getChildren().add(child.getTreeItem());
            else if (child.getType().equals("CameraKeyFrame") && tab == camera) root.getChildren().add(child.getTreeItem());

        }

        if (tab == addin) root.getChildren().add(getAddin().getTreeItem());

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
        // TODO2:
        /*
        StringBuilder levelExport = new StringBuilder();
        GOOWriter.recursiveGOOExport(levelExport, getLevel(), 0);
        EditorObject levelObject = ObjectGOOParser.read(_2_Level.class, levelExport.toString(), "level");
        ArrayList<EditorObject> objects = new ArrayList<>();
        Stack<EditorObject> toAdd = new Stack<>();
        toAdd.push(levelObject);
        while (!toAdd.isEmpty()) {
            EditorObject thisObject = toAdd.pop();
            objects.add(thisObject);
            for (EditorObject child : thisObject.getChildren()) {
                toAdd.push(child);
            }

        }

        ArrayList<EditorObject> addinList = new ArrayList<>();
        // Generate new addin object. IDK why cloning it doesn't work, but this is arguably better anyway
        FileManager.supremeAddToList(addinList, BlankObjectGenerator.generateBlankAddinObject(name, getVersion()));

        WOG2Level level = new WOG2Level(objects, addinList);

        for (EditorObject object : level.getObjects()) {
            object.update();
            object.onLoaded(level);
        }

        //FileManager.supremeAddToList(objectsList, ObjectUtil.deepClone(before.getLevel(), null));


        level.setName(name);
        FXEditorButtons.updateAllButtons();
        FXMenu.updateAllButtons();

        // Put everything in the hierarchy
        level.getLevel().getTreeItem().setExpanded(true);
        FXHierarchy.getHierarchy().setRoot(level.getLevel().getTreeItem());
        // Add items from the Scene to it
        FXPropertiesView.getPropertiesView().setRoot(FXPropertiesView.makePropertiesViewTreeItem(new EditorObject[]{level.getLevel()}));

        return level;

         */

        return null;

    }

    @Override
    public void load() {
        super.load();
        System.out.println("I STARTED LOADING!!");
        LoadingScreen loadingScreen = new LoadingScreen();

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() {

                long count = getObjects().size();

                long i = 0;
                try {

                    for (EditorObject object : getObjects().toArray(new EditorObject[0])) {
                        object.onLoaded(WOG2Level.this);
                        i++;
                        updateProgress(i, count);
                    }
                    System.out.println("Performance 1:");
                    for (EditorObject ball : getLevel().getChildren("balls")) {
                        EditorObject terrainBall = getLevel().getChildren("terrainBalls").get(0);
                        getLevel().getChildren().remove(terrainBall);
                        ball.setAttribute("terrainGroup", terrainBall.getAttribute("group").stringValue());
                    }
                    for (EditorObject terrainGroup : getLevel().getChildren("terrainGroups"))
                        if (terrainGroup instanceof _2_Level_TerrainGroup terrainGroup1) {
                        terrainGroup1.stopIgnoringUpdates();
                        terrainGroup1.update();
                    }
                } catch (Exception e) {
                    logger.error("", e);
                }

                System.out.println("Finished.");

                return null;
            }
        };

        loadingScreen.setAssetName("Level");
        loadingScreen.setTask(task);
        Stage stage = new Stage();
        loadingScreen.start(stage);
        task.setOnSucceeded(event -> {
            FXHierarchySwitcherButtons.getHierarchySwitcherButtons().getSelectionModel().select(1);
            FXHierarchySwitcherButtons.getHierarchySwitcherButtons().getSelectionModel().select(0);
            stage.close();
        });
        task.setOnCancelled(event -> {
            FXHierarchySwitcherButtons.getHierarchySwitcherButtons().getSelectionModel().select(1);
            FXHierarchySwitcherButtons.getHierarchySwitcherButtons().getSelectionModel().select(0);
            stage.close();
        });
        task.setOnFailed(event -> {
            FXHierarchySwitcherButtons.getHierarchySwitcherButtons().getSelectionModel().select(1);
            FXHierarchySwitcherButtons.getHierarchySwitcherButtons().getSelectionModel().select(0);
            stage.close();
        });
        new Thread(task).start();

        stage.setOnCloseRequest(event -> {
            task.cancel();
            FXAssetSelectPane.getAssetSelectPane().getTabs().remove(getAssetTab());
        });

        level.getTreeItem().setExpanded(true);
        FXHierarchy.getHierarchy().setRoot(level.getTreeItem());

        FXPropertiesView.getPropertiesView().setRoot(FXPropertiesView.makePropertiesViewTreeItem(new EditorObject[]{level}));

        FXHierarchySwitcherButtons.getHierarchySwitcherButtons().getSelectionModel().select(0);
        System.out.println("I FINISHED LOADING!!");

    }

    @Override
    public boolean isBaseGame() {
        return BaseGameResources.LEVELS.get(getVersion()).contains(getName());
    }


    public static ArrayList<Boolean> comboBoxList = new ArrayList<>();
    public static int comboBoxSelected = -1;

    private static final FXEditorButtons.EditorButton buttonViewTerrainGroup = new FXEditorButtons.EditorButton() {
        @Override
        public void updateDisabled() {
            setDisable(false);
        }
    };
    static {
        buttonViewTerrainGroup.setGraphic(new MenuButton());
    }

    public static FXEditorButtons.EditorButton getButtonViewTerrainGroup() {
        return buttonViewTerrainGroup;
    }

    public static void updateTerrainGroupSelector(WOG2Level level) {

        MenuButton content = (MenuButton) buttonViewTerrainGroup.getGraphic();
        content.getItems().clear();
        ImageView graphic = new ImageView(FileManager.getIcon("ButtonIcons/ShowHide/showhide_terrain.png"));
        content.setGraphic(graphic);
        buttonViewTerrainGroup.setStyle("-fx-background-insets: 0,0,0; -fx-padding: 0 0 0 0;");
        content.setStyle("-fx-background-insets: 0,0,0; -fx-padding: -4 -8 -4 -8;");
        comboBoxList.clear();
        int i = 0;
        for (EditorObject terrainGroup : level.getLevel().getChildren("terrainGroups")) {

            CheckBox checkBox = new CheckBox(i + (terrainGroup.getAttribute("foreground").booleanValue() ? "" : "*"));
            checkBox.setSelected(true);
            int finalI = i;
            checkBox.selectedProperty().addListener((observable, oldValue, newValue) ->
                    comboBoxList.set(finalI, newValue));

            CustomMenuItem menuItem = new CustomMenuItem(checkBox);

            menuItem.setHideOnClick(false);

            checkBox.addEventHandler(EventType.ROOT, event -> {
                if (event instanceof MouseEvent mouseEvent && mouseEvent.getEventType() == MouseEvent.MOUSE_ENTERED) {
                    comboBoxSelected = finalI;
                } else if (event instanceof MouseEvent mouseEvent && mouseEvent.getEventType() == MouseEvent.MOUSE_EXITED) {
                    comboBoxSelected = -1;
                }
                if (event instanceof MouseEvent mouseEvent && event.getEventType() == MouseEvent.MOUSE_CLICKED) {
                    if (mouseEvent.isControlDown()) {
                        for (MenuItem menuItem1 : content.getItems()) {
                            CheckBox checkBox1 = (CheckBox) ((CustomMenuItem)menuItem1).getContent();
                            checkBox1.setSelected(mouseEvent.isShiftDown());
                        }
                        checkBox.setSelected(!mouseEvent.isShiftDown());
                    }
                }
            });

            content.getItems().add(menuItem);
            comboBoxList.add(true);

            i++;

        }

    }


    private static final FXEditorButtons.EditorButton buttonAddItem = new FXEditorButtons.EditorButton() {
        @Override
        public void updateDisabled() {
            setDisable(false);
        }
    };
    static {
        MenuButton menuButton = new MenuButton("Add Items");
        buttonAddItem.setGraphic(menuButton);
        buttonAddItem.setTooltip(new FXEditorButtons.DelayedTooltip("Add Items"));
    }


    public static synchronized void updateItemsSelector(WOG2Level wog2Level) {
        MenuButton content = (MenuButton) buttonAddItem.getGraphic();
        content.getItems().clear();
        for (Map.Entry<Integer, String> entry : ItemHelper.itemTypeMap.entrySet()) {
            Menu item = new Menu(entry.getValue());
            // TODO2:
            /*
            for (Map.Entry<String, _2_Item> loadedItemEntry : ItemManager.itemMap.entrySet()) {
                if (loadedItemEntry.getValue().getAttribute("type").intValue() == entry.getKey()) {
                    MenuItem sub = new MenuItem(loadedItemEntry.getKey());
                    sub.setOnAction(e -> {
                        EditorObject object = ObjectAdder.addObject2(_2_Level_Item.class, wog2Level.getLevel().getPossibleChildrenTypeIDs()[3], wog2Level.getLevel());
                        object.setAttribute("type", loadedItemEntry.getKey());
                    });
                    item.getItems().add(sub);
                }
            }

             */
            content.getItems().add(item);
        }
    }

    @Override
    public ArrayList<Node> getGUIElements() {
        return new ArrayList<>(List.of(buttonAddItem));
    }

}
