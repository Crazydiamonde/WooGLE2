package com.WorldOfGoo.Level;

import com.WooGLEFX.EditorObjects.EditorObject;
import com.WooGLEFX.EditorObjects.InputField;
import com.WooGLEFX.Structures.SimpleStructures.MetaEditorAttribute;

public class Endoncollision extends EditorObject {

    public Endoncollision(EditorObject _parent) {
        super(_parent, "endoncollision", "level\\endoncollision");

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
