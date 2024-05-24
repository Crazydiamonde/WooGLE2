package com.woogleFX.functions.undoHandling.userActions;

import com.woogleFX.editorObjects.EditorObject;
import com.woogleFX.functions.LevelManager;
import com.woogleFX.functions.ObjectManager;

public class ObjectCreationAction extends UserAction {

    private final int position;
    public ObjectCreationAction(EditorObject object, int position) {
        super(object);
        this.position = position;
    }


    @Override
    public UserAction getInverse() {
        return new ObjectDestructionAction(getObject(), position);
    }


    @Override
    public void execute() {
        ObjectManager.create(LevelManager.getLevel(), getObject(), position);
    }

}
