package com.WorldOfGoo.Level;

import com.WooGLEFX.Structures.EditorObject;
import com.WooGLEFX.Structures.InputField;
import com.WooGLEFX.Structures.SimpleStructures.MetaEditorAttribute;

public class Endoncollision extends EditorObject {
    public Endoncollision(EditorObject _parent) {
        super(_parent);
        setRealName("endoncollision");
        addAttribute("id1", "", InputField.GEOMETRY, true);
        addAttribute("id2", "", InputField.GEOMETRY, true);
        addAttribute("delay", "", InputField.NUMBER, true);
        setMetaAttributes(MetaEditorAttribute.parse("id1,id2,delay"));
    }
}
