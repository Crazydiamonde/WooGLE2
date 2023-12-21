package com.WorldOfGoo.Level;

import com.WooGLEFX.Engine.Main;
import com.WooGLEFX.Engine.Renderer;
import com.WooGLEFX.Structures.EditorObject;
import com.WooGLEFX.Structures.InputField;
import com.WooGLEFX.Structures.SimpleStructures.DragSettings;
import com.WooGLEFX.Structures.SimpleStructures.MetaEditorAttribute;

import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Paint;
import javafx.scene.transform.Affine;

public class Vertex extends EditorObject {
    private Vertex previous = null;

    public Vertex(EditorObject _parent) {
        super(_parent);
        setRealName("Vertex");
        addAttribute("x", "0", InputField.NUMBER, true);
        addAttribute("y", "0", InputField.NUMBER, true);
        setNameAttribute(getAttribute2("x"));
        setNameAttribute2(getAttribute2("y"));
        setChangeListener("y", (observableValue, s, t1) -> {
            String bruh = getAttribute("x");
            setAttribute("x", "AAAAA");
            setAttribute("x", bruh);
        });
        setMetaAttributes(MetaEditorAttribute.parse("x,y,"));
    }

    public Vertex getPrevious() {
        return previous;
    }

    public void setPrevious(Vertex previous) {
        this.previous = previous;
    }

    public void draw(GraphicsContext graphicsContext, GraphicsContext imageGraphicsContext){

        if (Main.getLevel().isShowGeometry()) {

            double size = 10 * Main.getLevel().getZoom();

            double screenX = Double.parseDouble(getAttribute("x")) * Main.getLevel().getZoom() + Main.getLevel().getOffsetX();
            double screenY = -Double.parseDouble(getAttribute("y")) * Main.getLevel().getZoom() + Main.getLevel().getOffsetY();

            Affine t = graphicsContext.getTransform();
            t.appendRotation(45, screenX, screenY);
            graphicsContext.setTransform(t);

            graphicsContext.setLineWidth(Main.getLevel().getZoom() * 5);
            graphicsContext.setStroke(Renderer.pipeVertex);

            graphicsContext.strokeRect(screenX - size / 2, screenY - size / 2, size, size);

            if (this == Main.getSelected()) {

                graphicsContext.setStroke(Renderer.selectionOutline2);
                graphicsContext.setLineWidth(1);
                graphicsContext.setLineDashes(3);
                graphicsContext.setLineDashOffset(0);
                graphicsContext.strokeRect(screenX - size / 2, screenY - size / 2, size, size);
                graphicsContext.setStroke(Renderer.selectionOutline);
                graphicsContext.setLineWidth(1);
                graphicsContext.setLineDashOffset(3);
                graphicsContext.strokeRect(screenX - size / 2, screenY - size / 2, size, size);
                graphicsContext.setLineDashes(0);

            }
        }
    }

    @Override
    public DragSettings mouseIntersection(double mX2, double mY2) {

        if (Main.getLevel().isShowGeometry()) {

            double x = getDouble("x");
            double y = getDouble("y");

            double size = 7.5;
            double size2 = 2.5;

            Point2D rotated = rotate(new Point2D(mX2, mY2), Math.toRadians(45), new Point2D(x, -y));

            double mX = rotated.getX();
            double mY = rotated.getY();

            if ((mX > x - size && mX < x + size && mY > -y - size && mY < -y + size) && !(mX > x - size2 && mX < x + size2 && mY > -y - size2 && mY < -y + size2)) {
                DragSettings dragSettings = new DragSettings(DragSettings.MOVE);
                dragSettings.setInitialSourceX(mX2 - x);
                dragSettings.setInitialSourceY(mY2 + y);
                return dragSettings;
            } else {
                return new DragSettings(DragSettings.NONE);
            }
        } else {
            return new DragSettings(DragSettings.NONE);
        }
    }

    @Override
    public void dragFromMouse(double mouseX, double mouseY, double dragSourceX, double dragSourceY){
        double locX = mouseX - dragSourceX;
        double locY = dragSourceY - mouseY;
        // Fetch previous Vertex in the tree view
        if (previous != null) {
            // Get the previous Vertex's coordinates
            double previousX = Double.parseDouble(previous.getAttribute("x"));
            double previousY = Double.parseDouble(previous.getAttribute("y"));

            // Make this vertex snap to 90-degree angles of the previous vertex
            if (Math.abs(previousX - locX) < 20) {
                locX = previousX;
            } else if (Math.abs(previousY - locY) < 20) {
                locY = previousY;
            }
        }

        setAttribute("x", locX);
        setAttribute("y", locY);
    }

    public void drawTo(GraphicsContext graphicsContext, Vertex vertex, Paint color){
        if (Main.getLevel().isShowGeometry()) {
            double screenX1 = Double.parseDouble(getAttribute("x")) * Main.getLevel().getZoom() + Main.getLevel().getOffsetX();
            double screenY1 = -Double.parseDouble(getAttribute("y")) * Main.getLevel().getZoom() + Main.getLevel().getOffsetY();

            double screenX2 = Double.parseDouble(vertex.getAttribute("x")) * Main.getLevel().getZoom() + Main.getLevel().getOffsetX();
            double screenY2 = -Double.parseDouble(vertex.getAttribute("y")) * Main.getLevel().getZoom() + Main.getLevel().getOffsetY();

            graphicsContext.setStroke(color);
            graphicsContext.setLineWidth(Main.getLevel().getZoom() * 10);
            graphicsContext.strokeLine(screenX1, screenY1, screenX2, screenY2);
        }
    }
}

