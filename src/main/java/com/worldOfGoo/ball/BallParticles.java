package com.worldOfGoo.ball;

import com.woogleFX.editorObjects.EditorObject;
import com.woogleFX.editorObjects.InputField;

public class BallParticles extends EditorObject {

    public BallParticles(EditorObject _parent) {
        super(_parent, "particles");

        addAttribute("id",       InputField.ANY).assertRequired();
        addAttribute("states",   InputField.ANY).assertRequired();
        addAttribute("overball", InputField.ANY).assertRequired();

    }

}
