package com.WooGLEFX.Structures;

import com.WooGLEFX.File.FileManager;
import com.WooGLEFX.Engine.FXCreator;
import com.WooGLEFX.Engine.Main;
import com.WooGLEFX.File.GlobalResourceManager;
import com.WooGLEFX.GUI.Alarms;
import com.WorldOfGoo.Addin.Addin;
import com.WorldOfGoo.Level.Level;
import com.WorldOfGoo.Level.Signpost;
import com.WorldOfGoo.Resrc.ResourceManifest;
import com.WorldOfGoo.Scene.Scene;
import com.WorldOfGoo.Text.TextString;
import com.WorldOfGoo.Text.TextStrings;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TreeItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;

import java.io.FileNotFoundException;
import java.util.ArrayList;

public class WorldLevel {

    private final ArrayList<EditorObject> scene;
    private final ArrayList<EditorObject> level;
    private final ArrayList<EditorObject> resources;
    private final ArrayList<EditorObject> addin;
    private final ArrayList<EditorObject> text;

    public ArrayList<EditorObject> getScene() {
        return scene;
    }

    public ArrayList<EditorObject> getLevel() {
        return level;
    }

    public ArrayList<EditorObject> getResources() {
        return resources;
    }

    public ArrayList<EditorObject> getAddin() {
        return addin;
    }

    public ArrayList<EditorObject> getText() {
        return text;
    }

    private EditorObject levelObject;
    private EditorObject sceneObject;
    private EditorObject resourcesObject;
    private EditorObject addinObject;
    private EditorObject textObject;

    public EditorObject getLevelObject() {
        return levelObject;
    }

    public EditorObject getSceneObject() {
        return sceneObject;
    }

    public EditorObject getResourcesObject() {
        return resourcesObject;
    }

    public EditorObject getAddinObject() {
        return addinObject;
    }

    public EditorObject getTextObject() {
        return textObject;
    }

    private String levelName;

    public String getLevelName() {
        return levelName;
    }

    public void setLevelName(String levelName) {
        this.levelName = levelName;
    }

    private final double version;

    public double getVersion() {
        return version;
    }

    private boolean showCameras = false;
    private boolean showForcefields = true;
    private boolean showGeometry = true;
    private boolean showGraphics = true;
    private int showGoos = 2;
    private boolean showParticles = true;
    private boolean showLabels = true;
    private boolean showAnimations = true;


    public boolean isShowCameras() {
        return showCameras;
    }

    public void setShowCameras(boolean showCameras) {
        this.showCameras = showCameras;
        FXCreator.buttonShowHideCamera.setGraphic(new ImageView(showCameras ? showHideCam1 : showHideCam0));
        FXCreator.buttonShowHideParticles.setGraphic(new ImageView(showParticles ? showHideParticles1 : showHideParticles0));
    }

    public boolean isShowForcefields() {
        return showForcefields;
    }

    public void setShowForcefields(boolean showForcefields) {
        this.showForcefields = showForcefields;
        FXCreator.buttonShowHideForcefields.setGraphic(new ImageView(showForcefields ? showHideForcefields1 : showHideForcefields0));
    }

    public boolean isShowGeometry() {
        return showGeometry;
    }

    public void setShowGeometry(boolean showGeometry) {
        this.showGeometry = showGeometry;
        FXCreator.buttonShowHideGeometry.setGraphic(new ImageView(showGeometry ? showHideGeometry1 : showHideGeometry0));
    }

    public boolean isShowGraphics() {
        return showGraphics;
    }

    public void setShowGraphics(boolean showGraphics) {
        this.showGraphics = showGraphics;
        FXCreator.buttonShowHideGraphics.setGraphic(new ImageView(showGraphics ? showHideImages1 : showHideImages0));
    }

    public int getShowGoos() {
        return showGoos;
    }

    public void setShowGoos(int showGoos) {
        this.showGoos = showGoos;
        switch (showGoos) {
            case 0 -> FXCreator.buttonShowHideGoos.setGraphic(new ImageView(showHideGoobs0));
            case 1 -> FXCreator.buttonShowHideGoos.setGraphic(new ImageView(showHideGoobs1));
            case 2 -> FXCreator.buttonShowHideGoos.setGraphic(new ImageView(showHideGoobs2));
        }
    }

    public boolean isShowParticles() {
        return showParticles;
    }

    public void setShowParticles(boolean showParticles) {
        this.showParticles = showParticles;
    }

    public boolean isShowLabels() {
        return showLabels;
    }

    public void setShowLabels(boolean showLabels) {
        this.showLabels = showLabels;
        FXCreator.buttonShowHideLabels.setGraphic(new ImageView(showLabels ? showHideLabels1 : showHideLabels0));
    }

    public boolean isShowAnimations() {
        return showAnimations;
    }

    public void setShowAnimations(boolean showAnimations) {
        this.showAnimations = showAnimations;
        FXCreator.buttonShowHideAnim.setGraphic(new ImageView(showAnimations ? showHideAnim : showHideAnim0));
    }

