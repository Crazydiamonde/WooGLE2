package com.woogleFX.engine.undoHandling.userActions;

import com.woogleFX.editorObjects.EditorObject;
import com.woogleFX.engine.LevelManager;
import com.woogleFX.editorObjects.ObjectManager;
import com.woogleFX.engine.SelectionManager;

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
        SelectionManager.selectionMode();
    }

}
