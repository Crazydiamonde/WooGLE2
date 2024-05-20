package com.WorldOfGoo.Level;

import com.WooGLEFX.EditorObjects.EditorObject;
import com.WooGLEFX.EditorObjects.InputField;
import com.WooGLEFX.Structures.SimpleStructures.MetaEditorAttribute;

public class Loopsound extends EditorObject {

    public Loopsound(EditorObject _parent) {
        super(_parent, "loopsound", "level\\loopsound");

        addAttribute("id", InputField.ANY).assertRequired();

        setMetaAttributes(MetaEditorAttribute.parse("id,"));

    }


    @Override
    public String getName() {
        return getAttribute("id").stringValue();
    }

}
