package com.worldOfGoo.resrc;

import com.woogleFX.editorObjects.EditorObject;
import com.woogleFX.editorObjects.InputField;
import com.woogleFX.structures.GameVersion;
import com.woogleFX.structures.simpleStructures.MetaEditorAttribute;

public class SetDefaults extends EditorObject {

    public SetDefaults(EditorObject _parent, GameVersion version) {
        super(_parent, "SetDefaults", version);

        addAttribute("path", InputField.ANY).setDefaultValue("./").assertRequired();
        addAttribute("idprefix", InputField.ANY)                  .assertRequired();

        setMetaAttributes(MetaEditorAttribute.parse("path,idprefix,"));

    }


    @Override
    public String getName() {
        return getAttribute("path").stringValue();
    }

}
