package com.WorldOfGoo.Scene;

import com.WooGLEFX.File.GlobalResourceManager;
import com.WooGLEFX.Engine.Main;
import com.WooGLEFX.Engine.Renderer;
import com.WooGLEFX.EditorObjects.GeometryImage;
import com.WooGLEFX.GUI.Alarms;
import com.WooGLEFX.Structures.*;
import com.WooGLEFX.Structures.SimpleStructures.DragSettings;
import com.WooGLEFX.Structures.SimpleStructures.MetaEditorAttribute;
import com.WooGLEFX.Structures.SimpleStructures.Position;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;

import java.io.FileNotFoundException;

public class Circle extends EditorObject {

    private final GeometryImage image = new GeometryImage(this);

    public Circle(EditorObject _parent) {
        super(_parent);
        setRealName("circle");
        addAttribute("id", "", InputField.ANY, true);
        addAttribute("mass", "0", InputField.NUMBER, false);
        addAttribute("static", "true", InputField.FLAG, false);
        addAttribute("tag", "", InputField.TAG, false);
        addAttribute("material", "", InputField.MATERIAL, false);
        addAttribute("contacts", "", InputField.FLAG, false);
        addAttribute("x", "0", InputField.NUMBER, true);
        addAttribute("y", "0", InputField.NUMBER, true);
        addAttribute("radius", "75", InputField.NUMBER, true);
        addAttribute("break", "", InputField.NUMBER, false);
        addAttribute("image", "", InputField.IMAGE, false);
        addAttribute("imagepos", "0,0", InputField.POSITION, false);
        addAttribute("imagerot", "0", InputField.NUMBER, false);
        addAttribute("imagescale", "1,1", InputField.POSITION, false);
        addAttribute("rotspeed", "", InputField.NUMBER, false);
        addAttribute("nogeomcollisions", "", InputField.FLAG, false);
        setNameAttribute(getAttribute2("id"));
        setMetaAttributes(MetaEditorAttribute.parse("id,x,y,radius,Geometry<static,mass,material,tag,break,rotspeed,contacts,nogeomcollisions>?Image<image,imagepos,imagerot,imagescale>"));
    }

    @Override
    public void update() {
        if (!getAttribute("image").equals("")) {
            try {
                image.setImage(GlobalResourceManager.getImage(getAttribute("image"), Main.getLevel().getVersion()));
            } catch (FileNotFoundException e) {
                Alarms.errorMessage(e);
            }
        }

        setChangeListener("image", (observable, oldValue, newValue) -> {
            if (!getAttribute("image").equals("")) {
                try {
                    image.setImage(GlobalResourceManager.getImage(getAttribute("image"), Main.getLevel().getVersion()));
                } catch (FileNotFoundException e) {
                    Alarms.errorMessage(e);
                }
            }
        });
    }

    @Override
    public DragSettings mouseIntersection(double mX2, double mY2) {
        if (Main.getLevel().isShowGeometry()) {
            double x2 = Double.parseDouble(getAttribute("x"));
            double y2 = Double.parseDouble(getAttribute("y"));

            double radius = Double.parseDouble(getAttribute("radius"));

            if (getParent() instanceof Compositegeom) {
                Point2D rotated = rotate(new Point2D(x2, y2), getParent().getDouble("rotation"), new Point2D(0, 0));
                x2 = rotated.getX();
                y2 = rotated.getY();

                x2 += Double.parseDouble(getParent().getAttribute("x"));
                y2 += Double.parseDouble(getParent().getAttribute("y"));
            }

            if ((mX2 - x2) * (mX2 - x2) + (mY2 + y2) * (mY2 + y2) < radius * radius) {
                DragSettings dragSettings = new DragSettings(DragSettings.MOVE);
                if (getParent() instanceof Compositegeom) {
                    dragSettings.setInitialSourceX(mX2 - x2 + getParent().getDouble("x"));
                    dragSettings.setInitialSourceY(mY2 + y2 - getParent().getDouble("y"));
                } else {
                    dragSettings.setInitialSourceX(mX2 - x2);
                    dragSettings.setInitialSourceY(mY2 + y2);
                }
                return dragSettings;
            } else {
                return new DragSettings(DragSettings.NONE);
            }
        } else {
            return new DragSettings(DragSettings.NONE);
        }
    }

