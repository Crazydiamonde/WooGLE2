package com.worldOfGoo.resrc;

import com.woogleFX.editorObjects.EditorObject;
import com.woogleFX.editorObjects.attributes.InputField;
import com.woogleFX.gameData.level.GameVersion;
import com.woogleFX.editorObjects.attributes.MetaEditorAttribute;

public class Resources extends EditorObject {

    public Resources(EditorObject _parent, GameVersion version) {
        super(_parent, "Resources", version);

        addAttribute("id", InputField.ANY).assertRequired();

        setMetaAttributes(MetaEditorAttribute.parse("id,"));

    }


    @Override
    public String getName() {
        return getAttribute("id").stringValue();
    }


    @Override
    public String[] getPossibleChildren() {
        return new String[]{ "Image", "Sound", "font", "SetDefaults" };
    }

}
