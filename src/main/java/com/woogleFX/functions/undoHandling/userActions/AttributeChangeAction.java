package com.woogleFX.functions.undoHandling.userActions;

import com.woogleFX.editorObjects.EditorAttribute;
import com.woogleFX.engine.fx.FXHierarchy;
import com.woogleFX.engine.fx.FXPropertiesView;

public class AttributeChangeAction extends UserAction {

    private final EditorAttribute attribute;
    private final String oldValue;
    private final String newValue;
    public AttributeChangeAction(EditorAttribute attribute, String oldValue, String newValue) {
        super(null);
        this.attribute = attribute;
        this.oldValue = oldValue;
        this.newValue = newValue;
    }


    @Override
    public UserAction getInverse() {
        return new AttributeChangeAction(attribute, newValue, oldValue);
    }


    @Override
    public void execute() {
        attribute.setValue(newValue);
        FXHierarchy.getHierarchy().refresh();
        FXPropertiesView.getPropertiesView().refresh();
    }

}
