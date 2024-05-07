package com.WorldOfGoo.Scene;

import com.WooGLEFX.Engine.Main;
import com.WooGLEFX.Engine.Renderer;
import com.WooGLEFX.Functions.LevelManager;
import com.WooGLEFX.Structures.*;
import com.WooGLEFX.Structures.SimpleStructures.DragSettings;
import com.WooGLEFX.Structures.SimpleStructures.MetaEditorAttribute;
import com.WooGLEFX.Structures.SimpleStructures.Position;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;

public class Line extends EditorObject {

    public Line(EditorObject _parent) {
        super(_parent);
        setRealName("line");
        addAttribute("id", "", InputField.ANY, true);
        addAttribute("static", "true", InputField.FLAG, true);
        addAttribute("tag", "", InputField.TAG, false);
        addAttribute("material", "rock", InputField.MATERIAL, true);
        addAttribute("anchor", "0,0", InputField.POSITION, true);
        addAttribute("normal", "1,0", InputField.POSITION, true);
        addAttribute("break", "", InputField.NUMBER, false);
        setNameAttribute(getAttribute2("id"));
        setMetaAttributes(MetaEditorAttribute.parse("id,anchor,normal,material,tag,break,"));
    }

    @Override
    public DragSettings mouseIntersection(double mX2, double mY2) {
        if (LevelManager.getLevel().isShowGeometry()) {
            Position anchor = Position.parse(getAttribute("anchor"));
            Position normal = Position.parse(getAttribute("normal"));
            double x = anchor.getX();
            double y = -anchor.getY();
            Point2D dir = new Point2D(normal.getX(), -normal.getY()).normalize();
            Point2D realDir = new Point2D(-dir.getY(), dir.getX());

            double x2 = x + realDir.getX();
            double y2 = y + realDir.getY();

            if ((Math.abs((y2 - y) * mX2 - (x2 - x) * mY2 + x2 * y - y2 * x) / Math.sqrt(Math.pow(y2 - y, 2) + Math.pow(x2 - x, 2))) < 2) {
                DragSettings dragSettings = new DragSettings(DragSettings.MOVE);
                dragSettings.setInitialSourceX(mX2 - anchor.getX());
                dragSettings.setInitialSourceY(mY2 + anchor.getY());
                return dragSettings;
            }

            double angle = Renderer.angleTo(new Point2D(0, 0), new Point2D(normal.getX(), normal.getY()));

            Point2D rotatedMouse = rotate(new Point2D(mX2, mY2), -angle, new Point2D(x, y));

            double forceScale = 50;

            double drawWidth = 4;

            double normalMagnitude = Math.sqrt(normal.getX() * normal.getX() + normal.getY() * normal.getY()) * forceScale;

            if (rotatedMouse.getX() > x - drawWidth / 2 && rotatedMouse.getX() < x + normalMagnitude + drawWidth / 2 && rotatedMouse.getY() > y - drawWidth / 2 && rotatedMouse.getY() < y + drawWidth / 2) {
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
    public void draw(GraphicsContext graphicsContext, GraphicsContext imageGraphicsContext){
        if (LevelManager.getLevel().isShowGeometry()) {
            Position anchor = Position.parse(getAttribute("anchor"));
            Position normal = Position.parse(getAttribute("normal"));
            Point2D dir = new Point2D(normal.getX(), -normal.getY()).normalize();
            Point2D realDir = new Point2D(-dir.getY(), dir.getX());

            double screenX = anchor.getX() * LevelManager.getLevel().getZoom() + LevelManager.getLevel().getOffsetX();
            double screenY = -anchor.getY() * LevelManager.getLevel().getZoom() + LevelManager.getLevel().getOffsetY();
            graphicsContext.setLineWidth(LevelManager.getLevel().getZoom() * 4);

            double dst = Math.max(10000 * LevelManager.getLevel().getZoom(), 10000);
            double dst2 = 50 * LevelManager.getLevel().getZoom();

            graphicsContext.setStroke(Renderer.solidBlue);
            graphicsContext.strokeLine(screenX - realDir.getX() * LevelManager.getLevel().getZoom() * 4 - dst * realDir.getX(),
                    screenY - realDir.getY() * LevelManager.getLevel().getZoom() * 4 - dst * realDir.getY(),
                    screenX - realDir.getX() * LevelManager.getLevel().getZoom() * 4 + dst * realDir.getX(),
                    screenY - realDir.getY() * LevelManager.getLevel().getZoom() * 4 + dst * realDir.getY());
            graphicsContext.strokeLine(screenX, screenY, screenX + dst2 * dir.getX(), screenY + dst2 * dir.getY());
            if (this == Main.getSelected()) {
                double screenX2 = screenX + (dst2 + 2 * LevelManager.getLevel().getZoom()) * dir.getX();
                double screenY2 = screenY + (dst2 + 2 * LevelManager.getLevel().getZoom()) * dir.getY();

                graphicsContext.setLineDashes(0);

                graphicsContext.setStroke(Renderer.selectionOutline);
                graphicsContext.setLineWidth(1);

                graphicsContext.strokeLine(screenX2 - realDir.getX() * LevelManager.getLevel().getZoom() * 4 - dst * realDir.getX(),
                        screenY2 - realDir.getY() * LevelManager.getLevel().getZoom() * 4 - dst * realDir.getY(),
                        screenX2 - realDir.getX() * LevelManager.getLevel().getZoom() * 4 + dst * realDir.getX(),
                        screenY2 - realDir.getY() * LevelManager.getLevel().getZoom() * 4 + dst * realDir.getY());

                graphicsContext.setStroke(Renderer.selectionOutline);
                graphicsContext.setLineWidth(1);
                graphicsContext.setLineDashes(3);

                double x2 = anchor.getX();
                double y2 = anchor.getY();
                double angle = Renderer.angleTo(new Point2D(0, 0), new Point2D(normal.getX(), normal.getY()));
                double normalMagnitude = Math.sqrt(normal.getX() * normal.getX() + normal.getY() * normal.getY()) * 50;

                Point2D forceRight = new Point2D(x2 + normalMagnitude, y2);

                forceRight = rotate(forceRight, -angle, new Point2D(x2, y2)).multiply(LevelManager.getLevel().getZoom());

                forceRight = new Point2D((forceRight.getX()) + LevelManager.getLevel().getOffsetX(), (-forceRight.getY()) + LevelManager.getLevel().getOffsetY());

                graphicsContext.setLineDashes(0);

                graphicsContext.setLineWidth(1);

                graphicsContext.strokeRect(forceRight.getX() - 4, forceRight.getY() - 4, 8, 8);
            }
        }
    }

    @Override
    public DragSettings mouseIntersectingCorners(double mX2, double mY2) {

        Position anchor = Position.parse(getAttribute("anchor"));
        double x2 = anchor.getX();
        double y2 = anchor.getY();

        double normalLength = 50;
        Position normal = Position.parse(getAttribute("normal"));
        double angle = Renderer.angleTo(new Point2D(0, 0), new Point2D(normal.getX(), normal.getY()));

        Point2D right = new Point2D(x2 + Math.sqrt(normal.getX() * normal.getX() + normal.getY() * normal.getY()) * normalLength, y2);
        right = new Point2D((right.getX()), (-right.getY()));

        Point2D rotated = rotate(new Point2D(mX2, mY2), -angle, new Point2D(x2, -y2));
        if (rotated.getX() > right.getX() - 4 / LevelManager.getLevel().getZoom() && rotated.getX() < right.getX() + 4 / LevelManager.getLevel().getZoom() && rotated.getY() > right.getY() - 4 / LevelManager.getLevel().getZoom() && rotated.getY() < right.getY() + 4 / LevelManager.getLevel().getZoom()) {
            DragSettings anchorSettings = new DragSettings(DragSettings.SETANCHOR);
            anchorSettings.setInitialSourceX(x2);
            anchorSettings.setInitialSourceY(-y2);
            return anchorSettings;
        }
        return new DragSettings(DragSettings.NONE);
    }

    @Override
    public void setAnchor(double mouseX, double mouseY, double anchorStartX, double anchorStartY) {
        Point2D normal = new Point2D((mouseX - anchorStartX) / 50, (anchorStartY - mouseY) / 50).normalize();
        setAttribute("normal", normal.getX() + "," + normal.getY());
    }

    @Override
    public void dragFromMouse(double mouseX, double mouseY, double dragSourceX, double dragSourceY) {
        setAttribute("anchor", (mouseX - dragSourceX) + "," + (dragSourceY - mouseY));
    }
}
