package com.WorldOfGoo.Addin;

import com.WooGLEFX.Structures.EditorAttribute;
import com.WooGLEFX.Structures.EditorObject;
import com.WooGLEFX.Structures.SimpleStructures.MetaEditorAttribute;

public class AddinLevels extends EditorObject {
    public AddinLevels(EditorObject _parent) {
        super(_parent);
        setRealName("levels");
        setNameAttribute(EditorAttribute.NULL);
        setMetaAttributes(MetaEditorAttribute.parse(","));
    }
}
