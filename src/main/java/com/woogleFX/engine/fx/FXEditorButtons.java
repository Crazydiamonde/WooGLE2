package com.woogleFX.engine.fx;

import com.woogleFX.editorObjects.objectCreators.ObjectCreator;
import com.woogleFX.editorObjects._Ball;
import com.woogleFX.engine.SelectionManager;
import com.woogleFX.engine.gui.Alarms;
import com.woogleFX.file.FileManager;
import com.woogleFX.file.resourceManagers.GlobalResourceManager;
import com.woogleFX.functions.*;
import com.woogleFX.editorObjects.objectCreators.ObjectAdder;
import com.woogleFX.file.resourceManagers.BallManager;
import com.woogleFX.functions.undoHandling.UndoManager;
import com.woogleFX.engine.gui.PaletteReconfigurator;
import com.woogleFX.editorObjects.EditorObject;
import com.woogleFX.structures.GameVersion;
import com.worldOfGoo.ball.Part;
import com.worldOfGoo.resrc.Font;
import com.worldOfGoo.resrc.ResrcImage;
import com.worldOfGoo.resrc.Sound;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.IOException;

public class FXEditorButtons {

    private static class DelayedTooltip extends Tooltip {
        // Tooltip with a shorter delay than the default
        public DelayedTooltip(String text) {
            super(text);
            setShowDelay(javafx.util.Duration.millis(150));
        }
    }


    private static ToolBar functionsToolbar;


    private static ToolBar oldGooballsToolbar;
    public static ToolBar getOldGooballsToolbar() {
        return oldGooballsToolbar;
    }


    private static ToolBar newGooballsToolbar;
    public static ToolBar getNewGooballsToolbar() {
        return newGooballsToolbar;
    }


    private static ToolBar nullGooballsToolbar;
    public static ToolBar getNullGooballsToolbar() {
        return nullGooballsToolbar;
    }


    private static ToolBar addObjectsToolbar;


    private static void setIcon(Button button, String pathString) {
        try {
            button.setGraphic(new ImageView(FileManager.getIcon(pathString)));
        } catch (FileNotFoundException ignored) {
            button.setGraphic(null);
        }
    }



