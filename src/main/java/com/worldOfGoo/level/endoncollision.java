package com.worldOfGoo.level;

import com.woogleFX.editorObjects.EditorObject;
import com.woogleFX.assets.GameVersion;

public class endoncollision extends EditorObject {

    public endoncollision(EditorObject _parent, GameVersion version) {
        super(_parent, version);
    }


    @Override
    public String getName() {
        String id1 = getAttribute("id1").stringValue();
        String id2 = getAttribute("id2").stringValue();
        return id1 + ", " + id2;
    }
}
