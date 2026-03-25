package com.worldOfGoo.scene;

import com.woogleFX.assets.Asset;
import com.woogleFX.editorObjects.attributes.EditorAttribute;
import com.woogleFX.editorObjects.objectComponents.CircleComponent;
import com.woogleFX.editorObjects.objectComponents.ImageComponent;
import com.woogleFX.engine.renderer.Depth;
import com.woogleFX.engine.AssetManager;
import com.woogleFX.editorObjects.EditorObject;
import com.woogleFX.assets.GameVersion;
import com.woogleFX.editorObjects.attributes.dataTypes.Position;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

public class compositegeom extends EditorObject {

    public compositegeom(EditorObject _parent, GameVersion version) {
        super(_parent, version);
    }

    @Override
    public void onLoaded(Asset asset) {
        super.onLoaded(asset);

        addObjectComponent(new CircleComponent(this) {
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
                return -getAttribute("rotation").doubleValue();
            }
            public void setRotation(double rotation) {
                setAttribute("rotation", -rotation);
            }
            public double getRadius() {
                return 10;
            }
            public double getEdgeSize() {
                return 4;
            }
            public boolean isEdgeOnly() {
                return false;
            }
            public double getDepth() {
                return Depth.COMPOSITEGEOM;
            }
            public Paint getBorderColor() {
                return new Color(0, 1.0, 0, 1.0);
            }
            public Paint getColor() {
                return new Color(0, 1.0, 0, 0.25);
            }
            public boolean isVisible() {
                return AssetManager.getVisibility("geometry") != 0;
            }
            public boolean isResizable() {
                return false;
            }
        });

        addObjectComponent(new ImageComponent(this) {
            public double getX() {
                EditorAttribute imagepos = getAttribute("imagepos");
                if (imagepos.stringValue().isEmpty()) return getAttribute("x").doubleValue();
                else return imagepos.positionValue().getX();
            }
            public void setX(double x) {
                setAttribute("imagepos", x + "," + -getY());
            }
            public double getY() {
                EditorAttribute imagepos = getAttribute("imagepos");
                if (imagepos.stringValue().isEmpty()) return -getAttribute("y").doubleValue();
                else return -imagepos.positionValue().getY();
            }
            public void setY(double y) {
                setAttribute("imagepos", getX() + "," + -y);
            }
            public double getRotation() {
                return -getAttribute("imagerot").doubleValue();
            }
            public void setRotation(double rotation) {
                setAttribute("imagerot", -rotation);
            }
            public double getScaleX() {
                return getAttribute("imagescale").positionValue().getX();
            }
            public void setScaleX(double scaleX) {
                Position scale = getAttribute("imagescale").positionValue();
                setAttribute("imagescale", scaleX + "," + scale.getY());
            }
            public double getScaleY() {
                return getAttribute("imagescale").positionValue().getY();
            }
            public void setScaleY(double scaleY) {
                Position scale = getAttribute("imagescale").positionValue();
                setAttribute("imagescale", scale.getX() + "," + scaleY);
            }
            public double getDepth() {
                return 0;
            }
            public Image getImage() {
                return getAttribute("image").imageValue(AssetManager.getAsset().getResources(), getVersion());
            }
            public boolean isGeometryImage() {
                return true;
            }
            public boolean isVisible() {
                return AssetManager.getVisibility("graphics") != 0;
            }
            public boolean isResizable() {
                return false;
            }
            public boolean isRotatable() {
                return false;
            }
        });

    }

    @Override
    public String getName() {
        return getAttribute("id").stringValue();
    }

    @Override
    @SuppressWarnings("unchecked")
    public Class<? extends EditorObject>[] getPossibleChildren() {
        return new Class[]{ circle.class, rectangle.class };
    }

}
