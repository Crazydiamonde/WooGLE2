package com.worldOfGoo.resrc;

import com.woogleFX.editorObjects.EditorObject;
import com.woogleFX.editorObjects.attributes.InputField;
import com.woogleFX.gameData.level.GameVersion;
import com.woogleFX.editorObjects.attributes.MetaEditorAttribute;

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
