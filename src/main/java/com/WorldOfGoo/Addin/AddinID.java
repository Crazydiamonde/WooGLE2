package com.WorldOfGoo.Addin;

import com.WooGLEFX.Structures.EditorObject;
import com.WooGLEFX.Structures.InputField;
import com.WooGLEFX.Structures.SimpleStructures.MetaEditorAttribute;

public class AddinID extends EditorObject {
    public AddinID(EditorObject _parent) {
        super(_parent);
        setRealName("id");
        addAttribute("value", "", InputField.ANY, true);
        setNameAttribute(getAttribute2("value"));
        setMetaAttributes(MetaEditorAttribute.parse("value,"));
    }
}
