package com.WorldOfGoo.Ball;

import com.WooGLEFX.Structures.EditorObject;
import com.WooGLEFX.Structures.InputField;
import com.WooGLEFX.Structures.SimpleStructures.MetaEditorAttribute;

public class Ball extends EditorObject {
    public Ball(EditorObject _parent) {
        super(_parent);
        addAttribute("name", "", InputField.ANY, true);
        addAttribute("shape", "circle,36,0.25", InputField.ANY, true);
        setMetaAttributes(MetaEditorAttribute.parse("name,shape,"));
    }
}
