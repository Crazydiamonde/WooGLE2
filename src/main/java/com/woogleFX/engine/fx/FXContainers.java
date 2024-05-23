package com.woogleFX.engine.fx;

import com.woogleFX.engine.SelectionManager;
import javafx.scene.Cursor;
import javafx.scene.control.Separator;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class FXContainers {

    private static final SplitPane splitPane = new SplitPane();
    public static SplitPane getSplitPane() {
        return splitPane;
    }


    private static final Pane thingPane = new Pane();
    public static Pane getThingPane() {
        return thingPane;
    }


    private static final VBox vBox = new VBox();
    public static VBox getvBox() {
        return vBox;
    }


    private static final VBox viewPane = new VBox();
    public static VBox getViewPane() {
        return viewPane;
    }


    public static void init() {

        splitPane.maxHeightProperty().bind(FXStage.getStage().heightProperty());
        splitPane.prefHeightProperty().bind(FXStage.getStage().heightProperty());

        vBox.getChildren().addAll(FXMenu.getMenuBar(), splitPane);

        StackPane pane = new StackPane(thingPane, new Pane(FXCanvas.getCanvas()));
        Separator separator = new Separator();
        viewPane.getChildren().addAll(FXHierarchy.getHierarchy(), separator, FXHierarchy.getHierarchySwitcherButtons(), FXPropertiesView.getPropertiesView());
        separator.hoverProperty().addListener((observableValue, aBoolean, t1) -> {
            if (t1) {
                FXScene.getScene().setCursor(Cursor.N_RESIZE);
            } else {
                FXScene.getScene().setCursor(Cursor.DEFAULT);
            }
        });

        separator.setOnMouseDragged(event -> {
            double height = SelectionManager.getMouseY() + FXCanvas.getMouseYOffset() - FXContainers.getvBox().getChildren().get(4).getLayoutY() - 2;
            FXHierarchy.getHierarchy().setMinHeight(height);
            FXHierarchy.getHierarchy().setMaxHeight(height);
            FXHierarchy.getHierarchy().setPrefHeight(height);
        });

        splitPane.getItems().addAll(new VBox(FXLevelSelectPane.getLevelSelectPane(), pane), viewPane);

        splitPane.getDividers().get(0).setPosition(0.7);

    }

}
