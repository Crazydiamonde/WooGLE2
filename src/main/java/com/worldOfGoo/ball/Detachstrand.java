package com.worldOfGoo.ball;

import com.woogleFX.editorObjects.EditorObject;
import com.woogleFX.editorObjects.InputField;

public class Detachstrand extends EditorObject {

    public Detachstrand(EditorObject _parent) {
        super(_parent, "detachstrand");

        addAttribute("image",  InputField.ANY);
        addAttribute("maxlen", InputField.ANY).assertRequired();

    }

}
