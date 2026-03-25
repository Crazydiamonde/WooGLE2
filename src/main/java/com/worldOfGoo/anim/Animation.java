package com.worldOfGoo.anim;

import com.woogleFX.assets.wog1.animation.WOG1Animation;
import com.woogleFX.editorObjects.EditorObject;
import com.woogleFX.assets.Asset;
import com.woogleFX.editorObjects.objectComponents.ImageComponent;
import com.woogleFX.editorObjects.objectComponents.LineComponent;
import com.woogleFX.file.FileManager;
import com.woogleFX.assets.GameVersion;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

public class Animation extends EditorObject {

    public Animation(EditorObject parent, GameVersion version) {
        super(parent, version);
    }

    @Override
    public void onLoaded(Asset asset) {
        super.onLoaded(asset);

        if (!(asset instanceof WOG1Animation animation)) return;

        addObjectComponent(new ImageComponent(this) {
            @Override
            public Image getImage() {
                return animation.getPreviewImage();
            }

            @Override
            public double getX() {
                return getProperty("x", animation.getTime());
            }

            @Override
            public double getY() {
                return getProperty("y", animation.getTime());
            }

            @Override
            public double getScaleX() {
                return getProperty("scaleX", animation.getTime());
            }

            @Override
            public double getScaleY() {
                return getProperty("scaleY", animation.getTime());
            }

            @Override
            public double getAlpha() {
                return getProperty("alpha", animation.getTime()) / 255.0;
            }

            @Override
            public double getRotation() {
                return -Math.toRadians(getProperty("angle", animation.getTime()));
            }

            @Override
            public double getDepth() {
                return 0;
            }

        });

        addObjectComponent(new LineComponent(this) {
            @Override
            public double getLineWidth() {
                return 10;
            }

            @Override
            public double getX() {
                return 0;
            }

            @Override
            public double getY() {
                return 0;
            }

            @Override
            public double getDepth() {
                return -1;
            }

            @Override
            public double getRotation() {
                return Math.PI / 2;
            }

            @Override
            public Paint getColor() {
                return new Color(0.5, 1.0, 0.5, 0.5);
            }

            @Override
            public boolean isSelectable() {
                return false;
            }

        });

        addObjectComponent(new LineComponent(this) {
            @Override
            public double getLineWidth() {
                return 10;
            }

            @Override
            public double getX() {
                return 0;
            }

            @Override
            public double getY() {
                return 0;
            }

            @Override
            public double getDepth() {
                return -1;
            }

            @Override
            public Paint getColor() {
                return new Color(1.0, 0.5, 0.5, 0.5);
            }

            @Override
            public boolean isSelectable() {
                return false;
            }

        });

    }

    @Override
    public void frameUpdate(double deltaTime) {
        super.frameUpdate(deltaTime);

        if (!(getAsset() instanceof WOG1Animation animation)) return;

        if (animation.isPlaying()) animation.setTime(animation.getTime() + deltaTime);

    }


    public double interpolate(EditorObject keyframe, double time, String property) {
        if (keyframe.getAttribute(property).actualValue().isEmpty()) {
            EditorObject otherKeyframe = keyframe;
            while (otherKeyframe.getAttribute(property).actualValue().isEmpty()) {
                if (otherKeyframe == getChildren().get(0)) {
                    return keyframe.getAttribute(property).doubleValue();
                }
                otherKeyframe = getChildren().get(getChildren().indexOf(otherKeyframe) - 1);
            }
            if (!otherKeyframe.getAttribute(property).actualValue().isEmpty())
                return otherKeyframe.getAttribute(property).doubleValue();
        }
        double x1 = keyframe.getAttribute(property).doubleValue();
        if (keyframe.getAttribute("interpolation").intValue() == 0) return x1;
        EditorObject otherKeyframe = keyframe;
        do {
            if (otherKeyframe == getChildren().get(getChildren().size() - 1)) return x1;
            otherKeyframe = getChildren().get(getChildren().indexOf(otherKeyframe) + 1);
        } while (otherKeyframe.getAttribute(property).actualValue().isEmpty());
        double x2 = otherKeyframe.getAttribute(property).doubleValue();
        double time1 = keyframe.getAttribute("time").doubleValue();
        double time2 = otherKeyframe.getAttribute("time").doubleValue();
        double alpha = (time - time1) / (time2 - time1);
        return x1 * (1 - alpha) + x2 * alpha;
    }


    private boolean isKeyframeVisible(EditorObject keyframe, double time) {

        double time1 = keyframe.getAttribute("time").doubleValue();
        if (time < time1) return false;
        if (keyframe == getChildren().get(getChildren().size() - 1)) return true;
        EditorObject otherKeyframe = getChildren().get(getChildren().indexOf(keyframe) + 1);
        return time < otherKeyframe.getAttribute("time").doubleValue();
    }


    public double getProperty(String propertyName, double time) {

        for (EditorObject keyframe : getChildren())
            if (isKeyframeVisible(keyframe, time))
                return interpolate(keyframe, time, propertyName);

        return -1;

    }


}