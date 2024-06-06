package com.woogleFX.engine.gui.alarms;

import com.woogleFX.engine.Initializer;
import com.woogleFX.file.resourceManagers.GameResourceManager;
import com.woogleFX.gameData.level.GameVersion;
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
        Label info = new Label("You appear to have no World of Goo directories specified.");
        Label info2 = new Label("You can also select or change World of Goo directories using File>>Change World of Goo Directory.");
        info.setWrapText(true);
        info2.setWrapText(true);
        Button selectOldButton = new Button("Select 1.3 version...");
        Button selectNewButton = new Button("Select 1.5 version...");
        Stage stage = new Stage();

        selectOldButton.setOnAction(actionEvent -> {
            if (GameResourceManager.changeWorldOfGooDirectory(GameVersion.OLD, true)) {
                Initializer.startWithWorldOfGooVersion();
                stage.close();
            }
        });

        selectNewButton.setOnAction(actionEvent -> {
            if (GameResourceManager.changeWorldOfGooDirectory(GameVersion.OLD, true)) {
                Initializer.startWithWorldOfGooVersion();
                stage.close();
            }
        });

        HBox idk = new HBox(selectOldButton, selectNewButton);
        idk.setSpacing(20);
        everythingBox.getChildren().addAll(info, idk, info2);
        Scene scene = new Scene(everythingBox, 280, 170);
        stage.setResizable(false);
        stage.initStyle(StageStyle.UTILITY);
        stage.setTitle("Missing World of Goo Directory");
        stage.setScene(scene);
        stage.show();

    }

}
