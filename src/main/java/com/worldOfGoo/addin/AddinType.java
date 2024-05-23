package com.worldOfGoo.addin;

import com.woogleFX.editorObjects.EditorObject;
import com.woogleFX.editorObjects.InputField;
import com.woogleFX.structures.simpleStructures.MetaEditorAttribute;

public class AddinType extends EditorObject {

    public AddinType(EditorObject _parent) {
        super(_parent, "type", "addin\\addin");

        addAttribute("value", InputField.ANY).assertRequired().setValue("level");

        setMetaAttributes(MetaEditorAttribute.parse(","));

    }


    @Override
    public String getName() {
        return getAttribute("value").stringValue();
    }

}
