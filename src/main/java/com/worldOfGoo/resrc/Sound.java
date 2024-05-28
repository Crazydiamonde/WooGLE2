package com.worldOfGoo.resrc;

import com.woogleFX.editorObjects.EditorObject;
import com.woogleFX.editorObjects.InputField;
import com.woogleFX.structures.GameVersion;
import com.woogleFX.structures.simpleStructures.MetaEditorAttribute;

public class Sound extends EditorObject {

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


    public Sound(EditorObject _parent, GameVersion version) {
        super(_parent, "Sound", version);

        addAttribute("id", InputField.ANY).assertRequired();
        addAttribute("path", InputField.SOUND_PATH).assertRequired();

        setMetaAttributes(MetaEditorAttribute.parse("id,path,"));

    }


    @Override
    public String getName() {
        return getAttribute("id").stringValue();
    }

}
