package com.worldOfGoo.addin;

import com.woogleFX.editorObjects.EditorObject;
import com.woogleFX.editorObjects.InputField;
import com.woogleFX.structures.simpleStructures.MetaEditorAttribute;

public class AddinVersion extends EditorObject {

    public AddinVersion(EditorObject _parent) {
        super(_parent, "version", "addin\\addin");

        addAttribute("value", InputField.NUMBER_POSITIVE).assertRequired();

        setMetaAttributes(MetaEditorAttribute.parse("value,"));

    }


    @Override
    public String getName() {
        return getAttribute("value").stringValue();
    }

}
