package com.WorldOfGoo.Ball;

import com.WooGLEFX.Structures.EditorObject;
import com.WooGLEFX.Structures.InputField;

public class BallParticles extends EditorObject {

    public BallParticles(EditorObject _parent) {
        super(_parent);

        addAttribute("id", InputField.ANY).assertRequired();
        addAttribute("states", InputField.ANY).assertRequired();
        addAttribute("overball", InputField.ANY).assertRequired();

    }

}
