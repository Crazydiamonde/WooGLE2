package com.woogleFX.engine.fx;

import com.woogleFX.file.FileManager;
import com.woogleFX.functions.inputEvents.*;
import com.woogleFX.functions.LevelCloser;
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

        stage.setScene(FXScene.getScene());

        stage.setOnCloseRequest(windowEvent -> {
            windowEvent.consume();
            LevelCloser.resumeLevelClosing();
        });

        stage.show();

    }

}
