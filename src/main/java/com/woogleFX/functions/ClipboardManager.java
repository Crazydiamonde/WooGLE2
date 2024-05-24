package com.woogleFX.functions;

import com.woogleFX.engine.ClipboardHandler;
import com.woogleFX.engine.fx.FXHierarchy;
import com.woogleFX.engine.fx.FXPropertiesView;
import com.woogleFX.engine.SelectionManager;
import com.woogleFX.editorObjects.objectCreators.ObjectAdder;
import com.woogleFX.functions.undoHandling.UndoManager;
import com.woogleFX.editorObjects.EditorObject;
import com.woogleFX.functions.undoHandling.userActions.ObjectCreationAction;
import com.woogleFX.structures.WorldLevel;
import com.worldOfGoo.level.BallInstance;
import com.worldOfGoo.level.Strand;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;

public class ClipboardManager {

    public static void cut() {
        if (LevelManager.getLevel().getSelected() != null) {
            copy();
            ObjectManager.delete(LevelManager.getLevel());
        }
    }


    public static void copy() {
        if (LevelManager.getLevel().getSelected() != null && FXPropertiesView.getPropertiesView().getEditingCell() == null) {
            String clipboard = ClipboardHandler.exportToClipBoardString(LevelManager.getLevel().getSelected());
            ClipboardContent clipboardContent = new ClipboardContent();
            clipboardContent.putString(clipboard);
            Clipboard.getSystemClipboard().setContent(clipboardContent);
        }
    }


    public static void paste() {

        WorldLevel level = LevelManager.getLevel();
        if (level == null) return;

        if (FXPropertiesView.getPropertiesView().getEditingCell() != null) return;

        String clipboard = Clipboard.getSystemClipboard().getString();
        if (clipboard == null) return;

        EditorObject object = ClipboardHandler.importFromClipboardString(clipboard);
        if (object == null) return;

        if (object instanceof BallInstance) {
            ObjectAdder.fixGooBall(object);
            for (EditorObject editorObject : level.getLevel()) {
                if (editorObject instanceof Strand strand) {
                    strand.update();
                }
            }
        }

        ObjectManager.create(level, object, 0);
        SelectionManager.setSelected(object);
        UndoManager.registerChange(new ObjectCreationAction(object, object.getParent().getChildren().indexOf(object)));
        level.redoActions.clear();
        FXHierarchy.getHierarchy().refresh();

    }

}
