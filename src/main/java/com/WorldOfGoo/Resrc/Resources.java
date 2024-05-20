package com.WorldOfGoo.Resrc;

import com.WooGLEFX.EditorObjects.EditorObject;
import com.WooGLEFX.EditorObjects.InputField;
import com.WooGLEFX.Structures.SimpleStructures.MetaEditorAttribute;

public class Resources extends EditorObject {

    public Resources(EditorObject _parent) {
        super(_parent, "Resources", "resources\\resources");

        addAttribute("id", InputField.ANY).assertRequired();

        setMetaAttributes(MetaEditorAttribute.parse("id,"));

    }


    @Override
    public String getName() {
        return getAttribute("id").stringValue();
    }


    @Override
    public String[] getPossibleChildren() {
        return new String[]{ "resrcimage", "sound", "setdefaults" };
    }

}
