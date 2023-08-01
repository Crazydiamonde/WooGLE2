package com.WorldOfGoo.Resrc;

import com.WooGLEFX.Structures.EditorObject;
import com.WooGLEFX.File.FileManager;
import com.WooGLEFX.Structures.InputField;
import com.WooGLEFX.Structures.SimpleStructures.MetaEditorAttribute;

public class Sound extends EditorObject {
    public Sound(EditorObject _parent) {
        super(_parent);
        setRealName("Sound");
        addAttribute("id", "image", InputField.ANY, true);
        addAttribute("path", "", InputField.ANY, true);
        setMetaAttributes(MetaEditorAttribute.parse("id,path,"));
    }
}
