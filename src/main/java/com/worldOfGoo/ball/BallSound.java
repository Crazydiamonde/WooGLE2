package com.worldOfGoo.ball;

import com.woogleFX.editorObjects.EditorObject;
import com.woogleFX.editorObjects.InputField;

public class BallSound extends EditorObject {

    public BallSound(EditorObject _parent) {
        super(_parent, "sound");

        addAttribute("event", InputField.ANY).assertRequired();
        addAttribute("id",    InputField.ANY).assertRequired();

    }

}
