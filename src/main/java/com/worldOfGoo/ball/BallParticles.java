package com.worldOfGoo.ball;

import com.woogleFX.editorObjects.EditorObject;
import com.woogleFX.editorObjects.attributes.InputField;
import com.woogleFX.gameData.level.GameVersion;

public class BallParticles extends EditorObject {

    public BallParticles(EditorObject _parent, GameVersion version) {
        super(_parent, "particles", version);

        addAttribute("id",       InputField.ANY).assertRequired();
        addAttribute("states",   InputField.ANY).assertRequired();
        addAttribute("overball", InputField.ANY).assertRequired();

    }

}
