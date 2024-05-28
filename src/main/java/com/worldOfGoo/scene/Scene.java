package com.worldOfGoo.scene;

import com.woogleFX.editorObjects.objectComponents.RectangleComponent;
import com.woogleFX.engine.Depth;
import com.woogleFX.functions.LevelManager;
import com.woogleFX.editorObjects.EditorObject;
import com.woogleFX.editorObjects.InputField;
import com.woogleFX.structures.simpleStructures.MetaEditorAttribute;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

public class Scene extends EditorObject {

    public Scene(EditorObject _parent) {
        super(_parent, "scene", "scene\\scene");

        addAttribute("minx", InputField.NUMBER)          .setDefaultValue("-500");
        addAttribute("miny", InputField.NUMBER)          .setDefaultValue("0");
        addAttribute("maxx", InputField.NUMBER)          .setDefaultValue("500");
        addAttribute("maxy", InputField.NUMBER)          .setDefaultValue("1000");
        addAttribute("backgroundcolor", InputField.COLOR).setDefaultValue("0,0,0").assertRequired();

        setMetaAttributes(MetaEditorAttribute.parse("backgroundcolor,minx,miny,maxx,maxy,"));

        addObjectComponent(new RectangleComponent() {
            public double getX() {
                double minx = getAttribute("minx").doubleValue();
                double maxx = getAttribute("maxx").doubleValue();
                return (minx + maxx) / 2;
            }
            public void setX(double x) {
                double width = getWidth();
                setAttribute("minx", x - width / 2);
                setAttribute("maxx", x + width / 2);
            }
            public double getY() {
                double miny = -getAttribute("miny").doubleValue();
                double maxy = -getAttribute("maxy").doubleValue();
                return (miny + maxy) / 2;
            }
            public void setY(double y) {
                double height = getHeight();
                setAttribute("miny", -y - height / 2);
                setAttribute("maxy", -y + height / 2);
            }
            public double getWidth() {
                double minx = getAttribute("minx").doubleValue();
                double maxx = getAttribute("maxx").doubleValue();
                return Math.abs(maxx - minx);
            }
            public void setWidth(double width) {
                double x = getX();
                setAttribute("minx", x - width / 2);
                setAttribute("maxx", x + width / 2);
            }
            public double getHeight() {
                double miny = -getAttribute("miny").doubleValue();
                double maxy = -getAttribute("maxy").doubleValue();
                return Math.abs(maxy - miny);
            }
            public void setHeight(double height) {
                double y = getY();
                setAttribute("miny", -y - height / 2);
                setAttribute("maxy", -y + height / 2);
            }
            public double getDepth() {
                return Depth.SCENE;
            }
            public double getEdgeSize() {
                return 1.0;
            }
            public Paint getBorderColor() {
                return new Color(0.0, 0.0,0.0, 1.0);
            }
            public boolean isEdgeOnly() {
                return true;
            }
            public Paint getColor() {
                return new Color(0.0, 0.0, 0.0, 0.0);
            }
            public boolean isRotatable() {
                return false;
            }
        });

        addObjectComponent(new RectangleComponent() {
            public double getX() {
                double minx = getAttribute("minx").doubleValue();
                double maxx = getAttribute("maxx").doubleValue();
                return (minx + maxx) / 2;
            }
            public void setX(double x) {
                double width = getWidth();
                setAttribute("minx", x - width / 2);
                setAttribute("maxx", x + width / 2);
            }
            public double getY() {
                double miny = -getAttribute("miny").doubleValue();
                double maxy = -getAttribute("maxy").doubleValue();
                return (miny + maxy) / 2;
            }
            public void setY(double y) {
                double height = getHeight();
                setAttribute("miny", -(y - height / 2));
                setAttribute("maxy", -(y + height / 2));
            }
            public double getWidth() {
                double minx = getAttribute("minx").doubleValue();
                double maxx = getAttribute("maxx").doubleValue();
                return Math.abs(maxx - minx);
            }
            public double getHeight() {
                double miny = -getAttribute("miny").doubleValue();
                double maxy = -getAttribute("maxy").doubleValue();
                return Math.abs(maxy - miny);
            }
            public double getDepth() {
                return Depth.SCENE_BG;
            }
            public double getEdgeSize() {
                return 0.0;
            }
            public Paint getBorderColor() {
                return new Color(0.0, 0.0,0.0, 0.0);
            }
            public boolean isEdgeOnly() {
                return false;
            }
            public Paint getColor() {
                if (LevelManager.getLevel().getVisibilitySettings().isShowSceneBGColor()) {
                    com.woogleFX.structures.simpleStructures.Color backgroundColor = getAttribute("backgroundcolor").colorValue();
                    double r = backgroundColor.getR() / 255.0;
                    double g = backgroundColor.getG() / 255.0;
                    double b = backgroundColor.getB() / 255.0;
                    return new Color(r, g, b, 1.0);
                }
                return new Color(0.0, 0.0, 0.0, 0.0);
            }
            public boolean isSelectable() {
                return LevelManager.getLevel().getVisibilitySettings().isShowSceneBGColor();
            }
            public boolean isDraggable() {
                return LevelManager.getLevel().getVisibilitySettings().isShowSceneBGColor();
            }
            public boolean isResizable() {
                return false;
            }
            public boolean isRotatable() {
                return false;
            }
        });

    }


    @Override
    public String[] getPossibleChildren() {
        return new String[]{ "SceneLayer", "button", "buttongroup", "circle", "compositegeom", "hinge", "label", "line", "linearforcefield", "motor", "particles", "radialforcefield", "rectangle", "slider" };
    }

}
