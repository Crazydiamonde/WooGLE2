package com.WorldOfGoo.Level;

import com.WooGLEFX.Structures.EditorObject;
import com.WooGLEFX.Structures.InputField;

public class Targetheight extends EditorObject {

    public Targetheight(EditorObject _parent) {
        super(_parent);
        setRealName("targetheight");

        addAttribute("y", InputField.NUMBER).setDefaultValue("1000").assertRequired();

        setNameAttribute(getAttribute2("y"));

    }

}
