package com.woogleFX.engine.fx;

import com.woogleFX.editorObjects.ObjectManager;
import com.woogleFX.editorObjects.clipboardHandling.ClipboardManager;
import com.woogleFX.engine.LevelManager;
import com.woogleFX.file.FileManager;
import com.woogleFX.file.resourceManagers.GameResourceManager;
import com.woogleFX.gameData.ball.BallManager;
import com.woogleFX.engine.undoHandling.UndoManager;
import com.woogleFX.engine.gui.PaletteReconfigurator;
import com.woogleFX.gameData.level.LevelCloser;
import com.woogleFX.gameData.level.LevelResourceImporter;
import com.woogleFX.gameData.level.LevelResourceManager;
import com.woogleFX.gameData.level.levelOpening.LevelLoader;
import com.woogleFX.gameData.level.levelSaving.LevelUpdater;
import com.woogleFX.gameData.level.GameVersion;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

public class FXMenu {


    private static void setIcon(MenuItem menuItem, String pathString) {
        menuItem.setGraphic(new ImageView(FileManager.getIcon(pathString)));
    }


    private static final Menu fileMenu = new Menu();

    private static final MenuItem reloadWorldOfGooOldItem = new MenuItem();
    private static final MenuItem reloadWorldOfGooNewItem = new MenuItem();
    private static final MenuItem changeGameDirectoryOldItem = new MenuItem();
    private static final MenuItem changeGameDirectoryNewItem = new MenuItem();
    private static final MenuItem saveOldBallToNewItem = new MenuItem();
    private static final MenuItem saveNewBallToOldItem = new MenuItem();
    private static final MenuItem configurePaletteItem = new MenuItem();
    private static final MenuItem quitItem = new MenuItem();

    private static void file() {

        fileMenu.setText("File");

        String prefix = "ButtonIcons\\File\\";

        reloadWorldOfGooOldItem.setText("Reload World of Goo (1.3)");
        setIcon(reloadWorldOfGooOldItem, prefix + "reload_world_of_goo_old.png");
        reloadWorldOfGooOldItem.setOnAction(e -> GameResourceManager.reloadWorldOfGoo(GameVersion.OLD));
        fileMenu.getItems().add(reloadWorldOfGooOldItem);

        reloadWorldOfGooNewItem.setText("Reload World of Goo (1.5)");
        setIcon(reloadWorldOfGooNewItem, prefix + "reload_world_of_goo_new.png");
        reloadWorldOfGooNewItem.setOnAction(e -> GameResourceManager.reloadWorldOfGoo(GameVersion.NEW));
        fileMenu.getItems().add(reloadWorldOfGooNewItem);

        changeGameDirectoryOldItem.setText("Change World of Goo Directory (1.3)...");
        setIcon(changeGameDirectoryOldItem, prefix + "change_world_of_goo_directory_old.png");
        changeGameDirectoryOldItem.setOnAction(e -> GameResourceManager.changeWorldOfGooDirectory(GameVersion.OLD, false));
        fileMenu.getItems().add(changeGameDirectoryOldItem);

        changeGameDirectoryNewItem.setText("Change World of Goo Directory (1.5)...");
        setIcon(changeGameDirectoryNewItem, prefix + "change_world_of_goo_directory_new.png");
        changeGameDirectoryNewItem.setOnAction(e -> GameResourceManager.changeWorldOfGooDirectory(GameVersion.NEW, false));
        fileMenu.getItems().add(changeGameDirectoryNewItem);

        saveOldBallToNewItem.setText("Copy Ball from 1.3 to 1.5");
        setIcon(saveOldBallToNewItem, prefix + "move_ball_to_new_version.png");
        saveOldBallToNewItem.setOnAction(e -> BallManager.saveBallInVersion(GameVersion.OLD, GameVersion.NEW));
        fileMenu.getItems().add(saveOldBallToNewItem);

        saveNewBallToOldItem.setText("Copy Ball from 1.5 to 1.3");
        setIcon(saveNewBallToOldItem, prefix + "move_ball_to_old_version.png");
        saveNewBallToOldItem.setOnAction(e -> BallManager.saveBallInVersion(GameVersion.NEW, GameVersion.OLD));
        fileMenu.getItems().add(saveNewBallToOldItem);

        configurePaletteItem.setText("Configure Goo Ball Palette...");
        configurePaletteItem.setOnAction(e -> new PaletteReconfigurator().start(new Stage()));
        fileMenu.getItems().add(configurePaletteItem);

        quitItem.setText("Quit");
        setIcon(quitItem, prefix + "quit.png");
        quitItem.setOnAction(e -> LevelCloser.resumeLevelClosing());
        fileMenu.getItems().add(quitItem);

    }


    private static final Menu levelMenu = new Menu();

