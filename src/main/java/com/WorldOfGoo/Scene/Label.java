package com.WorldOfGoo.Scene;

import com.WooGLEFX.Structures.EditorObject;
import com.WooGLEFX.Structures.InputField;
import com.WooGLEFX.Structures.SimpleStructures.MetaEditorAttribute;

public class Label extends EditorObject {
    public Label(EditorObject _parent) {
        super(_parent);
        setRealName("label");
        addAttribute("id", "", InputField.ANY, true);
        addAttribute("depth", "10", InputField.NUMBER, true);
        addAttribute("x", "0", InputField.NUMBER, true);
        addAttribute("y", "0", InputField.NUMBER, true);
        addAttribute("align", "center", InputField.ANY, true);
        addAttribute("rotation", "0", InputField.NUMBER, true);
        addAttribute("scale", "1", InputField.NUMBER, true);
        addAttribute("overlay", "true", InputField.FLAG, true);
        addAttribute("screenspace", "true", InputField.FLAG, true);
        addAttribute("font", "", InputField.ANY, true); //???
        addAttribute("text", "", InputField.TEXT, true);
        addAttribute("colorize", "255,255,255", InputField.COLOR, false);
        setNameAttribute(getAttribute2("id"));
        setMetaAttributes(MetaEditorAttribute.parse("id,x,y,rotation,scale,depth,colorize,overlay,screenspace,Text<text,font,align>"));
    }

}
