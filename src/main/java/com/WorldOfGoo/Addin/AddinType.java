package com.WorldOfGoo.Addin;

import com.WooGLEFX.Structures.EditorObject;
import com.WooGLEFX.Structures.InputField;
import com.WooGLEFX.Structures.SimpleStructures.MetaEditorAttribute;

public class AddinType extends EditorObject {
    public AddinType(EditorObject _parent) {
        super(_parent);
        setRealName("type");
        addAttribute("value", "", InputField.ANY, true);
        setNameAttribute(getAttribute2("value"));
        setAttribute("value", "level");
        setMetaAttributes(MetaEditorAttribute.parse(","));
    }
}
