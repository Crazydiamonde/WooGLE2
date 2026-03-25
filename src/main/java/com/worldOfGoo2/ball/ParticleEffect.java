package com.worldOfGoo2.ball;

import com.woogleFX.editorObjects.EditorObject;
import com.woogleFX.assets.GameVersion;

public class ParticleEffect extends EditorObject {

    public ParticleEffect(EditorObject parent, GameVersion version) {
        super(parent, version);
    }

    @Override
    public String getName() {
        return getAttribute("particleEffectId").stringValue();
    }

}
