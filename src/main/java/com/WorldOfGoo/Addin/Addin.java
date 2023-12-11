package com.WorldOfGoo.Addin;

import com.WooGLEFX.Structures.EditorObject;
import com.WooGLEFX.Structures.InputField;
import com.WooGLEFX.Structures.SimpleStructures.MetaEditorAttribute;

public class Addin extends EditorObject {
    public Addin(EditorObject _parent) {
        super(_parent);
        setRealName("addin");
        addAttribute("spec-version", "", InputField.NUMBER_POSITIVE, true);
        setNameAttribute(getAttribute2("spec-version"));
        setMetaAttributes(MetaEditorAttribute.parse("spec-version,"));
    }
}
