package com.woogleFX.engine.undoHandling.userActions;

import com.woogleFX.editorObjects.EditorObject;
import com.woogleFX.engine.AssetManager;
import com.woogleFX.editorObjects.ObjectManager;
import com.woogleFX.engine.SelectionManager;

public class ObjectDestructionAction extends UserAction {

    private final EditorObject parent;
    private final int position;
    private final int treePosition;
    public ObjectDestructionAction(EditorObject object) {
        super(object);
        parent = object.getParent();
        position = object.getParent().getChildren().indexOf(object);
        treePosition = object.getParent().getTreeItem().getChildren().indexOf(object.getTreeItem());
    }
    public ObjectDestructionAction(EditorObject object, EditorObject parent, int position, int treePosition) {
        super(object);
        this.parent = parent;
        this.position = position;
        this.treePosition = treePosition;
    }


    @Override
    public UserAction getInverse() {
        return new ObjectCreationAction(getObject(), parent, position, treePosition);
    }


    @Override
    public void execute() {
        ObjectManager.deleteItem(AssetManager.getAsset(), getObject());
        SelectionManager.selectionMode();
    }

}
