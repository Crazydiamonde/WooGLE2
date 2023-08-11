package com.WorldOfGoo.Scene;

import com.WooGLEFX.File.FileManager;
import com.WooGLEFX.Engine.Main;
import com.WooGLEFX.Engine.Renderer;
import com.WooGLEFX.Structures.*;
import com.WooGLEFX.Structures.SimpleStructures.DragSettings;
import com.WooGLEFX.Structures.SimpleStructures.MetaEditorAttribute;
import com.WooGLEFX.Structures.SimpleStructures.Position;
import javafx.scene.canvas.GraphicsContext;

public class Slider extends EditorObject {
    public Slider(EditorObject _parent) {
        super(_parent);
        setRealName("slider");
        addAttribute("body1", "", InputField.ANY, true);
        addAttribute("body2", "", InputField.ANY, true);
        addAttribute("axis", "1,0", InputField.POSITION, true);
        addAttribute("bounce", "", InputField.NUMBER, false);
        addAttribute("histop", "", InputField.NUMBER, false);
        addAttribute("lostop", "", InputField.NUMBER, false);
        addAttribute("stopcfm", "", InputField.NUMBER, false);
        addAttribute("stoperp", "", InputField.NUMBER, false);
        setNameAttribute(getAttribute2("body1"));
        setNameAttribute2(getAttribute2("body2"));
        setChangeListener("body2", (observableValue, s, t1) -> {
            String bruh = getAttribute("body1");
            setAttribute("body1", "AAAAA");
            setAttribute("body1", bruh);
        });
        setMetaAttributes(MetaEditorAttribute.parse("body1,body2,axis,Slider<bounce,histop,lostop,stopcfm,stoperp>"));
    }

    @Override
    public void draw(GraphicsContext graphicsContext, GraphicsContext imageGraphicsContext){

        if (Main.getLevel().isShowGeometry()) {

            Position axis = Position.parse(getAttribute("axis"));
            double magnitude = Math.sqrt(axis.getX() * axis.getX() + axis.getY() * axis.getY());
            double theta = Math.asin(axis.getY() / magnitude);
            if (axis.getX() < 0) {
                theta *= -1;
            }

            double dx = axis.getX() / magnitude;
            double dy = axis.getY() / magnitude;

            EditorObject geometry1 = null;

            EditorObject geometry2 = null;

            for (EditorObject maybeGeometry : Main.getLevel().getScene()) {
                if (maybeGeometry instanceof Rectangle || maybeGeometry instanceof Circle) {
                    if (maybeGeometry.getAttribute("id").equals(getAttribute("body1"))) {
                        geometry1 = maybeGeometry;
                    } else if (maybeGeometry.getAttribute("id").equals(getAttribute("body2"))) {
                        geometry2 = maybeGeometry;
                    }
                }
            }

            if (geometry1 != null) {

                double size = 10 * Main.getLevel().getZoom();

                double x = geometry1.getDouble("x");
                double y = geometry1.getDouble("y");

                double screenX = x * Main.getLevel().getZoom() + Main.getLevel().getOffsetX();
                double screenY = -y * Main.getLevel().getZoom() + Main.getLevel().getOffsetY();

                graphicsContext.save();

                //Affine t = graphicsContext.getTransform();
                //t.appendRotation(45, screenX, screenY);
                //graphicsContext.setTransform(t);

                graphicsContext.setLineWidth(Main.getLevel().getZoom() * 5);
                graphicsContext.setStroke(Renderer.mechanics);

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

                graphicsContext.restore();
            }

            if (geometry2 != null) {

                double size = 10 * Main.getLevel().getZoom();

                double x = geometry2.getDouble("x");
                double y = geometry2.getDouble("y");

                //TODO: Account for geometry possibly being in compositegeoms

                double screenX = x * Main.getLevel().getZoom() + Main.getLevel().getOffsetX();
                double screenY = -y * Main.getLevel().getZoom() + Main.getLevel().getOffsetY();

                graphicsContext.save();

                //Affine t = graphicsContext.getTransform();
                //t.appendRotation(45, screenX, screenY);
                //graphicsContext.setTransform(t);

                graphicsContext.setFill(Renderer.mechanics);

                graphicsContext.fillRect(screenX - size * 2 / 2, screenY - size * 2 / 2, size * 2, size * 2);



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

                graphicsContext.restore();
                graphicsContext.save();

                double dst = Math.max(10000 * Main.getLevel().getZoom(), 10000);

                double screenX2 = (x - dst * dx) * Main.getLevel().getZoom() + Main.getLevel().getOffsetX();
                double screenY2 = (-y - dst * dy) * Main.getLevel().getZoom() + Main.getLevel().getOffsetY();

                double screenX3 = (x + dst * dx) * Main.getLevel().getZoom() + Main.getLevel().getOffsetX();
                double screenY3 = (-y + dst * dy) * Main.getLevel().getZoom() + Main.getLevel().getZoom();

                graphicsContext.setLineWidth(Main.getLevel().getZoom() * 5);
                graphicsContext.setStroke(Renderer.mechanics);

                graphicsContext.strokeLine(screenX2, screenY2, screenX3, screenY3);
                graphicsContext.restore();
            }
        }
    }

    @Override
    public DragSettings mouseIntersection(double mX2, double mY2) {
        return new DragSettings(DragSettings.NONE);
        /*

        if (Main.getLevel().isShowGeometry()) {

            Position anchor = Position.parse(getAttribute("anchor"));
            double x = anchor.getX();
            double y = anchor.getY();

            double size = 7.5;
            double size2 = 2.5;

            Point2D rotated = rotate(new Point2D(mX2, mY2), Math.toRadians(45), new Point2D(x, -y));

            double mX = rotated.getX();
            double mY = rotated.getY();

            return ((mX > x - size && mX < x + size && mY > -y - size && mY < -y + size) && !(mX > x - size2 && mX < x + size2 && mY > -y - size2 && mY < -y + size2));
        } else {
            return false;
        }

         */
    }

    @Override
    public void dragFromMouse(double mouseX, double mouseY, double dragSourceX, double dragSourceY){
        setAttribute("anchor", (mouseX - dragSourceX) + "," + (dragSourceY - mouseY));
    }
}
