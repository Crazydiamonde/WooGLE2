package com.WooGLEFX.Structures.UserActions;

import com.WooGLEFX.Structures.EditorObject;

public class UserAction {

    private final EditorObject object;

    public EditorObject getObject() {
        return object;
    }

    public UserAction(EditorObject object) {
        this.object = object;
    }

}
