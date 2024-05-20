package com.WorldOfGoo.Addin;

import com.WooGLEFX.EditorObjects.EditorObject;
import com.WooGLEFX.EditorObjects.InputField;
import com.WooGLEFX.Structures.SimpleStructures.MetaEditorAttribute;

public class Addin extends EditorObject {

    public Addin(EditorObject _parent) {
        super(_parent, "addin", "addin\\addin");

        addAttribute("spec-version", InputField.NUMBER_POSITIVE).assertRequired();
        setAttribute("spec-version", "1.1");

        setMetaAttributes(MetaEditorAttribute.parse("spec-version,"));

    }


    @Override
    public String getName() {
        return getAttribute("spec-version").stringValue();
    }

}
