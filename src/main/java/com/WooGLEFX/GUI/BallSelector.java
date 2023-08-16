package com.WooGLEFX.GUI;

import com.WooGLEFX.Engine.Main;
import com.WooGLEFX.File.FileManager;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.File;

public class BallSelector extends Application {

    private final double oldVersion;
    private final double newVersion;

    public BallSelector(double oldVersion, double newVersion) {
        this.oldVersion = oldVersion;
        this.newVersion = newVersion;
    }

    public static String selected = "";
    public static Label selectedLabel = null;

    public static final String[] originalBalls = new String[] {
            "Anchor",
            "AnchorFriendly",
            "AnchorSticky",
            "AnchorStickyInvisible",
            "balloon",
            "Beauty",
            "BeautyProduct",
            "BeautyProductEye",
            "Bit",
            "BlockHead",
            "BombMini",
            "BombShaft",
            "BombSticky",
            "Bone",
            "common",
            "common_albino",
            "common_black",
            "Distant",
            "Drained",
            "DrainedIsh",
            "EvilEye",
            "Fish",
            "Fuse",
            "generic",
            "GooProduct",
            "Hex",
            "IconWindowRect",
            "IconWindowSquare",
            "Ivy",
            "Peddler",
            "Pilot",
            "Pixel",
            "PixelProduct",
            "Pokey",
            "RectHead",
            "SimCommon",
            "Spam",
            "TimeBug",
            "Ugly",
            "UglyProduct",
            "UndeletePill",
            "UndeletePillFizz",
            "UtilAttachUnwalkable",
            "UtilAttachWalkable",
            "UtilChapter2",
            "UtilGooGlobber",
            "UtilGooGlobberMom",
            "UtilGooGlobberMomQuiet",
            "UtilNoAttachUnwalkable",
            "UtilNoAttachWalkableSmall",
            "UtilProductLauncherScreamer",
            "water",
            "WindowRect",
            "WindowSquare",
            "ZBomb",
            "ZBombMom"
    };

    public static File ballDir = null;

    @Override
    public void start(Stage stage) {

        Pane all = new Pane();

        VBox allBallsBox = new VBox();

        if (oldVersion == 1.3){
            ballDir = new File(FileManager.getOldWOGdir() + "\\res\\balls");
        } else if (oldVersion == 1.5){
            ballDir = new File(FileManager.getNewWOGdir() + "\\res\\balls");
        }

        ScrollPane realPane = new ScrollPane(allBallsBox);

        realPane.setPrefWidth(376);
        realPane.setPrefHeight(286);

        Label selectBallToSave = new Label("Select ball to save:");

        if (ballDir != null) {
            File[] balls = ballDir.listFiles();
            if (balls != null) {
                for (File ballFile : balls) {
                    boolean ok = true;
                    for (String ballName : originalBalls) {
                        if (ballName.equals(ballFile.getName())) {
                            ok = false;
                        }
                    }
                    if (ok) {
                        Label label = new Label(ballFile.getName());
                        label.setPrefWidth(376);
                        label.setOnMouseClicked(mouseEvent -> {
                            if (selectedLabel == label) {
                                Main.saveBallInVersion(selected, oldVersion, newVersion);
                                stage.close();
                            } else if (selectedLabel != null) {
                                selectedLabel.setStyle("");
                            }
                            label.setStyle("-fx-background-color: #C0E0FFFF");
                            selectedLabel = label;
                            selected = label.getText();
                        });
                        allBallsBox.getChildren().add(label);
                    }
                }
            }
        }

        Button openButton = new Button("Save");
        Button cancelButton = new Button("Cancel");

        openButton.setLayoutX(234);
        openButton.setLayoutY(332);
        cancelButton.setLayoutX(315);
        cancelButton.setLayoutY(332);

        openButton.setOnAction(actionEvent -> {
            Main.saveBallInVersion(selected, oldVersion, newVersion);
            stage.close();
        });

        cancelButton.setOnAction(actionEvent -> stage.close());

        all.getChildren().addAll(realPane, selectBallToSave, openButton, cancelButton);

        realPane.setLayoutX(12);
        realPane.setLayoutY(38);

        selectBallToSave.setLayoutX(12);
        selectBallToSave.setLayoutY(12);

        stage.setScene(new Scene(all, 400, 375));
        stage.setResizable(false);
        stage.show();

    }
}
