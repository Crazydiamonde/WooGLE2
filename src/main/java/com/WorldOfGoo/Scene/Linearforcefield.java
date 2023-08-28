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
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;

public class Linearforcefield extends EditorObject {
    public Linearforcefield(EditorObject _parent) {
        super(_parent);
        setRealName("linearforcefield");
        addAttribute("id", "", InputField.ANY, true);
        addAttribute("type", "gravity", InputField.ANY, true);
        addAttribute("center", "0,0", InputField.POSITION, false);
        addAttribute("width", "0", InputField.NUMBER, false);
        addAttribute("height", "0", InputField.NUMBER, false);
        addAttribute("force", "0,-10", InputField.POSITION, true);
        addAttribute("dampeningfactor", "0", InputField.NUMBER, true);
        addAttribute("rotationaldampeningfactor", "", InputField.NUMBER, false);
        addAttribute("antigrav", "false", InputField.FLAG, true);
        addAttribute("geomonly", "", InputField.FLAG, false);
        addAttribute("enabled", "true", InputField.FLAG, false);
        addAttribute("water", "false", InputField.FLAG, false);
        addAttribute("color", "", InputField.COLOR, false);
        addAttribute("depth", "", InputField.NUMBER, false);
        setNameAttribute(getAttribute2("id"));
        setMetaAttributes(MetaEditorAttribute.parse("id,center,width,height,Force Field<type,force,dampeningfactor,rotationaldampeningfactor,antigrav,geomonly,enabled>Water<water,color,depth>"));
    }

