package com.WooGLEFX.Engine.FX;

import javafx.scene.Scene;

import java.awt.*;

public class FXScene {

    private static Scene scene;
    public static Scene getScene() {
        return scene;
    }


    public static void init() {

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

        scene = new Scene(FXContainers.getvBox(), screenSize.width * 0.75, screenSize.height * 0.75 - 30);

        scene.getStylesheets().add("style.css");

    }


}
