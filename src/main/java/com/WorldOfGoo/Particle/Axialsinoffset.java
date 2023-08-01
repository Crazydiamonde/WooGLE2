package com.WorldOfGoo.Particle;

import com.WooGLEFX.Structures.EditorObject;
import com.WooGLEFX.Structures.InputField;

public class Axialsinoffset extends EditorObject {
    public Axialsinoffset(EditorObject _parent) {
        super(_parent);
        addAttribute("amp", "0", InputField.RANGE, true);
        addAttribute("freq", "0", InputField.RANGE, true);
        addAttribute("phaseshift", "0", InputField.RANGE, true);
        addAttribute("axis", "x", InputField.ANY, true);
    }
}
