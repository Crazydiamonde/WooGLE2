package com.worldOfGoo.level;

import com.woogleFX.editorObjects.EditorObject;
import com.woogleFX.assets.GameVersion;

public class loopsound extends EditorObject {

    public loopsound(EditorObject _parent, GameVersion version) {
        super(_parent, version);
    }


    @Override
    public String getName() {
        return getAttribute("id").stringValue();
    }

}
