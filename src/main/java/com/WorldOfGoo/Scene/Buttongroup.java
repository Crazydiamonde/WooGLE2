package com.WorldOfGoo.Scene;

import com.WooGLEFX.Structures.EditorObject;
import com.WooGLEFX.Structures.InputField;
import com.WooGLEFX.Structures.SimpleStructures.MetaEditorAttribute;

public class Buttongroup extends EditorObject {
    public Buttongroup(EditorObject _parent) {
        super(_parent);
        setRealName("buttongroup");
        addAttribute("id", "levelMarkerGroup", InputField.ANY, true);
        addAttribute("osx", "150,1.08", InputField.POSITION, true);
        setMetaAttributes(MetaEditorAttribute.parse("id,osx"));
    }

    @Override
    public String[] getPossibleChildren() {
        return new String[]{"Button"};
    }
}
