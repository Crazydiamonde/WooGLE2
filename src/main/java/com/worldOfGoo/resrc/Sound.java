package com.worldOfGoo.resrc;

import com.woogleFX.editorObjects.EditorObject;
import com.woogleFX.assets.GameVersion;

public class Sound extends EditorObject implements ResourceInterface {

    private SetDefaults setDefaults;
    public SetDefaults getSetDefaults() {
        return setDefaults;
    }
    public void setSetDefaults(SetDefaults setDefaults) {
        this.setDefaults = setDefaults;
    }


    public String getAdjustedID() {
        if (setDefaults == null) return getAttribute("id").stringValue();
        else return setDefaults.getAttribute("idprefix").stringValue() + getAttribute("id").stringValue();
    }


    public String getAdjustedPath() {
        if (setDefaults == null) return getAttribute("path").stringValue();
        else return setDefaults.getAttribute("path").stringValue() + getAttribute("path").stringValue();
    }


    public Sound(EditorObject _parent, GameVersion version) {
        super(_parent, version);
    }

    @Override
    public String getName() {
        return getAttribute("id").stringValue();
    }

}
