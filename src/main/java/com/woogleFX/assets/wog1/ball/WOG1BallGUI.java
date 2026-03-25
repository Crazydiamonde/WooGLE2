package com.woogleFX.assets.wog1.ball;

import com.woogleFX.assets.GameVersion;
import com.woogleFX.assets.wog1.level.WOG1Level;
import com.woogleFX.engine.AssetManager;
import com.woogleFX.engine.fx.editorButtons.FXEditorButtons;
import com.woogleFX.engine.fx.menu.FXMenu;
import javafx.scene.Node;
import javafx.scene.control.MenuButton;
import javafx.scene.control.ToolBar;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;

public class WOG1BallGUI {

    private static final ToolBar toolbar = new ToolBar();

    private static final FXEditorButtons.EditorMenuButton button = new FXEditorButtons.EditorMenuButton() {
        @Override
        public void updateDisabled() {
            setDisable(false);
        }
    };
    private static final FXEditorButtons.EditorMenuButton buttonSaveAndPlay = new FXEditorButtons.EditorMenuButton() {
        @Override
        public void updateDisabled() {
            setDisable(AssetManager.getAsset() == null || AssetManager.getAsset().getVersion() == GameVersion.VERSION_WOG1_NEW);
        }
    };
    private static final FXMenu.EditorMenuItem buttonSaveAndPlay1 = new FXMenu.EditorMenuItem() {
        @Override
        public void updateDisabled() {
            setDisable(AssetManager.getAsset() == null || AssetManager.getAsset().getVersion() == GameVersion.VERSION_WOG1_NEW);
        }
    };
    private static final FXMenu.EditorMenuItem buttonSaveAndPlay10 = new FXMenu.EditorMenuItem() {
        @Override
        public void updateDisabled() {
            setDisable(AssetManager.getAsset() == null || AssetManager.getAsset().getVersion() == GameVersion.VERSION_WOG1_NEW);
        }
    };
    private static final FXMenu.EditorMenuItem buttonSaveAndPlay100 = new FXMenu.EditorMenuItem() {
        @Override
        public void updateDisabled() {
            setDisable(AssetManager.getAsset() == null || AssetManager.getAsset().getVersion() == GameVersion.VERSION_WOG1_NEW);
        }
    };
    static {


        FXMenu.EditorMenuItem color100 = new FXMenu.EditorMenuItem() {
            @Override
            public void updateDisabled() {
                setDisable(false);
            }
        };
        color100.setIcon("ButtonIcons/Resources/100.png");
        color100.setOnAction(actionEvent -> {
            ((WOG1Ball) AssetManager.getAsset()).setBackgroundColor(new Color(1.0, 1.0, 1.0, 1.0));
            button.setIcon("ButtonIcons/Resources/100.png");
        });
        FXMenu.EditorMenuItem color075 = new FXMenu.EditorMenuItem() {
            @Override
            public void updateDisabled() {
                setDisable(false);
            }
        };
        color075.setIcon("ButtonIcons/Resources/075.png");
        color075.setOnAction(actionEvent -> {
            ((WOG1Ball)AssetManager.getAsset()).setBackgroundColor(new Color(0.75, 0.75, 0.75, 1.0));
            button.setIcon("ButtonIcons/Resources/075.png");
        });
        FXMenu.EditorMenuItem color050 = new FXMenu.EditorMenuItem() {
            @Override
            public void updateDisabled() {
                setDisable(false);
            }
        };
        color050.setIcon("ButtonIcons/Resources/050.png");
        color050.setOnAction(actionEvent -> {
            ((WOG1Ball)AssetManager.getAsset()).setBackgroundColor(new Color(0.5, 0.5, 0.5, 1.0));
            button.setIcon("ButtonIcons/Resources/050.png");
        });
        FXMenu.EditorMenuItem color025 = new FXMenu.EditorMenuItem() {
            @Override
            public void updateDisabled() {
                setDisable(false);
            }
        };
        color025.setIcon("ButtonIcons/Resources/025.png");
        color025.setOnAction(actionEvent -> {
            ((WOG1Ball)AssetManager.getAsset()).setBackgroundColor(new Color(0.25, 0.25, 0.25, 1.0));
            button.setIcon("ButtonIcons/Resources/025.png");
        });
        FXMenu.EditorMenuItem color000 = new FXMenu.EditorMenuItem() {
            @Override
            public void updateDisabled() {
                setDisable(false);
            }
        };
        color000.setIcon("ButtonIcons/Resources/000.png");
        color000.setOnAction(actionEvent -> {
            ((WOG1Ball)AssetManager.getAsset()).setBackgroundColor(new Color(0.0, 0.0, 0.0, 1.0));
            button.setIcon("ButtonIcons/Resources/000.png");
        });

        button.setIcon("ButtonIcons/Resources/075.png");
        button.getItems().addAll(color100, color075, color050, color025, color000);

        button.setStyle("-fx-padding: -1 -4 -1 -4; ");
        toolbar.getItems().add(button);


        buttonSaveAndPlay1.setIcon("ButtonIcons/Level/play.png");
        buttonSaveAndPlay1.setOnAction(e -> ((WOG1Ball)AssetManager.getAsset()).saveAndPlay(1));
        buttonSaveAndPlay1.setText("Test with 1 Ball");
        buttonSaveAndPlay.getItems().add(buttonSaveAndPlay1);

        buttonSaveAndPlay10.setIcon("ButtonIcons/Level/play.png");
        buttonSaveAndPlay10.setOnAction(e -> ((WOG1Ball)AssetManager.getAsset()).saveAndPlay(10));
        buttonSaveAndPlay10.setText("Test with 10 Balls");
        buttonSaveAndPlay.getItems().add(buttonSaveAndPlay10);

        buttonSaveAndPlay100.setIcon("ButtonIcons/Level/play.png");
        buttonSaveAndPlay100.setOnAction(e -> ((WOG1Ball)AssetManager.getAsset()).saveAndPlay(100));
        buttonSaveAndPlay100.setText("Test with 100 Balls");
        buttonSaveAndPlay.getItems().add(buttonSaveAndPlay100);

        buttonSaveAndPlay.setIcon("ButtonIcons/Level/play.png");
        buttonSaveAndPlay.setStyle("-fx-padding: -1 -4 -1 -4; ");
        buttonSaveAndPlay.setTooltip(new FXEditorButtons.DelayedTooltip("Test Ball"));
        toolbar.getItems().add(buttonSaveAndPlay);

    }


    public static ArrayList<Node> getGUIElements() {
        return new ArrayList<>(List.of(toolbar));
    }

}
