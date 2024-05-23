package com.woogleFX.engine;

import com.woogleFX.editorObjects._Ball;
import com.woogleFX.engine.fx.*;
import com.woogleFX.file.FileManager;
import com.woogleFX.file.resourceManagers.GlobalResourceManager;
import com.woogleFX.functions.*;
import com.woogleFX.file.resourceManagers.BallManager;
import com.woogleFX.engine.gui.Alarms;
import com.woogleFX.editorObjects.EditorObject;
import com.woogleFX.structures.GameVersion;
import com.worldOfGoo.addin.AddinLevelName;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Initializer {

    private static final Logger logger = LoggerFactory.getLogger(Initializer.class);


    public static void start(Stage stage2) {

        logger.debug(Initializer.class.getProtectionDomain().getCodeSource().getLocation().getPath());

        FXScene.init();
        FXStage.init(stage2);

        try {
            FileManager.readWOGdirs();

            /* Check that the world of goo directory from properties.txt actually points to a file */
            /* This might require users to reset their WoG directory */
            if (!FileManager.getOldWOGdir().isEmpty() && !Files.exists(Path.of(FileManager.getOldWOGdir()))) {
                FileManager.setOldWOGdir("");
            }
            if (!FileManager.getNewWOGdir().isEmpty() && !Files.exists(Path.of(FileManager.getNewWOGdir()))) {
                FileManager.setNewWOGdir("");
            }

            if (FileManager.getOldWOGdir().isEmpty() && FileManager.getNewWOGdir().isEmpty()) {
                Alarms.missingWOG();
            } else {
                startWithWorldOfGooVersion(stage2);
            }

        } catch (ParserConfigurationException | SAXException | IOException e) {
            Alarms.errorMessage(e);
        }
    }


    private static void initializeGUI() {

        FXCanvas.init();
        FXContainers.init();
        FXEditorButtons.init();
        FXHierarchy.init();
        FXLevelSelectPane.init();
        FXMenu.init();
        FXPropertiesView.init();

    }


    public static void startWithWorldOfGooVersion(Stage stage) {

        initializeGUI();

        logger.info("1.3 = " + FileManager.getOldWOGdir());
        logger.info("1.5 = " + FileManager.getNewWOGdir());

        // Import all goo balls and all misc resources from the game files
        if (!FileManager.getOldWOGdir().isEmpty()) LevelResourceManager.importGameResources(GameVersion.OLD);
        if (!FileManager.getNewWOGdir().isEmpty()) LevelResourceManager.importGameResources(GameVersion.NEW);

        for (int i = 0; i < PaletteManager.getPaletteBalls().size(); i++) {

            String ballName = PaletteManager.getPaletteBalls().get(i);
            GameVersion ballVersion = PaletteManager.getPaletteVersions().get(i);

            try {
                _Ball ball = FileManager.openBall(ballName, ballVersion);

                for (EditorObject resrc : FileManager.commonBallResrcData) {
                    GlobalResourceManager.addResource(resrc, ballVersion);
                }

                if (ball != null) {
                    ball.makeImages(ballVersion);
                    ball.setVersion(ballVersion);
                    BallManager.getImportedBalls().add(ball);
                }
            } catch (ParserConfigurationException | SAXException | IOException e) {
                Alarms.errorMessage(e);
            }

        }

        new AddinLevelName(null);

        try {
            FileManager.openFailedImage();
        } catch (FileNotFoundException ignored) {

        }

        FXEditorButtons.updateAllButtons();
        FXMenu.updateAllButtons();

        EditorWindow editorWindow = new EditorWindow();
        editorWindow.start();

        String[] launchArguments = Main.getLaunchArguments();

        if (launchArguments.length > 0) {
            logger.info("Opening level " + launchArguments[0]);
            if (FileManager.hasNewWOG()) {
                LevelLoader.openLevel(launchArguments[0], GameVersion.NEW);
            } else {
                LevelLoader.openLevel(launchArguments[0], GameVersion.OLD);
            }
        }
    }

}