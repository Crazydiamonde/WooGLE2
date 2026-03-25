package com.woogleFX.assets.wog1.animation;

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
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class WOG1AnimationGUI {

    /** Plays the animation. */
    private static final FXEditorButtons.EditorButton buttonPlay = new FXEditorButtons.EditorButton() {
        @Override
        public void updateDisabled() {
            setDisable(false);
        }
    };
    public static void updatePlaying(boolean playing) {
        buttonPlay.setIcon(playing ? "ButtonIcons/Level/pause.png" : "ButtonIcons/Level/play.png");
    }

    /** Allows the user to select a new image for the placeholder.
     * TODO: possibly save the preferred image? */
    private static final FXEditorButtons.EditorButton buttonSelectPlaceholderImage = new FXEditorButtons.EditorButton() {
        @Override
        public void updateDisabled() {
            setDisable(false);
        }
    };

    private static final ScrollBar timeline = new ScrollBar();
    public static void updateTimeline(double alpha) {
        timeline.setValue(alpha * (timeline.getMax() - timeline.getMin()));
    }

    private static final ToolBar toolbar = new ToolBar();

    public static ArrayList<Node> getGUIElements() {
        return new ArrayList<>(List.of(toolbar, timeline));
    }

    public static void init() {

        // Initialize play button
        buttonPlay.setIcon("ButtonIcons/Level/play.png");
        buttonPlay.setOnAction(e -> buttonPlayPressed());
        buttonPlay.setTooltip(new FXEditorButtons.DelayedTooltip("Play/Stop Animation"));
        toolbar.getItems().add(buttonPlay);

        // Initialize "select placeholder image" button
        buttonSelectPlaceholderImage.setIcon("ButtonIcons/Resources/update_level_resources.png");
        buttonSelectPlaceholderImage.setOnAction(e -> selectPlaceholderImage());
        buttonSelectPlaceholderImage.setTooltip(new FXEditorButtons.DelayedTooltip("Select Placeholder Image"));
        toolbar.getItems().add(buttonSelectPlaceholderImage);

        // Initialize timeline
        timeline.valueProperty().addListener((observableValue, number, t1) -> timelineUpdated(t1.doubleValue()));

    }

    /** Runs when the play button is pressed. */
    private static void buttonPlayPressed() {
        if (!(AssetManager.getAsset() instanceof WOG1Animation wog1Animation)) return;
        wog1Animation.setPlaying(!wog1Animation.isPlaying());
        updatePlaying(wog1Animation.isPlaying());
    }

    /** Runs when the timeline is scrolled. */
    private static void timelineUpdated(double timelineValue) {
        if (!(AssetManager.getAsset() instanceof WOG1Animation animation)) return;
        double length = 0;
        for (EditorObject keyframe : animation.getAnimation().getChildren()) {
            double time = keyframe.getAttribute("time").doubleValue();
            if (time > length) length = time;
        }
        animation.setTime(length * (timelineValue / (timeline.getMax() - timeline.getMin())));
    }

    /** Runs when the placeholder image button is pressed. */
    private static void selectPlaceholderImage() {

        // Make absolutely sure that we're dealing with a WOG1Animation.
        if (!(AssetManager.getAsset() instanceof WOG1Animation wog1Animation)) return;

        // Open a dialog for the new image.
        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showOpenDialog(new Stage());
        if (file == null) return;

        // Read the image.
        BufferedImage image;
        try {
            image = ImageIO.read(file);
        } catch (IOException e) {
            ErrorAlarm.show(e);
            return;
        }

        // Set the image as the new placeholder (at least until the asset is reloaded).
        wog1Animation.setPreviewImage(SwingFXUtils.toFXImage(image, null));

    }

}
