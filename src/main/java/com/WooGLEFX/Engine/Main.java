package com.WooGLEFX.Engine;

import com.WooGLEFX.GUI.Alarms;

import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    private static String[] launchArguments;
    public static String[] getLaunchArguments() {
        return launchArguments;
    }


    @Override
    public void start(Stage stage) {
        Initializer.start(stage);
    }


    public static void main(String[] args) {
        Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> Alarms.errorMessage(throwable));
        launchArguments = args;
        launch();
    }

}