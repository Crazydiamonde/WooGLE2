package com.worldOfGoo.scene;

import com.woogleFX.assets.Asset;
import com.woogleFX.editorObjects.EditorObject;
import com.woogleFX.editorObjects.objectComponents.AnchorComponent;
import com.woogleFX.editorObjects.objectComponents.RectangleComponent;
import com.woogleFX.engine.renderer.Depth;
import com.woogleFX.engine.AssetManager;
import com.woogleFX.assets.GameVersion;
import javafx.scene.paint.*;

public class linearforcefield extends EditorObject {

    public linearforcefield(EditorObject _parent, GameVersion version) {
        super(_parent, version);
    }

    @Override
    public void onLoaded(Asset asset) {
        super.onLoaded(asset);

        addObjectComponent(new RectangleComponent(this) {
            public double getX() {
                return getAttribute("center").positionValue().getX();
            }
            public void setX(double x) {
                setAttribute("center", x + "," + -getY());
            }
            public double getY() {
                return -getAttribute("center").positionValue().getY();
            }
            public void setY(double y) {
                setAttribute("center", getX() + "," + -y);
            }
            public double getWidth() {
                return Math.abs(getAttribute("width").doubleValue());
            }
            public void setWidth(double width) {
                setAttribute("width", width);
            }
            public double getHeight() {
                return Math.abs(getAttribute("height").doubleValue());
            }
            public void setHeight(double height) {
                setAttribute("height", height);
            }
            public double getEdgeSize() {
                return 4.5;
            }
            public boolean isEdgeOnly() {
                return true;
            }
            public double getDepth() {
                return Depth.FORCEFIELDS;
            }
            public Paint getBorderColor() {
                return new Color(1.0, 1.0, 0, 1.0);
            }
            public Paint getColor() {
                return new Color(1.0, 1.0, 0, 0.05);
            }
            public boolean isVisible() {
                return AssetManager.getVisibility("forcefields") == 1;
            }
            public boolean isRotatable() {
                return false;
            }
        });

        addObjectComponent(new AnchorComponent(this) {
            public double getX() {
                return getAttribute("center").positionValue().getX();
            }
            public void setX(double x) {
                setAttribute("center", x + "," + -getY());
            }
            public double getY() {
                return -getAttribute("center").positionValue().getY();
            }
            public void setY(double y) {
                setAttribute("center", getX() + "," + -y);
            }
            public double getAnchorX() {
                return getAttribute("force").positionValue().getX() * 20;
            }
            public double getAnchorY() {
                return -getAttribute("force").positionValue().getY() * 20;
            }
            public void setAnchor(double anchorX, double anchorY) {
                setAttribute("force", anchorX / 20 + "," + -anchorY / 20);
            }
            public double getLineWidth() {
                return 3;
            }
            public Paint getColor() {
                return new LinearGradient(0, 0, 1, 1, true, CycleMethod.NO_CYCLE,
                        new Stop(0, Color.valueOf("802000FF")), new Stop(1, Color.valueOf("FFC040FF")));
            }
            public double getDepth() {
                return Depth.FORCEFIELDS;
            }
            public boolean isVisible() {
                return AssetManager.getVisibility("forcefields") == 1;
            }
        });

    }

    @Override
    public String getName() {
        return getAttribute("id").stringValue();
    }

}
