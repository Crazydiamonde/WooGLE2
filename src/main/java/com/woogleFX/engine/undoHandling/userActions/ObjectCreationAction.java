package com.woogleFX.engine.undoHandling.userActions;

import com.woogleFX.editorObjects.EditorObject;
import com.woogleFX.editorObjects.ObjectManager;
import com.woogleFX.engine.SelectionManager;

public class ObjectCreationAction extends UserAction {

    private final EditorObject parent;
    public EditorObject getParent() {
        return parent;
    }

    private final int position;
    public int getPosition() {
        return position;
    }

    private final int treePosition;
    public int getTreePosition() {
        return treePosition;
    }

    public ObjectCreationAction(EditorObject object) {
        super(object);
        parent = object.getParent();
        position = object.getParent().getChildren().indexOf(object);
        treePosition = object.getParent().getTreeItem().getChildren().indexOf(object.getTreeItem());
    }
    public ObjectCreationAction(EditorObject object, EditorObject parent, int position, int treePosition) {
        super(object);
        this.parent = parent;
        this.position = position;
        this.treePosition = treePosition;
    }


    @Override
    public UserAction getInverse() {
        return new ObjectDestructionAction(getObject(), parent, position, treePosition);
    }


    @Override
    public void execute() {
        ObjectManager.createItem(getObject(), parent, position, treePosition);
        SelectionManager.selectionMode();
    }

}
