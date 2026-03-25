package com.worldOfGoo.level;

import com.woogleFX.assets.Asset;
import com.woogleFX.editorObjects.EditorObject;
import com.woogleFX.editorObjects.objectComponents.RectangleComponent;
import com.woogleFX.engine.AssetManager;
import com.woogleFX.assets.GameVersion;
import javafx.scene.paint.Paint;

public class pipe extends EditorObject {

    public pipe(EditorObject _parent, GameVersion version) {
        super(_parent, version);
    }


    private void addObjectPositionBetweenVertices(Vertex a, Vertex b) {

        addObjectComponent(new RectangleComponent(this) {

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

                // TODO: more dynamic pipe color system that supports custom colors

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
                return AssetManager.getVisibility("geometry") != 0;
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
        clearObjectComponents();
        for (int i = 0; i < getChildren().size() - 1; i++) {
            addObjectPositionBetweenVertices((Vertex)getChildren().get(i), (Vertex)getChildren().get(i + 1));
        }
    }

    @Override
    public void onLoaded(Asset asset) {
        super.onLoaded(asset);
        updateObjectPositions();
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
    @SuppressWarnings("unchecked")
    public Class<? extends EditorObject>[] getPossibleChildren() {
        return new Class[]{ Vertex.class };
    }

}

