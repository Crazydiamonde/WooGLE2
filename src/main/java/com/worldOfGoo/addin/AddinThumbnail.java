package com.worldOfGoo.addin;

import com.woogleFX.editorObjects.EditorObject;
import com.woogleFX.editorObjects.InputField;
import com.woogleFX.structures.simpleStructures.MetaEditorAttribute;

public class AddinThumbnail extends EditorObject {

    public AddinThumbnail(EditorObject _parent) {
        super(_parent, "thumbnail", "addin\\addin");

        addAttribute("value", InputField.ANY).assertRequired();
        addAttribute("type", InputField.IMAGE_TYPE).assertRequired();
        addAttribute("width", InputField.NUMBER_POSITIVE).assertRequired();
        addAttribute("height", InputField.NUMBER_POSITIVE).assertRequired();

        setMetaAttributes(MetaEditorAttribute.parse("value,type,width,height,"));

    }


    @Override
    public String getName() {
        return getAttribute("value").stringValue();
    }

}
