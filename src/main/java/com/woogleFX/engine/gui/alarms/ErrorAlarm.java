package com.woogleFX.engine.gui.alarms;

import javafx.scene.control.Alert;
import javafx.scene.control.ScrollPane;
import javafx.scene.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

public class ErrorAlarm {

    private static final Logger logger = LoggerFactory.getLogger(ErrorAlarm.class);

    private static Alert activeAlert = null;


    private static final Text text = new Text();
    private static final ScrollPane scrollPane = new ScrollPane(text);


    public static void show(Throwable error) {
        logger.error("", error);
        show(error.getClass().getSimpleName() + ": " + error.getMessage());
    }


    public static void show(String error) {

        // Show the message in the console
        logger.error(error);

        if (activeAlert != null) {
            activeAlert.setContentText(activeAlert.getContentText() + "\n\n" + error);
            return;
        }

        activeAlert = new Alert(Alert.AlertType.ERROR);
        activeAlert.setResizable(true);
        activeAlert.setHeaderText("Error");
        activeAlert.setContentText(error);
        //activeAlert.setGraphic(scrollPane);
        scrollPane.setTranslateX(-20);
        scrollPane.setTranslateY(200);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        //scrollPane.prefWidthProperty().bind(activeAlert.widthProperty().subtract(200));
        scrollPane.prefHeightProperty().bind(activeAlert.heightProperty());
        activeAlert.show();
        activeAlert.setOnCloseRequest(dialogEvent -> activeAlert = null);
    }

}
