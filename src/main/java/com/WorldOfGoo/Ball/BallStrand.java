package com.WorldOfGoo.Ball;

import com.WooGLEFX.Structures.EditorObject;
import com.WooGLEFX.Structures.InputField;

public class BallStrand extends EditorObject {
    public BallStrand(EditorObject _parent) {
        super(_parent);
        addAttribute("type", "", InputField.ANY, true);
        addAttribute("image", "", InputField.IMAGE, true);
        addAttribute("inactiveimage", "", InputField.ANY, true);
        addAttribute("springconstmin", "", InputField.ANY, true);
        addAttribute("springconstmax", "", InputField.ANY, true);
        addAttribute("dampfac", "", InputField.ANY, true);
        addAttribute("maxlen2", "0", InputField.ANY, true);
        addAttribute("maxlen1", "", InputField.ANY, false);
        addAttribute("maxforce", "", InputField.ANY, true);
        addAttribute("minlen", "", InputField.ANY, true);
        addAttribute("walkable", "", InputField.ANY, false);
        addAttribute("thickness", "24", InputField.NUMBER, false);
        addAttribute("ignitedelay", "", InputField.ANY, false);
        addAttribute("burnspeed", "", InputField.ANY, false);
        addAttribute("fireparticles", "", InputField.ANY, false);
        addAttribute("burntimage", "", InputField.ANY, false);
        addAttribute("geom", "", InputField.ANY, false);
        addAttribute("shrinklen", "", InputField.ANY, false);
        addAttribute("rope", "", InputField.ANY, false);
    }
}
