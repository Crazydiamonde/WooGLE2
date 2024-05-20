package com.WorldOfGoo.Ball;

import com.WooGLEFX.EditorObjects.EditorObject;
import com.WooGLEFX.EditorObjects.InputField;

public class Detachstrand extends EditorObject {

    public Detachstrand(EditorObject _parent) {
        super(_parent, "detachstrand");

        addAttribute("image",  InputField.ANY);
        addAttribute("maxlen", InputField.ANY).assertRequired();

    }

}