    public static Image showHideAnim0;
    public static Image showHideAnim;
    public static Image showHideCam0;
    public static Image showHideCam1;
    public static Image showHideForcefields0;
    public static Image showHideForcefields1;
    public static Image showHideGeometry0;
    public static Image showHideGeometry1;
    public static Image showHideGoobs0;
    public static Image showHideGoobs1;
    public static Image showHideGoobs2;
    public static Image showHideImages0;
    public static Image showHideImages1;
    public static Image showHideLabels0;
    public static Image showHideLabels1;
    public static Image showHideParticles0;
    public static Image showHideParticles1;

    static {
        try {
            showHideAnim0 = FileManager.getIcon("ButtonIcons\\ShowHide\\showhide_anim_disabled.png");
            showHideAnim = FileManager.getIcon("ButtonIcons\\ShowHide\\showhide_anim.png");
            showHideCam0 = FileManager.getIcon("ButtonIcons\\ShowHide\\showhide_cam_disabled.png");
            showHideCam1 = FileManager.getIcon("ButtonIcons\\ShowHide\\showhide_cam.png");
            showHideForcefields0 = FileManager.getIcon("ButtonIcons\\ShowHide\\showhide_forcefields_disabled.png");
            showHideForcefields1 = FileManager.getIcon("ButtonIcons\\ShowHide\\showhide_forcefields.png");
            showHideGeometry0 = FileManager.getIcon("ButtonIcons\\ShowHide\\showhide_geometry_disabled.png");
            showHideGeometry1 = FileManager.getIcon("ButtonIcons\\ShowHide\\showhide_geometry.png");
            showHideGoobs0 = FileManager.getIcon("ButtonIcons\\ShowHide\\showhide_goobs_disabled.png");
            showHideGoobs1 = FileManager.getIcon("ButtonIcons\\ShowHide\\showhide_goobs_wireframe.png");
            showHideGoobs2 = FileManager.getIcon("ButtonIcons\\ShowHide\\showhide_goobs.png");
            showHideImages0 = FileManager.getIcon("ButtonIcons\\ShowHide\\showhide_images_disabled.png");
            showHideImages1 = FileManager.getIcon("ButtonIcons\\ShowHide\\showhide_images.png");
            showHideLabels0 = FileManager.getIcon("ButtonIcons\\ShowHide\\showhide_labels_disabled.png");
            showHideLabels1 = FileManager.getIcon("ButtonIcons\\ShowHide\\showhide_labels.png");
            showHideParticles0 = FileManager.getIcon("ButtonIcons\\ShowHide\\showhide_particles_disabled.png");
            showHideParticles1 = FileManager.getIcon("ButtonIcons\\ShowHide\\showhide_particles.png");
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static final int NO_UNSAVED_CHANGES = 0;
    public static final int UNSAVED_CHANGES = 1;
    public static final int UNSAVED_CHANGES_UNMODIFIABLE = 2;

    private static final Image noChangesImageOld;
    private static final Image changesImageOld;
    private static final Image changesUnmodifiableImageOld;

    private static final Image noChangesImageNew;
    private static final Image changesImageNew;
    private static final Image changesUnmodifiableImageNew;

    static {
        try {
            noChangesImageOld = FileManager.getIcon("ButtonIcons\\Level\\no_unsaved_changes_old.png");
            changesImageOld = FileManager.getIcon("ButtonIcons\\Level\\unsaved_changes_old.png");
            changesUnmodifiableImageOld = FileManager.getIcon("ButtonIcons\\Level\\unsaved_changes_unmodifiable_old.png");
            noChangesImageNew = FileManager.getIcon("ButtonIcons\\Level\\no_unsaved_changes_new.png");
            changesImageNew = FileManager.getIcon("ButtonIcons\\Level\\unsaved_changes_new.png");
            changesUnmodifiableImageNew = FileManager.getIcon("ButtonIcons\\Level\\unsaved_changes_unmodifiable_new.png");
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private Image currentStatusImage;

    private Tab levelTab;

    public Tab getLevelTab() {
        return levelTab;
    }

    public void setLevelTab(Tab levelTab) {
        this.levelTab = levelTab;
    }

    private int editingStatus;

    public int getEditingStatus() {
        return editingStatus;
    }

    public void setEditingStatus(int editingStatus, boolean shouldSelect) {
        this.editingStatus = editingStatus;
        if (editingStatus == NO_UNSAVED_CHANGES) {
            currentStatusImage = version == 1.3 ? noChangesImageOld : noChangesImageNew;
        } else if (editingStatus == UNSAVED_CHANGES) {
            currentStatusImage = version == 1.3 ? changesImageOld : changesImageNew;
        } else if (editingStatus == UNSAVED_CHANGES_UNMODIFIABLE) {
            currentStatusImage = version == 1.3 ? changesUnmodifiableImageOld : changesUnmodifiableImageNew;
        }

        AnchorPane pane = new AnchorPane();

        pane.getChildren().add(new ImageView(currentStatusImage));

        TreeItem<EditorObject> root = Main.getHierarchy().getRoot();

        //ChatGPT
        StackPane graphicContainer = new StackPane();
        graphicContainer.prefWidthProperty().bind(levelTab.getTabPane().tabMaxWidthProperty());
        StackPane.setAlignment(pane, Pos.CENTER_LEFT);
        graphicContainer.getChildren().addAll(pane, new Label(getLevelName()));
        levelTab.setGraphic(graphicContainer);
        //End ChatGPT
        TabPane god = levelTab.getTabPane();

        int index = god.getTabs().indexOf(levelTab);
        god.getTabs().remove(levelTab);
        god.getTabs().add(index, levelTab);
        if (shouldSelect) {
            god.getSelectionModel().select(levelTab);
            Main.getHierarchy().setRoot(root);
        }
    }

    private double offsetX = 0;
    private double offsetY = 0;
    private double zoom = 1;

    public double getOffsetX() {
        return offsetX;
    }

    public void setOffsetX(double offsetX) {
        this.offsetX = offsetX;
    }

    public double getOffsetY() {
        return offsetY;
    }

    public void setOffsetY(double offsetY) {
        this.offsetY = offsetY;
    }

    public double getZoom() {
        return zoom;
    }

    public void setZoom(double zoom) {
        this.zoom = zoom;
    }

    private void cameraToMiddleOfLevel() {
        double sceneWidth = sceneObject.getDouble("maxx") - sceneObject.getDouble("minx");
        double sceneHeight = sceneObject.getDouble("maxy") - sceneObject.getDouble("miny");

        double middleX = (sceneObject.getDouble("minx") + sceneObject.getDouble("maxx")) / 2;
        double middleY = (sceneObject.getDouble("miny") + sceneObject.getDouble("maxy")) / 2;

        double canvasWidth = Main.getSplitPane().getDividers().get(0).getPosition() * Main.getSplitPane().getWidth();
        double canvasHeight = Main.getSplitPane().getHeight();

        offsetX = canvasWidth / 2 - middleX;
        offsetY = canvasHeight / 2 + middleY;

        double zoomX = canvasWidth / sceneWidth;
        double zoomY = canvasHeight / sceneHeight;

        zoom = Math.min(zoomX, zoomY);

        offsetX = (offsetX - canvasWidth / 2) * zoom + canvasWidth / 2;
        offsetY = (offsetY - canvasHeight / 2) * zoom + canvasHeight / 2;
    }

    public WorldLevel(ArrayList<EditorObject> _scene, ArrayList<EditorObject> _level, ArrayList<EditorObject> _resources, ArrayList<EditorObject> _addin, ArrayList<EditorObject> _text, double _version){

        scene = _scene;
        level = _level;
        resources = _resources;
        addin = _addin;
        text = _text;
        version = _version;

        for (EditorObject object : scene) {
            if (object instanceof Scene) {
                sceneObject = object;
            }
        }
        for (EditorObject object : level) {
            if (object instanceof Level) {
                levelObject = object;
            }
        }
        for (EditorObject object : resources) {
            if (object instanceof ResourceManifest) {
                resourcesObject = object;
            }
        }
        for (EditorObject object : addin) {
            object.getTreeItem().setExpanded(true);
            if (object instanceof Addin) {
                addinObject = object;
            }
        }
        for (EditorObject object : text) {
            object.getTreeItem().setExpanded(true);
            if (object instanceof TextStrings) {
                textObject = object;
            }
        }

        for (EditorObject sceneObject : scene) {
            if (sceneObject instanceof com.WorldOfGoo.Scene.Label label) {
                if (label.getAttribute("text") != null) {
                    boolean alreadyInText = false;
                    for (EditorObject textObject : text) {
                        if (textObject instanceof TextString string) {
                            if (string.getAttribute("id").equals(label.getAttribute("text"))) {
                                alreadyInText = true;
                                break;
                            }
                        }
                    }
                    if (!alreadyInText) {
                        try {
                            EditorObject myString = GlobalResourceManager.getText(label.getAttribute("text"), version).deepClone(textObject);
                            text.add(myString);
                        } catch (Exception e) {
                            Main.failedResources.add(("\"" + label.getAttribute("text") + "\" (version " + version + ")"));
                        }
                    }
                }
            }
        }

        for (EditorObject levelObject : level) {
            if (levelObject instanceof Signpost signpost) {
                if (signpost.getAttribute("text") != null) {
                    boolean alreadyInText = false;
                    for (EditorObject textObject : text) {
                        if (textObject instanceof TextString string) {
                            if (string.getAttribute("id").equals(signpost.getAttribute("text"))) {
                                alreadyInText = true;
                                break;
                            }
                        }
                    }
                    if (!alreadyInText) {
                        try {
                            GlobalResourceManager.getText(signpost.getAttribute("text"), version).deepClone(textObject);
                        } catch (Exception e) {
                            Main.failedResources.add(("\"" + signpost.getAttribute("text") + "\" (version " + version + ")"));
                            EditorObject string = EditorObject.create("string", new EditorAttribute[0], textObject);
                            string.setAttribute("id", signpost.getAttribute("text"));
                        }
                    }
                }
            }
        }

        cameraToMiddleOfLevel();
    }
}
