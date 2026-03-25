package com.woogleFX.engine.fx.menu;

import com.woogleFX.engine.gui.PaletteReconfigurator;
import com.woogleFX.file.resourceManagers.GameResourceManager;
import com.woogleFX.assets.GameVersion;
import com.woogleFX.assets.AssetCloser;
import javafx.scene.control.Menu;
import javafx.stage.Stage;

public class FXMenu_File {

    private static final Menu fileMenu = new Menu();
    public static Menu getFileMenu() {
        return fileMenu;
    }


    private static final FXMenu.EditorMenuItem reloadWorldOfGooOldItem = new FXMenu.EditorMenuItem() {
        @Override
        public void updateDisabled() {
            setDisable(false);
        }
    };
    private static final FXMenu.EditorMenuItem reloadWorldOfGooNewItem = new FXMenu.EditorMenuItem() {
        @Override
        public void updateDisabled() {
            setDisable(false);
        }
    };
    private static final FXMenu.EditorMenuItem reloadWorldOfGoo2Item = new FXMenu.EditorMenuItem() {
        @Override
        public void updateDisabled() {
            setDisable(false);
        }
    };
    private static final FXMenu.EditorMenuItem changeGameDirectoryOldItem = new FXMenu.EditorMenuItem() {
        @Override
        public void updateDisabled() {
            setDisable(false);
        }
    };
    private static final FXMenu.EditorMenuItem changeGameDirectoryNewItem = new FXMenu.EditorMenuItem() {
        @Override
        public void updateDisabled() {
            setDisable(false);
        }
    };
    private static final FXMenu.EditorMenuItem changeGameDirectory2Item = new FXMenu.EditorMenuItem() {
        @Override
        public void updateDisabled() {
            setDisable(false);
        }
    };
    private static final FXMenu.EditorMenuItem saveOldBallToNewItem = new FXMenu.EditorMenuItem() {
        @Override
        public void updateDisabled() {
            setDisable(false);
        }
    };
    private static final FXMenu.EditorMenuItem saveNewBallToOldItem = new FXMenu.EditorMenuItem() {
        @Override
        public void updateDisabled() {
            setDisable(false);
        }
    };
    private static final FXMenu.EditorMenuItem configurePaletteItem = new FXMenu.EditorMenuItem() {
        @Override
        public void updateDisabled() {
            setDisable(false);
        }
    };
    private static final FXMenu.EditorMenuItem quitItem = new FXMenu.EditorMenuItem() {
        @Override
        public void updateDisabled() {
            setDisable(false);
        }
    };


    public static void init() {

        fileMenu.setText("File");

        String prefix = "ButtonIcons/File/";

        reloadWorldOfGooOldItem.setText("Reload World of Goo 1.3");
        reloadWorldOfGooOldItem.setIcon(prefix + "reload_world_of_goo_old.png");
        reloadWorldOfGooOldItem.setOnAction(e -> GameResourceManager.reloadWorldOfGoo(GameVersion.VERSION_WOG1_OLD));
        fileMenu.getItems().add(reloadWorldOfGooOldItem);

        reloadWorldOfGooNewItem.setText("Reload World of Goo 1.5");
        reloadWorldOfGooNewItem.setIcon(prefix + "reload_world_of_goo_new.png");
        reloadWorldOfGooNewItem.setOnAction(e -> GameResourceManager.reloadWorldOfGoo(GameVersion.VERSION_WOG1_NEW));
        fileMenu.getItems().add(reloadWorldOfGooNewItem);

        reloadWorldOfGoo2Item.setText("Reload World of Goo 2");
        reloadWorldOfGoo2Item.setIcon(prefix + "reload_world_of_goo_new.png");
        reloadWorldOfGoo2Item.setOnAction(e -> GameResourceManager.reloadWorldOfGoo(GameVersion.VERSION_WOG2));
        fileMenu.getItems().add(reloadWorldOfGoo2Item);

        changeGameDirectoryOldItem.setText("Change World of Goo 1.3 Directory...");
        changeGameDirectoryOldItem.setIcon(prefix + "change_world_of_goo_directory_old.png");
        changeGameDirectoryOldItem.setOnAction(e -> GameResourceManager.changeWorldOfGooDirectory(GameVersion.VERSION_WOG1_OLD, false));
        fileMenu.getItems().add(changeGameDirectoryOldItem);

        changeGameDirectoryNewItem.setText("Change World of Goo 1.5 Directory...");
        changeGameDirectoryNewItem.setIcon(prefix + "change_world_of_goo_directory_new.png");
        changeGameDirectoryNewItem.setOnAction(e -> GameResourceManager.changeWorldOfGooDirectory(GameVersion.VERSION_WOG1_NEW, false));
        fileMenu.getItems().add(changeGameDirectoryNewItem);

        changeGameDirectory2Item.setText("Change World of Goo 2 Directory...");
        changeGameDirectory2Item.setIcon(prefix + "change_world_of_goo_directory_new.png");
        changeGameDirectory2Item.setOnAction(e -> GameResourceManager.changeWorldOfGooDirectory(GameVersion.VERSION_WOG2, false));
        fileMenu.getItems().add(changeGameDirectory2Item);

        configurePaletteItem.setText("Configure Goo Ball Palette...");
        configurePaletteItem.setOnAction(e -> new PaletteReconfigurator().start(new Stage()));
        fileMenu.getItems().add(configurePaletteItem);

        quitItem.setText("Quit");
        quitItem.setIcon(prefix + "quit.png");
        quitItem.setOnAction(e -> AssetCloser.resumeLevelClosing());
        fileMenu.getItems().add(quitItem);

    }

}
