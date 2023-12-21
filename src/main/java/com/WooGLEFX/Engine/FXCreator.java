package com.WooGLEFX.Engine;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import com.WooGLEFX.EditorObjects._Ball;
import com.WooGLEFX.File.FileManager;
import com.WooGLEFX.GUI.Alarms;
import com.WooGLEFX.GUI.PaletteReconfigurator;
import com.WooGLEFX.Structures.EditorAttribute;
import com.WooGLEFX.Structures.EditorObject;
import com.WooGLEFX.Structures.InputField;
import com.WooGLEFX.Structures.WorldLevel;
import com.WooGLEFX.Structures.SimpleStructures.LevelTab;
import com.WooGLEFX.Structures.SimpleStructures.MetaEditorAttribute;
import com.WooGLEFX.Structures.UserActions.AttributeChangeAction;
import com.WorldOfGoo.Ball.Part;
import com.WorldOfGoo.Resrc.ResrcImage;

import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Separator;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.ToolBar;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableRow;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.cell.TextFieldTreeTableCell;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseButton;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.StringConverter;

public class FXCreator {

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
                    e -> Main.addBall(Main.getLevel().getLevelObject(), ball.getObjects().get(0).getAttribute("name")));
            return idk;
        } else {
            Button idk = new Button();
            idk.setPrefSize(size, size);
            idk.setOnAction(
                    e -> Main.addBall(Main.getLevel().getLevelObject(), ball.getObjects().get(0).getAttribute("name")));
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

        buttonNewOld.setOnAction(e -> Main.newLevel(1.3));
        buttonNewNew.setOnAction(e -> Main.newLevel(1.5));
        buttonOpenOld.setOnAction(e -> Main.openLevel(1.3));
        buttonOpenNew.setOnAction(e -> Main.openLevel(1.5));
        buttonClone.setOnAction(e -> Main.cloneLevel());
        buttonSave.setOnAction(e -> Main.saveLevel());
        buttonSaveAll.setOnAction(e -> Main.saveAll());
        buttonSaveAndPlay.setOnAction(e -> Main.playLevel());
        buttonExport.setOnAction(e -> Main.exportLevel(true));
        buttonDummyExport.setOnAction(e -> Main.exportLevel(false));
        buttonUndo.setOnAction(e -> Main.undo());
        buttonRedo.setOnAction(e -> Main.redo());
        buttonCut.setOnAction(e -> Main.cut());
        buttonCopy.setOnAction(e -> Main.copy());
        buttonPaste.setOnAction(e -> Main.paste());
        buttonDelete.setOnAction(e -> Main.delete());
        buttonUpdateLevelResources.setOnAction(e -> Main.updateLevelResources());
        buttonImportImages.setOnAction(e -> Main.importImage());
        buttonAddTextResource.setOnAction(e -> Main.newTextResource());
        buttonCleanResources.setOnAction(e -> Main.cleanLevelResources());
        buttonSetMusic.setOnAction(e -> Main.importMusic());
        buttonSetLoopsound.setOnAction(e -> Main.importLoopsound());
        buttonSelectMoveAndResize.setOnAction(e -> Main.selectionMode());
        buttonSelectMoveAndResize.setStyle("-fx-background-color: #9999ff;"); // Highlighted by default
        buttonStrandMode.setOnAction(e -> Main.strandMode());
        buttonShowHideCamera.setOnAction(e -> Main.showHideCameras());
        buttonShowHideForcefields.setOnAction(e -> Main.showHideForcefields());
        buttonShowHideGeometry.setOnAction(e -> Main.showHideGeometry());
        buttonShowHideGraphics.setOnAction(e -> Main.showHideGraphics());
        buttonShowHideGoos.setOnAction(e -> Main.showHideGoos());
        buttonShowHideParticles.setOnAction(e -> Main.showHideParticles());
        buttonShowHideLabels.setOnAction(e -> Main.showHideLabels());
        buttonShowHideAnim.setOnAction(e -> Main.showHideAnim());
        buttonShowHideSceneBGColor.setOnAction(e -> Main.showHideSceneBGColor());

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

        oldGooballsToolbar.setOnMouseClicked(mouseEvent -> {
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
                oldGooballsToolbar.setContextMenu(contextMenu);
            }
        });

        newGooballsToolbar.setOnMouseClicked(mouseEvent -> {
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
                newGooballsToolbar.setContextMenu(contextMenu);
            }
        });

        nullGooballsToolbar.setOnMouseClicked(mouseEvent -> {
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
                nullGooballsToolbar.setContextMenu(contextMenu);
            }
        });

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

        addLineButton.setOnAction(e -> Main.addLine(Main.getLevel().getSceneObject()));
        addRectangleButton.setOnAction(e -> Main.addRectangle(Main.getLevel().getSceneObject()));
        addCircleButton.setOnAction(e -> Main.addCircle(Main.getLevel().getSceneObject()));
        addSceneLayerButton.setOnAction(e -> Main.addSceneLayer(Main.getLevel().getSceneObject()));
        addCompositegeomButton.setOnAction(e -> Main.addCompositegeom(Main.getLevel().getSceneObject()));
        addHingeButton.setOnAction(e -> Main.addHinge(Main.getLevel().getSceneObject()));
        autoPipeButton.setOnAction(e -> Main.autoPipe());
        addVertexButton.setOnAction(e -> Main.addPipeVertex(Main.getLevel().getSceneObject()));
        addFireButton.setOnAction(e -> Main.addFire(Main.getLevel().getLevelObject()));
        addLinearforcefieldButton.setOnAction(e -> Main.addLinearForcefield(Main.getLevel().getSceneObject()));
        addRadialforcefieldButton.setOnAction(e -> Main.addRadialForcefield(Main.getLevel().getSceneObject()));
        addParticlesButton.setOnAction(e -> Main.addParticle(Main.getLevel().getSceneObject()));
        addSignpostButton.setOnAction(e -> Main.addSign(Main.getLevel().getSceneObject()));
        addLabelButton.setOnAction(e -> Main.addLabel(Main.getLevel().getSceneObject()));

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

    public static final Paint notSelectedPaint = Paint.valueOf("FFFFFFFF");
    public static final Paint notSelectedHoverPaint = Paint.valueOf("B0D0FFFF");
    public static final Paint selectedPaint = Paint.valueOf("0000D0FF");

    /**
     * Generates the TreeTableView representing all objects in a "scene", "level",
     * or "resources" of a level.
     *
     * @return The TreeTableView.
     */
    public static TreeTableView<EditorObject> makeHierarchy() {

        // Create the TreeTableView.
        TreeTableView<EditorObject> hierarchy = new TreeTableView<>();

        hierarchy.setPlaceholder(new Label());

        // Create the columns the hierarchy uses ("Element" and its "ID or Name")
        TreeTableColumn<EditorObject, String> hierarchyElements = new TreeTableColumn<>();
        hierarchyElements.setGraphic(new Label("Elements"));
        hierarchyElements.setCellValueFactory(new TreeItemPropertyValueFactory<>("realName"));
        hierarchy.getColumns().add(hierarchyElements);
        hierarchyElements.setPrefWidth(200);

        TreeTableColumn<EditorObject, String> hierarchyNames = new TreeTableColumn<>();
        hierarchyNames.setGraphic(new Label("ID or Name"));

        hierarchyNames.setCellValueFactory(param -> {
            if (param.getValue().getValue().getObjName() != null) {
                if (param.getValue().getValue().getObjName2() != null
                        && !param.getValue().getValue().getObjName2().getValue().equals("")) {
                    return param.getValue().getValue().getObjName().valueProperty().concat(",")
                            .concat(param.getValue().getValue().getObjName2().valueProperty());
                } else {
                    return param.getValue().getValue().getObjName().valueProperty();
                }
            } else {
                return new EditorAttribute(param.getValue().getValue(), "AAAAA", "AAAAA", "AAAAA",
                        new InputField("AAAAA", InputField.ANY), false).valueProperty();
            }
        });

        hierarchy.getColumns().add(hierarchyNames);

        hierarchyElements.setCellFactory(column -> new TreeTableCell<>() {

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    // If this is an empty cell, set its text and graphic to empty.
                    // This prevents the cell from retaining other cells' information.
                    setText(null);
                    setGraphic(null);
                } else {
                    // Update this cell's text.
                    setText(item);
                    // Override the default padding that ruins the text.
                    setPadding(new Insets(0, 0, 0, 0));

                    if (getTableRow().getItem() != null) {
                        ImageView imageView;

                        try {
                            imageView = new ImageView(
                                    FileManager.getObjectIcon(getTableRow().getItem().getClass().getSimpleName()));
                        } catch (FileNotFoundException e) {
                            throw new RuntimeException(e);
                        }

                        // If the cell's EditorObject is invalid, display its graphic with a warning
                        // symbol.
                        // Otherwise, just display its graphic.
                        if (!getTableRow().getItem().isValid()) {
                            ImageView failedImg = new ImageView(FileManager.getFailedImage());
                            setGraphic(new StackPane(imageView, failedImg));
                        } else {
                            setGraphic(imageView);
                        }
                    }
                }
            }
        });

        hierarchyNames.setCellFactory(column -> new TreeTableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    // If this is an empty cell, set its text and graphic to empty.
                    // This prevents the cell from retaining other cells' information.
                    setText(null);
                    setGraphic(null);
                } else {
                    // setTextFill(Paint.valueOf("FFFFFFFF"));
                    // Update this cell's text.
                    setText(item);
                    // Override the default padding that ruins the text.
                    setPadding(new Insets(0, 0, 0, 0));
                }
            }
        });

        // If a cell is clicked from the hierarchy, update the selected object and
        // properties view.
        hierarchy.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                Main.setSelected(newValue.getValue());
                Main.changeTableView(newValue.getValue());
            }
        });

        // Make the rows small.
        hierarchy.setFixedCellSize(18);

        hierarchy.setRowFactory(treeTableView -> {
            final TreeTableRow<EditorObject> row = new TreeTableRow<>();

            row.setOnMousePressed(event -> {
                if (hierarchy.getTreeItem(row.getIndex()) != null) {
                    Main.setSelected(hierarchy.getTreeItem(row.getIndex()).getValue());
                    Main.changeTableView(hierarchy.getTreeItem(row.getIndex()).getValue());
                    if (event.getButton().equals(MouseButton.SECONDARY)) {
                        try {
                            row.setContextMenu(contextMenuForEditorObject(row.getTreeItem().getValue()));
                        } catch (FileNotFoundException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            });

            row.setOnDragDetected(event -> {
                TreeItem<EditorObject> selected2 = hierarchy.getSelectionModel().getSelectedItem();
                if (selected2 != null) {
                    Dragboard db = hierarchy.startDragAndDrop(TransferMode.ANY);
                    ClipboardContent content = new ClipboardContent();
                    content.putString(selected2.getValue().getClass().getName());
                    db.setContent(content);
                    Main.setMoving(row.getItem());
                    Main.setOldDropIndex(row.getIndex());
                    event.consume();
                }
            });

            row.setOnDragExited(event -> {
                // row.setStyle("");
            });

            row.setOnDragOver(event -> {
                if (event.getDragboard().hasString()) {
                    event.acceptTransferModes(TransferMode.MOVE);
                    // row.setStyle("-fx-font-size: 12pt, -fx-background-color: #D0F0FFFF");
                }
                event.consume();
            });

            row.setOnDragDropped(event -> {
                boolean success = false;
                if (event.getDragboard().hasString()) {

                    if (!row.isEmpty()) {
                        int dropIndex = row.getIndex();
                        hierarchy.getTreeItem(Main.getOldDropIndex())
                                .setValue(hierarchy.getTreeItem(dropIndex).getValue());
                        hierarchy.getTreeItem(dropIndex).setValue(Main.getMoving());
                        hierarchy.getSelectionModel().select(dropIndex);
                        // row.setItem((EditorObject) db.getContent(null));

                        success = true;
                    }
                }
                event.setDropCompleted(success);
                event.consume();
            });

            return row;
        });

        hierarchy.setColumnResizePolicy(TreeTableView.UNCONSTRAINED_RESIZE_POLICY);

        hierarchyNames.prefWidthProperty().bind(hierarchy.widthProperty().subtract(hierarchyElements.widthProperty()));

        // hierarchy.getStyleClass().add("column-header");

        return hierarchy;
    }

    /**
     * Creates the lower right "Properties" view.
     * This is where an object's attributes are shown and can be modified.
     *
     * @return The TreeTableView configured to show object properties.
     */
    public static TreeTableView<EditorAttribute> makePropertiesView() {

        // Create the properties view.
        TreeTableView<EditorAttribute> propertiesView = new TreeTableView<>();

        propertiesView.setPlaceholder(new Label());

        // Create the columns the properties view uses (Attribute name and attribute
        // value).
        TreeTableColumn<EditorAttribute, String> name = new TreeTableColumn<>();
        name.setGraphic(new Label("Name"));
        name.setCellValueFactory(param -> param.getValue().getValue().nameProperty());
        propertiesView.getColumns().add(name);

        TreeTableColumn<EditorAttribute, String> value = new TreeTableColumn<>();
        value.setGraphic(new Label("Value"));
        value.setCellValueFactory(param -> param.getValue().getValue().valueProperty());
        propertiesView.getColumns().add(value);

        // Limit the width of the "names" column.
        name.setPrefWidth(200);

        // Hide the empty "root" attribute. This allows all of its children to be
        // displayed at once at the top of the TreeTableView.
        propertiesView.setShowRoot(false);

        // For each row of the properties view:
        propertiesView.setRowFactory(tableView -> {
            final TreeTableRow<EditorAttribute> row = new TreeTableRow<>();

            // Manually change the row's font size to prevent clipping.
            row.setStyle("-fx-font-size: 11");

            row.hoverProperty().addListener((observable) -> {
                if (row.isHover() && row.getItem() != null) {
                    // If the user is hovering over this row, select it.
                    propertiesView.getSelectionModel().select(row.getIndex());
                } else {
                    // If the user is not hovering over this row, deselect all rows.
                    // This works because hovering off a row is processed before hovering onto
                    // another row.
                    propertiesView.getSelectionModel().clearSelection();
                }
            });

            row.pressedProperty().addListener((observable, oldValue, newValue) -> {
                // If we are editing a cell with null content or an uneditable cell, cancel the
                // edit.
                if (row.getTreeItem() == null || row.getTreeItem().getValue().getInput().getType() == InputField.NULL) {
                    Platform.runLater(() -> propertiesView.edit(-1, null));
                }
            });

            return row;
        });

        name.setCellFactory(column -> new TreeTableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    // If this is an empty cell, set its text and graphic to empty.
                    // This prevents the cell from retaining other cells' information.
                    setText(null);
                } else {
                    // Update this cell's text.
                    setText(item);
                    // Override the default padding that ruins the text.
                    setPadding(new Insets(0, 0, 0, 0));
                }
            }
        });

        value.setCellFactory(new Callback<>() {
            @Override
            public TreeTableCell<EditorAttribute, String> call(
                    TreeTableColumn<EditorAttribute, String> editorAttributeStringTreeTableColumn) {
                StringConverter<String> stringConverter = new StringConverter<>() {
                    @Override
                    public String toString(String s) {
                        return s;
                    }

                    @Override
                    public String fromString(String s) {
                        return s;
                    }
                };
                TextFieldTreeTableCell<EditorAttribute, String> cell = new TextFieldTreeTableCell<>(stringConverter) {
                    @Override
                    public void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);

                        if (!empty && getTableRow().getTreeItem() != null) {
                            EditorAttribute editorAttribute = getTableRow().getTreeItem().getValue();
                            if (editorAttribute.getInput().verify(editorAttribute.getObject(), item)) {
                                setStyle("-fx-text-fill: #000000ff");
                            } else {
                                setStyle("-fx-text-fill: #ff0000ff");
                            }
                        }

                        // Override the default padding that ruins the text.
                        setPadding(new Insets(0, 0, 0, 2));
                    }

                    private String before;

                    @Override
                    public void startEdit() {
                        super.startEdit();
                        before = getItem();

                        Bounds bounds = localToScreen(getBoundsInLocal());

                        if (bounds != null) {

                            double x = bounds.getMinX();
                            double y = bounds.getMinY() + 18;

                            FXCreator.possibleAttributeValues(this).show(Main.getStage(), x, y);
                        }
                    }

                    @Override
                    public void cancelEdit() {
                        super.cancelEdit();
                        if (getContextMenu() != null) {
                            getContextMenu().hide();
                        }
                    }

                    @Override
                    public void commitEdit(String s) {
                        super.commitEdit(
                                getTableRow().getItem().getInput().verify(getTableRow().getItem().getObject(), s) ? s
                                        : before);
                    }
                };

                cell.setOnMousePressed(event -> {

                    cell.setContextMenu(FXCreator.possibleAttributeValues(cell));
                    cell.getContextMenu().show(propertiesView, Main.getStage().getX()
                            + Main.getSplitPane().getDividers().get(0).getPosition() * Main.getSplitPane().getWidth(),
                            100);
                    propertiesView.getSelectionModel().selectedItemProperty()
                            .addListener((observable, oldValue, newValue) -> {
                                if (newValue != null) {
                                    cell.getContextMenu().hide();
                                }
                            });

                    if (cell.getContextMenu() != null) {
                        cell.getContextMenu().show(propertiesView,
                                Main.getStage().getX() + Main.getSplitPane().getDividers().get(0).getPosition()
                                        * Main.getSplitPane().getWidth(),
                                100);
                    }
                    if (!cell.isSelected() && cell.getContextMenu() != null) {
                        cell.getContextMenu().hide();
                    }
                });

                return cell;
            }
        });

        // value.setCellFactory(TextFieldTreeTableCell.forTreeTableColumn());

        value.setOnEditCommit(e -> {
            // When editing a cell:

            // Change the actual attribute to reflect the edit.
            EditorAttribute attribute = propertiesView.getTreeItem(e.getTreeTablePosition().getRow()).getValue();
            String oldValue = attribute.getValue();
            if (attribute.getInput().verify(attribute.getObject(), e.getNewValue())) {
                attribute.setValue(e.getNewValue());
            }

            // If the edit was actually valid:
            if (e.getNewValue().equals("") || attribute.getInput().verify(attribute.getObject(), e.getNewValue())) {

                // Push an attribute change to the undo buffer.
                Main.registerChange(new AttributeChangeAction(Main.getSelected(), attribute.getName(), oldValue,
                        attribute.getValue()));

                // If we have edited the name or ID of the object, change the object's "Name or
                // ID" value.
                if (attribute.getName().equals("name") || attribute.getName().equals("id")) {
                    Main.getSelected().setNameAttribute(attribute);
                }

            } else {
                // Reset the attribute.
                attribute.setValue(oldValue);
                // If the user entered an invalid value, refresh to clear the edit.
                propertiesView.refresh();
            }

            Main.hierarchy.refresh();

        });

        // Adjust the row height to fit more attributes on screen at once.
        // This currently breaks the text.
        propertiesView.setFixedCellSize(18);

        // Set the "value" column and the entire TreeTableView to be editable.
        value.setEditable(true);
        propertiesView.setEditable(true);

        // Set the "value" column to extend to the very edge of the TreeTableView.
        value.prefWidthProperty().bind(propertiesView.widthProperty().subtract(name.widthProperty()));

        return propertiesView;
    }

    public static Menu fileMenu = new Menu("File");

    public static MenuItem reloadWorldOfGooOldItem = new MenuItem("Reload World of Goo (1.3)");
    public static MenuItem reloadWorldOfGooNewItem = new MenuItem("Reload World of Goo (1.5)");
    public static MenuItem changeWorldOfGooDirectoryOldItem = new MenuItem("Change World of Goo Directory (1.3)...");
    public static MenuItem changeWorldOfGooDirectoryNewItem = new MenuItem("Change World of Goo Directory (1.5)...");
    public static MenuItem saveOldBallToNewItem = new MenuItem("Copy Ball from 1.3 to 1.5");
    public static MenuItem saveNewBallToOldItem = new MenuItem("Copy Ball from 1.5 to 1.3");
    public static MenuItem quitItem = new MenuItem("Quit");

    public static Menu levelMenu = new Menu("Level");

    public static MenuItem newLevelOldItem = new MenuItem("New Level (1.3)...");
    public static MenuItem newLevelNewItem = new MenuItem("New Level (1.5)...");
    public static MenuItem openLevelOldItem = new MenuItem("Open Level (1.3)...");
    public static MenuItem openLevelNewItem = new MenuItem("Open Level (1.5)...");
    public static MenuItem cloneLevelItem = new MenuItem("Clone Level...");
    public static MenuItem saveLevelItem = new MenuItem("Save Level");
    public static MenuItem saveAllLevelsItem = new MenuItem("Save All Levels");
    public static MenuItem saveAndPlayLevelItem = new MenuItem("Save and Play Level");
    public static MenuItem renameLevelItem = new MenuItem("Rename Level");
    public static MenuItem deleteLevelItem = new MenuItem("Delete Level");

    public static Menu editMenu = new Menu("Edit");

    public static MenuItem undoItem = new MenuItem("Undo");
    public static MenuItem redoItem = new MenuItem("Redo");
    public static MenuItem cutItem = new MenuItem("Cut");
    public static MenuItem copyItem = new MenuItem("Copy");
    public static MenuItem pasteItem = new MenuItem("Paste");
    public static MenuItem deleteItem = new MenuItem("Delete");

    public static Menu resourcesMenu = new Menu("Resources");

    public static MenuItem updateLevelResourcesItem = new MenuItem("Update Level Resources...");
    public static MenuItem importImageItem = new MenuItem("Import Images...");
    public static MenuItem newTextResourceItem = new MenuItem("New Text Resource");
    public static MenuItem cleanLevelResourcesItem = new MenuItem("Clean Resources");
    public static MenuItem setMusicItem = new MenuItem("Set Music...");
    public static MenuItem setLoopsoundItem = new MenuItem("Set Loop Sound...");

    public static MenuBar createMenu() throws FileNotFoundException {
        MenuBar bar = new MenuBar();

        reloadWorldOfGooOldItem
                .setGraphic(new ImageView(FileManager.getIcon("ButtonIcons\\File\\reload_world_of_goo_old.png")));
        reloadWorldOfGooNewItem
                .setGraphic(new ImageView(FileManager.getIcon("ButtonIcons\\File\\reload_world_of_goo_new.png")));
        changeWorldOfGooDirectoryOldItem.setGraphic(
                new ImageView(FileManager.getIcon("ButtonIcons\\File\\change_world_of_goo_directory_old.png")));
        changeWorldOfGooDirectoryNewItem.setGraphic(
                new ImageView(FileManager.getIcon("ButtonIcons\\File\\change_world_of_goo_directory_new.png")));
        saveOldBallToNewItem
                .setGraphic(new ImageView(FileManager.getIcon("ButtonIcons\\File\\move_ball_to_new_version.png")));
        saveNewBallToOldItem
                .setGraphic(new ImageView(FileManager.getIcon("ButtonIcons\\File\\move_ball_to_old_version.png")));
        quitItem.setGraphic(new ImageView(FileManager.getIcon("ButtonIcons\\File\\quit.png")));

        reloadWorldOfGooOldItem.setOnAction(e -> Main.reloadWorldOfGoo(1.3));
        reloadWorldOfGooNewItem.setOnAction(e -> Main.reloadWorldOfGoo(1.5));
        changeWorldOfGooDirectoryOldItem.setOnAction(e -> Main.changeWorldOfGooDirectory(1.3));
        changeWorldOfGooDirectoryNewItem.setOnAction(e -> Main.changeWorldOfGooDirectory(1.5));
        saveOldBallToNewItem.setOnAction(e -> Main.saveBallInVersion(1.3, 1.5));
        saveNewBallToOldItem.setOnAction(e -> Main.saveBallInVersion(1.5, 1.3));
        quitItem.setOnAction(e -> Main.quit());

        fileMenu.getItems().addAll(reloadWorldOfGooOldItem, reloadWorldOfGooNewItem, changeWorldOfGooDirectoryOldItem,
                changeWorldOfGooDirectoryNewItem, saveOldBallToNewItem, saveNewBallToOldItem, quitItem);

        newLevelOldItem.setGraphic(new ImageView(FileManager.getIcon("ButtonIcons\\Level\\new_lvl_old.png")));
        newLevelNewItem.setGraphic(new ImageView(FileManager.getIcon("ButtonIcons\\Level\\new_lvl_new.png")));
        openLevelOldItem.setGraphic(new ImageView(FileManager.getIcon("ButtonIcons\\Level\\open_lvl_old.png")));
        openLevelNewItem.setGraphic(new ImageView(FileManager.getIcon("ButtonIcons\\Level\\open_lvl_new.png")));
        cloneLevelItem.setGraphic(new ImageView(FileManager.getIcon("ButtonIcons\\Level\\clone_lvl.png")));
        saveLevelItem.setGraphic(new ImageView(FileManager.getIcon("ButtonIcons\\Level\\save.png")));
        saveAllLevelsItem.setGraphic(new ImageView(FileManager.getIcon("ButtonIcons\\Level\\save_all.png")));
        saveAndPlayLevelItem.setGraphic(new ImageView(FileManager.getIcon("ButtonIcons\\Level\\play.png")));
        renameLevelItem.setGraphic(new ImageView(FileManager.getIcon("ButtonIcons\\Level\\rename.png")));
        deleteLevelItem.setGraphic(new ImageView(FileManager.getIcon("ButtonIcons\\Level\\delete.png")));

        newLevelOldItem.setOnAction(e -> Main.newLevel(1.3));
        newLevelNewItem.setOnAction(e -> Main.newLevel(1.5));
        openLevelOldItem.setOnAction(e -> Main.openLevel(1.3));
        openLevelNewItem.setOnAction(e -> Main.openLevel(1.5));
        cloneLevelItem.setOnAction(e -> Main.cloneLevel());
        saveLevelItem.setOnAction(e -> Main.saveLevel());
        saveAllLevelsItem.setOnAction(e -> Main.saveAll());
        saveAndPlayLevelItem.setOnAction(e -> Main.playLevel());
        renameLevelItem.setOnAction(e -> Main.renameLevel());
        deleteLevelItem.setOnAction(e -> Main.deleteLevel());

        levelMenu.getItems().addAll(newLevelOldItem, openLevelOldItem, newLevelNewItem, openLevelNewItem,
                cloneLevelItem, saveLevelItem, saveAllLevelsItem,
                saveAndPlayLevelItem, renameLevelItem, deleteLevelItem);

        undoItem.setGraphic(new ImageView(FileManager.getIcon("ButtonIcons\\Edit\\undo.png")));
        redoItem.setGraphic(new ImageView(FileManager.getIcon("ButtonIcons\\Edit\\redo.png")));
        cutItem.setGraphic(new ImageView(FileManager.getIcon("ButtonIcons\\Edit\\cut.png")));
        copyItem.setGraphic(new ImageView(FileManager.getIcon("ButtonIcons\\Edit\\copy.png")));
        pasteItem.setGraphic(new ImageView(FileManager.getIcon("ButtonIcons\\Edit\\paste.png")));
        deleteItem.setGraphic(new ImageView(FileManager.getIcon("ButtonIcons\\Edit\\delete.png")));

        undoItem.setOnAction(e -> Main.undo());
        redoItem.setOnAction(e -> Main.redo());
        cutItem.setOnAction(e -> Main.cut());
        copyItem.setOnAction(e -> Main.copy());
        pasteItem.setOnAction(e -> Main.paste());
        deleteItem.setOnAction(e -> Main.delete());

        editMenu.getItems().addAll(undoItem, redoItem, cutItem, copyItem, pasteItem, deleteItem);

        updateLevelResourcesItem
                .setGraphic(new ImageView(FileManager.getIcon("ButtonIcons\\Resources\\update_level_resources.png")));
        importImageItem.setGraphic(new ImageView(FileManager.getIcon("ButtonIcons\\Resources\\import_img.png")));
        newTextResourceItem
                .setGraphic(new ImageView(FileManager.getIcon("ButtonIcons\\Resources\\add_text_resource.png")));
        cleanLevelResourcesItem
                .setGraphic(new ImageView(FileManager.getIcon("ButtonIcons\\Resources\\clean_level_resources.png")));
        setMusicItem.setGraphic(new ImageView(FileManager.getIcon("ButtonIcons\\Resources\\import_music.png")));
        setLoopsoundItem.setGraphic(new ImageView(FileManager.getIcon("ButtonIcons\\Resources\\import_soundloop.png")));

        updateLevelResourcesItem.setOnAction(e -> Main.updateLevelResources());
        importImageItem.setOnAction(e -> Main.importImage());
        newTextResourceItem.setOnAction(e -> Main.newTextResource());
        cleanLevelResourcesItem.setOnAction(e -> Main.cleanLevelResources());
        setMusicItem.setOnAction(e -> Main.importMusic());
        setLoopsoundItem.setOnAction(e -> Main.importLoopsound());

        resourcesMenu.getItems().addAll(updateLevelResourcesItem, importImageItem, newTextResourceItem,
                cleanLevelResourcesItem, setMusicItem, setLoopsoundItem);

        bar.getMenus().addAll(fileMenu, levelMenu, editMenu, resourcesMenu);
        return bar;
    }

    /**
     * Generates a right-click menu for an editor object.
     *
     * @param object The editor object to generate the menu for.
     * @return The ContextMenu for right-clicking on that object.
     */
    public static ContextMenu contextMenuForEditorObject(EditorObject object) throws FileNotFoundException {

        // Create the content menu.
        ContextMenu menu = new ContextMenu();

        // For every object that can be created as a child of this object:
        for (String childToAdd : object.getPossibleChildren()) {

            // Create a menu item representing creating this child.
            MenuItem addItemItem = new MenuItem("Add " + childToAdd);

            // Attempt to set graphics for this menu item.
            addItemItem.setGraphic(new ImageView(FileManager.getObjectIcon(childToAdd)));

            // Set the item's action to creating the child, with the object as its parent.
            addItemItem.setOnAction(event -> {
                switch (childToAdd) {
                    case "BallInstance" -> Main.addBall(object, "");
                    case "Strand" -> Main.addStrand(object);
                    case "camera" -> Main.addCamera(object);
                    case "poi" -> Main.addPoi(object);
                    case "music" -> Main.addMusic(object);
                    case "loopsound" -> Main.addLoopsound(object);
                    case "linearforcefield" -> Main.addLinearForcefield(object);
                    case "radialforcefield" -> Main.addRadialForcefield(object);
                    case "particles" -> Main.addParticle(object);
                    case "SceneLayer" -> Main.addSceneLayer(object);
                    case "buttongroup" -> Main.addButtongroup(object);
                    case "button" -> Main.addButton(object);
                    case "circle" -> Main.addCircle(object);
                    case "rectangle" -> Main.addRectangle(object);
                    case "hinge" -> Main.addHinge(object);
                    case "compositegeom" -> Main.addCompositegeom(object);
                    case "label" -> Main.addLabel(object);
                    case "line" -> Main.addLine(object);
                    case "motor" -> Main.addMotor(object);
                    case "slider" -> Main.addSlider(object);
                    case "endoncollision" -> Main.addEndoncollision(object);
                    case "endonnogeom" -> Main.addEndonnogeom(object);
                    case "endonmessage" -> Main.addEndonmessage(object);
                    case "targetheight" -> Main.addTargetheight(object);
                    case "fire" -> Main.addFire(object);
                    case "levelexit" -> Main.addLevelexit(object);
                    case "pipe" -> Main.addPipe(object);
                    case "signpost" -> Main.addSign(object);
                    case "textstring" -> Main.addString(object);
                    case "resrcimage" -> Main.addResrcImage(object);
                    case "sound" -> Main.addSound(object);
                    case "setdefaults" -> Main.addSetDefaults(object);
                    case "Vertex" -> Main.addPipeVertex(object);
                    default -> throw new RuntimeException("Unknown child type: " + childToAdd);
                }
            });

            menu.getItems().add(addItemItem);
        }

        return menu;
    }

    /**
     * Generates the Scene, Level, and Resrc buttons that switch between those
     * object structures.
     *
     * @return The HBox object containing the buttons.
     */
    public static TabPane hierarchySwitcherButtons() {

        // Create and customize the parent container.
        TabPane thing = new TabPane();

        // Create the three buttons.
        Tab sceneSelectButton = new Tab("Scene");
        Tab levelSelectButton = new Tab("Level");
        Tab resrcSelectButton = new Tab("Resrc");
        Tab textSelectButton = new Tab("Text");
        Tab addinSelectButton = new Tab("Addin");

        thing.getTabs().addAll(sceneSelectButton, levelSelectButton, resrcSelectButton, textSelectButton,
                addinSelectButton);
        thing.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        thing.setMinHeight(30);
        thing.setMaxHeight(30);
        thing.setPrefHeight(30);
        thing.setPadding(new Insets(-6, -6, -6, -6));

        thing.getSelectionModel().selectedItemProperty().addListener((observableValue, tab, t1) -> {
            if (Main.getLevel() != null) {
                if (t1 == sceneSelectButton) {
                    Main.hierarchy.setRoot(Main.getLevel().getScene().get(0).getTreeItem());
                    Main.hierarchy.refresh();
                    Main.hierarchy.getRoot().setExpanded(true);
                    Main.getLevel().setCurrentlySelectedSection("Scene");
                    Main.getHierarchy().setShowRoot(true);
                } else if (t1 == levelSelectButton) {
                    Main.hierarchy.setRoot(Main.getLevel().getLevel().get(0).getTreeItem());
                    Main.hierarchy.refresh();
                    Main.hierarchy.getRoot().setExpanded(true);
                    Main.getLevel().setCurrentlySelectedSection("Level");
                    Main.getHierarchy().setShowRoot(true);
                } else if (t1 == resrcSelectButton) {
                    Main.hierarchy.setRoot(Main.getLevel().getResourcesObject().getChildren().get(0).getTreeItem());
                    Main.hierarchy.refresh();
                    Main.hierarchy.getRoot().setExpanded(true);
                    Main.getLevel().setCurrentlySelectedSection("Resrc");
                    Main.getHierarchy().setShowRoot(true);
                } else if (t1 == textSelectButton) {
                    Main.hierarchy.setRoot(Main.getLevel().getTextObject().getTreeItem());
                    Main.hierarchy.refresh();
                    Main.hierarchy.getRoot().setExpanded(true);
                    Main.getLevel().setCurrentlySelectedSection("Text");
                    Main.getHierarchy().setShowRoot(true);
                } else if (t1 == addinSelectButton) {
                    Main.hierarchy.setRoot(Main.getLevel().getAddinObject().getTreeItem());
                    Main.hierarchy.refresh();
                    Main.hierarchy.getRoot().setExpanded(true);
                    Main.getLevel().setCurrentlySelectedSection("Addin");
                    Main.getHierarchy().setShowRoot(true);
                }
            }
        });

        return thing;
    }

    /**
     * Generates a list of attributes for an object, according to that object's meta
     * attributes.
     *
     * @param object The editor object to generate the interface for.
     * @return The root tree item representing the attributes for the object.
     */
    public static TreeItem<EditorAttribute> makePropertiesViewTreeItem(EditorObject object) {

        // Create the root tree item.
        TreeItem<EditorAttribute> treeItem = new TreeItem<>(
                new EditorAttribute(object, "", "", "", new InputField("", InputField.NULL), false));

        // Loop over the object's meta attributes.
        for (MetaEditorAttribute metaEditorAttribute : object.getMetaAttributes()) {

            // Find the object's EditorAttribute associated with this meta attribute
            // (sharing the same name).
            EditorAttribute attribute;
            if (object.getAttribute(metaEditorAttribute.getName()) != null) {
                attribute = object.getAttribute2(metaEditorAttribute.getName());
            } else {
                // If no such attribute exists, this attribute is instead the name of a category
                // of attributes.
                // In this case, create a dummy attribute with no value.
                attribute = new EditorAttribute(object, metaEditorAttribute.getName(), "", "",
                        new InputField("", InputField.NULL), false);
            }
            TreeItem<EditorAttribute> thisAttribute = new TreeItem<>(attribute);

            // If this attribute is set to be open by default, set its tree item to open.
            if (metaEditorAttribute.getOpenByDefault()) {
                thisAttribute.setExpanded(true);
            }

            // If this attribute represents a category of attributes, it will have children.
            // Add the children's TreeItems as children of the category's TreeItem.
            for (MetaEditorAttribute childAttribute : metaEditorAttribute.getChildren()) {
                thisAttribute.getChildren().add(new TreeItem<>(object.getAttribute2(childAttribute.getName())));
            }

            // Add the attribute's TreeItem as a child of the root's TreeItem.
            treeItem.getChildren().add(thisAttribute);
        }

        return treeItem;
    }

    /**
     * Generates a JavaFX Tab representing one level.
     *
     * @param level The level to generate a tab from.
     * @return The tab representing the level.
     */
    public static LevelTab levelSelectButton(WorldLevel level) {

        // Instantiate the tab.
        LevelTab tab = new LevelTab(level.getLevelName(), level);

        // Override the default close operation of the tab.
        tab.setOnCloseRequest(event -> {
            event.consume();
            // If the level has unsaved changes:
            if (level.getEditingStatus() == WorldLevel.UNSAVED_CHANGES) {
                // Show a dialogue asking the user if they want to close the level without
                // saving changes first.
                Alarms.closeTabMessage(tab, level);
            } else {
                // Close the tab.
                if (tab.getTabPane().getTabs().size() == 1) {
                    Main.getLevelSelectPane().setMinHeight(0);
                    Main.getLevelSelectPane().setMaxHeight(0);
                    // If all tabs are closed, clear the side pane
                    Main.hierarchy.setRoot(null);
                    // Clear the properties pane too
                    Main.changeTableView(null);
                }
                tab.getTabPane().getTabs().remove(tab);
            }
        });

        tab.selectedProperty().addListener((observableValue, aBoolean, t1) -> {
            // If the user has just selected this tab:
            if (t1) {
                // Set this tab's level to be the currently selected level.
                Main.setLevel(level);
                switch (level.getCurrentlySelectedSection()) {
                    case "Scene" -> Main.hierarchy.setRoot(level.getSceneObject().getTreeItem());
                    case "Level" -> Main.hierarchy.setRoot(level.getLevelObject().getTreeItem());
                    case "Resrc" -> Main.hierarchy.setRoot(level.getResourcesObject().getTreeItem());
                    case "Text" -> Main.hierarchy.setRoot(level.getTextObject().getTreeItem());
                    case "Addin" -> Main.hierarchy.setRoot(level.getAddinObject().getTreeItem());
                }

                switch (level.getCurrentlySelectedSection()) {
                    case "Scene" -> Main.hierarchySwitcherButtons.getSelectionModel().select(0);
                    case "Level" -> Main.hierarchySwitcherButtons.getSelectionModel().select(1);
                    case "Resrc" -> Main.hierarchySwitcherButtons.getSelectionModel().select(2);
                    case "Text" -> Main.hierarchySwitcherButtons.getSelectionModel().select(3);
                    case "Addin" -> Main.hierarchySwitcherButtons.getSelectionModel().select(4);
                }

            } else {
                // Destroy and replace the level tab to prevent an unknown freezing issue.
                if (level.getLevelTab() != null && level.getLevelTab().getTabPane() != null
                        && level.getLevelTab().getTabPane().getTabs().contains(level.getLevelTab())
                        && level.getLevelTab().getTabPane().getTabs().size() != 0) {
                    level.setEditingStatus(level.getEditingStatus(), false);
                }
            }
        });

        return tab;
    }

    public static ContextMenu possibleAttributeValues(TextFieldTreeTableCell<EditorAttribute, String> cell) {
        ContextMenu contextMenu = new ContextMenu();
        EditorAttribute attribute = cell.getTableRow().getItem();
        if (attribute == null) {
            return contextMenu;
        }

        switch (attribute.getInput().getType()) {
            case InputField.IMAGE, InputField.IMAGE_REQUIRED -> {
                for (EditorObject resource : Main.getLevel().getResources()) {
                    if (resource instanceof ResrcImage) {
                        MenuItem setImageItem = new MenuItem();

                        Label label = new Label(resource.getAttribute("id"));
                        label.setMaxHeight(17);
                        label.setMinHeight(17);
                        label.setPrefHeight(17);
                        label.setStyle("-fx-font-size: 11");
                        label.setPadding(new Insets(0, 0, 0, 0));

                        setImageItem.setGraphic(label);

                        setImageItem.setOnAction(event -> {
                            Main.registerChange(new AttributeChangeAction(Main.getSelected(), attribute.getName(),
                                    attribute.getValue(), resource.getAttribute("id")));
                            attribute.setValue(resource.getAttribute("REALid"));
                            if (contextMenu.isFocused()) {
                                cell.commitEdit(attribute.getValue());
                            }
                        });

                        contextMenu.getItems().add(setImageItem);
                    }
                }
            }
            case InputField.BALL -> {
                String path = Main.getLevel().getVersion() == 1.5 ? FileManager.getNewWOGdir()
                        : FileManager.getOldWOGdir();
                File[] ballFiles = new File(path + "\\res\\balls").listFiles();
                if (ballFiles != null) {
                    for (File ballFile : ballFiles) {
                        MenuItem setImageItem = new MenuItem(ballFile.getName());

                        setImageItem.setOnAction(event -> {
                            Main.registerChange(new AttributeChangeAction(Main.getSelected(), attribute.getName(),
                                    attribute.getValue(), ballFile.getName()));
                            attribute.setValue(ballFile.getName());
                            if (contextMenu.isFocused()) {
                                cell.commitEdit(attribute.getValue());
                            }
                        });

                        contextMenu.getItems().add(setImageItem);
                    }
                }
            }
        }

        return contextMenu;
    }

}
