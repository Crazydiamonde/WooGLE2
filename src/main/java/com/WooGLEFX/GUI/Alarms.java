package com.WooGLEFX.GUI;

import com.WooGLEFX.Engine.Main;
import com.WooGLEFX.File.FileManager;
import com.WooGLEFX.Structures.WorldLevel;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class Alarms {

    public static void errorMessage(Exception error) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setHeaderText("Error");
        alert.setContentText(error.getMessage());
        alert.show();
    }

    public static void closeTabMessage(Tab tab, WorldLevel level) {

        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setHeaderText("Unsaved Changes");
        alert.setContentText("Ignore unsaved changes?");

        alert.showAndWait().ifPresent(buttonType -> {
            if (buttonType.equals(ButtonType.OK)) {
                if (tab.getTabPane().getTabs().size() == 1) {
                    Main.getLevelSelectPane().setMinHeight(0);
                    Main.getLevelSelectPane().setMaxHeight(0);
                }
                Platform.runLater(() -> tab.getTabPane().getTabs().remove(tab));
            } else if (buttonType.equals(ButtonType.CANCEL)) {
                level.setEditingStatus(level.getEditingStatus(), level == Main.getLevel());
            }
        });
    }

    public static void closeTabMessage2(Tab tab, WorldLevel level) {

        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setHeaderText("Unsaved Changes");
        alert.setContentText("Ignore unsaved changes?");

        alert.showAndWait().ifPresent(buttonType -> {
            if (buttonType.equals(ButtonType.OK)) {
                if (tab.getTabPane().getTabs().size() == 1) {
                    Main.getLevelSelectPane().setMinHeight(0);
                    Main.getLevelSelectPane().setMaxHeight(0);
                }
                Platform.runLater(() -> {
                    tab.getTabPane().getTabs().remove(tab);
                    Main.resumeLevelClosing();
                });
            } else if (buttonType.equals(ButtonType.CANCEL)) {
                level.setEditingStatus(level.getEditingStatus(), level == Main.getLevel());
            }
        });
    }

    public static void askForLevelName(String purpose, double version) {
        Label newLevelNameText = new Label("New level name (directory name):");
        TextField enterNameHere = new TextField();
        Label typeItCorrectly = new Label("(Letters, Numbers and \"_\" only)");
        Button okButton = new Button("OK");
        Button cancelButton = new Button("Cancel");

        newLevelNameText.setStyle("-fx-font-size: 11");
        typeItCorrectly.setStyle("-fx-font-size: 11");

        enterNameHere.setMinHeight(20);
        enterNameHere.setPrefSize(291, 20);
        okButton.setMinHeight(21);
        okButton.setPrefSize(73, 21);
        cancelButton.setMinHeight(21);
        cancelButton.setPrefSize(73, 21);

        HBox buttonBox = new HBox(okButton, cancelButton);
        buttonBox.setSpacing(8);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);

        VBox centralVBox = new VBox(newLevelNameText, enterNameHere, typeItCorrectly, buttonBox);

        centralVBox.setPadding(new Insets(11, 11, 11, 11));

        centralVBox.setSpacing(5);

        Scene scene = new Scene(centralVBox);

        Stage stage = new Stage();
        stage.setScene(scene);

        stage.setWidth(329);
        stage.setHeight(151);

        stage.show();
        stage.setAlwaysOnTop(true);

        switch (purpose) {
            case "new" -> {
                stage.setTitle("Create New Level");
                okButton.setOnAction(event -> {
                    Main.newLevel(enterNameHere.getText(), version);
                    stage.close();
                });
            }
            case "clone" -> {
                stage.setTitle("Clone Level");
                okButton.setOnAction(event -> {
                    Main.cloneLevel(enterNameHere.getText(), version);
                    stage.close();
                });
            }
            case "changename" -> stage.setTitle("Change Level Name");
        }
        cancelButton.setOnAction(actionEvent -> stage.close());

        System.out.println(stage.getWidth());
    }

    public static void missingWOG() {
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
            if (Main.changeWorldOfGooDirectory(1.3)) {
                FileManager.setHasOldWOG(true);
                Main.startWithWorldOfGooVersion();
                stage.close();
            }
        });

        selectNewButton.setOnAction(actionEvent -> {
            if (Main.changeWorldOfGooDirectory(1.5)) {
                FileManager.setHasNewWOG(true);
                Main.startWithWorldOfGooVersion();
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
