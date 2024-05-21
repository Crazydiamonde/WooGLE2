package com.WorldOfGoo.Level;

import com.WooGLEFX.EditorObjects.objectcomponents.RectangleComponent;
import com.WooGLEFX.Engine.Renderer;
import com.WooGLEFX.Functions.LevelManager;
import com.WooGLEFX.EditorObjects.EditorObject;
import com.WooGLEFX.EditorObjects.InputField;
import com.WooGLEFX.Structures.SimpleStructures.MetaEditorAttribute;
import javafx.scene.paint.Paint;

public class Pipe extends EditorObject {

    public Pipe(EditorObject _parent) {
        super(_parent, "pipe", "level\\pipe");

        addAttribute("type", InputField.ANY);
        addAttribute("id", InputField.ANY)                        .assertRequired();
        addAttribute("depth", InputField.ANY).setDefaultValue("0").assertRequired();

        setMetaAttributes(MetaEditorAttribute.parse("type,id,depth,"));

    }


    private void addObjectPositionBetweenVertices(Vertex a, Vertex b) {

        addObjectComponent(new RectangleComponent() {

            public double getX() {
                double x1 = a.getAttribute("x").doubleValue();
                double x2 = b.getAttribute("x").doubleValue();
                return (x1 + x2) / 2;
            }

            public double getY() {
                double y1 = -a.getAttribute("y").doubleValue();
                double y2 = -b.getAttribute("y").doubleValue();
                return (y1 + y2) / 2;
            }

            public double getRotation() {

                double x1 = a.getAttribute("x").doubleValue();
                double y1 = -a.getAttribute("y").doubleValue();

                double x2 = b.getAttribute("x").doubleValue();
                double y2 = -b.getAttribute("y").doubleValue();

                return Math.PI / 2 + Math.atan2(y2 - y1, x2 - x1);

            }

            public double getWidth() {
                return 10.0;
            }

            public double getHeight() {
                double x1 = a.getAttribute("x").doubleValue();
                double y1 = -a.getAttribute("y").doubleValue();

                double x2 = b.getAttribute("x").doubleValue();
                double y2 = -b.getAttribute("y").doubleValue();

                return Math.hypot(y2 - y1, x2 - x1) + 11;
            }

            public double getEdgeSize() {
                return 0.0;
            }
            public boolean isEdgeOnly() {
                return false;
            }
            public double getDepth() {
                return Renderer.GEOMETRY + 0.0000005;
            }

            public Paint getBorderColor() {

                // TODO more dynamic pipe color system that supports custom colors

                switch (getAttribute("type").stringValue()) {
                    case "BEAUTY" -> { return Paint.valueOf("FFA6B7FF"); }
                    case "ISH" -> { return Paint.valueOf("5FFF5FFF"); }
                    default -> { return Paint.valueOf("404040FF"); }
                }

            }

            public Paint getColor() {
                return getBorderColor();
            }

            public boolean isVisible() {
                return LevelManager.getLevel().getVisibilitySettings().getShowGeometry() != 0;
            }
            public boolean isDraggable() {
                return false;
            }
            public boolean isResizable() {
                return false;
            }
            public boolean isRotatable() {
                return false;
            }

        });

    }


    private void updateObjectPositions() {
        clearObjectPositions();
        for (int i = 0; i < getChildren().size() - 1; i++) {
            addObjectPositionBetweenVertices((Vertex)getChildren().get(i), (Vertex)getChildren().get(i + 1));
        }
    }


    @Override
    public void update() {
        updateObjectPositions();
    }


    @Override
    public String getName() {
        return getAttribute("type").stringValue();
    }


    @Override
    public String[] getPossibleChildren() {
        return new String[]{ "Vertex" };
    }

}

