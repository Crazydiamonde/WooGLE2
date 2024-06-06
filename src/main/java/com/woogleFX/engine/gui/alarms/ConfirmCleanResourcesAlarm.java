package com.woogleFX.engine.gui.alarms;

import com.woogleFX.editorObjects.EditorObject;
import com.woogleFX.gameData.level.LevelResourceManager;
import com.woogleFX.gameData.level._Level;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.util.ArrayList;

public class ConfirmCleanResourcesAlarm {

    public static void show(_Level level, ArrayList<EditorObject> resourceNames) {

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setHeaderText("Clean Level Resources");
        StringBuilder content = new StringBuilder("Are you sure you want to remove unused resources?\nThe following resources will be cleaned:");

        for (EditorObject resourceName : resourceNames) {
            content.append("\n").append(resourceName.getAttribute("id").stringValue());
        }

        alert.setContentText(content.toString());

        alert.showAndWait().ifPresent(buttonType -> {
            if (buttonType.equals(ButtonType.OK)) {
                LevelResourceManager.confirmedCleanLevelResources(level, resourceNames);
            }
        });
    }
}
