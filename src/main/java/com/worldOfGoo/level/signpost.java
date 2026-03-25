package com.worldOfGoo.level;

import com.woogleFX.assets.Asset;
import com.woogleFX.editorObjects.ImageUtility;
import com.woogleFX.editorObjects.EditorObject;
import com.woogleFX.editorObjects.objectComponents.ImageComponent;
import com.woogleFX.assets.AssetLoader;
import com.woogleFX.engine.AssetManager;
import com.woogleFX.assets.GameVersion;
import javafx.scene.image.Image;

import com.woogleFX.editorObjects.attributes.dataTypes.Color;

public class signpost extends EditorObject {

    private Image image;


    public signpost(EditorObject _parent, GameVersion version) {
        super(_parent, version);
    }

    @Override
    public void onLoaded(Asset asset) {
        super.onLoaded(asset);

        addObjectComponent(new ImageComponent(this) {
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
                return AssetManager.getVisibility("graphics") == 1;
            }
        });

        getAttribute("image").addChangeListener((observable, oldValue, newValue) -> updateImage());
        getAttribute("colorize").addChangeListener((observable, oldValue, newValue) -> updateImage());

        updateImage();

    }

    @Override
    public String getName() {
        return getAttribute("name").stringValue();
    }

    @Override
    public void update() {
        updateImage();
    }


    private void updateImage() {

        if (AssetManager.getAsset() == null) return;

        try {
            image = getAttribute("image").imageValue(AssetManager.getAsset().getResources(), getVersion());
            if (image == null) return;
            Color color = getAttribute("colorize").colorValue();
            image = ImageUtility.colorize(image, color);
        } catch (Exception e) {
            // TODO: make this cleaner
            if (!AssetLoader.failedResources.contains("From signpost: \"" + getAttribute("image").stringValue() + "\" (version " + getVersion() + ")")) {
                AssetLoader.failedResources.add("From signpost: \"" + getAttribute("image").stringValue() + "\" (version " + getVersion() + ")");
            }
            image = null;
        }

    }

}
