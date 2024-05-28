package com.worldOfGoo.addin;

import com.woogleFX.editorObjects.EditorObject;
import com.woogleFX.editorObjects.InputField;
import com.woogleFX.structures.GameVersion;
import com.woogleFX.structures.simpleStructures.MetaEditorAttribute;

public class AddinLevelOCD extends EditorObject {

    public AddinLevelOCD(EditorObject _parent, GameVersion version) {
        super(_parent, "ocd", version);

        addAttribute("type", InputField.OCD_TYPE)            .assertRequired();
        addAttribute("value", InputField.NUMBER_NON_NEGATIVE).assertRequired();

        setMetaAttributes(MetaEditorAttribute.parse("type,value,"));

    }


    @Override
    public String getName() {
        String type = getAttribute("type").stringValue();
        String value = getAttribute("value").stringValue();
        return type + ", " + value;
    }

}
