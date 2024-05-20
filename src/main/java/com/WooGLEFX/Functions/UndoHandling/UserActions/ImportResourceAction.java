package com.WooGLEFX.Functions.UndoHandling.UserActions;

import com.WooGLEFX.EditorObjects.EditorObject;

public class ImportResourceAction extends UserAction {

    private final String path;

    public String getPath() {
        return path;
    }

    public ImportResourceAction(EditorObject object, String path) {
        super(object);
        this.path = path;
    }
}
