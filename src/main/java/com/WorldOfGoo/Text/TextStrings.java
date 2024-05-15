package com.WorldOfGoo.Text;

import com.WooGLEFX.Structures.EditorAttribute;
import com.WooGLEFX.Structures.EditorObject;

public class TextStrings extends EditorObject {

    public TextStrings(EditorObject _parent) {
        super(_parent);
        setRealName("strings");

        setNameAttribute(EditorAttribute.NULL);

    }


    @Override
    public String[] getPossibleChildren() {
        return new String[]{"textstring"};
    }


}
