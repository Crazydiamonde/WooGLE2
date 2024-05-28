package com.worldOfGoo.text;

import com.woogleFX.editorObjects.EditorObject;
import com.woogleFX.structures.GameVersion;

public class TextStrings extends EditorObject {

    public TextStrings(EditorObject _parent, GameVersion version) {
        super(_parent, "strings", version);
    }


    @Override
    public String[] getPossibleChildren() {
        return new String[]{ "textstring" };
    }


}