    @Override
    public void draw(GraphicsContext graphicsContext, GraphicsContext imageGraphicsContext) throws FileNotFoundException {
        graphicsContext.save();
        if (Main.getLevel().isShowGeometry()) {
            double x2 = Double.parseDouble(getAttribute("x"));
            double y2 = Double.parseDouble(getAttribute("y"));

            double radius = Double.parseDouble(getAttribute("radius"));

            if (getParent() instanceof Compositegeom) {
                Point2D rotated = rotate(new Point2D(x2, y2), getParent().getDouble("rotation"), new Point2D(0, 0));
                x2 = rotated.getX();
                y2 = rotated.getY();

                x2 += Double.parseDouble(getParent().getAttribute("x"));
                y2 += Double.parseDouble(getParent().getAttribute("y"));
            }

            double screenX = (x2) * Main.getLevel().getZoom() + Main.getLevel().getOffsetX() - radius * Main.getLevel().getZoom();
            double screenY = (-y2) * Main.getLevel().getZoom() + Main.getLevel().getOffsetY() - radius * Main.getLevel().getZoom();

            graphicsContext.setFill(Renderer.transparentBlue);
            graphicsContext.fillOval(screenX + Main.getLevel().getZoom() / 2, screenY + Main.getLevel().getZoom() / 2, (radius - 0.5) * 2 * Main.getLevel().getZoom(), (radius - 0.5) * 2 * Main.getLevel().getZoom());
            graphicsContext.setStroke(Renderer.solidBlue);
            graphicsContext.setLineWidth(Main.getLevel().getZoom());
            graphicsContext.strokeOval(screenX + Main.getLevel().getZoom() / 2, screenY + Main.getLevel().getZoom() / 2, (radius - 0.5) * 2 * Main.getLevel().getZoom(), (radius - 0.5) * 2 * Main.getLevel().getZoom());

            if (this == Main.getSelected()) {
                graphicsContext.setStroke(Renderer.selectionOutline2);
                graphicsContext.setLineWidth(1);
                graphicsContext.setLineDashes(3);
                graphicsContext.setLineDashOffset(0);
                graphicsContext.strokeRect(screenX, screenY, radius * 2 * Main.getLevel().getZoom(), radius * 2 * Main.getLevel().getZoom());
                graphicsContext.setStroke(Renderer.selectionOutline);
                graphicsContext.setLineWidth(1);
                graphicsContext.setLineDashOffset(3);
                graphicsContext.strokeRect(screenX, screenY, radius * 2 * Main.getLevel().getZoom(), radius * 2 * Main.getLevel().getZoom());
                graphicsContext.setLineDashes(0);

                graphicsContext.strokeRect(screenX - 4, screenY + radius * Main.getLevel().getZoom() - 4, 8, 8);
                graphicsContext.strokeRect(screenX + radius * Main.getLevel().getZoom() - 4, screenY + radius * 2 * Main.getLevel().getZoom() - 4, 8, 8);
                graphicsContext.strokeRect(screenX + radius * Main.getLevel().getZoom() - 4, screenY - 4, 8, 8);
                graphicsContext.strokeRect(screenX + radius * 2 * Main.getLevel().getZoom() - 4, screenY + radius * Main.getLevel().getZoom() - 4, 8, 8);
            }
        }

        if (!getAttribute("image").equals("")) {
            image.draw(graphicsContext, imageGraphicsContext);
        }
        graphicsContext.restore();
    }

