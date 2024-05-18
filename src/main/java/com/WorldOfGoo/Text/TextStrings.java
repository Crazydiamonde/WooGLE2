package com.WorldOfGoo.Text;

import com.WooGLEFX.Structures.EditorObject;

public class TextStrings extends EditorObject {

    public TextStrings(EditorObject _parent) {
        super(_parent, "strings");
    }


    @Override
    public String[] getPossibleChildren() {
        return new String[]{ "textstring" };
    }


}
