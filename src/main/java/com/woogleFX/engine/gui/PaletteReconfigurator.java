package com.woogleFX.engine.gui;

import com.woogleFX.engine.fx.FXEditorButtons;
import com.woogleFX.file.FileManager;
import com.woogleFX.editorObjects._Ball;
import com.woogleFX.file.resourceManagers.GlobalResourceManager;
import com.woogleFX.file.resourceManagers.BallManager;
import com.woogleFX.functions.PaletteManager;
import com.woogleFX.editorObjects.EditorObject;
import com.woogleFX.structures.GameVersion;
import com.worldOfGoo.resrc.Font;
import com.worldOfGoo.resrc.ResrcImage;
import com.worldOfGoo.resrc.Sound;
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
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
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

        if (FileManager.hasOldWOG()) {
            Label oldLabel = new Label("Version 1.3 Goo Balls");
            oldLabel.setStyle("-fx-font-weight: bold");
            oldVBox.getChildren().add(oldLabel);
            File[] balls = new File(FileManager.getOldWOGdir() + "\\res\\balls").listFiles();
            if (balls != null) {
                for (File ballFile : balls) {
                    oldVBox.getChildren().add(getBallHBox(ballFile.getName(), GameVersion.OLD));
                }
            }
        }
        if (FileManager.hasNewWOG()) {
            Label label = new Label("Version 1.5 Goo Balls");
            label.setStyle("-fx-font-weight: bold");
            newVBox.getChildren().add(label);
            File[] balls = new File(FileManager.getNewWOGdir() + "\\res\\balls").listFiles();
            if (balls != null) {
                for (File ballFile : balls) {
                    newVBox.getChildren().add(getBallHBox(ballFile.getName(), GameVersion.NEW));
                }
            }
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

                    PaletteManager.addPaletteBall(label.getText());
                    PaletteManager.addPaletteVersion(ballVersion);

                    boolean alreadyHasBall = false;
                    for (_Ball ball : BallManager.getImportedBalls()) {
                        if (ball.getObjects().get(0).getAttribute("name").stringValue().equals(ballName)) {
                            alreadyHasBall = true;
                            break;
                        }
                    }

                    if (!alreadyHasBall) {
                        _Ball ball;
                        try {
                            ball = FileManager.openBall(ballName, ballVersion);
                        } catch (ParserConfigurationException | SAXException | IOException e) {
                            ball = null;
                        }

                        if (ball != null) {
                            ball.setVersion(ballVersion);
                            BallManager.getImportedBalls().add(ball);
                        }
                    }
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
