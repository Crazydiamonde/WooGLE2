package com.woogleFX.structures;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Stack;

import com.woogleFX.editorObjects.EditorObject;
import com.woogleFX.editorObjects.ObjectUtil;
import com.woogleFX.engine.fx.FXContainers;
import com.woogleFX.file.resourceManagers.ResourceManager;
import com.woogleFX.functions.LevelManager;
import com.woogleFX.structures.simpleStructures.LevelTab;
import com.woogleFX.structures.simpleStructures.VisibilitySettings;
import com.woogleFX.functions.undoHandling.userActions.UserAction;
import com.worldOfGoo.resrc.Font;
import com.worldOfGoo.resrc.ResrcImage;
import com.worldOfGoo.resrc.SetDefaults;
import com.worldOfGoo.resrc.Sound;
import com.worldOfGoo.scene.Label;
import com.worldOfGoo.text.TextString;

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

    private final ArrayList<EditorObject> resrc;
    public ArrayList<EditorObject> getResrc() {
        return resrc;
    }
    public EditorObject getResrcObject() {
        return resrc.get(0);
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


    private int lastSavedUndoPosition = 0;
    public int getLastSavedUndoPosition() {
        return lastSavedUndoPosition;
    }
    public void setLastSavedUndoPosition(int position) {
        this.lastSavedUndoPosition = position;
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
                      ArrayList<EditorObject> resrc,
                      ArrayList<EditorObject> addin,
                      ArrayList<EditorObject> text,
                      GameVersion version) {

        this.scene = scene;
        this.level = level;
        this.resrc = resrc;
        this.addin = addin;
        this.text = text;
        this.version = version;

        LevelManager.setLevel(this);

        for (EditorObject object : addin) object.getTreeItem().setExpanded(true);
        for (EditorObject object : text) object.getTreeItem().setExpanded(true);

        SetDefaults currentSetDefaults = null;

        for (EditorObject editorObject : resrc) {

            if (editorObject instanceof SetDefaults setDefaults) {
                currentSetDefaults = setDefaults;
            }

            else if (editorObject instanceof ResrcImage resrcImage) {
                resrcImage.setSetDefaults(currentSetDefaults);
            } else if (editorObject instanceof Sound sound) {
                sound.setSetDefaults(currentSetDefaults);
            } else if (editorObject instanceof Font font) {
                font.setSetDefaults(currentSetDefaults);
            }

        }

        for (EditorObject editorObject : scene) if (editorObject instanceof Label label) {
            try {
                TextString textString = ResourceManager.getText(null, label.getAttribute("text").stringValue(), version);
                text.add(ObjectUtil.deepClone(textString, text.get(0)));
            } catch (FileNotFoundException ignored) {

            }
        }

        cameraToMiddleOfLevel();

    }

}