    @Override
    public void draw(GraphicsContext graphicsContext, GraphicsContext imageGraphicsContext) {
        if (Main.getLevel().isShowForcefields()) {
            Position center = Position.parse(getAttribute("center"));

            Position force = Position.parse(getAttribute("force"));

            double angle = Renderer.angleTo(new Point2D(0, 0), new Point2D(force.getX(), force.getY()));

            double x2 = center.getX();
            double y2 = center.getY();

            double width = Double.parseDouble(getAttribute("width"));
            double height = Double.parseDouble(getAttribute("height"));

            if (getParent() instanceof Compositegeom) {
                x2 += Double.parseDouble(getParent().getAttribute("x"));
                y2 += Double.parseDouble(getParent().getAttribute("y"));
            }

            double woag = 3;

            Point2D topRight2 = new Point2D(x2 + width / 2, y2 - height / 2).multiply(Main.getLevel().getZoom());
            Point2D topLeft2 = new Point2D(x2 - width / 2, y2 - height / 2).multiply(Main.getLevel().getZoom());
            Point2D bottomLeft2 = new Point2D(x2 - width / 2, y2 + height / 2).multiply(Main.getLevel().getZoom());
            Point2D bottomRight2 = new Point2D(x2 + width / 2, y2 + height / 2).multiply(Main.getLevel().getZoom());

            Point2D topRight = new Point2D(x2 + width / 2 - woag, y2 - height / 2 + woag).multiply(Main.getLevel().getZoom());
            Point2D topLeft = new Point2D(x2 - width / 2 + woag, y2 - height / 2 + woag).multiply(Main.getLevel().getZoom());
            Point2D bottomLeft = new Point2D(x2 - width / 2 + woag, y2 + height / 2 - woag).multiply(Main.getLevel().getZoom());
            Point2D bottomRight = new Point2D(x2 + width / 2 - woag, y2 + height / 2 - woag).multiply(Main.getLevel().getZoom());

            topRight = new Point2D((topRight.getX()) + Main.getLevel().getOffsetX(), (-topRight.getY()) + Main.getLevel().getOffsetY());
            topLeft = new Point2D((topLeft.getX()) + Main.getLevel().getOffsetX(), (-topLeft.getY()) + Main.getLevel().getOffsetY());
            bottomLeft = new Point2D((bottomLeft.getX()) + Main.getLevel().getOffsetX(), (-bottomLeft.getY()) + Main.getLevel().getOffsetY());
            bottomRight = new Point2D((bottomRight.getX()) + Main.getLevel().getOffsetX(), (-bottomRight.getY()) + Main.getLevel().getOffsetY());

            topRight2 = new Point2D((topRight2.getX()) + Main.getLevel().getOffsetX(), (-topRight2.getY()) + Main.getLevel().getOffsetY());
            topLeft2 = new Point2D((topLeft2.getX()) + Main.getLevel().getOffsetX(), (-topLeft2.getY()) + Main.getLevel().getOffsetY());
            bottomLeft2 = new Point2D((bottomLeft2.getX()) + Main.getLevel().getOffsetX(), (-bottomLeft2.getY()) + Main.getLevel().getOffsetY());
            bottomRight2 = new Point2D((bottomRight2.getX()) + Main.getLevel().getOffsetX(), (-bottomRight2.getY()) + Main.getLevel().getOffsetY());

            //graphicsContext.setFill(_Level.transparentBlue);
            //graphicsContext.fillPolygon(new double[]{topRight.getX(), topLeft.getX(), bottomLeft.getX(), bottomRight.getX()}, new double[]{topRight.getY(), topLeft.getY(), bottomLeft.getY(), bottomRight.getY()}, 4);
            graphicsContext.setStroke(Renderer.mechanics);
            graphicsContext.setLineWidth(Main.getLevel().getZoom() * woag * 2);
            if (width != 0 && height != 0) {
                graphicsContext.strokeLine(topRight.getX(), topRight.getY(), topLeft.getX(), topLeft.getY());
                graphicsContext.strokeLine(topLeft.getX(), topLeft.getY(), bottomLeft.getX(), bottomLeft.getY());
                graphicsContext.strokeLine(bottomLeft.getX(), bottomLeft.getY(), bottomRight.getX(), bottomRight.getY());
                graphicsContext.strokeLine(bottomRight.getX(), bottomRight.getY(), topRight.getX(), topRight.getY());
            }

            if (force.getX() != 0 || force.getY() != 0) {
                double screenX = x2 * Main.getLevel().getZoom() + Main.getLevel().getOffsetX();
                double screenY = -y2 * Main.getLevel().getZoom() + Main.getLevel().getOffsetY();

                double forceScale = 20;

                graphicsContext.setStroke(new LinearGradient(screenX, screenY, screenX + force.getX() * Main.getLevel().getZoom() * forceScale, screenY - force.getY() * Main.getLevel().getZoom() * forceScale, false, CycleMethod.NO_CYCLE, Renderer.stops));
                graphicsContext.strokeLine(screenX, screenY, screenX + force.getX() * Main.getLevel().getZoom() * forceScale, screenY - force.getY() * Main.getLevel().getZoom() * forceScale);
            }

            if (this == Main.getSelected()) {
                graphicsContext.setStroke(Renderer.selectionOutline);
                graphicsContext.setLineWidth(1);
                graphicsContext.setLineDashes(3);

                graphicsContext.strokePolygon(new double[]{topRight2.getX(), topLeft2.getX(), bottomLeft2.getX(), bottomRight2.getX()}, new double[]{topRight2.getY(), topLeft2.getY(), bottomLeft2.getY(), bottomRight2.getY()}, 4);

                double forceMagnitude = Math.sqrt(force.getX() * force.getX() + force.getY() * force.getY()) * 20;

                Point2D forceTopLeft = new Point2D(x2 - woag, y2 - woag);
                Point2D forceTopRight = new Point2D(x2 + forceMagnitude + woag, y2 - woag);
                Point2D forceBottomLeft = new Point2D(x2 - woag, y2 + woag);
                Point2D forceBottomRight = new Point2D(x2 + forceMagnitude + woag, y2 + woag);
                Point2D forceRight = new Point2D(x2 + forceMagnitude, y2);

                forceTopLeft = rotate(forceTopLeft, -angle, new Point2D(x2, y2)).multiply(Main.getLevel().getZoom());
                forceTopRight = rotate(forceTopRight, -angle, new Point2D(x2, y2)).multiply(Main.getLevel().getZoom());
                forceBottomLeft = rotate(forceBottomLeft, -angle, new Point2D(x2, y2)).multiply(Main.getLevel().getZoom());
                forceBottomRight = rotate(forceBottomRight, -angle, new Point2D(x2, y2)).multiply(Main.getLevel().getZoom());
                forceRight = rotate(forceRight, -angle, new Point2D(x2, y2)).multiply(Main.getLevel().getZoom());

                forceTopRight = new Point2D((forceTopRight.getX()) + Main.getLevel().getOffsetX(), (-forceTopRight.getY()) + Main.getLevel().getOffsetY());
                forceTopLeft = new Point2D((forceTopLeft.getX()) + Main.getLevel().getOffsetX(), (-forceTopLeft.getY()) + Main.getLevel().getOffsetY());
                forceBottomLeft = new Point2D((forceBottomLeft.getX()) + Main.getLevel().getOffsetX(), (-forceBottomLeft.getY()) + Main.getLevel().getOffsetY());
                forceBottomRight = new Point2D((forceBottomRight.getX()) + Main.getLevel().getOffsetX(), (-forceBottomRight.getY()) + Main.getLevel().getOffsetY());
                forceRight = new Point2D((forceRight.getX()) + Main.getLevel().getOffsetX(), (-forceRight.getY()) + Main.getLevel().getOffsetY());

                graphicsContext.strokePolygon(new double[]{forceTopRight.getX(), forceTopLeft.getX(), forceBottomLeft.getX(), forceBottomRight.getX()}, new double[]{forceTopRight.getY(), forceTopLeft.getY(), forceBottomLeft.getY(), forceBottomRight.getY()}, 4);

                graphicsContext.setLineDashes(0);

                graphicsContext.setLineWidth(1);

                if (width != 0 && height != 0) {
                    graphicsContext.strokeRect(topRight2.getX() - 4, topRight2.getY() - 4, 8, 8);
                    graphicsContext.strokeRect(topLeft2.getX() - 4, topLeft2.getY() - 4, 8, 8);
                    graphicsContext.strokeRect(bottomLeft2.getX() - 4, bottomLeft2.getY() - 4, 8, 8);
                    graphicsContext.strokeRect(bottomRight2.getX() - 4, bottomRight2.getY() - 4, 8, 8);
                }
                if (force.getX() != 0 || force.getY() != 0) {
                    graphicsContext.strokeRect(forceRight.getX() - 4, forceRight.getY() - 4, 8, 8);
                }
            }
        }
    }

