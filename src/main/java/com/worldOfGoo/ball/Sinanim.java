package com.worldOfGoo.ball;

import com.woogleFX.editorObjects.EditorObject;
import com.woogleFX.editorObjects.attributes.InputField;
import com.woogleFX.gameData.level.GameVersion;

public class Sinanim extends EditorObject {

    public Sinanim(EditorObject _parent, GameVersion version) {
        super(_parent, "sinanim", version);

        addAttribute("part",  InputField.ANY).assertRequired();
        addAttribute("state", InputField.ANY).assertRequired();
        addAttribute("type",  InputField.ANY).assertRequired();
        addAttribute("axis",  InputField.ANY).assertRequired();
        addAttribute("freq",  InputField.ANY).assertRequired();
        addAttribute("amp",   InputField.ANY).assertRequired();
        addAttribute("shift", InputField.ANY).assertRequired();

    }

}
