package com.WorldOfGoo.Addin;

import com.WooGLEFX.EditorObjects.EditorObject;
import com.WooGLEFX.EditorObjects.InputField;
import com.WooGLEFX.Structures.SimpleStructures.MetaEditorAttribute;

public class AddinAuthor extends EditorObject {

    public AddinAuthor(EditorObject _parent) {
        super(_parent, "author", "addin\\addin");

        addAttribute("value", InputField.ANY).assertRequired();

        setMetaAttributes(MetaEditorAttribute.parse("value,"));
    }


    @Override
    public String getName() {
        return getAttribute("value").stringValue();
    }
}
