package com.WorldOfGoo.Ball;

import com.WooGLEFX.EditorObjects.EditorObject;
import com.WooGLEFX.EditorObjects.InputField;

public class Splat extends EditorObject {

    public Splat(EditorObject _parent) {
        super(_parent, "splat");

        addAttribute("image", InputField.ANY).assertRequired();

    }

}
