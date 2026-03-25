package com.woogleFX.engine.fx.menu;

import com.woogleFX.assets.wog1.animation.WOG1Animation;
import com.woogleFX.assets.wog1.ball.WOG1Ball;
import com.woogleFX.assets.wog1.level.WOG1Level;
import com.woogleFX.assets.wog1.particle.WOG1Particle;
import com.woogleFX.assets.wog2.WOG2Animation.WOG2Animation;
import com.woogleFX.assets.wog2.WOG2Ball.WOG2Ball;
import com.woogleFX.assets.wog2.WOG2Environment.WOG2Environment;
import com.woogleFX.assets.wog2.WOG2Item.WOG2Item;
import com.woogleFX.assets.wog2.WOG2Level.WOG2Level;
import com.woogleFX.assets.wog2.WOG2Particle.WOG2Particle;
import com.woogleFX.assets.wog2.WOG2TerrainType.WOG2TerrainType;
import com.woogleFX.engine.AssetManager;
import com.woogleFX.engine.gui.AssetSelector;
import com.woogleFX.file.FileManager;
import com.woogleFX.assets.GameVersion;
import com.woogleFX.assets.AssetLoader;
import com.woogleFX.assets.AssetUpdater;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

public class FXMenu_Asset {

    private static final Menu levelMenu = new Menu();
    public static Menu getLevelMenu() {
        return levelMenu;
    }


    private static final FXMenu.EditorMenuItem newAnimationOldItem = new FXMenu.EditorMenuItem() {
        @Override
        public void updateDisabled() {
            setDisable(FileManager.getGameDir(GameVersion.VERSION_WOG1_OLD).isEmpty());
        }
    };
    private static final FXMenu.EditorMenuItem newBallOldItem = new FXMenu.EditorMenuItem() {
        @Override
        public void updateDisabled() {
            setDisable(FileManager.getGameDir(GameVersion.VERSION_WOG1_OLD).isEmpty());
        }
    };
    private static final FXMenu.EditorMenuItem newLevelOldItem = new FXMenu.EditorMenuItem() {
        @Override
        public void updateDisabled() {
            setDisable(FileManager.getGameDir(GameVersion.VERSION_WOG1_OLD).isEmpty());
        }
    };
    private static final FXMenu.EditorMenuItem newParticleOldItem = new FXMenu.EditorMenuItem() {
        @Override
        public void updateDisabled() {
            setDisable(FileManager.getGameDir(GameVersion.VERSION_WOG1_OLD).isEmpty());
        }
    };

    private static final FXMenu.EditorMenuItem newAnimationNewItem = new FXMenu.EditorMenuItem() {
        @Override
        public void updateDisabled() {
            setDisable(FileManager.getGameDir(GameVersion.VERSION_WOG1_NEW).isEmpty());
        }
    };
    private static final FXMenu.EditorMenuItem newBallNewItem = new FXMenu.EditorMenuItem() {
        @Override
        public void updateDisabled() {
            setDisable(FileManager.getGameDir(GameVersion.VERSION_WOG1_NEW).isEmpty());
        }
    };
    private static final FXMenu.EditorMenuItem newLevelNewItem = new FXMenu.EditorMenuItem() {
        @Override
        public void updateDisabled() {
            setDisable(FileManager.getGameDir(GameVersion.VERSION_WOG1_NEW).isEmpty());
        }
    };
    private static final FXMenu.EditorMenuItem newParticleNewItem = new FXMenu.EditorMenuItem() {
        @Override
        public void updateDisabled() {
            setDisable(FileManager.getGameDir(GameVersion.VERSION_WOG1_NEW).isEmpty());
        }
    };