    private static final MenuItem newLevelOldItem = new MenuItem();
    private static final MenuItem newLevelNewItem = new MenuItem();
    private static final MenuItem openLevelOldItem = new MenuItem();
    private static final MenuItem openLevelNewItem = new MenuItem();
    private static final MenuItem cloneLevelItem = new MenuItem();
    private static final MenuItem saveLevelItem = new MenuItem();
    private static final MenuItem saveAllLevelsItem = new MenuItem();
    private static final MenuItem saveAndPlayLevelItem = new MenuItem();
    private static final MenuItem renameLevelItem = new MenuItem();
    private static final MenuItem deleteLevelItem = new MenuItem();

    private static void level() {

        levelMenu.setText("Level");

        String prefix = "ButtonIcons\\Level\\";

        newLevelOldItem.setText("New Level (1.3)...");
        setIcon(newLevelOldItem, prefix + "new_lvl_old.png");
        newLevelOldItem.setOnAction(e -> LevelLoader.newLevel(GameVersion.OLD));
        levelMenu.getItems().add(newLevelOldItem);

        newLevelNewItem.setText("New Level (1.5)...");
        setIcon(newLevelNewItem, prefix + "new_lvl_new.png");
        newLevelNewItem.setOnAction(e -> LevelLoader.newLevel(GameVersion.NEW));
        levelMenu.getItems().add(newLevelNewItem);

        openLevelOldItem.setText("Open Level (1.3)...");
        setIcon(openLevelOldItem, prefix + "open_lvl_old.png");
        openLevelOldItem.setOnAction(e -> LevelLoader.openLevel(GameVersion.OLD));
        levelMenu.getItems().add(openLevelOldItem);

        openLevelNewItem.setText("Open Level (1.5)...");
        setIcon(openLevelNewItem, prefix + "open_lvl_new.png");
        openLevelNewItem.setOnAction(e -> LevelLoader.openLevel(GameVersion.NEW));
        levelMenu.getItems().add(openLevelNewItem);

        cloneLevelItem.setText("Clone Level...");
        setIcon(cloneLevelItem, prefix + "clone_lvl.png");
        cloneLevelItem.setOnAction(e -> LevelLoader.cloneLevel());
        levelMenu.getItems().add(cloneLevelItem);

        saveLevelItem.setText("Save Level");
        setIcon(saveLevelItem, prefix + "save.png");
        saveLevelItem.setOnAction(e -> LevelUpdater.saveLevel(LevelManager.getLevel()));
        levelMenu.getItems().add(saveLevelItem);

        saveAllLevelsItem.setText("Save All Levels");
        setIcon(saveAllLevelsItem, prefix + "save_all.png");
        saveAllLevelsItem.setOnAction(e -> LevelUpdater.saveAll());
        levelMenu.getItems().add(saveAllLevelsItem);

        saveAndPlayLevelItem.setText("Save and Play Level");
        setIcon(saveAndPlayLevelItem, prefix + "play.png");
        saveAndPlayLevelItem.setOnAction(e -> LevelUpdater.playLevel(LevelManager.getLevel()));
        levelMenu.getItems().add(saveAndPlayLevelItem);

        renameLevelItem.setText("Rename Level");
        setIcon(renameLevelItem, prefix + "rename.png");
        renameLevelItem.setOnAction(e -> LevelUpdater.renameLevel(LevelManager.getLevel()));
        levelMenu.getItems().add(renameLevelItem);

        deleteLevelItem.setText("Delete Level");
        setIcon(deleteLevelItem, prefix + "delete.png");
        deleteLevelItem.setOnAction(e -> LevelUpdater.deleteLevel(LevelManager.getLevel()));
        levelMenu.getItems().add(deleteLevelItem);

    }


    private static final Menu editMenu = new Menu();

    public static final MenuItem undoItem = new MenuItem();
    public static final MenuItem redoItem = new MenuItem();
    private static final MenuItem cutItem = new MenuItem();
    private static final MenuItem copyItem = new MenuItem();
    private static final MenuItem pasteItem = new MenuItem();
    private static final MenuItem deleteItem = new MenuItem();

    private static void edit() {

        editMenu.setText("Edit");

        String prefix = "ButtonIcons\\Edit\\";

        undoItem.setText("Undo");
        setIcon(undoItem, prefix + "undo.png");
        undoItem.setOnAction(e -> UndoManager.undo());
        editMenu.getItems().add(undoItem);

        redoItem.setText("Redo");
        setIcon(redoItem, prefix + "redo.png");
        redoItem.setOnAction(e -> UndoManager.redo());
        editMenu.getItems().add(redoItem);

        cutItem.setText("Cut");
        setIcon(cutItem, prefix + "cut.png");
        cutItem.setOnAction(e -> ClipboardManager.cut());
        editMenu.getItems().add(cutItem);

        copyItem.setText("Copy");
        setIcon(copyItem, prefix + "copy.png");
        copyItem.setOnAction(e -> ClipboardManager.copy());
        editMenu.getItems().add(copyItem);

        pasteItem.setText("Paste");
        setIcon(pasteItem, prefix + "paste.png");
        pasteItem.setOnAction(e -> ClipboardManager.paste());
        editMenu.getItems().add(pasteItem);

        deleteItem.setText("Delete");
        setIcon(deleteItem, prefix + "delete.png");
        deleteItem.setOnAction(e -> ObjectManager.delete(LevelManager.getLevel()));
        editMenu.getItems().add(deleteItem);

    }