    public static Button createTemplateForBall(int size, _Ball ball) {

        /*
        double minX = 0;
        double minY = 0;
        double maxX = 0;
        double maxY = 0;

        for (EditorObject editorObject : ball.getObjects()) {
            String state = "standing";

            if (editorObject instanceof Part part) {

                boolean ok = false;

                if (part.getAttribute("state").stringValue().isEmpty()) {
                    ok = true;
                } else {
                    String word = part.getAttribute("state").stringValue();
                    while (word.contains(",")) {
                        if (word.substring(0, word.indexOf(",")).equals(state)) {
                            ok = true;
                            break;
                        }
                        word = word.substring(word.indexOf(",") + 1);
                    }
                    if (word.equals(state)) {
                        ok = true;
                    }
                }
                if (ok) {
                    double lowX;
                    double highX;
                    double lowY;
                    double highY;

                    String x = part.getAttribute("x").stringValue();
                    String y = part.getAttribute("y").stringValue();

                    double scale = Double.parseDouble(part.getAttribute("scale").stringValue());

                    if (x.contains(",")) {
                        lowX = Double.parseDouble(x.substring(0, x.indexOf(",")));
                        highX = Double.parseDouble(x.substring(x.indexOf(",") + 1));
                    } else {
                        lowX = Double.parseDouble(x);
                        highX = lowX;
                    }
                    if (y.contains(",")) {
                        lowY = Double.parseDouble(y.substring(0, y.indexOf(",")));
                        highY = Double.parseDouble(y.substring(y.indexOf(",") + 1));
                    } else {
                        lowY = Double.parseDouble(y);
                        highY = lowY;
                    }

                    double myX = 0.5 * (highX - lowX) + lowX;
                    double myY = 0.5 * (highY - lowY) + lowY;

                    if (!part.getImages().isEmpty()) {
                        Image img = part.getImages().get(0);
                        if (img != null) {
                            BufferedImage image = SwingFXUtils.fromFXImage(img, null);

                            double iWidth = image.getWidth() * scale;
                            double iHeight = image.getHeight() * scale;

                            if (myX - iWidth / 2 < minX) {
                                minX = myX - iWidth / 2;
                            }
                            if (-myY - iHeight / 2 < minY) {
                                minY = -myY - iHeight / 2;
                            }
                            if (myX + iWidth / 2 > maxX) {
                                maxX = myX + iWidth / 2;
                            }
                            if (-myY + iHeight / 2 > maxY) {
                                maxY = -myY + iHeight / 2;
                            }
                        }
                    }
                }
            }
        }

        double width = maxX - minX;
        double height = maxY - minY;

        Button idk = new Button();

        if (width > 0 && height > 0) {

            BufferedImage toWriteOn = new BufferedImage((int) width, (int) height, BufferedImage.TYPE_INT_ARGB);
            Graphics writeGraphics = toWriteOn.getGraphics();

            for (EditorObject editorObject : ball.getObjects()) {

                String state = "standing";

                if (editorObject instanceof Part part) {

                    boolean ok = false;

                    if (part.getAttribute("state").stringValue().isEmpty()) {
                        ok = true;
                    } else {
                        String word = part.getAttribute("state").stringValue();
                        while (word.contains(",")) {
                            if (word.substring(0, word.indexOf(",")).equals(state)) {
                                ok = true;
                                break;
                            }
                            word = word.substring(word.indexOf(",") + 1);
                        }
                        if (word.equals(state)) {
                            ok = true;
                        }
                    }

                    if (ok && !part.getImages().isEmpty()) {
                        Image img = part.getImages().get(0);

                        double scale = part.getAttribute("scale").doubleValue();

                        double lowX;
                        double highX;
                        double lowY;
                        double highY;

                        String x = part.getAttribute("x").stringValue();
                        String y = part.getAttribute("y").stringValue();

                        if (x.contains(",")) {
                            lowX = Double.parseDouble(x.substring(0, x.indexOf(",")));
                            highX = Double.parseDouble(x.substring(x.indexOf(",") + 1));
                        } else {
                            lowX = Double.parseDouble(x);
                            highX = lowX;
                        }
                        if (y.contains(",")) {
                            lowY = Double.parseDouble(y.substring(0, y.indexOf(",")));
                            highY = Double.parseDouble(y.substring(y.indexOf(",") + 1));
                        } else {
                            lowY = Double.parseDouble(y);
                            highY = lowY;
                        }

                        double myX = 0.5 * (highX - lowX) + lowX;
                        double myY = 0.5 * (highY - lowY) + lowY;

                        if (myY == 0) {
                            myY = -0;
                        }

                        if (img != null) {

                            double screenX = myX + toWriteOn.getWidth() / 2.0 - img.getWidth() * scale / 2;
                            double screenY = -myY + toWriteOn.getHeight() / 2.0 - img.getHeight() * scale / 2;

                            writeGraphics.drawImage(SwingFXUtils.fromFXImage(img, null), (int) screenX, (int) screenY,
                                    (int) (img.getWidth() * scale), (int) (img.getHeight() * scale), null);

                            if (part.getPupilImage() != null) {
                                Image pupilImage = part.getPupilImage();

                                double screenX2 = myX + toWriteOn.getWidth() / 2.0 - pupilImage.getWidth() * scale / 2;
                                double screenY2 = -myY + toWriteOn.getHeight() / 2.0
                                        - pupilImage.getHeight() * scale / 2;

                                writeGraphics.drawImage(SwingFXUtils.fromFXImage(pupilImage, null), (int) screenX2,
                                        (int) screenY2, (int) (pupilImage.getWidth() * scale),
                                        (int) (pupilImage.getHeight() * scale), null);
                            }
                        }
                    }
                }
            }

            double scaleFactor = (double) size / Math.max(toWriteOn.getWidth(), toWriteOn.getHeight());

            java.awt.Image tmp = toWriteOn.getScaledInstance((int) (toWriteOn.getWidth() * scaleFactor),
                    (int) (toWriteOn.getHeight() * scaleFactor), java.awt.Image.SCALE_SMOOTH);
            BufferedImage dimg = new BufferedImage((int) (toWriteOn.getWidth() * scaleFactor),
                    (int) (toWriteOn.getHeight() * scaleFactor), BufferedImage.TYPE_INT_ARGB);

            Graphics2D g2d = dimg.createGraphics();
            g2d.drawImage(tmp, 0, 0, null);
            g2d.dispose();

            idk.setGraphic(new ImageView(SwingFXUtils.toFXImage(dimg, null)));
        }

        idk.setPrefSize(size, size);
        idk.setOnAction(e -> {

            String name = ball.getObjects().get(0).getAttribute("name").stringValue();

            EditorObject ballInstance = ObjectCreator.create("BallInstance", LevelManager.getLevel().getLevelObject());
            ballInstance.setAttribute("type", name);

            LevelManager.getLevel().getLevel().add(ballInstance);

            ObjectAdder.addAnything(ballInstance);

        });
        return idk;

         */
        return null;
    }

