package com.worldOfGoo.addin;

import com.woogleFX.editorObjects.EditorObject;
import com.woogleFX.editorObjects.attributes.InputField;
import com.woogleFX.gameData.level.GameVersion;
import com.woogleFX.editorObjects.attributes.MetaEditorAttribute;

public class AddinType extends EditorObject {

    public AddinType(EditorObject _parent, GameVersion version) {
        super(_parent, "type", version);

        addAttribute("value", InputField.ANY).assertRequired().setValue("level");

        setMetaAttributes(MetaEditorAttribute.parse(","));

    }


    @Override
    public String getName() {
        return getAttribute("value").stringValue();
    }

}
