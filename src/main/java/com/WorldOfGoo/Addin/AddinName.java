package com.WorldOfGoo.Addin;

import com.WooGLEFX.Structures.EditorObject;
import com.WooGLEFX.Structures.InputField;
import com.WooGLEFX.Structures.SimpleStructures.MetaEditorAttribute;

public class AddinName extends EditorObject {

    public AddinName(EditorObject _parent) {
        super(_parent);
        setRealName("name");

        addAttribute("value", InputField.ANY).assertRequired();

        setNameAttribute(getAttribute2("value"));
        setMetaAttributes(MetaEditorAttribute.parse("value,"));

    }

}
