package com.WorldOfGoo.Scene;

import com.WooGLEFX.Structures.EditorObject;
import com.WooGLEFX.Structures.InputField;
import com.WooGLEFX.Structures.SimpleStructures.MetaEditorAttribute;

public class Radialforcefield extends EditorObject {
    public Radialforcefield(EditorObject _parent) {
        super(_parent);
        setRealName("radialforcefield");
        addAttribute("id", "", InputField.ANY, true);
        addAttribute("type", "gravity", InputField.ANY, true);
        addAttribute("center", "0,0", InputField.POSITION, true);
        addAttribute("radius", "100", InputField.NUMBER, true);
        addAttribute("forceatcenter", "0", InputField.NUMBER, true);
        addAttribute("forceatedge", "0", InputField.NUMBER, true);
        addAttribute("dampeningfactor", "0", InputField.NUMBER, true);
        addAttribute("rotationaldampeningfactor", "", InputField.NUMBER, false);
        addAttribute("antigrav", "false", InputField.FLAG, true);
        addAttribute("geomonly", "", InputField.FLAG, true);
        addAttribute("enabled", "true", InputField.FLAG, true);
        setNameAttribute(getAttribute2("id"));
        setMetaAttributes(MetaEditorAttribute.parse("id,center,radius,forceatcenter,forceatedge,dampeningfactor,rotationaldampeningfactor,antigrav,"));
    }
}
