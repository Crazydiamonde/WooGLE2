package com.WorldOfGoo.Ball;

import com.WooGLEFX.Structures.EditorObject;
import com.WooGLEFX.Structures.InputField;

public class BallParticles extends EditorObject {

    public BallParticles(EditorObject _parent) {
        super(_parent);
        addAttribute("id", "", InputField.ANY, true);
        addAttribute("states", "", InputField.ANY, true);
        addAttribute("overball", "", InputField.ANY, true);
    }
}
