package com.WorldOfGoo.Scene;

import com.WooGLEFX.EditorObjects.Components.ObjectPosition;
import com.WooGLEFX.Functions.LevelManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.WooGLEFX.Structures.EditorObject;
import com.WooGLEFX.Structures.InputField;
import com.WooGLEFX.Structures.SimpleStructures.Color;
import com.WooGLEFX.Structures.SimpleStructures.MetaEditorAttribute;

import javafx.beans.value.ChangeListener;
import javafx.scene.image.Image;

public class Button extends EditorObject {

    private static final Logger logger = LoggerFactory.getLogger(Button.class);


    private Image image;


    public Button(EditorObject _parent) {
        super(_parent, "button");

        addAttribute("id",                    InputField.ANY)                                  .assertRequired();
        addAttribute("depth",                 InputField.NUMBER).assertRequired();
        addAttribute("x",                     InputField.NUMBER).assertRequired();
        addAttribute("y",                     InputField.NUMBER).setDefaultValue("0")          .assertRequired();
        addAttribute("scalex",                InputField.NUMBER).setDefaultValue("1")          .assertRequired();
        addAttribute("scaley",                InputField.NUMBER).setDefaultValue("1")          .assertRequired();
        addAttribute("rotation",              InputField.NUMBER).setDefaultValue("0")          .assertRequired();
        addAttribute("anchor",                InputField.ANY);
        addAttribute("alpha",                 InputField.ANY)   .setDefaultValue("1")          .assertRequired();
        addAttribute("colorize",              InputField.ANY)   .setDefaultValue("255,255,255").assertRequired();
        addAttribute("up",                    InputField.IMAGE);
        addAttribute("over",                  InputField.IMAGE);
        addAttribute("down",                  InputField.IMAGE);
        addAttribute("downover",              InputField.IMAGE);
        addAttribute("armed",                 InputField.IMAGE);
        addAttribute("downarmed",             InputField.IMAGE);
        addAttribute("disabled",              InputField.IMAGE);
        addAttribute("downdisabled",          InputField.IMAGE);
        addAttribute("latch",                 InputField.FLAG);
        addAttribute("font",                  InputField.ANY);
        addAttribute("text",                  InputField.ANY);
        addAttribute("onclick",               InputField.ANY);
        addAttribute("onmouseenter",          InputField.ANY);
        addAttribute("onmouseexit",           InputField.ANY);
        addAttribute("textcolorup",           InputField.ANY);
        addAttribute("textcolorupover",       InputField.ANY);
        addAttribute("textcoloruparmed",      InputField.ANY);
        addAttribute("textcolorupdisabled",   InputField.ANY);
        addAttribute("textcolordown",         InputField.ANY);
        addAttribute("textcolordownover",     InputField.ANY);
        addAttribute("textcolordownarmed",    InputField.ANY);
        addAttribute("textcolordowndisabled", InputField.ANY);
        addAttribute("tooltip",               InputField.ANY);
        addAttribute("overlay",               InputField.ANY);
        addAttribute("screenspace",           InputField.ANY);
        addAttribute("context",               InputField.ANY);

        addObjectPosition(new ObjectPosition(ObjectPosition.IMAGE) {
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
                return -Math.toRadians(getAttribute("rotation").doubleValue());
            }
            public void setRotation(double rotation) {
                setAttribute("rotation", -Math.toDegrees(rotation));
            }
            public double getWidth() {
                double scalex = getAttribute("scalex").doubleValue();
                return image.getWidth() * Math.abs(scalex);
            }
            public void setWidth(double width) {
                setAttribute("scalex", width / image.getWidth());
            }
            public double getHeight() {
                double scaley = getAttribute("scaley").doubleValue();
                return image.getHeight() * Math.abs(scaley);
            }
            public void setHeight(double height) {
                setAttribute("scaley", height / image.getHeight());
            }
            public double getDepth() {
                return getAttribute("depth").doubleValue();
            }
            public Image getImage() {
                return image;
            }
            public boolean isVisible() {
                return LevelManager.getLevel().isShowGraphics();
            }
        });

        setMetaAttributes(MetaEditorAttribute.parse("id,x,y,anchor,depth,alpha,rotation,scalex,scaley,colorize,?Graphics<up,over,down,downover,armed,downarmed,disabled,downdisabled>latch,?Events<onclick,onmouseenter,onmouseexit>?Text<font,text,tooltip,textcolorup,textcolorupover,textcoloruparmed,textcolorupdisabled,textcolordown,textcolordownover,textcolordownarmed,textcolordowndisabled>overlay,screenspace,context,"));

    }


    @Override
    public String getName() {
        return getAttribute("id").stringValue();
    }


    @Override
    public void update() {

        //try {
        colorize();
        //} catch (FileNotFoundException e) {
        //    LevelLoader.failedResources.add("From Button: image \"" + getAttribute("up") + "\" (version " + LevelManager.getLevel().getVersion() + ")");
        //}

        ChangeListener<String> wizard = (observable, oldValue, newValue) -> {
            logger.trace("Image changed from " + oldValue + " to " + newValue);
            //try {
            colorize();
            //} catch (FileNotFoundException e) {
            //    LevelLoader.failedResources.add("From Button: Image \"" + getAttribute("up") + "\" (version " + LevelManager.getLevel().getVersion() + ")");
            //}
        };

        //addChangeListener("up", wizard);
        //addChangeListener("colorize", wizard);

    }


    private void colorize() {
        image = getAttribute("up").imageValue(LevelManager.getVersion());
        Color color = getAttribute("colorize").colorValue();
        image = SceneLayer.colorize(image, color);
    }

}
