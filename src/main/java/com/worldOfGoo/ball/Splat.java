package com.worldOfGoo.ball;

import com.woogleFX.editorObjects.EditorObject;
import com.woogleFX.editorObjects.InputField;

public class Splat extends EditorObject {

    public Splat(EditorObject _parent) {
        super(_parent, "splat");

        addAttribute("image", InputField.ANY).assertRequired();

    }

}
