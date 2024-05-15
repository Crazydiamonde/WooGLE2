package com.WorldOfGoo.Level;

import com.WooGLEFX.Functions.LevelManager;
import com.WooGLEFX.Structures.EditorObject;
import com.WooGLEFX.Structures.InputField;
import com.WooGLEFX.Structures.SimpleStructures.MetaEditorAttribute;

public class Pipe extends EditorObject {

    public Pipe(EditorObject _parent) {
        super(_parent);
        setRealName("pipe");

        addAttribute("type", InputField.ANY);
        addAttribute("id", InputField.ANY)                        .assertRequired();
        addAttribute("depth", InputField.ANY).setDefaultValue("0").assertRequired();

        setNameAttribute(getAttribute2("type"));
        setMetaAttributes(MetaEditorAttribute.parse("type,id,depth,"));

    }


    @Override
    public String[] getPossibleChildren() {
        return new String[]{"Vertex"};
    }

}

