package com.woogleFX.engine.gui.alarms;

import javafx.scene.control.Alert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ErrorAlarm {

    private static final Logger logger = LoggerFactory.getLogger(ErrorAlarm.class);


    public static void show(Throwable error) {
        show(error.getClass().getSimpleName() + ": " + error.getMessage());
    }


    public static void show(String error) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setResizable(true);
        alert.setHeaderText("Error");
        alert.setContentText(error);
        alert.show();
        // Show the message in the console
        logger.error(error);
    }

}
