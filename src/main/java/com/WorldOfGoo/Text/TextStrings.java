package com.WorldOfGoo.Text;

import com.WooGLEFX.EditorObjects.EditorObject;

public class TextStrings extends EditorObject {

    public TextStrings(EditorObject _parent) {
        super(_parent, "strings", "text\\textstrings");
    }


    @Override
    public String[] getPossibleChildren() {
        return new String[]{ "textstring" };
    }


}
