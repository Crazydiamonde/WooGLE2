package com.WooGLEFX.Functions;

import com.WooGLEFX.Engine.FX.FXCreator;
import com.WooGLEFX.Engine.Main;
import com.WooGLEFX.Engine.Renderer;
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
        FXCreator.enableAllButtons(false);

        // Transform the canvas according to the updated translation and scale.
        Main.t = new Affine();
        Main.t.appendTranslation(level.getOffsetX(), level.getOffsetY());
        Main.t.appendScale(level.getZoom(), level.getZoom());
        Main.getImageCanvas().getGraphicsContext2D().setTransform(Main.t);
        LevelManager.onSetLevel(level);
        Renderer.draw();
    }

    public static void onSetLevel(WorldLevel level) {

        VBox vBox = Main.getvBox();

        vBox.getChildren().remove(2);
        if (level == null) {
            Main.getStage().setTitle("World of Goo Anniversary Editor");
            vBox.getChildren().add(2, FXCreator.getNullGooballsToolbar());
        } else {
            if (level.getVersion() == 1.3) {
                vBox.getChildren().add(2, FXCreator.getOldGooballsToolbar());
            } else {
                vBox.getChildren().add(2, FXCreator.getNewGooballsToolbar());
            }
            Main.getStage().setTitle(
                    level.getLevelName() + " (version " + level.getVersion() + ") — World of Goo Anniversary Editor");
            FXCreator.buttonShowHideAnim.setGraphic(
                    new ImageView(level.isShowAnimations() ? WorldLevel.showHideAnim : WorldLevel.showHideAnim0));
            FXCreator.buttonShowHideCamera.setGraphic(
                    new ImageView(level.isShowCameras() ? WorldLevel.showHideCam1 : WorldLevel.showHideCam0));
            FXCreator.buttonShowHideForcefields.setGraphic(new ImageView(
                    level.isShowForcefields() ? WorldLevel.showHideForcefields1 : WorldLevel.showHideForcefields0));
            FXCreator.buttonShowHideGeometry.setGraphic(new ImageView(
                    level.isShowGeometry() ? WorldLevel.showHideGeometry1 : WorldLevel.showHideGeometry0));
            switch (level.getShowGoos()) {
                case 0 -> FXCreator.buttonShowHideGoos.setGraphic(new ImageView(WorldLevel.showHideGoobs0));
                case 1 -> FXCreator.buttonShowHideGoos.setGraphic(new ImageView(WorldLevel.showHideGoobs1));
                case 2 -> FXCreator.buttonShowHideGoos.setGraphic(new ImageView(WorldLevel.showHideGoobs2));
            }
            FXCreator.buttonShowHideGraphics.setGraphic(
                    new ImageView(level.isShowGraphics() ? WorldLevel.showHideImages1 : WorldLevel.showHideImages0));
            FXCreator.buttonShowHideLabels.setGraphic(
                    new ImageView(level.isShowLabels() ? WorldLevel.showHideLabels1 : WorldLevel.showHideLabels0));
            FXCreator.buttonShowHideParticles.setGraphic(new ImageView(
                    level.isShowParticles() ? WorldLevel.showHideParticles1 : WorldLevel.showHideParticles0));
            FXCreator.buttonShowHideSceneBGColor.setGraphic(new ImageView(
                    level.isShowSceneBGColor() ? WorldLevel.showHideSceneBGColor1 : WorldLevel.showHideSceneBGColor0));
            Main.changeTableView(level.getSelected());
            Main.goToSelectedInHierarchy();
        }
    }

}
