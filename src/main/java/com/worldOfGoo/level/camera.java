package com.worldOfGoo.level;

import com.woogleFX.assets.Asset;
import com.woogleFX.editorObjects.EditorObject;
import com.woogleFX.editorObjects.objectComponents.RectangleComponent;
import com.woogleFX.engine.renderer.Depth;
import com.woogleFX.engine.AssetManager;
import com.woogleFX.assets.GameVersion;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

public class camera extends EditorObject {

    public camera(EditorObject _parent, GameVersion version) {
        super(_parent, version);
    }

    @Override
    public void onLoaded(Asset asset) {
        super.onLoaded(asset);

        addObjectComponent(new RectangleComponent(this) {
            public double getX() {
                return getAttribute("endpos").positionValue().getX();
            }
            public void setX(double x) {
                setAttribute("endpos", x + "," + -getY());
            }
            public double getY() {
                return -getAttribute("endpos").positionValue().getY();
            }
            public void setY(double y) {
                setAttribute("endpos", getX() + "," + -y);
            }
            public double getWidth() {

                String aspect = getAttribute("aspect").stringValue();
                double endzoom = getAttribute("endzoom").doubleValue();

                double screenWidth = (aspect.equals("widescreen")) ? 1007 : 800;
                return screenWidth / endzoom;

            }
            public void setWidth(double width) {

                String aspect = getAttribute("aspect").stringValue();

                double screenWidth = (aspect.equals("widescreen")) ? 1007 : 800;
                setAttribute("endzoom", screenWidth / width);

            }
            public double getHeight() {

                double endzoom = getAttribute("endzoom").doubleValue();

                return 525 / endzoom;

            }
            public void setHeight(double height) {
                setAttribute("endzoom", 525 / height);
            }
            public double getEdgeSize() {
                return 4.0;
            }
            public double getHorizontalEdgeSize() {
                return getHeight() / 8.0;
            }
            public Paint getBorderColor() {
                return new Color(0.0, 0.5, 0.5, 1.0);
            }
            public Paint getColor() {
                return new Color(0.0, 0.5, 0.5, 0.1);
            }
            public boolean isEdgeOnly() {
                return true;
            }
            public double getDepth() {
                return Depth.CAMERA;
            }
            public boolean isVisible() {
                return AssetManager.getVisibility("cameras") == 1;
            }
            public boolean isRotatable() {
                return false;
            }
        });
    }

    @Override
    public String getName() {
        return getAttribute("aspect").stringValue();
    }

    @Override
    @SuppressWarnings("unchecked")
    public Class<? extends EditorObject>[] getPossibleChildren() {
        return new Class[]{ poi.class };
    }

}
