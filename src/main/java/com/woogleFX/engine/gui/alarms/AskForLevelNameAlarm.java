package com.woogleFX.engine.gui.alarms;

import com.woogleFX.engine.AssetManager;
import com.woogleFX.engine.fx.assetSelectPane.FXAssetSelectPane;
import com.woogleFX.file.FileManager;
import com.woogleFX.assets.GameVersion;
import com.woogleFX.assets.AssetLoader;
import com.woogleFX.assets.AssetUpdater;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class AskForLevelNameAlarm {

    public static void show(String purpose, GameVersion version) {

        String titleText;
        String confirmText;

        switch (purpose) {
            case "new" -> {
                titleText = "New level name (directory name):";
                confirmText = "Create";
            }
            case "clone" -> {
                titleText = "New level name (directory name):";
                confirmText = "Clone";
            }
            case "changeName" -> {
                titleText = "New level name (directory name):";
                confirmText = "Rename";
            }
            case "delete" -> {
                titleText = "Enter level name to confirm:";
                confirmText = "Delete";
            }
            default -> {
                titleText = "";
                confirmText = "OK";
            }
        }

        Label newLevelNameText = new Label(titleText);
        TextField enterNameHere = new TextField();
        Label typeItCorrectly = new Label("(Letters, Numbers and \"_\" only)");
        // Set filter on TextField to allow for only letters, numbers and "_"
        enterNameHere.textProperty().addListener((observableValue, s, t1) -> {
            if (!t1.matches("[a-zA-Z0-9_]*")) {
                enterNameHere.setText(t1.replaceAll("[^a-zA-Z0-9_]", ""));
            }
        });

        Button okButton = new Button(confirmText);
        // Set okButton to be disabled if the TextField is empty
        okButton.disableProperty().bind(enterNameHere.textProperty().isEmpty());
        // Set okButton to be pressed if the user presses enter
        enterNameHere.setOnAction(actionEvent -> okButton.fire());
        Button cancelButton = new Button("Cancel");

        newLevelNameText.setStyle("-fx-font-size: 11");
        typeItCorrectly.setStyle("-fx-font-size: 11");

        Label errorText = new Label("");
        errorText.setStyle("-fx-font-size: 11");

        enterNameHere.setMinHeight(25);
        enterNameHere.setPrefSize(291, 25);
        okButton.setMinHeight(25);
        okButton.setPrefSize(73, 25);
        cancelButton.setMinHeight(25);
        cancelButton.setPrefSize(73, 25);

        HBox buttonBox = new HBox(okButton, cancelButton);
        buttonBox.setSpacing(8);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);

        VBox centralVBox = new VBox(newLevelNameText, enterNameHere, errorText, typeItCorrectly, buttonBox);

        centralVBox.setPadding(new Insets(11, 11, 11, 11));

        centralVBox.setSpacing(5);

        Scene scene = new Scene(centralVBox);

        Stage stage = new Stage();
        stage.setScene(scene);

        stage.setWidth(329);
        stage.setHeight(161);

        stage.initModality(Modality.APPLICATION_MODAL);
        stage.show();
        stage.setAlwaysOnTop(true);
        stage.setResizable(false);

        switch (purpose) {
            case "new" -> {
                stage.setTitle("Create New Level");
                okButton.setOnAction(event -> {
                    // TODO:
                    //AssetLoader.newAsset(new LevelSelector(version), enterNameHere.getText());
                    stage.close();
                });
            }
            case "clone" -> {
                stage.setTitle("Clone Level");
                okButton.setOnAction(event -> {
                    try {
                        AssetLoader.cloneLevel(enterNameHere.getText());
                    } catch (IOException e) {
                        ErrorAlarm.show(e);
                    }
                    stage.close();
                });
            }
            case "changeName" -> {
                stage.setTitle("Change Level Name");
                okButton.setOnAction(event -> {
                    File[] allLevels = new File(FileManager.getGameDir(version) + "/res/levels").listFiles();
                    if (allLevels == null) return;
                    for (File levelFile : allLevels) {
                        if (levelFile.getName().equals(enterNameHere.getText())) {
                            errorText.setText("Name is already taken.");

                            new Task<Void>() {
                                @Override
                                protected Void call() {

                                    try {
                                        wait(1000);
                                        errorText.setText("");
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }


                                    return null;
                                }
                            }.run();

                            return;
                        }
                    }
                    try {
                        AssetUpdater.renameLevel(AssetManager.getAsset(), enterNameHere.getText());
                    } catch (IOException e) {
                        ErrorAlarm.show(e);
                    }
                    stage.close();
                });
            }
            case "delete" -> {
                stage.setTitle("Delete Level");
                okButton.setOnAction(event -> {
                    if (enterNameHere.getText().equals(AssetManager.getAsset().getName())) {
                        AssetManager.getAsset().delete();
                        FXAssetSelectPane.closeCurrentTab();
                        stage.close();
                    }
                });
            }
        }
        cancelButton.setOnAction(actionEvent -> stage.close());
    }

}
