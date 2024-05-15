package com.WorldOfGoo.Ball;

import com.WooGLEFX.Structures.EditorObject;
import com.WooGLEFX.Structures.InputField;

public class Sinvariance extends EditorObject {

    public Sinvariance(EditorObject _parent) {
        super(_parent);

        addAttribute("freq", InputField.ANY).assertRequired();
        addAttribute("amp", InputField.ANY).assertRequired();
        addAttribute("shift", InputField.ANY).assertRequired();

    }

}
