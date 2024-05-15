package com.WorldOfGoo.Level;

import com.WooGLEFX.Structures.EditorObject;
import com.WooGLEFX.Structures.InputField;
import com.WooGLEFX.Structures.SimpleStructures.MetaEditorAttribute;

public class Endoncollision extends EditorObject {

    public Endoncollision(EditorObject _parent) {
        super(_parent);
        setRealName("endoncollision");

        addAttribute("id1", InputField.GEOMETRY).assertRequired();
        addAttribute("id2", InputField.GEOMETRY).assertRequired();
        addAttribute("delay", InputField.NUMBER).assertRequired();

        setNameAttribute(getAttribute2("id1"));
        setNameAttribute2(getAttribute2("id2"));
        setChangeListener("id2", (observableValue, s, t1) -> {
            String bruh = getAttribute("id1");
            setAttribute("id1", "AAAAA");
            setAttribute("id1", bruh);
        });
        setMetaAttributes(MetaEditorAttribute.parse("id1,id2,delay"));

    }

}
