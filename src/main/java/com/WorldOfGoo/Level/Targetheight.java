package com.WorldOfGoo.Level;

import com.WooGLEFX.Structures.EditorObject;
import com.WooGLEFX.Structures.InputField;

public class Targetheight extends EditorObject {
    public Targetheight(EditorObject _parent) {
        super(_parent);
        setRealName("targetheight");
        addAttribute("y", "1000", InputField.NUMBER, true);
        setNameAttribute(getAttribute2("y"));
    }
}
