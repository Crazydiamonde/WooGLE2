package com.WorldOfGoo.Addin;

import com.WooGLEFX.Structures.EditorObject;
import com.WooGLEFX.Structures.InputField;
import com.WooGLEFX.Structures.SimpleStructures.MetaEditorAttribute;

public class AddinDescription extends EditorObject {
    public AddinDescription(EditorObject _parent) {
        super(_parent);
        setRealName("description");
        addAttribute("value", "", InputField.ANY, true);
        setNameAttribute(getAttribute2("value"));
        setMetaAttributes(MetaEditorAttribute.parse("value,"));
    }
}
