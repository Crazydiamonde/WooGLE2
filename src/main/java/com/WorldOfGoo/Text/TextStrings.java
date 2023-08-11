package com.WorldOfGoo.Text;

import com.WooGLEFX.Structures.EditorAttribute;
import com.WooGLEFX.Structures.EditorObject;
import com.WooGLEFX.Structures.InputField;

public class TextStrings extends EditorObject {
    public TextStrings(EditorObject _parent) {
        super(_parent);
        setRealName("strings");
        setNameAttribute(new EditorAttribute(this, "", "", "", new InputField("", InputField.NULL), false));
    }
}
