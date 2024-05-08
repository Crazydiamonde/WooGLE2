package com.WooGLEFX.Engine.FX;

import com.WooGLEFX.Engine.Main;
import com.WooGLEFX.Functions.LevelManager;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;

public class FXCanvas {

    private static Canvas canvas;
    public static Canvas getCanvas() {
        return canvas;
    }
    public static void setCanvas(Canvas canvas) {
        FXCanvas.canvas = canvas;
    }


    private static Canvas imageCanvas;
    public static Canvas getImageCanvas() {
        return imageCanvas;
    }


    public static void setImageCanvas(Canvas imageCanvas) {
        FXCanvas.imageCanvas = imageCanvas;
    }
    public static Point2D getScreenCenter() {
        return new Point2D((FXContainers.getThingPane().getWidth() / 2 - LevelManager.getLevel().getOffsetX()) / LevelManager.getLevel().getZoom(),
                (FXContainers.getThingPane().getHeight() / 2 - LevelManager.getLevel().getOffsetY()) / LevelManager.getLevel().getZoom());
    }

    public static double getMouseYOffset() {
        return FXLevelSelectPane.getLevelSelectPane().getHeight() + FXContainers.getvBox().getChildren().get(4).getLayoutY();
    }

}
