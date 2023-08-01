package com.WooGLEFX.Structures.UserActions;

import com.WooGLEFX.Structures.EditorObject;
import com.WooGLEFX.Structures.UserActions.UserAction;

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
