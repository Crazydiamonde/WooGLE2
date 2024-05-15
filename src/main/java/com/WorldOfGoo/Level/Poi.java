package com.WorldOfGoo.Level;

import com.WooGLEFX.EditorObjects.Components.ObjectPosition;
import com.WooGLEFX.Functions.LevelManager;
import com.WooGLEFX.Structures.EditorAttribute;
import com.WooGLEFX.Structures.EditorObject;
import com.WooGLEFX.Structures.InputField;
import com.WooGLEFX.Structures.SimpleStructures.MetaEditorAttribute;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

public class Poi extends EditorObject {

    public Poi(EditorObject _parent) {
        super(_parent);
        setRealName("poi");

        addAttribute("pos",        InputField.POSITION).setDefaultValue("0,0").assertRequired();
        addAttribute("traveltime", InputField.NUMBER)  .setDefaultValue("3")  .assertRequired();
        addAttribute("pause",      InputField.NUMBER)  .setDefaultValue("0")  .assertRequired();
        addAttribute("zoom",       InputField.NUMBER)  .setDefaultValue("1")  .assertRequired();

        addObjectPosition(new ObjectPosition(ObjectPosition.RECTANGLE_HOLLOW) {
            public double getX() {
            return getPosition("pos").getX();
        }
            public void setX(double x) {
                setAttribute("pos", x + "," + -getY());
            }
            public double getY() {
                return -getPosition("pos").getY();
            }
            public void setY(double y) {
                setAttribute("pos", getX() + "," + -y);
            }
            public double getWidth() {
                double screenWidth = (getParent().getAttribute("aspect").equals("widescreen")) ? 1007 : 800;
                return screenWidth * getDouble("zoom");
            }
            public void setWidth(double width) {
                double screenWidth = (getParent().getAttribute("aspect").equals("widescreen")) ? 1007 : 800;
                setAttribute("zoom", width / screenWidth);
            }
            public double getHeight() {
                return 525 * getDouble("zoom");
            }
            public void setHeight(double height) {
                setAttribute("zoom", height / 525);
            }
            public double getEdgeSize() {
                return 1;
            }
            public Paint getBorderColor() {
                return new Color(0.25, 0.8, 0.8, 1.0);
            }
            public Paint getFillColor() {
                return new Color(0.25, 0.8, 0.8, 0.1);
            }
        });

        setNameAttribute(EditorAttribute.NULL);
        setMetaAttributes(MetaEditorAttribute.parse("pos,traveltime,pause,zoom,"));

    }


    @Override
    public boolean isVisible() {
        return LevelManager.getLevel().isShowCameras();
    }

}
