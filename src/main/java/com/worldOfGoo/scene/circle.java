package com.worldOfGoo.scene;

import com.woogleFX.assets.Asset;
import com.woogleFX.editorObjects.EditorObject;
import com.woogleFX.editorObjects.attributes.EditorAttribute;
import com.woogleFX.editorObjects.objectComponents.CircleComponent;
import com.woogleFX.editorObjects.objectComponents.ImageComponent;
import com.woogleFX.editorObjects.ObjectUtil;
import com.woogleFX.engine.renderer.Depth;
import com.woogleFX.engine.AssetManager;
import com.woogleFX.assets.GameVersion;
import com.woogleFX.editorObjects.attributes.dataTypes.Position;
import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

public class circle extends EditorObject {

    public circle(EditorObject _parent, GameVersion version) {
        super(_parent, version);
    }

    @Override
    public void onLoaded(Asset asset) {
        super.onLoaded(asset);

        addObjectComponent(new CircleComponent(this) {
            public double getX() {

                if (getParent() instanceof compositegeom compositegeom) {

                    double compGeomX = compositegeom.getAttribute("x").doubleValue();
                    double compGeomY = -compositegeom.getAttribute("y").doubleValue();
                    double compGeomRotation = -compositegeom.getAttribute("rotation").doubleValue();

                    double x = getAttribute("x").doubleValue();
                    double y = -getAttribute("y").doubleValue();

                    Point2D position = new Point2D(x, y);
                    position = ObjectUtil.rotate(position, compGeomRotation, new Point2D(0, 0));
                    position = position.add(compGeomX, compGeomY);

                    return position.getX();

                } else {

                    return getAttribute("x").doubleValue();

                }

            }
            public void setX(double x) {

                if (getParent() instanceof compositegeom compositegeom) {

                    double compGeomX = compositegeom.getAttribute("x").doubleValue();
                    double compGeomY = -compositegeom.getAttribute("y").doubleValue();
                    double compGeomRotation = -compositegeom.getAttribute("rotation").doubleValue();

                    Point2D position = new Point2D(x, getY());
                    position = position.subtract(compGeomX, compGeomY);
                    position = ObjectUtil.rotate(position, -compGeomRotation, new Point2D(0, 0));

                    setAttribute("x", position.getX());
                    setAttribute("y", -position.getY());

                } else {

                    setAttribute("x", x);

                }

            }
            public double getY() {

                if (getParent() instanceof compositegeom compositegeom) {

                    double compGeomX = compositegeom.getAttribute("x").doubleValue();
                    double compGeomY = -compositegeom.getAttribute("y").doubleValue();
                    double compGeomRotation = -compositegeom.getAttribute("rotation").doubleValue();

                    double x = getAttribute("x").doubleValue();
                    double y = -getAttribute("y").doubleValue();

                    Point2D position = new Point2D(x, y);
                    position = ObjectUtil.rotate(position, compGeomRotation, new Point2D(0, 0));
                    position = position.add(compGeomX, compGeomY);

                    return position.getY();

                } else {

                    return -getAttribute("y").doubleValue();

                }

            }
            public void setY(double y) {

                if (getParent() instanceof compositegeom compositegeom) {

                    double compGeomX = compositegeom.getAttribute("x").doubleValue();
                    double compGeomY = -compositegeom.getAttribute("y").doubleValue();
                    double compGeomRotation = -compositegeom.getAttribute("rotation").doubleValue();

                    Point2D position = new Point2D(getX(), y);
                    position = position.subtract(compGeomX, compGeomY);
                    position = ObjectUtil.rotate(position, -compGeomRotation, new Point2D(0, 0));

                    setAttribute("x", position.getX());
                    setAttribute("y", -position.getY());

                } else {

                    setAttribute("y", -y);

                }

            }
            public double getRadius() {
                return getAttribute("radius").doubleValue();
            }
            public void setRadius(double radius) {
                setAttribute("radius", radius);
            }
            public double getEdgeSize() {
                boolean contacts = getAttribute("contacts").booleanValue();
                return (contacts || AssetManager.getVisibility("geometry") != 2) ? 4 : 0;
            }
            public boolean isEdgeOnly() {
                return false;
            }
            public double getDepth() {
                return Depth.GEOMETRY;
            }
            public Paint getBorderColor() {
                return rectangle.geometryColor(getAttribute("tag").listValue(), getParent());
            }
            public Paint getColor() {
                Color color = rectangle.geometryColor(getAttribute("tag").listValue(), getParent());
                return new Color(color.getRed(), color.getGreen(), color.getBlue(), 0.25);
            }
            public boolean isVisible() {
                return AssetManager.getVisibility("geometry") != 0;
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
            public Image getImage() {
                return getAttribute("image").imageValue(AssetManager.getAsset().getResources(), getVersion());
            }
            public boolean isGeometryImage() {
                return true;
            }
            public double getDepth() {
                return 0;
            }
            public boolean isVisible() {
                return AssetManager.getVisibility("graphics") == 1;
            }
        });

    }

    @Override
    public String getName() {
        return getAttribute("id").stringValue();
    }

}
