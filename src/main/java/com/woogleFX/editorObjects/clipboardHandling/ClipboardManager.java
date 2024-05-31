package com.woogleFX.editorObjects.clipboardHandling;

import com.woogleFX.engine.LevelManager;
import com.woogleFX.engine.fx.hierarchy.FXHierarchy;
import com.woogleFX.engine.fx.FXPropertiesView;
import com.woogleFX.editorObjects.objectCreators.ObjectAdder;
import com.woogleFX.editorObjects.ObjectManager;
import com.woogleFX.engine.undoHandling.UndoManager;
import com.woogleFX.editorObjects.EditorObject;
import com.woogleFX.engine.undoHandling.userActions.ObjectCreationAction;
import com.woogleFX.engine.undoHandling.userActions.UserAction;
import com.woogleFX.gameData.level._Level;
import com.worldOfGoo.level.BallInstance;
import com.worldOfGoo.level.Strand;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;

import java.util.ArrayList;

public class ClipboardManager {

    public static void cut() {
        if (LevelManager.getLevel().getSelected().length != 0) {
            copy();
            ObjectManager.delete(LevelManager.getLevel());
        }
    }


    public static void copy() {
        if (LevelManager.getLevel().getSelected().length != 0 && FXPropertiesView.getPropertiesView().getEditingCell() == null) {
            String clipboard = ClipboardHandler.exportToClipBoardString(LevelManager.getLevel().getSelected());
            ClipboardContent clipboardContent = new ClipboardContent();
            clipboardContent.putString(clipboard);
            Clipboard.getSystemClipboard().setContent(clipboardContent);
        }
    }


    public static void paste() {

        _Level level = LevelManager.getLevel();
        if (level == null) return;

        if (FXPropertiesView.getPropertiesView().getEditingCell() != null) return;

        String clipboard = Clipboard.getSystemClipboard().getString();
        if (clipboard == null) return;

        EditorObject[] selectedList = ClipboardHandler.importFromClipboardString(clipboard);
        if (selectedList == null) return;

        ArrayList<UserAction> objectCreationActions = new ArrayList<>();

        for (EditorObject object : selectedList) {

            ObjectAdder.adjustObjectLocation(object);

            if (object instanceof BallInstance) {
                ObjectAdder.fixGooBall(object);
                for (EditorObject editorObject : level.getLevel()) {
                    if (editorObject instanceof Strand strand) {
                        strand.update();
                    }
                }
            }

            ObjectManager.create(level, object, 0);
            objectCreationActions.add(new ObjectCreationAction(object, object.getParent().getChildren().indexOf(object)));
        }

        UndoManager.registerChange(objectCreationActions.toArray(new UserAction[0]));
        level.setSelected(selectedList);
        FXHierarchy.getHierarchy().refresh();

    }

}
