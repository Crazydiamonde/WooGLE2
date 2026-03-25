package com.woogleFX.assets.wog1.particle;

import com.woogleFX.editorObjects.EditorObject;
import com.woogleFX.engine.AssetManager;
import com.woogleFX.engine.fx.editorButtons.FXEditorButtons;
import com.woogleFX.engine.fx.menu.FXMenu;
import com.worldOfGoo.particle.ParticleSpawner;
import com.worldOfGoo.particle.particle;
import javafx.scene.Node;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ToolBar;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;

public class WOG1ParticleGUI {

    private static final ToolBar toolBar = new ToolBar();

    private static final FXEditorButtons.EditorButton buttonReset = new FXEditorButtons.EditorButton() {
        @Override
        public void updateDisabled() {
            setDisable(false);
        }
    };
    private static final FXEditorButtons.EditorMenuButton button = new FXEditorButtons.EditorMenuButton() {
        @Override
        public void updateDisabled() {
            setDisable(false);
        }
    };
    static {

        buttonReset.setIcon("ButtonIcons/Resources/update_level_resources.png");
        buttonReset.setOnAction(e -> {
            if (!(AssetManager.getAsset() instanceof WOG1Particle wog1Particle)) return;
            for (EditorObject editorObject : wog1Particle.getObjects()) {
                if (editorObject instanceof particle particle) {
                    if (wog1Particle.getParticle() instanceof ParticleSpawner particleSpawner) {
                        particle.reset(particleSpawner);
                    }
                }
            }
        });
        buttonReset.setTooltip(new FXEditorButtons.DelayedTooltip("Reset Particle"));
        toolBar.getItems().add(buttonReset);

        FXMenu.EditorMenuItem color100 = new FXMenu.EditorMenuItem() {
            @Override
            public void updateDisabled() {
                setDisable(false);
            }
        };
        color100.setIcon("ButtonIcons/Resources/100.png");
        color100.setOnAction(actionEvent -> {
            ((WOG1Particle) AssetManager.getAsset()).setBackgroundColor(new Color(1.0, 1.0, 1.0, 1.0));
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
            ((WOG1Particle)AssetManager.getAsset()).setBackgroundColor(new Color(0.75, 0.75, 0.75, 1.0));
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
            ((WOG1Particle)AssetManager.getAsset()).setBackgroundColor(new Color(0.5, 0.5, 0.5, 1.0));
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
            ((WOG1Particle)AssetManager.getAsset()).setBackgroundColor(new Color(0.25, 0.25, 0.25, 1.0));
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
            ((WOG1Particle)AssetManager.getAsset()).setBackgroundColor(new Color(0.0, 0.0, 0.0, 1.0));
            button.setIcon("ButtonIcons/Resources/000.png");
        });

        button.setIcon("ButtonIcons/Resources/075.png");
        button.getItems().addAll(color100, color075, color050, color025, color000);

        button.setStyle("-fx-padding: -1 -4 -1 -4; ");
        toolBar.getItems().add(button);

    }


    public static ArrayList<Node> getGUIElements() {
        return new ArrayList<>(List.of(toolBar));
    }

}
