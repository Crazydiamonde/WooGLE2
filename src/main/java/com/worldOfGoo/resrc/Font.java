package com.worldOfGoo.resrc;

import com.woogleFX.editorObjects.EditorObject;
import com.woogleFX.editorObjects.InputField;

public class Font extends EditorObject {

    public Font(EditorObject _parent) {
        super(_parent, "font");

        addAttribute("id", InputField.ANY);
        addAttribute("path", InputField.ANY);

    }

}
