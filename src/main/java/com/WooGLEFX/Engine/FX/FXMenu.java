package com.WooGLEFX.Engine.FX;

import com.WooGLEFX.Engine.Main;
import com.WooGLEFX.File.FileManager;
import com.WooGLEFX.Functions.*;
import com.WooGLEFX.GUI.PaletteReconfigurator;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.FileNotFoundException;
import java.io.IOException;

public class FXMenu {

    public static Menu fileMenu = new Menu("File");

    public static MenuItem reloadWorldOfGooOldItem = new MenuItem("Reload World of Goo (1.3)");
    public static MenuItem reloadWorldOfGooNewItem = new MenuItem("Reload World of Goo (1.5)");
    public static MenuItem changeWorldOfGooDirectoryOldItem = new MenuItem("Change World of Goo Directory (1.3)...");
    public static MenuItem changeWorldOfGooDirectoryNewItem = new MenuItem("Change World of Goo Directory (1.5)...");
    public static MenuItem saveOldBallToNewItem = new MenuItem("Copy Ball from 1.3 to 1.5");
    public static MenuItem saveNewBallToOldItem = new MenuItem("Copy Ball from 1.5 to 1.3");
    public static MenuItem configurePaletteItem = new MenuItem("Configure Goo Ball Palette...");
    public static MenuItem quitItem = new MenuItem("Quit");

    public static Menu levelMenu = new Menu("Level");

    public static MenuItem newLevelOldItem = new MenuItem("New Level (1.3)...");
    public static MenuItem newLevelNewItem = new MenuItem("New Level (1.5)...");
    public static MenuItem openLevelOldItem = new MenuItem("Open Level (1.3)...");
    public static MenuItem openLevelNewItem = new MenuItem("Open Level (1.5)...");
    public static MenuItem cloneLevelItem = new MenuItem("Clone Level...");
    public static MenuItem saveLevelItem = new MenuItem("Save Level");
    public static MenuItem saveAllLevelsItem = new MenuItem("Save All Levels");
    public static MenuItem saveAndPlayLevelItem = new MenuItem("Save and Play Level");
    public static MenuItem renameLevelItem = new MenuItem("Rename Level");
    public static MenuItem deleteLevelItem = new MenuItem("Delete Level");

    public static Menu editMenu = new Menu("Edit");

    public static MenuItem undoItem = new MenuItem("Undo");
    public static MenuItem redoItem = new MenuItem("Redo");
    public static MenuItem cutItem = new MenuItem("Cut");
    public static MenuItem copyItem = new MenuItem("Copy");
    public static MenuItem pasteItem = new MenuItem("Paste");
    public static MenuItem deleteItem = new MenuItem("Delete");

    public static Menu resourcesMenu = new Menu("Resources");

    public static MenuItem updateLevelResourcesItem = new MenuItem("Update Level Resources...");
    public static MenuItem importImageItem = new MenuItem("Import Images...");
    public static MenuItem newTextResourceItem = new MenuItem("New Text Resource");
    public static MenuItem cleanLevelResourcesItem = new MenuItem("Clean Resources");
    public static MenuItem setMusicItem = new MenuItem("Set Music...");
    public static MenuItem setLoopsoundItem = new MenuItem("Set Loop Sound...");


