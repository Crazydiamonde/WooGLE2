package com.WorldOfGoo.Resrc;

import com.WooGLEFX.EditorObjects.EditorObject;
import com.WooGLEFX.EditorObjects.InputField;
import com.WooGLEFX.Structures.SimpleStructures.MetaEditorAttribute;

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
