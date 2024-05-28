package com.worldOfGoo.level;

import com.woogleFX.editorObjects.EditorObject;
import com.woogleFX.editorObjects.InputField;
import com.woogleFX.structures.GameVersion;
import com.woogleFX.structures.simpleStructures.MetaEditorAttribute;

public class Endoncollision extends EditorObject {

    public Endoncollision(EditorObject _parent, GameVersion version) {
        super(_parent, "endoncollision", version);

        addAttribute("id1", InputField.GEOMETRY).assertRequired();
        addAttribute("id2", InputField.GEOMETRY).assertRequired();
        addAttribute("delay", InputField.NUMBER).assertRequired();

        setMetaAttributes(MetaEditorAttribute.parse("id1,id2,delay"));

    }


    @Override
    public String getName() {
        String id1 = getAttribute("id1").stringValue();
        String id2 = getAttribute("id2").stringValue();
        return id1 + ", " + id2;
    }
}
