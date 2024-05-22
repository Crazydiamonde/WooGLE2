package com.worldOfGoo.ball;

import com.woogleFX.editorObjects.EditorObject;
import com.woogleFX.editorObjects.InputField;

public class Marker extends EditorObject {

    public Marker(EditorObject _parent) {
        super(_parent, "marker");

        addAttribute("drag",     InputField.ANY).assertRequired();
        addAttribute("detach",   InputField.ANY).assertRequired();
        addAttribute("rotspeed", InputField.ANY).assertRequired();

    }

}
