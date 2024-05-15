package com.WorldOfGoo.Scene;

import com.WooGLEFX.EditorObjects.Components.ObjectPosition;
import com.WooGLEFX.File.GlobalResourceManager;
import com.WooGLEFX.Functions.LevelManager;
import com.WooGLEFX.GUI.Alarms;
import com.WooGLEFX.Structures.*;
import com.WooGLEFX.Structures.SimpleStructures.MetaEditorAttribute;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

import java.io.FileNotFoundException;

public class Circle extends EditorObject {

    public Circle(EditorObject _parent) {
        super(_parent);
        setRealName("circle");

        addAttribute("id",               InputField.ANY)                                   .assertRequired();
        addAttribute("mass",             InputField.NUMBER)     .setDefaultValue("0");
        addAttribute("static",           InputField.FLAG)       .setDefaultValue("true");
        addAttribute("tag",              InputField.TAG);
        addAttribute("material",         InputField.MATERIAL);
        addAttribute("contacts",         InputField.FLAG);
        addAttribute("x",                InputField.NUMBER)     .setDefaultValue("0")      .assertRequired();
        addAttribute("y",                InputField.NUMBER)     .setDefaultValue("0")      .assertRequired();
        addAttribute("radius",           InputField.NUMBER)     .setDefaultValue("75")     .assertRequired();
        addAttribute("break",            InputField.NUMBER);
        addAttribute("image",            InputField.IMAGE);
        addAttribute("imagepos",         InputField.POSITION);
        addAttribute("imagerot",         InputField.NUMBER);
        addAttribute("imagescale",       InputField.POSITION)   .setDefaultValue("1,1");
        addAttribute("rotspeed",         InputField.NUMBER);
        addAttribute("nogeomcollisions", InputField.FLAG);

        addObjectPosition(new ObjectPosition(ObjectPosition.CIRCLE) {
            public double getX() {

                if (getParent() instanceof Compositegeom compositegeom) {

                    double compGeomX = compositegeom.getDouble("x");
                    double compGeomY = -compositegeom.getDouble("y");
                    double compGeomRotation = compositegeom.getDouble("rotation");

                    Point2D position = new Point2D(getDouble("x"), -getDouble("y"));
                    position = EditorObject.rotate(position, compGeomRotation, new Point2D(0, 0));
                    position = position.add(compGeomX, compGeomY);

                    return position.getX();

                } else {

                    return getDouble("x");

                }

            }
            public void setX(double x) {

                if (getParent() instanceof Compositegeom compositegeom) {

                    double compGeomX = compositegeom.getDouble("x");
                    double compGeomY = -compositegeom.getDouble("y");
                    double compGeomRotation = compositegeom.getDouble("rotation");

                    Point2D position = new Point2D(x, getY());
                    position = position.subtract(compGeomX, compGeomY);
                    position = EditorObject.rotate(position, -compGeomRotation, new Point2D(0, 0));

                    setAttribute("x", position.getX());
                    setAttribute("y", -position.getY());

                } else {

                    setAttribute("x", x);

                }

            }
            public double getY() {

                if (getParent() instanceof Compositegeom compositegeom) {

                    double compGeomX = compositegeom.getDouble("x");
                    double compGeomY = -compositegeom.getDouble("y");
                    double compGeomRotation = -compositegeom.getDouble("rotation");

                    Point2D position = new Point2D(getDouble("x"), -getDouble("y"));
                    position = EditorObject.rotate(position, compGeomRotation, new Point2D(0, 0));
                    position = position.add(compGeomX, compGeomY);

                    return position.getY();

                } else {

                    return -getDouble("y");

                }

            }
            public void setY(double y) {

                if (getParent() instanceof Compositegeom compositegeom) {

                    double compGeomX = compositegeom.getDouble("x");
                    double compGeomY = -compositegeom.getDouble("y");
                    double compGeomRotation = -compositegeom.getDouble("rotation");

                    Point2D position = new Point2D(getX(), y);
                    position = position.subtract(compGeomX, compGeomY);
                    position = EditorObject.rotate(position, -compGeomRotation, new Point2D(0, 0));

                    setAttribute("x", position.getX());
                    setAttribute("y", -position.getY());

                } else {

                    setAttribute("y", -y);

                }

            }
            public double getRadius() {
                return getDouble("radius");
            }
            public void setRadius(double radius) {
                setAttribute("radius", radius);
            }
            public Paint getBorderColor() {
                return new Color(0, 0.25, 1.0, 1.0);
            }
            public Paint getFillColor() {
                return new Color(0, 0.5, 1.0, 0.25);
            }
        });

        addObjectPosition(new ObjectPosition(ObjectPosition.IMAGE) {
            public double getX() {
                if (getAttribute("imagepos").isEmpty()) return getDouble("x");
                else return getPosition("imagepos").getX();
            }
            public void setX(double x) {
                setAttribute("pos", x + "," + -getY());
            }
            public double getY() {
                if (getAttribute("imagepos").isEmpty()) return -getDouble("y");
                else return -getPosition("imagepos").getY();
            }
            public void setY(double y) {
                setAttribute("imagepos", getX() + "," + -y);
            }
            public double getRotation() {
                return Math.toRadians(getDouble("imagerot"));
            }
            public void setRotation(double rotation) {
                setAttribute("imagerot", Math.toDegrees(rotation));
            }
            public double getWidth() {
                return getImage().getWidth() * Math.abs(getPosition("imagescale").getX());
            }
            public void setWidth(double width) {
                setAttribute("imagescale", (width / getImage().getWidth()) + "," + getPosition("imagescale").getY());
            }
            public double getHeight() {
                return getImage().getHeight() * Math.abs(getPosition("imagescale").getY());
            }
            public void setHeight(double height) {
                setAttribute("imagescale", getPosition("imagescale").getX() + "," + (height / getImage().getHeight()));
            }
        });

        setNameAttribute(getAttribute2("id"));
        setMetaAttributes(MetaEditorAttribute.parse("id,x,y,radius,Geometry<static,mass,material,tag,break,rotspeed,contacts,nogeomcollisions>?Image<image,imagepos,imagerot,imagescale>"));

    }


    @Override
    public void update() {

        if (!getAttribute("image").equals("")) {
            try {
                setImage(GlobalResourceManager.getImage(getAttribute("image"), LevelManager.getLevel().getVersion()));
            } catch (FileNotFoundException e) {
                Alarms.errorMessage(e);
            }
        }


        setChangeListener("image", (observable, oldValue, newValue) -> {
            if (!getAttribute("image").equals("")) {
                try {
                    setImage(GlobalResourceManager.getImage(getAttribute("image"), LevelManager.getLevel().getVersion()));
                } catch (FileNotFoundException e) {
                    Alarms.errorMessage(e);
                }
            }
        });

    }


    @Override
    public boolean isVisible() {
        return LevelManager.getLevel().isShowGeometry();
    }

}
