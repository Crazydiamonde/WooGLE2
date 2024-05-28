package com.worldOfGoo.ball;

import com.woogleFX.editorObjects.EditorObject;
import com.woogleFX.editorObjects.InputField;
import com.woogleFX.structures.GameVersion;

public class Part extends EditorObject {

    public Part(EditorObject _parent, GameVersion version) {
        super(_parent, "part", version);

        addAttribute("name",       InputField.ANY)                        .assertRequired();
        addAttribute("layer",      InputField.NUMBER)                     .assertRequired();
        addAttribute("x",          InputField.NUMBER)                     .assertRequired();
        addAttribute("y",          InputField.NUMBER)                     .assertRequired();
        addAttribute("image",      InputField.ANY)                        .assertRequired();
        addAttribute("scale",      InputField.NUMBER).setDefaultValue("1").assertRequired();
        addAttribute("rotate",     InputField.FLAG);
        addAttribute("stretch",    InputField.NUMBER);
        addAttribute("xrange",     InputField.POSITION);
        addAttribute("yrange",     InputField.POSITION);
        addAttribute("eye",        InputField.FLAG);
        addAttribute("pupil",      InputField.IMAGE);
        addAttribute("pupilinset", InputField.NUMBER);
        addAttribute("state",      InputField.ANY);

    }

}
