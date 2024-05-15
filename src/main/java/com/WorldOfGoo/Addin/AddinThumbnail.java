package com.WorldOfGoo.Addin;

import com.WooGLEFX.Structures.EditorObject;
import com.WooGLEFX.Structures.InputField;
import com.WooGLEFX.Structures.SimpleStructures.MetaEditorAttribute;

public class AddinThumbnail extends EditorObject {

    public AddinThumbnail(EditorObject _parent) {
        super(_parent);
        setRealName("thumbnail");

        addAttribute("value", InputField.ANY).assertRequired();
        addAttribute("type", InputField.IMAGE_TYPE).assertRequired();
        addAttribute("width", InputField.NUMBER_POSITIVE).assertRequired();
        addAttribute("height", InputField.NUMBER_POSITIVE).assertRequired();

        setNameAttribute(getAttribute2("value"));
        setMetaAttributes(MetaEditorAttribute.parse("value,type,width,height,"));

    }

}
