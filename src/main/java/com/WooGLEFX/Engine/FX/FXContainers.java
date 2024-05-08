package com.WooGLEFX.Engine.FX;

import com.WooGLEFX.Engine.Main;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class FXContainers {

    private static SplitPane splitPane;
    public static SplitPane getSplitPane() {
        return splitPane;
    }
    public static void setSplitPane(SplitPane splitPane) {
        FXContainers.splitPane = splitPane;
    }


    private static Stage stage;
    public static Stage getStage() {
        return stage;
    }
    public static void setStage(Stage stage) {
        FXContainers.stage = stage;
    }


    private static Pane thingPane;
    public static Pane getThingPane() {
        return thingPane;
    }
    public static void setThingPane(Pane thingPane) {
        FXContainers.thingPane = thingPane;
    }


    private static VBox vBox;
    public static VBox getvBox() {
        return vBox;
    }
    public static void setvBox(VBox vBox) {
        FXContainers.vBox = vBox;
    }


    public static VBox viewPane;
    public static VBox getViewPane() {
        return viewPane;
    }
    public static void setViewPane(VBox viewPane) {
        FXContainers.viewPane = viewPane;
    }

}
