package com.WorldOfGoo.Scene;

import com.WooGLEFX.File.FileManager;
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

public class Compositegeom extends EditorObject {

    private final GeometryImage image = new GeometryImage(this);

    public Compositegeom(EditorObject _parent) {
        super(_parent);
        setRealName("compositegeom");
        addAttribute("id", "", InputField.ANY, true);
        addAttribute("x", "0", InputField.NUMBER, true);
        addAttribute("y", "0", InputField.NUMBER, true);
        addAttribute("rotation", "0", InputField.NUMBER, true);
        addAttribute("static", "true", InputField.FLAG, true);
        addAttribute("mass", "", InputField.NUMBER, false);
        addAttribute("tag", "", InputField.TAG, false);
        addAttribute("material", "rock", InputField.MATERIAL, true);
        addAttribute("break", "", InputField.NUMBER, false);
        addAttribute("image", "", InputField.IMAGE, false);
        addAttribute("imagepos", "0,0", InputField.POSITION, false);
        addAttribute("imagerot", "0", InputField.NUMBER, false);
        addAttribute("imagescale", "1,1", InputField.POSITION, false);
        addAttribute("rotspeed", "0", InputField.NUMBER, false);
        addAttribute("contacts", "", InputField.FLAG, false);
        addAttribute("nogeomcollisions", "", InputField.FLAG, false);
        setNameAttribute(getAttribute2("id"));
        setMetaAttributes(MetaEditorAttribute.parse("id,x,y,rotation,Geometry<static,mass,material,tag,break,rotspeed,contacts,nogeomcollisions>?Image<image,imagepos,imagerot,imagescale>"));
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

    public void draw(GraphicsContext graphicsContext, GraphicsContext imageGraphicsContext) throws FileNotFoundException {
        if (Main.getLevel().isShowGeometry()) {

            double size = 10 * Main.getLevel().getZoom();

            double screenX = Double.parseDouble(getAttribute("x")) * Main.getLevel().getZoom() + Main.getLevel().getOffsetX();
            double screenY = -Double.parseDouble(getAttribute("y")) * Main.getLevel().getZoom() + Main.getLevel().getOffsetY();

            graphicsContext.setLineWidth(Main.getLevel().getZoom() * 5);
            graphicsContext.setStroke(Renderer.compositeGeom);

            graphicsContext.strokeOval(screenX - size / 2, screenY - size / 2, size, size);

            if (this == Main.getSelected()) {
                double x2 = Double.parseDouble(getAttribute("x"));
                double y2 = Double.parseDouble(getAttribute("y"));

                double rotation = Double.parseDouble(getAttribute("rotation"));

                double width = 15;
                double height = 15;

                Point2D center = new Point2D(x2, y2);

                Point2D topRight2 = new Point2D(x2 + width / 2, y2 - height / 2).multiply(Main.getLevel().getZoom());
                Point2D topLeft2 = new Point2D(x2 - width / 2, y2 - height / 2).multiply(Main.getLevel().getZoom());
                Point2D bottomLeft2 = new Point2D(x2 - width / 2, y2 + height / 2).multiply(Main.getLevel().getZoom());
                Point2D bottomRight2 = new Point2D(x2 + width / 2, y2 + height / 2).multiply(Main.getLevel().getZoom());

                topRight2 = EditorObject.rotate(topRight2, rotation, center.multiply(Main.getLevel().getZoom()));
                topLeft2 = EditorObject.rotate(topLeft2, rotation, center.multiply(Main.getLevel().getZoom()));
                bottomLeft2 = EditorObject.rotate(bottomLeft2, rotation, center.multiply(Main.getLevel().getZoom()));
                bottomRight2 = EditorObject.rotate(bottomRight2, rotation, center.multiply(Main.getLevel().getZoom()));

                topRight2 = new Point2D((topRight2.getX()) + Main.getLevel().getOffsetX(), (-topRight2.getY()) + Main.getLevel().getOffsetY());
                topLeft2 = new Point2D((topLeft2.getX()) + Main.getLevel().getOffsetX(), (-topLeft2.getY()) + Main.getLevel().getOffsetY());
                bottomLeft2 = new Point2D((bottomLeft2.getX()) + Main.getLevel().getOffsetX(), (-bottomLeft2.getY()) + Main.getLevel().getOffsetY());
                bottomRight2 = new Point2D((bottomRight2.getX()) + Main.getLevel().getOffsetX(), (-bottomRight2.getY()) + Main.getLevel().getOffsetY());

                graphicsContext.setStroke(Renderer.selectionOutline2);
                graphicsContext.setLineWidth(1);
                graphicsContext.setLineDashes(3);
                graphicsContext.setLineDashOffset(0);
                graphicsContext.strokePolygon(new double[]{topRight2.getX(), topLeft2.getX(), bottomLeft2.getX(), bottomRight2.getX()}, new double[]{topRight2.getY(), topLeft2.getY(), bottomLeft2.getY(), bottomRight2.getY()}, 4);
                graphicsContext.setStroke(Renderer.selectionOutline);
                graphicsContext.setLineWidth(1);
                graphicsContext.setLineDashOffset(3);
                graphicsContext.strokePolygon(new double[]{topRight2.getX(), topLeft2.getX(), bottomLeft2.getX(), bottomRight2.getX()}, new double[]{topRight2.getY(), topLeft2.getY(), bottomLeft2.getY(), bottomRight2.getY()}, 4);
                graphicsContext.setLineDashes(0);

                graphicsContext.setLineWidth(1);

                Point2D middleLeft = new Point2D(x2 - width / 2, y2);
                Point2D middleRight = new Point2D(x2 + width / 2, y2);

                middleLeft = EditorObject.rotate(middleLeft, rotation, center);
                middleRight = EditorObject.rotate(middleRight, rotation, center);

                middleLeft = new Point2D((middleLeft.getX()) * Main.getLevel().getZoom() + Main.getLevel().getOffsetX(), (-middleLeft.getY()) * Main.getLevel().getZoom() + Main.getLevel().getOffsetY());
                middleRight = new Point2D((middleRight.getX()) * Main.getLevel().getZoom() + Main.getLevel().getOffsetX(), (-middleRight.getY()) * Main.getLevel().getZoom() + Main.getLevel().getOffsetY());

                graphicsContext.strokeOval(middleLeft.getX() - 4, middleLeft.getY() - 4, 8, 8);
                graphicsContext.strokeOval(middleRight.getX() - 4, middleRight.getY() - 4, 8, 8);
            }
        }

        if (!getAttribute("image").equals("")) {
            imageGraphicsContext.save();
            image.draw(graphicsContext, imageGraphicsContext);
            imageGraphicsContext.restore();
        }
    }

    @Override
    public DragSettings mouseIntersection(double mX2, double mY2) {
        if (Main.getLevel().isShowGeometry()) {

            double x2 = getDouble("x");
            double y2 = getDouble("y");

            double radius1 = 2.5;
            double radius2 = 7.5;

            double distance = (mX2 - x2) * (mX2 - x2) + (mY2 + y2) * (mY2 + y2);
            if (distance < radius2 * radius2 && distance > radius1 * radius1) {
                DragSettings dragSettings = new DragSettings(DragSettings.MOVE);
                dragSettings.setInitialSourceX(mX2 - x2);
                dragSettings.setInitialSourceY(mY2 + y2);
                return dragSettings;
            } else {
                return new DragSettings(DragSettings.NONE);
            }
        } else {
            return new DragSettings(DragSettings.NONE);
        }
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

            try {
                if (mX > x - image.getImage().getWidth() * scalex / 2 && mX < x + image.getImage().getWidth() * scalex / 2 && mY > -y - image.getImage().getHeight() * scaley / 2 && mY < -y + image.getImage().getHeight() * scaley / 2) {
                    double goodX = (mX - (x - image.getImage().getWidth() * scalex / 2)) / scalex;
                    double goodY = (mY - (-y - image.getImage().getHeight() * scaley / 2)) / scaley;
                    int pixel = image.getImage().getPixelReader().getArgb((int) goodX, (int) goodY);
                    if (pixel >> 24 != 0) {
                        DragSettings dragSettings = new DragSettings(DragSettings.MOVE);
                        dragSettings.setInitialSourceX(mX2 - x);
                        dragSettings.setInitialSourceY(mY2 + y);
                        dragSettings.setDraggingImage(true);
                        return dragSettings;
                    } else {
                        return new DragSettings(DragSettings.NONE);
                    }
                }
            } catch (Exception e) {
                Alarms.errorMessage(e);
            }
        }
        return new DragSettings(DragSettings.NONE);
    }

