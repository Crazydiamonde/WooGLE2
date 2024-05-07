package com.WooGLEFX.Engine.FX;

import com.WooGLEFX.EditorObjects._Ball;
import com.WooGLEFX.Engine.DelayedTooltip;
import com.WooGLEFX.Engine.Main;
import com.WooGLEFX.File.FileManager;
import com.WooGLEFX.Functions.*;
import com.WooGLEFX.GUI.PaletteReconfigurator;
import com.WooGLEFX.Structures.EditorObject;
import com.WorldOfGoo.Ball.Part;
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
import java.io.IOException;

public class FXEditorButtons {

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


    public static Button buttonNewOld = new Button();
    public static Button buttonNewNew = new Button();
    public static Button buttonOpenOld = new Button();
    public static Button buttonOpenNew = new Button();
    public static Button buttonClone = new Button();
    public static Button buttonSave = new Button();
    public static Button buttonSaveAll = new Button();
    public static Button buttonSaveAndPlay = new Button();
    public static Button buttonExport = new Button();
    public static Button buttonDummyExport = new Button();
    public static Button buttonUndo = new Button();
    public static Button buttonRedo = new Button();
    public static Button buttonCut = new Button();
    public static Button buttonCopy = new Button();
    public static Button buttonPaste = new Button();
    public static Button buttonDelete = new Button();
    public static Button buttonUpdateLevelResources = new Button();
    public static Button buttonImportImages = new Button();
    public static Button buttonAddTextResource = new Button();
    public static Button buttonCleanResources = new Button();
    public static Button buttonSetMusic = new Button();
    public static Button buttonSetLoopsound = new Button();
    public static Button buttonSelectMoveAndResize = new Button();
    public static Button buttonStrandMode = new Button();
    public static Button buttonShowHideCamera = new Button();
    public static Button buttonShowHideForcefields = new Button();
    public static Button buttonShowHideGeometry = new Button();
    public static Button buttonShowHideGraphics = new Button();
    public static Button buttonShowHideGoos = new Button();
    public static Button buttonShowHideParticles = new Button();
    public static Button buttonShowHideLabels = new Button();
    public static Button buttonShowHideAnim = new Button();
    public static Button buttonShowHideSceneBGColor = new Button();

    public static Button addLineButton = new Button();
    public static Button addRectangleButton = new Button();
    public static Button addCircleButton = new Button();
    public static Button addSceneLayerButton = new Button();
    public static Button addCompositegeomButton = new Button();
    public static Button addHingeButton = new Button();
    public static Button autoPipeButton = new Button();
    public static Button addVertexButton = new Button();
    public static Button addFireButton = new Button();
    public static Button addLinearforcefieldButton = new Button();
    public static Button addRadialforcefieldButton = new Button();
    public static Button addParticlesButton = new Button();
    public static Button addSignpostButton = new Button();
    public static Button addLabelButton = new Button();

