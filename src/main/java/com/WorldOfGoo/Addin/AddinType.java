package com.WorldOfGoo.Addin;

import com.WooGLEFX.EditorObjects.EditorObject;
import com.WooGLEFX.EditorObjects.InputField;
import com.WooGLEFX.Structures.SimpleStructures.MetaEditorAttribute;

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
