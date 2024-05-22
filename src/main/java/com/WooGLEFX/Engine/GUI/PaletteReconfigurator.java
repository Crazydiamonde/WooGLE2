package com.WooGLEFX.Engine.GUI;

import com.WooGLEFX.Engine.FX.FXEditorButtons;
import com.WooGLEFX.File.FileManager;
import com.WooGLEFX.EditorObjects._Ball;
import com.WooGLEFX.File.ResourceManagers.GlobalResourceManager;
import com.WooGLEFX.File.ResourceManagers.BallManager;
import com.WooGLEFX.Functions.PaletteManager;
import com.WooGLEFX.EditorObjects.EditorObject;
import com.WooGLEFX.Structures.GameVersion;
import javafx.application.Application;
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
        ballName.setId(String.valueOf(version));

        int i = 0;
        for (String name2 : PaletteManager.getPaletteBalls()) {
            if (name2.equals(ballNameString) && version == PaletteManager.getPaletteVersions().get(i)) {
                enableBox.setSelected(true);
            }
            i++;
        }

        ballHBox.getChildren().addAll(enableBox, ballName);
        return ballHBox;
    }

    @Override
    public void start(Stage stage) {
        VBox oldVBox = new VBox();
        VBox newVBox = new VBox();

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

        Button applyButton = new Button("Apply");
        Button cancelButton = new Button("Cancel");

        applyButton.setOnAction(actionEvent -> {
            ArrayList<String> paletteBalls = new ArrayList<>();
            ArrayList<GameVersion> paletteVersions = new ArrayList<>();

            ArrayList<Node> nodeList = new ArrayList<>();

            nodeList.addAll(oldVBox.getChildren());
            nodeList.addAll(newVBox.getChildren());

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
                            throw new RuntimeException(e);
                        }

                        for (EditorObject resrc : FileManager.commonBallResrcData) {
                            GlobalResourceManager.addResource(resrc, ballVersion);
                        }

                        if (ball != null) {
                            ball.makeImages(ballVersion);
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
                throw new RuntimeException(e);
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
