package com.woogleFX.engine;

import javafx.application.Application;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main extends Application {

    private static final Logger logger = LoggerFactory.getLogger(Main.class);


    private static String[] launchArguments;
    public static String[] getLaunchArguments() {
        return launchArguments;
    }
    public static void setLaunchArguments(String[] launchArguments) {
        Main.launchArguments = launchArguments;
    }


    @Override
    public void start(Stage stage) {
        Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> logger.error("", throwable));
        Initializer.start(stage);
    }

}