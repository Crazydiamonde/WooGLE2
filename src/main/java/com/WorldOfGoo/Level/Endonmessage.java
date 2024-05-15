package com.WorldOfGoo.Level;

import com.WooGLEFX.Structures.EditorObject;
import com.WooGLEFX.Structures.InputField;
import com.WooGLEFX.Structures.SimpleStructures.MetaEditorAttribute;

public class Endonmessage extends EditorObject {

    public Endonmessage(EditorObject _parent) {
        super(_parent);
        setRealName("endonmessage");

        addAttribute("id", InputField.ANY).assertRequired();

        setNameAttribute(getAttribute2("id"));
        setMetaAttributes(MetaEditorAttribute.parse("id"));

    }

}
