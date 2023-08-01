package com.WorldOfGoo.Scene;

import com.WooGLEFX.Structures.EditorObject;
import com.WooGLEFX.Structures.InputField;
import com.WooGLEFX.Structures.SimpleStructures.MetaEditorAttribute;

public class Motor extends EditorObject {
    public Motor(EditorObject _parent) {
        super(_parent);
        setRealName("motor");
        addAttribute("body", "", InputField.ANY, true);
        addAttribute("speed", "-0.01", InputField.NUMBER, true);
        addAttribute("maxforce", "20", InputField.NUMBER, true);
        setMetaAttributes(MetaEditorAttribute.parse("body,maxforce,speed"));
    }
}
