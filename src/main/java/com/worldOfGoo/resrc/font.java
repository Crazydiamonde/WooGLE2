package com.worldOfGoo.resrc;

import com.woogleFX.editorObjects.EditorObject;
import com.woogleFX.gameData.font._Font;
import com.woogleFX.assets.GameVersion;

public class font extends EditorObject implements ResourceInterface {

    private _Font font;
    public _Font getFont() {
        return font;
    }
    public void setFont(_Font font) {
        this.font = font;
    }


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


    public font(EditorObject _parent, GameVersion version) {
        super(_parent, version);
    }

}
