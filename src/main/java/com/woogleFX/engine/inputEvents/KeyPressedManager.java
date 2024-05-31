package com.woogleFX.engine.inputEvents;

import com.woogleFX.editorObjects.ObjectManager;
import com.woogleFX.editorObjects.clipboardHandling.ClipboardManager;
import com.woogleFX.engine.LevelManager;
import com.woogleFX.engine.undoHandling.UndoManager;
import com.woogleFX.gameData.level.levelSaving.LevelUpdater;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class KeyPressedManager {

    public static void keyPressed(KeyEvent event) {
        if (event.getCode() == KeyCode.DELETE) {
            ObjectManager.delete(LevelManager.getLevel());
        }
        if (event.isControlDown()) {
            if (event.getCode() == KeyCode.S) {
                LevelUpdater.saveLevel(LevelManager.getLevel());
            }
            if (event.getCode() == KeyCode.Z) {
                if (event.isShiftDown()) UndoManager.redo();
                else UndoManager.undo();
            }
            if (event.getCode() == KeyCode.X) {
                ClipboardManager.cut();
            }
            if (event.getCode() == KeyCode.C) {
                ClipboardManager.copy();
            }
            if (event.getCode() == KeyCode.V) {
                ClipboardManager.paste();
            }
        }
    }

}
