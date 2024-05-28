package com.worldOfGoo.addin;

import com.woogleFX.editorObjects.EditorObject;
import com.woogleFX.editorObjects.InputField;
import com.woogleFX.structures.GameVersion;
import com.woogleFX.structures.simpleStructures.MetaEditorAttribute;

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
