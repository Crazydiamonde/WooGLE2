package com.WorldOfGoo.Addin;

import com.WooGLEFX.Structures.EditorAttribute;
import com.WooGLEFX.Structures.EditorObject;

public class AddinDependencies extends EditorObject {
    public AddinDependencies(EditorObject _parent) {
        super(_parent);
        setRealName("dependencies");
        setNameAttribute(EditorAttribute.NULL);
    }
}
