package com.woogleFX.functions.undoHandling.userActions;

import com.woogleFX.editorObjects.EditorObject;

public class UserAction {

    private final EditorObject object;

    public EditorObject getObject() {
        return object;
    }

    public UserAction(EditorObject object) {
        this.object = object;
    }

}
