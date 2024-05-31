package com.woogleFX.engine.undoHandling.userActions;

import com.woogleFX.editorObjects.EditorObject;

public abstract class UserAction {

    private final EditorObject object;
    public final EditorObject getObject() {
        return object;
    }


    public UserAction(EditorObject object) {
        this.object = object;
    }


    public abstract UserAction getInverse();


    public abstract void execute();

}
