package com.worldOfGoo.scene;

import com.woogleFX.assets.Asset;
import com.woogleFX.assets.wog1.level.WOG1LevelGUI;
import com.woogleFX.editorObjects.ObjectUtil;
import com.woogleFX.editorObjects.EditorObject;
import com.woogleFX.editorObjects.objectComponents.ImageComponent;
import com.woogleFX.editorObjects.objectComponents.RectangleComponent;
import com.woogleFX.engine.fx.FXStage;
import com.woogleFX.engine.renderer.Depth;
import com.woogleFX.engine.AssetManager;
import com.woogleFX.assets.GameVersion;
import com.woogleFX.editorObjects.attributes.dataTypes.Position;
import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class rectangle extends EditorObject {

    private Image image;


    public rectangle(EditorObject _parent, GameVersion version) {
        super(_parent, version);
    }

    @Override
    public void onLoaded(Asset asset) {
        super.onLoaded(asset);

        getAttribute2("tag").setOnClick(() ->
                WOG1LevelGUI.generateSetTagsDialog(
                        getAttribute2("tag")).show());

        addObjectComponent(new RectangleComponent(this) {
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
            public double getRotation() {

                if (getParent() instanceof compositegeom compositegeom) {

                    return -getAttribute("rotation").doubleValue() - compositegeom.getAttribute("rotation").doubleValue();

                } else {

                    return -getAttribute("rotation").doubleValue();

                }

            }
            public void setRotation(double rotation) {

                if (getParent() instanceof compositegeom compositegeom) {

                    setAttribute("rotation", -rotation - compositegeom.getAttribute("rotation").doubleValue());

                } else {

                    setAttribute("rotation", -rotation);

                }

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
                boolean contacts = getAttribute("contacts").booleanValue();
                return (contacts || AssetManager.getVisibility("geometry") != 2) ? 4 : 0;
            }
            public boolean isEdgeOnly() {
                return true;
            }
            public double getDepth() {
                return Depth.GEOMETRY;
            }
            public Paint getBorderColor() {
                return geometryColor(getAttribute("tag").listValue(), getParent());
            }
            public Paint getColor() {
                Color color = geometryColor(getAttribute("tag").listValue(), getParent());
                return new Color(color.getRed(), color.getGreen(), color.getBlue(), 0.25);
            }
            public boolean isVisible() {
                return AssetManager.getVisibility("geometry") != 0;
            }
        });

        addObjectComponent(new ImageComponent(this) {
            public double getX() {
                if (getAttribute("imagepos").stringValue().isEmpty()) return getAttribute("x").doubleValue();
                else return getAttribute("imagepos").positionValue().getX();
            }
            public void setX(double x) {
                if (getAttribute("imagepos").stringValue().isEmpty()) return;
                setAttribute("imagepos", x + "," + -getY());
            }
            public double getY() {
                if (getAttribute("imagepos").stringValue().isEmpty()) return -getAttribute("y").doubleValue();
                else return -getAttribute("imagepos").positionValue().getY();
            }
            public void setY(double y) {
                if (getAttribute("imagepos").stringValue().isEmpty()) return;
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


    public static Color geometryColor(String[] tags, EditorObject parent) {

        if (AssetManager.getVisibility("geometry") != 2) {
            return new Color(0.0, 0.25, 1.0, 1.0);
        }

        if (ObjectUtil.attributeContainsTag(tags, "deadly")) {
            return new Color(1.0, 0.25, 0, 1.0);
        }

        if (ObjectUtil.attributeContainsTag(tags, "mostlydeadly")) {
            return new Color(0.5, 0.25, 0.5, 1.0);
        }

        if (ObjectUtil.attributeContainsTag(tags, "detaching")) {
            return new Color(0.0, 0.5, 0.5, 1.0);
        }

        if (ObjectUtil.attributeContainsTag(tags, "ballbuster")) {
            return new Color(0.0, 1.0, 0.5, 1.0);
        }

        if (parent instanceof compositegeom compositegeom) {

            return geometryColor(compositegeom.getAttribute("tag").listValue(), null);

        }

        return new Color(0.0, 0.25, 1.0, 1.0);

    }


    @Override
    public String getName() {
        return getAttribute("id").stringValue();
    }

}
