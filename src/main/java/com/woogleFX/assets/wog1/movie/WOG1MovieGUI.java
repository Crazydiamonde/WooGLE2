package com.woogleFX.assets.wog1.movie;

import com.woogleFX.assets.wog1.animation.WOG1Animation;
import com.woogleFX.editorObjects.EditorObject;
import com.woogleFX.engine.AssetManager;
import com.woogleFX.engine.fx.editorButtons.FXEditorButtons;
import com.woogleFX.engine.gui.alarms.ErrorAlarm;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Node;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.ToolBar;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class WOG1MovieGUI {

    private static final ToolBar toolbar = new ToolBar();


    private static final FXEditorButtons.EditorButton buttonPlay = new FXEditorButtons.EditorButton() {
        @Override
        public void updateDisabled() {
            setDisable(false);
        }
    };
    private static final ScrollBar timeline = new ScrollBar();
    static {

        buttonPlay.setIcon("ButtonIcons/Level/play.png");
        buttonPlay.setOnAction(e -> {
            if (!(AssetManager.getAsset() instanceof WOG1Movie wog1Movie)) return;
            wog1Movie.setPlaying(!wog1Movie.isPlaying());
            updatePlaying(wog1Movie.isPlaying());
        });
        buttonPlay.setTooltip(new FXEditorButtons.DelayedTooltip("Play/Stop Movie"));
        toolbar.getItems().add(buttonPlay);

        timeline.valueProperty().addListener((observableValue, number, t1) -> {
            if (!(AssetManager.getAsset() instanceof WOG1Movie movie)) return;
            double length = movie.getMovie().getAttribute("length").doubleValue();
            movie.setTime(length * (t1.doubleValue() / (timeline.getMax() - timeline.getMin())));
        });

    }


    public static void updateTimeline(double alpha) {
        timeline.setValue(alpha * (timeline.getMax() - timeline.getMin()));
    }


    public static void updatePlaying(boolean playing) {
        buttonPlay.setIcon(playing ? "ButtonIcons/Level/pause.png" : "ButtonIcons/Level/play.png");
    }


    public static ArrayList<Node> getGUIElements() {
        return new ArrayList<>(List.of(toolbar, timeline));
    }

}
