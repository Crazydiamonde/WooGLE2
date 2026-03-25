package com.woogleFX.engine.fx.editorButtons;

import com.woogleFX.assets.wog1.animation.WOG1Animation;
import com.woogleFX.assets.wog1.ball.WOG1Ball;
import com.woogleFX.assets.wog1.level.WOG1Level;
import com.woogleFX.assets.wog1.movie.WOG1Movie;
import com.woogleFX.assets.wog1.particle.WOG1Particle;
import com.woogleFX.assets.wog2.WOG2Animation.WOG2Animation;
import com.woogleFX.assets.wog2.WOG2Ball.WOG2Ball;
import com.woogleFX.assets.wog2.WOG2Environment.WOG2Environment;
import com.woogleFX.assets.wog2.WOG2Item.WOG2Item;
import com.woogleFX.assets.wog2.WOG2Level.WOG2Level;
import com.woogleFX.assets.wog2.WOG2Particle.WOG2Particle;
import com.woogleFX.assets.wog2.WOG2TerrainType.WOG2TerrainType;
import com.woogleFX.engine.AssetManager;
import com.woogleFX.engine.fx.menu.FXMenu;
import com.woogleFX.engine.fx.menu.FXMenu_Asset;
import com.woogleFX.file.FileManager;
import com.woogleFX.assets.GameVersion;
import com.woogleFX.assets.AssetLoader;
import com.woogleFX.assets.AssetUpdater;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.Menu;
import javafx.scene.control.Separator;
import javafx.scene.control.ToolBar;
import javafx.stage.Stage;

public class FXEditorButtons_Asset {

    private static final FXEditorButtons.EditorMenuButton buttonNew = new FXEditorButtons.EditorMenuButton() {
        @Override
        public void updateDisabled() {
            setDisable(false);
        }
    };
    private static final FXEditorButtons.EditorMenuButton buttonOpen = new FXEditorButtons.EditorMenuButton() {
        @Override
        public void updateDisabled() {
            setDisable(false);
        }
    };


    private static final FXMenu.EditorMenuItem buttonNewAnimationOld = new FXMenu.EditorMenuItem() {
        @Override
        public void updateDisabled() {
            setDisable(FileManager.getGameDir(GameVersion.VERSION_WOG1_OLD).isEmpty());
        }
    };
    private static final FXMenu.EditorMenuItem buttonNewBallOld = new FXMenu.EditorMenuItem() {
        @Override
        public void updateDisabled() {
            setDisable(FileManager.getGameDir(GameVersion.VERSION_WOG1_OLD).isEmpty());
        }
    };
    private static final FXMenu.EditorMenuItem buttonNewLevelOld = new FXMenu.EditorMenuItem() {
        @Override
        public void updateDisabled() {
            setDisable(FileManager.getGameDir(GameVersion.VERSION_WOG1_OLD).isEmpty());
        }
    };
    private static final FXMenu.EditorMenuItem buttonNewMovieOld = new FXMenu.EditorMenuItem() {
        @Override
        public void updateDisabled() {
            setDisable(FileManager.getGameDir(GameVersion.VERSION_WOG1_OLD).isEmpty());
        }
    };
    private static final FXMenu.EditorMenuItem buttonNewParticleOld = new FXMenu.EditorMenuItem() {
        @Override
        public void updateDisabled() {
            setDisable(FileManager.getGameDir(GameVersion.VERSION_WOG1_OLD).isEmpty());
        }
    };
    private static final Menu newOldMenu = new FXMenu.EditorMenu("World of Goo 1.3...") {
        @Override
        public void updateDisabled() {
            setDisable(FileManager.getGameDir(GameVersion.VERSION_WOG1_OLD).isEmpty());
        }
    };
    private static void initNewOld() {

        buttonNewAnimationOld.setIcon("ObjectIcons/assets/Animation.png");
        buttonNewBallOld.setIcon("ObjectIcons/assets/Ball.png");
        buttonNewLevelOld.setIcon("ObjectIcons/assets/Level.png");
        buttonNewMovieOld.setIcon("ObjectIcons/assets/Movie.png");
        buttonNewParticleOld.setIcon("ObjectIcons/assets/Particle.png");

        buttonNewAnimationOld.setText("New Animation...");
        buttonNewBallOld.setText("New Ball...");
        buttonNewLevelOld.setText("New Level...");
        buttonNewMovieOld.setText("New Movie...");
        buttonNewParticleOld.setText("New Particle...");

        buttonNewAnimationOld.setOnAction(e ->
                WOG1Animation.assetSelector.start(new Stage(), GameVersion.VERSION_WOG1_OLD, true));
        buttonNewBallOld.setOnAction(e ->
                WOG1Ball.assetSelector.start(new Stage(), GameVersion.VERSION_WOG1_OLD, true));
        buttonNewLevelOld.setOnAction(e ->
                WOG1Level.assetSelector.start(new Stage(), GameVersion.VERSION_WOG1_OLD, true));
        buttonNewMovieOld.setOnAction(e ->
                WOG1Movie.assetSelector.start(new Stage(), GameVersion.VERSION_WOG1_OLD, true));
        buttonNewParticleOld.setOnAction(e ->
                WOG1Particle.assetSelector.start(new Stage(), GameVersion.VERSION_WOG1_OLD, true));

        newOldMenu.getItems().add(buttonNewAnimationOld);
        newOldMenu.getItems().add(buttonNewBallOld);
        newOldMenu.getItems().add(buttonNewLevelOld);
        newOldMenu.getItems().add(buttonNewMovieOld);
        newOldMenu.getItems().add(buttonNewParticleOld);

    }

