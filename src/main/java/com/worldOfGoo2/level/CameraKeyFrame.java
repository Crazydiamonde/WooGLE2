package com.worldOfGoo2.level;

import com.woogleFX.editorObjects.EditorObject;
import com.woogleFX.assets.Asset;
import com.woogleFX.editorObjects.attributes.AttributeAdapter;
import com.woogleFX.editorObjects.attributes.MetaEditorAttribute;
import com.woogleFX.editorObjects.objectComponents.RectangleComponent;
import com.woogleFX.engine.AssetManager;
import com.woogleFX.engine.renderer.Depth;
import com.woogleFX.assets.GameVersion;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

public class CameraKeyFrame extends EditorObject {

    public CameraKeyFrame(EditorObject parent, GameVersion version) {
        super(parent, version);

        addAttributeAdapter("position", AttributeAdapter.pointAttributeAdapter(this, "position", "position"));

        addObjectComponent(new RectangleComponent(this) {
            public double getX() {
                return getAttribute("position").positionValue().getX();
            }
            public void setX(double x) {
                setAttribute("position", x + "," + -getY());
            }
            public double getY() {
                return -getAttribute("position").positionValue().getY();
            }
            public void setY(double y) {
                setAttribute("position", getX() + "," + -y);
            }
            public double getWidth() {

                String aspect = "widescreen"; // getParent().getAttribute("aspect").stringValue();
                double zoom = getAttribute("zoom").doubleValue();

                double screenWidth = aspect.equals("widescreen") ? 10.07 : 800;
                return screenWidth / zoom;

            }
            public void setWidth(double width) {

                String aspect = "widescreen"; // getParent().getAttribute("aspect").stringValue();

                double screenWidth = aspect.equals("widescreen") ? 10.07 : 800;
                setAttribute("zoom", screenWidth / width);

            }
            public double getHeight() {

                double zoom = getAttribute("zoom").doubleValue();

                return 5.25 / zoom;

            }
            public void setHeight(double height) {
                setAttribute("zoom", 5.25 / height);
            }
            public double getEdgeSize() {
                return 0.1;
            }
            public boolean isEdgeOnly() {
                return true;
            }
            public double getDepth() {
                return Depth.CAMERA;
            }
            public Paint getBorderColor() {
                return new Color(0.25, 0.8, 0.8, 1.0);
            }
            public Paint getColor() {
                return new Color(0.25, 0.8, 0.8, 0.1);
            }
            public boolean isVisible() {
                return AssetManager.getVisibility("cameras") == 1;
            }
        });

        setMetaAttributes(MetaEditorAttribute.parse("position,zoom,timeScaleFactor,pauseTimeFactor,easeInSpeedFactor,easeOutSpeedFactor,timeScaleFactor2,"));

    }


    @Override
    public void onLoaded(Asset asset) {
        super.onLoaded(asset);

        EditorObject pos = getChildren("position").get(0);
        setAttribute2("position", pos.getAttribute("x").stringValue() + "," + pos.getAttribute("y").stringValue());
        pos.getAttribute("x").addChangeListener((observable, oldValue, newValue) ->
                setAttribute2("position", newValue + "," + getAttribute2("position").positionValue().getY()));
        pos.getAttribute("y").addChangeListener((observable, oldValue, newValue) ->
                setAttribute2("position", getAttribute2("position").positionValue().getX() + "," + newValue));

    }

}
