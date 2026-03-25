package com.worldOfGoo.resrc;

import com.woogleFX.editorObjects.EditorObject;
import com.woogleFX.assets.GameVersion;

public class Resources extends EditorObject {

    public Resources(EditorObject _parent, GameVersion version) {
        super(_parent, version);
    }


    @Override
    public String getName() {
        return getAttribute("id").stringValue();
    }


    @Override
    @SuppressWarnings("unchecked")
    public Class<? extends EditorObject>[] getPossibleChildren() {
        return (Class<? extends EditorObject>[]) new Class[]{ Image.class, Sound.class, font.class, SetDefaults.class };
    }

}
