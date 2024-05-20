package com.WorldOfGoo.Ball;

import com.WooGLEFX.EditorObjects.EditorObject;
import com.WooGLEFX.EditorObjects.InputField;

public class Marker extends EditorObject {

    public Marker(EditorObject _parent) {
        super(_parent, "marker");

        addAttribute("drag",     InputField.ANY).assertRequired();
        addAttribute("detach",   InputField.ANY).assertRequired();
        addAttribute("rotspeed", InputField.ANY).assertRequired();

    }

}
