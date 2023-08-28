package com.WooGLEFX.GUI;

import com.WooGLEFX.Engine.Main;
import com.WooGLEFX.File.FileManager;
import com.WooGLEFX.Structures.EditorObject;
import com.WooGLEFX.Structures.WorldLevel;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

public class Alarms {

    public static void errorMessage(Exception error) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setResizable(true);
        alert.setHeaderText("Error");
        alert.setContentText(error.getClass().getSimpleName() + ": " + error.getMessage());
        alert.show();
    }

    public static void loadingResourcesError(String error) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setResizable(true);
        alert.setHeaderText("Level resources could not be loaded");
        alert.setContentText(error);
        alert.show();
        if (alert.getHeight() > 1080) {
            alert.setHeight(1080);
        }
        alert.setY(0);
        System.out.println(alert.getHeight());
    }

    public static void loadingInitialResourcesError(String error) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setResizable(true);
        alert.setHeaderText("Resources could not be loaded");
        alert.setContentText(error);

        alert.initOwner(Main.getStage());

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

    public static void confirmCleanResourcesMessage(ArrayList<EditorObject> resourceNames) {

        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setHeaderText("Clean Level Resources");
        StringBuilder content = new StringBuilder("Are you sure you want to remove unused resources?\nThe following resources will be cleaned:");

        for (EditorObject resourceName : resourceNames) {
            content.append("\n").append(resourceName.getAttribute("id"));
        }

        alert.setContentText(content.toString());

        alert.showAndWait().ifPresent(buttonType -> {
            if (buttonType.equals(ButtonType.OK)) {
                Main.confirmedCleanLevelResources(resourceNames);
            }
        });
    }

    public static void askForLevelName(String purpose, double version) {

        String titleText = "";

        String confirmText = "OK";
        switch (purpose) {
            case "new" -> {
                confirmText = "Create";
                titleText = "New level name (directory name):";
            }
            case "clone" -> {
                confirmText = "Clone";
                titleText = "New level name (directory name):";
            }
            case "changename" -> {
                confirmText = "Rename";
                titleText = "New level name (directory name):";
            }
            case "delete" -> {
                confirmText = "Delete";
                titleText = "Enter level name to confirm:";
            }
        }

        Label newLevelNameText = new Label(titleText);
        TextField enterNameHere = new TextField();
        Label typeItCorrectly = new Label("(Letters, Numbers and \"_\" only)");

        Button okButton = new Button(confirmText);
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
            case "changename" -> {
                stage.setTitle("Change Level Name");
                okButton.setOnAction(event -> {
                    String start = version == 1.3 ? FileManager.getOldWOGdir() : FileManager.getNewWOGdir();
                    for (File levelFile : new File(start + "\\res\\levels").listFiles()) {
                        if (levelFile.getName().equals(enterNameHere.getText())) {
                            //TODO Display a message saying it's already taken
                            return;
                        }
                    }
                    Main.renameLevel(enterNameHere.getText());
                    stage.close();
                });
            }
            case "delete" -> {
                stage.setTitle("Delete Level");
                okButton.setOnAction(event -> {
                    if (enterNameHere.getText().equals(Main.getLevel().getLevelName())) {
                        Main.deleteLevelForReal();
                        stage.close();
                    }
                });
            }
        }
        cancelButton.setOnAction(actionEvent -> stage.close());
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
