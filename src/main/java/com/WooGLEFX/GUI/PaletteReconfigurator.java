package com.WooGLEFX.GUI;

import com.WooGLEFX.Engine.FXCreator;
import com.WooGLEFX.Engine.Main;
import com.WooGLEFX.File.FileManager;
import com.WooGLEFX.EditorObjects._Ball;
import com.WooGLEFX.File.GlobalResourceManager;
import com.WooGLEFX.Structures.EditorObject;
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

    private HBox getBallHBox(String ballNameString, double version) {
        HBox ballHBox = new HBox();
        CheckBox enableBox = new CheckBox();
        Label ballName = new Label(ballNameString);
        ballName.setId(String.valueOf(version));

        int i = 0;
        for (String name2 : FileManager.getPaletteBalls()) {
            if (name2.equals(ballNameString) && version == FileManager.getPaletteVersions().get(i)) {
                enableBox.setSelected(true);
            }
            i++;
        }

        ballHBox.getChildren().addAll(enableBox, ballName);
        return ballHBox;
    }

    @Override
    public void start(Stage stage) throws ParserConfigurationException, SAXException, IOException {
        VBox vBox = new VBox();

        stage.setTitle("Configure Goo Ball Palette");

        if (FileManager.isHasOldWOG()) {
            File[] balls = new File(FileManager.getOldWOGdir() + "\\res\\balls").listFiles();
            if (balls != null) {
                for (File ballFile : balls) {
                    vBox.getChildren().add(getBallHBox(ballFile.getName(), 1.3));
                }
            }
        }
        if (FileManager.isHasNewWOG()) {
            File[] balls = new File(FileManager.getNewWOGdir() + "\\res\\balls").listFiles();
            if (balls != null) {
                for (File ballFile : balls) {
                    vBox.getChildren().add(getBallHBox(ballFile.getName(), 1.5));
                }
            }
        }

        ScrollPane pane = new ScrollPane(vBox);

        Button applyButton = new Button("Apply");
        Button cancelButton = new Button("Cancel");

        applyButton.setOnAction(actionEvent -> {
            ArrayList<String> paletteBalls = new ArrayList<>();
            ArrayList<Double> paletteVersions = new ArrayList<>();

            for (Node ballHBox : vBox.getChildren()) {
                Label label = (Label)((HBox)ballHBox).getChildren().get(1);
                CheckBox checkBox = (CheckBox)((HBox)ballHBox).getChildren().get(0);
                if (checkBox.isSelected()) {
                    paletteBalls.add(label.getText());
                    paletteVersions.add(Double.parseDouble(label.getId()));

                    String ballName = label.getText();
                    double ballVersion = Double.parseDouble(label.getId());

                    boolean alreadyHasBall = false;
                    for (_Ball ball : Main.getImportedBalls()) {
                        if (ball.getObjects().get(0).getAttribute("name").equals(ballName)) {
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
                            Main.getImportedBalls().add(ball);
                        }
                    }
                }
            }

            FileManager.setPaletteBalls(paletteBalls);
            FileManager.setPaletteVersions(paletteVersions);

            FXCreator.getOldGooballsToolbar().getItems().clear();
            FXCreator.getNewGooballsToolbar().getItems().clear();
            FXCreator.addBallsTo();

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

        stage.setWidth(240);
        stage.setHeight(540);

        stage.setScene(scene);
        stage.show();
    }
}
