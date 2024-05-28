package com.worldOfGoo.level;

import com.woogleFX.editorObjects.objectComponents.LineComponent;
import com.woogleFX.engine.Depth;
import com.woogleFX.functions.LevelManager;
import com.woogleFX.editorObjects.EditorObject;
import com.woogleFX.editorObjects.InputField;
import com.woogleFX.structures.GameVersion;
import com.woogleFX.structures.simpleStructures.MetaEditorAttribute;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

public class Targetheight extends EditorObject {

    public Targetheight(EditorObject _parent, GameVersion version) {
        super(_parent, "targetheight", version);

        addAttribute("y", InputField.NUMBER).setDefaultValue("1000").assertRequired();

        addObjectComponent(new LineComponent() {
            public double getX() {
                return 0;
            }
            public double getY() {
                return -getAttribute("y").doubleValue();
            }
            public void setY(double y) {
                setAttribute("y", -y);
            }
            public double getLineWidth() {
                return 3;
            }
            public double getDepth() {
                return Depth.GEOMETRY;
            }
            public Paint getColor() {
                return new Color(1.0, 0.0, 1.0, 1.0);
            }
            public boolean isVisible() {
                return LevelManager.getLevel().getVisibilitySettings().getShowGeometry() != 0;
            }
        });

        setMetaAttributes(MetaEditorAttribute.parse("y,"));

    }


    @Override
    public String getName() {
        return getAttribute("y").stringValue();
    }

}
