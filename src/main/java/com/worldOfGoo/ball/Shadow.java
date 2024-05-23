package com.worldOfGoo.ball;

import com.woogleFX.editorObjects.EditorObject;
import com.woogleFX.editorObjects.InputField;

public class Shadow extends EditorObject {

    public Shadow(EditorObject _parent) {
        super(_parent, "shadow");

        addAttribute("image",    InputField.ANY)  .assertRequired();
        addAttribute("additive", InputField.ANY);

    }

}
