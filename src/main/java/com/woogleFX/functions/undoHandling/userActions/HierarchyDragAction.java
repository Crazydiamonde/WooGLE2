package com.woogleFX.functions.undoHandling.userActions;

import com.woogleFX.editorObjects.EditorObject;

public class HierarchyDragAction extends UserAction {

    private final int toPosition;
    public int getToPosition() {
        return toPosition;
    }


    private final int fromPosition;
    public int getFromPosition() {
        return fromPosition;
    }

    public HierarchyDragAction(EditorObject object, int fromPosition, int toPosition) {
        super(object);
        this.fromPosition = fromPosition;
        this.toPosition = toPosition;
    }

}
