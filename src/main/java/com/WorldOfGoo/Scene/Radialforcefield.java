package com.WorldOfGoo.Scene;

import com.WooGLEFX.EditorObjects.objectcomponents.AnchorComponent;
import com.WooGLEFX.EditorObjects.objectcomponents.CircleComponent;
import com.WooGLEFX.Functions.LevelManager;
import com.WooGLEFX.EditorObjects.EditorObject;
import com.WooGLEFX.EditorObjects.InputField;
import com.WooGLEFX.Structures.SimpleStructures.MetaEditorAttribute;
import javafx.scene.paint.*;

public class Radialforcefield extends EditorObject {

    public Radialforcefield(EditorObject _parent) {
        super(_parent, "radialforcefield", "scene\\radialforcefield");

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

        addObjectComponent(new CircleComponent() {
            public double getX() {
                return getAttribute("center").positionValue().getX();
            }
            public void setX(double x) {
                setAttribute("center", x + "," + getY());
            }
            public double getY() {
                return -getAttribute("center").positionValue().getY();
            }
            public void setY(double y) {
                setAttribute("center", getX() + "," + -y);
            }
            public double getRadius() {
                return getAttribute("radius").doubleValue();
            }
            public void setRadius(double radius) {
                setAttribute("radius", radius);
            }
            public double getEdgeSize() {
                return 4.5;
            }
            public boolean isEdgeOnly() {
                return true;
            }
            public double getDepth() {
                return 1000000;
            }
            public Paint getBorderColor() {
                return new Color(1.0, 1.0, 0, 1.0);
            }
            public Paint getColor() {
                return new Color(1.0, 1.0, 0, 0.05);
            }
            public boolean isVisible() {
                return LevelManager.getLevel().getVisibilitySettings().isShowForcefields();
            }
        });

        addObjectComponent(new AnchorComponent() {
            public double getX() {
                return getAttribute("center").positionValue().getX();
            }
            public double getY() {
                return -getAttribute("center").positionValue().getY();
            }
            public double getAnchorX() {
                return 0;
            }
            public double getAnchorY() {
                return -getAttribute("forceatcenter").doubleValue() * 20;
            }
            public void setAnchor(double anchorX, double anchorY) {
                setAttribute("forceatcenter", -anchorY / 20);
            }
            public double getLineWidth() {
                return 3;
            }
            public double getDepth() {
                return 1000000;
            }
            public Paint getColor() {
                return new LinearGradient(0, 0, 1, 1, true, CycleMethod.NO_CYCLE,
                        new Stop(0, Color.valueOf("802000FF")), new Stop(1, Color.valueOf("FFC040FF")));
            }
            public boolean isVisible() {
                return LevelManager.getLevel().getVisibilitySettings().isShowForcefields();
            }
        });

        addObjectComponent(new AnchorComponent() {
            public double getX() {
                return getAttribute("center").positionValue().getX() + getAttribute("radius").doubleValue();
            }
            public double getY() {
                return -getAttribute("center").positionValue().getY();
            }
            public double getAnchorX() {
                return 0;
            }
            public double getAnchorY() {
                return -getAttribute("forceatedge").doubleValue() * 20;
            }
            public void setAnchor(double anchorX, double anchorY) {
                setAttribute("forceatedge", -anchorY / 20);
            }
            public double getLineWidth() {
                return 3;
            }
            public double getDepth() {
                return 1000000;
            }
            public Paint getColor() {
                return new LinearGradient(0, 0, 1, 1, true, CycleMethod.NO_CYCLE,
                        new Stop(0, Color.valueOf("802000FF")), new Stop(1, Color.valueOf("FFC040FF")));
            }
            public boolean isVisible() {
                return LevelManager.getLevel().getVisibilitySettings().isShowForcefields();
            }
        });

        setMetaAttributes(MetaEditorAttribute.parse("id,center,radius,forceatcenter,forceatedge,dampeningfactor,rotationaldampeningfactor,antigrav,"));

    }


    @Override
    public String getName() {
        return getAttribute("id").stringValue();
    }

}
