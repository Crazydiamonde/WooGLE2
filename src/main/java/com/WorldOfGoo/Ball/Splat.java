package com.WorldOfGoo.Ball;

import com.WooGLEFX.Structures.EditorObject;
import com.WooGLEFX.Structures.InputField;

public class Splat extends EditorObject {
    public Splat(EditorObject _parent) {
        super(_parent);
        addAttribute("image", "", InputField.ANY, true);
    }
}
