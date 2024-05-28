package com.worldOfGoo.level;

import com.woogleFX.editorObjects.objectComponents.RectangleComponent;
import com.woogleFX.engine.Depth;
import com.woogleFX.functions.LevelManager;
import com.woogleFX.editorObjects.EditorObject;
import com.woogleFX.editorObjects.InputField;
import com.woogleFX.structures.simpleStructures.MetaEditorAttribute;

import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

public class Vertex extends EditorObject {

    public Vertex(EditorObject _parent) {
        super(_parent, "Vertex", "level\\vertex");

        addAttribute("x", InputField.NUMBER).setDefaultValue("0").assertRequired();
        addAttribute("y", InputField.NUMBER).setDefaultValue("0").assertRequired();

        Vertex thisVertex = this;

        addObjectComponent(new RectangleComponent() {
            public double getX() {
                return getAttribute("x").doubleValue();
            }
            public void setX(double x) {

                EditorObject pipe = null;
                for (EditorObject editorObject : LevelManager.getLevel().getLevelObject().getChildren())
                    if (editorObject instanceof Pipe) {
                        pipe = editorObject;
                        break;
                    }

                if (pipe == null) return;

                EditorObject previous;
                if (pipe.getChildren().indexOf(thisVertex) == 0) previous = null;
                else previous = pipe.getChildren().get(pipe.getChildren().indexOf(thisVertex) - 1);

                if (previous != null) {
                    double previousX = previous.getAttribute("x").doubleValue();

                    // Make this vertex snap to 90-degree angles of the previous vertex
                    if (Math.abs(previousX - x) < 20) {
                        setAttribute("x", previousX);
                        return;
                    }
                }

                setAttribute("x", x);

            }
            public double getY() {
                return -getAttribute("y").doubleValue();
            }
            public void setY(double y) {

                EditorObject pipe = null;
                for (EditorObject editorObject : LevelManager.getLevel().getLevelObject().getChildren())
                    if (editorObject instanceof Pipe) {
                        pipe = editorObject;
                        break;
                    }

                if (pipe == null) return;

                EditorObject previous;
                if (pipe.getChildren().indexOf(thisVertex) == 0) previous = null;
                else previous = pipe.getChildren().get(pipe.getChildren().indexOf(thisVertex) - 1);

                if (previous != null) {
                    double previousY = -previous.getAttribute("y").doubleValue();

                    // Make this vertex snap to 90-degree angles of the previous vertex
                    if (Math.abs(previousY - y) < 20) {
                        setAttribute("y", -previousY);
                        return;
                    }
                }

                setAttribute("y", -y);

            }
            public double getRotation() {
                return Math.toRadians(45);
            }
            public double getWidth() {
                return 15;
            }
            public double getHeight() {
                return 15;
            }
            public double getEdgeSize() {
                return 4;
            }
            public boolean isEdgeOnly() {
                return true;
            }
            public double getDepth() {
                return getParent().getAttribute("depth").doubleValue() + 0.0001;
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
            public boolean isResizable() {
                return false;
            }
            public boolean isRotatable() {
                return false;
            }
        });

        setMetaAttributes(MetaEditorAttribute.parse("x,y,"));

    }


    @Override
    public void update() {
        getParent().update();
    }


    @Override
    public String getName() {
        String x = getAttribute("x").stringValue();
        String y = getAttribute("y").stringValue();
        return x + ", " + y;
    }

}