    public static void addBallsTo() {
        int size = 18;
        int i = 0;
        for (String paletteBall : PaletteManager.getPaletteBalls()) {

            GameVersion version = PaletteManager.getPaletteVersions().get(i);

            boolean alreadyHasBall = false;
            for (_Ball _ball : BallManager.getImportedBalls()) {
                if (_ball.getObjects().get(0).getAttribute("name").stringValue().equals(paletteBall)) {
                    alreadyHasBall = true;
                    break;
                }
            }

            if (!alreadyHasBall) {
                _Ball _ball;
                try {
                    _ball = FileManager.openBall(paletteBall, version);
                } catch (ParserConfigurationException | SAXException | IOException e) {
                    Alarms.errorMessage(e);
                    return;
                }

                if (_ball != null) {
                    _ball.setVersion(version);
                    BallManager.getImportedBalls().add(_ball);
                }
            }

            for (_Ball ball : BallManager.getImportedBalls()) {

                if (ball.getObjects().get(0).getAttribute("name").stringValue().equals(paletteBall)
                        && ball.getVersion() == PaletteManager.getPaletteVersions().get(i)) {
                    Button button = createTemplateForBall(size, ball);
                    //button.setTooltip(new DelayedTooltip("Add " + ball.getObjects().get(0).getAttribute("name").stringValue()));
                    //if (ball.getVersion() == GameVersion.OLD) {
                    //    oldGooballsToolbar.getItems().add(button);
                    //} else {
                    //    newGooballsToolbar.getItems().add(button);
                    //}
                }
            }
            i++;
        }
    }


    private static final Button buttonNewOld = new Button();
    private static final Button buttonNewNew = new Button();
    private static final Button buttonOpenOld = new Button();
    private static final Button buttonOpenNew = new Button();
    private static final Button buttonClone = new Button();
    private static final Button buttonSave = new Button();
    private static final Button buttonSaveAll = new Button();
    private static final Button buttonSaveAndPlay = new Button();
    private static final Button buttonExport = new Button();
    private static final Button buttonDummyExport = new Button();

