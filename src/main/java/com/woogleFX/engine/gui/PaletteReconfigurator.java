package com.woogleFX.engine.gui;

import com.woogleFX.engine.fx.FXEditorButtons;
import com.woogleFX.file.FileManager;
import com.woogleFX.gameData.ball.PaletteManager;
import com.woogleFX.gameData.level.GameVersion;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class PaletteReconfigurator extends Application {

    private HBox getBallHBox(String ballNameString, GameVersion version) {
        HBox ballHBox = new HBox();
        CheckBox enableBox = new CheckBox();
        Label ballName = new Label(ballNameString);
        ballName.setId(version.toString());

        int i = 0;
        for (String name2 : PaletteManager.getPaletteBalls()) {
            if (name2.equals(ballNameString) && version == PaletteManager.getPaletteVersions().get(i)) {
                enableBox.setSelected(true);
            }
            i++;
        }

        ballHBox.getChildren().addAll(enableBox, ballName);

        ballHBox.setSpacing(5);

        return ballHBox;
    }

    @Override
    public void start(Stage stage) {
        VBox oldVBox = new VBox();
        VBox newVBox = new VBox();

        oldVBox.setSpacing(10);
        newVBox.setSpacing(10);

        stage.setTitle("Configure Goo Ball Palette");

        for (GameVersion version : GameVersion.ALL) {

            VBox vBox = (version == GameVersion.OLD) ? oldVBox : newVBox;

            String dir = FileManager.getGameDir(version);
            if (dir.isEmpty()) continue;
            File[] balls = new File(dir + "\\res\\balls").listFiles();
            if (balls == null) continue;

            Label label = new Label("Version 1.3 Goo Balls");
            label.setStyle("-fx-font-weight: bold");
            vBox.getChildren().add(label);

            for (File ballFile : balls) vBox.getChildren().add(getBallHBox(ballFile.getName(), version));

        }

        ScrollPane pane = new ScrollPane(new HBox(oldVBox, newVBox));

        pane.setPadding(new Insets(20, 20, 20, 20));

        Button applyButton = new Button("Apply");
        Button cancelButton = new Button("Cancel");

        applyButton.setOnAction(actionEvent -> {

            ArrayList<Node> nodeList = new ArrayList<>();

            nodeList.addAll(oldVBox.getChildren());
            nodeList.addAll(newVBox.getChildren());

            PaletteManager.clear();

            for (Node ballHBox : nodeList) {
                if (!(ballHBox instanceof HBox)) continue;
                Label label = (Label)((HBox)ballHBox).getChildren().get(1);
                CheckBox checkBox = (CheckBox)((HBox)ballHBox).getChildren().get(0);
                if (checkBox.isSelected()) {
                    String ballName = label.getText();
                    GameVersion ballVersion = label.getId().equals("1.3") ? GameVersion.OLD : GameVersion.NEW;

                    PaletteManager.addPaletteBall(ballName);
                    PaletteManager.addPaletteVersion(ballVersion);

                }
            }

            FXEditorButtons.getOldGooballsToolbar().getItems().clear();
            FXEditorButtons.getNewGooballsToolbar().getItems().clear();
            FXEditorButtons.addBallsTo();

            try {
                FileManager.saveProperties();
            } catch (IOException e) {
                Alarms.errorMessage(e);
            }

            stage.close();
        });

        cancelButton.setOnAction(actionEvent -> stage.close());

        HBox buttonBox = new HBox(applyButton, cancelButton);

        VBox allBox = new VBox(pane, buttonBox);

        Scene scene = new Scene(allBox);

        stage.setWidth(480);
        stage.setHeight(540);

        stage.setScene(scene);
        stage.show();
    }
}
