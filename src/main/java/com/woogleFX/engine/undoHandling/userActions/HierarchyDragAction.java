package com.woogleFX.engine.undoHandling.userActions;

import com.woogleFX.editorObjects.EditorObject;
import com.woogleFX.engine.fx.hierarchy.FXHierarchy;
import com.woogleFX.engine.fx.hierarchy.HierarchyManager;

public class HierarchyDragAction extends UserAction {

    private final EditorObject fromParent;
    private final int fromPosition;
    private final EditorObject toParent;
    private final int toPosition;
    public HierarchyDragAction(EditorObject object, EditorObject fromParent, int fromPosition, EditorObject toParent, int toPosition) {
        super(object);
        this.fromParent = fromParent;
        this.fromPosition = fromPosition;
        this.toPosition = toPosition;
        this.toParent = toParent;
    }


    @Override
    public UserAction getInverse() {
        if (toParent == fromParent && toPosition > fromPosition) return new HierarchyDragAction(getObject(), toParent, toPosition - 1, fromParent, fromPosition);
        if (toParent == fromParent && toPosition < fromPosition) return new HierarchyDragAction(getObject(), toParent, toPosition, fromParent, fromPosition + 1);

        return new HierarchyDragAction(getObject(), toParent, toPosition, fromParent, fromPosition);
    }


    @Override
    public void execute() {
        HierarchyManager.handleDragDrop(FXHierarchy.getHierarchy(), fromParent, fromPosition, toParent, toPosition);
        FXHierarchy.getHierarchy().getSelectionModel().clearSelection();
        FXHierarchy.getHierarchy().getSelectionModel().select(FXHierarchy.getHierarchy().getTreeItem(toPosition));
    }

}
