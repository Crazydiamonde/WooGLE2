package com.WorldOfGoo.Scene;

import java.io.FileNotFoundException;

import com.WooGLEFX.EditorObjects.Components.ObjectPosition;
import com.WooGLEFX.Functions.LevelLoader;
import com.WooGLEFX.Functions.LevelManager;
import com.WooGLEFX.Structures.SimpleStructures.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.WooGLEFX.File.GlobalResourceManager;
import com.WooGLEFX.Structures.EditorObject;
import com.WooGLEFX.Structures.InputField;

import javafx.beans.value.ChangeListener;
import javafx.scene.image.Image;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;

public class SceneLayer extends EditorObject {

    private static final Logger logger = LoggerFactory.getLogger(SceneLayer.class);


    private double animx = 0;
    private double animy = 0;
    private double animrotation = 0;
    private double animscalex = 1;
    private double animscaley = 1;


    public SceneLayer(EditorObject _parent) {
        super(_parent);
        setRealName("SceneLayer");

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

        addObjectPosition(new ObjectPosition(ObjectPosition.IMAGE) {
            public double getX() {
                return getDouble("x") + animx;
            }
            public void setX(double x) {
                setAttribute("x", x);
            }
            public double getY() {
                return -getDouble("y") - animy;
            }
            public void setY(double y) {
                setAttribute("y", -y);
            }
            public double getRotation() {
                return -Math.toRadians(getDouble("rotation") - animrotation);
            }
            public void setRotation(double rotation) {
                setAttribute("rotation", -Math.toDegrees(rotation));
            }
            public double getWidth() {
                return getImage().getWidth() * Math.abs(getDouble("scalex") * animscalex);
            }
            public void setWidth(double width) {
                setAttribute("scalex", width / getImage().getWidth());
            }
            public double getHeight() {
                return getImage().getHeight() * Math.abs(getDouble("scaley") * animscaley);
            }
            public void setHeight(double height) {
                setAttribute("scaley", height / getImage().getHeight());
            }
        });

        setNameAttribute(getAttribute2("name"));
        String meta1 = "id,name,x,y,scalex,scaley,rotation,";
        String meta2 = "Image<image,depth,tilex,tiley,tilecountx,tilecounty,alpha,colorize,anchor,context>";
        String meta3 = "?Anim<anim,animspeed,animdelay,animloop>";
        setMetaAttributes(MetaEditorAttribute.parse(meta1 + meta2 + meta3));

    }


    public static Image colorize(Image image, Color colorize) {
        double rScale = colorize.getR() / 255.0;
        double gScale = colorize.getG() / 255.0;
        double bScale = colorize.getB() / 255.0;

        WritableImage writableImage = new WritableImage((int)image.getWidth(), (int)image.getHeight());
        PixelWriter pixelWriter = writableImage.getPixelWriter();

        int[] pixelBuffer = new int[(int)image.getWidth() * (int)image.getHeight()];

        for (int x = 0; x < image.getWidth() - 1; x++) {
            for (int y = 0; y < image.getHeight() - 1; y++) {

                long pixel = (image.getPixelReader().getArgb(x, y));

                if (pixel < 0) {
                    pixel += 4294967296L;
                }

                // AAAAAAAA RRRRRRRR GGGGGGGG BBBBBBBB

                int pixelA = (int)(pixel / (2 << 23));
                int pixelR = (int)((pixel % (2 << 23)) / (2 << 15));
                int pixelG = (int)((pixel % (2 << 15)) / (2 << 7));
                int pixelB = (int)(pixel % (2 << 7));

                int scaledR = (int)(pixelR * rScale);
                int scaledG = (int)(pixelG * gScale);
                int scaledB = (int)(pixelB * bScale);

                pixelBuffer[y * (int)image.getWidth() + x] = (pixelA * (2 << 23)) + (scaledR * (2 << 15)) + (scaledG * (2 << 7)) + scaledB;

            }
        }

        pixelWriter.setPixels(0, 0, (int)image.getWidth(), (int)image.getHeight(), PixelFormat.getIntArgbInstance(), pixelBuffer, 0, (int)image.getWidth());

        return writableImage;
    }

    @Override
    public void update() {
        if (!getAttribute("image").equals("")) {
            try {
                setImage(GlobalResourceManager.getImage(getAttribute("image"), getLevel().getVersion()));
                Color color = Color.parse(getAttribute("colorize"));
                setImage(colorize(getImage(), color));
            } catch (FileNotFoundException e) {
                setImage(null);
                if (!LevelLoader.failedResources.contains("From SceneLayer: \"" + getAttribute("image") + "\" (version " + LevelManager.getLevel().getVersion() + ")")) {
                    LevelLoader.failedResources.add("From SceneLayer: \"" + getAttribute("image") + "\" (version " + LevelManager.getLevel().getVersion() + ")");
                }
            }
        }

        ChangeListener<String> wizard = (observable, oldValue, newValue) -> {
            logger.trace("Image changed from " + oldValue + " to " + newValue);
            try {
                setImage(GlobalResourceManager.getImage(getAttribute("image"), getLevel().getVersion()));
                Color color = Color.parse(getAttribute("colorize"));
                setImage(colorize(getImage(), color));

            } catch (FileNotFoundException e) {
                setImage(null);
                if (!LevelLoader.failedResources.contains("From SceneLayer: \"" + getAttribute("image") + "\" (version " + LevelManager.getLevel().getVersion() + ")")) {
                    LevelLoader.failedResources.add("From SceneLayer: \"" + getAttribute("image") + "\" (version " + LevelManager.getLevel().getVersion() + ")");
                }
            }
        };

        setChangeListener("image", wizard);
        setChangeListener("colorize", wizard);
    }


    private static double lerp(double a, double b, double c) {
        return a + (b - a) * c;
    }


    public void updateWithAnimation(WoGAnimation animation, float timer){
        double animspeed = getDouble("animspeed");
        double animdelay = getDouble("animdelay");
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
    public boolean isVisible() {
        return LevelManager.getLevel().isShowGraphics();
    }

}
