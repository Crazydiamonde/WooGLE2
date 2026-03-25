package com.worldOfGoo.scene;

import com.woogleFX.assets.Asset;
import com.woogleFX.editorObjects.EditorObject;
import com.woogleFX.editorObjects.ImageUtility;
import com.woogleFX.editorObjects.objectComponents.ImageComponent;
import com.woogleFX.engine.AssetManager;

import com.woogleFX.assets.GameVersion;

import javafx.scene.image.Image;

public class button extends EditorObject {

    private Image image;


    public button(EditorObject _parent, GameVersion version) {
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

        if (AssetManager.getAsset() == null) return;

        if (!getAttribute("up").stringValue().isEmpty()) {
            image = getAttribute("up").imageValue(AssetManager.getAsset().getResources(), getVersion());
            image = ImageUtility.colorize(image, getAttribute("colorize").colorValue());
        }

    }

}
