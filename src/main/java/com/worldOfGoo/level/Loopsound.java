package com.worldOfGoo.level;

import com.woogleFX.editorObjects.EditorObject;
import com.woogleFX.editorObjects.attributes.InputField;
import com.woogleFX.gameData.level.GameVersion;
import com.woogleFX.editorObjects.attributes.MetaEditorAttribute;

public class Loopsound extends EditorObject {

    public Loopsound(EditorObject _parent, GameVersion version) {
        super(_parent, "loopsound", version);

        addAttribute("id", InputField.ANY).assertRequired();

        setMetaAttributes(MetaEditorAttribute.parse("id,"));

    }


    @Override
    public String getName() {
        return getAttribute("id").stringValue();
    }

}
