package com.woogleFX.functions.undoHandling.userActions;

import com.woogleFX.editorObjects.EditorObject;

public class AttributeChangeAction extends UserAction {

    private final String attributeName;
    public String getAttributeName() {
        return attributeName;
    }


    private final String oldValue;
    public String getOldValue() {
        return oldValue;
    }


    private final String newValue;
    public String getNewValue() {
        return newValue;
    }


    public AttributeChangeAction(EditorObject object, String attributeName, String oldValue, String newValue) {
        super(object);
        this.attributeName = attributeName;
        this.oldValue = oldValue;
        this.newValue = newValue;
    }
}
