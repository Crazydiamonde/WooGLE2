package com.worldOfGoo.addin;

import com.woogleFX.editorObjects.EditorObject;
import com.woogleFX.editorObjects.InputField;
import com.woogleFX.structures.GameVersion;
import com.woogleFX.structures.simpleStructures.MetaEditorAttribute;

public class AddinAuthor extends EditorObject {

    public AddinAuthor(EditorObject _parent, GameVersion version) {
        super(_parent, "author", version);

        addAttribute("value", InputField.ANY).assertRequired();

        setMetaAttributes(MetaEditorAttribute.parse("value,"));
    }


    @Override
    public String getName() {
        return getAttribute("value").stringValue();
    }
}
