package com.woogleFX.engine.gui;

import com.woogleFX.editorObjects.EditorObject;
import com.woogleFX.editorObjects.attributes.EditorAttribute;
import com.woogleFX.file.FileManager;
import com.woogleFX.file.fileImport.ObjectGOOParser;
import com.woogleFX.assets.GameVersion;
import com.worldOfGoo2.environments.Environment;
import com.worldOfGoo2.util.EnvironmentHelper;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.cell.TextFieldTreeTableCell;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;

public class BackgroundViewer extends Application {

    private final GameVersion version;
    private final TextFieldTreeTableCell<EditorAttribute[], String> field;

    public BackgroundViewer(GameVersion version) {
        this.version = version;
        this.field = null;
    }

    public BackgroundViewer(GameVersion version, TextFieldTreeTableCell<EditorAttribute[], String> field) {
        this.version = version;
        this.field = field;
    }

    public static String selected = "";
    public static Label selectedLabel = null;

    @Override
    public void start(Stage stage) {

        Pane all = new Pane();

        VBox allBgsBox = new VBox();

        ArrayList<Environment> environments = new ArrayList<>();
        for (File child : new File(FileManager.getGameDir(version) + "/res/environments").listFiles()) {
            if (!child.getPath().endsWith(".wog2")) {
                continue;
            }
            EditorObject item;
            try {
                item = ObjectGOOParser.read(Environment.class, Files.readString(child.toPath()), "environments");
            } catch (IOException e) {
                continue;
            }
            if (item instanceof Environment environment) {
                environments.add(environment);
            }

        }

        ScrollPane realPane = new ScrollPane(allBgsBox);

        realPane.setPrefWidth(676);
        realPane.setPrefHeight(486);

        Label selectLevelToEdit = new Label("Select an environment:");

        ComboBox<String> filter = new ComboBox<>();

        filter.getItems().addAll("Original Environments Only", "Customizable Environments Only", "All Environments");

        filter.setLayoutX(204);
        filter.setLayoutY(6);
        filter.setPrefWidth(186);

        filter.getSelectionModel().selectedItemProperty().addListener((observableValue, s, t1) -> {

            allBgsBox.getChildren().clear();

            for (var env : environments) {

                //TODO2: BaseGame Environments

            /*
            boolean ok = false;
            switch (t1) {
                case "Original Environments Only" -> ok = BaseGameResources.LEVELS.stream().anyMatch(
                        e -> e.equals(env));
                case "Customizable Environments Only" -> ok = BaseGameResources.LEVELS.stream().noneMatch(
                        e -> e.equals(env));
                case "All Environments" -> ok = true;
            }
            if (!ok) return;
             */

                Label label = new Label(env.getAttribute("name").stringValue() + "\n" + env.getAttribute("id").stringValue());
                label.setPrefWidth(576);
                label.setOnMouseClicked(mouseEvent -> {

                    if (selectedLabel != null) selectedLabel.setStyle("");

                    label.setStyle("-fx-background-color: #C0E0FFFF");
                    selectedLabel = label;
                    selected = label.getText();

                });
                ImageView view = new ImageView(EnvironmentHelper.singleFromEnvironment(env));
                label.setGraphic(view);
                allBgsBox.getChildren().add(label);

            }
        });

        filter.getSelectionModel().select(2);

        Button openButton = new Button("Select");
        Button cancelButton = new Button("Cancel");

        openButton.setLayoutX(434);
        openButton.setLayoutY(532);
        cancelButton.setLayoutX(515);
        cancelButton.setLayoutY(532);

        openButton.setOnAction(actionEvent -> {
            if (selected.isEmpty() || field == null) return;
            LoadEnvironment(selected);
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

        stage.setScene(new Scene(all, 700, 575));
        stage.setResizable(false);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.show();

        if (field == null) {
            return;
        }
        double i = 0;
        for(var part : allBgsBox.getChildren()) if (part instanceof Label label) {
            if (label.getText().contains(field.getItem())) {
                label.fireEvent(new MouseEvent(MouseEvent.MOUSE_CLICKED, 0,
                        0, 0, 0, MouseButton.PRIMARY, 1, true, true, true, true,
                        true, true, true, true, true, true, null));
                realPane.setVvalue(i / allBgsBox.getChildren().size());
                break;
            }
            i++;
        }

    }

    public void LoadEnvironment(String selected) {
        if (this.field == null) return;
        this.field.commitEdit(selected.split("\n")[1]);
    }
}