    @Override
    public DragSettings mouseIntersection(double mX2, double mY2) {
        if (Main.getLevel().isShowForcefields()) {
            Position center = Position.parse(getAttribute("center"));

            double x2 = center.getX();
            double y2 = -center.getY();

            double drawWidth = 6;

            double width1 = Double.parseDouble(getAttribute("width"));
            double height1 = Double.parseDouble(getAttribute("height"));

            double width2 = Double.parseDouble(getAttribute("width")) - drawWidth * 2;
            double height2 = Double.parseDouble(getAttribute("height")) - drawWidth * 2;

            if (width1 != 0 && height1 != 0) {
                if (mX2 > x2 - width1 / 2 && mX2 < x2 + width1 / 2 && mY2 > y2 - height1 / 2 && mY2 < y2 + height1 / 2 &&
                        !(mX2 > x2 - width2 / 2 && mX2 < x2 + width2 / 2 && mY2 > y2 - height2 / 2 && mY2 < y2 + height2 / 2)) {
                    DragSettings dragSettings = new DragSettings(DragSettings.MOVE);
                    dragSettings.setInitialSourceX(mX2 - center.getX());
                    dragSettings.setInitialSourceY(mY2 + center.getY());
                    return dragSettings;
                }
            }

            Position force = Position.parse(getAttribute("force"));

            double angle = Renderer.angleTo(new Point2D(0, 0), new Point2D(force.getX(), force.getY()));

            Point2D rotatedMouse = rotate(new Point2D(mX2, mY2), -angle, new Point2D(x2, y2));

            double forceScale = 20;

            double forceMagnitude = Math.sqrt(force.getX() * force.getX() + force.getY() * force.getY()) * forceScale;

            if (rotatedMouse.getX() > x2 - drawWidth / 2 && rotatedMouse.getX() < x2 + forceMagnitude + drawWidth / 2 && rotatedMouse.getY() > y2 - drawWidth / 2 && rotatedMouse.getY() < y2 + drawWidth / 2) {
                DragSettings dragSettings = new DragSettings(DragSettings.MOVE);
                dragSettings.setInitialSourceX(mX2 - center.getX());
                dragSettings.setInitialSourceY(mY2 + center.getY());
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

        if (Main.getLevel().isShowForcefields()) {
            Position center = Position.parse(getAttribute("center"));

            double x2 = center.getX();
            double y2 = center.getY();

            double width = Double.parseDouble(getAttribute("width"));
            double height = Double.parseDouble(getAttribute("height"));

            Position force = Position.parse(getAttribute("force"));

            double angle = Renderer.angleTo(new Point2D(0, 0), new Point2D(force.getX(), force.getY()));

            double forceScale = 20;

            double forceMagnitude = Math.sqrt(force.getX() * force.getX() + force.getY() * force.getY()) * forceScale;

            Point2D topRight = new Point2D(x2 + width / 2, y2 + height / 2);
            Point2D topLeft = new Point2D(x2 - width / 2, y2 + height / 2);
            Point2D bottomLeft = new Point2D(x2 - width / 2, y2 - height / 2);
            Point2D bottomRight = new Point2D(x2 + width / 2, y2 - height / 2);
            Point2D right = new Point2D(x2 + forceMagnitude, y2);
            topRight = new Point2D((topRight.getX()), (-topRight.getY()));
            topLeft = new Point2D((topLeft.getX()), (-topLeft.getY()));
            bottomLeft = new Point2D((bottomLeft.getX()), (-bottomLeft.getY()));
            bottomRight = new Point2D((bottomRight.getX()), (-bottomRight.getY()));
            right = new Point2D((right.getX()), (-right.getY()));
            double distance = 4 / Main.getLevel().getZoom();

            double dragSourceX = 0;
            double dragSourceY = 0;
            double dragAnchorX = 0;
            double dragAnchorY = 0;

            DragSettings resizeSettings = new DragSettings(DragSettings.RESIZE);
            DragSettings anchorSettings = new DragSettings(DragSettings.SETANCHOR);

            boolean resize = false;
            boolean anchor = false;

            if (width != 0 && height != 0 && mX2 > topLeft.getX() - distance && mX2 < topLeft.getX() + distance && mY2 > topLeft.getY() - distance && mY2 < topLeft.getY() + distance) {
                resize = true;
                dragSourceX = x2 - width / 2;
                dragSourceY = -y2 - height / 2;
                dragAnchorX = x2 + width / 2;
                dragAnchorY = -y2 + height / 2;
            }
            if (width != 0 && height != 0 && mX2 > topRight.getX() - distance && mX2 < topRight.getX() + distance && mY2 > topRight.getY() - distance && mY2 < topRight.getY() + distance) {
                resize = true;
                dragSourceX = x2 + width / 2;
                dragSourceY = -y2 - height / 2;
                dragAnchorX = x2 - width / 2;
                dragAnchorY = -y2 + height / 2;
            }
            if (width != 0 && height != 0 && mX2 > bottomLeft.getX() - distance && mX2 < bottomLeft.getX() + distance && mY2 > bottomLeft.getY() - distance && mY2 < bottomLeft.getY() + distance) {
                resize = true;
                dragSourceX = x2 - width / 2;
                dragSourceY = -y2 + height / 2;
                dragAnchorX = x2 + width / 2;
                dragAnchorY = -y2 - height / 2;
            }
            if (width != 0 && height != 0 && mX2 > bottomRight.getX() - distance && mX2 < bottomRight.getX() + distance && mY2 > bottomRight.getY() - distance && mY2 < bottomRight.getY() + distance) {
                resize = true;
                dragSourceX = x2 + width / 2;
                dragSourceY = -y2 + height / 2;
                dragAnchorX = x2 - width / 2;
                dragAnchorY = -y2 - height / 2;
            }
            Point2D rotated = rotate(new Point2D(mX2, mY2), -angle, new Point2D(x2, -y2));
            if (forceMagnitude != 0 && rotated.getX() > right.getX() - 4 / Main.getLevel().getZoom() && rotated.getX() < right.getX() + 4 / Main.getLevel().getZoom() && rotated.getY() > right.getY() - 4 / Main.getLevel().getZoom() && rotated.getY() < right.getY() + 4 / Main.getLevel().getZoom()) {
                anchor = true;
                dragSourceX = x2;
                dragSourceY = -y2;
            }

            if (resize) {
                resizeSettings.setInitialSourceX(dragSourceX);
                resizeSettings.setInitialSourceY(dragSourceY);
                resizeSettings.setAnchorX(dragAnchorX);
                resizeSettings.setAnchorY(dragAnchorY);
                return resizeSettings;
            }
            if (anchor) {
                anchorSettings.setInitialSourceX(dragSourceX);
                anchorSettings.setInitialSourceY(dragSourceY);
                return anchorSettings;
            }
        }
        return new DragSettings(DragSettings.NONE);
    }

    @Override
    public void dragFromMouse(double mouseX, double mouseY, double dragSourceX, double dragSourceY) {
        setAttribute("center", (mouseX - dragSourceX) + "," + (dragSourceY - mouseY));
    }

    @Override
    public void setAnchor(double mouseX, double mouseY, double anchorStartX, double anchorStartY) {
        setAttribute("force", (mouseX - anchorStartX) / 20 + "," + (anchorStartY - mouseY) / 20);
    }

    @Override
    public void resizeFromMouse(double mouseX, double mouseY, double resizeDragSourceX, double resizeDragSourceY, double resizeDragAnchorX, double resizeDragAnchorY){
        Point2D center = new Point2D((mouseX + resizeDragAnchorX) / 2, (mouseY + resizeDragAnchorY) / 2);

        double newWidth;
        double newHeight;

        newWidth = Math.abs(resizeDragAnchorX - mouseX);
        newHeight = Math.abs(resizeDragAnchorY - mouseY);

        setAttribute("width", newWidth);
        setAttribute("height", newHeight);
        setAttribute("center", center.getX() + "," + -center.getY());
    }
}