    @Override
    public DragSettings mouseIntersectingCorners(double mX2, double mY2) {
        if (Main.getLevel().isShowGeometry()) {
            double x2 = Double.parseDouble(getAttribute("x"));
            double y2 = Double.parseDouble(getAttribute("y"));

            double radius = Double.parseDouble(getAttribute("radius"));

            if (getParent() instanceof Compositegeom) {
                Point2D rotated = rotate(new Point2D(x2, y2), getParent().getDouble("rotation"), new Point2D(0, 0));
                x2 = rotated.getX();
                y2 = rotated.getY();

                x2 += Double.parseDouble(getParent().getAttribute("x"));
                y2 += Double.parseDouble(getParent().getAttribute("y"));
            }

            Point2D left = new Point2D(x2 - radius, y2);
            Point2D top = new Point2D(x2, y2 + radius);
            Point2D right = new Point2D(x2 + radius, y2);
            Point2D bottom = new Point2D(x2, y2 - radius);
            top = new Point2D((top.getX()), (-top.getY()));
            left = new Point2D((left.getX()), (-left.getY()));
            right = new Point2D((right.getX()), (-right.getY()));
            bottom = new Point2D((bottom.getX()), (-bottom.getY()));
            double distance = 4 / Main.getLevel().getZoom();

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
        if (image.getImage() != null) {
            return image.mouseIntersectingCorners(mX2, mY2);
        }
        return new DragSettings(DragSettings.NONE);
    }

    @Override
    public DragSettings mouseImageIntersection(double mX2, double mY2) {

        if (Main.getLevel().isShowGraphics() && image.getImage() != null) {

            Position pos = Position.parse(getAttribute("imagepos"));
            double x = pos.getX();
            double y = pos.getY();

            double rotation = Double.parseDouble(getAttribute("imagerot"));

            Position scale = Position.parse(getAttribute("imagescale"));
            double scalex = scale.getX();
            double scaley = scale.getY();

            Point2D rotated = rotate(new Point2D(mX2, mY2), Math.toRadians(rotation), new Point2D(x, -y));

            double mX = rotated.getX();
            double mY = rotated.getY();

            if (mX > x - image.getImage().getWidth() * scalex / 2 && mX < x + image.getImage().getWidth() * scalex / 2 && mY > -y - image.getImage().getHeight() * scaley / 2 && mY < -y + image.getImage().getHeight() * scaley / 2) {
                double goodX = (mX - (x - image.getImage().getWidth() * scalex / 2)) / scalex;
                double goodY = (mY - (-y - image.getImage().getHeight() * scaley / 2)) / scaley;
                int pixel = image.getImage().getPixelReader().getArgb((int) goodX, (int) goodY);
                if (pixel >> 24 != 0) {
                    DragSettings dragSettings = new DragSettings(DragSettings.MOVE);
                    dragSettings.setInitialSourceX(mX - x);
                    dragSettings.setInitialSourceY(mY + y);
                    dragSettings.setDraggingImage(true);
                    return dragSettings;
                }
            }
        }
        return new DragSettings(DragSettings.NONE);
    }

    @Override
    public void dragFromMouse(double mouseX, double mouseY, double dragSourceX, double dragSourceY) {
        if (Main.getDragSettings().isDraggingImage()) {
            image.dragFromMouse(mouseX, mouseY, dragSourceX, dragSourceY);
        } else {

            if (getParent() instanceof Compositegeom) {
                Point2D rotated = rotate(new Point2D(mouseX - dragSourceX, mouseY - dragSourceY), getParent().getDouble("rotation"), new Point2D(0, 0));
                setAttribute("x", rotated.getX());
                setAttribute("y", -rotated.getY());
            } else {
                setAttribute("x", mouseX - dragSourceX);
                setAttribute("y", dragSourceY - mouseY);
            }
        }
    }

    @Override
    public void resizeFromMouse(double mouseX, double mouseY, double resizeDragSourceX, double resizeDragSourceY, double resizeDragAnchorX, double resizeDragAnchorY) {
        if (Main.getDragSettings().isDraggingImage()) {
            image.resizeFromMouse(mouseX, mouseY, resizeDragSourceX, resizeDragSourceY, resizeDragAnchorX, resizeDragAnchorY);
        } else {
            double x2 = Double.parseDouble(getAttribute("x"));
            double y2 = -Double.parseDouble(getAttribute("y"));

            if (getParent() instanceof Compositegeom) {
                Point2D rotated = rotate(new Point2D(x2, -y2), getParent().getDouble("rotation"), new Point2D(0, 0));
                x2 = rotated.getX();
                y2 = -rotated.getY();
                x2 += Double.parseDouble(getParent().getAttribute("x"));
                y2 -= Double.parseDouble(getParent().getAttribute("y"));
            }

            setAttribute("radius", Math.sqrt((mouseX - x2) * (mouseX - x2) + (mouseY - y2) * (mouseY - y2)));
        }
    }

    @Override
    public void rotateFromMouse(double mouseX, double mouseY, double rotateAngleOffset) {
        if (Main.getDragSettings().isDraggingImage()) {
            image.rotateFromMouse(mouseX, mouseY, rotateAngleOffset);
        }
    }
}
