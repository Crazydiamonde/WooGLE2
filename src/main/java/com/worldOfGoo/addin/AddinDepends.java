package com.worldOfGoo.addin;

import com.woogleFX.editorObjects.EditorObject;
import com.woogleFX.editorObjects.InputField;
import com.woogleFX.structures.GameVersion;
import com.woogleFX.structures.simpleStructures.MetaEditorAttribute;

public class AddinDepends extends EditorObject {

    public AddinDepends(EditorObject _parent, GameVersion version) {
        super(_parent, "depends", version);

        addAttribute("ref", InputField.ANY)                    .assertRequired();
        addAttribute("min-version", InputField.NUMBER_POSITIVE).assertRequired();
        addAttribute("max-version", InputField.NUMBER_POSITIVE).assertRequired();
        
        setMetaAttributes(MetaEditorAttribute.parse("ref,min-version,max-version,"));

    }


    @Override
    public String getName() {
        return getAttribute("ref").stringValue();
    }

}
