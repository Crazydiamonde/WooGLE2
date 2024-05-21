package com.WorldOfGoo.Scene;

import com.WooGLEFX.EditorObjects.objectcomponents.AnchorComponent;
import com.WooGLEFX.EditorObjects.objectcomponents.LineComponent;
import com.WooGLEFX.EditorObjects.objectcomponents.ObjectComponent;
import com.WooGLEFX.EditorObjects.EditorObject;
import com.WooGLEFX.EditorObjects.InputField;
import com.WooGLEFX.EditorObjects.ObjectUtil;
import com.WooGLEFX.Engine.Renderer;
import com.WooGLEFX.Functions.LevelManager;
import com.WooGLEFX.Structures.SimpleStructures.MetaEditorAttribute;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

public class Line extends EditorObject {

    public Line(EditorObject _parent) {
        super(_parent, "line", "scene\\line");

        addAttribute("id",       InputField.ANY)     .assertRequired();
        addAttribute("static",   InputField.FLAG)    .setDefaultValue("true").assertRequired();
        addAttribute("tag",      InputField.TAG);
        addAttribute("material", InputField.MATERIAL).setDefaultValue("rock").assertRequired();
        addAttribute("anchor",   InputField.POSITION).setDefaultValue("0,0") .assertRequired();
        addAttribute("normal",   InputField.POSITION).setDefaultValue("1,0") .assertRequired();
        addAttribute("break",    InputField.NUMBER);

        addObjectComponent(new LineComponent() {
            public double getX() {
                return getAttribute("anchor").positionValue().getX();
            }
            public void setX(double x) {
                setAttribute("anchor", x + "," + -getY());
            }
            public double getY() {
                return -getAttribute("anchor").positionValue().getY();
            }
            public void setY(double y) {
                setAttribute("anchor", getX() + "," + -y);
            }
            public double getRotation() {
                return Math.atan2(-getAttribute("normal").positionValue().getX(), -getAttribute("normal").positionValue().getY());
            }
            public double getLineWidth() {
                return 3;
            }
            public Paint getColor() {
                if (LevelManager.getLevel().getVisibilitySettings().getShowGeometry() != 2) return new Color(0.0, 0.25, 1.0, 1.0);

                if (ObjectUtil.attributeContainsTag(getAttribute("tag").listValue(), "deadly")) {
                    return new Color(1.0, 0.25, 0, 1.0);
                }

                if (ObjectUtil.attributeContainsTag(getAttribute("tag").listValue(), "mostlydeadly")) {
                    return new Color(0.5, 0.25, 0.5, 1.0);
                }

                if (ObjectUtil.attributeContainsTag(getAttribute("tag").listValue(), "detaching")) {
                    return new Color(0.0, 0.5, 0.5, 1.0);
                }

                return new Color(0.0, 0.25, 1.0, 1.0);

            }
            public double getDepth() {
                return Renderer.GEOMETRY;
            }
            public boolean isVisible() {
                return LevelManager.getLevel().getVisibilitySettings().getShowGeometry() != 0;
            }
        });

        addObjectComponent(new AnchorComponent() {
            public double getX() {
                return getAttribute("anchor").positionValue().getX();
            }
            public void setX(double x) {
                setAttribute("anchor", x + "," + -getY());
            }
            public double getY() {
                return -getAttribute("anchor").positionValue().getY();
            }
            public void setY(double y) {
                setAttribute("anchor", getX() + "," + -y);
            }
            public double getAnchorX() {
                return getAttribute("normal").positionValue().getX() * 20;
            }
            public double getAnchorY() {
                return -getAttribute("normal").positionValue().getY() * 20;
            }
            public void setAnchor(double anchorX, double anchorY) {
                double magnitude = Math.hypot(anchorX, anchorY);
                setAttribute("normal", anchorX / magnitude + "," + -anchorY / magnitude);
            }
            public double getLineWidth() {
                return 3;
            }
            public double getDepth() {
                return Renderer.GEOMETRY;
            }
            public Paint getColor() {
                return Rectangle.geometryColor(getAttribute("tag").listValue(), getParent());
            }
            public boolean isVisible() {
                return LevelManager.getLevel().getVisibilitySettings().getShowGeometry() != 0;
            }
        });

        setMetaAttributes(MetaEditorAttribute.parse("id,anchor,normal,material,tag,break,"));

    }


    @Override
    public String getName() {
        return getAttribute("id").stringValue();
    }

}
