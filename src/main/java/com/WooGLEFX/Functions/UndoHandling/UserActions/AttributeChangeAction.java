package com.WooGLEFX.Functions.UndoHandling.UserActions;

import com.WooGLEFX.EditorObjects.EditorObject;

public class AttributeChangeAction extends UserAction {
    private final String attributeName;
    private final String oldValue;
    private final String newValue;

    public String getAttributeName() {
        return attributeName;
    }

    public String getOldValue() {
        return oldValue;
    }

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
