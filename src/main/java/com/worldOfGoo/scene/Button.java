package com.worldOfGoo.scene;

import com.woogleFX.editorObjects.ImageUtility;
import com.woogleFX.editorObjects.objectComponents.ImageComponent;
import com.woogleFX.engine.LevelManager;

import com.woogleFX.editorObjects.EditorObject;
import com.woogleFX.editorObjects.attributes.InputField;
import com.woogleFX.gameData.level.GameVersion;
import com.woogleFX.editorObjects.attributes.MetaEditorAttribute;

import javafx.scene.image.Image;

import java.io.FileNotFoundException;

public class Button extends EditorObject {

    private Image image;


    public Button(EditorObject _parent, GameVersion version) {
        super(_parent, "button", version);

        addAttribute("id",                    InputField.ANY)                                  .assertRequired();
        addAttribute("depth",                 InputField.NUMBER).setDefaultValue("0")          .assertRequired();
        addAttribute("x",                     InputField.NUMBER)                               .assertRequired();
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

        addObjectComponent(new ImageComponent() {
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
            public double getScaleX() {
                return getAttribute("scalex").doubleValue();
            }
            public void setScaleX(double scaleX) {
                setAttribute("scalex", scaleX);
            }
            public double getScaleY() {
                return getAttribute("scaley").doubleValue();
            }
            public void setScaleY(double scaleY) {
                setAttribute("scaley", scaleY);
            }
            public double getDepth() {
                return getAttribute("depth").doubleValue();
            }
            public Image getImage() {
                return image;
            }
            public boolean isVisible() {
                return LevelManager.getLevel().getVisibilitySettings().isShowGraphics();
            }
        });

        setMetaAttributes(MetaEditorAttribute.parse("id,x,y,anchor,depth,alpha,rotation,scalex,scaley,colorize,?Graphics<up,over,down,downover,armed,downarmed,disabled,downdisabled>latch,?Events<onclick,onmouseenter,onmouseexit>?Text<font,text,tooltip,textcolorup,textcolorupover,textcoloruparmed,textcolorupdisabled,textcolordown,textcolordownover,textcolordownarmed,textcolordowndisabled>overlay,screenspace,context,"));

        getAttribute("up").addChangeListener((observable, oldValue, newValue) -> updateImage());
        getAttribute("colorize").addChangeListener((observable, oldValue, newValue) -> updateImage());

    }


    @Override
    public String getName() {
        return getAttribute("id").stringValue();
    }


    @Override
    public void update() {
        updateImage();
    }


    private void updateImage() {

        if (LevelManager.getLevel() == null) return;

        try {
            if (!getAttribute("up").stringValue().isEmpty()) {
                image = getAttribute("up").imageValue(LevelManager.getLevel().getResrc(), getVersion());
                image = ImageUtility.colorize(image, getAttribute("colorize").colorValue());
            }
        } catch (FileNotFoundException ignored) {

        }

    }

}
