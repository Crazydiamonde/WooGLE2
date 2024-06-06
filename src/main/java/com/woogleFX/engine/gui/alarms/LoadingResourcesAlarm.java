package com.woogleFX.engine.gui.alarms;

import com.woogleFX.engine.fx.FXStage;
import javafx.scene.control.Alert;

public class LoadingResourcesAlarm {

    public static void show(String error) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setResizable(true);
        alert.setHeaderText("Level resources could not be loaded");
        alert.setContentText(error);
        alert.show();
        if (alert.getHeight() > 1080) {
            alert.setHeight(1080);
        }
        alert.setY(0);
    }


    public static void showInitial(String error) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setResizable(true);
        alert.setHeaderText("Resources could not be loaded");
        alert.setContentText(error);

        alert.initOwner(FXStage.getStage());

        alert.show();
    }

}
