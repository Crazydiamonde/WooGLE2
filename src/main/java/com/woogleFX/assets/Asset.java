package com.woogleFX.assets;

import com.woogleFX.editorObjects.EditorObject;
import com.woogleFX.editorObjects.ObjectUtil;
import com.woogleFX.editorObjects.objectComponents.ObjectComponent;
import com.woogleFX.editorObjects.objectCreators.ObjectCreator;
import com.woogleFX.engine.SelectionManager;
import com.woogleFX.engine.fx.AssetTab;
import com.woogleFX.engine.fx.FXCanvas;
import com.woogleFX.engine.fx.hierarchy.FXHierarchy;
import com.woogleFX.engine.undoHandling.UndoState;
import com.woogleFX.file.resourceManagers.ResourceManager;
import com.worldOfGoo.addin.addin;
import com.worldOfGoo.resrc.*;
import com.worldOfGoo.text.string;
import com.worldOfGoo.text.strings;
import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.control.TreeItem;

import java.io.File;
import java.util.*;

/** The general editing object for the editor. */
public abstract class Asset {

    /** This asset's name. */
    private String name;
    public final String getName() {
        return name;
    }
    public final void setName(String name) {
        this.name = name;
    }

    /** This asset's file. */
    private File file;
    public final File getFile() {
        return file;
    }
    public final void setFile(File file) {
        this.file = file;
    }


    public final Stack<UndoState> redoActions = new Stack<>();
    public final Stack<UndoState> undoActions = new Stack<>();

    /** @return A set of every object in this asset. */
    public abstract List<EditorObject> getObjects();


    private final Resources resources;
    public final Resources getResources() {
        return resources;
    }


    private addin addin;
    public final addin getAddin() {
        return addin;
    }


    private final strings strings;
    public strings getStrings() {
        return strings;
    }


    private double offsetX = 0;
    public final double getOffsetX() {
        return offsetX;
    }
    public final void setOffsetX(double offsetX) {
        this.offsetX = offsetX;
    }

    private double offsetY = 0;
    public final double getOffsetY() {
        return offsetY;
    }
    public final void setOffsetY(double offsetY) {
        this.offsetY = offsetY;
    }

    private double zoom = 1;
    public final double getZoom() {
        return zoom;
    }
    public final void setZoom(double zoom) {
        this.zoom = zoom;
    }


    private final VisibilitySettings visibilitySettings = new VisibilitySettings();
    public final VisibilitySettings getVisibilitySettings() {
        return visibilitySettings;
    }


    private int editingStatus;
    public final int getEditingStatus() {
        return editingStatus;
    }
    public final void setEditingStatus(int editingStatus, boolean shouldSelect) {
        this.editingStatus = editingStatus;
        getAssetTab().update(editingStatus, shouldSelect);
    }


    private EditorObject[] selectedObjects = new EditorObject[]{};
    public final EditorObject[] getSelectedObjects() {
        return selectedObjects;
    }
    public final void setSelectedObjects(EditorObject[] selectedObjects) {
        this.selectedObjects = selectedObjects;
        selectionLock = true;
        SelectionManager.goToSelectedInHierarchy();
        selectionLock = false;
    }

    private ObjectComponent[] selectedComponents = new ObjectComponent[]{};
    public final ObjectComponent[] getSelectedComponents() {
        return selectedComponents;
    }
    private boolean selectionLock = false;
    public final void lockSelection() {
        selectionLock = true;
    }
    public final void unlockSelection() {
        selectionLock = false;
    }
    public final void setSelectedDiscreetly(EditorObject[] selected) {
        if (selectionLock) return;
        this.selectedObjects = selected;
        ArrayList<ObjectComponent> components = new ArrayList<>();
        for (EditorObject editorObject : selected)
            for (ObjectComponent objectComponent : editorObject.getObjectComponents())
                if (objectComponent.isSelectable())
                    components.add(objectComponent);
        this.selectedComponents = components.toArray(ObjectComponent[]::new);
    }
    public final void setSelectedLoudly(EditorObject[] selected) {
        setSelectedDiscreetly(selected);
        setSelectedObjects(selected);
        if (selected[0].getObjectComponents().length != 0) {
            offsetX = -selected[0].getObjectComponents()[0].getX() * zoom + FXCanvas.getCanvas().getWidth() / 2;
            offsetY = -selected[0].getObjectComponents()[0].getY() * zoom + FXCanvas.getCanvas().getHeight() / 2;
        }

        // make sure the item is visible by expanding all of its parents
        TreeItem<EditorObject> item = selected[0].getTreeItem();
        while (item.getParent() != null) {
            item.getParent().setExpanded(true);
            item = item.getParent();
        }

        FXHierarchy.getHierarchy().getSelectionModel().select(selected[0].getTreeItem());

    }
    public final void setSelectedComponents(ObjectComponent[] selectedComponents) {
        this.selectedComponents = selectedComponents;
    }
    public final void clearSelection() {
        selectedObjects = new EditorObject[0];
        selectedComponents = new ObjectComponent[0];
        FXHierarchy.getHierarchy().getSelectionModel().clearSelection();
    }
    public final boolean isSelected(ObjectComponent editorObject) {
        return Arrays.stream(selectedComponents).anyMatch(e -> e == editorObject);
    }


