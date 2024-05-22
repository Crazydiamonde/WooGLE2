package com.woogleFX.functions.undoHandling.userActions;

import com.woogleFX.editorObjects.EditorObject;

public class HierarchyDragAction extends UserAction {

    private int toPosition;
    private int fromPosition;

    public int getToPosition() {
        return toPosition;
    }

    public int getFromPosition() {
        return fromPosition;
    }

    public void setToPosition(int toPosition) {
        this.toPosition = toPosition;
    }

    public void setFromPosition(int fromPosition) {
        this.fromPosition = fromPosition;
    }

    public HierarchyDragAction(EditorObject object, int fromPosition, int toPosition) {
        super(object);
        this.fromPosition = fromPosition;
        this.toPosition = toPosition;
    }

}