    private static void level(ToolBar toolBar) {

        String prefix = "ButtonIcons\\Level\\";

        setIcon(buttonNewOld, prefix + "new_lvl_old.png");
        buttonNewOld.setOnAction(e -> LevelLoader.newLevel(GameVersion.OLD));
        buttonNewOld.setTooltip(new DelayedTooltip("New Level (1.3)"));
        toolBar.getItems().add(buttonNewOld);

        setIcon(buttonNewNew, prefix + "new_lvl_new.png");
        buttonNewNew.setOnAction(e -> LevelLoader.newLevel(GameVersion.NEW));
        buttonNewNew.setTooltip(new DelayedTooltip("New Level (1.5)"));
        toolBar.getItems().add(buttonNewNew);

        toolBar.getItems().add(new Separator());

        setIcon(buttonOpenOld, prefix + "open_lvl_old.png");
        buttonOpenOld.setOnAction(e -> LevelLoader.openLevel(GameVersion.OLD));
        buttonOpenOld.setTooltip(new DelayedTooltip("Open Level (1.3)"));
        toolBar.getItems().add(buttonOpenOld);

        setIcon(buttonOpenNew, prefix + "open_lvl_new.png");
        buttonOpenNew.setOnAction(e -> LevelLoader.openLevel(GameVersion.NEW));
        buttonOpenNew.setTooltip(new DelayedTooltip("Open Level (1.5)"));
        toolBar.getItems().add(buttonOpenNew);

        toolBar.getItems().add(new Separator());

        setIcon(buttonClone, prefix + "clone_lvl.png");
        buttonClone.setOnAction(e -> LevelLoader.cloneLevel());
        buttonClone.setTooltip(new DelayedTooltip("Clone Level"));
        toolBar.getItems().add(buttonClone);

        setIcon(buttonSave, prefix + "save.png");
        buttonSave.setOnAction(e -> LevelUpdater.saveLevel(LevelManager.getLevel()));
        buttonSave.setTooltip(new DelayedTooltip("Save Level"));
        toolBar.getItems().add(buttonSave);

        setIcon(buttonSaveAll, prefix + "save_all.png");
        buttonSaveAll.setOnAction(e -> LevelUpdater.saveAll());
        buttonSaveAll.setTooltip(new DelayedTooltip("Save All Levels"));
        toolBar.getItems().add(buttonSaveAll);

        setIcon(buttonSaveAndPlay, prefix + "play.png");
        buttonSaveAndPlay.setOnAction(e -> LevelUpdater.playLevel(LevelManager.getLevel()));
        buttonSaveAndPlay.setTooltip(new DelayedTooltip("Save and Play Level on Level Version"));
        toolBar.getItems().add(buttonSaveAndPlay);

        toolBar.getItems().add(new Separator());

        setIcon(buttonExport, prefix + "make_goomod.png");
        buttonExport.setOnAction(e -> LevelUpdater.exportLevel(LevelManager.getLevel(), true));
        buttonExport.setTooltip(new DelayedTooltip("Export Level"));
        toolBar.getItems().add(buttonExport);

        setIcon(buttonDummyExport, prefix + "make_dummy_goomod.png");
        buttonDummyExport.setOnAction(e -> LevelUpdater.exportLevel(LevelManager.getLevel(), false));
        buttonDummyExport.setTooltip(new DelayedTooltip("Export Level Without Addin Info"));
        toolBar.getItems().add(buttonDummyExport);

    }


    public static final Button buttonUndo = new Button();
    public static final Button buttonRedo = new Button();
    private static final Button buttonCut = new Button();
    private static final Button buttonCopy = new Button();
    private static final Button buttonPaste = new Button();
    private static final Button buttonDelete = new Button();

    private static void edit(ToolBar toolBar) {

        String prefix = "ButtonIcons\\Edit\\";

        setIcon(buttonUndo, prefix + "undo.png");
        buttonUndo.setOnAction(e -> UndoManager.undo());
        buttonUndo.setTooltip(new DelayedTooltip("Undo"));
        toolBar.getItems().add(buttonUndo);

        setIcon(buttonRedo, prefix + "redo.png");
        buttonRedo.setOnAction(e -> UndoManager.redo());
        buttonRedo.setTooltip(new DelayedTooltip("Redo"));
        toolBar.getItems().add(buttonRedo);

        toolBar.getItems().add(new Separator());

        setIcon(buttonCut, prefix + "cut.png");
        buttonCut.setOnAction(e -> ClipboardManager.cut());
        buttonCut.setTooltip(new DelayedTooltip("Cut"));
        toolBar.getItems().add(buttonCut);

        setIcon(buttonCopy, prefix + "copy.png");
        buttonCopy.setOnAction(e -> ClipboardManager.copy());
        buttonCopy.setTooltip(new DelayedTooltip("Copy"));
        toolBar.getItems().add(buttonCopy);

        setIcon(buttonPaste, prefix + "paste.png");
        buttonPaste.setOnAction(e -> ClipboardManager.paste());
        buttonPaste.setTooltip(new DelayedTooltip("Paste"));
        toolBar.getItems().add(buttonPaste);

        toolBar.getItems().add(new Separator());

        setIcon(buttonDelete, prefix + "delete.png");
        buttonDelete.setOnAction(e -> ObjectManager.delete(LevelManager.getLevel()));
        buttonDelete.setTooltip(new DelayedTooltip("Delete"));
        toolBar.getItems().add(buttonDelete);

        toolBar.getItems().add(new Separator());

        setIcon(buttonSelectMoveAndResize, prefix + "selection_mode.png");
        buttonSelectMoveAndResize.setOnAction(e -> SelectionManager.selectionMode());
        buttonSelectMoveAndResize.setStyle("-fx-background-color: #9999ff;"); // Highlighted by default
        buttonSelectMoveAndResize.setTooltip(new DelayedTooltip("Select, Move and Resize"));
        toolBar.getItems().add(buttonSelectMoveAndResize);

        setIcon(buttonStrandMode, prefix + "strand_mode.png");
        buttonStrandMode.setOnAction(e -> SelectionManager.strandMode());
        buttonStrandMode.setTooltip(new DelayedTooltip("Place Strands"));
        toolBar.getItems().add(buttonStrandMode);

    }