    @Override
    public DragSettings mouseIntersectingCorners(double mX2, double mY2) {
        if (image.getImage() != null) {
            return image.mouseIntersectingCorners(mX2, mY2);
        }
        return new DragSettings(DragSettings.NONE);
    }

    @Override
    public void dragFromMouse(double mouseX, double mouseY, double dragSourceX, double dragSourceY) {
        if (Main.getDragSettings().isDraggingImage()) {
            image.dragFromMouse(mouseX, mouseY, dragSourceX, dragSourceY);
        } else {
            setAttribute("x", mouseX - dragSourceX);
            setAttribute("y", dragSourceY - mouseY);
        }
    }

    @Override
    public void resizeFromMouse(double mouseX, double mouseY, double resizeDragSourceX, double resizeDragSourceY, double resizeDragAnchorX, double resizeDragAnchorY) {
        if (Main.getDragSettings().isDraggingImage()) {
            image.resizeFromMouse(mouseX, mouseY, resizeDragSourceX, resizeDragSourceY, resizeDragAnchorX, resizeDragAnchorY);
        }
    }

    @Override
    public void rotateFromMouse(double mouseX, double mouseY, double rotateAngleOffset) {
        if (Main.getDragSettings().isDraggingImage()) {
            image.rotateFromMouse(mouseX, mouseY, rotateAngleOffset);
        }
    }

    @Override
    public String[] getPossibleChildren() {
        return new String[]{"Circle", "Rectangle"};
    }
}
