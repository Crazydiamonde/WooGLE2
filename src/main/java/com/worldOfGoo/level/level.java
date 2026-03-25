package com.worldOfGoo.level;

import com.woogleFX.editorObjects.EditorObject;
import com.woogleFX.assets.GameVersion;

public class level extends EditorObject {

    public level(EditorObject _parent, GameVersion version) {
        super(_parent, version);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Class<? extends EditorObject>[] getPossibleChildren() {
        return new Class[] {
                BallInstance.class,
                Strand.class,
                camera.class,
                endoncollision.class,
                endonmessage.class,
                endonnogeom.class,
                fire.class,
                levelexit.class,
                loopsound.class,
                music.class,
                pipe.class,
                signpost.class,
                targetheight.class
        };
    }

}
