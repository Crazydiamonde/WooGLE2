package com.WorldOfGoo.Level;

import com.WooGLEFX.EditorObjects.Components.ObjectPosition;
import com.WooGLEFX.Functions.LevelManager;
import com.WooGLEFX.Structures.EditorObject;
import com.WooGLEFX.Structures.InputField;
import com.WooGLEFX.Structures.SimpleStructures.MetaEditorAttribute;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

public class Targetheight extends EditorObject {

    public Targetheight(EditorObject _parent) {
        super(_parent);
        setRealName("targetheight");

        addAttribute("y", InputField.NUMBER).setDefaultValue("1000").assertRequired();

        addObjectPosition(new ObjectPosition(ObjectPosition.LINE) {
            public double getY() {
                return -getDouble("y");
            }
            public void setY(double y) {
                setAttribute("y", -y);
            }
            public double getEdgeSize() {
                return 3;
            }
            public Paint getBorderColor() {
                return new Color(1.0, 0.0, 1.0, 1.0);
            }
            public boolean isVisible() {
                return LevelManager.getLevel().isShowGeometry();
            }
        });

        setNameAttribute(getAttribute2("y"));
        setMetaAttributes(MetaEditorAttribute.parse("y,"));

    }

}
