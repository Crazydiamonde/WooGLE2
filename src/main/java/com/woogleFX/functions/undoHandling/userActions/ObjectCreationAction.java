package com.woogleFX.functions.undoHandling.userActions;

import com.woogleFX.editorObjects.EditorObject;

public class ObjectCreationAction extends UserAction {

    private final int position;
    public int getPosition() {
        return position;
    }


    public ObjectCreationAction(EditorObject object, int position) {
        super(object);
        this.position = position;
    }

}
