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
        } else {
            if (level.getVersion() == 1.3) {
                vBox.getChildren().add(2, FXEditorButtons.getOldGooballsToolbar());
            } else {
                vBox.getChildren().add(2, FXEditorButtons.getNewGooballsToolbar());
            }
            FXContainers.getStage().setTitle(
                    level.getLevelName() + " (version " + level.getVersion() + ") — World of Goo Anniversary Editor");
            FXEditorButtons.buttonShowHideAnim.setGraphic(
                    new ImageView(level.isShowAnimations() ? WorldLevel.showHideAnim : WorldLevel.showHideAnim0));
            FXEditorButtons.buttonShowHideCamera.setGraphic(
                    new ImageView(level.isShowCameras() ? WorldLevel.showHideCam1 : WorldLevel.showHideCam0));
            FXEditorButtons.buttonShowHideForcefields.setGraphic(new ImageView(
                    level.isShowForcefields() ? WorldLevel.showHideForcefields1 : WorldLevel.showHideForcefields0));
            FXEditorButtons.buttonShowHideGeometry.setGraphic(new ImageView(
                    level.isShowGeometry() ? WorldLevel.showHideGeometry1 : WorldLevel.showHideGeometry0));
            switch (level.getShowGoos()) {
                case 0 -> FXEditorButtons.buttonShowHideGoos.setGraphic(new ImageView(WorldLevel.showHideGoobs0));
                case 1 -> FXEditorButtons.buttonShowHideGoos.setGraphic(new ImageView(WorldLevel.showHideGoobs1));
                case 2 -> FXEditorButtons.buttonShowHideGoos.setGraphic(new ImageView(WorldLevel.showHideGoobs2));
            }
            FXEditorButtons.buttonShowHideGraphics.setGraphic(
                    new ImageView(level.isShowGraphics() ? WorldLevel.showHideImages1 : WorldLevel.showHideImages0));
            FXEditorButtons.buttonShowHideLabels.setGraphic(
                    new ImageView(level.isShowLabels() ? WorldLevel.showHideLabels1 : WorldLevel.showHideLabels0));
            FXEditorButtons.buttonShowHideParticles.setGraphic(new ImageView(
                    level.isShowParticles() ? WorldLevel.showHideParticles1 : WorldLevel.showHideParticles0));
            FXEditorButtons.buttonShowHideSceneBGColor.setGraphic(new ImageView(
                    level.isShowSceneBGColor() ? WorldLevel.showHideSceneBGColor1 : WorldLevel.showHideSceneBGColor0));
            FXPropertiesView.changeTableView(level.getSelected());
            SelectionManager.goToSelectedInHierarchy();
        }
    }

}