    private static final FXMenu.EditorMenuItem buttonNewAnimationNew = new FXMenu.EditorMenuItem() {
        @Override
        public void updateDisabled() {
            setDisable(FileManager.getGameDir(GameVersion.VERSION_WOG1_NEW).isEmpty());
        }
    };
    private static final FXMenu.EditorMenuItem buttonNewBallNew = new FXMenu.EditorMenuItem() {
        @Override
        public void updateDisabled() {
            setDisable(FileManager.getGameDir(GameVersion.VERSION_WOG1_NEW).isEmpty());
        }
    };
    private static final FXMenu.EditorMenuItem buttonNewLevelNew = new FXMenu.EditorMenuItem() {
        @Override
        public void updateDisabled() {
            setDisable(FileManager.getGameDir(GameVersion.VERSION_WOG1_NEW).isEmpty());
        }
    };
    private static final FXMenu.EditorMenuItem buttonNewMovieNew = new FXMenu.EditorMenuItem() {
        @Override
        public void updateDisabled() {
            setDisable(FileManager.getGameDir(GameVersion.VERSION_WOG1_NEW).isEmpty());
        }
    };
    private static final FXMenu.EditorMenuItem buttonNewParticleNew = new FXMenu.EditorMenuItem() {
        @Override
        public void updateDisabled() {
            setDisable(FileManager.getGameDir(GameVersion.VERSION_WOG1_NEW).isEmpty());
        }
    };
    private static final Menu newNewMenu = new FXMenu.EditorMenu("World of Goo 1.5...") {
        @Override
        public void updateDisabled() {
            setDisable(FileManager.getGameDir(GameVersion.VERSION_WOG1_NEW).isEmpty());
        }
    };
    private static void initNewNew() {

        buttonNewAnimationNew.setIcon("ObjectIcons/assets/Animation.png");
        buttonNewBallNew.setIcon("ObjectIcons/assets/Ball.png");
        buttonNewLevelNew.setIcon("ObjectIcons/assets/Level.png");
        buttonNewMovieNew.setIcon("ObjectIcons/assets/Movie.png");
        buttonNewParticleNew.setIcon("ObjectIcons/assets/Particle.png");

        buttonNewAnimationNew.setText("New Animation...");
        buttonNewBallNew.setText("New Ball...");
        buttonNewLevelNew.setText("New Level...");
        buttonNewMovieNew.setText("New Movie...");
        buttonNewParticleNew.setText("New Particle...");

        buttonNewAnimationNew.setOnAction(e ->
                WOG1Animation.assetSelector.start(new Stage(), GameVersion.VERSION_WOG1_OLD, true));
        buttonNewBallNew.setOnAction(e ->
                WOG1Ball.assetSelector.start(new Stage(), GameVersion.VERSION_WOG1_OLD, true));
        buttonNewLevelNew.setOnAction(e ->
                WOG1Level.assetSelector.start(new Stage(), GameVersion.VERSION_WOG1_OLD, true));
        buttonNewMovieNew.setOnAction(e ->
                WOG1Movie.assetSelector.start(new Stage(), GameVersion.VERSION_WOG1_OLD, true));
        buttonNewParticleNew.setOnAction(e ->
                WOG1Particle.assetSelector.start(new Stage(), GameVersion.VERSION_WOG1_OLD, true));

        newNewMenu.getItems().add(buttonNewAnimationNew);
        newNewMenu.getItems().add(buttonNewBallNew);
        newNewMenu.getItems().add(buttonNewLevelNew);
        newNewMenu.getItems().add(buttonNewMovieNew);
        newNewMenu.getItems().add(buttonNewParticleNew);

    }

