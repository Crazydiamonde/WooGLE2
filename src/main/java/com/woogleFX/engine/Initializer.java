package com.woogleFX.engine;

import com.woogleFX.engine.fx.*;
import com.woogleFX.engine.fx.hierarchy.FXHierarchy;
import com.woogleFX.engine.gui.EditorWindow;
import com.woogleFX.engine.gui.alarms.ErrorAlarm;
import com.woogleFX.engine.gui.alarms.MissingWOGAlarm;
import com.woogleFX.file.resourceManagers.BaseGameResources;
import com.woogleFX.file.FileManager;
import com.woogleFX.gameData.font.FontReader;
import com.woogleFX.file.resourceManagers.GlobalResourceManager;
import com.woogleFX.engine.inputEvents.*;
import com.woogleFX.gameData.level.levelOpening.LevelLoader;
import com.woogleFX.gameData.level.GameVersion;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
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
        } catch (ParserConfigurationException | SAXException | IOException e) {
            ErrorAlarm.show(e);
        }

        /* Check that the world of goo directory from properties.txt actually points to a file */
        /* This might require users to reset their WoG directory */
        String oldGameDir = FileManager.getGameDir(GameVersion.OLD);
        if (!oldGameDir.isEmpty() && !Files.exists(Path.of(oldGameDir))) FileManager.setOldWOGdir("");

        String newGameDir = FileManager.getGameDir(GameVersion.NEW);
        if (!newGameDir.isEmpty() && !Files.exists(Path.of(newGameDir))) FileManager.setNewWOGdir("");

        if (oldGameDir.isEmpty() && newGameDir.isEmpty()) {
            MissingWOGAlarm.show();
        } else {
            startWithWorldOfGooVersion();
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

        // Event handlers
        FXStage.getStage().addEventFilter(MouseEvent.MOUSE_PRESSED, MousePressedManager::eventMousePressed);
        FXStage.getStage().addEventFilter(MouseEvent.MOUSE_RELEASED, MouseReleasedManager::eventMouseReleased);
        FXStage.getStage().addEventFilter(MouseEvent.MOUSE_DRAGGED, MouseDraggedManager::eventMouseDragged);
        FXStage.getStage().addEventFilter(MouseEvent.MOUSE_MOVED, MouseMovedManager::eventMouseMoved);
        FXStage.getStage().addEventFilter(KeyEvent.KEY_PRESSED, KeyPressedManager::keyPressed);
        FXStage.getStage().addEventFilter(ScrollEvent.SCROLL, MouseWheelMovedManager::mouseWheelMoved);

    }


    public static void startWithWorldOfGooVersion() {

        BaseGameResources.init();
        GlobalResourceManager.init();

        initializeGUI();

        try {
            FileManager.openFailedImage();
        } catch (IOException ignored) {

        }

        FXEditorButtons.updateAllButtons();
        FXMenu.updateAllButtons();

        EditorWindow editorWindow = new EditorWindow();
        editorWindow.start();

        String[] launchArguments = Main.getLaunchArguments();

        if (launchArguments.length > 0) {
            logger.info("Opening level " + launchArguments[0]);
            if (!FileManager.getGameDir(GameVersion.NEW).isEmpty()) {
                LevelLoader.openLevel(launchArguments[0], GameVersion.NEW);
            } else {
                LevelLoader.openLevel(launchArguments[0], GameVersion.OLD);
            }
        }
        FontReader.read("res\\fonts\\console_2x", GameVersion.NEW);

    }

}
