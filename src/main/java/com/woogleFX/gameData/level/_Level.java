package com.woogleFX.gameData.level;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Stack;

import com.woogleFX.editorObjects.EditorObject;
import com.woogleFX.editorObjects.ObjectUtil;
import com.woogleFX.engine.SelectionManager;
import com.woogleFX.engine.fx.FXContainers;
import com.woogleFX.engine.fx.hierarchy.FXHierarchy;
import com.woogleFX.file.resourceManagers.ResourceManager;
import com.woogleFX.engine.LevelManager;
import com.woogleFX.engine.undoHandling.userActions.UserAction;
import com.worldOfGoo.level.Signpost;
import com.worldOfGoo.resrc.Font;
import com.worldOfGoo.resrc.ResrcImage;
import com.worldOfGoo.resrc.SetDefaults;
import com.worldOfGoo.resrc.Sound;
import com.worldOfGoo.scene.Label;
import com.worldOfGoo.text.TextString;

public class _Level {

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


    private EditorObject[] selected = new EditorObject[]{};
    public EditorObject[] getSelected() {
        return selected;
    }
    public void setSelected(EditorObject[] selected) {
        this.selected = selected;
        SelectionManager.goToSelectedInHierarchy();
    }
    public void clearSelection() {
        selected = new EditorObject[]{};
        FXHierarchy.getHierarchy().getSelectionModel().clearSelection();
    }
    public boolean isSelected(EditorObject editorObject) {
        return Arrays.stream(selected).anyMatch(e -> e == editorObject);
    }


    private String currentlySelectedSection = "Scene";
    public String getCurrentlySelectedSection() {
        return currentlySelectedSection;
    }
    public void setCurrentlySelectedSection(String s) {
        this.currentlySelectedSection = s;
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

        zoom = Math.min(Math.abs(zoomX), Math.abs(zoomY));

        offsetX = (offsetX - canvasWidth / 2) * zoom + canvasWidth / 2;
        offsetY = (offsetY - canvasHeight / 2) * zoom + canvasHeight / 2;
    }


    public _Level(ArrayList<EditorObject> scene,
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

        reAssignSetDefaultsToAllResources();

        for (EditorObject editorObject : scene) if (editorObject instanceof Label label) {
            tryToAddText(label.getAttribute("text").stringValue());
        }

        for (EditorObject editorObject : level) if (editorObject instanceof Signpost signpost) {
            tryToAddText(signpost.getAttribute("text").stringValue());
        }

        cameraToMiddleOfLevel();

    }


    private void tryToAddText(String id) {
        TextString textString;
        try {
            textString = ResourceManager.getText(null, id, version);
        } catch (FileNotFoundException ignored) {
            return;
        }
        boolean notAlreadyHere = false;
        for (EditorObject object : text) {
            if (object instanceof TextString) {
                if (object.getAttribute("id").stringValue().equals(id)) {
                    notAlreadyHere = true;
                    break;
                }
            }
        }
        if (!notAlreadyHere) {
            ObjectUtil.deepClone(textString, text.get(0));
            text.add(textString);
        }
    }


    public void reAssignSetDefaultsToAllResources() {

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

    }

}
