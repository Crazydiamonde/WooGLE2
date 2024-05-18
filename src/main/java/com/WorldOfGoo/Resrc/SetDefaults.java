package com.WorldOfGoo.Resrc;

import com.WooGLEFX.Structures.EditorObject;
import com.WooGLEFX.Structures.InputField;
import com.WooGLEFX.Structures.SimpleStructures.MetaEditorAttribute;

public class SetDefaults extends EditorObject {

    public SetDefaults(EditorObject _parent) {
        super(_parent, "SetDefaults");

        addAttribute("path", InputField.ANY).setDefaultValue("./").assertRequired();
        addAttribute("idprefix", InputField.ANY)                  .assertRequired();

        setMetaAttributes(MetaEditorAttribute.parse("path,idprefix,"));

    }


    @Override
    public String getName() {
        return getAttribute("path").stringValue();
    }

}