    private static final Button buttonUpdateLevelResources = new Button();
    private static final Button buttonImportImages = new Button();
    private static final Button buttonAddTextResource = new Button();
    private static final Button buttonCleanResources = new Button();
    private static final Button buttonSetMusic = new Button();
    private static final Button buttonSetLoopsound = new Button();
    public static final Button buttonSelectMoveAndResize = new Button();
    public static final Button buttonStrandMode = new Button();

    private static void resources(ToolBar toolBar) {

        String prefix = "ButtonIcons\\Resources\\";

        setIcon(buttonUpdateLevelResources, prefix + "update_level_resources.png");
        buttonUpdateLevelResources.setOnAction(e -> LevelResourceManager.updateLevelResources(LevelManager.getLevel()));
        buttonUpdateLevelResources.setTooltip(new DelayedTooltip("Update Level Resources"));
        toolBar.getItems().add(buttonUpdateLevelResources);

        setIcon(buttonImportImages, prefix + "import_img.png");
        buttonImportImages.setOnAction(e -> LevelResourceImporter.importImages(LevelManager.getLevel()));
        buttonImportImages.setTooltip(new DelayedTooltip("Import Images"));
        toolBar.getItems().add(buttonImportImages);

        setIcon(buttonAddTextResource, prefix + "add_text_resource.png");
        buttonAddTextResource.setOnAction(e -> LevelResourceManager.newTextResource(LevelManager.getLevel()));
        buttonAddTextResource.setTooltip(new DelayedTooltip("Add Text Resource"));
        toolBar.getItems().add(buttonAddTextResource);

        toolBar.getItems().add(new Separator());

        setIcon(buttonCleanResources, prefix + "clean_level_resources.png");
        buttonCleanResources.setOnAction(e -> LevelResourceManager.cleanLevelResources(LevelManager.getLevel()));
        buttonCleanResources.setTooltip(new DelayedTooltip("Clean Level Resources"));
        toolBar.getItems().add(buttonCleanResources);

        toolBar.getItems().add(new Separator());

        setIcon(buttonSetMusic, prefix + "import_music.png");
        buttonSetMusic.setOnAction(e -> LevelResourceImporter.importMusic(LevelManager.getLevel()));
        buttonSetMusic.setTooltip(new DelayedTooltip("Set Music"));
        toolBar.getItems().add(buttonSetMusic);

        setIcon(buttonSetLoopsound, prefix + "import_soundloop.png");
        buttonSetLoopsound.setOnAction(e -> LevelResourceImporter.importLoopsound(LevelManager.getLevel()));
        buttonSetLoopsound.setTooltip(new DelayedTooltip("Set Loop Sound"));
        toolBar.getItems().add(buttonSetLoopsound);

    }


    private static final Button buttonShowHideCamera = new Button();
    private static final Button buttonShowHideForcefields = new Button();
    private static final Button buttonShowHideGeometry = new Button();
    private static final Button buttonShowHideGraphics = new Button();
    private static final Button buttonShowHideGoos = new Button();
    private static final Button buttonShowHideParticles = new Button();
    private static final Button buttonShowHideLabels = new Button();
    private static final Button buttonShowHideAnim = new Button();
    private static final Button buttonShowHideSceneBGColor = new Button();
    public static void cameraGraphic(Image image) {
        buttonShowHideCamera.setGraphic(new ImageView(image));
    }
    public static void forcefieldsGraphic(Image image) {
        buttonShowHideForcefields.setGraphic(new ImageView(image));
    }
    public static void geometryGraphic(Image image) {
        buttonShowHideGeometry.setGraphic(new ImageView(image));
    }
    public static void graphicsGraphic(Image image) {
        buttonShowHideGraphics.setGraphic(new ImageView(image));
    }
    public static void goosGraphic(Image image) {
        buttonShowHideGoos.setGraphic(new ImageView(image));
    }
    public static void particlesGraphic(Image image) {
        buttonShowHideParticles.setGraphic(new ImageView(image));
    }
    public static void labelsGraphic(Image image) {
        buttonShowHideLabels.setGraphic(new ImageView(image));
    }
    public static void animGraphic(Image image) {
        buttonShowHideAnim.setGraphic(new ImageView(image));
    }
    public static void sceneBGGraphic(Image image) {
        buttonShowHideSceneBGColor.setGraphic(new ImageView(image));
    }

