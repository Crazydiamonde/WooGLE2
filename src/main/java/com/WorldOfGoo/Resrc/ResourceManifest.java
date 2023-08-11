package com.WorldOfGoo.Resrc;

import com.WooGLEFX.Structures.EditorAttribute;
import com.WooGLEFX.Structures.EditorObject;
import com.WooGLEFX.Structures.InputField;

public class ResourceManifest extends EditorObject {
    public ResourceManifest(EditorObject _parent) {
        super(_parent);
        setRealName("ResourceManifest");
        setNameAttribute(new EditorAttribute(this, "", "", "", new InputField("", InputField.NULL), false));
    }
}
