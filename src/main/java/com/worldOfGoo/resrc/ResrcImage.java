package com.worldOfGoo.resrc;

import com.woogleFX.editorObjects.EditorObject;
import com.woogleFX.editorObjects.InputField;
import com.woogleFX.structures.simpleStructures.MetaEditorAttribute;

public class ResrcImage extends EditorObject {

    public ResrcImage(EditorObject _parent) {
        super(_parent, "Image", "resources\\resrcimage");

        addAttribute("id",   InputField.ANY).assertRequired();
        addAttribute("path", InputField.ANY).assertRequired();

        // extra attribute from 1.5
        addAttribute("atlasid", InputField.ANY);

        setMetaAttributes(MetaEditorAttribute.parse("id,path,"));

    }


    @Override
    public String getName() {
        return getAttribute("id").stringValue();
    }

}
