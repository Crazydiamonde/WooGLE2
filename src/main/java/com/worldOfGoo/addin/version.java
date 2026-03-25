package com.worldOfGoo.addin;

import com.woogleFX.editorObjects.EditorObject;
import com.woogleFX.assets.GameVersion;

public class version extends EditorObject {

    public version(EditorObject _parent, GameVersion version) {
        super(_parent, version);
    }


    @Override
    public String getName() {
        return getAttribute("value").stringValue();
    }

}