    public static MenuBar createMenu() throws FileNotFoundException {
        MenuBar bar = new MenuBar();

        reloadWorldOfGooOldItem
                .setGraphic(new ImageView(FileManager.getIcon("ButtonIcons\\File\\reload_world_of_goo_old.png")));
        reloadWorldOfGooNewItem
                .setGraphic(new ImageView(FileManager.getIcon("ButtonIcons\\File\\reload_world_of_goo_new.png")));
        changeWorldOfGooDirectoryOldItem.setGraphic(
                new ImageView(FileManager.getIcon("ButtonIcons\\File\\change_world_of_goo_directory_old.png")));
        changeWorldOfGooDirectoryNewItem.setGraphic(
                new ImageView(FileManager.getIcon("ButtonIcons\\File\\change_world_of_goo_directory_new.png")));
        saveOldBallToNewItem
                .setGraphic(new ImageView(FileManager.getIcon("ButtonIcons\\File\\move_ball_to_new_version.png")));
        saveNewBallToOldItem
                .setGraphic(new ImageView(FileManager.getIcon("ButtonIcons\\File\\move_ball_to_old_version.png")));
        quitItem.setGraphic(new ImageView(FileManager.getIcon("ButtonIcons\\File\\quit.png")));

        reloadWorldOfGooOldItem.setOnAction(e -> GameResourceManager.reloadWorldOfGoo(1.3));
        reloadWorldOfGooNewItem.setOnAction(e -> GameResourceManager.reloadWorldOfGoo(1.5));
        changeWorldOfGooDirectoryOldItem.setOnAction(e -> GameResourceManager.changeWorldOfGooDirectory(1.3));
        changeWorldOfGooDirectoryNewItem.setOnAction(e -> GameResourceManager.changeWorldOfGooDirectory(1.5));
        saveOldBallToNewItem.setOnAction(e -> Main.saveBallInVersion(1.3, 1.5));
        saveNewBallToOldItem.setOnAction(e -> Main.saveBallInVersion(1.5, 1.3));
        configurePaletteItem.setOnAction(e -> {
            try {
                new PaletteReconfigurator().start(new Stage());
            } catch (ParserConfigurationException | SAXException | IOException e1) {
                throw new RuntimeException(e1);
            }
        });
        quitItem.setOnAction(e -> Main.quit());

        fileMenu.getItems().addAll(reloadWorldOfGooOldItem, reloadWorldOfGooNewItem, changeWorldOfGooDirectoryOldItem,
                changeWorldOfGooDirectoryNewItem, saveOldBallToNewItem, saveNewBallToOldItem, configurePaletteItem, quitItem);

        newLevelOldItem.setGraphic(new ImageView(FileManager.getIcon("ButtonIcons\\Level\\new_lvl_old.png")));
        newLevelNewItem.setGraphic(new ImageView(FileManager.getIcon("ButtonIcons\\Level\\new_lvl_new.png")));
        openLevelOldItem.setGraphic(new ImageView(FileManager.getIcon("ButtonIcons\\Level\\open_lvl_old.png")));
        openLevelNewItem.setGraphic(new ImageView(FileManager.getIcon("ButtonIcons\\Level\\open_lvl_new.png")));
        cloneLevelItem.setGraphic(new ImageView(FileManager.getIcon("ButtonIcons\\Level\\clone_lvl.png")));
        saveLevelItem.setGraphic(new ImageView(FileManager.getIcon("ButtonIcons\\Level\\save.png")));
        saveAllLevelsItem.setGraphic(new ImageView(FileManager.getIcon("ButtonIcons\\Level\\save_all.png")));
        saveAndPlayLevelItem.setGraphic(new ImageView(FileManager.getIcon("ButtonIcons\\Level\\play.png")));
        renameLevelItem.setGraphic(new ImageView(FileManager.getIcon("ButtonIcons\\Level\\rename.png")));
        deleteLevelItem.setGraphic(new ImageView(FileManager.getIcon("ButtonIcons\\Level\\delete.png")));

        newLevelOldItem.setOnAction(e -> LevelLoader.newLevel(1.3));
        newLevelNewItem.setOnAction(e -> LevelLoader.newLevel(1.5));
        openLevelOldItem.setOnAction(e -> LevelLoader.openLevel(1.3));
        openLevelNewItem.setOnAction(e -> LevelLoader.openLevel(1.5));
        cloneLevelItem.setOnAction(e -> Main.cloneLevel());
        saveLevelItem.setOnAction(e -> LevelUpdater.saveLevel(LevelManager.getLevel()));
        saveAllLevelsItem.setOnAction(e -> LevelUpdater.saveAll());
        saveAndPlayLevelItem.setOnAction(e -> LevelUpdater.playLevel(LevelManager.getLevel()));
        renameLevelItem.setOnAction(e -> LevelUpdater.renameLevel(LevelManager.getLevel()));
        deleteLevelItem.setOnAction(e -> LevelUpdater.deleteLevel(LevelManager.getLevel()));

        levelMenu.getItems().addAll(newLevelOldItem, openLevelOldItem, newLevelNewItem, openLevelNewItem,
                cloneLevelItem, saveLevelItem, saveAllLevelsItem,
                saveAndPlayLevelItem, renameLevelItem, deleteLevelItem);

        undoItem.setGraphic(new ImageView(FileManager.getIcon("ButtonIcons\\Edit\\undo.png")));
        redoItem.setGraphic(new ImageView(FileManager.getIcon("ButtonIcons\\Edit\\redo.png")));
        cutItem.setGraphic(new ImageView(FileManager.getIcon("ButtonIcons\\Edit\\cut.png")));
        copyItem.setGraphic(new ImageView(FileManager.getIcon("ButtonIcons\\Edit\\copy.png")));
        pasteItem.setGraphic(new ImageView(FileManager.getIcon("ButtonIcons\\Edit\\paste.png")));
        deleteItem.setGraphic(new ImageView(FileManager.getIcon("ButtonIcons\\Edit\\delete.png")));

        undoItem.setOnAction(e -> UndoManager.undo());
        redoItem.setOnAction(e -> UndoManager.redo());
        cutItem.setOnAction(e -> ClipboardManager.cut());
        copyItem.setOnAction(e -> ClipboardManager.copy());
        pasteItem.setOnAction(e -> ClipboardManager.paste());
        deleteItem.setOnAction(e -> ObjectManager.delete(LevelManager.getLevel()));

        editMenu.getItems().addAll(undoItem, redoItem, cutItem, copyItem, pasteItem, deleteItem);

        updateLevelResourcesItem
                .setGraphic(new ImageView(FileManager.getIcon("ButtonIcons\\Resources\\update_level_resources.png")));
        importImageItem.setGraphic(new ImageView(FileManager.getIcon("ButtonIcons\\Resources\\import_img.png")));
        newTextResourceItem
                .setGraphic(new ImageView(FileManager.getIcon("ButtonIcons\\Resources\\add_text_resource.png")));
        cleanLevelResourcesItem
                .setGraphic(new ImageView(FileManager.getIcon("ButtonIcons\\Resources\\clean_level_resources.png")));
        setMusicItem.setGraphic(new ImageView(FileManager.getIcon("ButtonIcons\\Resources\\import_music.png")));
        setLoopsoundItem.setGraphic(new ImageView(FileManager.getIcon("ButtonIcons\\Resources\\import_soundloop.png")));

        updateLevelResourcesItem.setOnAction(e -> LevelResourceManager.updateLevelResources(LevelManager.getLevel()));
        importImageItem.setOnAction(e -> LevelResourceManager.importImages(LevelManager.getLevel()));
        newTextResourceItem.setOnAction(e -> LevelResourceManager.newTextResource(LevelManager.getLevel()));
        cleanLevelResourcesItem.setOnAction(e -> LevelResourceManager.cleanLevelResources(LevelManager.getLevel()));
        setMusicItem.setOnAction(e -> LevelResourceManager.importMusic(LevelManager.getLevel()));
        setLoopsoundItem.setOnAction(e -> LevelResourceManager.importLoopsound(LevelManager.getLevel()));

        resourcesMenu.getItems().addAll(updateLevelResourcesItem, importImageItem, newTextResourceItem,
                cleanLevelResourcesItem, setMusicItem, setLoopsoundItem);

        bar.getMenus().addAll(fileMenu, levelMenu, editMenu, resourcesMenu);
        return bar;
    }

}