    private static final Menu resourcesMenu = new Menu();

    private static final MenuItem updateLevelResourcesItem = new MenuItem();
    private static final MenuItem importImageItem = new MenuItem();
    private static final MenuItem newTextResourceItem = new MenuItem();
    private static final MenuItem cleanLevelResourcesItem = new MenuItem();
    private static final MenuItem setMusicItem = new MenuItem();
    private static final MenuItem setLoopsoundItem = new MenuItem();

    private static void resources() {

        resourcesMenu.setText("Resources");

        String prefix = "ButtonIcons\\Resources\\";

        updateLevelResourcesItem.setText("Update Level Resources...");
        setIcon(updateLevelResourcesItem, prefix + "update_level_resources.png");
        updateLevelResourcesItem.setOnAction(e -> LevelResourceManager.updateLevelResources(LevelManager.getLevel()));
        resourcesMenu.getItems().add(updateLevelResourcesItem);

        importImageItem.setText("Import Images...");
        setIcon(importImageItem, prefix + "import_img.png");
        importImageItem.setOnAction(e -> LevelResourceImporter.importImages(LevelManager.getLevel()));
        resourcesMenu.getItems().add(importImageItem);

        newTextResourceItem.setText("New Text Resource");
        setIcon(newTextResourceItem, prefix + "add_text_resource.png");
        newTextResourceItem.setOnAction(e -> LevelResourceManager.newTextResource(LevelManager.getLevel()));
        resourcesMenu.getItems().add(newTextResourceItem);

        cleanLevelResourcesItem.setText("Clean Resources");
        setIcon(cleanLevelResourcesItem, prefix + "clean_level_resources.png");
        cleanLevelResourcesItem.setOnAction(e -> LevelResourceManager.cleanLevelResources(LevelManager.getLevel()));
        resourcesMenu.getItems().add(cleanLevelResourcesItem);

        setMusicItem.setText("Set Music...");
        setIcon(setMusicItem, prefix + "import_music.png");
        setMusicItem.setOnAction(e -> LevelResourceImporter.importMusic(LevelManager.getLevel()));
        resourcesMenu.getItems().add(setMusicItem);

        setLoopsoundItem.setText("Set Loop Sound...");
        setIcon(setLoopsoundItem, prefix + "import_soundloop.png");
        setLoopsoundItem.setOnAction(e -> LevelResourceImporter.importLoopsound(LevelManager.getLevel()));
        resourcesMenu.getItems().add(setLoopsoundItem);

    }


    private static final MenuBar menuBar = new MenuBar(fileMenu, levelMenu, editMenu, resourcesMenu);
    public static MenuBar getMenuBar() {
        return menuBar;
    }


    public static void init() {
        file();
        level();
        edit();
        resources();
    }


    public static void updateAllButtons() {

        boolean inLevel = LevelManager.getLevel() != null;

        boolean missingOldDir = FileManager.getGameDir(GameVersion.OLD).isEmpty();
        boolean missingNewDir = FileManager.getGameDir(GameVersion.NEW).isEmpty();

        reloadWorldOfGooOldItem.setDisable(missingOldDir);
        saveOldBallToNewItem.setDisable(missingOldDir);

        reloadWorldOfGooNewItem.setDisable(missingNewDir);
        saveNewBallToOldItem.setDisable(missingNewDir);

        for (MenuItem menuItem : levelMenu.getItems()) menuItem.setDisable(!inLevel);

        undoItem.setDisable(!inLevel || LevelManager.getLevel().undoActions.isEmpty());
        redoItem.setDisable(!inLevel || LevelManager.getLevel().redoActions.isEmpty());
        cutItem.setDisable(!inLevel || LevelManager.getLevel().getSelected().length == 0);
        copyItem.setDisable(!inLevel || LevelManager.getLevel().getSelected().length == 0);
        pasteItem.setDisable(!inLevel);
        deleteItem.setDisable(!inLevel || LevelManager.getLevel().getSelected().length == 0);

        for (MenuItem menuItem : resourcesMenu.getItems()) menuItem.setDisable(!inLevel);

        newLevelOldItem.setDisable(missingOldDir);
        openLevelOldItem.setDisable(missingOldDir);

        newLevelNewItem.setDisable(missingNewDir);
        openLevelNewItem.setDisable(missingNewDir);

    }

}
