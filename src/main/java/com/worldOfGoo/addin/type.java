package com.worldOfGoo.addin;

import com.woogleFX.editorObjects.EditorObject;
import com.woogleFX.assets.GameVersion;

public class type extends EditorObject {

    public type(EditorObject _parent, GameVersion version) {
        super(_parent, version);
    }

    @Override
    public String getName() {
        return getAttribute("value").stringValue();
    }

}
