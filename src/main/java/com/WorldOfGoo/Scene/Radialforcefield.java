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

public class Radialforcefield extends EditorObject {

    public Radialforcefield(EditorObject _parent) {
        super(_parent);
        setRealName("radialforcefield");

        addAttribute("id",               InputField.ANY)                                    .assertRequired();
        addAttribute("type",             InputField.ANY)       .setDefaultValue("gravity")  .assertRequired();
        addAttribute("center",           InputField.POSITION)  .setDefaultValue("0,0")      .assertRequired();
        addAttribute("radius",           InputField.NUMBER)    .setDefaultValue("100")      .assertRequired();
        addAttribute("forceatcenter",    InputField.NUMBER)    .setDefaultValue("0")        .assertRequired();
        addAttribute("forceatedge",      InputField.NUMBER)    .setDefaultValue("0")        .assertRequired();
        addAttribute("dampeningfactor",  InputField.NUMBER)    .setDefaultValue("0")        .assertRequired();
        addAttribute(
            "rotationaldampeningfactor", InputField.NUMBER);
        addAttribute("antigrav",         InputField.FLAG)      .setDefaultValue("false")    .assertRequired();
        addAttribute("geomonly",         InputField.FLAG)                                   .assertRequired();
        addAttribute("enabled",          InputField.FLAG)      .setDefaultValue("true")     .assertRequired();

        addObjectPosition(new ObjectPosition(ObjectPosition.CIRCLE_HOLLOW) {
            public double getX() {
                return getPosition("center").getX();
            }
            public void setX(double x) {
                setAttribute("center", x + "," + getY());
            }
            public double getY() {
                return -getPosition("center").getY();
            }
            public void setY(double y) {
                setAttribute("center", getX() + "," + -y);
            }
            public double getRadius() {
                return getDouble("radius");
            }
            public void setRadius(double radius) {
                setAttribute("radius", radius);
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
        });

        addObjectPosition(new ObjectPosition(ObjectPosition.ANCHOR) {
            public double getX() {
                return getPosition("center").getX();
            }
            public double getY() {
                return -getPosition("center").getY();
            }
            public double getAnchorY() {
                return -getDouble("forceatcenter") * 20;
            }
            public void setAnchor(double anchorX, double anchorY) {
                setAttribute("forceatcenter", anchorX / 20 + "," + -anchorY / 20);
            }
            public Paint getBorderColor() {
                return new LinearGradient(0, 0, 1, 1, true, CycleMethod.NO_CYCLE, Renderer.stops);
            }
            public boolean isVisible() {
                return LevelManager.getLevel().isShowForcefields();
            }
        });

        addObjectPosition(new ObjectPosition(ObjectPosition.ANCHOR) {
            public double getX() {
                return getPosition("center").getX() + getDouble("radius");
            }
            public double getY() {
                return -getPosition("center").getY();
            }
            public double getAnchorY() {
                return -getDouble("forceatedge") * 20;
            }
            public void setAnchor(double anchorX, double anchorY) {
                setAttribute("forceatedge", anchorX / 20 + "," + -anchorY / 20);
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
        setMetaAttributes(MetaEditorAttribute.parse("id,center,radius,forceatcenter,forceatedge,dampeningfactor,rotationaldampeningfactor,antigrav,"));

    }

}
