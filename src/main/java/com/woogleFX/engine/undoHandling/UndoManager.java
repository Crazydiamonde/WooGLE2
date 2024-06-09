package com.woogleFX.engine.undoHandling;

import com.woogleFX.engine.fx.*;
import com.woogleFX.engine.LevelManager;
import com.woogleFX.gameData.level.LevelTab;
import com.woogleFX.engine.undoHandling.userActions.*;
import com.woogleFX.gameData.level._Level;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;

import java.util.Stack;

public class UndoManager {

    public static void registerChange(UserAction... actions) {

        _Level level = LevelManager.getLevel();

        level.undoActions.add(actions);
        level.redoActions.clear();

        if (level.getEditingStatus() == LevelTab.NO_UNSAVED_CHANGES)
            level.setEditingStatus(LevelTab.UNSAVED_CHANGES, true);

        FXEditorButtons.buttonUndo.setDisable(false);
        FXMenu.undoItem.setDisable(false);

        FXEditorButtons.buttonRedo.setDisable(true);
        FXMenu.redoItem.setDisable(true);

    }


    public static void undo() {
        eitherDo(true);
    }


    public static void redo() {
        eitherDo(false);
    }


    private static void eitherDo(boolean isUndo) {

        _Level level = LevelManager.getLevel();

        Stack<UserAction[]> forwardActions = isUndo ? level.undoActions : level.redoActions;
        Stack<UserAction[]> backwardActions = isUndo ? level.redoActions : level.undoActions;

        Button forwardButton = isUndo ? FXEditorButtons.buttonUndo : FXEditorButtons.buttonRedo;
        Button backwardButton = isUndo ? FXEditorButtons.buttonRedo : FXEditorButtons.buttonUndo;

        MenuItem forwardMenuItem = isUndo ? FXMenu.undoItem : FXMenu.redoItem;
        MenuItem backwardMenuItem = isUndo ? FXMenu.redoItem : FXMenu.undoItem;

        if (forwardActions.isEmpty()) return;

        UserAction[] changes = forwardActions.pop();

        UserAction[] inverted = new UserAction[changes.length];
        for (int i = 0; i < changes.length; i++) inverted[i] = changes[changes.length - i - 1].getInverse();
        backwardActions.add(inverted);

        for (int i = changes.length - 1; i >= 0; i--) changes[i].getInverse().execute();

        if (level.undoActions.size() == level.getLastSavedUndoPosition()) {
            level.setEditingStatus(LevelTab.NO_UNSAVED_CHANGES, true);
        } else {
            level.setEditingStatus(LevelTab.UNSAVED_CHANGES, true);
        }

        backwardButton.setDisable(false);
        backwardMenuItem.setDisable(false);

        if (forwardActions.isEmpty()) {
            forwardButton.setDisable(true);
            forwardMenuItem.setDisable(true);
        }

    }

}
