package com.woogleFX.gameData.level;

import com.woogleFX.engine.LevelManager;

public class VisibilityManager {

    public static void showHideCameras() {
        LevelManager.getLevel().getVisibilitySettings().setShowCameras(
                !LevelManager.getLevel().getVisibilitySettings().isShowCameras());
    }

    public static void showHideForcefields() {
        LevelManager.getLevel().getVisibilitySettings().setShowForcefields(
                !LevelManager.getLevel().getVisibilitySettings().isShowForcefields());
    }

    public static void showHideGeometry() {
        LevelManager.getLevel().getVisibilitySettings().setShowGeometry(
                LevelManager.getLevel().getVisibilitySettings().getShowGeometry() - 1);
        if (LevelManager.getLevel().getVisibilitySettings().getShowGeometry() < 0) {
            LevelManager.getLevel().getVisibilitySettings().setShowGeometry(2);
        }
    }

    public static void showHideGraphics() {
        LevelManager.getLevel().getVisibilitySettings().setShowGraphics(
                !LevelManager.getLevel().getVisibilitySettings().isShowGraphics());
    }

    public static void showHideGoos() {
        LevelManager.getLevel().getVisibilitySettings().setShowGoos(
                LevelManager.getLevel().getVisibilitySettings().getShowGoos() - 1);
        if (LevelManager.getLevel().getVisibilitySettings().getShowGoos() < 0) {
            LevelManager.getLevel().getVisibilitySettings().setShowGoos(2);
        }
    }

    public static void showHideParticles() {
        LevelManager.getLevel().getVisibilitySettings().setShowParticles(
                !LevelManager.getLevel().getVisibilitySettings().isShowParticles());
    }

    public static void showHideLabels() {
        LevelManager.getLevel().getVisibilitySettings().setShowLabels(
                !LevelManager.getLevel().getVisibilitySettings().isShowLabels());
    }

    public static void showHideAnim() {
        LevelManager.getLevel().getVisibilitySettings().setShowAnimations(
                !LevelManager.getLevel().getVisibilitySettings().isShowAnimations());
    }

    public static void showHideSceneBGColor() {
        LevelManager.getLevel().getVisibilitySettings().setShowSceneBGColor(
                !LevelManager.getLevel().getVisibilitySettings().isShowSceneBGColor());
    }

}
