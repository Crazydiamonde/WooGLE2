package com.worldOfGoo.ball;

import com.woogleFX.editorObjects.EditorObject;
import com.woogleFX.editorObjects.attributes.InputField;
import com.woogleFX.gameData.level.GameVersion;

public class Splat extends EditorObject {

    public Splat(EditorObject _parent, GameVersion version) {
        super(_parent, "splat", version);

        addAttribute("image", InputField.ANY).assertRequired();

    }

}