    private static final FXMenu.EditorMenuItem buttonNewAnimation2 = new FXMenu.EditorMenuItem() {
        @Override
        public void updateDisabled() {
            setDisable(FileManager.getGameDir(GameVersion.VERSION_WOG2).isEmpty());
        }
    };
    private static final FXMenu.EditorMenuItem buttonNewBall2 = new FXMenu.EditorMenuItem() {
        @Override
        public void updateDisabled() {
            setDisable(FileManager.getGameDir(GameVersion.VERSION_WOG2).isEmpty());
        }
    };
    private static final FXMenu.EditorMenuItem buttonNewEnvironment2 = new FXMenu.EditorMenuItem() {
        @Override
        public void updateDisabled() {
            setDisable(FileManager.getGameDir(GameVersion.VERSION_WOG2).isEmpty());
        }
    };
    private static final FXMenu.EditorMenuItem buttonNewFlashAnimation2 = new FXMenu.EditorMenuItem() {
        @Override
        public void updateDisabled() {
            setDisable(FileManager.getGameDir(GameVersion.VERSION_WOG2).isEmpty());
        }
    };
    private static final FXMenu.EditorMenuItem buttonNewItem2 = new FXMenu.EditorMenuItem() {
        @Override
        public void updateDisabled() {
            setDisable(FileManager.getGameDir(GameVersion.VERSION_WOG2).isEmpty());
        }
    };
    private static final FXMenu.EditorMenuItem buttonNewLevel2 = new FXMenu.EditorMenuItem() {
        @Override
        public void updateDisabled() {
            setDisable(FileManager.getGameDir(GameVersion.VERSION_WOG2).isEmpty());
        }
    };
    private static final FXMenu.EditorMenuItem buttonNewParticle2 = new FXMenu.EditorMenuItem() {
        @Override
        public void updateDisabled() {
            setDisable(FileManager.getGameDir(GameVersion.VERSION_WOG2).isEmpty());
        }
    };
    private static final FXMenu.EditorMenuItem buttonNewTerrain2 = new FXMenu.EditorMenuItem() {
        @Override
        public void updateDisabled() {
            setDisable(FileManager.getGameDir(GameVersion.VERSION_WOG2).isEmpty());
        }
    };
    private static final Menu new2Menu = new FXMenu.EditorMenu("World of Goo 2...") {
        @Override
        public void updateDisabled() {
            setDisable(FileManager.getGameDir(GameVersion.VERSION_WOG2).isEmpty());
        }
    };
    private static void initNew2() {

        buttonNewAnimation2.setIcon("ObjectIcons/assets/Animation.png");
        buttonNewBall2.setIcon("ObjectIcons/assets/Ball.png");
        buttonNewEnvironment2.setIcon("ObjectIcons/assets/Environment.png");
        buttonNewFlashAnimation2.setIcon("ObjectIcons/assets/Movie.png");
        buttonNewItem2.setIcon("ObjectIcons/assets/Item.png");
        buttonNewLevel2.setIcon("ObjectIcons/assets/Level.png");
        buttonNewParticle2.setIcon("ObjectIcons/assets/Particle.png");
        buttonNewTerrain2.setIcon("ObjectIcons/assets/TerrainType.png");

        buttonNewAnimation2.setText("New Animation...");
        buttonNewBall2.setText("New Ball...");
        buttonNewEnvironment2.setText("New Environment...");
        buttonNewFlashAnimation2.setText("New Level...");
        buttonNewItem2.setText("New Item...");
        buttonNewLevel2.setText("New Level...");
        buttonNewParticle2.setText("New Particle...");
        buttonNewTerrain2.setText("New Terrain Type...");

        buttonNewAnimation2.setOnAction(e ->
                WOG1Animation.assetSelector.start(new Stage(), GameVersion.VERSION_WOG2, true));
        buttonNewBall2.setOnAction(e ->
                WOG2Ball.assetSelector.start(new Stage(), GameVersion.VERSION_WOG2, true));
        buttonNewEnvironment2.setOnAction(e ->
                WOG2Environment.assetSelector.start(new Stage(), GameVersion.VERSION_WOG2, true));
        buttonNewFlashAnimation2.setOnAction(e ->
                WOG2Animation.assetSelector.start(new Stage(), GameVersion.VERSION_WOG2, true));
        buttonNewItem2.setOnAction(e ->
                WOG2Item.assetSelector.start(new Stage(), GameVersion.VERSION_WOG2, true));
        buttonNewLevel2.setOnAction(e ->
                WOG2Level.assetSelector.start(new Stage(), GameVersion.VERSION_WOG2, true));
        buttonNewParticle2.setOnAction(e ->
                WOG2Particle.assetSelector.start(new Stage(), GameVersion.VERSION_WOG2, true));
        buttonNewTerrain2.setOnAction(e ->
                WOG2TerrainType.assetSelector.start(new Stage(), GameVersion.VERSION_WOG2, true));

        new2Menu.getItems().add(buttonNewAnimation2);
        new2Menu.getItems().add(buttonNewBall2);
        new2Menu.getItems().add(buttonNewEnvironment2);
        new2Menu.getItems().add(buttonNewFlashAnimation2);
        new2Menu.getItems().add(buttonNewItem2);
        new2Menu.getItems().add(buttonNewLevel2);
        new2Menu.getItems().add(buttonNewParticle2);
        new2Menu.getItems().add(buttonNewTerrain2);

    }


