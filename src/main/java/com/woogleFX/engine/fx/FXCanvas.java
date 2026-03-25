package com.woogleFX.engine.fx;

import com.woogleFX.engine.AssetManager;
import com.woogleFX.engine.fx.assetSelectPane.FXAssetSelectPane;
import com.woogleFX.engine.fx.hierarchy.FXHierarchy;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;

public class FXCanvas {

    private static final Canvas canvas = new Canvas();
    public static Canvas getCanvas() {
        return canvas;
    }


    public static Point2D getScreenCenter() {
        return new Point2D((FXContainers.getThingPane().getWidth() / 2 - AssetManager.getAsset().getOffsetX()) / AssetManager.getAsset().getZoom(),
                (FXContainers.getThingPane().getHeight() / 2 - AssetManager.getAsset().getOffsetY()) / AssetManager.getAsset().getZoom());
    }

    public static double getMouseYOffset() {
        return FXAssetSelectPane.getAssetSelectPane().getHeight() + FXContainers.getvBox().getChildren().get(FXContainers.getvBox().getChildren().size() - 1).getLayoutY();
    }


    public static void init() {

        canvas.setWidth(FXStage.getStage().getWidth() * 0.7);
        canvas.setHeight(FXStage.getStage().getHeight() - 100);
        canvas.widthProperty().bind(FXStage.getStage().widthProperty().subtract(FXHierarchy.getHierarchy().widthProperty()));
        canvas.heightProperty().bind(FXContainers.getSplitPane().heightProperty());

    }

}
