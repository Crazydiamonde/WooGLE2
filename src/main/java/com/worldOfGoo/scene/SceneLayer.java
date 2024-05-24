package com.worldOfGoo.scene;

import java.io.FileNotFoundException;

import com.woogleFX.editorObjects.ImageUtility;
import com.woogleFX.editorObjects.objectComponents.ImageComponent;
import com.woogleFX.functions.LevelManager;
import com.woogleFX.structures.simpleStructures.*;

import com.woogleFX.editorObjects.EditorObject;
import com.woogleFX.editorObjects.InputField;

import javafx.scene.image.Image;

public class SceneLayer extends EditorObject {

    private Image image;


    private double animx = 0;
    private double animy = 0;
    private double animrotation = 0;
    private double animscalex = 1;
    private double animscaley = 1;


    public SceneLayer(EditorObject _parent) {
        super(_parent, "SceneLayer", "scene\\SceneLayer");

        addAttribute("id",         InputField.ANY);
        addAttribute("name",       InputField.ANY);
        addAttribute("depth",      InputField.NUMBER)         .setDefaultValue("0")            .assertRequired();
        addAttribute("x",          InputField.NUMBER)         .setDefaultValue("0")            .assertRequired();
        addAttribute("y",          InputField.NUMBER)         .setDefaultValue("0")            .assertRequired();
        addAttribute("scalex",     InputField.NUMBER)         .setDefaultValue("1");
        addAttribute("scaley",     InputField.NUMBER)         .setDefaultValue("1");
        addAttribute("rotation",   InputField.NUMBER)         .setDefaultValue("0");
        addAttribute("alpha",      InputField.NUMBER)         .setDefaultValue("1");
        addAttribute("colorize",   InputField.COLOR)          .setDefaultValue("255,255,255");
        addAttribute("image",      InputField.IMAGE_REQUIRED)                                  .assertRequired();
        addAttribute("tilex",      InputField.FLAG)           .setDefaultValue("false");
        addAttribute("tiley",      InputField.FLAG)           .setDefaultValue("false");
        addAttribute("tilecountx", InputField.NUMBER)         .setDefaultValue("0");
        addAttribute("tilecounty", InputField.NUMBER)         .setDefaultValue("0");
        addAttribute("anim",       InputField.ANIMATION);
        addAttribute("animspeed",  InputField.NUMBER)         .setDefaultValue("1");
        addAttribute("animdelay",  InputField.NUMBER)         .setDefaultValue("0");
        addAttribute("animloop",   InputField.FLAG)           .setDefaultValue("false");
        addAttribute("anchor",     InputField.GEOMETRY);
        addAttribute("context",    InputField.CONTEXT)        .setDefaultValue("screen");

        addObjectComponent(new ImageComponent() {
            public double getX() {
                double extraX = LevelManager.getLevel().getVisibilitySettings().isShowAnimations() ? animx : 0;
                return getAttribute("x").doubleValue() + extraX;
            }
            public void setX(double x) {
                setAttribute("x", x);
            }
            public double getY() {
                double extraY = LevelManager.getLevel().getVisibilitySettings().isShowAnimations() ? animy : 0;
                return -getAttribute("y").doubleValue() + extraY;
            }
            public void setY(double y) {
                setAttribute("y", -y);
            }
            public double getRotation() {
                double extraRotation = LevelManager.getLevel().getVisibilitySettings().isShowAnimations() ? animrotation : 0;
                return -Math.toRadians(getAttribute("rotation").doubleValue() - extraRotation);
            }
            public void setRotation(double rotation) {
                setAttribute("rotation", -Math.toDegrees(rotation));
            }
            public double getScaleX() {
                double extraScaleX = LevelManager.getLevel().getVisibilitySettings().isShowAnimations() ? animscalex : 1;
                return getAttribute("scalex").doubleValue() * extraScaleX;
            }
            public void setScaleX(double scaleX) {
                setAttribute("scalex", scaleX);
            }
            public double getScaleY() {
                double extraScaleY = LevelManager.getLevel().getVisibilitySettings().isShowAnimations() ? animscaley : 1;
                return getAttribute("scaley").doubleValue() * extraScaleY;
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

        String general = "id,name,x,y,scalex,scaley,rotation,";
        String image = "Image<image,depth,tilex,tiley,tilecountx,tilecounty,alpha,colorize,anchor,context>";
        String anim = "?Anim<anim,animspeed,animdelay,animloop>";
        setMetaAttributes(MetaEditorAttribute.parse(general + image + anim));

        getAttribute("image").addChangeListener((observable, oldValue, newValue) -> updateImage());
        getAttribute("colorize").addChangeListener((observable, oldValue, newValue) -> updateImage());

    }


    @Override
    public String getName() {
        return getAttribute("name").stringValue();
    }


    private static double lerp(double a, double b, double c) {
        return a + (b - a) * c;
    }


    private static float reverseInterpolate(float a, float b, float c) {
        if (b > a) {
            return (c - a) / (b - a);
        } else if (b == a) {
            return a;
        } else {
            return (c - b) / (a - b);
        }
    }


    public void updateWithAnimation(WoGAnimation animation, float timer){
        double animspeed = getAttribute("animspeed").doubleValue();
        double animdelay = getAttribute("animdelay").doubleValue();
        double goodTimer = (timer * animspeed - animdelay);
        if (goodTimer >= 0) {
            goodTimer %= animation.getFrameTimes()[animation.getFrameTimes().length - 1];
        } else {
            while (goodTimer < 0){
                goodTimer += animation.getFrameTimes()[animation.getFrameTimes().length - 1];
            }
        }
        for (int i2 : new int[]{ 0, 1, 2 }) {
            if (animation.getTransformFrames()[i2].length == 0) continue;
            int i = 0;
            for (int i3 = 0; i3 < animation.getFrameTimes().length; i3++) {
                if (goodTimer < animation.getFrameTimes()[i3] && animation.getTransformFrames()[i2][i3] != null) {
                    break;
                } else if (animation.getTransformFrames()[i2][i3] != null) {
                    i = i3;
                }
            }
            Keyframe currentFrame = animation.getTransformFrames()[i2][i];
            Keyframe nextFrame;
            int nextIndex = currentFrame.getNextFrameIndex();
            if (currentFrame.getNextFrameIndex() == -1){
                nextIndex = 0;
                nextFrame = currentFrame;
            } else {
                nextFrame = animation.getTransformFrames()[i2][currentFrame.getNextFrameIndex()];
            }
            float timerInterpolateValue = reverseInterpolate(animation.getFrameTimes()[i], animation.getFrameTimes()[nextIndex], (float)goodTimer);
            if (i2 == 0) {
                animscalex = lerp(currentFrame.getX(), nextFrame.getX(), timerInterpolateValue);
                animscaley = lerp(currentFrame.getY(), nextFrame.getY(), timerInterpolateValue);
            } else if (i2 == 1) {
                animrotation = lerp(currentFrame.getAngle(), nextFrame.getAngle(), timerInterpolateValue);
            } else {
                animx = lerp(currentFrame.getX(), nextFrame.getX(), timerInterpolateValue);
                animy = lerp(currentFrame.getY(), nextFrame.getY(), timerInterpolateValue);
            }
        }
    }


    @Override
    public void update() {
        updateImage();
    }


    private void updateImage() {

        try {
            if (!getAttribute("image").stringValue().isEmpty()) {
                image = getAttribute("image").imageValue(LevelManager.getVersion());
                image = ImageUtility.colorize(image, getAttribute("colorize").colorValue());
            }
        } catch (FileNotFoundException ignored) {

        }

    }

}
