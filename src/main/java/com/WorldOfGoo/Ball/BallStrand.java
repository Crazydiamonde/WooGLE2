package com.WorldOfGoo.Ball;

import com.WooGLEFX.Structures.EditorObject;
import com.WooGLEFX.Structures.InputField;
import com.WooGLEFX.Structures.SimpleStructures.MetaEditorAttribute;

public class BallStrand extends EditorObject {
    public BallStrand(EditorObject _parent) {
        super(_parent);
        addAttribute("image", "", InputField.IMAGE, false);
        addAttribute("thickness", "24", InputField.NUMBER, false);
        addAttribute("minlen", "0", InputField.NUMBER, false);
        addAttribute("maxlen2", "0", InputField.NUMBER, false);
        setMetaAttributes(MetaEditorAttribute.parse("image,thickness,"));
    }
}
