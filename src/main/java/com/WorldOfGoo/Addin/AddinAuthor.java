package com.WorldOfGoo.Addin;

import com.WooGLEFX.Structures.EditorObject;
import com.WooGLEFX.Structures.InputField;
import com.WooGLEFX.Structures.SimpleStructures.MetaEditorAttribute;

public class AddinAuthor extends EditorObject {
    public AddinAuthor(EditorObject _parent) {
        super(_parent);
        setRealName("author");
        addAttribute("value", "", InputField.ANY, true);
        setNameAttribute(getAttribute2("value"));
        setMetaAttributes(MetaEditorAttribute.parse("value,"));
    }
}
