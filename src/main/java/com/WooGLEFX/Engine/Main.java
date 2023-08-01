package com.WooGLEFX.Engine;

import com.WooGLEFX.File.AnimationReader;
import com.WooGLEFX.File.FileManager;
import com.WooGLEFX.File.GlobalResourceManager;
import com.WooGLEFX.File.LevelExporter;
import com.WooGLEFX.EditorObjects._Ball;
import com.WooGLEFX.GUI.*;
import com.WooGLEFX.Structures.*;
import com.WooGLEFX.Structures.SimpleStructures.DragSettings;
import com.WooGLEFX.Structures.SimpleStructures.LevelTab;
import com.WooGLEFX.Structures.SimpleStructures.WoGAnimation;
import com.WooGLEFX.Structures.UserActions.*;
import com.WorldOfGoo.Addin.AddinLevelName;
import com.WorldOfGoo.Level.*;
import com.WorldOfGoo.Particle._Particle;
import com.WorldOfGoo.Resrc.ResourceManifest;
import com.WorldOfGoo.Resrc.ResrcImage;
import com.WorldOfGoo.Scene.Label;
import com.WorldOfGoo.Scene.*;
import com.WorldOfGoo.Scene.Rectangle;
import com.WorldOfGoo.Scene.Slider;
import com.WorldOfGoo.Text.TextString;
import com.WorldOfGoo.Text.TextStrings;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.control.MenuBar;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.transform.Affine;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.List;

public class Main extends Application {

    public static Affine t;

    private static EditorObject strand1Gooball;

    public static EditorObject getStrand1Gooball() {
        return strand1Gooball;
    }

    public static void setStrand1Gooball(EditorObject strand1Gooball) {
        Main.strand1Gooball = strand1Gooball;
    }

    private static EditorWindow editorWindow;

    private static TabPane levelSelectPane;

    public static Point2D getScreenCenter() {
        return new Point2D((thingPane.getWidth() / 2 - level.getOffsetX()) / level.getZoom(), (thingPane.getHeight() / 2 - level.getOffsetY()) / level.getZoom());
    }

    public static double getMouseYOffset() {
        return levelSelectPane.getHeight() + vBox.getChildren().get(4).getLayoutY();
    }

    public static void reloadWorldOfGoo(double version) {
        FileManager.readWOGdirs();

        //Import all goo balls and all misc resources from the game files
        importedBalls = new ArrayList<>();

        importGameResources();

        for (int i = 0; i < FileManager.getPaletteBalls().size(); i++) {

            String ballName = FileManager.getPaletteBalls().get(i);
            double ballVersion = FileManager.getPaletteVersions().get(i);

            _Ball ball = FileManager.openBall(ballName, ballVersion);

            for (EditorObject resrc : FileManager.commonBallResrcData){
                GlobalResourceManager.addResource(resrc, ballVersion);
            }

            if (ball != null) {
                ball.makeImages(ballVersion);
                ball.setVersion(ballVersion);
                importedBalls.add(ball);
            }
        }
    }

    public static boolean changeWorldOfGooDirectory(double version) {
        FileChooser findWorldOfGoo = new FileChooser();
        findWorldOfGoo.getExtensionFilters().add(new FileChooser.ExtensionFilter("World of Goo executable", "WorldOfGoo.exe"));
        File worldOfGoo = findWorldOfGoo.showOpenDialog(new Stage());
        if (worldOfGoo == null) {
            return false;
        } else {
            if (version == 1.3) {
                FileManager.setOldWOGdir(worldOfGoo.getParent());
                FileManager.saveProperties();
                reloadWorldOfGoo(1.3);
            } else {
                FileManager.setNewWOGdir(worldOfGoo.getParent() + "\\game");
                FileManager.saveProperties();
                reloadWorldOfGoo(1.5);
            }
            return true;
        }
    }

    public static void quit() {
        resumeLevelClosing();
    }

    public static void newLevel(double version) {
        Alarms.askForLevelName("new", version);
    }

    public static void newLevel(String name, double version) {
        System.out.println("New level");

        levelSelectPane.setMinHeight(30);
        levelSelectPane.setMaxHeight(30);

        ArrayList<EditorObject> sceneList = new ArrayList<>();
        sceneList.add(EditorObject.create("scene", new EditorAttribute[0], null));
        ArrayList<EditorObject> levelList = new ArrayList<>();
        levelList.add(EditorObject.create("level", new EditorAttribute[0], null));
        ArrayList<EditorObject> resourcesList = new ArrayList<>();
        resourcesList.add(EditorObject.create("ResourceManifest", new EditorAttribute[0], null));
        ArrayList<EditorObject> addinList = new ArrayList<>();
        addinList.add(EditorObject.create("Addin", new EditorAttribute[0], null));
        ArrayList<EditorObject> textList = new ArrayList<>();
        textList.add(EditorObject.create("strings", new EditorAttribute[0], null));

        level = new WorldLevel(sceneList, levelList, resourcesList, addinList, textList, version);
        level.setLevelName(name);
        enableAllButtons(false);

        level.getSceneObject().setRealName("scene");
        level.getSceneObject().setTreeItem(new TreeItem<>(level.getSceneObject()));
        level.getLevelObject().setRealName("level");
        level.getLevelObject().setTreeItem(new TreeItem<>(level.getLevelObject()));
        level.getResourcesObject().setRealName("ResourceManifest");
        level.getResourcesObject().setTreeItem(new TreeItem<>(level.getResourcesObject()));

        EditorObject resourcesThing = EditorObject.create("Resources", new EditorAttribute[0], level.getResourcesObject());
        resourcesThing.setTreeItem(new TreeItem<>(resourcesThing));
        resourcesThing.setAttribute("id", "scene_" + level.getLevelName());
        level.getResourcesObject().getTreeItem().getChildren().add(resourcesThing.getTreeItem());

        for (EditorObject object : level.getScene()) {
            object.update();
        }

        for (EditorObject object : level.getLevel()) {
            object.update();
        }

        for (EditorObject object : level.getResources()) {
            object.update();
        }

        //Put everything in the hierarchy
        level.getSceneObject().getTreeItem().setExpanded(true);
        hierarchy.setRoot(level.getSceneObject().getTreeItem());

        //Add items from the Scene to it
        propertiesView.setRoot(level.getSceneObject().getPropertiesTreeItem());

        Tab levelSelectButton = FXCreator.levelSelectButton(level);
        levelSelectPane.getTabs().add(levelSelectButton);

        int numTabs = levelSelectPane.getTabs().size();
        double tabSize = 1 / (numTabs + 1.0);
        levelSelectPane.setTabMaxWidth(tabSize * (levelSelectPane.getWidth() - 15) - 15);
        levelSelectPane.setTabMinWidth(tabSize * (levelSelectPane.getWidth() - 15) - 15);

        saveLevel(version);

        level.setLevelTab(levelSelectButton);
        level.setEditingStatus(WorldLevel.NO_UNSAVED_CHANGES, true);
        levelSelectPane.getSelectionModel().select(levelSelectButton);
        onSetLevel();
    }

