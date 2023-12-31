package com.WorldOfGoo.Addin;

import com.WooGLEFX.Structures.EditorObject;
import com.WooGLEFX.Structures.InputField;
import com.WooGLEFX.Structures.SimpleStructures.MetaEditorAttribute;

public class AddinDepends extends EditorObject {
    public AddinDepends(EditorObject _parent) {
        super(_parent);
        setRealName("depends");
        addAttribute("ref", "", InputField.ANY, true);
        addAttribute("min-version", "", InputField.NUMBER_POSITIVE, true);
        addAttribute("max-version", "", InputField.NUMBER_POSITIVE, true);
        setNameAttribute(getAttribute2("ref"));
        setMetaAttributes(MetaEditorAttribute.parse("ref,min-version,max-version,"));
    }
}
