package com.worldOfGoo.scene;

import com.woogleFX.editorObjects.objectComponents.CircleComponent;
import com.woogleFX.editorObjects.objectComponents.ImageComponent;
import com.woogleFX.engine.Depth;
import com.woogleFX.functions.LevelManager;
import com.woogleFX.editorObjects.EditorAttribute;
import com.woogleFX.editorObjects.EditorObject;
import com.woogleFX.editorObjects.InputField;
import com.woogleFX.structures.simpleStructures.MetaEditorAttribute;
import com.woogleFX.structures.simpleStructures.Position;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

import java.io.FileNotFoundException;

public class Compositegeom extends EditorObject {

    private Image image;


    public Compositegeom(EditorObject _parent) {
        super(_parent, "compositegeom", "scene\\compositegeom");

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

        addObjectComponent(new CircleComponent() {
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
                return -getAttribute("rotation").doubleValue();
            }
            public void setRotation(double rotation) {
                setAttribute("rotation", -rotation);
            }
            public double getRadius() {
                return 10;
            }
            public double getEdgeSize() {
                return 4;
            }
            public boolean isEdgeOnly() {
                return true;
            }
            public double getDepth() {
                return Depth.COMPOSITEGEOM;
            }
            public Paint getBorderColor() {
                return new Color(0, 1.0, 0, 1.0);
            }
            public Paint getColor() {
                return new Color(0, 1.0, 0, 0.25);
            }
            public boolean isVisible() {
                return LevelManager.getLevel().getVisibilitySettings().getShowGeometry() != 0;
            }
            public boolean isResizable() {
                return false;
            }
        });

        addObjectComponent(new ImageComponent() {
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
            public double getDepth() {
                return 0;
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

        String geometry = "Geometry<static,mass,material,tag,break,rotspeed,contacts,nogeomcollisions>";
        String image = "?Image<image,imagepos,imagerot,imagescale>";
        setMetaAttributes(MetaEditorAttribute.parse("id,x,y,rotation," + geometry + image));

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

        if (getAttribute("image").stringValue().isEmpty()) return;

        try {
            image = getAttribute("image").imageValue(LevelManager.getVersion());
        } catch (FileNotFoundException ignored) {
            image = null;
        }

    }


    @Override
    public String[] getPossibleChildren() {
        return new String[]{"Circle", "Rectangle"};
    }

}
