package com.WorldOfGoo.Resrc;

import com.WooGLEFX.Structures.EditorObject;
import com.WooGLEFX.Structures.InputField;
import com.WooGLEFX.Structures.SimpleStructures.MetaEditorAttribute;

public class Resources extends EditorObject {
    public Resources(EditorObject _parent) {
        super(_parent);
        setRealName("Resources");
        addAttribute("id", "", InputField.ANY, true);
        setMetaAttributes(MetaEditorAttribute.parse("id,"));
    }
}
