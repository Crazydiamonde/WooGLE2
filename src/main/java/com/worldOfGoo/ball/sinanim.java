package com.worldOfGoo.ball;

import com.woogleFX.editorObjects.EditorObject;
import com.woogleFX.assets.GameVersion;

public class sinanim extends EditorObject {

    public sinanim(EditorObject _parent, GameVersion version) {
        super(_parent, version);
    }

    @Override
    public String getName() {
        return getAttribute("part").stringValue();
    }

}
