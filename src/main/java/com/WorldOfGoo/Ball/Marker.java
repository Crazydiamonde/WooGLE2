package com.WorldOfGoo.Ball;

import com.WooGLEFX.Structures.EditorObject;
import com.WooGLEFX.Structures.InputField;

public class Marker extends EditorObject {
    public Marker(EditorObject _parent) {
        super(_parent);
        addAttribute("drag", "", InputField.ANY, true);
        addAttribute("detach", "", InputField.ANY, true);
        addAttribute("rotspeed", "", InputField.ANY, true);
    }
}
