package com.WorldOfGoo.Scene;

import com.WooGLEFX.File.FileManager;
import com.WooGLEFX.Engine.Main;
import com.WooGLEFX.Engine.Renderer;
import com.WooGLEFX.Structures.*;
import com.WooGLEFX.Structures.SimpleStructures.DragSettings;
import com.WooGLEFX.Structures.SimpleStructures.MetaEditorAttribute;
import com.WooGLEFX.Structures.SimpleStructures.Position;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.transform.Affine;

public class Hinge extends EditorObject {
    public Hinge(EditorObject _parent) {
        super(_parent);
        setRealName("hinge");
        addAttribute("body1", "", InputField.ANY, true);
        addAttribute("body2", "", InputField.ANY, false);
        addAttribute("anchor", "0,0", InputField.POSITION, true);
        addAttribute("lostop", "", InputField.NUMBER, false);
        addAttribute("histop", "", InputField.NUMBER, false);
        addAttribute("bounce", "", InputField.NUMBER, false);
        addAttribute("stopcfm", "", InputField.NUMBER, false);
        addAttribute("stoperp", "", InputField.NUMBER, false);
        setMetaAttributes(MetaEditorAttribute.parse("anchor,body1,body2,?Hinge<bounce,histop,lostop,stopcfm,stoperp>"));
    }

    @Override
    public void draw(GraphicsContext graphicsContext, GraphicsContext imageGraphicsContext){

        if (Main.getLevel().isShowGeometry()) {

            double size = 10 * Main.getLevel().getZoom();

            Position anchor = Position.parse(getAttribute("anchor"));

            double screenX = anchor.getX() * Main.getLevel().getZoom() + Main.getLevel().getOffsetX();
            double screenY = -anchor.getY() * Main.getLevel().getZoom() + Main.getLevel().getOffsetY();

            Affine t = graphicsContext.getTransform();
            t.appendRotation(45, screenX, screenY);
            graphicsContext.setTransform(t);

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
        }
    }

    @Override
    public DragSettings mouseIntersection(double mX2, double mY2) {

        if (Main.getLevel().isShowGeometry()) {

            Position anchor = Position.parse(getAttribute("anchor"));
            double x = anchor.getX();
            double y = anchor.getY();

            double size = 7.5;
            double size2 = 2.5;

            Point2D rotated = rotate(new Point2D(mX2, mY2), Math.toRadians(45), new Point2D(x, -y));

            double mX = rotated.getX();
            double mY = rotated.getY();

            if ((mX > x - size && mX < x + size && mY > -y - size && mY < -y + size) && !(mX > x - size2 && mX < x + size2 && mY > -y - size2 && mY < -y + size2)) {
                DragSettings dragSettings = new DragSettings(DragSettings.MOVE);
                dragSettings.setInitialSourceX(mX2 - anchor.getX());
                dragSettings.setInitialSourceY(mY2 + anchor.getY());
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
        setAttribute("anchor", (mouseX - dragSourceX) + "," + (dragSourceY - mouseY));
    }
}
