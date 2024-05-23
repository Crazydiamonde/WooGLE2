package com.worldOfGoo.scene;

import com.woogleFX.editorObjects.EditorObject;
import com.woogleFX.editorObjects.InputField;
import com.woogleFX.structures.simpleStructures.MetaEditorAttribute;

public class Label extends EditorObject {

    public Label(EditorObject _parent) {
        super(_parent, "label", "scene\\label");

        addAttribute("id",          InputField.ANY)                             .assertRequired();
        addAttribute("depth",       InputField.NUMBER).setDefaultValue("10")    .assertRequired();
        addAttribute("x",           InputField.NUMBER).setDefaultValue("0")     .assertRequired();
        addAttribute("y",           InputField.NUMBER).setDefaultValue("0")     .assertRequired();
        addAttribute("align",       InputField.ANY)   .setDefaultValue("center").assertRequired();
        addAttribute("rotation",    InputField.NUMBER).setDefaultValue("0")     .assertRequired();
        addAttribute("scale",       InputField.NUMBER).setDefaultValue("1")     .assertRequired();
        addAttribute("overlay",     InputField.FLAG)  .setDefaultValue("true")  .assertRequired();
        addAttribute("screenspace", InputField.FLAG)  .setDefaultValue("true")  .assertRequired();
        addAttribute("font",        InputField.FONT)                            .assertRequired();
        addAttribute("text",        InputField.TEXT)                            .assertRequired();
        addAttribute("colorize", InputField.COLOR)    .setDefaultValue("255,255,255");



        setMetaAttributes(MetaEditorAttribute.parse("id,x,y,rotation,scale,depth,colorize,overlay,screenspace,Text<text,font,align>"));

    }


    @Override
    public String getName() {
        return getAttribute("id").stringValue();
    }

}