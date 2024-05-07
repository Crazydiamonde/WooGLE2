package com.WorldOfGoo.Scene;

import java.io.FileNotFoundException;

import com.WooGLEFX.EditorObjects.GeometryImage;
import com.WooGLEFX.Engine.Main;
import com.WooGLEFX.Engine.Renderer;
import com.WooGLEFX.File.GlobalResourceManager;
import com.WooGLEFX.Functions.LevelManager;
import com.WooGLEFX.GUI.Alarms;
import com.WooGLEFX.Structures.EditorObject;
import com.WooGLEFX.Structures.InputField;
import com.WooGLEFX.Structures.SimpleStructures.DragSettings;
import com.WooGLEFX.Structures.SimpleStructures.MetaEditorAttribute;

import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;

public class Rectangle extends EditorObject {

    private final GeometryImage image = new GeometryImage(this);

    public Rectangle(EditorObject _parent) {
        super(_parent);
        setRealName("rectangle");
        addAttribute("id", "", InputField.ANY, true);
        addAttribute("mass", "", InputField.NUMBER, false);
        addAttribute("static", "", InputField.FLAG, false);
        addAttribute("tag", "", InputField.ANY, false); //list of valid tags
        addAttribute("material", "", InputField.ANY, false); //valid material
        addAttribute("image", "", InputField.IMAGE, false);
        addAttribute("imagepos", "", InputField.POSITION, false);
        addAttribute("imagerot", "0", InputField.NUMBER, false);
        addAttribute("imagescale", "1,1", InputField.POSITION, false);
        addAttribute("contacts", "", InputField.FLAG, false);
        addAttribute("x", "0", InputField.NUMBER, true);
        addAttribute("y", "0", InputField.NUMBER, true);
        addAttribute("width", "100", InputField.NUMBER, true);
        addAttribute("height", "100", InputField.NUMBER, true);
        addAttribute("rotation", "0", InputField.NUMBER, true);
        addAttribute("break", "", InputField.NUMBER, false);
        addAttribute("rotspeed", "", InputField.NUMBER, false);
        addAttribute("collide", "", InputField.FLAG, false);
        addAttribute("nogeomcollisions", "", InputField.FLAG, false);
        setNameAttribute(getAttribute2("id"));
        setMetaAttributes(MetaEditorAttribute.parse("id,x,y,width,height,rotation,Geometry<static,mass,material,tag,break,rotspeed,contacts,collide,nogeomcollisions>?Image<image,imagepos,imagerot,imagescale>"));
    }

    @Override
    public void update() {
        setNameAttribute(getAttribute2("id"));
        if (!getAttribute("image").equals("")) {
            try {
                image.setImage(GlobalResourceManager.getImage(getAttribute("image"), LevelManager.getLevel().getVersion()));
            } catch (FileNotFoundException e) {
                Alarms.errorMessage(e);
            }
        }

        setChangeListener("image", (observable, oldValue, newValue) -> {
            if (!getAttribute("image").equals("")) {
                try {
                    image.setImage(GlobalResourceManager.getImage(getAttribute("image"), LevelManager.getLevel().getVersion()));
                } catch (FileNotFoundException e) {
                    Alarms.errorMessage(e);
                }
            }
        });
    }

