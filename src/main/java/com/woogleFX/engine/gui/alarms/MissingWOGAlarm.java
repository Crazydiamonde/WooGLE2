package com.woogleFX.engine.gui.alarms;

import com.woogleFX.engine.Initializer;
import com.woogleFX.file.resourceManagers.GameResourceManager;
import com.woogleFX.assets.GameVersion;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class MissingWOGAlarm {

    public static void show() {

        VBox everythingBox = new VBox();
        everythingBox.setPadding(new Insets(10, 10, 10, 10));
        everythingBox.setSpacing(10);
        Label info = new Label("Before you get started, the editor needs to be told the location of any World of Goo directory.\nSelect any of the buttons below and locate the corresponding version of World of Goo.");
        Label info2 = new Label("You can also select or change World of Goo directories at any time by selecting File>>Change World of Goo Directory.");
        info.setWrapText(true);
        info2.setWrapText(true);
        Button selectOldButton = new Button("WoG 1.3...");
        Button selectNewButton = new Button("WoG 1.5...");
        Button select2Button = new Button("WoG 2...");
        Stage stage = new Stage();

        selectOldButton.setOnAction(actionEvent -> {
            if (GameResourceManager.changeWorldOfGooDirectory(GameVersion.VERSION_WOG1_OLD, true)) {
                Initializer.startWithWorldOfGooVersion();
                stage.close();
            }
        });

        selectNewButton.setOnAction(actionEvent -> {
            if (GameResourceManager.changeWorldOfGooDirectory(GameVersion.VERSION_WOG1_NEW, true)) {
                Initializer.startWithWorldOfGooVersion();
                stage.close();
            }
        });

        select2Button.setOnAction(actionEvent -> {
            if (GameResourceManager.changeWorldOfGooDirectory(GameVersion.VERSION_WOG2, true)) {
                Initializer.startWithWorldOfGooVersion();
                stage.close();
            }
        });

        HBox idk = new HBox(selectOldButton, selectNewButton, select2Button);
        idk.setSpacing(20);
        idk.setPadding(new Insets(0, 10, 0, 10));
        everythingBox.getChildren().addAll(info, idk, info2);
        Scene scene = new Scene(everythingBox, 280, 185);
        stage.setResizable(false);
        stage.initStyle(StageStyle.UTILITY);
        stage.setTitle("Missing World of Goo Directory!");
        stage.setScene(scene);
        stage.show();

    }

}
