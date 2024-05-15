package com.WorldOfGoo.Addin;

import com.WooGLEFX.Structures.EditorObject;
import com.WooGLEFX.Structures.InputField;
import com.WooGLEFX.Structures.SimpleStructures.MetaEditorAttribute;

public class AddinVersion extends EditorObject {

    public AddinVersion(EditorObject _parent) {
        super(_parent);
        setRealName("version");

        addAttribute("value", InputField.NUMBER_POSITIVE).assertRequired();

        setNameAttribute(getAttribute2("value"));
        setMetaAttributes(MetaEditorAttribute.parse("value,"));

    }

}
