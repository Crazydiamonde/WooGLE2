package com.worldOfGoo.text;

import com.woogleFX.editorObjects.EditorObject;

public class TextStrings extends EditorObject {

    public TextStrings(EditorObject _parent) {
        super(_parent, "strings", "text\\textstrings");
    }


    @Override
    public String[] getPossibleChildren() {
        return new String[]{ "textstring" };
    }


}
