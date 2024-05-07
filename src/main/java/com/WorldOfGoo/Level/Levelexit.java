package com.WorldOfGoo.Level;

import com.WooGLEFX.Engine.Main;
import com.WooGLEFX.Engine.Renderer;
import com.WooGLEFX.Functions.LevelManager;
import com.WooGLEFX.Structures.EditorObject;
import com.WooGLEFX.Structures.InputField;
import com.WooGLEFX.Structures.SimpleStructures.DragSettings;
import com.WooGLEFX.Structures.SimpleStructures.MetaEditorAttribute;
import com.WooGLEFX.Structures.SimpleStructures.Position;

import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;

public class Levelexit extends EditorObject {
    public Levelexit(EditorObject _parent) {
        super(_parent);
        setRealName("levelexit");
        addAttribute("id", "theExit", InputField.ANY, true);
        addAttribute("pos", "0,0", InputField.POSITION, true);
        addAttribute("radius", "75", InputField.NUMBER, true);
        addAttribute("filter", "", InputField.ANY, true);
        setNameAttribute(getAttribute2("id"));
        setMetaAttributes(MetaEditorAttribute.parse("id,pos,radius,filter,"));
    }

    @Override
    public void draw(GraphicsContext graphicsContext, GraphicsContext imageGraphicsContext) {

        if (LevelManager.getLevel().isShowGeometry()) {

            Position pos = Position.parse(getAttribute("pos"));

            double radius = Double.parseDouble(getAttribute("radius"));

            double screenX = pos.getX() * LevelManager.getLevel().getZoom() + LevelManager.getLevel().getOffsetX() - radius * LevelManager.getLevel().getZoom();
            double screenY = -pos.getY() * LevelManager.getLevel().getZoom() + LevelManager.getLevel().getOffsetY() - radius * LevelManager.getLevel().getZoom();

            graphicsContext.setStroke(Renderer.pipeVertex);
            graphicsContext.setLineWidth(LevelManager.getLevel().getZoom() * 3);
            graphicsContext.strokeOval(screenX + LevelManager.getLevel().getZoom() * 3 / 2, screenY + LevelManager.getLevel().getZoom() * 3 / 2, (radius - 1.5) * 2 * LevelManager.getLevel().getZoom(), (radius - 1.5) * 2 * LevelManager.getLevel().getZoom());

            if (this == Main.getSelected()) {
                graphicsContext.setStroke(Renderer.selectionOutline);
                graphicsContext.setLineWidth(1);
                graphicsContext.setLineDashes(3);

                graphicsContext.strokeRect(screenX, screenY, radius * 2 * LevelManager.getLevel().getZoom(), radius * 2 * LevelManager.getLevel().getZoom());

                graphicsContext.setLineDashes(0);
                graphicsContext.setStroke(Renderer.selectionOutline);
                graphicsContext.setLineWidth(1);

                graphicsContext.strokeRect(screenX - 4, screenY + radius * LevelManager.getLevel().getZoom() - 4, 8, 8);
                graphicsContext.strokeRect(screenX + radius * LevelManager.getLevel().getZoom() - 4, screenY + radius * 2 * LevelManager.getLevel().getZoom() - 4, 8, 8);
                graphicsContext.strokeRect(screenX + radius * LevelManager.getLevel().getZoom() - 4, screenY - 4, 8, 8);
                graphicsContext.strokeRect(screenX + radius * 2 * LevelManager.getLevel().getZoom() - 4, screenY + radius * LevelManager.getLevel().getZoom() - 4, 8, 8);
            }
        }
    }

    @Override
    public DragSettings mouseIntersection(double mX2, double mY2) {
        if (LevelManager.getLevel().isShowGeometry()) {

            Position pos = Position.parse(getAttribute("pos"));

            double x2 = pos.getX();
            double y2 = pos.getY();

            double radius = Double.parseDouble(getAttribute("radius"));

            double radius1 = radius - 3;
            double radius2 = radius;

            double distance = (mX2 - x2) * (mX2 - x2) + (mY2 + y2) * (mY2 + y2);
            if (distance < radius2 * radius2 && distance > radius1 * radius1) {
                DragSettings dragSettings = new DragSettings(DragSettings.MOVE);
                dragSettings.setInitialSourceX(mX2 - pos.getX());
                dragSettings.setInitialSourceY(mY2 + pos.getY());
                return dragSettings;
            } else {
                return new DragSettings(DragSettings.NONE);
            }
        } else {
            return new DragSettings(DragSettings.NONE);
        }
    }

    @Override
    public DragSettings mouseIntersectingCorners(double mX2, double mY2) {
        if (LevelManager.getLevel().isShowGeometry()) {

            Position pos = Position.parse(getAttribute("pos"));

            double x2 = pos.getX();
            double y2 = pos.getY();

            double radius = getDouble("radius");

            Point2D left = new Point2D(x2 - radius, y2);
            Point2D top = new Point2D(x2, y2 + radius);
            Point2D right = new Point2D(x2 + radius, y2);
            Point2D bottom = new Point2D(x2, y2 - radius);
            top = new Point2D((top.getX()), (-top.getY()));
            left = new Point2D((left.getX()), (-left.getY()));
            right = new Point2D((right.getX()), (-right.getY()));
            bottom = new Point2D((bottom.getX()), (-bottom.getY()));
            double distance = 4 / LevelManager.getLevel().getZoom();

            DragSettings resizeSettings = new DragSettings(DragSettings.RESIZE);

            boolean resize = false;

            if (mX2 > left.getX() - distance && mX2 < left.getX() + distance && mY2 > left.getY() - distance && mY2 < left.getY() + distance) {
                resize = true;
            }
            if (mX2 > top.getX() - distance && mX2 < top.getX() + distance && mY2 > top.getY() - distance && mY2 < top.getY() + distance) {
                resize = true;
            }
            if (mX2 > right.getX() - distance && mX2 < right.getX() + distance && mY2 > right.getY() - distance && mY2 < right.getY() + distance) {
                resize = true;
            }
            if (mX2 > bottom.getX() - distance && mX2 < bottom.getX() + distance && mY2 > bottom.getY() - distance && mY2 < bottom.getY() + distance) {
                resize = true;
            }

            if (resize) {
                resizeSettings.setInitialSourceX(0);
                resizeSettings.setInitialSourceY(0);
                resizeSettings.setAnchorX(0);
                resizeSettings.setAnchorY(0);
                return resizeSettings;
            }
        }
        return new DragSettings(DragSettings.NONE);
    }

    @Override
    public void dragFromMouse(double mouseX, double mouseY, double dragSourceX, double dragSourceY){
        setAttribute("pos", (mouseX - dragSourceX) + "," + (dragSourceY - mouseY));
    }

    @Override
    public void resizeFromMouse(double mouseX, double mouseY, double resizeDragSourceX, double resizeDragSourceY, double resizeDragAnchorX, double resizeDragAnchorY){
        Position pos = Position.parse(getAttribute("pos"));
        setAttribute("radius", Math.sqrt((mouseX - pos.getX()) * (mouseX - pos.getX()) + (mouseY + pos.getY()) * (mouseY + pos.getY())));
    }
}
