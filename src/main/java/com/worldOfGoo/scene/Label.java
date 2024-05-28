package com.worldOfGoo.scene;

import com.woogleFX.editorObjects.EditorObject;
import com.woogleFX.editorObjects.InputField;
import com.woogleFX.editorObjects._Font;
import com.woogleFX.editorObjects.objectComponents.TextComponent;
import com.woogleFX.file.resourceManagers.ResourceManager;
import com.woogleFX.functions.LevelManager;
import com.woogleFX.structures.GameVersion;
import com.woogleFX.structures.simpleStructures.MetaEditorAttribute;
import com.worldOfGoo.text.TextString;

import java.io.FileNotFoundException;

public class Label extends EditorObject {

    public Label(EditorObject _parent, GameVersion version) {
        super(_parent, "label", version);

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

        addObjectComponent(new TextComponent() {
            @Override
            public _Font getFont() {
                try {
                    return ResourceManager.getFont(LevelManager.getLevel().getResrc(), getAttribute("font").stringValue(), LevelManager.getLevel().getVersion());
                } catch (FileNotFoundException e) {
                    return null;
                }
            }

            @Override
            public String getText() {
                for (EditorObject editorObject : LevelManager.getLevel().getText()) {
                    if (editorObject instanceof TextString textString) {
                        if (textString.getAttribute("id").stringValue().equals(getAttribute("text").stringValue())) {
                            return textString.getAttribute("text").stringValue();
                        }
                    }
                }
                return null;
            }

            @Override
            public double getX() {
                return getAttribute("x").doubleValue();
            }

            @Override
            public double getY() {
                return -getAttribute("y").doubleValue();
            }

            @Override
            public double getDepth() {
                return 0;
            }

            @Override
            public double getScale() {
                return getAttribute("scale").doubleValue();
            }

            @Override
            public double getRotation() {
                return -Math.toRadians(getAttribute("rotation").doubleValue());
            }

        });

        setMetaAttributes(MetaEditorAttribute.parse("id,x,y,rotation,scale,depth,colorize,overlay,screenspace,Text<text,font,align>"));

    }


    @Override
    public String getName() {
        return getAttribute("id").stringValue();
    }

}
