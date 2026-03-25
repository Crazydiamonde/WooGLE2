package com.worldOfGoo.movie;

import com.woogleFX.editorObjects.EditorObject;
import com.woogleFX.assets.Asset;
import com.woogleFX.assets.wog1.movie.WOG1Movie;
import com.woogleFX.editorObjects.objectComponents.ImageComponent;
import com.woogleFX.editorObjects.objectComponents.TextComponent;
import com.woogleFX.file.resourceManagers.ResourceManager;
import com.woogleFX.gameData.font._Font;
import com.woogleFX.assets.GameVersion;
import com.worldOfGoo.anim.Animation;
import com.worldOfGoo.text.string;
import javafx.scene.image.Image;
import javafx.scene.paint.Paint;

public class Actor extends EditorObject {

    public Actor(EditorObject parent, GameVersion version) {
        super(parent, version);
    }

    private Animation animation;
    public Animation getAnimation() {
        return animation;
    }
    public void setAnimation(Animation animation) {
        this.animation = animation;
    }

    @Override
    public void onLoaded(Asset asset) {
        super.onLoaded(asset);

        if (!(asset instanceof WOG1Movie movie)) return;

        if (getAttribute("type").intValue() == 0) {

            Image image = ResourceManager.getImage(asset.getResources(), getAttribute("image").stringValue(), getVersion());

            for (EditorObject keyframe : animation.getChildren()) {

                addObjectComponent(new ImageComponent(this) {
                    @Override
                    public Image getImage() {
                        return image;
                    }

                    @Override
                    public double getX() {
                        return animation.interpolate(keyframe, movie.getTime(), "x");
                    }

                    @Override
                    public double getY() {
                        return animation.interpolate(keyframe, movie.getTime(), "y");
                    }

                    @Override
                    public double getScaleX() {
                        return animation.interpolate(keyframe, movie.getTime(), "scaleX");
                    }

                    @Override
                    public double getScaleY() {
                        return animation.interpolate(keyframe, movie.getTime(), "scaleY");
                    }

                    @Override
                    public double getAlpha() {
                        return animation.interpolate(keyframe, movie.getTime(), "alpha") / 255.0;
                    }

                    @Override
                    public double getRotation() {
                        return -Math.toRadians(animation.interpolate(keyframe, movie.getTime(), "angle"));
                    }

                    @Override
                    public double getDepth() {
                        return getAttribute("depth").doubleValue();
                    }

                    @Override
                    public boolean isVisible() {
                        double time = keyframe.getAttribute("time").doubleValue();
                        if (movie.getTime() < time) return false;
                        if (keyframe == animation.getChildren().get(animation.getChildren().size() - 1)) return true;
                        EditorObject otherKeyframe = animation.getChildren().get(animation.getChildren().indexOf(keyframe) + 1);
                        return movie.getTime() < otherKeyframe.getAttribute("time").doubleValue();
                    }

                });

            }

        } else {

            for (EditorObject keyframe : animation.getChildren()) {

                addObjectComponent(new TextComponent(this) {

                    @Override
                    public double getScale() {
                        return animation.interpolate(keyframe, movie.getTime(), "scaleX");
                    }

                    @Override
                    public Paint getColor() {
                        return null;
                    }

                    @Override
                    public _Font getFont() {
                        return ResourceManager.getFont(asset.getResources(), getAttribute("font").stringValue(), getVersion());
                    }

                    @Override
                    public String getText() {
                        string text = ResourceManager.getText(asset.getResources(), getAttribute("label").stringValue().substring(1), getVersion());
                        if (text == null) {
                            return "";
                        }
                        return text.getAttribute("text").stringValue();
                    }

                    @Override
                    public boolean isCentered() {
                        return getAttribute("labelJustification").intValue() == 1;
                    }

                    @Override
                    public double getX() {
                        return animation.interpolate(keyframe, movie.getTime(), "x");
                    }

                    @Override
                    public double getY() {
                        return animation.interpolate(keyframe, movie.getTime(), "y");
                    }

                    @Override
                    public double getAlpha() {
                        return animation.interpolate(keyframe, movie.getTime(), "alpha") / 255.0;
                    }

                    @Override
                    public double getRotation() {
                        return -Math.toRadians(animation.interpolate(keyframe, movie.getTime(), "angle"));
                    }

                    @Override
                    public double getDepth() {
                        return getAttribute("depth").doubleValue();
                    }

                    @Override
                    public boolean isVisible() {
                        double time = keyframe.getAttribute("time").doubleValue();
                        if (movie.getTime() < time) return false;
                        if (keyframe == animation.getChildren().get(animation.getChildren().size() - 1)) return true;
                        EditorObject otherKeyframe = animation.getChildren().get(animation.getChildren().indexOf(keyframe) + 1);
                        return movie.getTime() < otherKeyframe.getAttribute("time").doubleValue();
                    }

                });

            }

        }

    }

    @Override
    public String getName() {
        if (!getAttribute("image").stringValue().isEmpty()) {
            return getAttribute("image").stringValue();
        } else if (!getAttribute("label").stringValue().isEmpty()) {
            return getAttribute("label").stringValue();
        } else return "";
    }
}
