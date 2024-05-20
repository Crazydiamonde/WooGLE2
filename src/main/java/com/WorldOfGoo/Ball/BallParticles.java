package com.WorldOfGoo.Ball;

import com.WooGLEFX.EditorObjects.EditorObject;
import com.WooGLEFX.EditorObjects.InputField;

public class BallParticles extends EditorObject {

    public BallParticles(EditorObject _parent) {
        super(_parent, "particles");

        addAttribute("id",       InputField.ANY).assertRequired();
        addAttribute("states",   InputField.ANY).assertRequired();
        addAttribute("overball", InputField.ANY).assertRequired();

    }

}
