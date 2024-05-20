package com.WorldOfGoo.Ball;

import com.WooGLEFX.EditorObjects.EditorObject;
import com.WooGLEFX.EditorObjects.InputField;

public class Sinvariance extends EditorObject {

    public Sinvariance(EditorObject _parent) {
        super(_parent, "sinvariance");

        addAttribute("freq", InputField.ANY).assertRequired();
        addAttribute("amp", InputField.ANY).assertRequired();
        addAttribute("shift", InputField.ANY).assertRequired();

    }

}
