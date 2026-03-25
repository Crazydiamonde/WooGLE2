package com.worldOfGoo.scene;

import com.woogleFX.assets.Asset;
import com.woogleFX.editorObjects.objectComponents.RectangleComponent;
import com.woogleFX.engine.renderer.Depth;
import com.woogleFX.engine.AssetManager;
import com.woogleFX.editorObjects.EditorObject;
import com.woogleFX.assets.GameVersion;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

public class scene extends EditorObject {

    public scene(EditorObject _parent, GameVersion version) {
        super(_parent, version);
    }

    @Override
    public void onLoaded(Asset asset) {
        super.onLoaded(asset);

        addObjectComponent(new RectangleComponent(this) {
            public double getX() {
                double minx = getAttribute("minx").doubleValue();
                double maxx = getAttribute("maxx").doubleValue();
                return (minx + maxx) / 2;
            }
            public void setX(double x) {
                double width = getWidth();
                setAttribute("minx", x - width / 2);
                setAttribute("maxx", x + width / 2);
            }
            public double getY() {
                double miny = -getAttribute("miny").doubleValue();
                double maxy = -getAttribute("maxy").doubleValue();
                return (miny + maxy) / 2;
            }
            public void setY(double y) {
                double height = getHeight();
                setAttribute("miny", -y + height / 2);
                setAttribute("maxy", -y - height / 2);
            }
            public double getWidth() {
                double minx = getAttribute("minx").doubleValue();
                double maxx = getAttribute("maxx").doubleValue();
                return Math.abs(maxx - minx);
            }
            public void setWidth(double width) {
                double x = getX();
                setAttribute("minx", x - width / 2);
                setAttribute("maxx", x + width / 2);
            }
            public double getHeight() {
                double miny = -getAttribute("miny").doubleValue();
                double maxy = -getAttribute("maxy").doubleValue();
                return Math.abs(maxy - miny);
            }
            public void setHeight(double height) {
                double y = getY();
                setAttribute("miny", -y + height / 2);
                setAttribute("maxy", -y - height / 2);
            }
            public double getDepth() {
                return Depth.SCENE;
            }
            public double getEdgeSize() {
                return 1.0;
            }
            public Paint getBorderColor() {
                return new Color(0.0, 0.0,0.0, 1.0);
            }
            public boolean isEdgeOnly() {
                return true;
            }
            public Paint getColor() {
                return new Color(0.0, 0.0, 0.0, 0.0);
            }
            public boolean isVisible() {
                return AssetManager.getVisibility("scene") != 0;
            }
            public boolean isRotatable() {
                return false;
            }
        });

        addObjectComponent(new RectangleComponent(this) {
            public double getX() {
                double minx = getAttribute("minx").doubleValue();
                double maxx = getAttribute("maxx").doubleValue();
                return (minx + maxx) / 2;
            }
            public void setX(double x) {
                double width = getWidth();
                setAttribute("minx", x - width / 2);
                setAttribute("maxx", x + width / 2);
            }
            public double getY() {
                double miny = -getAttribute("miny").doubleValue();
                double maxy = -getAttribute("maxy").doubleValue();
                return (miny + maxy) / 2;
            }
            public void setY(double y) {
                double height = getHeight();
                setAttribute("miny", -(y + height / 2));
                setAttribute("maxy", -(y - height / 2));
            }
            public double getWidth() {
                double minx = getAttribute("minx").doubleValue();
                double maxx = getAttribute("maxx").doubleValue();
                return Math.abs(maxx - minx);
            }
            public double getHeight() {
                double miny = -getAttribute("miny").doubleValue();
                double maxy = -getAttribute("maxy").doubleValue();
                return Math.abs(maxy - miny);
            }
            public double getDepth() {
                return Depth.SCENE_BG;
            }
            public double getEdgeSize() {
                return 0.0;
            }
            public Paint getBorderColor() {
                return new Color(0.0, 0.0,0.0, 0.0);
            }
            public boolean isEdgeOnly() {
                return false;
            }
            public Paint getColor() {
                if (AssetManager.getVisibility("scene") == 2) {
                    com.woogleFX.editorObjects.attributes.dataTypes.Color backgroundColor = getAttribute("backgroundcolor").colorValue();
                    double r = backgroundColor.getR() / 255.0;
                    double g = backgroundColor.getG() / 255.0;
                    double b = backgroundColor.getB() / 255.0;
                    return new Color(r, g, b, 1.0);
                }
                return new Color(0.0, 0.0, 0.0, 0.0);
            }
            public boolean isVisible() {
                return AssetManager.getVisibility("scene") == 2;
            }
            public boolean isSelectable() {
                return AssetManager.getVisibility("scene") == 2;
            }
            public boolean isDraggable() {
                return AssetManager.getVisibility("scene") == 2;
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
    @SuppressWarnings("unchecked")
    public Class<? extends EditorObject>[] getPossibleChildren() {
        return new Class[] {
                SceneLayer.class,
                button.class,
                buttongroup.class,
                circle.class,
                compositegeom.class,
                hinge.class,
                label.class,
                line.class,
                linearforcefield.class,
                motor.class,
                particles.class,
                radialforcefield.class,
                rectangle.class,
                slider.class
        };
    }

}
