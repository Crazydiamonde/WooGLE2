package com.woogleFX.functions.undoHandling.userActions;

import com.woogleFX.editorObjects.EditorObject;

public class ObjectDestructionAction extends UserAction {

    private final int position;
    public int getPosition() {
        return position;
    }


    public ObjectDestructionAction(EditorObject object, int position) {
        super(object);
        this.position = position;
    }

}
