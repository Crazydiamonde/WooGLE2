package com.WooGLEFX.Structures;

import java.util.ArrayList;
import java.util.Stack;

import com.WooGLEFX.EditorObjects.ObjectCreator;
import com.WooGLEFX.EditorObjects.ObjectUtil;
import com.WooGLEFX.Engine.FX.FXContainers;
import com.WooGLEFX.File.GlobalResourceManager;
import com.WooGLEFX.Functions.LevelLoader;
import com.WooGLEFX.Structures.SimpleStructures.LevelTab;
import com.WooGLEFX.Structures.SimpleStructures.VisibilitySettings;
import com.WooGLEFX.Structures.UserActions.UserAction;
import com.WorldOfGoo.Level.Signpost;
import com.WorldOfGoo.Scene.Label;
import com.WorldOfGoo.Text.TextString;

public class WorldLevel {

    public final Stack<UserAction[]> redoActions = new Stack<>();
    public final Stack<UserAction[]> undoActions = new Stack<>();

    private final ArrayList<EditorObject> scene;
    public ArrayList<EditorObject> getScene() {
        return scene;
    }
    public EditorObject getSceneObject() {
        return scene.get(0);
    }

    private final ArrayList<EditorObject> level;
    public ArrayList<EditorObject> getLevel() {
        return level;
    }
    public EditorObject getLevelObject() {
        return level.get(0);
    }

    private final ArrayList<EditorObject> resources;
    public ArrayList<EditorObject> getResources() {
        return resources;
    }
    public EditorObject getResourcesObject() {
        return resources.get(0);
    }

    private final ArrayList<EditorObject> addin;
    public ArrayList<EditorObject> getAddin() {
        return addin;
    }
    public EditorObject getAddinObject() {
        return addin.get(0);
    }

    private final ArrayList<EditorObject> text;
    public ArrayList<EditorObject> getText() {
        return text;
    }
    public EditorObject getTextObject() {
        return text.get(0);
    }


    private LevelTab levelTab;
    public LevelTab getLevelTab() {
        return levelTab;
    }
    public void setLevelTab(LevelTab levelTab) {
        this.levelTab = levelTab;
    }


    private double offsetX = 0;
    public double getOffsetX() {
        return offsetX;
    }
    public void setOffsetX(double offsetX) {
        this.offsetX = offsetX;
    }


    private double offsetY = 0;
    public double getOffsetY() {
        return offsetY;
    }
    public void setOffsetY(double offsetY) {
        this.offsetY = offsetY;
    }


    private double zoom = 1;
    public double getZoom() {
        return zoom;
    }
    public void setZoom(double zoom) {
        this.zoom = zoom;
    }


    private final VisibilitySettings visibilitySettings = new VisibilitySettings();
    public VisibilitySettings getVisibilitySettings() {
        return visibilitySettings;
    }


    private String levelName;
    public String getLevelName() {
        return levelName;
    }
    public void setLevelName(String levelName) {
        this.levelName = levelName;
    }


    private final GameVersion version;
    public GameVersion getVersion() {
        return version;
    }


    private int editingStatus;
    public int getEditingStatus() {
        return editingStatus;
    }
    public void setEditingStatus(int editingStatus, boolean shouldSelect) {
        this.editingStatus = editingStatus;
        this.levelTab.update(editingStatus, shouldSelect);
    }


    private EditorObject selected = null;
    public EditorObject getSelected() {
        return selected;
    }
    public void setSelected(EditorObject selected) {
        this.selected = selected;
    }
    public boolean isSelected(EditorObject editorObject) {
        return editorObject == selected;
    }


    private String currentlySelectedSection = "Scene";
    public String getCurrentlySelectedSection() {
        return currentlySelectedSection;
    }
    public void setCurrentlySelectedSection(String currentlySelectedSection) {
        this.currentlySelectedSection = currentlySelectedSection;
    }


    private void cameraToMiddleOfLevel() {

        EditorObject sceneObject = scene.get(0);

        double sceneWidth = sceneObject.getAttribute("maxx").doubleValue() - sceneObject.getAttribute("minx").doubleValue();
        double sceneHeight = sceneObject.getAttribute("maxy").doubleValue() - sceneObject.getAttribute("miny").doubleValue();

        double middleX = (sceneObject.getAttribute("minx").doubleValue() + sceneObject.getAttribute("maxx").doubleValue()) / 2;
        double middleY = (sceneObject.getAttribute("miny").doubleValue() + sceneObject.getAttribute("maxy").doubleValue()) / 2;

        double canvasWidth = FXContainers.getSplitPane().getDividers().get(0).getPosition() * FXContainers.getSplitPane().getWidth();
        double canvasHeight = FXContainers.getSplitPane().getHeight();

        offsetX = canvasWidth / 2 - middleX;
        offsetY = canvasHeight / 2 + middleY;

        double zoomX = canvasWidth / sceneWidth;
        double zoomY = canvasHeight / sceneHeight;

        zoom = Math.min(zoomX, zoomY);

        offsetX = (offsetX - canvasWidth / 2) * zoom + canvasWidth / 2;
        offsetY = (offsetY - canvasHeight / 2) * zoom + canvasHeight / 2;
    }


    public WorldLevel(ArrayList<EditorObject> scene,
                      ArrayList<EditorObject> level,
                      ArrayList<EditorObject> resources,
                      ArrayList<EditorObject> addin,
                      ArrayList<EditorObject> text,
                      GameVersion version) {

        this.scene = scene;
        this.level = level;
        this.resources = resources;
        this.addin = addin;
        this.text = text;
        this.version = version;

        for (EditorObject object : addin) object.getTreeItem().setExpanded(true);
        for (EditorObject object : text) object.getTreeItem().setExpanded(true);

        for (EditorObject sceneObject : scene) {
            if (sceneObject instanceof Label label) {
                boolean alreadyInText = false;
                for (EditorObject textObject : text) {
                    if (textObject instanceof TextString string) {
                        if (string.getAttribute("id").stringValue().equals(label.getAttribute("text").stringValue())) {
                            alreadyInText = true;
                            break;
                        }
                    }
                }
                if (!alreadyInText) {
                    try {
                        EditorObject myString = ObjectUtil.deepClone(GlobalResourceManager.getText(label.getAttribute("text").stringValue(), version), text.get(0));
                        text.add(myString);
                    } catch (Exception e) {
                        LevelLoader.failedResources.add(("Level text \"" + label.getAttribute("text").stringValue() + "\" (version " + version + ")"));
                    }
                }
            }
        }

        for (EditorObject levelObject : level) {
            if (levelObject instanceof Signpost signpost) {
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
                        ObjectUtil.deepClone(GlobalResourceManager.getText(signpost.getAttribute("text").stringValue(), version), text.get(0));
                    } catch (Exception e) {
                        LevelLoader.failedResources.add(("Level text \"" + signpost.getAttribute("text") + "\" (version " + version + ")"));
                        EditorObject string = ObjectCreator.create("string", text.get(0));
                        string.setAttribute("id", signpost.getAttribute("text"));
                    }
                }
            }
        }

        cameraToMiddleOfLevel();
    }

}
