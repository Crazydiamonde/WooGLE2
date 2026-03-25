package com.worldOfGoo.addin;

import com.woogleFX.editorObjects.EditorObject;
import com.woogleFX.assets.GameVersion;

public class ocd extends EditorObject {

    public ocd(EditorObject _parent, GameVersion version) {
        super(_parent, version);
    }

    @Override
    public String getName() {
        String type = getAttribute("type").stringValue();
        String value = getAttribute("value").stringValue();
        return type + ", " + value;
    }

}
