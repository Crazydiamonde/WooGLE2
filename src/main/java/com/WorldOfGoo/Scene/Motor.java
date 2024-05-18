package com.WorldOfGoo.Scene;

import com.WooGLEFX.Structures.EditorObject;
import com.WooGLEFX.Structures.InputField;
import com.WooGLEFX.Structures.SimpleStructures.MetaEditorAttribute;

public class Motor extends EditorObject {

    public Motor(EditorObject _parent) {
        super(_parent, "motor");

        addAttribute("body",     InputField.ANY)                            .assertRequired();
        addAttribute("speed",    InputField.NUMBER).setDefaultValue("-0.01").assertRequired();
        addAttribute("maxforce", InputField.NUMBER).setDefaultValue("20")   .assertRequired();

        setMetaAttributes(MetaEditorAttribute.parse("body,maxforce,speed,"));

    }


    @Override
    public String getName() {
        return getAttribute("body").stringValue();
    }

}
