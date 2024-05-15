package com.WorldOfGoo.Addin;

import com.WooGLEFX.Structures.EditorObject;
import com.WooGLEFX.Structures.InputField;
import com.WooGLEFX.Structures.SimpleStructures.MetaEditorAttribute;

public class AddinLevelOCD extends EditorObject {

    public AddinLevelOCD(EditorObject _parent) {
        super(_parent);
        setRealName("ocd");

        addAttribute("type", InputField.OCD_TYPE)            .assertRequired();
        addAttribute("value", InputField.NUMBER_NON_NEGATIVE).assertRequired();

        setNameAttribute(getAttribute2("type"));
        setNameAttribute2(getAttribute2("value"));
        setMetaAttributes(MetaEditorAttribute.parse("type,value,"));

    }

}
