package com.worldOfGoo.resrc;

import com.woogleFX.editorObjects.EditorObject;
import com.woogleFX.editorObjects.InputField;

public class Font extends EditorObject {

    public Font(EditorObject _parent) {
        super(_parent, "font");

        addAttribute("id", InputField.ANY);
        addAttribute("path", InputField.ANY);

        // extra attributes from 1.5
        addAttribute("cn", InputField.ANY);
        addAttribute("ko", InputField.ANY);
        addAttribute("jp", InputField.ANY);

    }

}