    private static final FXMenu.EditorMenuItem buttonOpenAnimationOld = new FXMenu.EditorMenuItem() {
        @Override
        public void updateDisabled() {
            setDisable(FileManager.getGameDir(GameVersion.VERSION_WOG1_OLD).isEmpty());
        }
    };
    private static final FXMenu.EditorMenuItem buttonOpenBallOld = new FXMenu.EditorMenuItem() {
        @Override
        public void updateDisabled() {
            setDisable(FileManager.getGameDir(GameVersion.VERSION_WOG1_OLD).isEmpty());
        }
    };
    private static final FXMenu.EditorMenuItem buttonOpenLevelOld = new FXMenu.EditorMenuItem() {
        @Override
        public void updateDisabled() {
            setDisable(FileManager.getGameDir(GameVersion.VERSION_WOG1_OLD).isEmpty());
        }
    };
    private static final FXMenu.EditorMenuItem buttonOpenMovieOld = new FXMenu.EditorMenuItem() {
        @Override
        public void updateDisabled() {
            setDisable(FileManager.getGameDir(GameVersion.VERSION_WOG1_OLD).isEmpty());
        }
    };
    private static final FXMenu.EditorMenuItem buttonOpenParticleOld = new FXMenu.EditorMenuItem() {
        @Override
        public void updateDisabled() {
            setDisable(FileManager.getGameDir(GameVersion.VERSION_WOG1_OLD).isEmpty());
        }
    };
    private static final Menu openOldMenu = new FXMenu.EditorMenu("World of Goo 1.3...") {
        @Override
        public void updateDisabled() {
            setDisable(FileManager.getGameDir(GameVersion.VERSION_WOG1_OLD).isEmpty());
        }
    };
    private static void initOpenOld() {

        buttonOpenAnimationOld.setIcon("ObjectIcons/assets/Animation.png");
        buttonOpenBallOld.setIcon("ObjectIcons/assets/Ball.png");
        buttonOpenLevelOld.setIcon("ObjectIcons/assets/Level.png");
        buttonOpenMovieOld.setIcon("ObjectIcons/assets/Movie.png");
        buttonOpenParticleOld.setIcon("ObjectIcons/assets/Particle.png");

        buttonOpenAnimationOld.setText("Open Animation...");
        buttonOpenBallOld.setText("Open Ball...");
        buttonOpenLevelOld.setText("Open Level...");
        buttonOpenMovieOld.setText("Open Movie...");
        buttonOpenParticleOld.setText("Open Particle...");

        buttonOpenAnimationOld.setOnAction(e ->
                WOG1Animation.assetSelector.start(new Stage(), GameVersion.VERSION_WOG1_OLD, false));
        buttonOpenBallOld.setOnAction(e ->
                WOG1Ball.assetSelector.start(new Stage(), GameVersion.VERSION_WOG1_OLD, false));
        buttonOpenLevelOld.setOnAction(e ->
                WOG1Level.assetSelector.start(new Stage(), GameVersion.VERSION_WOG1_OLD, false));
        buttonOpenMovieOld.setOnAction(e ->
                WOG1Movie.assetSelector.start(new Stage(), GameVersion.VERSION_WOG1_OLD, false));
        buttonOpenParticleOld.setOnAction(e ->
                WOG1Particle.assetSelector.start(new Stage(), GameVersion.VERSION_WOG1_OLD, false));

        openOldMenu.getItems().add(buttonOpenAnimationOld);
        openOldMenu.getItems().add(buttonOpenBallOld);
        openOldMenu.getItems().add(buttonOpenLevelOld);
        openOldMenu.getItems().add(buttonOpenMovieOld);
        openOldMenu.getItems().add(buttonOpenParticleOld);

    }

