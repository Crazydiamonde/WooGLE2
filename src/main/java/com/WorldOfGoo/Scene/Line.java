package com.WorldOfGoo.Scene;

import com.WooGLEFX.EditorObjects.Components.ObjectPosition;
import com.WooGLEFX.Functions.LevelManager;
import com.WooGLEFX.Structures.*;
import com.WooGLEFX.Structures.SimpleStructures.MetaEditorAttribute;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

public class Line extends EditorObject {

    public Line(EditorObject _parent) {
        super(_parent);
        setRealName("line");

        addAttribute("id",       InputField.ANY)     .assertRequired();
        addAttribute("static",   InputField.FLAG)    .setDefaultValue("true").assertRequired();
        addAttribute("tag",      InputField.TAG);
        addAttribute("material", InputField.MATERIAL).setDefaultValue("rock").assertRequired();
        addAttribute("anchor",   InputField.POSITION).setDefaultValue("0,0") .assertRequired();
        addAttribute("normal",   InputField.POSITION).setDefaultValue("1,0") .assertRequired();
        addAttribute("break",    InputField.NUMBER);

        addObjectPosition(new ObjectPosition(ObjectPosition.LINE) {
            public double getX() {
                return getPosition("anchor").getX();
            }
            public void setX(double x) {
                setAttribute("anchor", x + "," + -getY());
            }
            public double getY() {
                return -getPosition("anchor").getY();
            }
            public void setY(double y) {
                setAttribute("anchor", getX() + "," + -y);
            }
            public double getRotation() {
                return Math.atan2(-getPosition("normal").getX(), -getPosition("normal").getY());
            }
            public double getEdgeSize() {
                return 3;
            }
            public Paint getBorderColor() {
                return new Color(0.0, 0.25, 1.0, 1.0);
            }
            public boolean isVisible() {
                return LevelManager.getLevel().isShowGeometry();
            }
        });

        addObjectPosition(new ObjectPosition(ObjectPosition.ANCHOR) {
            public double getX() {
                return getPosition("anchor").getX();
            }
            public void setX(double x) {
                setAttribute("anchor", x + "," + -getY());
            }
            public double getY() {
                return -getPosition("anchor").getY();
            }
            public void setY(double y) {
                setAttribute("anchor", getX() + "," + -y);
            }
            public double getAnchorX() {
                return getPosition("normal").getX() * 20;
            }
            public double getAnchorY() {
                return -getPosition("normal").getY() * 20;
            }
            public void setAnchor(double anchorX, double anchorY) {
                double magnitude = Math.hypot(anchorX, anchorY);
                setAttribute("normal", anchorX / magnitude + "," + -anchorY / magnitude);
            }
            public double getEdgeSize() {
                return 3;
            }
            public Paint getBorderColor() {
                return new Color(0.0, 0.25, 1.0, 1.0);
            }
            public boolean isVisible() {
                return LevelManager.getLevel().isShowGeometry();
            }
        });

        setNameAttribute(getAttribute2("id"));
        setMetaAttributes(MetaEditorAttribute.parse("id,anchor,normal,material,tag,break,"));

    }

}
