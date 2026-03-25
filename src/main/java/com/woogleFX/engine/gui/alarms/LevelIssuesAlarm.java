package com.woogleFX.engine.gui.alarms;

import com.woogleFX.assets.AssetError;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.VBox;

import java.util.List;

public class LevelIssuesAlarm {

    public static boolean show(List<AssetError> error) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setResizable(true);
        alert.setHeaderText("This asset has SUPER CRITICAL issues!");

        VBox errors = new VBox();
        for (AssetError assetError : error) errors.getChildren().add(assetError.getContents());
        errors.setPadding(new Insets(40, 40, 40, 40));

        alert.getDialogPane().setContent(errors);

        alert.setTitle("Asset Issues");
        alert.getButtonTypes().set(0, new ButtonType("Save"));
        alert.getButtonTypes().set(1, new ButtonType("Cancel"));
        alert.showAndWait();
        return alert.getResult().getText().equals("Cancel");
    }

}