    private static final FXMenu.EditorMenuItem buttonOpenAnimationNew = new FXMenu.EditorMenuItem() {
        @Override
        public void updateDisabled() {
            setDisable(FileManager.getGameDir(GameVersion.VERSION_WOG1_NEW).isEmpty());
        }
    };
    private static final FXMenu.EditorMenuItem buttonOpenBallNew = new FXMenu.EditorMenuItem() {
        @Override
        public void updateDisabled() {
            setDisable(FileManager.getGameDir(GameVersion.VERSION_WOG1_NEW).isEmpty());
        }
    };
    private static final FXMenu.EditorMenuItem buttonOpenLevelNew = new FXMenu.EditorMenuItem() {
        @Override
        public void updateDisabled() {
            setDisable(FileManager.getGameDir(GameVersion.VERSION_WOG1_NEW).isEmpty());
        }
    };
    private static final FXMenu.EditorMenuItem buttonOpenMovieNew = new FXMenu.EditorMenuItem() {
        @Override
        public void updateDisabled() {
            setDisable(FileManager.getGameDir(GameVersion.VERSION_WOG1_NEW).isEmpty());
        }
    };
    private static final FXMenu.EditorMenuItem buttonOpenParticleNew = new FXMenu.EditorMenuItem() {
        @Override
        public void updateDisabled() {
            setDisable(FileManager.getGameDir(GameVersion.VERSION_WOG1_NEW).isEmpty());
        }
    };
    private static final Menu openNewMenu = new FXMenu.EditorMenu("World of Goo 1.5...") {
        @Override
        public void updateDisabled() {
            setDisable(FileManager.getGameDir(GameVersion.VERSION_WOG1_NEW).isEmpty());
        }
    };
    private static void initOpenNew() {

        buttonOpenAnimationNew.setIcon("ObjectIcons/assets/Animation.png");
        buttonOpenBallNew.setIcon("ObjectIcons/assets/Ball.png");
        buttonOpenLevelNew.setIcon("ObjectIcons/assets/Level.png");
        buttonOpenMovieNew.setIcon("ObjectIcons/assets/Movie.png");
        buttonOpenParticleNew.setIcon("ObjectIcons/assets/Particle.png");

        buttonOpenAnimationNew.setText("Open Animation...");
        buttonOpenBallNew.setText("Open Ball...");
        buttonOpenLevelNew.setText("Open Level...");
        buttonOpenMovieNew.setText("Open Movie...");
        buttonOpenParticleNew.setText("Open Particle...");

        buttonOpenAnimationNew.setOnAction(e ->
                WOG1Animation.assetSelector.start(new Stage(), GameVersion.VERSION_WOG1_NEW, false));
        buttonOpenBallNew.setOnAction(e ->
                WOG1Ball.assetSelector.start(new Stage(), GameVersion.VERSION_WOG1_NEW, false));
        buttonOpenLevelNew.setOnAction(e ->
                WOG1Level.assetSelector.start(new Stage(), GameVersion.VERSION_WOG1_NEW, false));
        buttonOpenMovieNew.setOnAction(e ->
                WOG1Movie.assetSelector.start(new Stage(), GameVersion.VERSION_WOG1_NEW, false));
        buttonOpenParticleNew.setOnAction(e ->
                WOG1Particle.assetSelector.start(new Stage(), GameVersion.VERSION_WOG1_NEW, false));

        openNewMenu.getItems().add(buttonOpenAnimationNew);
        openNewMenu.getItems().add(buttonOpenBallNew);
        openNewMenu.getItems().add(buttonOpenLevelNew);
        openNewMenu.getItems().add(buttonOpenMovieNew);
        openNewMenu.getItems().add(buttonOpenParticleNew);

    }

