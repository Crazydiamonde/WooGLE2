package com.worldOfGoo.level;

import com.woogleFX.editorObjects.EditorObject;
import com.woogleFX.editorObjects.attributes.InputField;
import com.woogleFX.gameData.level.GameVersion;
import com.woogleFX.editorObjects.attributes.MetaEditorAttribute;

public class Endonmessage extends EditorObject {

    public Endonmessage(EditorObject _parent, GameVersion version) {
        super(_parent, "endonmessage", version);

        addAttribute("id", InputField.ANY).assertRequired();

        setMetaAttributes(MetaEditorAttribute.parse("id"));

    }


    @Override
    public String getName() {
        return getAttribute("id").stringValue();
    }

}