    public static Button createTemplateForBall(int size, _Ball ball) {

        double minX = 0;
        double minY = 0;
        double maxX = 0;
        double maxY = 0;

        for (EditorObject part : ball.getObjects()) {
            String state = "standing";

            if (part instanceof Part) {

                boolean ok = false;

                if (part.getAttribute("state").equals("")) {
                    ok = true;
                } else {
                    String word = part.getAttribute("state");
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

                    String x = part.getAttribute("x");
                    String y = part.getAttribute("y");

                    double scale = Double.parseDouble(part.getAttribute("scale"));

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

                    if (((Part) part).getImages().size() > 0) {
                        Image img = ((Part) part).getImages().get(0);
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

        if (width > 0 && height > 0) {

            BufferedImage toWriteOn = new BufferedImage((int) width, (int) height, BufferedImage.TYPE_INT_ARGB);
            Graphics writeGraphics = toWriteOn.getGraphics();

            for (EditorObject part : ball.getObjects()) {

                String state = "standing";

                if (part instanceof Part) {

                    boolean ok = false;

                    if (part.getAttribute("state").equals("")) {
                        ok = true;
                    } else {
                        String word = part.getAttribute("state");
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

                    if (ok && ((Part) part).getImages().size() > 0) {
                        Image img = ((Part) part).getImages().get(0);

                        double scale = Double.parseDouble(part.getAttribute("scale"));

                        double lowX;
                        double highX;
                        double lowY;
                        double highY;

                        String x = part.getAttribute("x");
                        String y = part.getAttribute("y");

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

                            if (((Part) part).getPupilImage() != null) {
                                Image pupilImage = ((Part) part).getPupilImage();

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
            Button idk = new Button();

            double scaleFactor = (double) size / Math.max(toWriteOn.getWidth(), toWriteOn.getHeight());

            java.awt.Image tmp = toWriteOn.getScaledInstance((int) (toWriteOn.getWidth() * scaleFactor),
                    (int) (toWriteOn.getHeight() * scaleFactor), java.awt.Image.SCALE_SMOOTH);
            BufferedImage dimg = new BufferedImage((int) (toWriteOn.getWidth() * scaleFactor),
                    (int) (toWriteOn.getHeight() * scaleFactor), BufferedImage.TYPE_INT_ARGB);

            Graphics2D g2d = dimg.createGraphics();
            g2d.drawImage(tmp, 0, 0, null);
            g2d.dispose();

            idk.setGraphic(new ImageView(SwingFXUtils.toFXImage(dimg, null)));
            idk.setOnAction(
                    e -> ObjectAdder.addBall(LevelManager.getLevel().getLevelObject(), ball.getObjects().get(0).getAttribute("name")));
            return idk;
        } else {
            Button idk = new Button();
            idk.setPrefSize(size, size);
            idk.setOnAction(
                    e -> ObjectAdder.addBall(LevelManager.getLevel().getLevelObject(), ball.getObjects().get(0).getAttribute("name")));
            return idk;
        }
    }

    public static void addBallsTo() {
        int size = 18;
        int i = 0;
        for (String paletteBall : FileManager.getPaletteBalls()) {
            for (_Ball ball : Main.getImportedBalls()) {
                if (ball.getObjects().get(0).getAttribute("name").equals(paletteBall)
                        && ball.getVersion() == FileManager.getPaletteVersions().get(i)) {
                    Button button = createTemplateForBall(size, ball);
                    button.setTooltip(new DelayedTooltip("Add " + ball.getObjects().get(0).getAttribute("name")));
                    if (ball.getVersion() == 1.3) {
                        oldGooballsToolbar.getItems().add(button);
                    } else {
                        newGooballsToolbar.getItems().add(button);
                    }
                }
            }
            i++;
        }
    }


    public static void buttons(VBox vBox) throws IOException {

        ToolBar functionsToolbar = new ToolBar();

        buttonNewOld.setGraphic(new ImageView(FileManager.getIcon("ButtonIcons\\Level\\new_lvl_old.png")));
        buttonNewNew.setGraphic(new ImageView(FileManager.getIcon("ButtonIcons\\Level\\new_lvl_new.png")));
        buttonOpenOld.setGraphic(new ImageView(FileManager.getIcon("ButtonIcons\\Level\\open_lvl_old.png")));
        buttonOpenNew.setGraphic(new ImageView(FileManager.getIcon("ButtonIcons\\Level\\open_lvl_new.png")));
        buttonClone.setGraphic(new ImageView(FileManager.getIcon("ButtonIcons\\Level\\clone_lvl.png")));
        buttonSave.setGraphic(new ImageView(FileManager.getIcon("ButtonIcons\\Level\\save.png")));
        buttonSaveAll.setGraphic(new ImageView(FileManager.getIcon("ButtonIcons\\Level\\save_all.png")));
        buttonSaveAndPlay.setGraphic(new ImageView(FileManager.getIcon("ButtonIcons\\Level\\play.png")));
        buttonExport.setGraphic(new ImageView(FileManager.getIcon("ButtonIcons\\Level\\make_goomod.png")));
        buttonDummyExport.setGraphic(new ImageView(FileManager.getIcon("ButtonIcons\\Level\\make_dummy_goomod.png")));
        buttonUndo.setGraphic(new ImageView(FileManager.getIcon("ButtonIcons\\Edit\\undo.png")));
        buttonRedo.setGraphic(new ImageView(FileManager.getIcon("ButtonIcons\\Edit\\redo.png")));
        buttonCut.setGraphic(new ImageView(FileManager.getIcon("ButtonIcons\\Edit\\cut.png")));
        buttonCopy.setGraphic(new ImageView(FileManager.getIcon("ButtonIcons\\Edit\\copy.png")));
        buttonPaste.setGraphic(new ImageView(FileManager.getIcon("ButtonIcons\\Edit\\paste.png")));
        buttonDelete.setGraphic(new ImageView(FileManager.getIcon("ButtonIcons\\Edit\\delete.png")));
        buttonUpdateLevelResources
                .setGraphic(new ImageView(FileManager.getIcon("ButtonIcons\\Resources\\update_level_resources.png")));
        buttonImportImages.setGraphic(new ImageView(FileManager.getIcon("ButtonIcons\\Resources\\import_img.png")));
        buttonAddTextResource
                .setGraphic(new ImageView(FileManager.getIcon("ButtonIcons\\Resources\\add_text_resource.png")));
        buttonCleanResources
                .setGraphic(new ImageView(FileManager.getIcon("ButtonIcons\\Resources\\clean_level_resources.png")));
        buttonSetMusic.setGraphic(new ImageView(FileManager.getIcon("ButtonIcons\\Resources\\import_music.png")));
        buttonSetLoopsound
                .setGraphic(new ImageView(FileManager.getIcon("ButtonIcons\\Resources\\import_soundloop.png")));
        buttonSelectMoveAndResize
                .setGraphic(new ImageView(FileManager.getIcon("ButtonIcons\\Edit\\selection_mode.png")));
        buttonStrandMode.setGraphic(new ImageView(FileManager.getIcon("ButtonIcons\\Edit\\strand_mode.png")));
        buttonShowHideCamera.setGraphic(new ImageView(FileManager.getIcon("ButtonIcons\\ShowHide\\showhide_cam.png")));
        buttonShowHideForcefields
                .setGraphic(new ImageView(FileManager.getIcon("ButtonIcons\\ShowHide\\showhide_forcefields.png")));
        buttonShowHideGeometry
                .setGraphic(new ImageView(FileManager.getIcon("ButtonIcons\\ShowHide\\showhide_geometry.png")));
        buttonShowHideGraphics
                .setGraphic(new ImageView(FileManager.getIcon("ButtonIcons\\ShowHide\\showhide_images.png")));
        buttonShowHideGoos.setGraphic(new ImageView(FileManager.getIcon("ButtonIcons\\ShowHide\\showhide_goobs.png")));
        buttonShowHideParticles
                .setGraphic(new ImageView(FileManager.getIcon("ButtonIcons\\ShowHide\\showhide_particles.png")));
        buttonShowHideLabels
                .setGraphic(new ImageView(FileManager.getIcon("ButtonIcons\\ShowHide\\showhide_labels.png")));
        buttonShowHideAnim.setGraphic(new ImageView(FileManager.getIcon("ButtonIcons\\ShowHide\\showhide_anim.png")));
        buttonShowHideSceneBGColor
                .setGraphic(new ImageView(FileManager.getIcon("ButtonIcons\\ShowHide\\showhide_scenebgcolor.png")));

        buttonNewOld.setOnAction(e -> LevelLoader.newLevel(1.3));
        buttonNewNew.setOnAction(e -> LevelLoader.newLevel(1.5));
        buttonOpenOld.setOnAction(e -> LevelLoader.openLevel(1.3));
        buttonOpenNew.setOnAction(e -> LevelLoader.openLevel(1.5));
        buttonClone.setOnAction(e -> Main.cloneLevel());
        buttonSave.setOnAction(e -> LevelUpdater.saveLevel(LevelManager.getLevel()));
        buttonSaveAll.setOnAction(e -> LevelUpdater.saveAll());
        buttonSaveAndPlay.setOnAction(e -> LevelUpdater.playLevel(LevelManager.getLevel()));
        buttonExport.setOnAction(e -> LevelUpdater.exportLevel(LevelManager.getLevel(), true));
        buttonDummyExport.setOnAction(e -> LevelUpdater.exportLevel(LevelManager.getLevel(), false));
        buttonUndo.setOnAction(e -> UndoManager.undo());
        buttonRedo.setOnAction(e -> UndoManager.redo());
        buttonCut.setOnAction(e -> ClipboardManager.cut());
        buttonCopy.setOnAction(e -> ClipboardManager.copy());
        buttonPaste.setOnAction(e -> ClipboardManager.paste());
        buttonDelete.setOnAction(e -> ObjectManager.delete(LevelManager.getLevel()));
        buttonUpdateLevelResources.setOnAction(e -> LevelResourceManager.updateLevelResources(LevelManager.getLevel()));
        buttonImportImages.setOnAction(e -> LevelResourceManager.importImages(LevelManager.getLevel()));
        buttonAddTextResource.setOnAction(e -> LevelResourceManager.newTextResource(LevelManager.getLevel()));
        buttonCleanResources.setOnAction(e -> LevelResourceManager.cleanLevelResources(LevelManager.getLevel()));
        buttonSetMusic.setOnAction(e -> LevelResourceManager.importMusic(LevelManager.getLevel()));
        buttonSetLoopsound.setOnAction(e -> LevelResourceManager.importLoopsound(LevelManager.getLevel()));
        buttonSelectMoveAndResize.setOnAction(e -> Main.selectionMode());
        buttonSelectMoveAndResize.setStyle("-fx-background-color: #9999ff;"); // Highlighted by default
        buttonStrandMode.setOnAction(e -> Main.strandMode());
        buttonShowHideCamera.setOnAction(e -> VisibilityManager.showHideCameras());
        buttonShowHideForcefields.setOnAction(e -> VisibilityManager.showHideForcefields());
        buttonShowHideGeometry.setOnAction(e -> VisibilityManager.showHideGeometry());
        buttonShowHideGraphics.setOnAction(e -> VisibilityManager.showHideGraphics());
        buttonShowHideGoos.setOnAction(e -> VisibilityManager.showHideGoos());
        buttonShowHideParticles.setOnAction(e -> VisibilityManager.showHideParticles());
        buttonShowHideLabels.setOnAction(e -> VisibilityManager.showHideLabels());
        buttonShowHideAnim.setOnAction(e -> VisibilityManager.showHideAnim());
        buttonShowHideSceneBGColor.setOnAction(e -> VisibilityManager.showHideSceneBGColor());

        buttonNewOld.setTooltip(new DelayedTooltip("New Level (1.3)"));
        buttonNewNew.setTooltip(new DelayedTooltip("New Level (1.5)"));
        buttonOpenOld.setTooltip(new DelayedTooltip("Open Level (1.3)"));
        buttonOpenNew.setTooltip(new DelayedTooltip("Open Level (1.5)"));
        buttonClone.setTooltip(new DelayedTooltip("Clone Level"));
        buttonSave.setTooltip(new DelayedTooltip("Save Level"));
        buttonSaveAll.setTooltip(new DelayedTooltip("Save All Levels"));
        buttonSaveAndPlay.setTooltip(new DelayedTooltip("Save and Play Level on Level Version"));
        buttonExport.setTooltip(new DelayedTooltip("Export Level"));
        buttonDummyExport.setTooltip(new DelayedTooltip("Export Level Without Addin Info"));
        buttonUndo.setTooltip(new DelayedTooltip("Undo"));
        buttonRedo.setTooltip(new DelayedTooltip("Redo"));
        buttonCut.setTooltip(new DelayedTooltip("Cut"));
        buttonCopy.setTooltip(new DelayedTooltip("Copy"));
        buttonPaste.setTooltip(new DelayedTooltip("Paste"));
        buttonDelete.setTooltip(new DelayedTooltip("Delete"));
        buttonUpdateLevelResources.setTooltip(new DelayedTooltip("Update Level Resources"));
        buttonImportImages.setTooltip(new DelayedTooltip("Import Images"));
        buttonAddTextResource.setTooltip(new DelayedTooltip("Add Text Resource"));
        buttonCleanResources.setTooltip(new DelayedTooltip("Clean Level Resources"));
        buttonSetMusic.setTooltip(new DelayedTooltip("Set Music"));
        buttonSetLoopsound.setTooltip(new DelayedTooltip("Set Loop Sound"));
        buttonSelectMoveAndResize.setTooltip(new DelayedTooltip("Select, Move and Resize"));
        buttonStrandMode.setTooltip(new DelayedTooltip("Place Strands"));
        buttonShowHideCamera.setTooltip(new DelayedTooltip("Show/Hide Camera"));
        buttonShowHideForcefields.setTooltip(new DelayedTooltip("Show/Hide Force Fields"));
        buttonShowHideGeometry.setTooltip(new DelayedTooltip("Show/Hide Geometry"));
        buttonShowHideGraphics.setTooltip(new DelayedTooltip("Show/Hide Graphics"));
        buttonShowHideGoos.setTooltip(new DelayedTooltip("Show/Hide Goo Balls"));
        buttonShowHideParticles.setTooltip(new DelayedTooltip("Show/Hide Particles"));
        buttonShowHideLabels.setTooltip(new DelayedTooltip("Show/Hide Labels"));
        buttonShowHideAnim.setTooltip(new DelayedTooltip("Show/Hide Animations"));
        buttonShowHideSceneBGColor.setTooltip(new DelayedTooltip("Show/Hide Scene Background Color"));

        functionsToolbar.getItems().addAll(buttonNewOld, buttonOpenOld, new Separator());
        functionsToolbar.getItems().addAll(buttonNewNew, buttonOpenNew, new Separator());
        functionsToolbar.getItems().addAll(buttonClone, buttonSave, buttonSaveAll, buttonSaveAndPlay,
                new Separator());
        functionsToolbar.getItems().addAll(buttonDummyExport, buttonExport, new Separator());
        functionsToolbar.getItems().addAll(buttonUndo, buttonRedo, new Separator());
        functionsToolbar.getItems().addAll(buttonCut, buttonCopy, buttonPaste, new Separator());
        functionsToolbar.getItems().addAll(buttonDelete, new Separator());
        functionsToolbar.getItems().addAll(buttonUpdateLevelResources, buttonImportImages, buttonAddTextResource,
                new Separator());
        functionsToolbar.getItems().addAll(buttonCleanResources, new Separator());
        functionsToolbar.getItems().addAll(buttonSetMusic, buttonSetLoopsound, new Separator());
        functionsToolbar.getItems().addAll(buttonSelectMoveAndResize, buttonStrandMode, new Separator());
        functionsToolbar.getItems().addAll(buttonShowHideCamera, buttonShowHideForcefields, buttonShowHideGeometry,
                buttonShowHideGraphics, buttonShowHideGoos, buttonShowHideParticles, buttonShowHideLabels,
                buttonShowHideAnim, buttonShowHideSceneBGColor, new Separator());

        for (Node node : functionsToolbar.getItems()) {
            node.setDisable(true);
        }

        vBox.getChildren().add(functionsToolbar);

        oldGooballsToolbar = new ToolBar();
        newGooballsToolbar = new ToolBar();
        nullGooballsToolbar = new ToolBar();

        oldGooballsToolbar.setMinHeight(27);
        newGooballsToolbar.setMinHeight(27);
        nullGooballsToolbar.setMinHeight(27);

        addBallsTo();

        oldGooballsToolbar.setOnMouseClicked(e -> showPaletteConfigurator(e, oldGooballsToolbar));
        newGooballsToolbar.setOnMouseClicked(e -> showPaletteConfigurator(e, newGooballsToolbar));
        nullGooballsToolbar.setOnMouseClicked(e -> showPaletteConfigurator(e, nullGooballsToolbar));

        ToolBar addObjectsToolbar = new ToolBar();

        addLineButton.setGraphic(new ImageView(FileManager.getIcon("ButtonIcons\\AddObject\\line.png")));
        addRectangleButton.setGraphic(new ImageView(FileManager.getIcon("ButtonIcons\\AddObject\\rectangle.png")));
        addCircleButton.setGraphic(new ImageView(FileManager.getIcon("ButtonIcons\\AddObject\\circle.png")));
        addSceneLayerButton.setGraphic(new ImageView(FileManager.getIcon("ButtonIcons\\AddObject\\SceneLayer.png")));
        addCompositegeomButton
                .setGraphic(new ImageView(FileManager.getIcon("ButtonIcons\\AddObject\\compositegeom.png")));
        addHingeButton.setGraphic(new ImageView(FileManager.getIcon("ButtonIcons\\AddObject\\hinge.png")));
        autoPipeButton.setGraphic(new ImageView(FileManager.getIcon("ButtonIcons\\AddObject\\pipe.png")));
        addVertexButton.setGraphic(new ImageView(FileManager.getIcon("ButtonIcons\\AddObject\\Vertex.png")));
        addFireButton.setGraphic(new ImageView(FileManager.getIcon("ButtonIcons\\AddObject\\fire.png")));
        addLinearforcefieldButton
                .setGraphic(new ImageView(FileManager.getIcon("ButtonIcons\\AddObject\\linearforcefield.png")));
        addRadialforcefieldButton
                .setGraphic(new ImageView(FileManager.getIcon("ButtonIcons\\AddObject\\radialforcefield.png")));
        addParticlesButton.setGraphic(new ImageView(FileManager.getIcon("ButtonIcons\\AddObject\\particles.png")));
        addSignpostButton.setGraphic(new ImageView(FileManager.getIcon("ButtonIcons\\AddObject\\signpost.png")));
        addLabelButton.setGraphic(new ImageView(FileManager.getIcon("ButtonIcons\\AddObject\\label.png")));

        addLineButton.setOnAction(e -> ObjectAdder.addLine(LevelManager.getLevel().getSceneObject()));
        addRectangleButton.setOnAction(e -> ObjectAdder.addRectangle(LevelManager.getLevel().getSceneObject()));
        addCircleButton.setOnAction(e -> ObjectAdder.addCircle(LevelManager.getLevel().getSceneObject()));
        addSceneLayerButton.setOnAction(e -> ObjectAdder.addSceneLayer(LevelManager.getLevel().getSceneObject()));
        addCompositegeomButton.setOnAction(e -> ObjectAdder.addCompositegeom(LevelManager.getLevel().getSceneObject()));
        addHingeButton.setOnAction(e -> ObjectAdder.addHinge(LevelManager.getLevel().getSceneObject()));
        autoPipeButton.setOnAction(e -> ObjectAdder.autoPipe());
        addVertexButton.setOnAction(e -> ObjectAdder.addPipeVertex(LevelManager.getLevel().getLevelObject()));
        addFireButton.setOnAction(e -> ObjectAdder.addFire(LevelManager.getLevel().getLevelObject()));
        addLinearforcefieldButton.setOnAction(e -> ObjectAdder.addLinearForcefield(LevelManager.getLevel().getSceneObject()));
        addRadialforcefieldButton.setOnAction(e -> ObjectAdder.addRadialForcefield(LevelManager.getLevel().getSceneObject()));
        addParticlesButton.setOnAction(e -> ObjectAdder.addParticle(LevelManager.getLevel().getSceneObject()));
        addSignpostButton.setOnAction(e -> ObjectAdder.addSign(LevelManager.getLevel().getSceneObject()));
        addLabelButton.setOnAction(e -> ObjectAdder.addLabel(LevelManager.getLevel().getSceneObject()));

        addLineButton.setTooltip(new DelayedTooltip("Add Line"));
        addRectangleButton.setTooltip(new DelayedTooltip("Add Rectangle"));
        addCircleButton.setTooltip(new DelayedTooltip("Add Circle"));
        addSceneLayerButton.setTooltip(new DelayedTooltip("Add Scene Layer"));
        addCompositegeomButton.setTooltip(new DelayedTooltip("Add Composite Geometry"));
        addHingeButton.setTooltip(new DelayedTooltip("Add Hinge"));
        autoPipeButton.setTooltip(new DelayedTooltip("Auto Pipe"));
        addVertexButton.setTooltip(new DelayedTooltip("Add Vertex"));
        addFireButton.setTooltip(new DelayedTooltip("Add Fire"));
        addLinearforcefieldButton.setTooltip(new DelayedTooltip("Add Linear Force Field"));
        addRadialforcefieldButton.setTooltip(new DelayedTooltip("Add Radial Force Field"));
        addParticlesButton.setTooltip(new DelayedTooltip("Add Particles"));
        addSignpostButton.setTooltip(new DelayedTooltip("Add Signpost"));
        addLabelButton.setTooltip(new DelayedTooltip("Add Label"));

        addObjectsToolbar.getItems().addAll(addLineButton, addRectangleButton, addCircleButton, addSceneLayerButton,
                addCompositegeomButton, addHingeButton, new Separator());
        addObjectsToolbar.getItems().addAll(autoPipeButton, addVertexButton, new Separator());
        addObjectsToolbar.getItems().addAll(addFireButton, addLinearforcefieldButton, addRadialforcefieldButton,
                addParticlesButton, new Separator());
        addObjectsToolbar.getItems().addAll(addSignpostButton, addLabelButton, new Separator());

        for (Node node : addObjectsToolbar.getItems()) {
            node.setDisable(true);
        }

        vBox.getChildren().addAll(nullGooballsToolbar, addObjectsToolbar);

        functionsToolbar.setId("thing");
        nullGooballsToolbar.setId("thing");
        oldGooballsToolbar.setId("thing");
        newGooballsToolbar.setId("thing");
        addObjectsToolbar.setId("thing");
    }

    public static void enableAllButtons(boolean disable) {
        for (int i : new int[] { 1, 3 }) {
            if (Main.getvBox().getChildren().get(i) instanceof ToolBar toolBar) {
                for (Node node : toolBar.getItems()) {
                    node.setDisable(disable);
                }
            }
        }
        for (javafx.scene.control.MenuItem menuItem : FXMenu.editMenu.getItems()) {
            menuItem.setDisable(disable);
        }
        for (javafx.scene.control.MenuItem menuItem : FXMenu.levelMenu.getItems()) {
            menuItem.setDisable(disable);
        }
        for (MenuItem menuItem : FXMenu.resourcesMenu.getItems()) {
            menuItem.setDisable(disable);
        }
        if (!FileManager.isHasOldWOG()) {
            buttonNewOld.setDisable(true);
            buttonOpenOld.setDisable(true);
            buttonSave.setDisable(true);
            buttonClone.setDisable(true);
            if (LevelManager.getLevel() != null) {
                FXMenu.newLevelOldItem.setDisable(true);
                FXMenu.openLevelOldItem.setDisable(true);
                FXMenu.saveLevelItem.setDisable(true);
                FXMenu.cloneLevelItem.setDisable(true);
            } else {
                FXMenu.newLevelOldItem.setDisable(false);
                FXMenu.openLevelOldItem.setDisable(false);
                FXMenu.saveLevelItem.setDisable(false);
                FXMenu.cloneLevelItem.setDisable(false);
            }
        }
        if (!FileManager.isHasNewWOG()) {
            buttonNewNew.setDisable(true);
            buttonOpenNew.setDisable(true);
            buttonSave.setDisable(true);
            buttonClone.setDisable(true);
            if (LevelManager.getLevel() != null) {
                FXMenu.newLevelNewItem.setDisable(true);
                FXMenu.openLevelNewItem.setDisable(true);
                FXMenu.saveLevelItem.setDisable(true);
                FXMenu.cloneLevelItem.setDisable(true);
            } else {
                FXMenu.newLevelNewItem.setDisable(false);
                FXMenu.openLevelNewItem.setDisable(false);
                FXMenu.saveLevelItem.setDisable(false);
                FXMenu.cloneLevelItem.setDisable(false);
            }
        }
        if ((FileManager.isHasOldWOG() || FileManager.isHasNewWOG()) && LevelManager.getLevel() != null) {
            buttonSaveAndPlay.setDisable(false);
            FXMenu.saveAndPlayLevelItem.setDisable(false);
        }
        if (LevelManager.getLevel() != null && LevelManager.getLevel().undoActions.size() > 0) {
            FXMenu.undoItem.setDisable(disable);
            buttonUndo.setDisable(disable);
        } else {
            FXMenu.undoItem.setDisable(true);
            buttonUndo.setDisable(true);
        }
        if (LevelManager.getLevel() != null && LevelManager.getLevel().redoActions.size() > 0) {
            FXMenu.redoItem.setDisable(disable);
            buttonRedo.setDisable(disable);
        } else {
            FXMenu.redoItem.setDisable(true);
            buttonRedo.setDisable(true);
        }
    }

    public static void showPaletteConfigurator(MouseEvent mouseEvent, ToolBar toolbar) {
        if (mouseEvent.getButton() == MouseButton.SECONDARY) {
            ContextMenu contextMenu = new ContextMenu();
            MenuItem menuItem = new MenuItem("Configure Palette...");
            menuItem.setOnAction(actionEvent -> {
                try {
                    new PaletteReconfigurator().start(new Stage());
                } catch (ParserConfigurationException | SAXException | IOException e) {
                    throw new RuntimeException(e);
                }
            });
            contextMenu.getItems().add(menuItem);
            if (toolbar != null) {
                toolbar.setContextMenu(contextMenu);
            }
        }
    }

}
