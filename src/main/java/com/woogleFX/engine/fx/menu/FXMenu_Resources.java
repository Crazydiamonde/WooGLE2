package com.woogleFX.engine.fx.menu;

import com.woogleFX.engine.AssetManager;
import com.woogleFX.file.resourceManagers.LevelResourceImporter;
import com.woogleFX.file.resourceManagers.LevelResourceManager;
import javafx.scene.control.Menu;

public class FXMenu_Resources {

    private static final Menu resourcesMenu = new Menu();
    public static Menu getResourcesMenu() {
        return resourcesMenu;
    }


    private static final FXMenu.EditorMenuItem updateLevelResourcesItem = new FXMenu.EditorMenuItem() {
        @Override
        public void updateDisabled() {
            setDisable(false);
        }
    };
    private static final FXMenu.EditorMenuItem importImageItem = new FXMenu.EditorMenuItem() {
        @Override
        public void updateDisabled() {
            setDisable(false);
        }
    };
    private static final FXMenu.EditorMenuItem newTextResourceItem = new FXMenu.EditorMenuItem() {
        @Override
        public void updateDisabled() {
            setDisable(false);
        }
    };
    private static final FXMenu.EditorMenuItem cleanLevelResourcesItem = new FXMenu.EditorMenuItem() {
        @Override
        public void updateDisabled() {
            setDisable(false);
        }
    };
    private static final FXMenu.EditorMenuItem setMusicItem = new FXMenu.EditorMenuItem() {
        @Override
        public void updateDisabled() {
            setDisable(false);
        }
    };
    private static final FXMenu.EditorMenuItem setLoopsoundItem = new FXMenu.EditorMenuItem() {
        @Override
        public void updateDisabled() {
            setDisable(false);
        }
    };


    public static void init() {

        resourcesMenu.setText("Resources");

        String prefix = "ButtonIcons/Resources/";

        updateLevelResourcesItem.setText("Update Level Resources...");
        updateLevelResourcesItem.setIcon(prefix + "update_level_resources.png");
        updateLevelResourcesItem.setOnAction(e -> LevelResourceManager.updateLevelResources(AssetManager.getAsset()));
        resourcesMenu.getItems().add(updateLevelResourcesItem);

        importImageItem.setText("Import Images...");
        importImageItem.setIcon(prefix + "import_img.png");
        importImageItem.setOnAction(e -> LevelResourceImporter.importResources(AssetManager.getAsset()));
        resourcesMenu.getItems().add(importImageItem);

        newTextResourceItem.setText("New Text Resource");
        newTextResourceItem.setIcon(prefix + "add_text_resource.png");
        newTextResourceItem.setOnAction(e -> LevelResourceManager.newTextResource(AssetManager.getAsset()));
        resourcesMenu.getItems().add(newTextResourceItem);

        cleanLevelResourcesItem.setText("Clean Resources");
        cleanLevelResourcesItem.setIcon(prefix + "clean_level_resources.png");
        cleanLevelResourcesItem.setOnAction(e -> LevelResourceManager.cleanLevelResources(AssetManager.getAsset()));
        resourcesMenu.getItems().add(cleanLevelResourcesItem);

        setMusicItem.setText("Set Music...");
        setMusicItem.setIcon(prefix + "import_music.png");
        setMusicItem.setOnAction(e -> LevelResourceImporter.importMusic(AssetManager.getAsset()));
        resourcesMenu.getItems().add(setMusicItem);

        setLoopsoundItem.setText("Set Loop Sound...");
        setLoopsoundItem.setIcon(prefix + "import_soundloop.png");
        setLoopsoundItem.setOnAction(e -> LevelResourceImporter.importLoopsound(AssetManager.getAsset()));
        resourcesMenu.getItems().add(setLoopsoundItem);

    }

}
