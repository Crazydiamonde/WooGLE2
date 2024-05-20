package com.WorldOfGoo.Resrc;

import com.WooGLEFX.EditorObjects.EditorObject;
import com.WooGLEFX.EditorObjects.InputField;
import com.WooGLEFX.Structures.SimpleStructures.MetaEditorAttribute;

public class ResrcImage extends EditorObject {

    public ResrcImage(EditorObject _parent) {
        super(_parent, "Image", "resources\\resrcimage");

        addAttribute("id",   InputField.ANY).assertRequired();
        addAttribute("path", InputField.ANY).assertRequired();

        setMetaAttributes(MetaEditorAttribute.parse("id,path,"));

    }


    @Override
    public String getName() {
        return getAttribute("id").stringValue();
    }

}
