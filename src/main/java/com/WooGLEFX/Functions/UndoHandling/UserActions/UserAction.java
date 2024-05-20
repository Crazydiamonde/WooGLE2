package com.WooGLEFX.Functions.UndoHandling.UserActions;

import com.WooGLEFX.EditorObjects.EditorObject;

public class UserAction {

    private final EditorObject object;

    public EditorObject getObject() {
        return object;
    }

    public UserAction(EditorObject object) {
        this.object = object;
    }

}
