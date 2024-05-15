package com.WorldOfGoo.Resrc;

import com.WooGLEFX.Structures.EditorObject;
import com.WooGLEFX.Structures.InputField;
import com.WooGLEFX.Structures.SimpleStructures.MetaEditorAttribute;

public class SetDefaults extends EditorObject {

    public SetDefaults(EditorObject _parent) {
        super(_parent);
        setRealName("SetDefaults");

        addAttribute("path", InputField.ANY).setDefaultValue("./").assertRequired();
        addAttribute("idprefix", InputField.ANY)                  .assertRequired();

        setNameAttribute(getAttribute2("path"));
        setMetaAttributes(MetaEditorAttribute.parse("path,idprefix,"));

    }

}