    private int lastSavedUndoPosition = 0;
    public final int getLastSavedUndoPosition() {
        return lastSavedUndoPosition;
    }
    public final void setLastSavedUndoPosition(int position) {
        this.lastSavedUndoPosition = position;
    }

    private AssetTab assetTab;
    public final AssetTab getAssetTab() {
        return assetTab;
    }
    public final void setAssetTab(AssetTab assetTab) {
        this.assetTab = assetTab;
    }


    private final GameVersion version;
    public final GameVersion getVersion() {
        return version;
    }


    public Asset(GameVersion version, Resources resources, strings strings) {
        this.version = version;
        this.resources = resources;
        this.strings = strings;
    }


    public final void reAssignSetDefaultsToAllResources() {

        SetDefaults currentSetDefaults = null;

        for (EditorObject editorObject : resources.getChildren()) {

            if (editorObject instanceof SetDefaults setDefaults) {
                currentSetDefaults = setDefaults;
            }

            else if (editorObject instanceof ResourceInterface resourceInterface) {
                resourceInterface.setSetDefaults(currentSetDefaults);
            }

        }

    }



    public final void tryToAddText(String id) {
        string string = ResourceManager.getText(null, id, getVersion());
        if (string == null) {
            string = ObjectCreator.create(com.worldOfGoo.text.string.class, strings, getVersion());
            string.setAttribute("id", id);
        }
        boolean notAlreadyHere = false;
        for (EditorObject object : strings.getChildren()) {
            if (object.getAttribute("id").stringValue().equals(id)) {
                notAlreadyHere = true;
                break;
            }
        }
        if (!notAlreadyHere) {
            ObjectUtil.deepClone(string, strings);
        }
    }

    /** Sets the camera to the default position.
     * This is important for when the user gets lost in
     * the infinite canvas and needs to make it back to the asset. */
    public abstract void resetCamera();

    /** Checks asset-specific errors, like levelexit with no pipe. */
    public abstract List<AssetError> verify();

    /** Saves this asset to the given file.
     * @return Whether the save was successful. */
    public abstract boolean save(File file);

    /** Exports this asset to a goomod file.
     * @param includeAddinInfo Whether the addin info should be included in the goomod. */
    public abstract void export(boolean includeAddinInfo);

    /** Deletes the entire asset. */
    public abstract void delete();

    /** @return If the user has scaled beyond a reasonable range. */
    public abstract boolean isScaleTooFar(double scaleX, double scaleY);

    /** @return This asset's tabs. */
    public abstract List<Tab> getTabs();

    /**  */
    public abstract void onSetTab(Tab tab);

    public abstract Tab getTabForObject(EditorObject editorObject);

    public abstract EditorObject getDefaultParent(Class<? extends EditorObject> toAdd);

    public abstract Asset clone(String name);

    public void load() {
        this.addin = AssetLoader.getAddinInfo(this);

        List<EditorObject> miscObjects = new ArrayList<>();
        addin.addAllChildren(miscObjects);
        if (resources != null) resources.addAllChildren(miscObjects);
        for (EditorObject editorObject : miscObjects) {
            editorObject.onLoaded(this);
        }

    }

    public abstract boolean isBaseGame();

    public abstract ArrayList<Node> getGUIElements();

    public void onSet() {

    }

    /** @return any assets that this asset depends on in order to run.
     * ex. Custom levels might require custom Goo Balls */
    public List<Asset> getAllCurrentDependencies() {
        return new ArrayList<>();
    }

}
