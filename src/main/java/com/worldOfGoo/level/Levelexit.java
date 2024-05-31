package com.worldOfGoo.level;

import com.woogleFX.editorObjects.objectComponents.CircleComponent;
import com.woogleFX.engine.renderer.Depth;
import com.woogleFX.engine.LevelManager;
import com.woogleFX.editorObjects.EditorObject;
import com.woogleFX.editorObjects.attributes.InputField;
import com.woogleFX.gameData.level.GameVersion;
import com.woogleFX.editorObjects.attributes.MetaEditorAttribute;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

public class Levelexit extends EditorObject {

    public Levelexit(EditorObject _parent, GameVersion version) {
        super(_parent, "levelexit", version);

        addAttribute("id",     InputField.ANY)     .setDefaultValue("theExit").assertRequired();
        addAttribute("pos",    InputField.POSITION).setDefaultValue("0,0")    .assertRequired();
        addAttribute("radius", InputField.NUMBER)  .setDefaultValue("75")     .assertRequired();
        addAttribute("filter", InputField.ANY)                                .assertRequired();

        addObjectComponent(new CircleComponent() {
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
                return 4;
            }
            public boolean isEdgeOnly() {
                return true;
            }
            public double getDepth() {
                return Depth.GEOMETRY;
            }
            public Paint getBorderColor() {
                return new Color(1.0, 0, 1.0, 1.0);
            }
            public Paint getColor() {
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
