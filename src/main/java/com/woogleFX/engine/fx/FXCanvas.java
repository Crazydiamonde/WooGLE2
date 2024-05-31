package com.woogleFX.engine.fx;

import com.woogleFX.engine.LevelManager;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;

public class FXCanvas {

    private static final Canvas canvas = new Canvas();
    public static Canvas getCanvas() {
        return canvas;
    }


    public static Point2D getScreenCenter() {
        return new Point2D((FXContainers.getThingPane().getWidth() / 2 - LevelManager.getLevel().getOffsetX()) / LevelManager.getLevel().getZoom(),
                (FXContainers.getThingPane().getHeight() / 2 - LevelManager.getLevel().getOffsetY()) / LevelManager.getLevel().getZoom());
    }

    public static double getMouseYOffset() {
        return FXLevelSelectPane.getLevelSelectPane().getHeight() + FXContainers.getvBox().getChildren().get(4).getLayoutY();
    }


    public static void init() {

        canvas.setWidth(FXStage.getStage().getWidth() * 0.7);
        canvas.setHeight(FXStage.getStage().getHeight() - 100);
        canvas.widthProperty().bind(FXStage.getStage().widthProperty());
        canvas.heightProperty().bind(FXContainers.getSplitPane().heightProperty());

    }

}
