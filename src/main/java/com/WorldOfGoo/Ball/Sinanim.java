package com.WorldOfGoo.Ball;

import com.WooGLEFX.Structures.EditorObject;
import com.WooGLEFX.Structures.InputField;

public class Sinanim extends EditorObject {
    public Sinanim(EditorObject _parent) {
        super(_parent);
        addAttribute("part", "", InputField.ANY, true);
        addAttribute("state", "", InputField.ANY, true);
        addAttribute("type", "", InputField.ANY, true);
        addAttribute("axis", "", InputField.ANY, true);
        addAttribute("freq", "", InputField.ANY, true);
        addAttribute("amp", "", InputField.ANY, true);
        addAttribute("shift", "", InputField.ANY, true);
    }
}
