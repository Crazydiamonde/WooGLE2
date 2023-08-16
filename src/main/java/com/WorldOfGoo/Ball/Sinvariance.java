package com.WorldOfGoo.Ball;

import com.WooGLEFX.Structures.EditorObject;
import com.WooGLEFX.Structures.InputField;

public class Sinvariance extends EditorObject {
    public Sinvariance(EditorObject _parent) {
        super(_parent);
        addAttribute("freq", "", InputField.ANY, true);
        addAttribute("amp", "", InputField.ANY, true);
        addAttribute("shift", "", InputField.ANY, true);
    }
}
