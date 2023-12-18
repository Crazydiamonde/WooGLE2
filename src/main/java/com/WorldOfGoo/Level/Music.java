package com.WorldOfGoo.Level;

import com.WooGLEFX.Structures.EditorObject;
import com.WooGLEFX.Structures.InputField;
import com.WooGLEFX.Structures.SimpleStructures.MetaEditorAttribute;

public class Music extends EditorObject {
    public Music(EditorObject _parent) {
        super(_parent);
        setRealName("music");
        addAttribute("id", "", InputField.ANY, true);
        setNameAttribute(getAttribute2("id"));
        setMetaAttributes(MetaEditorAttribute.parse("id,"));
    }
}
