package com.WorldOfGoo.Ball;

import com.WooGLEFX.Structures.EditorObject;
import com.WooGLEFX.Structures.InputField;

public class BallSound extends EditorObject {

    public BallSound(EditorObject _parent) {
        super(_parent, "sound");

        addAttribute("event", InputField.ANY).assertRequired();
        addAttribute("id",    InputField.ANY).assertRequired();

    }

}
