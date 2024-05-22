package com.worldOfGoo.addin;

import com.woogleFX.editorObjects.EditorObject;
import com.woogleFX.editorObjects.InputField;
import com.woogleFX.structures.simpleStructures.MetaEditorAttribute;

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
