package com.WorldOfGoo.Scene;

import com.WooGLEFX.EditorObjects.ObjectPosition;
import com.WooGLEFX.Functions.LevelManager;
import com.WooGLEFX.EditorObjects.EditorObject;
import com.WooGLEFX.EditorObjects.InputField;
import com.WooGLEFX.Structures.SimpleStructures.MetaEditorAttribute;
import javafx.scene.paint.*;

public class Linearforcefield extends EditorObject {

    public Linearforcefield(EditorObject _parent) {
        super(_parent, "linearforcefield", "scene\\linearforcefield");

        addAttribute("id",               InputField.ANY)                                    .assertRequired();
        addAttribute("type",             InputField.ANY)       .setDefaultValue("gravity")  .assertRequired();
        addAttribute("center",           InputField.POSITION)  .setDefaultValue("0,0")      .assertRequired();
        addAttribute("width",            InputField.NUMBER)    .setDefaultValue("0");
        addAttribute("height",           InputField.NUMBER)    .setDefaultValue("0");
        addAttribute("force",            InputField.POSITION)  .setDefaultValue("0,-10")    .assertRequired();
        addAttribute("dampeningfactor",  InputField.NUMBER)    .setDefaultValue("0")        .assertRequired();
        addAttribute(
            "rotationaldampeningfactor", InputField.NUMBER);
        addAttribute("antigrav",         InputField.FLAG)      .setDefaultValue("false")    .assertRequired();
        addAttribute("geomonly",         InputField.FLAG)                                   .assertRequired();
        addAttribute("enabled",          InputField.FLAG)      .setDefaultValue("true")     .assertRequired();
        addAttribute("water",            InputField.FLAG)      .setDefaultValue("false");
        addAttribute("color",            InputField.COLOR);
        addAttribute("depth",            InputField.NUMBER);

        addObjectPosition(new ObjectPosition(ObjectPosition.RECTANGLE_HOLLOW) {
            public double getX() {
                return getAttribute("center").positionValue().getX();
            }
            public void setX(double x) {
                setAttribute("center", x + "," + -getY());
            }
            public double getY() {
                return -getAttribute("center").positionValue().getY();
            }
            public void setY(double y) {
                setAttribute("center", getX() + "," + -y);
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
                return 4.5;
            }
            public Paint getBorderColor() {
                return new Color(1.0, 1.0, 0, 1.0);
            }
            public Paint getFillColor() {
                return new Color(1.0, 1.0, 0, 0.05);
            }
            public boolean isVisible() {
                return LevelManager.getLevel().getVisibilitySettings().isShowForcefields();
            }
            public boolean isRotatable() {
                return false;
            }
        });

        addObjectPosition(new ObjectPosition(ObjectPosition.ANCHOR) {
            public double getX() {
                return getAttribute("center").positionValue().getX();
            }
            public void setX(double x) {
                setAttribute("center", x + "," + -getY());
            }
            public double getY() {
                return -getAttribute("center").positionValue().getY();
            }
            public void setY(double y) {
                setAttribute("center", getX() + "," + -y);
            }
            public double getAnchorX() {
                return getAttribute("force").positionValue().getX() * 20;
            }
            public double getAnchorY() {
                return -getAttribute("force").positionValue().getY() * 20;
            }
            public void setAnchor(double anchorX, double anchorY) {
                setAttribute("force", anchorX / 20 + "," + -anchorY / 20);
            }
            public double getEdgeSize() {
                return 3;
            }
            public Paint getBorderColor() {
                return new LinearGradient(0, 0, 1, 1, true, CycleMethod.NO_CYCLE,
                        new Stop(0, Color.valueOf("802000FF")), new Stop(1, Color.valueOf("FFC040FF")));
            }
            public boolean isVisible() {
                return LevelManager.getLevel().getVisibilitySettings().isShowForcefields();
            }
        });

        setMetaAttributes(MetaEditorAttribute.parse("id,center,width,height,Force Field<type,force,dampeningfactor,rotationaldampeningfactor,antigrav,geomonly,enabled>Water<water,color,depth>"));

    }


    @Override
    public String getName() {
        return getAttribute("id").stringValue();
    }

}
