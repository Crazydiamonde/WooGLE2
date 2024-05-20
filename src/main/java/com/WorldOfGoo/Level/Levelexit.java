package com.WorldOfGoo.Level;

import com.WooGLEFX.EditorObjects.Components.ObjectPosition;
import com.WooGLEFX.Engine.Renderer;
import com.WooGLEFX.Functions.LevelManager;
import com.WooGLEFX.Structures.EditorObject;
import com.WooGLEFX.Structures.InputField;
import com.WooGLEFX.Structures.SimpleStructures.MetaEditorAttribute;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

public class Levelexit extends EditorObject {

    public Levelexit(EditorObject _parent) {
        super(_parent, "levelexit");

        addAttribute("id",     InputField.ANY)     .setDefaultValue("theExit").assertRequired();
        addAttribute("pos",    InputField.POSITION).setDefaultValue("0,0")    .assertRequired();
        addAttribute("radius", InputField.NUMBER)  .setDefaultValue("75")     .assertRequired();
        addAttribute("filter", InputField.ANY)                                .assertRequired();

        addObjectPosition(new ObjectPosition(ObjectPosition.CIRCLE_HOLLOW) {
            public double getX() {
                return getAttribute("pos").positionValue().getX();
            }
            public void setX(double x) {
                setAttribute("pos", x + "," + -getY());
            }
            public double getY() {
                return -getAttribute("pos").positionValue().getY();
            }
            public void setY(double y) {
                setAttribute("pos", getX() + "," + -y);
            }
            public double getRadius() {
                return getAttribute("radius").doubleValue();
            }
            public void setRadius(double radius) {
                setAttribute("radius", radius);
            }
            public double getEdgeSize() {
                return 2;
            }
            public double getDepth() {
                return Renderer.GEOMETRY;
            }
            public Paint getBorderColor() {
                return new Color(1.0, 0, 1.0, 1.0);
            }
            public Paint getFillColor() {
                return new Color(1.0, 0, 1.0, 0.1);
            }
            public boolean isVisible() {
                return LevelManager.getLevel().getVisibilitySettings().getShowGeometry() != 0;
            }
        });

        setMetaAttributes(MetaEditorAttribute.parse("id,pos,radius,filter,"));

    }


    @Override
    public String getName() {
        return getAttribute("id").stringValue();
    }

}
