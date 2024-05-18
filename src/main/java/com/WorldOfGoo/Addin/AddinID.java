package com.WorldOfGoo.Addin;

import com.WooGLEFX.Structures.EditorObject;
import com.WooGLEFX.Structures.InputField;
import com.WooGLEFX.Structures.SimpleStructures.MetaEditorAttribute;

public class AddinID extends EditorObject {

    public AddinID(EditorObject _parent) {
        super(_parent, "id");

        addAttribute("value", InputField.ANY).assertRequired();

        setMetaAttributes(MetaEditorAttribute.parse("value,"));

    }


    @Override
    public String getName() {
        return getAttribute("value").stringValue();
    }

}
