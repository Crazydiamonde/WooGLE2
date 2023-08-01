package com.WooGLEFX.GUI;

import com.WooGLEFX.Engine.Main;
import com.WooGLEFX.File.FileManager;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.File;

public class LevelSelector extends Application {

    private double version;

    public LevelSelector(double version) {
        this.version = version;
    }

    public static String selected = "";
    public static Label selectedLabel = null;

    public static final String[] originalLevels = new String[] {
            "AB3",
            "BeautyAndTheTentacle",
            "BeautySchool",
            "BlusteryDay",
            "BulletinBoardSystem",
            "BurningMan",
            "Chain",
            "Deliverance",
            "Drool",
            "EconomicDivide",
            "FistyReachesOut",
            "FlyAwayLittleOnes",
            "FlyingMachine",
            "GeneticSortingMachine",
            "GoingUp",
            "GracefulFailure",
            "GrapeVineVirus",
            "GraphicProcessingUnit",
            "HangLow",
            "HelloWorld",
            "HTInnovationCommittee",
            "ImmigrationNaturalizationUnit",
            "ImpaleSticky",
            "IncinerationDestination",
            "InfestyTheWorm",
            "island1",
            "island2",
            "island3",
            "island4",
            "island5",
            "IslandUi",
            "IvyTower",
            "LeapHole",
            "MapWorldView",
            "MightyBlimp",
            "MistysLongBonyRoad",
            "MOM",
            "ObservatoryObservationStation",
            "OdeToBridgeBuilder",
            "ProductLauncher",
            "RedCarpet",
            "RegurgitationPumpingStation",
            "RoadBlocks",
            "Route99",
            "SecondHandSmoke",
            "SuperFuseChallengeTime",
            "TestLevel",
            "TheServer",
            "ThirdWheel",
            "TowerOfGoo",
            "Tumbler",
            "UpperShaft",
            "VolcanicPercolatorDaySpa",
            "WaterLock",
            "WeatherVane",
            "Whistler",
            "wogc",
            "wogc3d",
            "wogcd",
            "YouHaveToExplodeTheHead"
    };

    public static File levelDir = null;

    @Override
    public void start(Stage stage) throws Exception {

        Pane all = new Pane();

        VBox allLevelsBox = new VBox();

        if (version == 1.3){
            levelDir = new File(FileManager.getOldWOGdir() + "\\res\\levels");
        } else if (version == 1.5){
            levelDir = new File(FileManager.getNewWOGdir() + "\\res\\levels");
        }

        ScrollPane realPane = new ScrollPane(allLevelsBox);

        realPane.setPrefWidth(376);
        realPane.setPrefHeight(286);

        Label selectLevelToEdit = new Label("Select level to edit:");

        ComboBox<String> filter = new ComboBox<>();

        filter.getItems().addAll("Original Levels Only", "Customizable Levels Only", "All Levels");

        filter.setLayoutX(204);
        filter.setLayoutY(6);
        filter.setPrefWidth(186);

        filter.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String s, String t1) {

                allLevelsBox.getChildren().clear();

                if (levelDir != null) {
                    for (File levelFile : levelDir.listFiles()) {
                        boolean ok = false;
                        switch(t1) {
                            case "Original Levels Only":
                                for (String levelName : originalLevels) {
                                    if (levelName.equals(levelFile.getName())) {
                                        ok = true;
                                    }
                                }
                                break;
                            case "Customizable Levels Only":
                                ok = true;
                                for (String levelName : originalLevels) {
                                    if (levelName.equals(levelFile.getName())) {
                                        ok = false;
                                    }
                                }
                                break;
                            case "All Levels":
                                ok = true;
                                break;
                        }
                        if (ok) {
                            Label label = new Label(levelFile.getName());
                            label.setPrefWidth(376);
                            label.setOnMouseClicked(mouseEvent -> {
                                if (selectedLabel == label) {
                                    Main.openLevel(selected, version);
                                    stage.close();
                                } else if (selectedLabel != null) {
                                    selectedLabel.setStyle("");
                                }
                                label.setStyle("-fx-background-color: #C0E0FFFF");
                                selectedLabel = label;
                                selected = label.getText();
                            });
                            allLevelsBox.getChildren().add(label);
                        }
                    }
                }
            }
        });

        filter.getSelectionModel().select(2);

        Button openButton = new Button("Open");
        Button cancelButton = new Button("Cancel");

        openButton.setLayoutX(234);
        openButton.setLayoutY(332);
        cancelButton.setLayoutX(315);
        cancelButton.setLayoutY(332);

        openButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                Main.openLevel(selected, version);
                stage.close();
            }
        });

        cancelButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                stage.close();
            }
        });

        all.getChildren().addAll(realPane, selectLevelToEdit, filter, openButton, cancelButton);

        realPane.setLayoutX(12);
        realPane.setLayoutY(38);

        selectLevelToEdit.setLayoutX(12);
        selectLevelToEdit.setLayoutY(12);

        stage.setScene(new Scene(all, 400, 375));
        stage.setResizable(false);
        stage.show();

    }
}
