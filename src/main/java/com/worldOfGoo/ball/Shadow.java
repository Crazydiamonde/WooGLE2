package com.worldOfGoo.ball;

import com.woogleFX.editorObjects.EditorObject;
import com.woogleFX.editorObjects.InputField;
import com.woogleFX.structures.GameVersion;

public class Shadow extends EditorObject {

    public Shadow(EditorObject _parent, GameVersion version) {
        super(_parent, "shadow", version);

        addAttribute("image",    InputField.ANY)  .assertRequired();
        addAttribute("additive", InputField.ANY);

    }

}
