package com.WorldOfGoo.Scene;

import com.WooGLEFX.EditorObjects.Components.ObjectPosition;
import com.WooGLEFX.Functions.LevelManager;
import com.WooGLEFX.Structures.EditorObject;
import com.WooGLEFX.Structures.InputField;
import com.WooGLEFX.Structures.SimpleStructures.MetaEditorAttribute;

public class Label extends EditorObject {

    public Label(EditorObject _parent) {
        super(_parent, "label");

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

        addObjectPosition(new ObjectPosition(ObjectPosition.RECTANGLE) {
            public double getX() {
                return getAttribute("x").doubleValue();
            }
            public void setX(double x) {
                setAttribute("x", x);
            }
            public double getY() {
                return -getAttribute("y").doubleValue();
            }
            public void setY(double y) {
                setAttribute("y", -y);
            }
            public double getRotation() {
                return getAttribute("rotation").doubleValue();
            }
            public void setRotation(double rotation) {
                setAttribute("rotation", rotation);
            }
            public boolean isVisible() {
                return LevelManager.getLevel().isShowLabels();
            }

            // TODO label width and height

        });

        setMetaAttributes(MetaEditorAttribute.parse("id,x,y,rotation,scale,depth,colorize,overlay,screenspace,Text<text,font,align>"));

    }


    @Override
    public String getName() {
        return getAttribute("id").stringValue();
    }

}
