package com.worldOfGoo.text;

import com.woogleFX.editorObjects.EditorObject;
import com.woogleFX.assets.GameVersion;

public class strings extends EditorObject {

    public strings(EditorObject _parent, GameVersion version) {
        super(_parent, version);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Class<? extends EditorObject>[] getPossibleChildren() {
        return new Class[]{ string.class };
    }


}
