package com.worldOfGoo.resrc;

import com.woogleFX.editorObjects.EditorObject;
import com.woogleFX.editorObjects.attributes.InputField;
import com.woogleFX.gameData.font._Font;
import com.woogleFX.gameData.level.GameVersion;

public class Font extends EditorObject {

    private _Font font;
    public _Font getFont() {
        return font;
    }
    public void setFont(_Font font) {
        this.font = font;
    }


    private SetDefaults setDefaults;
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


    public Font(EditorObject _parent, GameVersion version) {
        super(_parent, "font", version);

        addAttribute("id", InputField.ANY);
        addAttribute("path", InputField.ANY);

        // extra attributes from 1.5
        addAttribute("cn", InputField.ANY);
        addAttribute("ko", InputField.ANY);
        addAttribute("jp", InputField.ANY);

    }

}
