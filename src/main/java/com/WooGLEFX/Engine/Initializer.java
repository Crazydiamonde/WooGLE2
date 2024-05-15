package com.WooGLEFX.Engine;

import com.WooGLEFX.EditorObjects._Ball;
import com.WooGLEFX.Engine.FX.*;
import com.WooGLEFX.File.FileManager;
import com.WooGLEFX.File.GlobalResourceManager;
import com.WooGLEFX.Functions.*;
import com.WooGLEFX.Functions.InputEvents.*;
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


    public static void start(Stage stage2) {
        logger.debug(Initializer.class.getProtectionDomain().getCodeSource().getLocation().getPath());

        FXContainers.setStage(stage2);

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
                Alarms.missingWOG();
            } else {
                startWithWorldOfGooVersion();
            }
        } catch (ParserConfigurationException | SAXException | IOException e) {
            Alarms.errorMessage(e);
        }
    }

    public static void startWithWorldOfGooVersion() {
        logger.info("1.3 = " + FileManager.getOldWOGdir());
        logger.info("1.5 = " + FileManager.getNewWOGdir());

        FXLevelSelectPane.setLevelSelectPane(new TabPane());

        Stage stage = FXContainers.getStage();

        // Initialize stage name/icon
        stage.setTitle("World of Goo Anniversary Editor");

        stage.setScene(new Scene(new Pane()));
        stage.show();
        try {
            stage.getIcons().add(FileManager.getIcon("ButtonIcons\\icon.png"));
        } catch (Exception e) {
            logger.error("", e);
        }

        // Make menu that currently does nothing
        try {
            MenuBar bar = FXMenu.createMenu();

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
                        BallManager.getImportedBalls().add(ball);
                    }
                } catch (ParserConfigurationException | SAXException | IOException e) {
                    Alarms.errorMessage(e);
                }

            }

            // Initialize both canvasses
            FXCanvas.setCanvas(new Canvas());
            FXCanvas.getCanvas().setWidth(1400);
            FXCanvas.getCanvas().setHeight(1100);

            FXCanvas.setImageCanvas(new Canvas());
            FXCanvas.getImageCanvas().setWidth(1400);
            FXCanvas.getImageCanvas().setHeight(1100);

            // Ask FXCreator for both TreeTableViews
            FXHierarchy.init();
            FXPropertiesView.init();

            // Configure PropertiesView
            FXPropertiesView.getPropertiesView().prefWidthProperty().bind(FXHierarchy.getHierarchy().widthProperty());
            FXPropertiesView.getPropertiesView().setRoot(new TreeItem<>(EditorAttribute.NULL));

            // Combine everything weirdly
            FXContainers.setSplitPane(new SplitPane());
            FXContainers.setThingPane(new Pane(FXCanvas.getImageCanvas()));
            StackPane pane = new StackPane(FXContainers.getThingPane(), new Pane(FXCanvas.getCanvas()));
            Separator separator = new Separator();
            FXContainers.setViewPane(new VBox(FXHierarchy.getHierarchy(), separator, FXHierarchy.getHierarchySwitcherButtons(), FXPropertiesView.getPropertiesView()));
            separator.hoverProperty().addListener(new ChangeListener<Boolean>() {
                @Override
                public void changed(ObservableValue<? extends Boolean> observableValue, Boolean aBoolean, Boolean t1) {
                    if (t1) {
                        stage.getScene().setCursor(Cursor.N_RESIZE);
                    } else {
                        stage.getScene().setCursor(Cursor.DEFAULT);
                    }
                }
            });

            separator.setOnMouseDragged(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    double height = SelectionManager.getMouseY() + FXCanvas.getMouseYOffset() - FXContainers.getvBox().getChildren().get(4).getLayoutY() - 2;
                    FXHierarchy.getHierarchy().setMinHeight(height);
                    FXHierarchy.getHierarchy().setMaxHeight(height);
                    FXHierarchy.getHierarchy().setPrefHeight(height);
                }
            });

            TabPane levelSelectPane = FXLevelSelectPane.getLevelSelectPane();

            FXContainers.getSplitPane().getItems().addAll(new VBox(levelSelectPane, pane), FXContainers.getViewPane());
            stage.addEventFilter(ScrollEvent.SCROLL, MouseWheelMovedManager::mouseWheelMoved);

            levelSelectPane.getSelectionModel().selectedItemProperty().addListener((observableValue, tab, t1) -> {
                if (t1 == null) {
                    LevelManager.setLevel(null);
                    LevelManager.onSetLevel(null);
                    FXEditorButtons.enableAllButtons(true);
                    if (FileManager.isHasOldWOG()) {
                        FXEditorButtons.buttonNewOld.setDisable(false);
                        FXEditorButtons.buttonOpenOld.setDisable(false);
                        FXMenu.newLevelOldItem.setDisable(false);
                        FXMenu.openLevelOldItem.setDisable(false);
                    }
                    if (FileManager.isHasNewWOG()) {
                        FXEditorButtons.buttonNewNew.setDisable(false);
                        FXEditorButtons.buttonOpenNew.setDisable(false);
                        FXMenu.newLevelNewItem.setDisable(false);
                        FXMenu.openLevelNewItem.setDisable(false);
                    }
                }
            });

            levelSelectPane.widthProperty().addListener(new ChangeListener<Number>() {
                @Override
                public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                    int numTabs = levelSelectPane.getTabs().size();
                    double tabSize = 1 / (numTabs + 1.0);
                    levelSelectPane
                            .setTabMaxWidth(tabSize * (levelSelectPane.getWidth() - 15) - 15);
                    levelSelectPane
                            .setTabMinWidth(tabSize * (levelSelectPane.getWidth() - 15) - 15);
                }
            });

            // Combine everything inside a VBox
            FXContainers.setvBox(new VBox(bar));
            try {
                FXEditorButtons.buttons(FXContainers.getvBox());
            } catch (IOException e) {
                Alarms.errorMessage(e);
            }

            levelSelectPane.setMinHeight(0);
            levelSelectPane.setMaxHeight(0);

            levelSelectPane.setTabClosingPolicy(TabPane.TabClosingPolicy.ALL_TABS);

            FXContainers.getvBox().getChildren().add(FXContainers.getSplitPane());

            // Event handlers
            stage.addEventFilter(MouseEvent.MOUSE_PRESSED, MousePressedManager::eventMousePressed);
            stage.addEventFilter(MouseEvent.MOUSE_RELEASED, MouseReleasedManager::eventMouseReleased);
            stage.addEventFilter(MouseEvent.MOUSE_DRAGGED, MouseDraggedManager::eventMouseDragged);
            stage.addEventFilter(MouseEvent.MOUSE_MOVED, MouseMovedManager::eventMouseMoved);
            stage.addEventFilter(KeyEvent.KEY_PRESSED, KeyPressedManager::keyPressed);

            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            stage.setScene(new Scene(FXContainers.getvBox(), screenSize.width * 0.75, screenSize.height * 0.75 - 30));
            stage.getScene().getStylesheets().add("style.css");

            stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                @Override
                public void handle(WindowEvent windowEvent) {
                    windowEvent.consume();
                    stage.close();
                }
            });

            stage.show();

            FXCanvas.getCanvas().widthProperty().bind(FXContainers.getStage().widthProperty());
            FXCanvas.getCanvas().heightProperty().bind(FXContainers.getSplitPane().heightProperty());
            FXCanvas.getImageCanvas().widthProperty().bind(FXContainers.getSplitPane().widthProperty());
            FXCanvas.getImageCanvas().heightProperty().bind(FXContainers.getSplitPane().heightProperty());
            FXContainers.getSplitPane().maxHeightProperty().bind(FXContainers.getStage().heightProperty());
            FXContainers.getSplitPane().prefHeightProperty().bind(FXContainers.getStage().heightProperty());
            FXPropertiesView.getPropertiesView().prefHeightProperty()
                    .bind(FXContainers.getViewPane().heightProperty().subtract(FXPropertiesView.getPropertiesView().layoutYProperty()));

            levelSelectPane.setTabDragPolicy(TabPane.TabDragPolicy.REORDER);
            levelSelectPane.setStyle("-fx-open-tab-animation: NONE");

            FXContainers.getSplitPane().getDividers().get(0).setPosition(0.8);

            stage.heightProperty().addListener(new ChangeListener<Number>() {
                @Override
                public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                }
            });

            new AddinLevelName(null);

            FileManager.openFailedImage();

            FXEditorButtons.enableAllButtons(true);

            if (FileManager.isHasOldWOG()) {
                FXEditorButtons.buttonNewOld.setDisable(false);
                FXEditorButtons.buttonOpenOld.setDisable(false);
                FXMenu.newLevelOldItem.setDisable(false);
                FXMenu.openLevelOldItem.setDisable(false);
            }
            if (FileManager.isHasNewWOG()) {
                FXEditorButtons.buttonNewNew.setDisable(false);
                FXEditorButtons.buttonOpenNew.setDisable(false);
                FXMenu.newLevelNewItem.setDisable(false);
                FXMenu.openLevelNewItem.setDisable(false);
            }

            FXHierarchy.getHierarchy().sort();

            EditorWindow editorWindow = new EditorWindow();
            editorWindow.start();

            stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                @Override
                public void handle(WindowEvent event) {
                    event.consume();
                    LevelCloser.resumeLevelClosing();
                }
            });
            // LevelExporter.exportBallAsXML(Main.getImportedBalls().get(0), "");

            String[] launchArguments = Main.getLaunchArguments();

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
