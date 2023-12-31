package com.WorldOfGoo.Addin;

import com.WooGLEFX.Structures.EditorObject;
import com.WooGLEFX.Structures.InputField;
import com.WooGLEFX.Structures.SimpleStructures.MetaEditorAttribute;

public class AddinLevelDir extends EditorObject {
    public AddinLevelDir(EditorObject _parent) {
        super(_parent);
        setRealName("dir");
        addAttribute("value", "", InputField.ANY, true);
        setNameAttribute(getAttribute2("value"));
        setMetaAttributes(MetaEditorAttribute.parse("value,"));
    }
}
