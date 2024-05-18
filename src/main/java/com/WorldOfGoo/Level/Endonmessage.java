package com.WorldOfGoo.Level;

import com.WooGLEFX.Structures.EditorObject;
import com.WooGLEFX.Structures.InputField;
import com.WooGLEFX.Structures.SimpleStructures.MetaEditorAttribute;

public class Endonmessage extends EditorObject {

    public Endonmessage(EditorObject _parent) {
        super(_parent, "endonmessage");

        addAttribute("id", InputField.ANY).assertRequired();

        setMetaAttributes(MetaEditorAttribute.parse("id"));

    }


    @Override
    public String getName() {
        return getAttribute("id").stringValue();
    }

}
