package com.WooGLEFX.Functions;

import com.WooGLEFX.Engine.FX.FXCanvas;
import com.WooGLEFX.Engine.FX.FXContainers;
import com.WooGLEFX.Engine.FX.FXEditorButtons;
import com.WooGLEFX.Engine.FX.FXPropertiesView;
import com.WooGLEFX.Engine.Main;
import com.WooGLEFX.Engine.Renderer;
import com.WooGLEFX.Engine.SelectionManager;
import com.WooGLEFX.Structures.WorldLevel;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.transform.Affine;

public class LevelManager {

    private static WorldLevel level = null;
    public static WorldLevel getLevel() {
        return level;
    }
    public static void setLevel(WorldLevel _level) {
        level = _level;
        FXEditorButtons.enableAllButtons(false);

        if (level != null) {

            // Transform the canvas according to the updated translation and scale.
            Renderer.t = new Affine();
            Renderer.t.appendTranslation(level.getOffsetX(), level.getOffsetY());
            Renderer.t.appendScale(level.getZoom(), level.getZoom());
            FXCanvas.getImageCanvas().getGraphicsContext2D().setTransform(Renderer.t);

        }

        onSetLevel(level);
        Renderer.draw();
    }

    public static void onSetLevel(WorldLevel level) {

        VBox vBox = FXContainers.getvBox();

        vBox.getChildren().remove(2);

        if (level == null) {
            FXContainers.getStage().setTitle("World of Goo Anniversary Editor");
            vBox.getChildren().add(2, FXEditorButtons.getNullGooballsToolbar());
            return;
        }

        if (level.getVersion() == 1.3) {
            vBox.getChildren().add(2, FXEditorButtons.getOldGooballsToolbar());
        } else {
            vBox.getChildren().add(2, FXEditorButtons.getNewGooballsToolbar());
        }

        String levelName = level.getLevelName() + " (version " + level.getVersion() + ")";
        FXContainers.getStage().setTitle(levelName + " — World of Goo Anniversary Editor");

        FXEditorButtons.updateButtons();

        FXPropertiesView.changeTableView(level.getSelected());
        SelectionManager.goToSelectedInHierarchy();

    }

}