    private static void showHide(ToolBar toolBar) {

        String prefix = "ButtonIcons\\AddObject\\";

        setIcon(buttonShowHideCamera, prefix + "showhide_cam.png");
        buttonShowHideCamera.setOnAction(e -> VisibilityManager.showHideCameras());
        buttonShowHideCamera.setTooltip(new DelayedTooltip("Show/Hide Camera"));
        toolBar.getItems().add(buttonShowHideCamera);

        setIcon(buttonShowHideForcefields, prefix + "showhide_forcefields.png");
        buttonShowHideForcefields.setOnAction(e -> VisibilityManager.showHideForcefields());
        buttonShowHideForcefields.setTooltip(new DelayedTooltip("Show/Hide Force Fields"));
        toolBar.getItems().add(buttonShowHideForcefields);

        setIcon(buttonShowHideGeometry, prefix + "showhide_geometry.png");
        buttonShowHideGeometry.setOnAction(e -> VisibilityManager.showHideGeometry());
        buttonShowHideGeometry.setTooltip(new DelayedTooltip("Show/Hide Geometry"));
        toolBar.getItems().add(buttonShowHideGeometry);

        setIcon(buttonShowHideGraphics, prefix + "showhide_images.png");
        buttonShowHideGraphics.setOnAction(e -> VisibilityManager.showHideGraphics());
        buttonShowHideGraphics.setTooltip(new DelayedTooltip("Show/Hide Graphics"));
        toolBar.getItems().add(buttonShowHideGraphics);

        setIcon(buttonShowHideGoos, prefix + "showhide_goobs.png");
        buttonShowHideGoos.setOnAction(e -> VisibilityManager.showHideGoos());
        buttonShowHideGoos.setTooltip(new DelayedTooltip("Show/Hide Goo Balls"));
        toolBar.getItems().add(buttonShowHideGoos);

        setIcon(buttonShowHideParticles, prefix + "showhide_particles.png");
        buttonShowHideParticles.setOnAction(e -> VisibilityManager.showHideParticles());
        buttonShowHideParticles.setTooltip(new DelayedTooltip("Show/Hide Particles"));
        toolBar.getItems().add(buttonShowHideParticles);

        setIcon(buttonShowHideLabels, prefix + "showhide_labels.png");
        buttonShowHideLabels.setOnAction(e -> VisibilityManager.showHideLabels());
        buttonShowHideLabels.setTooltip(new DelayedTooltip("Show/Hide Labels"));
        toolBar.getItems().add(buttonShowHideLabels);

        setIcon(buttonShowHideAnim, prefix + "showhide_anim.png");
        buttonShowHideAnim.setOnAction(e -> VisibilityManager.showHideAnim());
        buttonShowHideAnim.setTooltip(new DelayedTooltip("Show/Hide Animations"));
        toolBar.getItems().add(buttonShowHideAnim);

        setIcon(buttonShowHideSceneBGColor, prefix + "showhide_scenebgcolor.png");
        buttonShowHideSceneBGColor.setOnAction(e -> VisibilityManager.showHideSceneBGColor());
        buttonShowHideSceneBGColor.setTooltip(new DelayedTooltip("Show/Hide Scene Background Color"));
        toolBar.getItems().add(buttonShowHideSceneBGColor);

    }


