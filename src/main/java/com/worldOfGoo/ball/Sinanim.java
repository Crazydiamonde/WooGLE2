package com.worldOfGoo.ball;

import com.woogleFX.editorObjects.EditorObject;
import com.woogleFX.editorObjects.InputField;

public class Sinanim extends EditorObject {

    public Sinanim(EditorObject _parent) {
        super(_parent, "sinanim");

        addAttribute("part",  InputField.ANY).assertRequired();
        addAttribute("state", InputField.ANY).assertRequired();
        addAttribute("type",  InputField.ANY).assertRequired();
        addAttribute("axis",  InputField.ANY).assertRequired();
        addAttribute("freq",  InputField.ANY).assertRequired();
        addAttribute("amp",   InputField.ANY).assertRequired();
        addAttribute("shift", InputField.ANY).assertRequired();

    }

}