    private static final FXMenu.EditorMenuItem newAnimation2Item = new FXMenu.EditorMenuItem() {
        @Override
        public void updateDisabled() {
            setDisable(FileManager.getGameDir(GameVersion.VERSION_WOG2).isEmpty());
        }
    };
    private static final FXMenu.EditorMenuItem newBall2Item = new FXMenu.EditorMenuItem() {
        @Override
        public void updateDisabled() {
            setDisable(FileManager.getGameDir(GameVersion.VERSION_WOG2).isEmpty());
        }
    };
    private static final FXMenu.EditorMenuItem newEnvironment2Item = new FXMenu.EditorMenuItem() {
        @Override
        public void updateDisabled() {
            setDisable(FileManager.getGameDir(GameVersion.VERSION_WOG2).isEmpty());
        }
    };
    private static final FXMenu.EditorMenuItem newItem2Item = new FXMenu.EditorMenuItem() {
        @Override
        public void updateDisabled() {
            setDisable(FileManager.getGameDir(GameVersion.VERSION_WOG2).isEmpty());
        }
    };
    private static final FXMenu.EditorMenuItem newLevel2Item = new FXMenu.EditorMenuItem() {
        @Override
        public void updateDisabled() {
            setDisable(FileManager.getGameDir(GameVersion.VERSION_WOG2).isEmpty());
        }
    };
    private static final FXMenu.EditorMenuItem newParticle2Item = new FXMenu.EditorMenuItem() {
        @Override
        public void updateDisabled() {
            setDisable(FileManager.getGameDir(GameVersion.VERSION_WOG2).isEmpty());
        }
    };
    private static final FXMenu.EditorMenuItem newTerrain2Item = new FXMenu.EditorMenuItem() {
        @Override
        public void updateDisabled() {
            setDisable(FileManager.getGameDir(GameVersion.VERSION_WOG2).isEmpty());
        }
    };


    private static final FXMenu.EditorMenuItem openAnimationOldItem = new FXMenu.EditorMenuItem() {
        @Override
        public void updateDisabled() {
            setDisable(FileManager.getGameDir(GameVersion.VERSION_WOG1_OLD).isEmpty());
        }
    };
    private static final FXMenu.EditorMenuItem openBallOldItem = new FXMenu.EditorMenuItem() {
        @Override
        public void updateDisabled() {
            setDisable(FileManager.getGameDir(GameVersion.VERSION_WOG1_OLD).isEmpty());
        }
    };
    private static final FXMenu.EditorMenuItem openLevelOldItem = new FXMenu.EditorMenuItem() {
        @Override
        public void updateDisabled() {
            setDisable(FileManager.getGameDir(GameVersion.VERSION_WOG1_OLD).isEmpty());
        }
    };
    private static final FXMenu.EditorMenuItem openParticleOldItem = new FXMenu.EditorMenuItem() {
        @Override
        public void updateDisabled() {
            setDisable(FileManager.getGameDir(GameVersion.VERSION_WOG1_OLD).isEmpty());
        }
    };

    private static final FXMenu.EditorMenuItem openAnimationNewItem = new FXMenu.EditorMenuItem() {
        @Override
        public void updateDisabled() {
            setDisable(FileManager.getGameDir(GameVersion.VERSION_WOG1_NEW).isEmpty());
        }
    };
    private static final FXMenu.EditorMenuItem openBallNewItem = new FXMenu.EditorMenuItem() {
        @Override
        public void updateDisabled() {
            setDisable(FileManager.getGameDir(GameVersion.VERSION_WOG1_NEW).isEmpty());
        }
    };
    private static final FXMenu.EditorMenuItem openLevelNewItem = new FXMenu.EditorMenuItem() {
        @Override
        public void updateDisabled() {
            setDisable(FileManager.getGameDir(GameVersion.VERSION_WOG1_NEW).isEmpty());
        }
    };
    private static final FXMenu.EditorMenuItem openParticleNewItem = new FXMenu.EditorMenuItem() {
        @Override
        public void updateDisabled() {
            setDisable(FileManager.getGameDir(GameVersion.VERSION_WOG1_NEW).isEmpty());
        }
    };