    private static final Button addLineButton = new Button();
    private static final Button addRectangleButton = new Button();
    private static final Button addCircleButton = new Button();
    private static final Button addSceneLayerButton = new Button();
    private static final Button addCompGeomButton = new Button();
    private static final Button addHingeButton = new Button();
    private static final Button autoPipeButton = new Button();
    private static final Button addVertexButton = new Button();
    private static final Button addFireButton = new Button();
    private static final Button addLinearFFButton = new Button();
    private static final Button addRadialFFButton = new Button();
    private static final Button addParticlesButton = new Button();
    private static final Button addSignpostButton = new Button();
    private static final Button addLabelButton = new Button();

    private static void addObjects(ToolBar toolBar) {

        String prefix = "ButtonIcons\\AddObject\\";

        setIcon(addLineButton, prefix + "line.png");
        addLineButton.setOnAction(e -> ObjectAdder.addObject("line"));
        addLineButton.setTooltip(new DelayedTooltip("Add Line"));
        toolBar.getItems().add(addLineButton);

        setIcon(addRectangleButton, prefix + "rectangle.png");
        addRectangleButton.setOnAction(e -> ObjectAdder.addObject("rectangle"));
        addRectangleButton.setTooltip(new DelayedTooltip("Add Rectangle"));
        toolBar.getItems().add(addRectangleButton);

        setIcon(addCircleButton, prefix + "circle.png");
        addCircleButton.setOnAction(e -> ObjectAdder.addObject("circle"));
        addCircleButton.setTooltip(new DelayedTooltip("Add Circle"));
        toolBar.getItems().add(addCircleButton);

        setIcon(addSceneLayerButton, prefix + "SceneLayer.png");
        addSceneLayerButton.setOnAction(e -> ObjectAdder.addObject("SceneLayer"));
        addSceneLayerButton.setTooltip(new DelayedTooltip("Add Scene Layer"));
        toolBar.getItems().add(addSceneLayerButton);

        setIcon(addCompGeomButton, prefix + "compositegeom.png");
        addCompGeomButton.setOnAction(e -> ObjectAdder.addObject("compositegeom"));
        addCompGeomButton.setTooltip(new DelayedTooltip("Add Composite Geometry"));
        toolBar.getItems().add(addCompGeomButton);

        setIcon(addHingeButton, prefix + "hinge.png");
        addHingeButton.setOnAction(e -> ObjectAdder.addObject("hinge"));
        addHingeButton.setTooltip(new DelayedTooltip("Add Hinge"));
        toolBar.getItems().add(addHingeButton);

        toolBar.getItems().add(new Separator());

        setIcon(autoPipeButton, prefix + "pipe.png");
        autoPipeButton.setOnAction(e -> ObjectAdder.autoPipe());
        autoPipeButton.setTooltip(new DelayedTooltip("Auto Pipe"));
        toolBar.getItems().add(autoPipeButton);

        setIcon(addVertexButton, prefix + "Vertex.png");
        addVertexButton.setOnAction(e -> ObjectAdder.addObject("Vertex"));
        addVertexButton.setTooltip(new DelayedTooltip("Add Vertex"));
        toolBar.getItems().add(addVertexButton);

        toolBar.getItems().add(new Separator());

        setIcon(addFireButton, prefix + "fire.png");
        addFireButton.setOnAction(e -> ObjectAdder.addObject("fire"));
        addFireButton.setTooltip(new DelayedTooltip("Add Fire"));
        toolBar.getItems().add(addFireButton);

        setIcon(addLinearFFButton, prefix + "linearforcefield.png");
        addLinearFFButton.setOnAction(e -> ObjectAdder.addObject("linearforcefield"));
        addLinearFFButton.setTooltip(new DelayedTooltip("Add Linear Force Field"));
        toolBar.getItems().add(addLinearFFButton);

        setIcon(addRadialFFButton, prefix + "radialforcefield.png");
        addRadialFFButton.setOnAction(e -> ObjectAdder.addObject("radialforcefield"));
        addRadialFFButton.setTooltip(new DelayedTooltip("Add Radial Force Field"));
        toolBar.getItems().add(addRadialFFButton);

        setIcon(addParticlesButton, prefix + "particles.png");
        addParticlesButton.setOnAction(e -> ObjectAdder.addObject("particles"));
        addParticlesButton.setTooltip(new DelayedTooltip("Add Particles"));
        toolBar.getItems().add(addParticlesButton);

        toolBar.getItems().add(new Separator());

        setIcon(addSignpostButton, prefix + "signpost.png");
        addSignpostButton.setOnAction(e -> ObjectAdder.addObject("signpost"));
        addSignpostButton.setTooltip(new DelayedTooltip("Add Signpost"));
        toolBar.getItems().add(addSignpostButton);

        setIcon(addLabelButton, prefix + "label.png");
        addLabelButton.setOnAction(e -> ObjectAdder.addObject("label"));
        addLabelButton.setTooltip(new DelayedTooltip("Add Label"));
        toolBar.getItems().add(addLabelButton);

    }


