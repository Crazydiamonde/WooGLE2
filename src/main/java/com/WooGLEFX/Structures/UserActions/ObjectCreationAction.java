package com.WooGLEFX.Structures.UserActions;

import com.WooGLEFX.Structures.EditorObject;

public class ObjectCreationAction extends UserAction {

    private int position;

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public ObjectCreationAction(EditorObject object, int position) {
        super(object);
        this.position = position;
    }
}