    private static final FXMenu.EditorMenuItem buttonOpenAnimation2 = new FXMenu.EditorMenuItem() {
        @Override
        public void updateDisabled() {
            setDisable(FileManager.getGameDir(GameVersion.VERSION_WOG2).isEmpty());
        }
    };
    private static final FXMenu.EditorMenuItem buttonOpenBall2 = new FXMenu.EditorMenuItem() {
        @Override
        public void updateDisabled() {
            setDisable(FileManager.getGameDir(GameVersion.VERSION_WOG2).isEmpty());
        }
    };
    private static final FXMenu.EditorMenuItem buttonOpenEnvironment2 = new FXMenu.EditorMenuItem() {
        @Override
        public void updateDisabled() {
            setDisable(FileManager.getGameDir(GameVersion.VERSION_WOG2).isEmpty());
        }
    };
    private static final FXMenu.EditorMenuItem buttonOpenFlashAnimation2 = new FXMenu.EditorMenuItem() {
        @Override
        public void updateDisabled() {
            setDisable(FileManager.getGameDir(GameVersion.VERSION_WOG2).isEmpty());
        }
    };
    private static final FXMenu.EditorMenuItem buttonOpenItem2 = new FXMenu.EditorMenuItem() {
        @Override
        public void updateDisabled() {
            setDisable(FileManager.getGameDir(GameVersion.VERSION_WOG2).isEmpty());
        }
    };
    private static final FXMenu.EditorMenuItem buttonOpenLevel2 = new FXMenu.EditorMenuItem() {
        @Override
        public void updateDisabled() {
            setDisable(FileManager.getGameDir(GameVersion.VERSION_WOG2).isEmpty());
        }
    };
    private static final FXMenu.EditorMenuItem buttonOpenParticle2 = new FXMenu.EditorMenuItem() {
        @Override
        public void updateDisabled() {
            setDisable(FileManager.getGameDir(GameVersion.VERSION_WOG2).isEmpty());
        }
    };
    private static final FXMenu.EditorMenuItem buttonOpenTerrain2 = new FXMenu.EditorMenuItem() {
        @Override
        public void updateDisabled() {
            setDisable(FileManager.getGameDir(GameVersion.VERSION_WOG2).isEmpty());
        }
    };


