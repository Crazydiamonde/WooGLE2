package com.WorldOfGoo.Particle;

import com.WooGLEFX.Structures.EditorObject;
import com.WooGLEFX.Structures.InputField;

public class Axialsinoffset extends EditorObject {

    public Axialsinoffset(EditorObject _parent) {
        super(_parent);

        addAttribute("amp",        InputField.RANGE).setDefaultValue("0").assertRequired();
        addAttribute("freq",       InputField.RANGE).setDefaultValue("0").assertRequired();
        addAttribute("phaseshift", InputField.RANGE).setDefaultValue("0").assertRequired();
        addAttribute("axis",       InputField.ANY)  .setDefaultValue("x").assertRequired();

    }

}