    public static void init() {

        VBox vBox = FXContainers.getvBox();

        functionsToolbar = new ToolBar();
        level(functionsToolbar);
        functionsToolbar.getItems().add(new Separator());
        edit(functionsToolbar);
        functionsToolbar.getItems().add(new Separator());
        resources(functionsToolbar);
        functionsToolbar.getItems().add(new Separator());
        showHide(functionsToolbar);
        for (Node node : functionsToolbar.getItems()) node.setDisable(true);
        vBox.getChildren().add(1, functionsToolbar);

        oldGooballsToolbar = new ToolBar();
        oldGooballsToolbar.setMinHeight(27);
        oldGooballsToolbar.setOnMouseClicked(e -> showPaletteConfigurator(e, oldGooballsToolbar));
        newGooballsToolbar = new ToolBar();
        newGooballsToolbar.setMinHeight(27);
        newGooballsToolbar.setOnMouseClicked(e -> showPaletteConfigurator(e, newGooballsToolbar));
        nullGooballsToolbar = new ToolBar();
        nullGooballsToolbar.setMinHeight(27);
        nullGooballsToolbar.setOnMouseClicked(e -> showPaletteConfigurator(e, nullGooballsToolbar));
        addBallsTo();
        vBox.getChildren().add(2, nullGooballsToolbar);

        addObjectsToolbar = new ToolBar();
        addObjects(addObjectsToolbar);
        for (Node node : addObjectsToolbar.getItems()) node.setDisable(true);
        vBox.getChildren().add(3, addObjectsToolbar);

    }


    public static void updateAllButtons() {

        boolean inLevel = LevelManager.getLevel() != null;

        for (Node node : functionsToolbar.getItems()) node.setDisable(!inLevel);
        for (Node node : oldGooballsToolbar.getItems()) node.setDisable(!inLevel);
        for (Node node : newGooballsToolbar.getItems()) node.setDisable(!inLevel);
        for (Node node : nullGooballsToolbar.getItems()) node.setDisable(!inLevel);
        for (Node node : addObjectsToolbar.getItems()) node.setDisable(!inLevel);

        buttonUndo.setDisable(!inLevel || LevelManager.getLevel().undoActions.isEmpty());
        buttonRedo.setDisable(!inLevel || LevelManager.getLevel().redoActions.isEmpty());
        buttonCut.setDisable(!inLevel || LevelManager.getLevel().getSelected() == null);
        buttonCopy.setDisable(!inLevel || LevelManager.getLevel().getSelected() == null);
        buttonPaste.setDisable(!inLevel);
        buttonDelete.setDisable(!inLevel || LevelManager.getLevel().getSelected() == null);

        boolean hasOld = FileManager.hasOldWOG();
        buttonNewOld.setDisable(!hasOld);
        buttonOpenOld.setDisable(!hasOld);

        boolean hasNew = FileManager.hasNewWOG();
        buttonNewNew.setDisable(!hasNew);
        buttonOpenNew.setDisable(!hasNew);

    }


    public static void showPaletteConfigurator(MouseEvent mouseEvent, ToolBar toolbar) {
        if (mouseEvent.getButton() == MouseButton.SECONDARY) {
            ContextMenu contextMenu = new ContextMenu();
            MenuItem menuItem = new MenuItem("Configure Palette...");
            menuItem.setOnAction(actionEvent -> new PaletteReconfigurator().start(new Stage()));
            contextMenu.getItems().add(menuItem);
            if (toolbar != null) {
                toolbar.setContextMenu(contextMenu);
            }
        }
    }

}
