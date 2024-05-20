package com.WooGLEFX.Functions.InputEvents;

import com.WooGLEFX.Functions.*;
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
                UndoManager.undo();
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
