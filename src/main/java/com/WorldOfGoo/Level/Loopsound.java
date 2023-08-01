package com.WorldOfGoo.Level;

import com.WooGLEFX.Structures.EditorObject;
import com.WooGLEFX.Structures.InputField;
import com.WooGLEFX.Structures.SimpleStructures.MetaEditorAttribute;

public class Loopsound extends EditorObject {
    public Loopsound(EditorObject _parent) {
        super(_parent);
        setRealName("loopsound");
        addAttribute("id", "", InputField.ANY, true);
        setMetaAttributes(MetaEditorAttribute.parse("id,"));
    }
}
