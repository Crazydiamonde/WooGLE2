package com.WorldOfGoo.Ball;

import com.WooGLEFX.Structures.EditorObject;
import com.WooGLEFX.Structures.InputField;

public class Sinanim extends EditorObject {

    public Sinanim(EditorObject _parent) {
        super(_parent);

        addAttribute("part", InputField.ANY).assertRequired();
        addAttribute("state", InputField.ANY).assertRequired();
        addAttribute("type", InputField.ANY).assertRequired();
        addAttribute("axis", InputField.ANY).assertRequired();
        addAttribute("freq", InputField.ANY).assertRequired();
        addAttribute("amp", InputField.ANY).assertRequired();
        addAttribute("shift", InputField.ANY).assertRequired();

    }

}
