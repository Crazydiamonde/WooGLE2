package com.worldOfGoo.ball;

import com.woogleFX.editorObjects.EditorObject;
import com.woogleFX.editorObjects.InputField;
import com.woogleFX.structures.GameVersion;

public class Splat extends EditorObject {

    public Splat(EditorObject _parent, GameVersion version) {
        super(_parent, "splat", version);

        addAttribute("image", InputField.ANY).assertRequired();

    }

}
