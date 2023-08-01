package com.WooGLEFX.Structures.UserActions;

import com.WooGLEFX.Structures.EditorObject;
import com.WooGLEFX.Structures.UserActions.UserAction;

public class ObjectDestructionAction extends UserAction {

    private int position;

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public ObjectDestructionAction(EditorObject object, int position) {
        super(object);
        this.position = position;
    }
}
