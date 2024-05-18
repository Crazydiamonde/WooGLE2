package com.WooGLEFX.Functions;

import com.WooGLEFX.Engine.Main;
import com.WooGLEFX.GUI.BallSelector;
import javafx.stage.Stage;

public class VisibilityManager {

    public static void showHideCameras() {
        LevelManager.getLevel().setShowCameras(!LevelManager.getLevel().isShowCameras());
    }

    public static void showHideForcefields() {
        LevelManager.getLevel().setShowForcefields(!LevelManager.getLevel().isShowForcefields());
    }

    public static void showHideGeometry() {
        LevelManager.getLevel().setShowGeometry(LevelManager.getLevel().getShowGeometry() - 1);
        if (LevelManager.getLevel().getShowGeometry() < 0) {
            LevelManager.getLevel().setShowGeometry(2);
        }
    }

    public static void showHideGraphics() {
        LevelManager.getLevel().setShowGraphics(!LevelManager.getLevel().isShowGraphics());
    }

    public static void showHideGoos() {
        LevelManager.getLevel().setShowGoos(LevelManager.getLevel().getShowGoos() - 1);
        if (LevelManager.getLevel().getShowGoos() < 0) {
            LevelManager.getLevel().setShowGoos(2);
        }
    }

    public static void showHideParticles() {
        LevelManager.getLevel().setShowParticles(!LevelManager.getLevel().isShowParticles());
    }

    public static void showHideLabels() {
        LevelManager.getLevel().setShowLabels(!LevelManager.getLevel().isShowLabels());
    }

    public static void showHideAnim() {
        LevelManager.getLevel().setShowAnimations(!LevelManager.getLevel().isShowAnimations());
    }

    public static void showHideSceneBGColor() {
        LevelManager.getLevel().setShowSceneBGColor(!LevelManager.getLevel().isShowSceneBGColor());
    }

}
