package com.woogleFX.functions.undoHandling.userActions;

import com.woogleFX.editorObjects.EditorObject;
import com.woogleFX.functions.LevelManager;
import com.woogleFX.functions.ObjectManager;

public class ObjectDestructionAction extends UserAction {

    private final int position;
    public ObjectDestructionAction(EditorObject object, int position) {
        super(object);
        this.position = position;
    }


    @Override
    public UserAction getInverse() {
        return new ObjectCreationAction(getObject(), position);
    }


    @Override
    public void execute() {
        ObjectManager.deleteItem(LevelManager.getLevel(), getObject(), false);
    }

}