    private static final FXMenu.EditorMenuItem openAnimation2Item = new FXMenu.EditorMenuItem() {
        @Override
        public void updateDisabled() {
            setDisable(FileManager.getGameDir(GameVersion.VERSION_WOG2).isEmpty());
        }
    };
    private static final FXMenu.EditorMenuItem openBall2Item = new FXMenu.EditorMenuItem() {
        @Override
        public void updateDisabled() {
            setDisable(FileManager.getGameDir(GameVersion.VERSION_WOG2).isEmpty());
        }
    };
    private static final FXMenu.EditorMenuItem openEnvironment2Item = new FXMenu.EditorMenuItem() {
        @Override
        public void updateDisabled() {
            setDisable(FileManager.getGameDir(GameVersion.VERSION_WOG2).isEmpty());
        }
    };
    private static final FXMenu.EditorMenuItem openItem2Item = new FXMenu.EditorMenuItem() {
        @Override
        public void updateDisabled() {
            setDisable(FileManager.getGameDir(GameVersion.VERSION_WOG2).isEmpty());
        }
    };
    private static final FXMenu.EditorMenuItem openLevel2Item = new FXMenu.EditorMenuItem() {
        @Override
        public void updateDisabled() {
            setDisable(FileManager.getGameDir(GameVersion.VERSION_WOG2).isEmpty());
        }
    };
    private static final FXMenu.EditorMenuItem openParticle2Item = new FXMenu.EditorMenuItem() {
        @Override
        public void updateDisabled() {
            setDisable(FileManager.getGameDir(GameVersion.VERSION_WOG2).isEmpty());
        }
    };
    private static final FXMenu.EditorMenuItem openTerrain2Item = new FXMenu.EditorMenuItem() {
        @Override
        public void updateDisabled() {
            setDisable(FileManager.getGameDir(GameVersion.VERSION_WOG2).isEmpty());
        }
    };

    private static final FXMenu.EditorMenuItem cloneLevelItem = new FXMenu.EditorMenuItem() {
        @Override
        public void updateDisabled() {
            setDisable(AssetManager.getAsset() == null);
        }
    };
    private static final FXMenu.EditorMenuItem saveLevelItem = new FXMenu.EditorMenuItem() {
        @Override
        public void updateDisabled() {
            setDisable(AssetManager.getAsset() == null);
        }
    };
    private static final FXMenu.EditorMenuItem saveAllLevelsItem = new FXMenu.EditorMenuItem() {
        @Override
        public void updateDisabled() {
            setDisable(AssetManager.getAsset() == null);
        }
    };
    private static final FXMenu.EditorMenuItem renameLevelItem = new FXMenu.EditorMenuItem() {
        @Override
        public void updateDisabled() {
            setDisable(AssetManager.getAsset() == null);
        }
    };
    private static final FXMenu.EditorMenuItem deleteLevelItem = new FXMenu.EditorMenuItem() {
        @Override
        public void updateDisabled() {
            setDisable(AssetManager.getAsset() == null);
        }
    };


