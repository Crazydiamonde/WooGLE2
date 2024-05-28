package com.worldOfGoo.addin;

import com.woogleFX.editorObjects.EditorObject;
import com.woogleFX.structures.GameVersion;

public class AddinDependencies extends EditorObject {

    public AddinDependencies(EditorObject _parent, GameVersion version) {
        super(_parent, "dependencies", version);
    }

}
