package com.WorldOfGoo.Ball;

import com.WooGLEFX.Structures.EditorObject;
import com.WooGLEFX.Structures.InputField;

public class Detachstrand extends EditorObject {
    public Detachstrand(EditorObject _parent) {
        super(_parent);
        addAttribute("image", "", InputField.ANY, false);
        addAttribute("maxlen", "", InputField.ANY, true);
    }
}