    private static final FXEditorButtons.EditorButton buttonClone = new FXEditorButtons.EditorButton() {
        @Override
        public void updateDisabled() {
            setDisable(AssetManager.getAsset() == null);
        }
    };
    private static final FXEditorButtons.EditorButton buttonSave = new FXEditorButtons.EditorButton() {
        @Override
        public void updateDisabled() {
            setDisable(AssetManager.getAsset() == null);
        }
    };
    private static final FXEditorButtons.EditorButton buttonSaveAs = new FXEditorButtons.EditorButton() {
        @Override
        public void updateDisabled() {
            setDisable(AssetManager.getAsset() == null);
        }
    };
    private static final FXEditorButtons.EditorButton buttonSaveAll = new FXEditorButtons.EditorButton() {
        @Override
        public void updateDisabled() {
            setDisable(AssetManager.getAsset() == null);
        }
    };
    private static final FXEditorButtons.EditorButton buttonExport = new FXEditorButtons.EditorButton() {
        @Override
        public void updateDisabled() {
            setDisable(AssetManager.getAsset() == null);
        }
    };
    private static final FXEditorButtons.EditorButton buttonDummyExport = new FXEditorButtons.EditorButton() {
        @Override
        public void updateDisabled() {
            setDisable(AssetManager.getAsset() == null);
        }
    };
    private static final Menu open2Menu = new FXMenu.EditorMenu("World of Goo 2...") {
        @Override
        public void updateDisabled() {
            setDisable(FileManager.getGameDir(GameVersion.VERSION_WOG2).isEmpty());
        }
    };
    private static void initOpen2() {

        buttonOpenAnimation2.setIcon("ObjectIcons/assets/Animation.png");
        buttonOpenBall2.setIcon("ObjectIcons/assets/Ball.png");
        buttonOpenEnvironment2.setIcon("ObjectIcons/assets/Environment.png");
        buttonOpenFlashAnimation2.setIcon("ObjectIcons/assets/Movie.png");
        buttonOpenItem2.setIcon("ObjectIcons/assets/Item.png");
        buttonOpenLevel2.setIcon("ObjectIcons/assets/Level.png");
        buttonOpenParticle2.setIcon("ObjectIcons/assets/Particle.png");
        buttonOpenTerrain2.setIcon("ObjectIcons/assets/TerrainType.png");

        buttonOpenAnimation2.setText("Open Animation...");
        buttonOpenBall2.setText("Open Ball...");
        buttonOpenEnvironment2.setText("Open Environment...");
        buttonOpenFlashAnimation2.setText("Open Flash... Animation");
        buttonOpenItem2.setText("Open Item...");
        buttonOpenLevel2.setText("Open Level...");
        buttonOpenParticle2.setText("Open Particle...");
        buttonOpenTerrain2.setText("Open Terrain Type...");

        buttonOpenAnimation2.setOnAction(e ->
                WOG1Animation.assetSelector.start(new Stage(), GameVersion.VERSION_WOG2, false));
        buttonOpenBall2.setOnAction(e ->
                WOG2Ball.assetSelector.start(new Stage(), GameVersion.VERSION_WOG2, false));
        buttonOpenEnvironment2.setOnAction(e ->
                WOG2Environment.assetSelector.start(new Stage(), GameVersion.VERSION_WOG2, false));
        buttonOpenFlashAnimation2.setOnAction(e ->
                WOG2Animation.assetSelector.start(new Stage(), GameVersion.VERSION_WOG2, false));
        buttonOpenItem2.setOnAction(e ->
                WOG2Item.assetSelector.start(new Stage(), GameVersion.VERSION_WOG2, false));
        buttonOpenLevel2.setOnAction(e ->
                WOG2Level.assetSelector.start(new Stage(), GameVersion.VERSION_WOG2, false));
        buttonOpenParticle2.setOnAction(e ->
                WOG2Particle.assetSelector.start(new Stage(), GameVersion.VERSION_WOG2, false));
        buttonOpenTerrain2.setOnAction(e ->
                WOG2TerrainType.assetSelector.start(new Stage(), GameVersion.VERSION_WOG2, false));

        open2Menu.getItems().add(buttonOpenAnimation2);
        open2Menu.getItems().add(buttonOpenBall2);
        open2Menu.getItems().add(buttonOpenEnvironment2);
        open2Menu.getItems().add(buttonOpenFlashAnimation2);
        open2Menu.getItems().add(buttonOpenItem2);
        open2Menu.getItems().add(buttonOpenLevel2);
        open2Menu.getItems().add(buttonOpenParticle2);
        open2Menu.getItems().add(buttonOpenTerrain2);

    }


