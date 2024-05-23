package com.worldOfGoo.level;

import com.woogleFX.editorObjects.objectComponents.RectangleComponent;
import com.woogleFX.engine.Depth;
import com.woogleFX.functions.LevelManager;
import com.woogleFX.editorObjects.EditorObject;
import com.woogleFX.editorObjects.InputField;
import com.woogleFX.structures.simpleStructures.MetaEditorAttribute;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

public class Camera extends EditorObject {

    public Camera(EditorObject _parent) {
        super(_parent, "camera", "level\\camera");

        addAttribute("aspect",  InputField.ANY)       .setDefaultValue("normal");
        addAttribute("endpos",  InputField.POSITION)  .setDefaultValue("0,0");
        addAttribute("endzoom", InputField.NUMBER)    .setDefaultValue("1");

        addObjectComponent(new RectangleComponent() {
            public double getX() {
                return getAttribute("endpos").positionValue().getX();
            }
            public void setX(double x) {
                setAttribute("endpos", x + "," + -getY());
            }
            public double getY() {
                return -getAttribute("endpos").positionValue().getY();
            }
            public void setY(double y) {
                setAttribute("endpos", getX() + "," + -y);
            }
            public double getWidth() {

                String aspect = getAttribute("aspect").stringValue();
                double endzoom = getAttribute("endzoom").doubleValue();

                double screenWidth = (aspect.equals("widescreen")) ? 1007 : 800;
                return screenWidth * endzoom;

            }
            public void setWidth(double width) {

                String aspect = getAttribute("aspect").stringValue();

                double screenWidth = (aspect.equals("widescreen")) ? 1007 : 800;
                setAttribute("endzoom", width / screenWidth);

            }
            public double getHeight() {

                double endzoom = getAttribute("endzoom").doubleValue();

                return 525 * endzoom;

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
            public Paint getColor() {
                return new Color(0.0, 0.5, 0.5, 0.1);
            }
            public boolean isEdgeOnly() {
                return true;
            }
            public double getDepth() {
                return Depth.CAMERA;
            }
            public boolean isVisible() {
                return LevelManager.getLevel().getVisibilitySettings().isShowCameras();
            }
            public boolean isRotatable() {
                return false;
            }
        });

        setMetaAttributes(MetaEditorAttribute.parse("aspect,endpos,endzoom,"));

    }


    @Override
    public String getName() {
        return getAttribute("aspect").stringValue();
    }


    @Override
    public String[] getPossibleChildren() {
        return new String[]{ "poi" };
    }

}
