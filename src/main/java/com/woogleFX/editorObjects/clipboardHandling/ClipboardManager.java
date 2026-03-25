package com.woogleFX.editorObjects.clipboardHandling;

import com.woogleFX.assets.Asset;
import com.woogleFX.editorObjects.EditorObject;
import com.woogleFX.engine.AssetManager;
import com.woogleFX.engine.fx.propertiesView.FXPropertiesView;
import com.woogleFX.editorObjects.objectCreators.ObjectAdder;
import com.woogleFX.editorObjects.ObjectManager;
import com.worldOfGoo.level.BallInstance;
import com.worldOfGoo2.level._2_Level_BallInstance;
import com.worldOfGoo2.level.Strand;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ClipboardManager {

    public static void cut() {
        if (AssetManager.getAsset().getSelectedComponents().length != 0) {
            copy();
            ObjectManager.delete(AssetManager.getAsset());
        }
    }


    public static void copy() {
        if (AssetManager.getAsset().getSelectedComponents().length != 0 && FXPropertiesView.getPropertiesView().getEditingCell() == null) {
            String clipboard = ClipboardHandler.exportToClipBoardString(AssetManager.getAsset().getSelectedObjects());
            ClipboardContent clipboardContent = new ClipboardContent();
            clipboardContent.putString(clipboard);
            Clipboard.getSystemClipboard().setContent(clipboardContent);
        }
    }


    public static void paste() {

        Asset asset = AssetManager.getAsset();
        if (asset == null) return;

        // Don't paste an object if the user is in a text box, since they probably want to paste in there instead.
        if (FXPropertiesView.getPropertiesView().getEditingCell() != null) return;

        String clipboard = Clipboard.getSystemClipboard().getString();
        if (clipboard == null) return;

        ArrayList<EditorObject> selectedList;
        try {
            EditorObject[] list = ClipboardHandler.importFromClipboardString(clipboard);
            if (list == null) return;
            selectedList = new ArrayList<>(List.of(list));
        } catch (Exception e) {
            // The clipboard string was probably malformed. This is fine, it just means we shouldn't paste anything.
            return;
        }

        for (EditorObject object : selectedList.toArray(EditorObject[]::new)) {

            // Not sure what this actually does...
            EditorObject parent;
            if (object.getParent() != null && Arrays.stream(object.getParent().getPossibleChildren())
                    .anyMatch(e -> e == object.getClass())) {
                parent = object.getParent();
            } else {
                parent = asset.getDefaultParent(object.getClass());
            }

            // Make sure the object can actually exist here
            if (parent == null || Arrays.stream(parent.getPossibleChildren()).noneMatch(e -> e == object.getClass())) {
                selectedList.remove(object);
                continue;
            }

            // TODO: put this ridiculous ballinstance stuff somewhere else
            if (object instanceof BallInstance) {
                ObjectAdder.fixGooBall(object);
                for (EditorObject EditorObject : asset.getObjects()) {
                    if (EditorObject instanceof com.worldOfGoo.level.Strand strand) {
                        strand.update();
                    }
                }
            }
            if (object instanceof _2_Level_BallInstance) {
                ObjectAdder.fixGooBall(object);
                for (EditorObject EditorObject : asset.getObjects()) {
                    if (EditorObject instanceof Strand strand) {
                        if (strand.getAttribute("ball1UID").stringValue().equals(object.getAttribute("uid").stringValue())
                        || strand.getAttribute("ball2UID").stringValue().equals(object.getAttribute("uid").stringValue()))
                            strand.update();
                    }
                }
            }

            // Initialize the newly pasted objects
            object.onLoaded(asset);
            object.update();
            // And move them to the cursor
            // TODO: when pasting a group of objects, paste them all relative to each other instead of right on the cursor
            ObjectAdder.adjustObjectLocation(object);
            // More initialization stuff
            ObjectManager.create(object, parent, parent.getChildren().size() - 1, parent.getTreeItem().getChildren().size() - 1);

        }

        // Select the new objects
        asset.setSelectedDiscreetly(selectedList.toArray(EditorObject[]::new));
        asset.setSelectedObjects(selectedList.toArray(EditorObject[]::new));

    }

}
