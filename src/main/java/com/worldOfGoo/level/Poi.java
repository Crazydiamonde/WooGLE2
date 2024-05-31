package com.worldOfGoo.level;

import com.woogleFX.editorObjects.objectComponents.RectangleComponent;
import com.woogleFX.engine.renderer.Depth;
import com.woogleFX.engine.LevelManager;
import com.woogleFX.editorObjects.EditorObject;
import com.woogleFX.editorObjects.attributes.InputField;
import com.woogleFX.gameData.level.GameVersion;
import com.woogleFX.editorObjects.attributes.MetaEditorAttribute;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

public class Poi extends EditorObject {

    public Poi(EditorObject _parent, GameVersion version) {
        super(_parent, "poi", version);

        addAttribute("pos",        InputField.POSITION).setDefaultValue("0,0").assertRequired();
        addAttribute("traveltime", InputField.NUMBER)  .setDefaultValue("3")  .assertRequired();
        addAttribute("pause",      InputField.NUMBER)  .setDefaultValue("0")  .assertRequired();
        addAttribute("zoom",       InputField.NUMBER)  .setDefaultValue("1")  .assertRequired();

        addObjectComponent(new RectangleComponent() {
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
            public double getWidth() {

                String aspect = getParent().getAttribute("aspect").stringValue();
                double zoom = getAttribute("zoom").doubleValue();

                double screenWidth = aspect.equals("widescreen") ? 1007 : 800;
                return screenWidth / zoom;

            }
            public void setWidth(double width) {

                String aspect = getParent().getAttribute("aspect").stringValue();

                double screenWidth = aspect.equals("widescreen") ? 1007 : 800;
                setAttribute("zoom", screenWidth / width);

            }
            public double getHeight() {

                double zoom = getAttribute("zoom").doubleValue();

                return 525 / zoom;

            }
            public void setHeight(double height) {
                setAttribute("zoom", 525 / height);
            }
            public double getEdgeSize() {
                return 1;
            }
            public boolean isEdgeOnly() {
                return true;
            }
            public double getDepth() {
                return Depth.CAMERA;
            }
            public Paint getBorderColor() {
                return new Color(0.25, 0.8, 0.8, 1.0);
            }
            public Paint getColor() {
                return new Color(0.25, 0.8, 0.8, 0.1);
            }
            public boolean isVisible() {
                return LevelManager.getLevel().getVisibilitySettings().isShowCameras();
            }
        });

        setMetaAttributes(MetaEditorAttribute.parse("pos,traveltime,pause,zoom,"));

    }

}
