package com.worldOfGoo.level;

import com.woogleFX.editorObjects.objectComponents.RectangleComponent;
import com.woogleFX.functions.LevelManager;
import com.woogleFX.editorObjects.EditorObject;
import com.woogleFX.editorObjects.InputField;
import com.woogleFX.structures.GameVersion;
import com.woogleFX.structures.simpleStructures.MetaEditorAttribute;
import javafx.scene.paint.Paint;

public class Pipe extends EditorObject {

    public Pipe(EditorObject _parent, GameVersion version) {
        super(_parent, "pipe", version);

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
                return getAttribute("depth").doubleValue();
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