    public static void init() {

        levelMenu.setText("Asset");

        String prefix = "ButtonIcons/Level/";

        Menu newMenu = new Menu("New Asset...");

        Menu newOldMenu = new Menu("World of Goo 1.3...");

        newAnimationOldItem.setIcon("ObjectIcons/assets/Animation.png");
        newAnimationOldItem.setOnAction(e -> WOG1Animation.assetSelector.start(new Stage(), GameVersion.VERSION_WOG1_OLD, true));
        newAnimationOldItem.setText("New Animation");
        newOldMenu.getItems().add(newAnimationOldItem);

        newBallOldItem.setIcon("ObjectIcons/assets/Ball.png");
        newBallOldItem.setOnAction(e -> WOG1Ball.assetSelector.start(new Stage(), GameVersion.VERSION_WOG1_OLD, true));
        newBallOldItem.setText("New Ball");
        newOldMenu.getItems().add(newBallOldItem);

        newLevelOldItem.setIcon("ObjectIcons/assets/Level.png");
        newLevelOldItem.setOnAction(e -> WOG1Level.assetSelector.start(new Stage(), GameVersion.VERSION_WOG1_OLD, true));
        newLevelOldItem.setText("New Level");
        newOldMenu.getItems().add(newLevelOldItem);

        newParticleOldItem.setIcon("ObjectIcons/assets/Particle.png");
        newParticleOldItem.setOnAction(e -> WOG1Particle.assetSelector.start(new Stage(), GameVersion.VERSION_WOG1_OLD, true));
        newParticleOldItem.setText("New Particle");
        newOldMenu.getItems().add(newParticleOldItem);

        newMenu.getItems().add(newOldMenu);

        Menu newNewMenu = new Menu("World of Goo 1.5...");

        newAnimationNewItem.setIcon("ObjectIcons/assets/Animation.png");
        newAnimationNewItem.setOnAction(e -> WOG1Animation.assetSelector.start(new Stage(), GameVersion.VERSION_WOG1_OLD, true));
        newAnimationNewItem.setText("New Animation");
        newNewMenu.getItems().add(newAnimationNewItem);

        newBallNewItem.setIcon("ObjectIcons/assets/Ball.png");
        newBallNewItem.setOnAction(e -> WOG1Ball.assetSelector.start(new Stage(), GameVersion.VERSION_WOG1_OLD, true));
        newBallNewItem.setText("New Ball");
        newNewMenu.getItems().add(newBallNewItem);

        newLevelNewItem.setIcon("ObjectIcons/assets/Level.png");
        newLevelNewItem.setOnAction(e -> WOG1Level.assetSelector.start(new Stage(), GameVersion.VERSION_WOG1_OLD, true));
        newLevelNewItem.setText("New Level");
        newNewMenu.getItems().add(newLevelNewItem);

        newParticleNewItem.setIcon("ObjectIcons/assets/Particle.png");
        newParticleNewItem.setOnAction(e -> WOG1Particle.assetSelector.start(new Stage(), GameVersion.VERSION_WOG1_OLD, true));
        newParticleNewItem.setText("New Particle");
        newNewMenu.getItems().add(newParticleNewItem);

        newMenu.getItems().add(newNewMenu);

        Menu new2Menu = new Menu("World of Goo 2...");

        newAnimation2Item.setIcon("ObjectIcons/assets/Animation.png");
        newAnimation2Item.setOnAction(e -> WOG2Animation.assetSelector.start(new Stage(), GameVersion.VERSION_WOG1_OLD, true));
        newAnimation2Item.setText("New Animation");
        new2Menu.getItems().add(newAnimation2Item);

        newBall2Item.setIcon("ObjectIcons/assets/Ball.png");
        newBall2Item.setOnAction(e -> WOG2Ball.assetSelector.start(new Stage(), GameVersion.VERSION_WOG1_OLD, true));
        newBall2Item.setText("New Ball");
        new2Menu.getItems().add(newBall2Item);

        newEnvironment2Item.setIcon("ObjectIcons/assets/Environment.png");
        newEnvironment2Item.setOnAction(e -> WOG2Environment.assetSelector.start(new Stage(), GameVersion.VERSION_WOG1_OLD, true));
        newEnvironment2Item.setText("New Environment");
        new2Menu.getItems().add(newEnvironment2Item);

        newItem2Item.setIcon("ObjectIcons/assets/Item.png");
        newItem2Item.setOnAction(e -> WOG2Item.assetSelector.start(new Stage(), GameVersion.VERSION_WOG1_OLD, true));
        newItem2Item.setText("New Item");
        new2Menu.getItems().add(newItem2Item);

        newLevel2Item.setIcon("ObjectIcons/assets/Level.png");
        newLevel2Item.setOnAction(e -> WOG2Level.assetSelector.start(new Stage(), GameVersion.VERSION_WOG1_OLD, true));
        newLevel2Item.setText("New Level");
        new2Menu.getItems().add(newLevel2Item);

        newParticle2Item.setIcon("ObjectIcons/assets/Particle.png");
        newParticle2Item.setOnAction(e -> WOG2Particle.assetSelector.start(new Stage(), GameVersion.VERSION_WOG1_OLD, true));
        newParticle2Item.setText("New Particle");
        new2Menu.getItems().add(newParticle2Item);

        newTerrain2Item.setIcon("ObjectIcons/assets/TerrainType.png");
        newTerrain2Item.setOnAction(e -> WOG2TerrainType.assetSelector.start(new Stage(), GameVersion.VERSION_WOG1_OLD, true));
        newTerrain2Item.setText("New Terrain Type");
        new2Menu.getItems().add(newTerrain2Item);

        newMenu.getItems().add(new2Menu);

        newMenu.setGraphic(new ImageView(FileManager.getIcon(prefix + "new_lvl.png")));
        levelMenu.getItems().add(newMenu);

        Menu openMenu = new Menu("Open Asset...");

        Menu openOldMenu = new Menu("World of Goo 1.3...");

        openAnimationOldItem.setIcon("ObjectIcons/assets/Animation.png");
        openAnimationOldItem.setOnAction(e -> WOG1Animation.assetSelector.start(new Stage(), GameVersion.VERSION_WOG1_OLD, false));
        openAnimationOldItem.setText("Open Animation");
        openOldMenu.getItems().add(openAnimationOldItem);

        openBallOldItem.setIcon("ObjectIcons/assets/Ball.png");
        openBallOldItem.setOnAction(e -> WOG1Ball.assetSelector.start(new Stage(), GameVersion.VERSION_WOG1_OLD, false));
        openBallOldItem.setText("Open Ball");
        openOldMenu.getItems().add(openBallOldItem);

        openLevelOldItem.setIcon("ObjectIcons/assets/Level.png");
        openLevelOldItem.setOnAction(e -> WOG1Level.assetSelector.start(new Stage(), GameVersion.VERSION_WOG1_OLD, false));
        openLevelOldItem.setText("Open Level");
        openOldMenu.getItems().add(openLevelOldItem);

        openParticleOldItem.setIcon("ObjectIcons/assets/Particle.png");
        openParticleOldItem.setOnAction(e -> WOG1Particle.assetSelector.start(new Stage(), GameVersion.VERSION_WOG1_OLD, false));
        openParticleOldItem.setText("Open Particle");
        openOldMenu.getItems().add(openParticleOldItem);

        openMenu.getItems().add(openOldMenu);

        Menu openNewMenu = new Menu("World of Goo 1.5...");

        openAnimationNewItem.setIcon("ObjectIcons/assets/Animation.png");
        openAnimationNewItem.setOnAction(e -> WOG1Animation.assetSelector.start(new Stage(), GameVersion.VERSION_WOG1_NEW, false));
        openAnimationNewItem.setText("Open Animation");
        openNewMenu.getItems().add(openAnimationNewItem);

        openBallNewItem.setIcon("ObjectIcons/assets/Ball.png");
        openBallNewItem.setOnAction(e -> WOG1Ball.assetSelector.start(new Stage(), GameVersion.VERSION_WOG1_NEW, false));
        openBallNewItem.setText("Open Ball");
        openNewMenu.getItems().add(openBallNewItem);

        openLevelNewItem.setIcon("ObjectIcons/assets/Level.png");
        openLevelNewItem.setOnAction(e -> WOG1Level.assetSelector.start(new Stage(), GameVersion.VERSION_WOG1_NEW, false));
        openLevelNewItem.setText("Open Level");
        openNewMenu.getItems().add(openLevelNewItem);

        openParticleNewItem.setIcon("ObjectIcons/assets/Particle.png");
        openParticleNewItem.setOnAction(e -> WOG1Particle.assetSelector.start(new Stage(), GameVersion.VERSION_WOG1_NEW, false));
        openParticleNewItem.setText("Open Particle");
        openNewMenu.getItems().add(openParticleNewItem);

        openMenu.getItems().add(openNewMenu);

        Menu open2Menu = new Menu("World of Goo 2...");

        openAnimation2Item.setIcon("ObjectIcons/assets/Animation.png");
        openAnimation2Item.setOnAction(e -> WOG2Animation.assetSelector.start(new Stage(), GameVersion.VERSION_WOG2, false));
        openAnimation2Item.setText("Open Animation");
        open2Menu.getItems().add(openAnimation2Item);

        openBall2Item.setIcon("ObjectIcons/assets/Ball.png");
        openBall2Item.setOnAction(e -> WOG2Ball.assetSelector.start(new Stage(), GameVersion.VERSION_WOG2, false));
        openBall2Item.setText("Open Ball");
        open2Menu.getItems().add(openBall2Item);

        openEnvironment2Item.setIcon("ObjectIcons/assets/Environment.png");
        openEnvironment2Item.setOnAction(e -> WOG2Environment.assetSelector.start(new Stage(), GameVersion.VERSION_WOG2, false));
        openEnvironment2Item.setText("Open Environment");
        open2Menu.getItems().add(openEnvironment2Item);

        openItem2Item.setIcon("ObjectIcons/assets/Item.png");
        openItem2Item.setOnAction(e -> WOG2Item.assetSelector.start(new Stage(), GameVersion.VERSION_WOG2, false));
        openItem2Item.setText("Open Item");
        open2Menu.getItems().add(openItem2Item);

        openLevel2Item.setIcon("ObjectIcons/assets/Level.png");
        openLevel2Item.setOnAction(e -> WOG2Level.assetSelector.start(new Stage(), GameVersion.VERSION_WOG2, false));
        openLevel2Item.setText("Open Level");
        open2Menu.getItems().add(openLevel2Item);

        openParticle2Item.setIcon("ObjectIcons/assets/Particle.png");
        openParticle2Item.setOnAction(e -> WOG2Particle.assetSelector.start(new Stage(), GameVersion.VERSION_WOG2, false));
        openParticle2Item.setText("Open Particle");
        open2Menu.getItems().add(openParticle2Item);

        openTerrain2Item.setIcon("ObjectIcons/assets/TerrainType.png");
        openTerrain2Item.setOnAction(e -> WOG2TerrainType.assetSelector.start(new Stage(), GameVersion.VERSION_WOG2, false));
        openTerrain2Item.setText("Open Terrain Type");
        open2Menu.getItems().add(openTerrain2Item);

        openMenu.getItems().add(open2Menu);

        openMenu.setOnShowing(event -> addRecentlyOpened(openMenu.getItems()));

//<recentlyOpened>
//	<Asset type="com.woogleFX.assets.wog1.level.WOG1Level" file="C://Users/Crazine/Documents/GOOTOOL_CUSTOM/res/levels/BurningMan" name="BurningMan" version="1.3"/>
//	<Asset type="com.woogleFX.assets.wog1.level.WOG1Level" file="C://Users/Crazine/Documents/GOOTOOL_CUSTOM/res/levels/Chain" name="Chain" version="1.3"/>
// </recentlyOpened>

        openMenu.setGraphic(new ImageView(FileManager.getIcon("ButtonIcons/Level/open_lvl.png")));
        levelMenu.getItems().add(openMenu);

        cloneLevelItem.setText("Clone Asset...");
        cloneLevelItem.setIcon(prefix + "clone_lvl.png");
        cloneLevelItem.setOnAction(e -> AssetLoader.cloneLevel());
        levelMenu.getItems().add(cloneLevelItem);

        saveLevelItem.setText("Save Asset");
        saveLevelItem.setIcon(prefix + "save.png");
        saveLevelItem.setOnAction(e -> AssetUpdater.saveAsset(AssetManager.getAsset()));
        levelMenu.getItems().add(saveLevelItem);

        saveAllLevelsItem.setText("Save All Assets");
        saveAllLevelsItem.setIcon(prefix + "save_all.png");
        saveAllLevelsItem.setOnAction(e -> AssetUpdater.saveAll());
        levelMenu.getItems().add(saveAllLevelsItem);

        renameLevelItem.setText("Rename Asset");
        renameLevelItem.setIcon(prefix + "rename.png");
        renameLevelItem.setOnAction(e -> AssetUpdater.renameLevel(AssetManager.getAsset()));
        levelMenu.getItems().add(renameLevelItem);

        deleteLevelItem.setText("Delete Asset");
        deleteLevelItem.setIcon(prefix + "delete.png");
        deleteLevelItem.setOnAction(e -> AssetUpdater.deleteLevel(AssetManager.getAsset()));
        levelMenu.getItems().add(deleteLevelItem);

    }

    public static void addRecentlyOpened(ObservableList<MenuItem> menu) {
        while (menu.size() > 3) {
            menu.remove(3);
        }

        for (AssetManager.AssetDescription assetDescription : AssetManager.getRecentlyOpenedAssets()) {
            MenuItem menuItem = new MenuItem(assetDescription.name() + " (" + assetDescription.type().getSimpleName().substring(4) + ", version " + assetDescription.version() + ")");
            menuItem.setOnAction(event1 -> {
                try {
                    AssetLoader.openAsset((AssetSelector<?>) assetDescription.type().getField("assetSelector").get(null), assetDescription.file(), assetDescription.name(), assetDescription.version());
                } catch (IllegalAccessException | NoSuchFieldException e) {
                    throw new RuntimeException(e);
                }
            });
            menu.add(menuItem);
        }
    }

}
