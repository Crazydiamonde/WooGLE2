package com.worldOfGoo.scene;

import com.woogleFX.assets.Asset;
import com.woogleFX.editorObjects.EditorObject;
import com.woogleFX.editorObjects.objectComponents.AnchorComponent;
import com.woogleFX.editorObjects.objectComponents.LineComponent;
import com.woogleFX.editorObjects.ObjectUtil;
import com.woogleFX.engine.renderer.Depth;
import com.woogleFX.engine.AssetManager;
import com.woogleFX.assets.GameVersion;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

public class line extends EditorObject {

    public line(EditorObject _parent, GameVersion version) {
        super(_parent, version);
    }

    @Override
    public void onLoaded(Asset asset) {
        super.onLoaded(asset);

        addObjectComponent(new LineComponent(this) {
            public double getX() {
                return getAttribute("anchor").positionValue().getX();
            }
            public void setX(double x) {
                setAttribute("anchor", x + "," + -getY());
            }
            public double getY() {
                return -getAttribute("anchor").positionValue().getY();
            }
            public void setY(double y) {
                setAttribute("anchor", getX() + "," + -y);
            }
            public double getRotation() {
                return Math.atan2(-getAttribute("normal").positionValue().getX(), -getAttribute("normal").positionValue().getY());
            }
            public double getLineWidth() {
                return 3;
            }
            public Paint getColor() {
                if (AssetManager.getVisibility("geometry") != 2) return new Color(0.0, 0.25, 1.0, 1.0);

                if (ObjectUtil.attributeContainsTag(getAttribute("tag").listValue(), "deadly")) {
                    return new Color(1.0, 0.25, 0, 1.0);
                }

                if (ObjectUtil.attributeContainsTag(getAttribute("tag").listValue(), "mostlydeadly")) {
                    return new Color(0.5, 0.25, 0.5, 1.0);
                }

                if (ObjectUtil.attributeContainsTag(getAttribute("tag").listValue(), "detaching")) {
                    return new Color(0.0, 0.5, 0.5, 1.0);
                }

                return new Color(0.0, 0.25, 1.0, 1.0);

            }
            public double getDepth() {
                return Depth.GEOMETRY;
            }
            public boolean isVisible() {
                return AssetManager.getVisibility("geometry") != 0;
            }
        });

        addObjectComponent(new AnchorComponent(this) {
            public double getX() {
                return getAttribute("anchor").positionValue().getX();
            }
            public void setX(double x) {
                setAttribute("anchor", x + "," + -getY());
            }
            public double getY() {
                return -getAttribute("anchor").positionValue().getY();
            }
            public void setY(double y) {
                setAttribute("anchor", getX() + "," + -y);
            }
            public double getAnchorX() {
                return getAttribute("normal").positionValue().getX() * 20;
            }
            public double getAnchorY() {
                return -getAttribute("normal").positionValue().getY() * 20;
            }
            public void setAnchor(double anchorX, double anchorY) {
                double magnitude = Math.hypot(anchorX, anchorY);
                setAttribute("normal", anchorX / magnitude + "," + -anchorY / magnitude);
            }
            public double getLineWidth() {
                return 3;
            }
            public double getDepth() {
                return Depth.GEOMETRY;
            }
            public Paint getColor() {
                return rectangle.geometryColor(getAttribute("tag").listValue(), getParent());
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