    public static void openLevel(double version) {
        System.out.println("Open level");
        try {
            new LevelSelector(version).start(new Stage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void enableAllButtons(boolean disable) {
        for (int i = 1; i < 4; i++) {
            for (Node node : ((ToolBar) (vBox.getChildren().get(i))).getItems()) {
                node.setDisable(disable);
            }
        }
        if (!FileManager.isHasOldWOG()) {
            FXCreator.buttonNewOld.setDisable(true);
            FXCreator.buttonOpenOld.setDisable(true);
            FXCreator.buttonSaveOld.setDisable(true);
            FXCreator.buttonSaveAndPlayOld.setDisable(true);
            FXCreator.buttonCloneOld.setDisable(true);
        }
        if (!FileManager.isHasNewWOG()) {
            FXCreator.buttonNewNew.setDisable(true);
            FXCreator.buttonOpenNew.setDisable(true);
            FXCreator.buttonSaveNew.setDisable(true);
            FXCreator.buttonSaveAndPlayNew.setDisable(true);
            FXCreator.buttonCloneNew.setDisable(true);
        }
    }

    public static void openLevel(String levelName, double version) {
        level = FileManager.openLevel(levelName, version);
        if (level != null) {
            level.setLevelName(levelName);
            enableAllButtons(false);

            for (int i = 0; i < FileManager.getPaletteBalls().size(); i++) {
                FXCreator.getGooballsToolbar().getItems().get(i).setDisable(FileManager.getPaletteVersions().get(i) != version);
            }

            levelSelectPane.setMinHeight(30);
            levelSelectPane.setMaxHeight(30);

            for (EditorObject object : level.getLevel()) {
                if (object instanceof BallInstance) {
                    boolean alreadyIn = false;
                    for (_Ball ball : importedBalls) {
                        if (ball.getObjects().get(0).getAttribute("name").equals(object.getAttribute("type")) && ball.getVersion() == level.getVersion()) {
                            alreadyIn = true;
                            break;
                        }
                    }
                    if (!alreadyIn) {
                        _Ball ball2 = FileManager.openBall(object.getAttribute("type"), version);

                        for (EditorObject resrc : FileManager.commonBallResrcData) {
                            GlobalResourceManager.addResource(resrc, version);
                        }

                        if (ball2 != null) {
                            ball2.makeImages(version);
                            ball2.setVersion(version);

                            importedBalls.add(ball2);
                        }
                    }
                }
            }

            for (EditorObject object : level.getResources()) {
                if (object instanceof ResrcImage) {
                    GlobalResourceManager.addResource(object, level.getVersion());
                }
            }

            for (EditorObject object : level.getScene()) {
                object.update();
            }

            for (EditorObject object : level.getLevel()) {
                object.update();
            }

            for (EditorObject object : level.getResources()) {
                object.update();
            }

            //Put everything in the hierarchy
            level.getSceneObject().getTreeItem().setExpanded(true);
            hierarchy.setRoot(level.getSceneObject().getTreeItem());

            //Add items from the Scene to it
            propertiesView.setRoot(level.getSceneObject().getPropertiesTreeItem());

            Tab levelSelectButton = FXCreator.levelSelectButton(level);
            levelSelectPane.getTabs().add(levelSelectButton);

            int numTabs = levelSelectPane.getTabs().size();
            double tabSize = 1 / (numTabs + 1.0);
            levelSelectPane.setTabMaxWidth(tabSize * (levelSelectPane.getWidth() - 15) - 15);
            levelSelectPane.setTabMinWidth(tabSize * (levelSelectPane.getWidth() - 15) - 15);

            level.setLevelTab(levelSelectButton);
            level.setEditingStatus(WorldLevel.NO_UNSAVED_CHANGES, true);
            levelSelectPane.getSelectionModel().select(levelSelectButton);
            onSetLevel();
        }
    }

    public static Canvas getCanvas() {
        return canvas;
    }

    public static void setCanvas(Canvas canvas) {
        Main.canvas = canvas;
    }

    public static void supremeAddToList(ArrayList<EditorObject> list, EditorObject object) {
        list.add(object);
        for (EditorObject child : object.getChildren()) {
            supremeAddToList(list, child);
        }
    }

    public static void cloneLevel(double version) {
        Alarms.askForLevelName("clone", version);
    }

    public static void cloneLevel(String name, double version) {
        System.out.println("New level");

        levelSelectPane.setMinHeight(30);
        levelSelectPane.setMaxHeight(30);

        ArrayList<EditorObject> sceneList = new ArrayList<>();
        ArrayList<EditorObject> levelList = new ArrayList<>();
        ArrayList<EditorObject> resourcesList = new ArrayList<>();
        ArrayList<EditorObject> addinList = new ArrayList<>();
        ArrayList<EditorObject> textList = new ArrayList<>();

        supremeAddToList(resourcesList, level.getResourcesObject().deepClone(null));
        supremeAddToList(sceneList, level.getSceneObject().deepClone(null));
        supremeAddToList(levelList, level.getLevelObject().deepClone(null));
        supremeAddToList(addinList, level.getAddinObject().deepClone(null));
        supremeAddToList(textList, level.getTextObject().deepClone(null));

        level = new WorldLevel(sceneList, levelList, resourcesList, addinList, textList, version);

        level.getResourcesObject().getChildren().get(0).setAttribute("id", "scene_" + name);

        level.setLevelName(name);
        enableAllButtons(false);

        for (EditorObject object : level.getScene()) {
            object.update();
        }

        for (EditorObject object : level.getLevel()) {
            object.update();
        }

        for (EditorObject object : level.getResources()) {
            object.update();
        }

        //Put everything in the hierarchy
        level.getSceneObject().getTreeItem().setExpanded(true);
        hierarchy.setRoot(level.getSceneObject().getTreeItem());

        //Add items from the Scene to it
        propertiesView.setRoot(level.getSceneObject().getPropertiesTreeItem());

        Tab levelSelectButton = FXCreator.levelSelectButton(level);
        levelSelectPane.getTabs().add(levelSelectButton);

        int numTabs = levelSelectPane.getTabs().size();
        double tabSize = 1 / (numTabs + 1.0);
        levelSelectPane.setTabMaxWidth(tabSize * (levelSelectPane.getWidth() - 15) - 15);
        levelSelectPane.setTabMinWidth(tabSize * (levelSelectPane.getWidth() - 15) - 15);

        saveLevel(version);

        level.setLevelTab(levelSelectButton);
        level.setEditingStatus(WorldLevel.NO_UNSAVED_CHANGES, true);
        levelSelectPane.getSelectionModel().select(levelSelectButton);
        onSetLevel();
    }

    public static void saveLevel(double version) {
        System.out.println("Save level");
        if (version == 1.3) {
            LevelExporter.saveAsXML(level, FileManager.getOldWOGdir() + "\\res\\levels\\" + level.getLevelName(), version, false, true);
        } else {
            LevelExporter.saveAsXML(level, FileManager.getNewWOGdir() + "\\res\\levels\\" + level.getLevelName(), version, false, true);
        }
        if (level.getEditingStatus() != WorldLevel.NO_UNSAVED_CHANGES) {
            level.setEditingStatus(WorldLevel.NO_UNSAVED_CHANGES, true);
        }
    }

    public static void playLevel(double version) {
        System.out.println("Play level");

        if (level.getVersion() == 1.3) {
            try {
                ProcessBuilder processBuilder = new ProcessBuilder(FileManager.getOldWOGdir() + "\\WorldOfGoo.exe", level.getLevelName());
                processBuilder.directory(new File(FileManager.getOldWOGdir()));
                processBuilder.redirectErrorStream(true);
                Process process = processBuilder.start();
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println(line);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    public static void exportLevel(boolean includeAddinInfo) {
        FileChooser fileChooser = new FileChooser();
        if (!Files.exists(Path.of((level.getVersion() == 1.3 ? FileManager.getOldWOGdir() : FileManager.getNewWOGdir()) + "\\res\\levels\\" + level.getLevelName() + "\\goomod"))) {
            try {
                Files.createDirectories(Path.of((level.getVersion() == 1.3 ? FileManager.getOldWOGdir() : FileManager.getNewWOGdir()) + "\\res\\levels\\" + level.getLevelName() + "\\goomod"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        fileChooser.setInitialDirectory(new File((level.getVersion() == 1.3 ? FileManager.getOldWOGdir() : FileManager.getNewWOGdir()) + "\\res\\levels\\" + level.getLevelName() + "\\goomod"));
        fileChooser.setInitialFileName(level.getLevelName());
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("World of Goo mod (*.goomod)", "*.goomod"));
        File export = fileChooser.showSaveDialog(stage);
        if (export != null) {
            LevelExporter.exportGoomod(export, level, new ArrayList<>(), includeAddinInfo);
        }
    }

    public static void undo() {
        System.out.println("Undo");
        if (userActions.size() != 0) {
            UserAction[] changes = userActions.pop();
            redoActions.add(changes);
            for (UserAction change : changes) {
                if (change instanceof AttributeChangeAction) {
                    change.getObject().setAttribute(((AttributeChangeAction) change).getAttributeName(), ((AttributeChangeAction) change).getOldValue());
                    propertiesView.refresh();
                } else if (change instanceof ObjectCreationAction) {
                    deleteItem(change.getObject(), false);
                } else if (change instanceof ObjectDestructionAction) {
                    create(change.getObject(), ((ObjectDestructionAction) change).getPosition());
                } else if (change instanceof ImportResourceAction) {
                    deleteResource(change.getObject().getAttribute("path"));
                    deleteItem(change.getObject(), false);
                }
            }
        }
        if (userActions.size() == 0) {
            level.setEditingStatus(WorldLevel.NO_UNSAVED_CHANGES, true);
        }
    }

    private static Stack<UserAction[]> redoActions = new Stack<>();

    public static void redo() {
        System.out.println("Redo");
        if (redoActions.size() != 0) {
            UserAction[] changes = redoActions.pop();
            registerChange(changes);
            for (UserAction change : changes) {
                if (change instanceof AttributeChangeAction) {
                    change.getObject().setAttribute(((AttributeChangeAction) change).getAttributeName(), ((AttributeChangeAction) change).getNewValue());
                } else if (change instanceof ObjectCreationAction) {
                    create(change.getObject(), ((ObjectCreationAction) change).getPosition());
                } else if (change instanceof ObjectDestructionAction) {
                    deleteItem(change.getObject(), false);
                } else if (change instanceof ImportResourceAction) {
                    importImage(new File(((ImportResourceAction) change).getPath()));
                }
            }
        }
    }

    public static void cut() {
        System.out.println("Cut");
    }

    public static void copy() {
        if (selected != null && propertiesView.getEditingCell() == null) {
            System.out.println("Copy");
            String clipboard = ClipboardHandler.exportToClipBoardString(selected);
            ClipboardContent clipboardContent = new ClipboardContent();
            clipboardContent.putString(clipboard);
            Clipboard.getSystemClipboard().setContent(clipboardContent);
        }
    }

    public static void create(EditorObject object, int row) {
        System.out.println(object.getRealName());
        object.update();

        if (object.getTreeItem() == null) {
            object.setTreeItem(new TreeItem<>(object));
        }

        object.getParent().getTreeItem().getChildren().add(row, object.getTreeItem());

        if (object.getChildren().size() > 0) {
            ArrayList<EditorObject> children = object.getChildren();
            EditorObject[] dontBreak = new EditorObject[children.size()];
            int i = 0;
            for (EditorObject child : children) {
                dontBreak[i] = child;
                i++;
            }
            i = 0;
            for (EditorObject child : dontBreak) {
                create(child, i);
                i++;
            }
        }

        TreeItem<EditorObject> thing = object.getTreeItem();
        while (thing.getParent() != null) {
            thing = thing.getParent();
        }
        if (thing.getValue().getRealName().equals("scene")) {
            level.getScene().add(object);
        }
        if (thing.getValue().getRealName().equals("level")) {
            level.getLevel().add(object);
        }

        if (object instanceof BallInstance) {
            for (EditorObject strand : level.getLevel()) {
                if (strand instanceof Strand) {
                    if (object.getAttribute("id").equals(strand.getAttribute("gb1"))) {
                        ((Strand) strand).setGoo1((BallInstance)object);
                    }
                    if (object.getAttribute("id").equals(strand.getAttribute("gb2"))) {
                        ((Strand) strand).setGoo2((BallInstance)object);
                    }
                }
            }
        }
    }

    public static void paste() {
        if (propertiesView.getEditingCell() == null) {
            System.out.println("Paste");
            String clipboard = Clipboard.getSystemClipboard().getString();
            if (clipboard != null) {
                EditorObject object = ClipboardHandler.importFromClipboardString(clipboard);
                object.setParent(selected.getParent());
                if (object instanceof BallInstance) {
                    fixGooball(object);
                }
                //object.getParent().getChildren().add(0, object);
                create(object, 0);
                selected = object;
                registerChange(new ObjectCreationAction(object, hierarchy.getRow(object.getTreeItem()) - hierarchy.getRow(object.getParent().getTreeItem())));
                redoActions.clear();
            }
        }
    }

    public static void deleteItem(EditorObject item, boolean parentDeleted) {
        System.out.println(item.getRealName());
        if (item instanceof Strand) {
            for (EditorObject ball : level.getLevel()) {
                if (ball instanceof BallInstance) {
                    ((BallInstance) ball).getStrands().remove(item);
                }
            }
        }
        if (item instanceof BallInstance) {
            for (Strand strand : ((BallInstance) item).getStrands()) {
                if (strand.getGoo1() == item) {
                    strand.setGoo1(null);
                }
                if (strand.getGoo2() == item) {
                    strand.setGoo2(null);
                }
            }
        }
        if (item.getChildren().size() > 0) {
            List<EditorObject> children = item.getChildren();
            EditorObject[] dontBreak = new EditorObject[children.size()];
            int i = 0;
            for (EditorObject child : children) {
                dontBreak[i] = child;
                i++;
            }
            for (EditorObject child : dontBreak) {
                deleteItem(child, true);
            }
        }
        if (level.getScene().contains(item)) {
            level.getScene().remove(item);
        } else if (level.getLevel().contains(item)) {
            level.getLevel().remove(item);
        } else if (level.getResources().contains(item)) {
            level.getResources().remove(item);
        } else if (level.getAddin().contains(item)) {
            level.getAddin().remove(item);
        } else if (level.getText().contains(item)) {
            level.getText().remove(item);
        }
        item.getParent().getTreeItem().getChildren().remove(item.getTreeItem());
        if (!parentDeleted) {
            item.getParent().getChildren().remove(item);
        }
    }

    public static void delete() {
        System.out.println("Delete");
        if (selected != null) {
            EditorObject parent = selected.getParent();
            int row = parent.getTreeItem().getChildren().indexOf(selected.getTreeItem());
            System.out.println(row);
            registerChange(new ObjectDestructionAction(selected, row));
            redoActions.clear();
            deleteItem(selected, false);
            if (row == 0) {
                selected = parent;
            } else {
                selected = parent.getChildren().get(row - 1);
            }
            changeTableView(selected);
            //hierarchy.getFocusModel().focus(row);
            hierarchy.getSelectionModel().select(hierarchy.getRow(selected.getTreeItem()));
        }
    }

    public static void registerChange(UserAction action) {
        registerChange(new UserAction[]{action});
    }

    public static void registerChange(UserAction[] actions) {
        userActions.add(actions);
        if (level.getEditingStatus() == WorldLevel.NO_UNSAVED_CHANGES) {
            level.setEditingStatus(WorldLevel.UNSAVED_CHANGES, true);
        }
    }

    public static void deleteResource(String file) {
        String path = new File(file).getName();
        System.out.println(path);
        String startPath = level.getVersion() == 1.5 ? FileManager.getNewWOGdir() : FileManager.getOldWOGdir();
        for (File resourceFile : new File(startPath + "\\res\\levels\\" + level.getLevelName()).listFiles()) {
            if (resourceFile.getName().split("\\.")[0].equals(path)){
                resourceFile.delete();
            }
        }
    }

    public static void updateLevelResources() {
        System.out.println("Update level resources");
    }

    public static void importImage() {
        System.out.println("Import image");

        FileChooser fileChooser = new FileChooser();

        File resrcFile = fileChooser.showOpenDialog(new Stage());

        importImage(resrcFile);
    }

    public static void importImage(File resrcFile) {
        try {
            BufferedImage image = ImageIO.read(resrcFile);
            String path = "";
            if (level.getVersion() == 1.3) {
                path = FileManager.getOldWOGdir();
            } else if (level.getVersion() == 1.5) {
                path = FileManager.getNewWOGdir();
            }

            String imgPath = resrcFile.getPath();

            if (!resrcFile.getPath().contains("\\res\\")) {
                ImageIO.write(image, "png", new File(path + "\\res\\levels\\" + level.getLevelName() + "\\" + resrcFile.getName().split("\\.")[0] + ".png"));
                imgPath = path + "\\res\\levels\\" + level.getLevelName() + "\\" + resrcFile.getName().split("\\.")[0] + ".png";
            }

            String imageResourceName = "IMAGE_SCENE_" + level.getLevelName().toUpperCase() + "_" + resrcFile.getName().split("\\.")[0].toUpperCase();
            EditorObject imageResourceObject = EditorObject.create("Image", new EditorAttribute[]{
                    new EditorAttribute("id", imageResourceName, "", new InputField("id", InputField.ANY), false),
                    new EditorAttribute("path", "res\\levels\\" + level.getLevelName() + "\\" + resrcFile.getName().split("\\.")[0], "", new InputField("path", InputField.ANY), false),
            }, null);

            int whereToPlaceResource = 0;
            int count = 0;
            for (EditorObject resourceThing : level.getResourcesObject().getChildren().get(0).getChildren()) {
                count++;
                if (resourceThing instanceof ResrcImage) {
                    whereToPlaceResource = count;
                }
            }

            level.getResources().add(imageResourceObject);
            imageResourceObject.setParent(level.getResourcesObject().getChildren().get(0), whereToPlaceResource);
            level.getResourcesObject().getChildren().get(0).getTreeItem().getChildren().add(whereToPlaceResource, imageResourceObject.getTreeItem());

            registerChange(new ImportResourceAction(imageResourceObject, imgPath));

            GlobalResourceManager.addResource(imageResourceObject, level.getVersion());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void newTextResource() {
        System.out.println("New text resource");
        EditorObject newTextObject = EditorObject.create("string", new EditorAttribute[0], level.getTextObject());
        fixString(newTextObject);
        level.getText().add(newTextObject);
        setSelected(newTextObject);
        registerChange(new ObjectCreationAction(newTextObject, level.getTextObject().getChildren().indexOf(newTextObject)));
    }

    public static void cleanLevelResources() {
        System.out.println("Clean level resources");
    }

    public static void importMusic() {
        System.out.println("Import music");
    }

    public static void importLoopsound() {
        System.out.println("Import loopsound");
    }

    public static final int SELECTION = 0;
    public static final int ZOOM = 1;
    public static final int STRAND = 2;

    private static int mode = SELECTION;

    public static int getMode() {
        return mode;
    }

    public static void setMode(int mode) {
        Main.mode = mode;
    }

    public static void selectionMode() {
        System.out.println("Selection Mode");
        mode = SELECTION;
    }

    public static void zoomMode() {
        System.out.println("Zoom Mode");
        mode = ZOOM;
    }

    public static void strandMode() {
        System.out.println("Strand Mode");
        mode = STRAND;
    }

    public static void showHideCameras() {
        System.out.println("Show/Hide Cameras");
        level.setShowCameras(!level.isShowCameras());
    }

    public static void showHideForcefields() {
        System.out.println("Show/Hide Forcefields");
        level.setShowForcefields(!level.isShowForcefields());
    }

    public static void showHideGeometry() {
        System.out.println("Show/Hide Geometry");
        level.setShowGeometry(!level.isShowGeometry());
    }

    public static void showHideGraphics() {
        System.out.println("Show/Hide Graphics");
        level.setShowGraphics(!level.isShowGraphics());
    }

    public static void showHideGoos() {
        System.out.println("Show/Hide Goos");
        level.setShowGoos(level.getShowGoos() - 1);
        if (level.getShowGoos() < 0) {
            level.setShowGoos(2);
        }
    }

    public static void showHideParticles() {
        System.out.println("Show/Hide Particles");
        level.setShowParticles(!level.isShowParticles());
    }

    public static void showHideLabels() {
        System.out.println("Show/Hide Labels");
        level.setShowLabels(!level.isShowLabels());
    }

    public static void showHideAnim() {
        System.out.println("Show/Hide Animations");
        level.setShowAnimations(!level.isShowAnimations());
    }

    public static void addAnythingByName(String objectName, EditorObject parent) {
        EditorObject obj = EditorObject.create(objectName, new EditorAttribute[0], parent);
        obj.setToScreenCenter();
        addAnything(obj, obj.getParent());
    }

    public static void addAnything(EditorObject obj, EditorObject parent) {
        obj.setTreeItem(new TreeItem<>(obj));
        parent.getTreeItem().getChildren().add(obj.getTreeItem());
        hierarchy.scrollTo(hierarchy.getRow(obj.getTreeItem()));
        hierarchy.getSelectionModel().select(hierarchy.getRow(obj.getTreeItem()));
        obj.update();
        selected = obj;
        changeTableView(selected);

        registerChange(new ObjectCreationAction(obj, hierarchy.getRow(obj.getTreeItem()) - hierarchy.getRow(obj.getParent().getTreeItem())));
        redoActions.clear();
    }

    /**Changes the id attribute of a BallInstance to give it a unique ID.
     * IDs are given in the form of "goo[number]".
     * @param obj The BallInstance to modify. */
    public static void fixGooball(EditorObject obj) {

        //Create an array to store which id numbers are already taken by BallInstances.
        boolean[] taken = new boolean[level.getLevel().size()];

        //Loop over all BallInstances in the level.
        for (EditorObject ball : level.getLevel()) {
            if (ball instanceof BallInstance) {

                //Check if the ball's ID is "goo[number]".
                //If it is, flag that number as already taken.
                String id = ball.getAttribute("id");
                if (id.length() > 3 && id.startsWith("goo")) {
                    try {
                        taken[Integer.parseInt(id.substring(3))] = true;
                    } catch (Exception ignored) {
                    }
                }
            }
        }

        //Find the smallest available number to use as an ID and set the ball's ID attribute accordingly.
        int count = 0;
        while (taken[count]) {
            count++;
        }
        obj.setAttribute("id", "goo" + count);
    }

    /**Changes the id attribute of a text string to give it a unique ID.
     * IDs are given in the form of "TEXT_[level name]_STR[number]".
     * @param obj The string to modify. */
    public static void fixString(EditorObject obj) {

        //Create an array to store which id numbers are already taken by strings.
        boolean[] taken = new boolean[level.getText().size()];

        //Loop over all text strings in the level.
        for (EditorObject string : level.getText()) {
            if (string instanceof TextString) {

                //Check if the string's ID is "TEXT_[level name]_STR[number]".
                //If it is, flag that number as already taken.
                String id = string.getAttribute("id");
                if (id.length() > 9 + level.getLevelName().length() && id.startsWith("TEXT_" + level.getLevelName().toUpperCase() + "_STR")) {
                    try {
                        taken[Integer.parseInt(id.substring(9 + level.getLevelName().length()))] = true;
                    } catch (Exception ignored) {
                    }
                }
            }
        }

        //Find the smallest available number to use as an ID and set the string's ID attribute accordingly.
        int count = 0;
        while (taken[count]) {
            count++;
        }
        obj.setAttribute("id", "TEXT_" + level.getLevelName().toUpperCase() + "_STR" + count);
    }

    public static void addBall(EditorObject parent, String type) {
        BallInstance obj = (BallInstance) EditorObject.create("BallInstance", new EditorAttribute[0], parent);
        obj.setAttribute("type", type);
        obj.setAttribute("x", getScreenCenter().getX());
        obj.setAttribute("y", -getScreenCenter().getY());
        fixGooball(obj);
        obj.update();
        obj.setRealName("BallInstance");
        level.getLevel().add(obj);
        addAnything(obj, parent);
    }


    public static void addLine(EditorObject parent) {
        Line obj = (Line) EditorObject.create("line", new EditorAttribute[0], parent);
        obj.setAttribute("anchor", getScreenCenter().getX() + "," + getScreenCenter().getY());
        obj.setRealName("line");
        level.getScene().add(obj);
        addAnything(obj, parent);
    }

    public static void addRectangle(EditorObject parent) {
        Rectangle obj = (Rectangle) EditorObject.create("rectangle", new EditorAttribute[0], parent);
        obj.setAttribute("x", getScreenCenter().getX());
        obj.setAttribute("y", -getScreenCenter().getY());
        obj.setRealName("rectangle");
        level.getScene().add(obj);
        addAnything(obj, parent);
    }

    public static void addCircle(EditorObject parent) {
        Circle obj = (Circle) EditorObject.create("circle", new EditorAttribute[0], parent);
        obj.setAttribute("x", getScreenCenter().getX());
        obj.setAttribute("y", -getScreenCenter().getY());
        obj.setRealName("circle");
        level.getScene().add(obj);
        addAnything(obj, parent);
    }

    public static void addSceneLayer(EditorObject parent) {
        SceneLayer obj = (SceneLayer) EditorObject.create("SceneLayer", new EditorAttribute[0], parent);
        obj.setAttribute("x", getScreenCenter().getX());
        obj.setAttribute("y", -getScreenCenter().getY());
        obj.setRealName("SceneLayer");
        level.getScene().add(obj);
        addAnything(obj, parent);
    }

    public static void addCompositegeom(EditorObject parent) {
        Compositegeom obj = (Compositegeom) EditorObject.create("compositegeom", new EditorAttribute[0], parent);
        obj.setAttribute("x", getScreenCenter().getX());
        obj.setAttribute("y", -getScreenCenter().getY());
        obj.setRealName("compositegeom");
        level.getScene().add(obj);
        addAnything(obj, parent);
    }

    public static void addHinge(EditorObject parent) {
        Hinge obj = (Hinge) EditorObject.create("hinge", new EditorAttribute[0], parent);
        obj.setAttribute("anchor", getScreenCenter().getX() + "," + getScreenCenter().getY());
        obj.setRealName("hinge");
        level.getScene().add(obj);
        addAnything(obj, parent);
    }

    public static void autoPipe(EditorObject parent) {
        System.out.println("Auto Pipe");
    }

    public static void addPipeVertex(EditorObject parent) {
        System.out.println("Add Pipe Vertex");
    }

    public static void addFire(EditorObject parent) {
        Fire obj = (Fire) EditorObject.create("fire", new EditorAttribute[0], parent);
        obj.setAttribute("x", getScreenCenter().getX());
        obj.setAttribute("y", -getScreenCenter().getY());
        obj.setRealName("fire");
        level.getLevel().add(obj);
        addAnything(obj, parent);
    }

    public static void addLinearForcefield(EditorObject parent) {
        Linearforcefield obj = (Linearforcefield) EditorObject.create("linearforcefield", new EditorAttribute[0], parent);
        obj.setAttribute("center", getScreenCenter().getX() + "," + getScreenCenter().getY());
        obj.setRealName("linearforcefield");
        level.getScene().add(obj);
        addAnything(obj, parent);
    }

    public static void addRadialForcefield(EditorObject parent) {
        Radialforcefield obj = (Radialforcefield) EditorObject.create("radialforcefield", new EditorAttribute[0], parent);
        obj.setAttribute("center", getScreenCenter().getX() + "," + getScreenCenter().getY());
        obj.setRealName("radialforcefield");
        level.getScene().add(obj);
        addAnything(obj, parent);
    }

    public static void addParticle(EditorObject parent) {
        System.out.println("Add Particle");
    }

    public static void addSign(EditorObject parent) {
        Signpost obj = (Signpost) EditorObject.create("signpost", new EditorAttribute[0], parent);
        obj.setAttribute("x", getScreenCenter().getX());
        obj.setAttribute("y", -getScreenCenter().getY());
        obj.setRealName("signpost");
        level.getScene().add(obj);
        addAnything(obj, parent);
    }

    public static void addLabel(EditorObject parent) {
        Label obj = (Label) EditorObject.create("label", new EditorAttribute[0], parent);
        obj.setAttribute("x", getScreenCenter().getX());
        obj.setAttribute("y", -getScreenCenter().getY());
        obj.setRealName("label");
        level.getScene().add(obj);
        addAnything(obj, parent);
    }

    public static void addStrand(EditorObject parent) {
        Strand obj = (Strand) EditorObject.create("Strand", new EditorAttribute[0], parent);
        obj.setRealName("Strand");
        level.getLevel().add(obj);
        addAnything(obj, parent);
    }

    public static void addCamera(EditorObject parent) {
        Camera obj = (Camera) EditorObject.create("camera", new EditorAttribute[0], parent);
        obj.setAttribute("endpos", getScreenCenter().getX() + "," + getScreenCenter().getY());
        obj.setRealName("camera");
        level.getLevel().add(obj);
        addAnything(obj, parent);
    }

    public static void addPoi(EditorObject parent) {
        System.out.println("Add Label");
    }

    public static void addMusic(EditorObject parent) {
        System.out.println("Add Label");
    }

    public static void addLoopsound(EditorObject parent) {
        System.out.println("Add Label");
    }

    public static void addButton(EditorObject parent) {
        System.out.println("Add Label");
    }

    public static void addButtongroup(EditorObject parent) {
        System.out.println("Add Label");
    }

    public static void addMotor(EditorObject parent) {
        System.out.println("Add Label");
    }

    public static void addSlider(EditorObject parent) {
        Slider obj = (Slider) EditorObject.create("slider", new EditorAttribute[0], parent);
        obj.setRealName("slider");
        level.getLevel().add(obj);
        addAnything(obj, parent);
    }

    public static void addEndoncollision(EditorObject parent) {
        Endoncollision obj = (Endoncollision) EditorObject.create("endoncollision", new EditorAttribute[0], parent);
        obj.setRealName("endoncollision");
        level.getLevel().add(obj);
        addAnything(obj, parent);
    }

    public static void addEndonnogeom(EditorObject parent) {
        Endonnogeom obj = (Endonnogeom) EditorObject.create("endonnogeom", new EditorAttribute[0], parent);
        obj.setRealName("endonnogeom");
        level.getLevel().add(obj);
        addAnything(obj, parent);
    }

    public static void addEndonmessage(EditorObject parent) {
        Endonmessage obj = (Endonmessage) EditorObject.create("endonmessage", new EditorAttribute[0], parent);
        obj.setRealName("endonmessage");
        level.getLevel().add(obj);
        addAnything(obj, parent);
    }

    public static void addTargetheight(EditorObject parent) {
        System.out.println("Add Label");
    }

    public static void addLevelexit(EditorObject parent) {
        System.out.println("Add Label");
    }

    public static void addPipe(EditorObject parent) {
        System.out.println("Add Label");
    }


    private static EditorObject selected;

    private static EditorObject sceneObject;
    private static EditorObject levelObject;

    public static EditorObject getSceneObject() {
        return sceneObject;
    }

    public static void setSceneObject(EditorObject sceneObject) {
        Main.sceneObject = sceneObject;
    }

    public static EditorObject getLevelObject() {
        return levelObject;
    }

    public static void setLevelObject(EditorObject levelObject) {
        Main.levelObject = levelObject;
    }

    public static EditorObject getSelected() {
        return selected;
    }

    public static void setSelected(EditorObject _selected) {
        if (_selected != null && _selected.getParent() != null) {
            if (_selected.getAbsoluteParent() instanceof ResourceManifest) {
                hierarchy.setRoot(_selected.getAbsoluteParent().getChildren().get(0).getTreeItem());
                hierarchy.setShowRoot(false);
            } else if (_selected.getAbsoluteParent() instanceof TextStrings) {
                hierarchy.setRoot(_selected.getAbsoluteParent().getTreeItem());
                hierarchy.setShowRoot(false);
            } else {
                hierarchy.setRoot(_selected.getAbsoluteParent().getTreeItem());
                hierarchy.setShowRoot(true);
            }
        }
        selected = _selected;
    }

    public static EditorObject getMoving() {
        return moving;
    }

    public static void setMoving(EditorObject moving) {
        Main.moving = moving;
    }

    public static int getOldDropIndex() {
        return oldDropIndex;
    }

    public static void setOldDropIndex(int oldDropIndex) {
        Main.oldDropIndex = oldDropIndex;
    }

    public int getEditingRow() {
        return editingRow;
    }

    public void setEditingRow(int editingRow) {
        this.editingRow = editingRow;
    }

    private static double rotateDragSourceY;
    private static double rotateAngleOffset;

    private static double mouseX = 0;
    private static double mouseY = 0;

    public static double getMouseX() {
        return mouseX;
    }

    public static double getMouseY() {
        return mouseY;
    }

    private static EditorAttribute[] oldAttributes;
    private static EditorObject oldSelected;

    /**
     * Called whenever the mouse is pressed.
     */
    public static void eventMousePressed(MouseEvent event) {

        if (level != null) {

            if (selected != null) {
                if (propertiesView.getEditingCell() == null || propertiesView.getFocusModel().focusedIndexProperty().get() == -1) {
                    propertiesView.edit(-1, null);
                }
                oldAttributes = selected.cloneAttributes();
                oldSelected = selected;
            }

            double editorViewWidth = splitPane.getDividerPositions()[0] * splitPane.getWidth();

            if (event.getButton().equals(MouseButton.PRIMARY)) {

                if (mode == SELECTION) {
                    if (event.getX() < editorViewWidth && event.getY() > getMouseYOffset()) {
                        if (selected != null) {
                            dragSettings = selected.mouseIntersectingCorners((event.getX() - level.getOffsetX()) / level.getZoom(), (event.getY() - getMouseYOffset() - level.getOffsetY()) / level.getZoom());
                        }
                        if (dragSettings.getType() == DragSettings.NONE) {
                            EditorObject prevSelected = selected;
                            selected = null;
                            ArrayList<EditorObject> byDepth = Renderer.orderObjectsBySelectionDepth(level);
                            for (int i = byDepth.size() - 1; i >= 0; i--) {
                                EditorObject object = byDepth.get(i);
                                if (object.mouseIntersection((event.getX() - level.getOffsetX()) / level.getZoom(), (event.getY() - getMouseYOffset() - level.getOffsetY()) / level.getZoom()).getType() != DragSettings.NONE) {
                                    changeTableView(object);
                                    selected = object;
                                    object.getParent().getTreeItem().setExpanded(true);
                                    hierarchy.getSelectionModel().select(object.getTreeItem());
                                    break;
                                    //} else if ((object instanceof Circle || object instanceof Rectangle || object instanceof Compositegeom || object instanceof Signpost) && object.getAttribute("image").length() > 0) {
                                } else if (object.mouseImageIntersection((event.getX() - level.getOffsetX()) / level.getZoom(), (event.getY() - getMouseYOffset() - level.getOffsetY()) / level.getZoom()).getType() != DragSettings.NONE) {
                                    changeTableView(object);
                                    selected = object;
                                    object.getParent().getTreeItem().setExpanded(true);
                                    hierarchy.getSelectionModel().select(object.getTreeItem());
                                    break;
                                }
                            }
                            if (selected != null && selected == prevSelected) {
                                DragSettings thisSettings = selected.mouseIntersection((event.getX() - level.getOffsetX()) / level.getZoom(), (event.getY() - getMouseYOffset() - level.getOffsetY()) / level.getZoom());
                                DragSettings thisImageSettings = selected.mouseImageIntersection((event.getX() - level.getOffsetX()) / level.getZoom(), (event.getY() - getMouseYOffset() - level.getOffsetY()) / level.getZoom());
                                if (thisSettings.getType() != DragSettings.NONE) {
                                    scene.setCursor(Cursor.MOVE);
                                    dragSettings = thisSettings;
                                } else if (thisImageSettings.getType() != DragSettings.NONE) {
                                    scene.setCursor(Cursor.MOVE);
                                    dragSettings = thisImageSettings;
                                }
                            }
                        }
                    }
                } else if (mode == STRAND) {
                    for (EditorObject ball : level.getLevel().toArray(new EditorObject[0])) {
                        if (ball instanceof BallInstance && ball.mouseIntersection((event.getX() - level.getOffsetX()) / level.getZoom(), (event.getY() - getMouseYOffset() - level.getOffsetY()) / level.getZoom()).getType() != DragSettings.NONE) {
                            if (strand1Gooball == null) {
                                strand1Gooball = ball;
                                break;
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Called whenever the mouse is dragged.
     */
    public static void eventMouseDragged(MouseEvent event) {
        mouseX = event.getX();
        mouseY = event.getY() - getMouseYOffset();
        if (selected != null && dragSettings != null) {

            //Calculate game-relative mouse coordinates.
            double gameRelativeMouseX = (mouseX - level.getOffsetX()) / level.getZoom();
            double gameRelativeMouseY = (mouseY - level.getOffsetY()) / level.getZoom();

            //Update the selected object according to what kind of operation is being performed.
            switch (dragSettings.getType()) {
                case DragSettings.MOVE -> selected.dragFromMouse(gameRelativeMouseX, gameRelativeMouseY, dragSettings.getInitialSourceX(), dragSettings.getInitialSourceY());
                case DragSettings.RESIZE -> selected.resizeFromMouse(gameRelativeMouseX, gameRelativeMouseY, dragSettings.getInitialSourceX(), dragSettings.getInitialSourceY(), dragSettings.getAnchorX(), dragSettings.getAnchorY());
                case DragSettings.ROTATE -> selected.rotateFromMouse(gameRelativeMouseX, gameRelativeMouseY, dragSettings.getRotateAngleOffset());
                case DragSettings.SETANCHOR -> selected.setAnchor(gameRelativeMouseX, gameRelativeMouseY, dragSettings.getInitialSourceX(), dragSettings.getInitialSourceY());
            }

            propertiesView.refresh();
        }
    }

    /**
     * Called whenever the mouse is moved.
     */
    public static void eventMouseMoved(MouseEvent event) {
        if (level != null) {
            mouseX = event.getX();
            mouseY = event.getY() - getMouseYOffset();
            if (selected != null) {
                DragSettings hit = selected.mouseIntersectingCorners((event.getX() - level.getOffsetX()) / level.getZoom(), (event.getY() - getMouseYOffset() - level.getOffsetY()) / level.getZoom());
                switch (hit.getType()) {
                    case DragSettings.NONE -> scene.setCursor(Cursor.DEFAULT);
                    case DragSettings.RESIZE -> scene.setCursor(Cursor.NE_RESIZE);
                    case DragSettings.ROTATE -> scene.setCursor(Cursor.OPEN_HAND);
                    case DragSettings.SETANCHOR -> scene.setCursor(Cursor.OPEN_HAND);
                }
            }
        }
    }

    private static DragSettings dragSettings = new DragSettings(DragSettings.NONE);

    public static DragSettings getDragSettings() {
        return dragSettings;
    }

    /**
     * Called whenever the mouse is released.
     */
    public static void eventMouseReleased(MouseEvent event) {
        //If the mouse was released inside the editor window:
        if (event.getX() < splitPane.getDividerPositions()[0] * splitPane.getWidth() && level != null) {
            //Record the changes made to the selected object.
            //Clear all possible redos if changes have been made.
            if (selected != null && selected == oldSelected && oldAttributes != null) {
                UserAction[] changes = selected.getUserActions(oldAttributes);
                if (changes.length > 0) {
                    registerChange(changes);
                    redoActions.clear();
                }
            }

            //Reset the cursor's appearance.
            scene.setCursor(Cursor.DEFAULT);

            //Clear all drag settings now that the mouse has been released.
            dragSettings = new DragSettings(DragSettings.NONE);
            //If we have started placing a strand, attempt to complete the strand.
            if (mode == STRAND && strand1Gooball != null) {
                for (EditorObject ball : level.getLevel().toArray(new EditorObject[0])) {
                    if (ball != strand1Gooball) {
                        if (ball instanceof BallInstance && ball.mouseIntersection((event.getX() - level.getOffsetX()) / level.getZoom(), (event.getY() - getMouseYOffset() - level.getOffsetY()) / level.getZoom()).getType() != DragSettings.NONE) {

                            EditorObject strand = EditorObject.create("Strand", new EditorAttribute[0], level.getLevelObject());

                            strand.setAttribute("gb1", strand1Gooball.getAttribute("id"));
                            strand.setAttribute("gb2", ball.getAttribute("id"));

                            strand.setRealName("Strand");

                            level.getLevel().add(strand);
                            addAnything(strand, level.getLevelObject());
                            break;
                        }
                    }
                }
                strand1Gooball = null;
            }
        }
    }

    /**
     * Called whenever the mouse wheel is scrolled.
     */
    public static void mouse_wheel_moved(ScrollEvent e) {
        //If the mouse was scrolled inside the editor window:
        if (e.getX() < splitPane.getDividerPositions()[0] * splitPane.getWidth() && level != null) {

            //Calculate the new translation and scale.
            double amt = Math.pow(1.25, (e.getDeltaY() / 40));

            double oldTranslateX = level.getOffsetX();
            double oldTranslateY = level.getOffsetY();

            double oldScaleX = level.getZoom();
            double oldScaleY = level.getZoom();

            double mouseX = e.getX();
            double mouseY = e.getY() - getMouseYOffset();

            if (oldScaleX * amt > 0.001 && oldScaleX * amt < 1000 && oldScaleY * amt > 0.001 && oldScaleY * amt < 1000) {

                double newScaleX = oldScaleX * amt;
                double newScaleY = oldScaleY * amt;

                double newTranslateX = (int) ((oldTranslateX - mouseX) * amt + mouseX);
                double newTranslateY = (int) ((oldTranslateY - mouseY) * amt + mouseY);

                //Transform the canvas according to the updated translation and scale.
                t = new Affine();
                t.appendTranslation(newTranslateX, newTranslateY);
                t.appendScale(newScaleX, newScaleY);
                imageCanvas.getGraphicsContext2D().setTransform(t);

                level.setOffsetX(newTranslateX);
                level.setOffsetY(newTranslateY);
                level.setZoom(newScaleX);

                if (selected != null) {
                    DragSettings hit = selected.mouseIntersectingCorners(mouseX, mouseY);
                    switch (hit.getType()) {
                        case DragSettings.NONE -> scene.setCursor(Cursor.DEFAULT);
                        case DragSettings.RESIZE -> scene.setCursor(Cursor.NE_RESIZE);
                        case DragSettings.ROTATE -> scene.setCursor(Cursor.OPEN_HAND);
                    }

                    //Calculate game-relative mouse coordinates.
                    double gameRelativeMouseX = (mouseX - newTranslateX) / newScaleX;
                    double gameRelativeMouseY = (mouseY - newTranslateY) / newScaleX;

                    //Update the selected object according to what kind of operation is being performed.
                    switch (dragSettings.getType()) {
                        case DragSettings.MOVE -> selected.dragFromMouse(gameRelativeMouseX, gameRelativeMouseY, dragSettings.getInitialSourceX(), dragSettings.getInitialSourceY());
                        case DragSettings.RESIZE -> selected.resizeFromMouse(gameRelativeMouseX, gameRelativeMouseY, dragSettings.getInitialSourceX(), dragSettings.getInitialSourceY(), dragSettings.getAnchorX(), dragSettings.getAnchorY());
                        case DragSettings.ROTATE -> selected.rotateFromMouse(gameRelativeMouseX, gameRelativeMouseY, dragSettings.getRotateAngleOffset());
                    }

                    propertiesView.refresh();
                }

                //Redraw the canvas.
                Renderer.drawEverything(level, canvas, imageCanvas);
            }
        }

    }

    public static void keyPressed(KeyEvent event) {
        if (event.getCode() == KeyCode.CONTROL) {
            CTRL = true;
        }
        if (event.getCode() == KeyCode.SHIFT) {
            SHIFT = true;
        }
        if (event.getCode() == KeyCode.DELETE) {
            delete();
        }
        if (CTRL) {
            if (event.getCode() == KeyCode.S) {
                saveLevel(level.getVersion());
            }
            if (event.getCode() == KeyCode.Z) {
                undo();
            }
            if (event.getCode() == KeyCode.X) {
                cut();
            }
            if (event.getCode() == KeyCode.C) {
                copy();
            }
            if (event.getCode() == KeyCode.V) {
                paste();
            }
        }
    }

    public static void keyReleased(KeyEvent event) {
        if (event.getCode() == KeyCode.CONTROL) {
            CTRL = false;
        }
        if (event.getCode() == KeyCode.SHIFT) {
            SHIFT = false;
        }
    }

    public static Stage getStage() {
        return stage;
    }

    private static Canvas canvas;
    private static Canvas imageCanvas;
    private static WorldLevel level = null;

    private static ArrayList<_Ball> importedBalls;

    private static ArrayList<WoGAnimation> animations;

    private static SplitPane splitPane;

    private static TreeTableView<EditorAttribute> propertiesView;

    private static Stage stage;

    private static Scene scene;

    private static boolean stop;

    private static EditorObject moving;
    private static int oldDropIndex;

    private int editingRow = -1;

    private static Pane thingPane;

    private static boolean SHIFT;
    private static boolean CTRL;

    public static WorldLevel getLevel() {
        return level;
    }

    public static void setLevel(WorldLevel _level) {
        level = _level;
        enableAllButtons(false);

        for (int i = 0; i < FileManager.getPaletteBalls().size(); i++) {
            FXCreator.getGooballsToolbar().getItems().get(i).setDisable(FileManager.getPaletteVersions().get(i) != _level.getVersion());
        }

        //Transform the canvas according to the updated translation and scale.
        t = new Affine();
        t.appendTranslation(level.getOffsetX(), level.getOffsetY());
        t.appendScale(level.getZoom(), level.getZoom());
        imageCanvas.getGraphicsContext2D().setTransform(t);
        onSetLevel();
        draw();
    }

    public static void onSetLevel() {
        if (level == null) {
            stage.setTitle("World of Goo Anniversary Editor");
        } else {
            stage.setTitle(level.getLevelName() + " (version " + level.getVersion() + ") — World of Goo Anniversary Editor");
            FXCreator.buttonShowHideAnim.setGraphic(new ImageView(level.isShowAnimations() ? WorldLevel.showHideAnim : WorldLevel.showHideAnim0));
            FXCreator.buttonShowHideCamera.setGraphic(new ImageView(level.isShowCameras() ? WorldLevel.showHideCam1 : WorldLevel.showHideCam0));
            FXCreator.buttonShowHideForcefields.setGraphic(new ImageView(level.isShowForcefields() ? WorldLevel.showHideForcefields1 : WorldLevel.showHideForcefields0));
            FXCreator.buttonShowHideGeometry.setGraphic(new ImageView(level.isShowGeometry() ? WorldLevel.showHideGeometry1 : WorldLevel.showHideGeometry0));
            switch (level.getShowGoos()) {
                case 0 -> FXCreator.buttonShowHideGoos.setGraphic(new ImageView(WorldLevel.showHideGoobs0));
                case 1 -> FXCreator.buttonShowHideGoos.setGraphic(new ImageView(WorldLevel.showHideGoobs1));
                case 2 -> FXCreator.buttonShowHideGoos.setGraphic(new ImageView(WorldLevel.showHideGoobs2));
            }
            FXCreator.buttonShowHideGraphics.setGraphic(new ImageView(level.isShowGraphics() ? WorldLevel.showHideImages1 : WorldLevel.showHideImages0));
            FXCreator.buttonShowHideLabels.setGraphic(new ImageView(level.isShowLabels() ? WorldLevel.showHideLabels1 : WorldLevel.showHideLabels0));
            FXCreator.buttonShowHideParticles.setGraphic(new ImageView(level.isShowParticles() ? WorldLevel.showHideParticles1 : WorldLevel.showHideParticles0));
        }
    }

    public static TabPane getLevelSelectPane() {
        return levelSelectPane;
    }

    public static void changeTableView(EditorObject obj) {
        propertiesView.setRoot(obj.getPropertiesTreeItem());
    }

    public static VBox viewPane;

    public static double lerp(double a, double b, double c) {
        return a + (b - a) * c;
    }

    public static Point2D lineBoxIntersection(double x1, double y1, double theta, double x2, double y2, double sizeX, double sizeY, double rotation) {

        Point2D topLeft = EditorObject.rotate(new Point2D(x2 - sizeX / 2, y2 - sizeY / 2), -rotation, new Point2D(x2, y2));
        Point2D topRight = EditorObject.rotate(new Point2D(x2 + sizeX / 2, y2 - sizeY / 2), -rotation, new Point2D(x2, y2));
        Point2D bottomLeft = EditorObject.rotate(new Point2D(x2 - sizeX / 2, y2 + sizeY / 2), -rotation, new Point2D(x2, y2));
        Point2D bottomRight = EditorObject.rotate(new Point2D(x2 + sizeX / 2, y2 + sizeY / 2), -rotation, new Point2D(x2, y2));

        Point2D top = lineLineSegmentIntersection(x1, y1, -theta, topLeft.getX(), topLeft.getY(), topRight.getX(), topRight.getY());
        Point2D bottom = lineLineSegmentIntersection(x1, y1, -theta, bottomLeft.getX(), bottomLeft.getY(), bottomRight.getX(), bottomRight.getY());
        Point2D left = lineLineSegmentIntersection(x1, y1, -theta, topLeft.getX(), topLeft.getY(), bottomLeft.getX(), bottomLeft.getY());
        Point2D right = lineLineSegmentIntersection(x1, y1, -theta, topRight.getX(), topRight.getY(), bottomRight.getX(), bottomRight.getY());

        Point2D origin = new Point2D(x1, y1);

        double topDistance = top == null ? 100000000 : top.distance(origin);
        double bottomDistance = bottom == null ? 100000000 : bottom.distance(origin);
        double leftDistance = left == null ? 100000000 : left.distance(origin);
        double rightDistance = right == null ? 100000000 : right.distance(origin);

        if (topDistance < bottomDistance && topDistance < leftDistance && topDistance < rightDistance) {
            return top;
        }

        if (bottomDistance < leftDistance && bottomDistance < rightDistance) {
            return bottom;
        }

        if (leftDistance < rightDistance) {
            return left;
        }

        if (right == null) {
            return new Point2D(0, 0);
        }
        return right;
    }

    public static Point2D lineLineSegmentIntersection(double x1, double y1, double theta, double x2, double y2, double x3, double y3) {
        //System.out.println(x1 + ", " + y1 + ", " + theta + ", " + x2 + ", " + y2 + ", " + x3 + ", " + y3);
        if (y3 == y2) {
            y3 += 0.00001;
        }
        if (x3 == x2) {
            x3 += 0.00001;
        }
        double m = (y3 - y2) / (x3 - x2);
        double x = (y2 - x2 * m + x1 * Math.tan(theta) - y1) / (Math.tan(theta) - m);
        double y = (x - x1) * Math.tan(theta) + y1;

        double bruh = 0.01;
        //System.out.println(x + ", " + y);
        //System.out.println(y + ", " + ((x - x2) * m + y2));
        //385.94690307546693, 682.9469030754669
        if (x > Math.min(x2, x3) - bruh && x < Math.max(x2, x3) + bruh && y > Math.min(y2, y3) - bruh && y < Math.max(y2, y3) + bruh) {
            //System.out.println("e");
            return new Point2D(x, y);
        } else {
            return null;
        }
    }

    public static ArrayList<_Ball> getImportedBalls() {
        return importedBalls;
    }

    public static void setImportedBalls(ArrayList<_Ball> importedBalls) {
        Main.importedBalls = importedBalls;
    }

    public static void updateAnimations(float timeElapsed) {
        if (level != null) {
            for (EditorObject object : level.getScene()) {
                if (object instanceof SceneLayer) {
                    String anim = object.getAttribute("anim");
                    if (!anim.equals("")) {
                        for (WoGAnimation animation : animations) {
                            if (animation.getName().equals(anim + ".anim.binuni") || animation.getName().equals(anim + ".anim.binltl")) {
                                object.updateWithAnimation(animation, timeElapsed);
                            }
                        }
                    }
                }
            }
        }
    }

    public static boolean hasAnimation(String potential) {
        for (WoGAnimation animation : animations) {
            if (animation.getName().equals(potential + ".anim.binuni") || animation.getName().equals(potential + ".anim.binltl")) {
                return true;
            }
        }
        return false;
    }

    public static TreeTableView<EditorAttribute> getPropertiesView() {
        return propertiesView;
    }

    public static TreeTableView<EditorObject> getHierarchy() {
        return hierarchy;
    }

    public static void draw() {

        if (level != null) {
            imageCanvas.getGraphicsContext2D().clearRect(-5000000, -5000000, 10000000, 10000000);
            canvas.getGraphicsContext2D().clearRect(-5000000, -5000000, 10000000, 10000000);
            Renderer.drawEverything(level, canvas, imageCanvas);
        } else {
            Renderer.clear(level, canvas, imageCanvas);
        }
    }

    private static ArrayList<EditorObject> particles;

    public static ArrayList<EditorObject> getParticles() {
        return particles;
    }

    public static void setParticles(ArrayList<EditorObject> particles) {
        Main.particles = particles;
    }

    public static EditorObject generateBlankAddinObject() {
        EditorObject addin = EditorObject.create("Addin_addin", new EditorAttribute[0], null);
        EditorObject id = EditorObject.create("Addin_id", new EditorAttribute[0], addin);
        EditorObject name = EditorObject.create("Addin_name", new EditorAttribute[0], addin);
        EditorObject type = EditorObject.create("Addin_type", new EditorAttribute[0], addin);
        EditorObject version = EditorObject.create("Addin_version", new EditorAttribute[0], addin);
        EditorObject description = EditorObject.create("Addin_description", new EditorAttribute[0], addin);
        EditorObject author = EditorObject.create("Addin_author", new EditorAttribute[0], addin);
        EditorObject levels = EditorObject.create("Addin_levels", new EditorAttribute[0], addin);
        EditorObject level = EditorObject.create("Addin_level", new EditorAttribute[0], levels);
        EditorObject levelDir = EditorObject.create("Addin_dir", new EditorAttribute[0], level);
        EditorObject levelName = EditorObject.create("Addin_wtf_name", new EditorAttribute[0], level);
        EditorObject levelSubtitle = EditorObject.create("Addin_subtitle", new EditorAttribute[0], level);
        EditorObject levelOCD = EditorObject.create("Addin_ocd", new EditorAttribute[0], level);
        return addin;
    }

    public static EditorObject generateBlankTextObject() {
        return EditorObject.create("strings", new EditorAttribute[0], null);
    }

    private static VBox vBox;

    public static TreeTableView<EditorObject> hierarchy;

    private final static Stack<UserAction[]> userActions = new Stack<>();
    private static ArrayList<WorldLevel> openedLevels = new ArrayList<>();


    private static void importGameResources() {

        System.out.println("lag 1 (decrypting global resource file)");

        //Open resources.xml for 1.3
        //This takes forever to finish
        if (FileManager.isHasOldWOG()) {
            for (EditorObject resrc : FileManager.openResources(1.3)) {
                if (resrc instanceof ResrcImage) {
                    GlobalResourceManager.addResource(resrc, 1.3);
                }
            }
        }

        //Open resources.xml for 1.5
        //This happens instantly
        if (FileManager.isHasNewWOG()) {
            for (EditorObject resrc : FileManager.openResources(1.5)) {
                if (resrc instanceof ResrcImage) {
                    GlobalResourceManager.addResource(resrc, 1.5);
                }
            }
        }

        particles = new ArrayList<>();

        if (FileManager.isHasOldWOG()) {
            FileManager.openParticles(1.3);
            particles = FileManager.commonBallData;
            for (EditorObject particle : particles) {
                if (particle instanceof _Particle) {
                    ((_Particle) particle).update(1.3);
                } else {
                    particle.update();
                }
            }
        }

        if (FileManager.isHasNewWOG()) {
            FileManager.openParticles(1.5);
            ArrayList<EditorObject> particles2 = FileManager.commonBallData;
            for (EditorObject particle : particles2) {
                if (particle instanceof _Particle) {
                    ((_Particle) particle).update(1.5);
                } else {
                    particle.update();
                }
            }
            particles.addAll(particles2);
        }

        //Load all animations from the game files
        animations = new ArrayList<>();
        try {
            if (FileManager.isHasOldWOG()) {
                File bruh1 = new File(FileManager.getOldWOGdir() + "\\res\\anim");
                for (File second : bruh1.listFiles()) {
                    FileInputStream test2 = new FileInputStream(second);
                    byte[] allBytes = test2.readAllBytes();
                    animations.add(AnimationReader.readBinltl(allBytes, second.getName()));
                }
            }
            if (FileManager.isHasNewWOG()) {
                File bruh2 = new File(FileManager.getNewWOGdir() + "\\res\\anim");
                for (File second : bruh2.listFiles()) {
                    FileInputStream test2 = new FileInputStream(second);
                    byte[] allBytes = test2.readAllBytes();
                    animations.add(AnimationReader.readBinuni(allBytes, second.getName()));
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }

        if (FileManager.isHasOldWOG()) {
            for (EditorObject text : FileManager.openText(1.3)) {
                if (text instanceof TextString) {
                    GlobalResourceManager.addResource(text, 1.3);
                }
            }
        }
        if (FileManager.isHasNewWOG()) {
            for (EditorObject text : FileManager.openText(1.5)) {
                if (text instanceof TextString) {
                    GlobalResourceManager.addResource(text, 1.5);
                }
            }
        }
    }

    /*

        System.out.println("lag 2 (decrypting goo balls)");
        File mogus = new File(FileManager.getOldWOGdir() + "\\res\\balls");
        for (File ballFile : mogus.listFiles()) {
            if (!ballFile.getName().equals("generic") && !ballFile.getName().equals("_generic")) {
                importedBalls.add(FileManager.openBall(ballFile.getName(), 1.3));
                for (EditorObject resrc : FileManager.commonBallResrcData){
                    GlobalResourceManager.addResource(resrc, 1.3);
                }
            }
        }

        //Set up all the toolbar goo ball images
        for (_Ball ball : importedBalls) {
            ball.setVersion(1.3);
            ball.makeImages(1.3);
        }

        ArrayList<_Ball> balls2 = new ArrayList<>();

        File mogus2 = new File(FileManager.getNewWOGdir() + "\\res\\balls");
        for (File ballFile : mogus2.listFiles()) {
            if (!ballFile.getName().equals("generic") && !ballFile.getName().equals("_generic")) {
                balls2.add(FileManager.openBall(ballFile.getName(), 1.5));
                for (EditorObject resrc : FileManager.commonBallResrcData){
                    GlobalResourceManager.addResource(resrc, 1.5);
                }
            }
        }

        for (_Ball ball : balls2) {
            ball.makeImages(1.5);
            ball.setVersion(1.5);
            importedBalls.add(ball);
        }

        for (EditorObject possiblyBall : level.getLevel()){
            if (possiblyBall instanceof BallInstance){
                String ballName = possiblyBall.getAttribute("type");
                boolean alreadyHaveIt = false;
                for (_Ball ball : importedBalls) {
                    if (ball.getObjects().get(0).getAttribute("name").equals(ballName)){
                        alreadyHaveIt = true;
                        break;
                    }
                }
                if (!alreadyHaveIt){
                    importedBalls.add(FileManager.openBall(ballName));
                }
            }
        }
    }
     */

    public static SplitPane getSplitPane() {
        return splitPane;
    }

    public static VBox getViewPane() {
        return viewPane;
    }

    @Override
    public void start(Stage stage2) {

        stage = stage2;

        FileManager.readWOGdirs();

        if (FileManager.getOldWOGdir().equals("") && FileManager.getNewWOGdir().equals("")) {
            Alarms.missingWOG();
        } else {
            startWithWorldOfGooVersion();
        }
    }

    public static void startWithWorldOfGooVersion() {
        System.out.println("1.3 = " + FileManager.getOldWOGdir());
        System.out.println("1.5 = " + FileManager.getNewWOGdir());

        levelSelectPane = new TabPane();

        //Initialize stage name/icon
        stage.setTitle("World of Goo Anniversary Editor");
        //try {
            //stage.getIcons().add(FileManager.openImageFromFilePath(FileManager.superTemporary + "stolenlogo.png"));
        //} catch (Exception e){
        //    e.printStackTrace();
        //}

        //Make menu that currently does nothing
        MenuBar bar = FXCreator.createMenu();

        //Import all goo balls and all misc resources from the game files
        importedBalls = new ArrayList<>();

        importGameResources();

        for (int i = 0; i < FileManager.getPaletteBalls().size(); i++) {

            String ballName = FileManager.getPaletteBalls().get(i);
            double ballVersion = FileManager.getPaletteVersions().get(i);

            _Ball ball = FileManager.openBall(ballName, ballVersion);

            for (EditorObject resrc : FileManager.commonBallResrcData){
                GlobalResourceManager.addResource(resrc, ballVersion);
            }

            ball.makeImages(ballVersion);
            ball.setVersion(ballVersion);

            importedBalls.add(ball);

        }

        System.out.println("lag 3 (making buttons)");

        //Initialize both canvasses
        canvas = new Canvas();
        canvas.setWidth(1400);
        canvas.setHeight(1100);

        imageCanvas = new Canvas();
        imageCanvas.setWidth(1400);
        imageCanvas.setHeight(1100);

        //Ask FXCreator for both TreeTableViews
        hierarchy = FXCreator.makeHierarchy();
        propertiesView = FXCreator.makePropertiesView();

        //Configure PropertiesView
        propertiesView.prefWidthProperty().bind(hierarchy.widthProperty());
        propertiesView.setRoot(new TreeItem<>(new EditorAttribute("", "", "", new InputField("", InputField.ANY), false)));

        //Combine everything weirdly
        splitPane = new SplitPane();
        thingPane = new Pane(imageCanvas);
        StackPane pane = new StackPane(thingPane, new Pane(canvas));
        Separator separator = new Separator();
        viewPane = new VBox(hierarchy, FXCreator.hierarchySwitcherButtons(), separator, propertiesView);
        separator.hoverProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observableValue, Boolean aBoolean, Boolean t1) {
                if (t1) {
                    scene.setCursor(Cursor.N_RESIZE);
                } else {
                    scene.setCursor(Cursor.DEFAULT);
                }
            }
        });
        separator.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                double height = mouseY + getMouseYOffset() - vBox.getChildren().get(4).getLayoutY() - ((TabPane)viewPane.getChildren().get(1)).getHeight() - 2;
                hierarchy.setMinHeight(height);
                hierarchy.setMaxHeight(height);
                hierarchy.setPrefHeight(height);
            }
        });
        splitPane.getItems().addAll(new VBox(levelSelectPane, pane), viewPane);
        stage.addEventFilter(ScrollEvent.SCROLL, Main::mouse_wheel_moved);

        levelSelectPane.getSelectionModel().selectedItemProperty().addListener((observableValue, tab, t1) -> {
            if (t1 == null) {
                level = null;
                onSetLevel();
                enableAllButtons(true);
                if (FileManager.isHasOldWOG()) {
                    FXCreator.buttonNewOld.setDisable(false);
                    FXCreator.buttonOpenOld.setDisable(false);
                }
                if (FileManager.isHasNewWOG()) {
                    FXCreator.buttonNewNew.setDisable(false);
                    FXCreator.buttonOpenNew.setDisable(false);
                }
            }
        });

        levelSelectPane.widthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                int numTabs = Main.getLevelSelectPane().getTabs().size();
                double tabSize = 1 / (numTabs + 1.0);
                Main.getLevelSelectPane().setTabMaxWidth(tabSize * (Main.getLevelSelectPane().getWidth() - 15) - 15);
                Main.getLevelSelectPane().setTabMinWidth(tabSize * (Main.getLevelSelectPane().getWidth() - 15) - 15);
            }
        });

        //Combine everything inside a VBox
        vBox = new VBox(bar);
        FXCreator.buttons(vBox);

        levelSelectPane.setMinHeight(0);
        levelSelectPane.setMaxHeight(0);

        levelSelectPane.setTabClosingPolicy(TabPane.TabClosingPolicy.ALL_TABS);

        vBox.getChildren().add(splitPane);

        //Event handlers
        stage.addEventFilter(MouseEvent.MOUSE_PRESSED, Main::eventMousePressed);
        stage.addEventFilter(MouseEvent.MOUSE_RELEASED, Main::eventMouseReleased);
        stage.addEventFilter(MouseEvent.MOUSE_DRAGGED, Main::eventMouseDragged);
        stage.addEventFilter(MouseEvent.MOUSE_MOVED, Main::eventMouseMoved);

        stage.addEventFilter(KeyEvent.KEY_PRESSED, Main::keyPressed);
        stage.addEventFilter(KeyEvent.KEY_RELEASED, Main::keyReleased);

        scene = new Scene(vBox, 1920, 1080);
        scene.getStylesheets().add("style.css");
        stage.setScene(scene);

        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent windowEvent) {
                windowEvent.consume();
                stage.close();
            }
        });

        stage.show();

        canvas.widthProperty().bind(stage.widthProperty());
        canvas.heightProperty().bind(splitPane.heightProperty());
        imageCanvas.widthProperty().bind(stage.widthProperty());
        imageCanvas.heightProperty().bind(splitPane.heightProperty());
        splitPane.maxHeightProperty().bind(stage.heightProperty());
        splitPane.prefHeightProperty().bind(stage.heightProperty());

        levelSelectPane.setTabDragPolicy(TabPane.TabDragPolicy.REORDER);
        levelSelectPane.setStyle("-fx-open-tab-animation: NONE");

        splitPane.getDividers().get(0).setPosition(0.8);

        stage.heightProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
            }
        });

        new AddinLevelName(null);

        FileManager.openFailedImage();
        System.out.println(FileManager.getFailedImage());

        if (FileManager.isHasOldWOG()) {
            FXCreator.buttonNewOld.setDisable(false);
            FXCreator.buttonOpenOld.setDisable(false);
        }
        if (FileManager.isHasNewWOG()) {
            FXCreator.buttonNewNew.setDisable(false);
            FXCreator.buttonOpenNew.setDisable(false);
        }

        hierarchy.sort();

        System.out.println("started");
        editorWindow = new EditorWindow();
        editorWindow.start();

        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                event.consume();
                resumeLevelClosing();
            }
        });
    }

    public static void resumeLevelClosing() {
        if (levelSelectPane.getTabs().size() == 0) {
            stage.close();
        } else {
            try {
                LevelTab levelTab = (LevelTab) levelSelectPane.getTabs().get(levelSelectPane.getTabs().size() - 1);
                if (levelTab.getLevel().getEditingStatus() == WorldLevel.UNSAVED_CHANGES) {
                    Alarms.closeTabMessage2(levelTab, levelTab.getLevel());
                } else {
                    levelTab.getTabPane().getTabs().remove(levelTab);
                    resumeLevelClosing();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        launch();
    }
}