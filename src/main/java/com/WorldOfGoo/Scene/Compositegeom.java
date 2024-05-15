package com.WorldOfGoo.Scene;

import java.io.FileNotFoundException;

import com.WooGLEFX.EditorObjects.Components.ObjectPosition;
import com.WooGLEFX.File.GlobalResourceManager;
import com.WooGLEFX.Functions.LevelManager;
import com.WooGLEFX.GUI.Alarms;
import com.WooGLEFX.Structures.EditorObject;
import com.WooGLEFX.Structures.InputField;
import com.WooGLEFX.Structures.SimpleStructures.MetaEditorAttribute;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

public class Compositegeom extends EditorObject {

    private Image image;


    public Compositegeom(EditorObject _parent) {
        super(_parent);
        setRealName("compositegeom");

        addAttribute("id",               InputField.ANY)                             .assertRequired();
        addAttribute("x",                InputField.NUMBER)  .setDefaultValue("0")   .assertRequired();
        addAttribute("y",                InputField.NUMBER)  .setDefaultValue("0")   .assertRequired();
        addAttribute("rotation",         InputField.NUMBER)  .setDefaultValue("0")   .assertRequired();
        addAttribute("static",           InputField.FLAG)    .setDefaultValue("true").assertRequired();
        addAttribute("mass",             InputField.NUMBER);
        addAttribute("tag",              InputField.TAG);
        addAttribute("material",         InputField.MATERIAL).setDefaultValue("rock").assertRequired();
        addAttribute("break",            InputField.NUMBER);
        addAttribute("image",            InputField.IMAGE);
        addAttribute("imagepos",         InputField.POSITION);
        addAttribute("imagerot",         InputField.NUMBER)  .setDefaultValue("0");
        addAttribute("imagescale",       InputField.POSITION).setDefaultValue("1.1");
        addAttribute("rotspeed",         InputField.NUMBER)  .setDefaultValue("0");
        addAttribute("contacts",         InputField.FLAG);
        addAttribute("nogeomcollisions", InputField.FLAG);

        addObjectPosition(new ObjectPosition(ObjectPosition.CIRCLE_HOLLOW) {
            public double getX() {
                return getDouble("x");
            }
            public void setX(double x) {
                setAttribute("x", x);
            }
            public double getY() {
                return -getDouble("y");
            }
            public void setY(double y) {
                setAttribute("y", -y);
            }
            public double getRotation() {
                return getDouble("rotation");
            }
            public void setRotation(double rotation) {
                setAttribute("rotation", rotation);
            }
            public double getRadius() {
                return 10;
            }
            public double getEdgeSize() {
                return 2.5;
            }
            public Paint getBorderColor() {
                return new Color(0, 1.0, 0, 1.0);
            }
            public Paint getFillColor() {
                return new Color(0, 1.0, 0, 0.25);
            }
            public boolean isVisible() {
                return LevelManager.getLevel().isShowGeometry();
            }
        });

        addObjectPosition(new ObjectPosition(ObjectPosition.IMAGE) {
            public double getX() {
                if (getAttribute("imagepos").isEmpty()) return getDouble("x");
                else return getPosition("imagepos").getX();
            }
            public void setX(double x) {
                setAttribute("imagepos", x + "," + -getY());
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
            public Image getImage() {
                return image;
            }
            public boolean isVisible() {
                return LevelManager.getLevel().isShowGeometry();
            }
            public boolean isResizable() {
                return false;
            }
            public boolean isRotatable() {
                return false;
            }
        });

        setNameAttribute(getAttribute2("id"));
        setMetaAttributes(MetaEditorAttribute.parse("id,x,y,rotation,Geometry<static,mass,material,tag,break,rotspeed,contacts,nogeomcollisions>?Image<image,imagepos,imagerot,imagescale>"));

    }


    @Override
    public void update() {
        if (!getAttribute("image").equals("")) {
            try {
                image = GlobalResourceManager.getImage(getAttribute("image"), LevelManager.getLevel().getVersion());
            } catch (FileNotFoundException e) {
                Alarms.errorMessage(e);
            }
        }

        setChangeListener("image", (observable, oldValue, newValue) -> {
            if (!getAttribute("image").equals("")) {
                try {
                    image = GlobalResourceManager.getImage(getAttribute("image"), LevelManager.getLevel().getVersion());
                } catch (FileNotFoundException e) {
                    Alarms.errorMessage(e);
                }
            }
        });
    }

    @Override
    public String[] getPossibleChildren() {
        return new String[]{ "Circle", "Rectangle" };
    }

}
