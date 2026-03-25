package com.worldOfGoo.resrc;

import com.woogleFX.editorObjects.EditorObject;
import com.woogleFX.assets.GameVersion;

public class SetDefaults extends EditorObject {

    public SetDefaults(EditorObject _parent, GameVersion version) {
        super(_parent, version);
    }

    @Override
    public String getName() {
        return getAttribute("path").stringValue();
    }

}