    @Override
    public DragSettings mouseIntersection(double mX2, double mY2) {
        if (LevelManager.getLevel().isShowGeometry()) {
            double x2 = Double.parseDouble(getAttribute("x"));
            double y2 = Double.parseDouble(getAttribute("y"));

            double rotation = Double.parseDouble(getAttribute("rotation"));

            double width = Double.parseDouble(getAttribute("width"));
            double height = Double.parseDouble(getAttribute("height"));

            if (getParent() instanceof Compositegeom) {
                Point2D rotated = rotate(new Point2D(x2, y2), getParent().getDouble("rotation"), new Point2D(0, 0));
                x2 = rotated.getX();
                y2 = rotated.getY();

                x2 += Double.parseDouble(getParent().getAttribute("x"));
                y2 += Double.parseDouble(getParent().getAttribute("y"));

                rotation += getParent().getDouble("rotation");
            }

            Point2D rotated = rotate(new Point2D(mX2, mY2), rotation, new Point2D(x2, -y2));

            double mX = rotated.getX();
            double mY = rotated.getY();

            if (mX > x2 - width / 2 && mX < x2 + width / 2 && mY > -y2 - height / 2 && mY < -y2 + height / 2) {
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
    public void draw(GraphicsContext graphicsContext, GraphicsContext imageGraphicsContext) {
        if (LevelManager.getLevel().isShowGeometry()) {

            double x2 = Double.parseDouble(getAttribute("x"));
            double y2 = Double.parseDouble(getAttribute("y"));

            double rotation = Double.parseDouble(getAttribute("rotation"));

            double width = Double.parseDouble(getAttribute("width"));
            double height = Double.parseDouble(getAttribute("height"));

            if (getParent() instanceof Compositegeom) {
                Point2D rotated = rotate(new Point2D(x2, y2), getParent().getDouble("rotation"), new Point2D(0, 0));
                x2 = rotated.getX();
                y2 = rotated.getY();

                x2 += Double.parseDouble(getParent().getAttribute("x"));
                y2 += Double.parseDouble(getParent().getAttribute("y"));

                rotation += getParent().getDouble("rotation");
            }

            Point2D center = new Point2D(x2, y2);

            Point2D topRight2 = new Point2D(x2 + width / 2, y2 - height / 2).multiply(LevelManager.getLevel().getZoom());
            Point2D topLeft2 = new Point2D(x2 - width / 2, y2 - height / 2).multiply(LevelManager.getLevel().getZoom());
            Point2D bottomLeft2 = new Point2D(x2 - width / 2, y2 + height / 2).multiply(LevelManager.getLevel().getZoom());
            Point2D bottomRight2 = new Point2D(x2 + width / 2, y2 + height / 2).multiply(LevelManager.getLevel().getZoom());

            double woag = Math.min(Math.min(0.5, Math.abs(width) / 4), Math.abs(height) / 4);

            Point2D topRight = new Point2D(x2 + width / 2 - woag, y2 - height / 2 + woag).multiply(LevelManager.getLevel().getZoom());
            Point2D topLeft = new Point2D(x2 - width / 2 + woag, y2 - height / 2 + woag).multiply(LevelManager.getLevel().getZoom());
            Point2D bottomLeft = new Point2D(x2 - width / 2 + woag, y2 + height / 2 - woag).multiply(LevelManager.getLevel().getZoom());
            Point2D bottomRight = new Point2D(x2 + width / 2 - woag, y2 + height / 2 - woag).multiply(LevelManager.getLevel().getZoom());

            topRight = EditorObject.rotate(topRight, rotation, center.multiply(LevelManager.getLevel().getZoom()));
            topLeft = EditorObject.rotate(topLeft, rotation, center.multiply(LevelManager.getLevel().getZoom()));
            bottomLeft = EditorObject.rotate(bottomLeft, rotation, center.multiply(LevelManager.getLevel().getZoom()));
            bottomRight = EditorObject.rotate(bottomRight, rotation, center.multiply(LevelManager.getLevel().getZoom()));

            topRight2 = EditorObject.rotate(topRight2, rotation, center.multiply(LevelManager.getLevel().getZoom()));
            topLeft2 = EditorObject.rotate(topLeft2, rotation, center.multiply(LevelManager.getLevel().getZoom()));
            bottomLeft2 = EditorObject.rotate(bottomLeft2, rotation, center.multiply(LevelManager.getLevel().getZoom()));
            bottomRight2 = EditorObject.rotate(bottomRight2, rotation, center.multiply(LevelManager.getLevel().getZoom()));

            topRight = new Point2D((topRight.getX()) + LevelManager.getLevel().getOffsetX(), (-topRight.getY()) + LevelManager.getLevel().getOffsetY());
            topLeft = new Point2D((topLeft.getX()) + LevelManager.getLevel().getOffsetX(), (-topLeft.getY()) + LevelManager.getLevel().getOffsetY());
            bottomLeft = new Point2D((bottomLeft.getX()) + LevelManager.getLevel().getOffsetX(), (-bottomLeft.getY()) + LevelManager.getLevel().getOffsetY());
            bottomRight = new Point2D((bottomRight.getX()) + LevelManager.getLevel().getOffsetX(), (-bottomRight.getY()) + LevelManager.getLevel().getOffsetY());

            topRight2 = new Point2D((topRight2.getX()) + LevelManager.getLevel().getOffsetX(), (-topRight2.getY()) + LevelManager.getLevel().getOffsetY());
            topLeft2 = new Point2D((topLeft2.getX()) + LevelManager.getLevel().getOffsetX(), (-topLeft2.getY()) + LevelManager.getLevel().getOffsetY());
            bottomLeft2 = new Point2D((bottomLeft2.getX()) + LevelManager.getLevel().getOffsetX(), (-bottomLeft2.getY()) + LevelManager.getLevel().getOffsetY());
            bottomRight2 = new Point2D((bottomRight2.getX()) + LevelManager.getLevel().getOffsetX(), (-bottomRight2.getY()) + LevelManager.getLevel().getOffsetY());

            graphicsContext.setFill(Renderer.transparentBlue);
            graphicsContext.fillPolygon(new double[]{topRight.getX(), topLeft.getX(), bottomLeft.getX(), bottomRight.getX()}, new double[]{topRight.getY(), topLeft.getY(), bottomLeft.getY(), bottomRight.getY()}, 4);
            graphicsContext.setStroke(Renderer.solidBlue);
            graphicsContext.setLineWidth(2 * woag * LevelManager.getLevel().getZoom());
            graphicsContext.strokeLine(topRight.getX(), topRight.getY(), topLeft.getX(), topLeft.getY());
            graphicsContext.strokeLine(bottomLeft.getX(), bottomLeft.getY(), bottomRight.getX(), bottomRight.getY());
            graphicsContext.strokeLine(topLeft.getX(), topLeft.getY(), bottomLeft.getX(), bottomLeft.getY());
            graphicsContext.strokeLine(bottomRight.getX(), bottomRight.getY(), topRight.getX(), topRight.getY());

            if (this == Main.getSelected()) {
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
                graphicsContext.strokeRect(topRight2.getX() - 4, topRight2.getY() - 4, 8, 8);
                graphicsContext.strokeRect(topLeft2.getX() - 4, topLeft2.getY() - 4, 8, 8);
                graphicsContext.strokeRect(bottomLeft2.getX() - 4, bottomLeft2.getY() - 4, 8, 8);
                graphicsContext.strokeRect(bottomRight2.getX() - 4, bottomRight2.getY() - 4, 8, 8);

                Point2D middleLeft = new Point2D(x2 - width / 2, y2);
                Point2D middleRight = new Point2D(x2 + width / 2, y2);

                middleLeft = EditorObject.rotate(middleLeft, rotation, center);
                middleRight = EditorObject.rotate(middleRight, rotation, center);

                middleLeft = new Point2D((middleLeft.getX()) * LevelManager.getLevel().getZoom() + LevelManager.getLevel().getOffsetX(), (-middleLeft.getY()) * LevelManager.getLevel().getZoom() + LevelManager.getLevel().getOffsetY());
                middleRight = new Point2D((middleRight.getX()) * LevelManager.getLevel().getZoom() + LevelManager.getLevel().getOffsetX(), (-middleRight.getY()) * LevelManager.getLevel().getZoom() + LevelManager.getLevel().getOffsetY());

                graphicsContext.strokeOval(middleLeft.getX() - 4, middleLeft.getY() - 4, 8, 8);
                graphicsContext.strokeOval(middleRight.getX() - 4, middleRight.getY() - 4, 8, 8);
            }

        }

        if (!getAttribute("image").equals("")) {
            imageGraphicsContext.save();
            try {
                image.draw(graphicsContext, imageGraphicsContext);
            } catch (Exception e) {
                //nothing
            }
            imageGraphicsContext.restore();
        }
    }

    @Override
    public DragSettings mouseImageIntersection(double mX2, double mY2) {
        if (LevelManager.getLevel().isShowGraphics() && image.getImage() != null) {
            return image.mouseIntersection(mX2, mY2);
        }
        return new DragSettings(DragSettings.NONE);
    }

    @Override
    public DragSettings mouseIntersectingCorners(double mX2, double mY2) {

        if (LevelManager.getLevel().isShowGeometry()) {

            double x2 = Double.parseDouble(getAttribute("x"));
            double y2 = Double.parseDouble(getAttribute("y"));

            double rotation = Double.parseDouble(getAttribute("rotation"));

            double width = Double.parseDouble(getAttribute("width"));
            double height = Double.parseDouble(getAttribute("height"));

            if (getParent() instanceof Compositegeom) {
                Point2D rotated = rotate(new Point2D(x2, y2), getParent().getDouble("rotation"), new Point2D(0, 0));
                x2 = rotated.getX();
                y2 = rotated.getY();

                x2 += Double.parseDouble(getParent().getAttribute("x"));
                y2 += Double.parseDouble(getParent().getAttribute("y"));

                rotation += getParent().getDouble("rotation");
            }

            Point2D center = new Point2D(x2, y2);
            Point2D right = new Point2D(x2 + width / 2, y2);
            Point2D left = new Point2D(x2 - width / 2, y2);
            Point2D topRight = new Point2D(x2 + width / 2, y2 + height / 2);
            Point2D topLeft = new Point2D(x2 - width / 2, y2 + height / 2);
            Point2D bottomLeft = new Point2D(x2 - width / 2, y2 - height / 2);
            Point2D bottomRight = new Point2D(x2 + width / 2, y2 - height / 2);
            right = rotate(right, rotation, center);
            left = rotate(left, rotation, center);
            right = new Point2D((right.getX()), (-right.getY()));
            left = new Point2D((left.getX()), (-left.getY()));
            topRight = rotate(topRight, rotation, center);
            topLeft = rotate(topLeft, rotation, center);
            bottomLeft = rotate(bottomLeft, rotation, center);
            bottomRight = rotate(bottomRight, rotation, center);
            topRight = new Point2D((topRight.getX()), (-topRight.getY()));
            topLeft = new Point2D((topLeft.getX()), (-topLeft.getY()));
            bottomLeft = new Point2D((bottomLeft.getX()), (-bottomLeft.getY()));
            bottomRight = new Point2D((bottomRight.getX()), (-bottomRight.getY()));
            double distance = 4 / LevelManager.getLevel().getZoom();

            DragSettings resizeSettings = new DragSettings(DragSettings.RESIZE);

            DragSettings rotateSettings = new DragSettings(DragSettings.ROTATE);

            double dragSourceX = 0;
            double dragSourceY = 0;
            double dragAnchorX = 0;
            double dragAnchorY = 0;
            double rotateAngleOffset = 0;

            boolean resize = false;
            boolean rotate = false;

            if (mX2 > topLeft.getX() - distance && mX2 < topLeft.getX() + distance && mY2 > topLeft.getY() - distance && mY2 < topLeft.getY() + distance) {
                resize  = true;
                dragSourceX = x2 - width / 2;
                dragSourceY = -y2 - height / 2;
                dragAnchorX = x2 + width / 2;
                dragAnchorY = -y2 + height / 2;
            }
            if (mX2 > topRight.getX() - distance && mX2 < topRight.getX() + distance && mY2 > topRight.getY() - distance && mY2 < topRight.getY() + distance) {
                resize  = true;
                dragSourceX = x2 + width / 2;
                dragSourceY = -y2 - height / 2;
                dragAnchorX = x2 - width / 2;
                dragAnchorY = -y2 + height / 2;
            }
            if (mX2 > bottomLeft.getX() - distance && mX2 < bottomLeft.getX() + distance && mY2 > bottomLeft.getY() - distance && mY2 < bottomLeft.getY() + distance) {
                resize  = true;
                dragSourceX = x2 - width / 2;
                dragSourceY = -y2 + height / 2;
                dragAnchorX = x2 + width / 2;
                dragAnchorY = -y2 - height / 2;
            }
            if (mX2 > bottomRight.getX() - distance && mX2 < bottomRight.getX() + distance && mY2 > bottomRight.getY() - distance && mY2 < bottomRight.getY() + distance) {
                resize  = true;
                dragSourceX = x2 + width / 2;
                dragSourceY = -y2 + height / 2;
                dragAnchorX = x2 - width / 2;
                dragAnchorY = -y2 - height / 2;
            }
            if (mX2 > left.getX() - distance && mX2 < left.getX() + distance && mY2 > left.getY() - distance && mY2 < left.getY() + distance) {
                rotate = true;
                dragSourceX = x2 - width / 2;
                dragSourceY = -y2;
                rotateAngleOffset = 0;
            }
            if (mX2 > right.getX() - distance && mX2 < right.getX() + distance && mY2 > right.getY() - distance && mY2 < right.getY() + distance) {
                rotate = true;
                dragSourceX = x2 + width / 2;
                dragSourceY = -y2;
                rotateAngleOffset = 180;
            }
            if (resize) {
                Point2D dragSourceRotated = EditorObject.rotate(new Point2D(dragSourceX, dragSourceY), -rotation, new Point2D(x2, -y2));
                Point2D dragAnchorRotated = EditorObject.rotate(new Point2D(dragAnchorX, dragAnchorY), -rotation, new Point2D(x2, -y2));
                resizeSettings.setInitialSourceX(dragSourceRotated.getX());
                resizeSettings.setInitialSourceY(dragSourceRotated.getY());
                resizeSettings.setAnchorX(dragAnchorRotated.getX());
                resizeSettings.setAnchorY(dragAnchorRotated.getY());
                return resizeSettings;
            }
            if (rotate) {
                Point2D dragSourceRotated = EditorObject.rotate(new Point2D(dragSourceX, dragSourceY), -Math.toRadians(rotation), new Point2D(x2, -y2));
                rotateSettings.setInitialSourceX(dragSourceRotated.getX());
                rotateSettings.setInitialSourceY(dragSourceRotated.getY());
                rotateSettings.setRotateAngleOffset(rotateAngleOffset);
                return rotateSettings;
            }
        }
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
    public void resizeFromMouse(double mouseX, double mouseY, double resizeDragSourceX, double resizeDragSourceY, double resizeDragAnchorX, double resizeDragAnchorY){
        if (Main.getDragSettings().isDraggingImage()) {
            image.resizeFromMouse(mouseX, mouseY, resizeDragSourceX, resizeDragSourceY, resizeDragAnchorX, resizeDragAnchorY);
        } else {
            double rotation = Double.parseDouble(getAttribute("rotation"));

            if (getParent() instanceof Compositegeom) {
                rotation += getParent().getDouble("rotation");
            }

            Point2D center = new Point2D((mouseX + resizeDragAnchorX) / 2, (mouseY + resizeDragAnchorY) / 2);

            Point2D rotatedReal = EditorObject.rotate(new Point2D(mouseX, mouseY), rotation, center);
            Point2D rotatedAnchor = EditorObject.rotate(new Point2D(resizeDragAnchorX, resizeDragAnchorY), rotation, center);

            double newWidth;
            double newHeight;

            newWidth = Math.abs(rotatedAnchor.getX() - rotatedReal.getX());
            newHeight = Math.abs(rotatedAnchor.getY() - rotatedReal.getY());

            setAttribute("width", newWidth);
            setAttribute("height", newHeight);
            if (getParent() instanceof Compositegeom) {

                Point2D rotated = rotate(new Point2D(center.getX() - getParent().getDouble("x"), center.getY() + getParent().getDouble("y")), getParent().getDouble("rotation"), new Point2D(0, 0));

                setAttribute("x", rotated.getX());
                setAttribute("y", -rotated.getY());
            } else {
                setAttribute("x", center.getX());
                setAttribute("y", -center.getY());
            }
        }
    }

    @Override
    public void rotateFromMouse(double mouseX, double mouseY, double rotateAngleOffset) {
        if (Main.getDragSettings().isDraggingImage()) {
            image.rotateFromMouse(mouseX, mouseY, rotateAngleOffset);
        } else {
            double x2 = Double.parseDouble(getAttribute("x"));
            double y2 = Double.parseDouble(getAttribute("y"));

            if (getParent() instanceof Compositegeom) {
                Point2D rotated = rotate(new Point2D(x2, y2), getParent().getDouble("rotation"), new Point2D(0, 0));
                x2 = rotated.getX();
                y2 = rotated.getY();

                x2 += Double.parseDouble(getParent().getAttribute("x"));
                y2 += Double.parseDouble(getParent().getAttribute("y"));
            }

            double rotation2 = Math.toDegrees(Renderer.angleTo(new Point2D(mouseX, mouseY), new Point2D(x2, -y2)));
            if (getParent() instanceof Compositegeom) {
                setAttribute("rotation", Math.toRadians(rotation2 + rotateAngleOffset) - getParent().getDouble("rotation"));
            } else {
                setAttribute("rotation", Math.toRadians(rotation2 + rotateAngleOffset));
            }
        }
    }
}
