package com.WorldOfGoo.Scene;

import com.WooGLEFX.Structures.EditorObject;
import com.WooGLEFX.Structures.InputField;
import com.WooGLEFX.Structures.SimpleStructures.MetaEditorAttribute;

public class Buttongroup extends EditorObject {
    
    public Buttongroup(EditorObject _parent) {
        super(_parent, "buttongroup");

        addAttribute("id", InputField.ANY)      .setDefaultValue("levelMarkerGroup").assertRequired();
        addAttribute("osx", InputField.POSITION).setDefaultValue("150,1.08")        .assertRequired();

        setMetaAttributes(MetaEditorAttribute.parse("id,osx"));

    }


    @Override
    public String getName() {
        return getAttribute("id").stringValue();
    }


    @Override
    public String[] getPossibleChildren() {
        return new String[]{"Button"};
    }

}
