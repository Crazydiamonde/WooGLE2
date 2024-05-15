package com.WorldOfGoo.Level;

import com.WooGLEFX.EditorObjects.Components.ObjectPosition;
import com.WooGLEFX.Functions.LevelManager;
import com.WooGLEFX.Structures.EditorObject;
import com.WooGLEFX.Structures.InputField;
import com.WooGLEFX.Structures.SimpleStructures.MetaEditorAttribute;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

public class Camera extends EditorObject {

    public Camera(EditorObject _parent) {
        super(_parent);
        setRealName("camera");

        addAttribute("aspect", InputField.ANY)     .setDefaultValue("normal");
        addAttribute("endpos", InputField.POSITION).setDefaultValue("0,0");
        addAttribute("endzoom", InputField.NUMBER) .setDefaultValue("1");

        addObjectPosition(new ObjectPosition(ObjectPosition.RECTANGLE_HOLLOW) {
            public double getX() {
                return getPosition("endpos").getX();
            }
            public void setX(double x) {
                setAttribute("endpos", x + "," + -getY());
            }
            public double getY() {
                return -getPosition("endpos").getY();
            }
            public void setY(double y) {
                setAttribute("endpos", getX() + "," + -y);
            }
            public double getWidth() {
                double screenWidth = (getAttribute("aspect").equals("widescreen")) ? 1007 : 800;
                return screenWidth * getDouble("endzoom");
            }
            public void setWidth(double width) {
                double screenWidth = (getAttribute("aspect").equals("widescreen")) ? 1007 : 800;
                setAttribute("endzoom", width / screenWidth);
            }
            public double getHeight() {
                return 525 * getDouble("endzoom");
            }
            public void setHeight(double height) {
                setAttribute("endzoom", height / 525);
            }
            public double getEdgeSize() {
                return 1.0;
            }
            public Paint getBorderColor() {
                return new Color(0.0, 0.5, 0.5, 1.0);
            }
            public Paint getFillColor() {
                return new Color(0.0, 0.5, 0.5, 0.1);
            }
            public boolean isVisible() {
                return LevelManager.getLevel().isShowCameras();
            }
            public boolean isRotatable() {
                return false;
            }
        });

        setNameAttribute(getAttribute2("aspect"));
        setMetaAttributes(MetaEditorAttribute.parse("aspect,endpos,endzoom,"));

    }


    @Override
    public String[] getPossibleChildren() {
        return new String[]{ "poi" };
    }

}
