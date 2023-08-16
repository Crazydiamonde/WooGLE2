package com.WorldOfGoo.Ball;

import com.WooGLEFX.Structures.EditorObject;
import com.WooGLEFX.Structures.InputField;

public class BallSound extends EditorObject {
    public BallSound(EditorObject _parent) {
        super(_parent);
        addAttribute("event", "", InputField.ANY, true);
        addAttribute("id", "", InputField.ANY, true);
    }
}
