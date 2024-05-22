package com.worldOfGoo.level;

import com.woogleFX.editorObjects.ParticleGraphicsInstance;
import com.woogleFX.editorObjects.objectComponents.CircleComponent;
import com.woogleFX.engine.Depth;
import com.woogleFX.functions.LevelManager;
import com.woogleFX.editorObjects.EditorObject;
import com.woogleFX.editorObjects.InputField;
import com.woogleFX.structures.simpleStructures.MetaEditorAttribute;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

import java.util.ArrayList;

public class Fire extends EditorObject {

    private final ArrayList<ArrayList<ParticleGraphicsInstance>> drawing = new ArrayList<>();
    public ArrayList<ArrayList<ParticleGraphicsInstance>> getDrawing() {
        return drawing;
    }


    private final ArrayList<Integer> counts = new ArrayList<>();
    public ArrayList<Integer> getCounts() {
        return counts;
    }


    public Fire(EditorObject _parent) {
        super(_parent, "fire", "level\\fire");

        addAttribute("depth",     InputField.NUMBER)   .setDefaultValue("0") .assertRequired();
        addAttribute("particles", InputField.PARTICLES)                      .assertRequired();
        addAttribute("x",         InputField.NUMBER)   .setDefaultValue("0") .assertRequired();
        addAttribute("y",         InputField.NUMBER)   .setDefaultValue("0") .assertRequired();
        addAttribute("radius",    InputField.NUMBER)   .setDefaultValue("50").assertRequired();

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
            public double getRadius() {
                return getAttribute("radius").doubleValue();
            }
            public void setRadius(double radius) {
                setAttribute("radius", radius);
            }
            public double getEdgeSize() {
                return 3;
            }
            public Paint getBorderColor() {
                return new Color(1.0, 0.25, 0.0, 1.0);
            }
            public double getDepth() {
                return Depth.GEOMETRY;
            }
            public Paint getColor() {
                return new Color(1.0, 0.25, 0.0, 0.1);
            }
            public boolean isEdgeOnly() {
                return false;
            }
            public boolean isVisible() {
                return LevelManager.getLevel().getVisibilitySettings().getShowGeometry() != 0;
            }
        });

        setMetaAttributes(MetaEditorAttribute.parse("x,y,radius,particles,depth,"));

    }


    @Override
    public String getName() {
        return getAttribute("particles").stringValue();
    }

}
