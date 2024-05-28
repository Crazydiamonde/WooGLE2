package com.worldOfGoo.scene;

import com.woogleFX.editorObjects.EditorObject;
import com.woogleFX.editorObjects.InputField;
import com.woogleFX.structures.GameVersion;
import com.woogleFX.structures.simpleStructures.MetaEditorAttribute;

public class Motor extends EditorObject {

    public Motor(EditorObject _parent, GameVersion version) {
        super(_parent, "motor", version);

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
