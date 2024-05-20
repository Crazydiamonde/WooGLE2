package com.WorldOfGoo.Resrc;

import com.WooGLEFX.EditorObjects.EditorObject;
import com.WooGLEFX.EditorObjects.InputField;

public class Font extends EditorObject {

    public Font(EditorObject _parent) {
        super(_parent, "font");

        addAttribute("id", InputField.ANY);
        addAttribute("path", InputField.ANY);

    }

}
