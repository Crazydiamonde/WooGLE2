package com.WooGLEFX.Engine;

import java.util.ArrayList;

import com.WooGLEFX.EditorObjects._Ball;
import com.WooGLEFX.Engine.FX.*;
import com.WooGLEFX.File.FileManager;
import com.WooGLEFX.File.LevelExporter;
import com.WooGLEFX.Functions.LevelManager;
import com.WooGLEFX.GUI.Alarms;
import com.WooGLEFX.GUI.BallSelector;
import com.WooGLEFX.Structures.EditorAttribute;
import com.WooGLEFX.Structures.EditorObject;
import com.WooGLEFX.Structures.WorldLevel;
import com.WooGLEFX.Structures.SimpleStructures.DragSettings;
import com.WooGLEFX.Structures.SimpleStructures.LevelTab;
import com.WorldOfGoo.Resrc.ResourceManifest;
import com.WorldOfGoo.Text.TextStrings;

import javafx.application.Application;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TabPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.transform.Affine;
import javafx.stage.Stage;

public class Main extends Application {

    private static String[] launchArguments;

    public static Affine t;


    private static EditorObject strand1Gooball;
    public static EditorObject getStrand1Gooball() {
        return strand1Gooball;
    }
    public static void setStrand1Gooball(EditorObject strand1Gooball) {
        Main.strand1Gooball = strand1Gooball;
    }


    private static Canvas canvas;
    public static Canvas getCanvas() {
        return canvas;
    }
    public static void setCanvas(Canvas canvas) {
        Main.canvas = canvas;
    }


    private static Canvas imageCanvas;
    public static Canvas getImageCanvas() {
        return imageCanvas;
    }
    public static void setImageCanvas(Canvas imageCanvas) {
        Main.imageCanvas = imageCanvas;
    }


    private static final ArrayList<_Ball> importedBalls = new ArrayList<>();
    public static ArrayList<_Ball> getImportedBalls() {
        return importedBalls;
    }


    private static SplitPane splitPane;
    public static SplitPane getSplitPane() {
        return splitPane;
    }
    public static void setSplitPane(SplitPane splitPane) {
        Main.splitPane = splitPane;
    }


    private static Stage stage;
    public static Stage getStage() {
        return stage;
    }
    public static void setStage(Stage stage) {
        Main.stage = stage;
    }


    private static Pane thingPane;
    public static Pane getThingPane() {
        return thingPane;
    }
    public static void setThingPane(Pane thingPane) {
        Main.thingPane = thingPane;
    }


    private static double mouseStartX;
    public static double getMouseStartX() {
        return mouseStartX;
    }
    public static void setMouseStartX(double mouseStartX) {
        Main.mouseStartX = mouseStartX;
    }


    private static double mouseStartY;
    public static double getMouseStartY() {
        return mouseStartY;
    }
    public static void setMouseStartY(double mouseStartY) {
        Main.mouseStartY = mouseStartY;
    }


    public static final ArrayList<String> failedResources = new ArrayList<>();


    private static VBox vBox;
    public static VBox getvBox() {
        return vBox;
    }
    public static void setvBox(VBox vBox) {
        Main.vBox = vBox;
    }


    public static Point2D getScreenCenter() {
        return new Point2D((thingPane.getWidth() / 2 - LevelManager.getLevel().getOffsetX()) / LevelManager.getLevel().getZoom(),
                (thingPane.getHeight() / 2 - LevelManager.getLevel().getOffsetY()) / LevelManager.getLevel().getZoom());
    }

    public static double getMouseYOffset() {
        return FXLevelSelectPane.getLevelSelectPane().getHeight() + vBox.getChildren().get(4).getLayoutY();
    }


    public static void quit() {
        resumeLevelClosing();
    }


    public static void supremeAddToList(ArrayList<EditorObject> list, EditorObject object) {
        list.add(object);
        for (EditorObject child : object.getChildren()) {
            supremeAddToList(list, child);
        }
    }

    public static void cloneLevel() {
        double version = LevelManager.getLevel().getVersion();
        Alarms.askForLevelName("clone", version);
    }

    public static final int SELECTION = 0;
    public static final int STRAND = 1;

    private static int mode = SELECTION;

    public static int getMode() {
        return mode;
    }

    public static void selectionMode() {
        mode = SELECTION;
        // Highlight selection button blue
        FXEditorButtons.buttonSelectMoveAndResize.setStyle("-fx-background-color: #9999ff;");
        // Un-highlight strand button
        FXEditorButtons.buttonStrandMode.setStyle("");
    }

    public static void strandMode() {
        mode = STRAND;
        // Highlight strand button
        FXEditorButtons.buttonStrandMode.setStyle("-fx-background-color: #9999ff;");
        // Un-highlight selection button
        FXEditorButtons.buttonSelectMoveAndResize.setStyle("");
    }

    public static void saveBallInVersion(double oldVersion, double newVersion) {
        new BallSelector(oldVersion, newVersion).start(new Stage());
    }

