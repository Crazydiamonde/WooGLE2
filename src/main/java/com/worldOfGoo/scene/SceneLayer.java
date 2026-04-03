package com.worldOfGoo.scene;

import com.woogleFX.assets.Asset;
import com.woogleFX.assets.wog1.animation.WOG1Animation;
import com.woogleFX.editorObjects.EditorObject;
import com.woogleFX.editorObjects.objectComponents.ImageComponent;
import com.woogleFX.editorObjects.objectComponents.RectangleComponent;
import com.woogleFX.engine.AssetManager;
import com.woogleFX.assets.GameVersion;

import com.woogleFX.engine.gui.alarms.ErrorAlarm;
import com.worldOfGoo.anim.Animation;
import com.worldOfGoo.anim.Keyframe;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class SceneLayer extends EditorObject {

    private WOG1Animation animation;
    public WOG1Animation getAnimation() {
        return animation;
    }


    private double animx = 0;
    private double animy = 0;
    private double animrotation = 0;
    private double animscalex = 1;
    private double animscaley = 1;


    public SceneLayer(EditorObject _parent, GameVersion version) {
        super(_parent, version);
    }

    @Override
    public void onLoaded(Asset asset) {
        super.onLoaded(asset);

        addObjectComponent(new ImageComponent(this) {
            public double getX() {
                double extraX = AssetManager.getVisibility("animations") == 1 ? animx : 0;
                return getAttribute("x").doubleValue() + extraX;
            }
            public void setX(double x) {
                setAttribute("x", x);
            }
            public double getY() {
                double extraY = AssetManager.getVisibility("animations") == 1 ? animy : 0;
                return -getAttribute("y").doubleValue() + extraY;
            }
            public void setY(double y) {
                setAttribute("y", -y);
            }
            public double getRotation() {
                double extraRotation = AssetManager.getVisibility("animations") == 1 ? animrotation : 0;
                return -Math.toRadians(getAttribute("rotation").doubleValue() + extraRotation);
            }
            public void setRotation(double rotation) {
                setAttribute("rotation", -Math.toDegrees(rotation));
            }
            public double getScaleX() {
                double extraScaleX = AssetManager.getVisibility("animations") == 1 ? animscalex : 1;
                return getAttribute("scalex").doubleValue() * extraScaleX;
            }
            public void setScaleX(double scaleX) {
                setAttribute("scalex", scaleX);
            }
            public double getScaleY() {
                double extraScaleY = AssetManager.getVisibility("animations") == 1 ? animscaley : 1;
                return getAttribute("scaley").doubleValue() * extraScaleY;
            }
            public void setScaleY(double scaleY) {
                setAttribute("scaley", scaleY);
            }
            public double getDepth() {
                return getAttribute("depth").doubleValue();
            }
            public double getAlpha() {
                return getAttribute("alpha").doubleValue();
            }
            public Image getImage() {
                return getAttribute("image").imageValue(asset.getResources(), getVersion());
            }
            public Color getColorize() {
                com.woogleFX.editorObjects.attributes.dataTypes.Color color =
                        getAttribute("colorize").colorValue();
                return new Color(color.getR() / 255.0, color.getG() / 255.0, color.getB() / 255.0, color.getA() / 255.0);
            }
            public boolean isVisible() {
                return AssetManager.getVisibility("graphics") == 1;
            }
        });

        // Invalid image rectangle
        addObjectComponent(new RectangleComponent(this) {
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
            public double getWidth() {
                return 100;
            }
            public double getHeight() {
                return 100;
            }
            public double getDepth() {
                return getAttribute("depth").doubleValue();
            }
            public double getEdgeSize() {
                return 5.0;
            }
            public Color getColor() {
                return new Color(0, 0, 0, 0);
            }
            public boolean isEdgeOnly() {
                return true;
            }
            public Color getBorderColor() {
                return new Color(0.5, 0.0, 0.0, 1.0);
            }
            public boolean isVisible() {
                return AssetManager.getVisibility("graphics") == 1 &&
                        getAttribute("image").imageValue(asset.getResources(), getVersion()) == null;
            }
        });

        getAttribute("anim").addChangeListener((observable, oldValue, newValue) -> updateAnimation());
        //getAttribute("colorize").addChangeListener((observable, oldValue, newValue) -> updateImage());

        updateAnimation();

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


    private double animationTimeElapsed = 0;

    @Override
    public void frameUpdate(double deltaTime) {

        animationTimeElapsed += deltaTime;

        if (animation == null) return;

        double animspeed = getAttribute("animspeed").doubleValue();
        double animdelay = getAttribute("animdelay").doubleValue();
        double effectiveAnimationTime = animationTimeElapsed * animspeed - animdelay;

        Animation animation1 = animation.getAnimation();

        double length = 0;
        for (EditorObject child : animation1.getChildren()) if (child instanceof Keyframe keyframe) {
            double time = keyframe.getAttribute("time").doubleValue();
            if (time > length) length = time;
        }
        if (effectiveAnimationTime < 0) effectiveAnimationTime += ((int)(-effectiveAnimationTime / length) + 10) * length;
        effectiveAnimationTime %= length;

        animx = animation1.getProperty("x", effectiveAnimationTime);
        animy = animation1.getProperty("y", effectiveAnimationTime);
        animscalex = animation1.getProperty("scaleX", effectiveAnimationTime);
        animscaley = animation1.getProperty("scaleY", effectiveAnimationTime);
        // anim animation1.getProperty("alpha", animation.getTime()) / 255.0;
        animrotation = animation1.getProperty("angle", effectiveAnimationTime);

    }

    @Override
    public void update() {
        updateAnimation();
    }



    private void updateAnimation() {

        // Animations in a level folder will apply to SceneLayers with a matching ID
        // Ex. Fisty_eye.anim.binltl and the Fisty_eye SceneLayer
        // I learned this TODAY

        String suffix = (getVersion() == GameVersion.VERSION_WOG1_OLD) ? ".binltl" : ".binuni";

        try {
            if (!getAttribute("anim").stringValue().isEmpty()) {
                animation = WOG1Animation.assetSelector.openInstance(getAttribute("anim").stringValue(), getVersion());
            } else if (Files.exists(Path.of(getAsset().getFile().toPath() + "/" + getAttribute("id").stringValue() + ".anim" + suffix))) {
                animation = WOG1Animation.assetSelector.openInstance(new File(getAsset().getFile().toPath() + "/" + getAttribute("id").stringValue() + ".anim" + suffix), getAttribute("id").stringValue(), getVersion());
            }
        } catch (IOException e) {
            ErrorAlarm.show(e);
        }

    }

}
