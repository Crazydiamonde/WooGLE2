package com.worldOfGoo.scene;

import com.woogleFX.editorObjects.EditorObject;
import com.woogleFX.assets.GameVersion;

public class buttongroup extends EditorObject {
    
    public buttongroup(EditorObject _parent, GameVersion version) {
        super(_parent, version);
    }

    @Override
    public String getName() {
        return getAttribute("id").stringValue();
    }

    @Override
    @SuppressWarnings("unchecked")
    public Class<? extends EditorObject>[] getPossibleChildren() {
        return new Class[]{ button.class };
    }

}
