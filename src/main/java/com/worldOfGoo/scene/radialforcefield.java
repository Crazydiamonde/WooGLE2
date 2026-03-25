package com.worldOfGoo.scene;

import com.woogleFX.assets.Asset;
import com.woogleFX.editorObjects.EditorObject;
import com.woogleFX.editorObjects.objectComponents.AnchorComponent;
import com.woogleFX.editorObjects.objectComponents.CircleComponent;
import com.woogleFX.engine.renderer.Depth;
import com.woogleFX.engine.AssetManager;
import com.woogleFX.assets.GameVersion;
import javafx.scene.paint.*;

public class radialforcefield extends EditorObject {

    public radialforcefield(EditorObject _parent, GameVersion version) {
        super(_parent, version);
    }

    @Override
    public void onLoaded(Asset asset) {
        super.onLoaded(asset);

        addObjectComponent(new CircleComponent(this) {
            public double getX() {
                return getAttribute("center").positionValue().getX();
            }
            public void setX(double x) {
                setAttribute("center", x + "," + getY());
            }
            public double getY() {
                return -getAttribute("center").positionValue().getY();
            }
            public void setY(double y) {
                setAttribute("center", getX() + "," + -y);
            }
            public double getRadius() {
                return getAttribute("radius").doubleValue();
            }
            public void setRadius(double radius) {
                setAttribute("radius", radius);
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
        });

        addObjectComponent(new AnchorComponent(this) {
            public double getX() {
                return getAttribute("center").positionValue().getX();
            }
            public double getY() {
                return -getAttribute("center").positionValue().getY();
            }
            public double getAnchorX() {
                return 0;
            }
            public double getAnchorY() {
                return -getAttribute("forceatcenter").doubleValue() * 20;
            }
            public void setAnchor(double anchorX, double anchorY) {
                setAttribute("forceatcenter", -anchorY / 20);
            }
            public double getLineWidth() {
                return 3;
            }
            public double getDepth() {
                return Depth.FORCEFIELDS;
            }
            public Paint getColor() {
                return new LinearGradient(0, 0, 1, 1, true, CycleMethod.NO_CYCLE,
                        new Stop(0, Color.valueOf("802000FF")), new Stop(1, Color.valueOf("FFC040FF")));
            }
            public boolean isVisible() {
                return AssetManager.getVisibility("forcefields") == 1;
            }
        });

        addObjectComponent(new AnchorComponent(this) {
            public double getX() {
                return getAttribute("center").positionValue().getX() + getAttribute("radius").doubleValue();
            }
            public double getY() {
                return -getAttribute("center").positionValue().getY();
            }
            public double getAnchorX() {
                return 0;
            }
            public double getAnchorY() {
                return -getAttribute("forceatedge").doubleValue() * 20;
            }
            public void setAnchor(double anchorX, double anchorY) {
                setAttribute("forceatedge", -anchorY / 20);
            }
            public double getLineWidth() {
                return 3;
            }
            public double getDepth() {
                return Depth.FORCEFIELDS;
            }
            public Paint getColor() {
                return new LinearGradient(0, 0, 1, 1, true, CycleMethod.NO_CYCLE,
                        new Stop(0, Color.valueOf("802000FF")), new Stop(1, Color.valueOf("FFC040FF")));
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
