package com.woogleFX.engine.gui;

import java.io.File;

import com.woogleFX.file.BaseGameResources;
import com.woogleFX.file.FileManager;

import com.woogleFX.functions.LevelLoader;
import com.woogleFX.structures.GameVersion;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class LevelSelector extends Application {

    private final GameVersion version;

    public LevelSelector(GameVersion version) {
        this.version = version;
    }

    public static String selected = "";
    public static Label selectedLabel = null;

    @Override
    public void start(Stage stage) {

        Pane all = new Pane();

        VBox allLevelsBox = new VBox();

        File levelDir = new File(FileManager.getGameDir(version) + "\\res\\levels");

        ScrollPane realPane = new ScrollPane(allLevelsBox);

        realPane.setPrefWidth(376);
        realPane.setPrefHeight(286);

        Label selectLevelToEdit = new Label("Select level to edit:");

        ComboBox<String> filter = new ComboBox<>();

        filter.getItems().addAll("Original Levels Only", "Customizable Levels Only", "All Levels");

        filter.setLayoutX(204);
        filter.setLayoutY(6);
        filter.setPrefWidth(186);

        filter.getSelectionModel().selectedItemProperty().addListener((observableValue, s, t1) -> {

            allLevelsBox.getChildren().clear();

            File[] levels = levelDir.listFiles();
            if (levels == null) return;

            for (File levelFile : levels) {

                boolean ok = false;
                switch (t1) {
                    case "Original Levels Only" -> ok = BaseGameResources.LEVELS.stream().anyMatch(
                            e -> e.equals(levelFile.getName()));
                    case "Customizable Levels Only" -> ok = BaseGameResources.LEVELS.stream().noneMatch(
                            e -> e.equals(levelFile.getName()));
                    case "All Levels" -> ok = true;
                }
                if (!ok) return;

                Label label = new Label(levelFile.getName());
                label.setPrefWidth(376);
                label.setOnMouseClicked(mouseEvent -> {

                    if (selectedLabel == label) {
                        LevelLoader.openLevel(selected, version);
                        stage.close();
                        selectedLabel = null;
                        selected = "";
                        return;
                    }

                    if (selectedLabel != null) selectedLabel.setStyle("");

                    label.setStyle("-fx-background-color: #C0E0FFFF");
                    selectedLabel = label;
                    selected = label.getText();

                });
                allLevelsBox.getChildren().add(label);

            }
        });

        filter.getSelectionModel().select(2);

        Button openButton = new Button("Open");
        Button cancelButton = new Button("Cancel");

        openButton.setLayoutX(234);
        openButton.setLayoutY(332);
        cancelButton.setLayoutX(315);
        cancelButton.setLayoutY(332);

        openButton.setOnAction(actionEvent -> {
            if (selected.isEmpty()) return;
            LevelLoader.openLevel(selected, version);
            stage.close();
            selectedLabel = null;
            selected = "";
        });

        cancelButton.setOnAction(actionEvent -> stage.close());

        all.getChildren().addAll(realPane, selectLevelToEdit, filter, openButton, cancelButton);

        realPane.setLayoutX(12);
        realPane.setLayoutY(38);

        selectLevelToEdit.setLayoutX(12);
        selectLevelToEdit.setLayoutY(12);

        stage.setScene(new Scene(all, 400, 375));
        stage.setResizable(false);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.show();

    }
}
