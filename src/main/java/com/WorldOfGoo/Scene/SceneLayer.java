package com.WorldOfGoo.Scene;

import java.io.FileNotFoundException;

import com.WooGLEFX.EditorObjects.Components.ObjectPosition;
import com.WooGLEFX.Engine.Renderer;
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


    private Image image;


    private double animx = 0;
    private double animy = 0;
    private double animrotation = 0;
    private double animscalex = 1;
    private double animscaley = 1;


    public SceneLayer(EditorObject _parent) {
        super(_parent, "SceneLayer");

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
                return getAttribute("x").doubleValue() + animx;
            }
            public void setX(double x) {
                setAttribute("x", x);
            }
            public double getY() {
                return -getAttribute("y").doubleValue() - animy;
            }
            public void setY(double y) {
                setAttribute("y", -y);
            }
            public double getRotation() {
                return -Math.toRadians(getAttribute("rotation").doubleValue() - animrotation);
            }
            public void setRotation(double rotation) {
                setAttribute("rotation", -Math.toDegrees(rotation));
            }
            public double getWidth() {
                return getImage().getWidth() * Math.abs(getAttribute("scalex").doubleValue() * animscalex);
            }
            public void setWidth(double width) {
                setAttribute("scalex", width / getImage().getWidth());
            }
            public double getHeight() {
                return getImage().getHeight() * Math.abs(getAttribute("scaley").doubleValue() * animscaley);
            }
            public void setHeight(double height) {
                setAttribute("scaley", height / getImage().getHeight());
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

        String meta1 = "id,name,x,y,scalex,scaley,rotation,";
        String meta2 = "Image<image,depth,tilex,tiley,tilecountx,tilecounty,alpha,colorize,anchor,context>";
        String meta3 = "?Anim<anim,animspeed,animdelay,animloop>";
        setMetaAttributes(MetaEditorAttribute.parse(meta1 + meta2 + meta3));

        getAttribute("image").addChangeListener((observable, oldValue, newValue) -> updateImage());
        getAttribute("colorize").addChangeListener((observable, oldValue, newValue) -> updateImage());

    }


    @Override
    public String getName() {
        return getAttribute("name").stringValue();
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
            }
        } catch (FileNotFoundException ignored) {

        }

    }

}
