package com.WorldOfGoo.Addin;

import com.WooGLEFX.Structures.EditorAttribute;
import com.WooGLEFX.Structures.EditorObject;
import com.WooGLEFX.Structures.SimpleStructures.MetaEditorAttribute;

public class AddinLevel extends EditorObject {
    public AddinLevel(EditorObject _parent) {
        super(_parent);
        setRealName("level");
        setNameAttribute(EditorAttribute.NULL);
        setMetaAttributes(MetaEditorAttribute.parse(","));
    }
}