    private static void initEverythingElse(ToolBar toolBar) {

        buttonNew.setIcon("ButtonIcons/Level/new_lvl.png");
        buttonOpen.setIcon("ButtonIcons/Level/open_lvl.png");
        buttonClone.setIcon("ButtonIcons/Level/clone_lvl.png");
        buttonSave.setIcon("ButtonIcons/Level/save.png");
        buttonSaveAs.setIcon("ButtonIcons/Level/save.png");
        buttonSaveAll.setIcon("ButtonIcons/Level/save_all.png");
        buttonExport.setIcon("ButtonIcons/Level/make_goomod.png");
        buttonDummyExport.setIcon("ButtonIcons/Level/make_dummy_goomod.png");

        buttonNew.setTooltip(new FXEditorButtons.DelayedTooltip("New Asset..."));
        buttonOpen.setTooltip(new FXEditorButtons.DelayedTooltip("Open Asset..."));
        buttonClone.setTooltip(new FXEditorButtons.DelayedTooltip("Clone Asset"));
        buttonSave.setTooltip(new FXEditorButtons.DelayedTooltip("Save Asset"));
        buttonSaveAs.setTooltip(new FXEditorButtons.DelayedTooltip("Save Asset As..."));
        buttonSaveAll.setTooltip(new FXEditorButtons.DelayedTooltip("Save All Assets"));
        buttonExport.setTooltip(new FXEditorButtons.DelayedTooltip("Export Asset"));
        buttonDummyExport.setTooltip(new FXEditorButtons.DelayedTooltip("Export Asset Without Addin Info"));

        buttonClone.setOnAction(e -> AssetLoader.cloneLevel());
        buttonSave.setOnAction(e -> AssetUpdater.saveAsset(AssetManager.getAsset()));
        buttonSaveAs.setOnAction(e -> AssetUpdater.saveAssetAs(AssetManager.getAsset()));
        buttonSaveAll.setOnAction(e -> AssetUpdater.saveAll());
        buttonExport.setOnAction(e -> AssetManager.getAsset().export(true));
        buttonDummyExport.setOnAction(e -> AssetManager.getAsset().export(false));

        toolBar.getItems().add(buttonNew);
        toolBar.getItems().add(buttonOpen);
        toolBar.getItems().add(buttonClone);
        toolBar.getItems().add(buttonSave);
        toolBar.getItems().add(buttonSaveAs);
        toolBar.getItems().add(buttonSaveAll);
        toolBar.getItems().add(new Separator());
        toolBar.getItems().add(buttonExport);
        toolBar.getItems().add(buttonDummyExport);

    }


    public static void asset(ToolBar toolBar) {

        initNewOld();
        buttonNew.getItems().add(newOldMenu);

        initNewNew();
        buttonNew.getItems().add(newNewMenu);

        initNew2();
        buttonNew.getItems().add(new2Menu);

        initOpenOld();
        buttonOpen.getItems().add(openOldMenu);

        initOpenNew();
        buttonOpen.getItems().add(openNewMenu);

        initOpen2();
        buttonOpen.getItems().add(open2Menu);

        buttonOpen.setOnShowing(new EventHandler<Event>() {
            @Override
            public void handle(Event event) {
                FXMenu_Asset.addRecentlyOpened(buttonOpen.getItems());
            }
        });

        initEverythingElse(toolBar);

    }

}
