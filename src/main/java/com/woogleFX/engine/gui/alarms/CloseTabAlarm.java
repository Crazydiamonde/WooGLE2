package com.woogleFX.engine.gui.alarms;

import com.woogleFX.editorObjects.EditorObject;
import com.woogleFX.engine.LevelManager;
import com.woogleFX.engine.fx.FXLevelSelectPane;
import com.woogleFX.engine.fx.FXPropertiesView;
import com.woogleFX.engine.fx.hierarchy.FXHierarchy;
import com.woogleFX.gameData.level.LevelCloser;
import com.woogleFX.gameData.level._Level;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Tab;

public class CloseTabAlarm {

    public static void show(Tab tab, _Level level) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setHeaderText("Unsaved Changes");
        alert.setContentText("Ignore unsaved changes?");

        alert.showAndWait().ifPresent(buttonType -> {
            if (buttonType.equals(ButtonType.OK)) {
                if (tab.getTabPane().getTabs().size() == 1) {
                    FXLevelSelectPane.getLevelSelectPane().setMinHeight(0);
                    FXLevelSelectPane.getLevelSelectPane().setMaxHeight(0);
                    FXHierarchy.getHierarchy().setRoot(null);
                    FXPropertiesView.changeTableView(new EditorObject[]{});
                }
                Platform.runLater(() -> tab.getTabPane().getTabs().remove(tab));
            } else if (buttonType.equals(ButtonType.CANCEL)) {
                level.setEditingStatus(level.getEditingStatus(), level == LevelManager.getLevel());
            }
        });
    }

    public static void showClosingEditor(Tab tab, _Level level) {

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setHeaderText("Unsaved Changes");
        alert.setContentText("Ignore unsaved changes?");

        alert.showAndWait().ifPresent(buttonType -> {
            if (buttonType.equals(ButtonType.OK)) {
                if (tab.getTabPane().getTabs().size() == 1) {
                    FXLevelSelectPane.getLevelSelectPane().setMinHeight(0);
                    FXLevelSelectPane.getLevelSelectPane().setMaxHeight(0);
                    FXHierarchy.getHierarchy().setRoot(null);
                    FXPropertiesView.changeTableView(new EditorObject[]{});
                }
                Platform.runLater(() -> {
                    tab.getTabPane().getTabs().remove(tab);
                    LevelCloser.resumeLevelClosing();
                });
            } else if (buttonType.equals(ButtonType.CANCEL)) {
                level.setEditingStatus(level.getEditingStatus(), level == LevelManager.getLevel());
            }
        });
    }

}
