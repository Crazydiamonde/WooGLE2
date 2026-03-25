package com.worldOfGoo.addin;

import com.woogleFX.editorObjects.EditorObject;
import com.woogleFX.assets.GameVersion;

public class addin extends EditorObject {

    public addin(EditorObject _parent, GameVersion version) {
        super(_parent, version);
        // TODO: spec-version
    }

    @Override
    public String getName() {
        return getAttribute("spec-version").stringValue();
    }

}
