package com.woogleFX.assets.wog1.level;

import com.woogleFX.assets.GameVersion;
import com.woogleFX.editorObjects.EditorObject;
import com.woogleFX.editorObjects.attributes.EditorAttribute;
import com.woogleFX.editorObjects.objectCreators.ObjectAdder;
import com.woogleFX.engine.AssetManager;
import com.woogleFX.engine.fx.FXStage;
import com.woogleFX.engine.fx.editorButtons.FXBallPaletteManager;
import com.woogleFX.engine.fx.editorButtons.FXEditorButtons;
import com.woogleFX.engine.fx.propertiesView.FXPropertiesView;
import com.woogleFX.engine.fx.propertiesView.PropertiesViewManager;
import com.woogleFX.engine.gui.PaletteReconfigurator;
import com.woogleFX.engine.undoHandling.UndoManager;
import com.woogleFX.engine.undoHandling.userActions.AttributeChangeAction;
import com.woogleFX.file.resourceManagers.LevelResourceImporter;
import com.worldOfGoo.level.Vertex;
import com.worldOfGoo.level.fire;
import com.worldOfGoo.level.pipe;
import com.worldOfGoo.level.signpost;
import com.worldOfGoo.scene.*;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WOG1LevelGUI {

    // Function bar (do things bar)
    private static final ToolBar toolbar = new ToolBar();

    // Goo Ball palette bar
    private static final ToolBar ballsToolbar = new ToolBar();

    private static final FXEditorButtons.EditorButton buttonAddObject_Rectangle = new FXEditorButtons.EditorButton() {
        @Override
        public void updateDisabled() {
            setDisable(false);
        }
    };
    private static final FXEditorButtons.EditorButton buttonAddObject_Circle = new FXEditorButtons.EditorButton() {
        @Override
        public void updateDisabled() {
            setDisable(false);
        }
    };
    private static final FXEditorButtons.EditorButton buttonAddObject_SceneLayer = new FXEditorButtons.EditorButton() {
        @Override
        public void updateDisabled() {
            setDisable(false);
        }
    };
    private static final FXEditorButtons.EditorButton buttonAddObject_Compositegeom = new FXEditorButtons.EditorButton() {
        @Override
        public void updateDisabled() {
            setDisable(false);
        }
    };
    private static final FXEditorButtons.EditorButton buttonAddObject_Hinge = new FXEditorButtons.EditorButton() {
        @Override
        public void updateDisabled() {
            setDisable(false);
        }
    };
    private static final FXEditorButtons.EditorButton buttonAutoPipe = new FXEditorButtons.EditorButton() {
        @Override
        public void updateDisabled() {
            setDisable(false);
        }
    };
    private static final FXEditorButtons.EditorButton buttonAddObject_Vertex = new FXEditorButtons.EditorButton() {
        @Override
        public void updateDisabled() {
            setDisable(false);
        }
    };
    private static final FXEditorButtons.EditorButton buttonAddObject_Fire = new FXEditorButtons.EditorButton() {
        @Override
        public void updateDisabled() {
            setDisable(false);
        }
    };
    private static final FXEditorButtons.EditorButton buttonAddObject_Linearforcefield = new FXEditorButtons.EditorButton() {
        @Override
        public void updateDisabled() {
            setDisable(false);
        }
    };
    private static final FXEditorButtons.EditorButton buttonAddObject_Radialforcefield = new FXEditorButtons.EditorButton() {
        @Override
        public void updateDisabled() {
            setDisable(false);
        }
    };
    private static final FXEditorButtons.EditorButton buttonAddObject_Particles = new FXEditorButtons.EditorButton() {
        @Override
        public void updateDisabled() {
            setDisable(false);
        }
    };
    private static final FXEditorButtons.EditorButton buttonAddObject_Signpost = new FXEditorButtons.EditorButton() {
        @Override
        public void updateDisabled() {
            setDisable(false);
        }
    };
    private static final FXEditorButtons.EditorButton buttonAddObject_Label = new FXEditorButtons.EditorButton() {
        @Override
        public void updateDisabled() {
            setDisable(false);
        }
    };

    private static final FXEditorButtons.EditorButton buttonSetMusic = new FXEditorButtons.EditorButton() {
        @Override
        public void updateDisabled() {
            setDisable(false);
        }
    };
    private static final FXEditorButtons.EditorButton buttonSetLoopsound = new FXEditorButtons.EditorButton() {
        @Override
        public void updateDisabled() {
            setDisable(false);
        }
    };
    private static final FXEditorButtons.EditorButton buttonSaveAndPlay = new FXEditorButtons.EditorButton() {
        @Override
        public void updateDisabled() {
            setDisable(AssetManager.getAsset() == null || AssetManager.getAsset().getVersion() == GameVersion.VERSION_WOG1_NEW);
        }
    };


    public static void init() {

        MenuItem configurePaletteItem = new MenuItem("Configure Palette...");
        configurePaletteItem.setOnAction(actionEvent -> new PaletteReconfigurator().start(new Stage()));
        ContextMenu contextMenu = new ContextMenu(configurePaletteItem);
        ballsToolbar.setContextMenu(contextMenu);

        // TODO: make bigger icons

        buttonAddObject_Rectangle.setIcon("src/main/resources/com/worldOfGoo/scene/rectangle.png");
        buttonAddObject_Rectangle.setOnAction(e ->
                ObjectAdder.addObject(rectangle.class, ((WOG1Level)AssetManager.getAsset()).getScene(), true));
        buttonAddObject_Rectangle.setTooltip(new FXEditorButtons.DelayedTooltip("Add Rectangle"));
        toolbar.getItems().add(buttonAddObject_Rectangle);

        buttonAddObject_Circle.setIcon("src/main/resources/com/worldOfGoo/scene/circle.png");
        buttonAddObject_Circle.setOnAction(e ->
                ObjectAdder.addObject(circle.class, ((WOG1Level)AssetManager.getAsset()).getScene(), true));
        buttonAddObject_Circle.setTooltip(new FXEditorButtons.DelayedTooltip("Add Circle"));
        toolbar.getItems().add(buttonAddObject_Circle);

        buttonAddObject_SceneLayer.setIcon("src/main/resources/com/worldOfGoo/scene/SceneLayer.png");
        buttonAddObject_SceneLayer.setOnAction(e ->
                ObjectAdder.addObject(SceneLayer.class, ((WOG1Level)AssetManager.getAsset()).getScene(), true));
        buttonAddObject_SceneLayer.setTooltip(new FXEditorButtons.DelayedTooltip("Add SceneLayer"));
        toolbar.getItems().add(buttonAddObject_SceneLayer);

        buttonAddObject_Compositegeom.setIcon("src/main/resources/com/worldOfGoo/scene/compositegeom.png");
        buttonAddObject_Compositegeom.setOnAction(e ->
                ObjectAdder.addObject(compositegeom.class, ((WOG1Level)AssetManager.getAsset()).getScene(), true));
        buttonAddObject_Compositegeom.setTooltip(new FXEditorButtons.DelayedTooltip("Add Compositegeom"));
        toolbar.getItems().add(buttonAddObject_Compositegeom);

        buttonAddObject_Hinge.setIcon("src/main/resources/com/worldOfGoo/scene/hinge.png");
        buttonAddObject_Hinge.setOnAction(e ->
                ObjectAdder.addObject(hinge.class, ((WOG1Level)AssetManager.getAsset()).getScene(), true));
        buttonAddObject_Hinge.setTooltip(new FXEditorButtons.DelayedTooltip("Add Hinge"));
        toolbar.getItems().add(buttonAddObject_Hinge);

        toolbar.getItems().add(new Separator(Orientation.VERTICAL));

        buttonAutoPipe.setIcon("src/main/resources/com/worldOfGoo/scene/rectangle.png");
        buttonAutoPipe.setOnAction(e -> ObjectAdder.autoPipe((WOG1Level)AssetManager.getAsset()));
        buttonAutoPipe.setTooltip(new FXEditorButtons.DelayedTooltip("Auto Pipe"));
        toolbar.getItems().add(buttonAutoPipe);

        buttonAddObject_Vertex.setIcon("src/main/resources/com/worldOfGoo/level/Vertex.png");
        buttonAddObject_Vertex.setOnAction(e -> {
            for (EditorObject editorObject : ((WOG1Level) AssetManager.getAsset()).getLevel().getChildren()) {
                if (editorObject instanceof pipe pipe) {
                    ObjectAdder.addObject(Vertex.class, pipe, true);
                    break;
                }
            }
        });
        buttonAddObject_Vertex.setTooltip(new FXEditorButtons.DelayedTooltip("Add Vertex"));
        toolbar.getItems().add(buttonAddObject_Vertex);

        toolbar.getItems().add(new Separator(Orientation.VERTICAL));

        buttonAddObject_Fire.setIcon("src/main/resources/com/worldOfGoo/level/fire.png");
        buttonAddObject_Fire.setOnAction(e ->
                ObjectAdder.addObject(fire.class, ((WOG1Level)AssetManager.getAsset()).getLevel(), true));
        buttonAddObject_Fire.setTooltip(new FXEditorButtons.DelayedTooltip("Add Fire"));
        toolbar.getItems().add(buttonAddObject_Fire);

        buttonAddObject_Linearforcefield.setIcon("src/main/resources/com/worldOfGoo/scene/linearforcefield.png");
        buttonAddObject_Linearforcefield.setOnAction(e ->
                ObjectAdder.addObject(linearforcefield.class, ((WOG1Level)AssetManager.getAsset()).getScene(), true));
        buttonAddObject_Linearforcefield.setTooltip(new FXEditorButtons.DelayedTooltip("Add Linearforcefield"));
        toolbar.getItems().add(buttonAddObject_Linearforcefield);

        buttonAddObject_Radialforcefield.setIcon("src/main/resources/com/worldOfGoo/scene/radialforcefield.png");
        buttonAddObject_Radialforcefield.setOnAction(e ->
                ObjectAdder.addObject(radialforcefield.class, ((WOG1Level)AssetManager.getAsset()).getScene(), true));
        buttonAddObject_Radialforcefield.setTooltip(new FXEditorButtons.DelayedTooltip("Add Radialforcefield"));
        toolbar.getItems().add(buttonAddObject_Radialforcefield);

        buttonAddObject_Particles.setIcon("src/main/resources/com/worldOfGoo/scene/particles.png");
        buttonAddObject_Particles.setOnAction(e ->
                ObjectAdder.addObject(particles.class, ((WOG1Level)AssetManager.getAsset()).getScene(), true));
        buttonAddObject_Particles.setTooltip(new FXEditorButtons.DelayedTooltip("Add Particles"));
        toolbar.getItems().add(buttonAddObject_Particles);

        toolbar.getItems().add(new Separator(Orientation.VERTICAL));

        buttonAddObject_Signpost.setIcon("src/main/resources/com/worldOfGoo/level/signpost.png");
        buttonAddObject_Signpost.setOnAction(e ->
                ObjectAdder.addObject(signpost.class, ((WOG1Level)AssetManager.getAsset()).getLevel(), true));
        buttonAddObject_Signpost.setTooltip(new FXEditorButtons.DelayedTooltip("Add Signpost"));
        toolbar.getItems().add(buttonAddObject_Signpost);

        buttonAddObject_Label.setIcon("src/main/resources/com/worldOfGoo/scene/label.png");
        buttonAddObject_Label.setOnAction(e ->
                ObjectAdder.addObject(label.class, ((WOG1Level)AssetManager.getAsset()).getScene(), true));
        buttonAddObject_Label.setTooltip(new FXEditorButtons.DelayedTooltip("Add Label"));
        toolbar.getItems().add(buttonAddObject_Label);

        toolbar.getItems().add(new Separator(Orientation.VERTICAL));

        buttonSetMusic.setIcon("ButtonIcons/Resources/import_music.png");
        buttonSetMusic.setOnAction(e -> LevelResourceImporter.importMusic(AssetManager.getAsset()));
        buttonSetMusic.setTooltip(new FXEditorButtons.DelayedTooltip("Set Music"));
        toolbar.getItems().add(buttonSetMusic);

        buttonSetLoopsound.setIcon("ButtonIcons/Resources/import_soundloop.png");
        buttonSetLoopsound.setOnAction(e -> LevelResourceImporter.importLoopsound(AssetManager.getAsset()));
        buttonSetLoopsound.setTooltip(new FXEditorButtons.DelayedTooltip("Set Loop Sound"));
        toolbar.getItems().add(buttonSetLoopsound);

        buttonSaveAndPlay.setIcon("ButtonIcons/Level/play.png");
        buttonSaveAndPlay.setOnAction(e -> ((WOG1Level)AssetManager.getAsset()).saveAndPlay());
        buttonSaveAndPlay.setTooltip(new FXEditorButtons.DelayedTooltip("Save and Play Level"));
        toolbar.getItems().add(buttonSaveAndPlay);

    }


    public static ArrayList<Node> getGUIElements() {
        return new ArrayList<>(List.of(ballsToolbar, toolbar));
    }


    public static void refreshBallPalette(GameVersion version) {
        ballsToolbar.getItems().clear();
        ballsToolbar.getItems().addAll(FXBallPaletteManager.getPaletteForVersion(version));
    }


    public static Stage generateSetTagsDialog(EditorAttribute tagAttribute) {
        Stage stage = new Stage();
        PropertiesViewManager.shouldRunOnClick[0] = false;
        stage.initOwner(FXStage.getStage());
        stage.initModality(Modality.WINDOW_MODAL);
        String[] possibleTags = {
                "ballbuster", "deadly", "detaching",
                "geomkiller", "mostlydeadly", "nodrag",
                "stopsign", "unwalkable", "walkable"
        };
        VBox vBox = new VBox();
        List<CheckBox> checkBoxes = new ArrayList<>();
        for (String possibleTag : possibleTags) {
            CheckBox checkBox = new CheckBox();
            checkBoxes.add(checkBox);
            checkBox.setSelected(Arrays.stream(tagAttribute.listValue())
                    .toList().contains(possibleTag));
            checkBox.selectedProperty().addListener(
                    (observable, oldValue, newValue) -> {
                        String tagsBuilder = "";
                        for (int i = 0; i < possibleTags.length; i++) {
                            if (checkBoxes.get(i).isSelected()) {
                                if (!tagsBuilder.isEmpty()) {
                                    tagsBuilder += ",";
                                }
                                tagsBuilder += possibleTags[i];
                            }
                        }
                        if (!tagsBuilder.equals(tagAttribute.stringValue()))
                            UndoManager.registerChange(new AttributeChangeAction(
                                    tagAttribute, tagAttribute.stringValue(), tagsBuilder));
                        tagAttribute.setValue(tagsBuilder);
                        FXPropertiesView.getPropertiesView().refresh();
            });
            Label label = new Label(possibleTag);
            HBox hBox = new HBox(checkBox, label);
            hBox.setSpacing(5);
            vBox.getChildren().add(hBox);
        }
        Button button = new Button("Done");
        button.setOnAction(event -> {
            PropertiesViewManager.shouldRunOnClick[0] = true;
            stage.close();
        });
        vBox.getChildren().add(button);
        vBox.setPadding(new Insets(5, 5, 5, 5));
        vBox.setSpacing(5);
        Scene scene = new Scene(vBox);
        scene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                PropertiesViewManager.shouldRunOnClick[0] = true;
                stage.close();
            }
        });
        stage.setScene(scene);
        stage.setOnCloseRequest(event -> PropertiesViewManager.shouldRunOnClick[0] = true);
        return stage;
    }

}