    public static void saveBallInVersion(String ball, double oldVersion, double newVersion) {

        try {

            _Ball ballObject = FileManager.openBall(ball, oldVersion);

            if (ballObject == null) return;

            if (newVersion == 1.3) {
                LevelExporter.exportBallAsXML(ballObject,
                        FileManager.getOldWOGdir() + "\\res\\balls\\" + ball,
                        1.3, false);
            } else if (newVersion == 1.5) {
                LevelExporter.exportBallAsXML(ballObject,
                        FileManager.getNewWOGdir() + "\\res\\balls\\" + ball,
                        1.5, false);
            }
        } catch (Exception e) {
            Alarms.errorMessage(e);
        }
    }

    public static EditorObject getSelected() {
        return LevelManager.getLevel().getSelected();
    }

    public static void setSelected(EditorObject _selected) {
        LevelManager.getLevel().setSelected(_selected);
        goToSelectedInHierarchy();
    }



    private static double mouseX = 0;
    public static double getMouseX() {
        return mouseX;
    }
    public static void setMouseX(double mouseX) {
        Main.mouseX = mouseX;
    }


    private static double mouseY = 0;
    public static double getMouseY() {
        return mouseY;
    }
    public static void setMouseY(double mouseY) {
        Main.mouseY = mouseY;
    }


    private static EditorAttribute[] oldAttributes;
    public static EditorAttribute[] getOldAttributes() {
        return oldAttributes;
    }
    public static void setOldAttributes(EditorAttribute[] oldAttributes) {
        Main.oldAttributes = oldAttributes;
    }


    private static EditorObject oldSelected;
    public static EditorObject getOldSelected() {
        return oldSelected;
    }
    public static void setOldSelected(EditorObject oldSelected) {
        Main.oldSelected = oldSelected;
    }


    private static DragSettings dragSettings = new DragSettings(DragSettings.NONE);
    public static DragSettings getDragSettings() {
        return dragSettings;
    }
    public static void setDragSettings(DragSettings dragSettings) {
        Main.dragSettings = dragSettings;
    }




    public static void goToSelectedInHierarchy() {
        if (LevelManager.getLevel().getSelected() != null && LevelManager.getLevel().getSelected().getParent() != null) {
            if (LevelManager.getLevel().getSelected().getAbsoluteParent() instanceof ResourceManifest) {
                FXHierarchy.getHierarchy().setRoot(LevelManager.getLevel().getSelected().getAbsoluteParent().getChildren().get(0).getTreeItem());
                FXHierarchy.getHierarchy().setShowRoot(true);
            } else if (LevelManager.getLevel().getSelected().getAbsoluteParent() instanceof TextStrings) {
                FXHierarchy.getHierarchy().setRoot(LevelManager.getLevel().getSelected().getAbsoluteParent().getTreeItem());
                FXHierarchy.getHierarchy().setShowRoot(true);
            } else {
                FXHierarchy.getHierarchy().setRoot(LevelManager.getLevel().getSelected().getAbsoluteParent().getTreeItem());
                FXHierarchy.getHierarchy().setShowRoot(true);
            }

            switch (LevelManager.getLevel().getSelected().getClass().getPackage().getName()) {
                case "com.WorldOfGoo.Scene" -> Main.hierarchySwitcherButtons.getSelectionModel().select(0);
                case "com.WorldOfGoo.Level" -> Main.hierarchySwitcherButtons.getSelectionModel().select(1);
                case "com.WorldOfGoo.Resrc" -> Main.hierarchySwitcherButtons.getSelectionModel().select(2);
                case "com.WorldOfGoo.Text" -> Main.hierarchySwitcherButtons.getSelectionModel().select(3);
                case "com.WorldOfGoo.Addin" -> Main.hierarchySwitcherButtons.getSelectionModel().select(4);
            }
        }
    }

    public static void changeTableView(EditorObject obj) {
        if (obj == null) {
            FXPropertiesView.getPropertiesView().setRoot(null);
        } else {
            FXPropertiesView.getPropertiesView().setRoot(obj.getPropertiesTreeItem());
        }
    }

    public static VBox viewPane;
    public static VBox getViewPane() {
        return viewPane;
    }
    public static void setViewPane(VBox viewPane) {
        Main.viewPane = viewPane;
    }

    @Override
    public void start(Stage stage) {
        Initializer.start(stage, launchArguments);
    }

    public static final TabPane hierarchySwitcherButtons = FXHierarchy.hierarchySwitcherButtons();



    public static void resumeLevelClosing() {
        TabPane levelSelectPane = FXLevelSelectPane.getLevelSelectPane();
        if (levelSelectPane.getTabs().size() == 0) {
            stage.close();
        } else {
            try {
                LevelTab levelTab = (LevelTab) levelSelectPane.getTabs().get(levelSelectPane.getTabs().size() - 1);
                if (levelTab.getLevel().getEditingStatus() == WorldLevel.UNSAVED_CHANGES) {
                    Alarms.closeTabMessage2(levelTab, levelTab.getLevel());
                } else {
                    levelTab.getTabPane().getTabs().remove(levelTab);
                    resumeLevelClosing();
                }
            } catch (Exception e) {
                Alarms.errorMessage(e);
            }
        }
    }

    public static void main(String[] args) {
        Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> {
            Alarms.errorMessage(throwable);
        });
        launchArguments = args;
        launch();
    }

}