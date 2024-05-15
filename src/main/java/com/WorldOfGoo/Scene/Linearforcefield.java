package com.WorldOfGoo.Scene;

import com.WooGLEFX.EditorObjects.Components.ObjectPosition;
import com.WooGLEFX.Engine.Renderer;
import com.WooGLEFX.Functions.LevelManager;
import com.WooGLEFX.Structures.EditorObject;
import com.WooGLEFX.Structures.InputField;
import com.WooGLEFX.Structures.SimpleStructures.MetaEditorAttribute;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Paint;

public class Linearforcefield extends EditorObject {

    public Linearforcefield(EditorObject _parent) {
        super(_parent);
        setRealName("linearforcefield");

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
                return getPosition("center").getX();
            }
            public void setX(double x) {
                setAttribute("center", x + "," + -getY());
            }
            public double getY() {
                return -getPosition("center").getY();
            }
            public void setY(double y) {
                setAttribute("center", getX() + "," + -y);
            }
            public double getWidth() {
                return Math.abs(getDouble("width"));
            }
            public void setWidth(double width) {
                setAttribute("width", width);
            }
            public double getHeight() {
                return Math.abs(getDouble("height"));
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
                return new Color(1.0, 1.0, 0, 0.1);
            }
            public boolean isVisible() {
                return LevelManager.getLevel().isShowForcefields();
            }
            public boolean isRotatable() {
                return false;
            }
        });

        addObjectPosition(new ObjectPosition(ObjectPosition.ANCHOR) {
            public double getX() {
                return getPosition("center").getX();
            }
            public void setX(double x) {
                setAttribute("center", x + "," + -getY());
            }
            public double getY() {
                return -getPosition("center").getY();
            }
            public void setY(double y) {
                setAttribute("center", getX() + "," + -y);
            }
            public double getAnchorX() {
                return getPosition("force").getX() * 20;
            }
            public double getAnchorY() {
                return -getPosition("force").getY() * 20;
            }
            public void setAnchor(double anchorX, double anchorY) {
                setAttribute("force", anchorX / 20 + "," + -anchorY / 20);
            }
            public double getEdgeSize() {
                return 3;
            }
            public Paint getBorderColor() {
                return new LinearGradient(0, 0, 1, 1, true, CycleMethod.NO_CYCLE, Renderer.stops);
            }
            public boolean isVisible() {
                return LevelManager.getLevel().isShowForcefields();
            }
        });

        setNameAttribute(getAttribute2("id"));
        setMetaAttributes(MetaEditorAttribute.parse("id,center,width,height,Force Field<type,force,dampeningfactor,rotationaldampeningfactor,antigrav,geomonly,enabled>Water<water,color,depth>"));

    }

}
