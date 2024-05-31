package com.worldOfGoo.particle;

import com.woogleFX.editorObjects.EditorObject;
import com.woogleFX.editorObjects.attributes.InputField;
import com.woogleFX.gameData.level.GameVersion;

public class Axialsinoffset extends EditorObject {

    public Axialsinoffset(EditorObject _parent, GameVersion version) {
        super(_parent, "axialsinoffset", version);

        addAttribute("amp",        InputField.RANGE).setDefaultValue("0").assertRequired();
        addAttribute("freq",       InputField.RANGE).setDefaultValue("0").assertRequired();
        addAttribute("phaseshift", InputField.RANGE).setDefaultValue("0").assertRequired();
        addAttribute("axis",       InputField.ANY)  .setDefaultValue("x").assertRequired();

    }

}
