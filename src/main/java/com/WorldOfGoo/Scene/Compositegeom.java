package com.WorldOfGoo.Scene;

import com.WooGLEFX.EditorObjects.Components.ObjectPosition;
import com.WooGLEFX.Functions.LevelManager;
import com.WooGLEFX.Structures.EditorObject;
import com.WooGLEFX.Structures.InputField;
import com.WooGLEFX.Structures.SimpleStructures.MetaEditorAttribute;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

import java.io.FileNotFoundException;

public class Compositegeom extends EditorObject {

    private Image image;


    public Compositegeom(EditorObject _parent) {
        super(_parent, "compositegeom");

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
        addAttribute("imagescale",       InputField.POSITION).setDefaultValue("1,1");
        addAttribute("rotspeed",         InputField.NUMBER)  .setDefaultValue("0");
        addAttribute("contacts",         InputField.FLAG);
        addAttribute("nogeomcollisions", InputField.FLAG);

        addObjectPosition(new ObjectPosition(ObjectPosition.CIRCLE_HOLLOW) {
            public double getX() {
                return getAttribute("x").doubleValue();
            }
            public void setX(double x) {
                setAttribute("x", x);
            }
            public double getY() {
                return -getAttribute("y").doubleValue();
            }
            public void setY(double y) {
                setAttribute("y", -y);
            }
            public double getRotation() {
                return getAttribute("rotation").doubleValue();
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
                return LevelManager.getLevel().getVisibilitySettings().getShowGeometry() != 0;
            }
        });

        addObjectPosition(new ObjectPosition(ObjectPosition.IMAGE) {
            public double getX() {
                if (getAttribute("imagepos").stringValue().isEmpty()) return getAttribute("x").doubleValue();
                else return getAttribute("imagepos").positionValue().getX();
            }
            public void setX(double x) {
                setAttribute("imagepos", x + "," + -getY());
            }
            public double getY() {
                if (getAttribute("imagepos").stringValue().isEmpty()) return -getAttribute("y").doubleValue();
                else return -getAttribute("imagepos").positionValue().getY();
            }
            public void setY(double y) {
                setAttribute("imagepos", getX() + "," + -y);
            }
            public double getRotation() {
                return Math.toRadians(getAttribute("imagerot").doubleValue());
            }
            public void setRotation(double rotation) {
                setAttribute("imagerot", Math.toDegrees(rotation));
            }
            public double getWidth() {
                return getImage().getWidth() * Math.abs(getAttribute("imagescale").positionValue().getX());
            }
            public void setWidth(double width) {
                setAttribute("imagescale", (width / getImage().getWidth()) + "," + getAttribute("imagescale").positionValue().getY());
            }
            public double getHeight() {
                return getImage().getHeight() * Math.abs(getAttribute("imagescale").positionValue().getY());
            }
            public void setHeight(double height) {
                setAttribute("imagescale", getAttribute("imagescale").positionValue().getX() + "," + (height / getImage().getHeight()));
            }
            public Image getImage() {
                return image;
            }
            public boolean isVisible() {
                return LevelManager.getLevel().getVisibilitySettings().getShowGeometry() != 0;
            }
            public boolean isResizable() {
                return false;
            }
            public boolean isRotatable() {
                return false;
            }
        });

        setMetaAttributes(MetaEditorAttribute.parse("id,x,y,rotation,Geometry<static,mass,material,tag,break,rotspeed,contacts,nogeomcollisions>?Image<image,imagepos,imagerot,imagescale>"));

        getAttribute("image").addChangeListener((observable, oldValue, newValue) -> updateImage());

    }


    @Override
    public String getName() {
        return getAttribute("id").stringValue();
    }


    @Override
    public void update() {

        updateImage();

    }


    private void updateImage() {

        try {
            if (!getAttribute("image").stringValue().isEmpty()) {
                image = getAttribute("image").imageValue(LevelManager.getVersion());
            }
        } catch (FileNotFoundException ignored) {

        }

    }


    @Override
    public String[] getPossibleChildren() {
        return new String[]{ "Circle", "Rectangle" };
    }

}
