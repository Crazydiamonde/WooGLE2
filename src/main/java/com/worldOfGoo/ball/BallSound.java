package com.worldOfGoo.ball;

import com.woogleFX.editorObjects.EditorObject;
import com.woogleFX.editorObjects.InputField;
import com.woogleFX.structures.GameVersion;

public class BallSound extends EditorObject {

    public BallSound(EditorObject _parent, GameVersion version) {
        super(_parent, "sound", version);

        addAttribute("event", InputField.ANY).assertRequired();
        addAttribute("id",    InputField.ANY).assertRequired();

    }

}
