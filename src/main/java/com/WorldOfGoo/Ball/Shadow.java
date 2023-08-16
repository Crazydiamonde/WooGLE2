package com.WorldOfGoo.Ball;

import com.WooGLEFX.Structures.EditorObject;
import com.WooGLEFX.Structures.InputField;

public class Shadow extends EditorObject {
    public Shadow(EditorObject _parent) {
        super(_parent);
        addAttribute("image", "", InputField.ANY, true);
        addAttribute("additive", "", InputField.ANY, false);
    }
}
