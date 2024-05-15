package com.WorldOfGoo.Level;

import com.WooGLEFX.EditorObjects.Components.ObjectPosition;
import com.WooGLEFX.Functions.LevelManager;
import com.WooGLEFX.Structures.EditorObject;
import com.WooGLEFX.Structures.InputField;
import com.WooGLEFX.Structures.SimpleStructures.MetaEditorAttribute;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

public class Vertex extends EditorObject {

    private Vertex previous = null;
    public void setPrevious(Vertex previous) {
        this.previous = previous;
    }


    public Vertex(EditorObject _parent) {
        super(_parent);
        setRealName("Vertex");

        addAttribute("x", InputField.NUMBER).setDefaultValue("0").assertRequired();
        addAttribute("y", InputField.NUMBER).setDefaultValue("0").assertRequired();

        addObjectPosition(new ObjectPosition(ObjectPosition.RECTANGLE) {
            public double getX() {
                return getDouble("x");
            }
            public void setX(double x) {

                if (previous != null) {
                    double previousX = previous.getDouble("x");

                    // Make this vertex snap to 90-degree angles of the previous vertex
                    if (Math.abs(previousX - x) < 20) {
                        setAttribute("x", previousX);
                        return;
                    }
                }

                setAttribute("x", x);

            }
            public double getY() {
                return -getDouble("y");
            }
            public void setY(double y) {

                if (previous != null) {
                    double previousY = -previous.getDouble("y");

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
                return 2;
            }
            public Paint getBorderColor() {
                return new Color(1.0, 0, 1.0, 1.0);
            }
            public Paint getFillColor() {
                return new Color(1.0, 0, 1.0, 0.1);
            }
        });

        setNameAttribute(getAttribute2("x"));
        setNameAttribute2(getAttribute2("y"));
        setChangeListener("y", (observableValue, s, t1) -> {
            String bruh = getAttribute("x");
            setAttribute("x", "AAAAA");
            setAttribute("x", bruh);
        });
        setMetaAttributes(MetaEditorAttribute.parse("x,y,"));

    }


    public void drawTo(GraphicsContext graphicsContext, Vertex vertex, Paint color){
        if (LevelManager.getLevel().isShowGeometry()) {
            double screenX1 = Double.parseDouble(getAttribute("x")) * LevelManager.getLevel().getZoom() + LevelManager.getLevel().getOffsetX();
            double screenY1 = -Double.parseDouble(getAttribute("y")) * LevelManager.getLevel().getZoom() + LevelManager.getLevel().getOffsetY();

            double screenX2 = Double.parseDouble(vertex.getAttribute("x")) * LevelManager.getLevel().getZoom() + LevelManager.getLevel().getOffsetX();
            double screenY2 = -Double.parseDouble(vertex.getAttribute("y")) * LevelManager.getLevel().getZoom() + LevelManager.getLevel().getOffsetY();

            graphicsContext.setStroke(color);
            graphicsContext.setLineWidth(LevelManager.getLevel().getZoom() * 10);
            graphicsContext.strokeLine(screenX1, screenY1, screenX2, screenY2);
        }
    }


    @Override
    public boolean isVisible() {
        return LevelManager.getLevel().isShowGeometry();
    }

}

