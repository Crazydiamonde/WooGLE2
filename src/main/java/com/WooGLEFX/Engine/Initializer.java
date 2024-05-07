package com.WooGLEFX.Engine;

import com.WooGLEFX.EditorObjects._Ball;
import com.WooGLEFX.Engine.FX.FXCreator;
import com.WooGLEFX.Engine.FX.FXHierarchy;
import com.WooGLEFX.Engine.FX.FXPropertiesView;
import com.WooGLEFX.File.FileManager;
import com.WooGLEFX.File.GlobalResourceManager;
import com.WooGLEFX.Functions.InputEvents.*;
import com.WooGLEFX.Functions.LevelLoader;
import com.WooGLEFX.Functions.LevelManager;
import com.WooGLEFX.Functions.LevelResourceManager;
import com.WooGLEFX.GUI.Alarms;
import com.WooGLEFX.Structures.EditorAttribute;
import com.WooGLEFX.Structures.EditorObject;
import com.WooGLEFX.Structures.InputField;
import com.WorldOfGoo.Addin.AddinLevelName;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.control.MenuBar;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.awt.*;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Initializer {

    private static final Logger logger = LoggerFactory.getLogger(Main.class);


    public static void start(Stage stage2, String[] launchArguments) {
        logger.debug(Initializer.class.getProtectionDomain().getCodeSource().getLocation().getPath());

        Main.setStage(stage2);

        try {
            FileManager.readWOGdirs();

            /* Check that the world of goo directory from properties.txt actually points to a file */
            /* This might require users to reset their WoG directory */
            if (!FileManager.getOldWOGdir().equals("") && !Files.exists(Path.of(FileManager.getOldWOGdir()))) {
                FileManager.setOldWOGdir("");
            }
            if (!FileManager.getNewWOGdir().equals("") && !Files.exists(Path.of(FileManager.getNewWOGdir()))) {
                FileManager.setNewWOGdir("");
            }

            if (FileManager.getOldWOGdir().equals("") && FileManager.getNewWOGdir().equals("")) {
                Alarms.missingWOG(launchArguments);
            } else {
                startWithWorldOfGooVersion(launchArguments);
            }
        } catch (ParserConfigurationException | SAXException | IOException e) {
            Alarms.errorMessage(e);
        }
    }

    public static void startWithWorldOfGooVersion(String[] launchArguments) {
        logger.info("1.3 = " + FileManager.getOldWOGdir());
        logger.info("1.5 = " + FileManager.getNewWOGdir());

        Main.setLevelSelectPane(new TabPane());

        // Initialize stage name/icon
        Main.getStage().setTitle("World of Goo Anniversary Editor");

        Main.getStage().setScene(new Scene(new Pane()));
        Main.getStage().show();
        try {
            Main.getStage().getIcons().add(FileManager.getIcon("ButtonIcons\\icon.png"));
        } catch (Exception e) {
            logger.error("", e);
        }

        // Make menu that currently does nothing
        try {
            MenuBar bar = FXCreator.createMenu();

            // Import all goo balls and all misc resources from the game files

            if (!FileManager.getOldWOGdir().equals("")) {
                LevelResourceManager.importGameResources(1.3);
            }
            if (!FileManager.getNewWOGdir().equals("")) {
                LevelResourceManager.importGameResources(1.5);
            }

            // logger.info(FileManager.isHasOldWOG());

            for (int i = 0; i < FileManager.getPaletteBalls().size(); i++) {

                String ballName = FileManager.getPaletteBalls().get(i);
                double ballVersion = FileManager.getPaletteVersions().get(i);

                try {
                    _Ball ball = FileManager.openBall(ballName, ballVersion);

                    for (EditorObject resrc : FileManager.commonBallResrcData) {
                        GlobalResourceManager.addResource(resrc, ballVersion);
                    }

                    if (ball != null) {
                        ball.makeImages(ballVersion);
                        ball.setVersion(ballVersion);
                        Main.getImportedBalls().add(ball);
                    }
                } catch (ParserConfigurationException | SAXException | IOException e) {
                    Alarms.errorMessage(e);
                }

            }

            // Initialize both canvasses
            Main.setCanvas(new Canvas());
            Main.getCanvas().setWidth(1400);
            Main.getCanvas().setHeight(1100);

            Main.setImageCanvas(new Canvas());
            Main.getImageCanvas().setWidth(1400);
            Main.getImageCanvas().setHeight(1100);

            // Ask FXCreator for both TreeTableViews
            FXHierarchy.init();
            FXPropertiesView.init();

            // Configure PropertiesView
            FXPropertiesView.getPropertiesView().prefWidthProperty().bind(FXHierarchy.getHierarchy().widthProperty());
            FXPropertiesView.getPropertiesView().setRoot(
                    new TreeItem<>(new EditorAttribute(null, "", "", "", new InputField("", InputField.ANY), false)));

            // Combine everything weirdly
            Main.setSplitPane(new SplitPane());
            Main.setThingPane(new Pane(Main.getImageCanvas()));
            StackPane pane = new StackPane(Main.getThingPane(), new Pane(Main.getCanvas()));
            Separator separator = new Separator();
            Main.setViewPane(new VBox(FXHierarchy.getHierarchy(), separator, Main.hierarchySwitcherButtons, FXPropertiesView.getPropertiesView()));
            separator.hoverProperty().addListener(new ChangeListener<Boolean>() {
                @Override
                public void changed(ObservableValue<? extends Boolean> observableValue, Boolean aBoolean, Boolean t1) {
                    if (t1) {
                        Main.getStage().getScene().setCursor(Cursor.N_RESIZE);
                    } else {
                        Main.getStage().getScene().setCursor(Cursor.DEFAULT);
                    }
                }
            });

            separator.setOnMouseDragged(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    double height = Main.getMouseY() + Main.getMouseYOffset() - Main.getvBox().getChildren().get(4).getLayoutY() - 2;
                    FXHierarchy.getHierarchy().setMinHeight(height);
                    FXHierarchy.getHierarchy().setMaxHeight(height);
                    FXHierarchy.getHierarchy().setPrefHeight(height);
                }
            });
            Main.getSplitPane().getItems().addAll(new VBox(Main.getLevelSelectPane(), pane), Main.getViewPane());
            Main.getStage().addEventFilter(ScrollEvent.SCROLL, MouseWheelMovedManager::mouseWheelMoved);

            Main.getLevelSelectPane().getSelectionModel().selectedItemProperty().addListener((observableValue, tab, t1) -> {
                if (t1 == null) {
                    LevelManager.setLevel(null);
                    LevelManager.onSetLevel(null);
                    FXCreator.enableAllButtons(true);
                    if (FileManager.isHasOldWOG()) {
                        FXCreator.buttonNewOld.setDisable(false);
                        FXCreator.buttonOpenOld.setDisable(false);
                        FXCreator.newLevelOldItem.setDisable(false);
                        FXCreator.openLevelOldItem.setDisable(false);
                    }
                    if (FileManager.isHasNewWOG()) {
                        FXCreator.buttonNewNew.setDisable(false);
                        FXCreator.buttonOpenNew.setDisable(false);
                        FXCreator.newLevelNewItem.setDisable(false);
                        FXCreator.openLevelNewItem.setDisable(false);
                    }
                }
            });

            Main.getLevelSelectPane().widthProperty().addListener(new ChangeListener<Number>() {
                @Override
                public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                    int numTabs = Main.getLevelSelectPane().getTabs().size();
                    double tabSize = 1 / (numTabs + 1.0);
                    Main.getLevelSelectPane()
                            .setTabMaxWidth(tabSize * (Main.getLevelSelectPane().getWidth() - 15) - 15);
                    Main.getLevelSelectPane()
                            .setTabMinWidth(tabSize * (Main.getLevelSelectPane().getWidth() - 15) - 15);
                }
            });

            // Combine everything inside a VBox
            Main.setvBox(new VBox(bar));
            try {
                FXCreator.buttons(Main.getvBox());
            } catch (IOException e) {
                Alarms.errorMessage(e);
            }

            Main.getLevelSelectPane().setMinHeight(0);
            Main.getLevelSelectPane().setMaxHeight(0);

            Main.getLevelSelectPane().setTabClosingPolicy(TabPane.TabClosingPolicy.ALL_TABS);

            Main.getvBox().getChildren().add(Main.getSplitPane());

            // Event handlers
            Main.getStage().addEventFilter(MouseEvent.MOUSE_PRESSED, MousePressedManager::eventMousePressed);
            Main.getStage().addEventFilter(MouseEvent.MOUSE_RELEASED, MouseReleasedManager::eventMouseReleased);
            Main.getStage().addEventFilter(MouseEvent.MOUSE_DRAGGED, MouseDraggedManager::eventMouseDragged);
            Main.getStage().addEventFilter(MouseEvent.MOUSE_MOVED, MouseMovedManager::eventMouseMoved);
            Main.getStage().addEventFilter(KeyEvent.KEY_PRESSED, KeyPressedManager::keyPressed);

            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            Main.getStage().setScene(new Scene(Main.getvBox(), screenSize.width * 0.75, screenSize.height * 0.75 - 30));
            Main.getStage().getScene().getStylesheets().add("style.css");

            Main.getStage().setOnCloseRequest(new EventHandler<WindowEvent>() {
                @Override
                public void handle(WindowEvent windowEvent) {
                    windowEvent.consume();
                    Main.getStage().close();
                }
            });

            Main.getStage().show();

            Main.getCanvas().widthProperty().bind(Main.getStage().widthProperty());
            Main.getCanvas().heightProperty().bind(Main.getSplitPane().heightProperty());
            Main.getImageCanvas().widthProperty().bind(Main.getSplitPane().widthProperty());
            Main.getImageCanvas().heightProperty().bind(Main.getSplitPane().heightProperty());
            Main.getSplitPane().maxHeightProperty().bind(Main.getStage().heightProperty());
            Main.getSplitPane().prefHeightProperty().bind(Main.getStage().heightProperty());
            FXPropertiesView.getPropertiesView().prefHeightProperty()
                    .bind(Main.getViewPane().heightProperty().subtract(FXPropertiesView.getPropertiesView().layoutYProperty()));

            Main.getLevelSelectPane().setTabDragPolicy(TabPane.TabDragPolicy.REORDER);
            Main.getLevelSelectPane().setStyle("-fx-open-tab-animation: NONE");

            Main.getSplitPane().getDividers().get(0).setPosition(0.8);

            Main.getStage().heightProperty().addListener(new ChangeListener<Number>() {
                @Override
                public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                }
            });

            new AddinLevelName(null);

            FileManager.openFailedImage();

            FXCreator.enableAllButtons(true);

            if (FileManager.isHasOldWOG()) {
                FXCreator.buttonNewOld.setDisable(false);
                FXCreator.buttonOpenOld.setDisable(false);
                FXCreator.newLevelOldItem.setDisable(false);
                FXCreator.openLevelOldItem.setDisable(false);
            }
            if (FileManager.isHasNewWOG()) {
                FXCreator.buttonNewNew.setDisable(false);
                FXCreator.buttonOpenNew.setDisable(false);
                FXCreator.newLevelNewItem.setDisable(false);
                FXCreator.openLevelNewItem.setDisable(false);
            }

            FXHierarchy.getHierarchy().sort();

            EditorWindow editorWindow = new EditorWindow();
            editorWindow.start();

            Main.getStage().setOnCloseRequest(new EventHandler<WindowEvent>() {
                @Override
                public void handle(WindowEvent event) {
                    event.consume();
                    Main.resumeLevelClosing();
                }
            });
            // LevelExporter.exportBallAsXML(Main.getImportedBalls().get(0), "");

            if (launchArguments.length > 0) {
                logger.info("Opening level " + launchArguments[0]);
                if (FileManager.isHasNewWOG()) {
                    LevelLoader.openLevel(launchArguments[0], 1.5);
                } else {
                    LevelLoader.openLevel(launchArguments[0], 1.3);
                }
            }
        } catch (FileNotFoundException e) {
            Alarms.errorMessage(e);
        }
    }

}
