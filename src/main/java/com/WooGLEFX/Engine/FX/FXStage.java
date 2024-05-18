package com.WooGLEFX.Engine.FX;

import com.WooGLEFX.File.FileManager;
import com.WooGLEFX.Functions.InputEvents.*;
import com.WooGLEFX.Functions.LevelCloser;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FXStage {

    private static final Logger logger = LoggerFactory.getLogger(FXStage.class);


    private static Stage stage;
    public static Stage getStage() {
        return stage;
    }


    public static void init(Stage stage) {

        FXStage.stage = stage;


        stage.setTitle("World of Goo Anniversary Editor");


        try {
            stage.getIcons().add(FileManager.getIcon("ButtonIcons\\icon.png"));
        } catch (Exception e) {
            logger.error("", e);
        }

        // Event handlers
        stage.addEventFilter(MouseEvent.MOUSE_PRESSED, MousePressedManager::eventMousePressed);
        stage.addEventFilter(MouseEvent.MOUSE_RELEASED, MouseReleasedManager::eventMouseReleased);
        stage.addEventFilter(MouseEvent.MOUSE_DRAGGED, MouseDraggedManager::eventMouseDragged);
        stage.addEventFilter(MouseEvent.MOUSE_MOVED, MouseMovedManager::eventMouseMoved);
        stage.addEventFilter(KeyEvent.KEY_PRESSED, KeyPressedManager::keyPressed);
        stage.addEventFilter(ScrollEvent.SCROLL, MouseWheelMovedManager::mouseWheelMoved);

        stage.setScene(FXScene.getScene());

        stage.setOnCloseRequest(windowEvent -> {
            windowEvent.consume();
            LevelCloser.resumeLevelClosing();
        });

        stage.show();

    }

}
