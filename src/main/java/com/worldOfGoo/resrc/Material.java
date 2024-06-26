package com.worldOfGoo.resrc;

import com.woogleFX.editorObjects.EditorObject;
import com.woogleFX.editorObjects.attributes.InputField;
import com.woogleFX.gameData.level.GameVersion;

public class Material extends EditorObject {

    public Material(EditorObject _parent, GameVersion version) {
        super(_parent, "material", version);

        addAttribute("id", InputField.ANY);
        addAttribute("friction", InputField.NUMBER);
        addAttribute("bounce", InputField.NUMBER);
        addAttribute("minbouncevel", InputField.NUMBER);
        addAttribute("stickiness", InputField.NUMBER);

    }

}
