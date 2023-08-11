package com.WorldOfGoo.Level;

import com.WooGLEFX.Structures.EditorAttribute;
import com.WooGLEFX.Structures.EditorObject;
import com.WooGLEFX.Structures.InputField;

public class Endonnogeom extends EditorObject {
    public Endonnogeom(EditorObject _parent) {
        super(_parent);
        setNameAttribute(new EditorAttribute(this, "", "", "", new InputField("", InputField.NULL), false));
        setRealName("endonnogeom");
    }
}
