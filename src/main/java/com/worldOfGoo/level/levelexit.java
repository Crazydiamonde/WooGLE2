package com.worldOfGoo.level;

import com.woogleFX.assets.Asset;
import com.woogleFX.editorObjects.EditorObject;
import com.woogleFX.editorObjects.objectComponents.CircleComponent;
import com.woogleFX.engine.renderer.Depth;
import com.woogleFX.engine.AssetManager;
import com.woogleFX.assets.GameVersion;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

public class levelexit extends EditorObject {

    public levelexit(EditorObject _parent, GameVersion version) {
        super(_parent, version);
    }

    @Override
    public void onLoaded(Asset asset) {
        super.onLoaded(asset);

        addObjectComponent(new CircleComponent(this) {
            public double getX() {
                return getAttribute("pos").positionValue().getX();
            }
            public void setX(double x) {
                setAttribute("pos", x + "," + -getY());
            }
            public double getY() {
                return -getAttribute("pos").positionValue().getY();
            }
            public void setY(double y) {
                setAttribute("pos", getX() + "," + -y);
            }
            public double getRadius() {
                return getAttribute("radius").doubleValue();
            }
            public void setRadius(double radius) {
                setAttribute("radius", radius);
            }
            public double getEdgeSize() {
                return 4;
            }
            public boolean isEdgeOnly() {
                return true;
            }
            public double getDepth() {
                return Depth.GEOMETRY;
            }
            public Paint getBorderColor() {
                return new Color(1.0, 0, 1.0, 1.0);
            }
            public Paint getColor() {
                return new Color(1.0, 0, 1.0, 0.1);
            }
            public boolean isVisible() {
                return AssetManager.getVisibility("geometry") != 0;
            }
        });

    }

    @Override
    public String getName() {
        return getAttribute("id").stringValue();
    }

}
