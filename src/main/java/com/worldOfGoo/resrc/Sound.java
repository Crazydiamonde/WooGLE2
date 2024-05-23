package com.worldOfGoo.resrc;

import com.woogleFX.editorObjects.EditorObject;
import com.woogleFX.editorObjects.InputField;
import com.woogleFX.structures.simpleStructures.MetaEditorAttribute;

public class Sound extends EditorObject {

    public Sound(EditorObject _parent) {
        super(_parent, "Sound", "resources\\sound");

        addAttribute("id", InputField.ANY).assertRequired();
        addAttribute("path", InputField.ANY).assertRequired();

        setMetaAttributes(MetaEditorAttribute.parse("id,path,"));

    }


    @Override
    public String getName() {
        return getAttribute("id").stringValue();
    }

}
