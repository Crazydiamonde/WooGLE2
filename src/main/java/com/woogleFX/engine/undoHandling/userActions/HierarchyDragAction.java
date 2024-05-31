package com.woogleFX.engine.undoHandling.userActions;

import com.woogleFX.editorObjects.EditorObject;
import com.woogleFX.engine.fx.hierarchy.FXHierarchy;
import com.woogleFX.engine.fx.hierarchy.HierarchyManager;

public class HierarchyDragAction extends UserAction {

    private final int toPosition;
    private final int fromPosition;
    public HierarchyDragAction(EditorObject object, int fromPosition, int toPosition) {
        super(object);
        this.fromPosition = fromPosition;
        this.toPosition = toPosition;
    }


    @Override
    public UserAction getInverse() {

        int extra;
        if (toPosition > fromPosition) extra = getObject().getChildren().size();
        else extra = 0;

        return new HierarchyDragAction(getObject(), toPosition - extra, fromPosition);
    }


    @Override
    public void execute() {
        HierarchyManager.setOldDropIndex(fromPosition);
        HierarchyManager.handleDragDrop(FXHierarchy.getHierarchy(), toPosition);
    }

}
