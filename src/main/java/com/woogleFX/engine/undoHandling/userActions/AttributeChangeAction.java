package com.woogleFX.engine.undoHandling.userActions;

import com.woogleFX.editorObjects.attributes.EditorAttribute;
import com.woogleFX.engine.fx.hierarchy.FXHierarchy;
import com.woogleFX.engine.fx.propertiesView.FXPropertiesView;

public class AttributeChangeAction extends UserAction {

    private final EditorAttribute attribute;
    private final String oldValue;
    private final String newValue;
    public AttributeChangeAction(EditorAttribute attribute, String oldValue, String newValue) {
        super(attribute.getObject());
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
        attribute.getObject().setAttribute(attribute.getName(), newValue);
        FXHierarchy.getHierarchy().refresh();
        FXPropertiesView.getPropertiesView().refresh();
    }

}
