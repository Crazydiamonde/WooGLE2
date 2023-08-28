package com.WorldOfGoo.Level;

import com.WooGLEFX.File.FileManager;
import com.WooGLEFX.File.GlobalResourceManager;
import com.WooGLEFX.Engine.Main;
import com.WooGLEFX.Engine.Renderer;
import com.WooGLEFX.GUI.Alarms;
import com.WooGLEFX.Structures.*;
import com.WooGLEFX.Structures.SimpleStructures.Color;
import com.WooGLEFX.Structures.SimpleStructures.DragSettings;
import com.WooGLEFX.Structures.SimpleStructures.MetaEditorAttribute;
import com.WorldOfGoo.Scene.Compositegeom;
import com.WorldOfGoo.Scene.SceneLayer;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.transform.Affine;

import java.io.FileNotFoundException;

public class Signpost extends EditorObject {

    private Image image;

    public Signpost(EditorObject _parent) {
        super(_parent);
        setRealName("signpost");
        addAttribute("name", "", InputField.ANY, true);
        addAttribute("depth", "0", InputField.NUMBER, true);
        addAttribute("x", "0", InputField.NUMBER, true);
        addAttribute("y", "0", InputField.NUMBER, true);
        addAttribute("scalex", "1", InputField.NUMBER, true);
        addAttribute("scaley", "1", InputField.NUMBER, true);
        addAttribute("rotation", "0", InputField.NUMBER, true);
        addAttribute("alpha", "1", InputField.NUMBER, true);
        addAttribute("colorize", "255,255,255", InputField.COLOR, true);
        addAttribute("image", "", InputField.IMAGE, true);
        addAttribute("text", "", InputField.TEXT, true);
        addAttribute("particles", "", InputField.PARTICLES, false);
        addAttribute("pulse", "", InputField.ANY, false);
        setNameAttribute(getAttribute2("name"));
        setMetaAttributes(MetaEditorAttribute.parse("name,text,particles,pulse,Image<x,y,scalex,scaley,image,rotation,depth,alpha,colorize>"));
    }

    @Override
    public void update() {
        setNameAttribute(getAttribute2("name"));
        try {
            image = getImage("image");
            Color color = getColor("colorize");
            image = SceneLayer.colorize(image, color);
        } catch (Exception e) {
            if (!Main.failedResources.contains("From signpost: \"" + getAttribute("image") + "\" (version " + Main.getLevel().getVersion() + ")")) {
                Main.failedResources.add("From signpost: \"" + getAttribute("image") + "\" (version " + Main.getLevel().getVersion() + ")");
            }
            image = null;
        }

        ChangeListener<String> wizard = (observable, oldValue, newValue) -> {
            System.out.println("Image changed from " + oldValue + " to " + newValue);
            try {
                image = getImage("image");
                Color color = getColor("colorize");
                image = SceneLayer.colorize(image, color);
            } catch (Exception e) {
                if (!Main.failedResources.contains("From signpost: \"" + getString("image") + "\" (version " + Main.getLevel().getVersion() + ")")) {
                    Main.failedResources.add("From signpost: \"" + getString("image") + "\" (version " + Main.getLevel().getVersion() + ")");
                }
                image = null;
            }
        };

        setChangeListener("image", wizard);
        setChangeListener("colorize", wizard);
    }

    @Override
    public void drawImage(GraphicsContext graphicsContext, GraphicsContext imageGraphicsContext) {
        if (image != null) {
            double x2 = getDouble("x");
            double y2 = getDouble("y");

            double rotation = getDouble("rotation");

            double scalex = getDouble("scalex");
            double scaley = getDouble("scaley");

            if (getParent() instanceof Compositegeom) {
                x2 += getParent().getDouble("x");
                y2 += getParent().getDouble("y");
            }

            double imgWidth = image.getWidth() * scalex;
            double imgHeight = image.getHeight() * scaley;

            double screenX = (x2);
            double screenY = (-y2);

            //image = EditorObject.rotateImageByDegrees(image, -rotation);

            imageGraphicsContext.save();
            Affine t = imageGraphicsContext.getTransform();
            t.appendRotation(-rotation, screenX, screenY);
            imageGraphicsContext.setTransform(t);

            imageGraphicsContext.drawImage(image, screenX - imgWidth / 2.0, screenY - imgHeight / 2.0, imgWidth, imgHeight);

            if (this == Main.getSelected()) {

                Point2D rotated2 = EditorObject.rotate(new Point2D(x2 - image.getWidth() * scalex / 2, -y2 - image.getHeight() * scaley / 2), -Math.toRadians(rotation), new Point2D(x2, -y2));
                Point2D rotated3 = EditorObject.rotate(new Point2D(x2 + image.getWidth() * scalex / 2, -y2 + image.getHeight() * scaley / 2), -Math.toRadians(rotation), new Point2D(x2, -y2));

                Point2D rotated4 = EditorObject.rotate(new Point2D(x2 - image.getWidth() * scalex / 2, -y2), -Math.toRadians(rotation), new Point2D(x2, -y2));
                Point2D rotated5 = EditorObject.rotate(new Point2D(x2 + image.getWidth() * scalex / 2, -y2), -Math.toRadians(rotation), new Point2D(x2, -y2));

                Point2D rotated6 = EditorObject.rotate(new Point2D(x2 - image.getWidth() * scalex / 2, -y2 + image.getHeight() * scaley / 2), -Math.toRadians(rotation), new Point2D(x2, -y2));
                Point2D rotated7 = EditorObject.rotate(new Point2D(x2 + image.getWidth() * scalex / 2, -y2 - image.getHeight() * scaley / 2), -Math.toRadians(rotation), new Point2D(x2, -y2));

                double screenX2 = (x2) * Main.getLevel().getZoom() + Main.getLevel().getOffsetX();
                double screenY2 = (-y2) * Main.getLevel().getZoom() + Main.getLevel().getOffsetY();

                graphicsContext.save();
                Affine t2 = graphicsContext.getTransform();
                t2.appendRotation(-rotation, screenX2, screenY2);
                graphicsContext.setTransform(t2);

                graphicsContext.setStroke(Renderer.selectionOutline);
                graphicsContext.setLineWidth(1);
                graphicsContext.setLineDashes(3);

                double screenX3 = (rotated2.getX()) * Main.getLevel().getZoom() + Main.getLevel().getOffsetX();
                double screenY3 = (rotated2.getY()) * Main.getLevel().getZoom() + Main.getLevel().getOffsetY();

                double screenX4 = (rotated3.getX()) * Main.getLevel().getZoom() + Main.getLevel().getOffsetX();
                double screenY4 = (rotated3.getY()) * Main.getLevel().getZoom() + Main.getLevel().getOffsetY();

                double screenX5 = (rotated4.getX()) * Main.getLevel().getZoom() + Main.getLevel().getOffsetX();
                double screenY5 = (rotated4.getY()) * Main.getLevel().getZoom() + Main.getLevel().getOffsetY();

                double screenX6 = (rotated5.getX()) * Main.getLevel().getZoom() + Main.getLevel().getOffsetX();
                double screenY6 = (rotated5.getY()) * Main.getLevel().getZoom() + Main.getLevel().getOffsetY();

                double screenX7 = (rotated6.getX()) * Main.getLevel().getZoom() + Main.getLevel().getOffsetX();
                double screenY7 = (rotated6.getY()) * Main.getLevel().getZoom() + Main.getLevel().getOffsetY();

                double screenX8 = (rotated7.getX()) * Main.getLevel().getZoom() + Main.getLevel().getOffsetX();
                double screenY8 = (rotated7.getY()) * Main.getLevel().getZoom() + Main.getLevel().getOffsetY();

                graphicsContext.setStroke(Renderer.selectionOutline2);
                graphicsContext.setLineWidth(1);
                graphicsContext.setLineDashes(3);
                graphicsContext.setLineDashOffset(0);
                graphicsContext.strokeRect(screenX2 - image.getWidth() * scalex * Main.getLevel().getZoom() / 2, screenY2 - image.getHeight() * scaley * Main.getLevel().getZoom() / 2, image.getWidth() * scalex * Main.getLevel().getZoom(), image.getHeight() * scaley * Main.getLevel().getZoom());
                graphicsContext.setStroke(Renderer.selectionOutline);
                graphicsContext.setLineWidth(1);
                graphicsContext.setLineDashOffset(3);
                graphicsContext.strokeRect(screenX2 - image.getWidth() * scalex * Main.getLevel().getZoom() / 2, screenY2 - image.getHeight() * scaley * Main.getLevel().getZoom() / 2, image.getWidth() * scalex * Main.getLevel().getZoom(), image.getHeight() * scaley * Main.getLevel().getZoom());
                graphicsContext.setLineDashes(0);
                graphicsContext.restore();
                graphicsContext.setLineWidth(1);
                graphicsContext.strokeRect(screenX3 - 4, screenY3 - 4, 8, 8);
                graphicsContext.strokeRect(screenX4 - 4, screenY4 - 4, 8, 8);
                graphicsContext.strokeRect(screenX7 - 4, screenY7 - 4, 8, 8);
                graphicsContext.strokeRect(screenX8 - 4, screenY8 - 4, 8, 8);
                graphicsContext.strokeOval(screenX5 - 4, screenY5 - 4, 8, 8);
                graphicsContext.strokeOval(screenX6 - 4, screenY6 - 4, 8, 8);
            }
        }
    }

    @Override
    public DragSettings mouseImageIntersection(double mX2, double mY2) {

        if (image != null) {

            double x = getDouble("x");
            double y = getDouble("y");

            double rotation = getDouble("rotation");

            double scalex = getDouble("scalex");
            double scaley = getDouble("scaley");

            Point2D rotated = rotate(new Point2D(mX2, mY2), Math.toRadians(rotation), new Point2D(x, -y));

            double mX = rotated.getX();
            double mY = rotated.getY();

            if (mX > x - image.getWidth() * Math.abs(scalex) / 2 && mX < x + image.getWidth() * Math.abs(scalex) / 2 && mY > -y - image.getHeight() * Math.abs(scaley) / 2 && mY < -y + image.getHeight() * Math.abs(scaley) / 2) {
                double goodX = (mX - (x - image.getWidth() * Math.abs(scalex) / 2)) / Math.abs(scalex);
                double goodY = (mY - (-y - image.getHeight() * Math.abs(scaley) / 2)) / Math.abs(scaley);
                int pixel = this.image.getPixelReader().getArgb((int) goodX, (int) goodY);
                if (pixel >> 24 != 0) {
                    DragSettings dragSettings = new DragSettings(DragSettings.MOVE);
                    dragSettings.setInitialSourceX(mX2 - x);
                    dragSettings.setInitialSourceY(mY2 + y);
                    return dragSettings;
                }
            }
        }
        return new DragSettings(DragSettings.NONE);
    }

    @Override
    public DragSettings mouseIntersectingCorners(double mX2, double mY2) {

        if (image != null) {

            double x = getDouble("x");
            double y = getDouble("y");

            double rotation = getDouble("rotation");

            double scalex = getDouble("scalex");
            double scaley = getDouble("scaley");

            //image = EditorObject.rotateImageByDegrees(image, -rotation);

            Point2D rotated2 = EditorObject.rotate(new Point2D(x - image.getWidth() * scalex / 2, -y - image.getHeight() * scaley / 2), -Math.toRadians(rotation), new Point2D(x, -y));
            Point2D rotated3 = EditorObject.rotate(new Point2D(x + image.getWidth() * scalex / 2, -y + image.getHeight() * scaley / 2), -Math.toRadians(rotation), new Point2D(x, -y));

            Point2D rotated4 = EditorObject.rotate(new Point2D(x - image.getWidth() * scalex / 2, -y), -Math.toRadians(rotation), new Point2D(x, -y));
            Point2D rotated5 = EditorObject.rotate(new Point2D(x + image.getWidth() * scalex / 2, -y), -Math.toRadians(rotation), new Point2D(x, -y));

            Point2D rotated6 = EditorObject.rotate(new Point2D(x - image.getWidth() * scalex / 2, -y + image.getHeight() * scaley / 2), -Math.toRadians(rotation), new Point2D(x, -y));
            Point2D rotated7 = EditorObject.rotate(new Point2D(x + image.getWidth() * scalex / 2, -y - image.getHeight() * scaley / 2), -Math.toRadians(rotation), new Point2D(x, -y));

            double screenX3 = (rotated2.getX());
            double screenY3 = (rotated2.getY());

            double screenX4 = (rotated3.getX());
            double screenY4 = (rotated3.getY());

            double screenX5 = (rotated4.getX());
            double screenY5 = (rotated4.getY());

            double screenX6 = (rotated5.getX());
            double screenY6 = (rotated5.getY());

            double screenX7 = (rotated6.getX());
            double screenY7 = (rotated6.getY());

            double screenX8 = (rotated7.getX());
            double screenY8 = (rotated7.getY());

            double distance = 4 / Main.getLevel().getZoom();

            DragSettings resizeSettings = new DragSettings(DragSettings.RESIZE);
            resizeSettings.setInitialScaleX(scalex);
            resizeSettings.setInitialScaleY(scaley);

            DragSettings rotateSettings = new DragSettings(DragSettings.ROTATE);

            double dragSourceX = 0;
            double dragSourceY = 0;
            double dragAnchorX = 0;
            double dragAnchorY = 0;
            double rotateAngleOffset = 0;

            boolean resize = false;
            boolean rotate = false;

            if (mX2 > screenX3 - distance && mX2 < screenX3 + distance && mY2 > screenY3 - distance && mY2 < screenY3 + distance) {
                resize = true;
                dragSourceX = x - image.getWidth() * scalex / 2;
                dragSourceY = -y - image.getHeight() * scaley / 2;
                dragAnchorX = x + image.getWidth() * scalex / 2;
                dragAnchorY = -y + image.getHeight() * scaley / 2;
            }
            if (mX2 > screenX4 - distance && mX2 < screenX4 + distance && mY2 > screenY4 - distance && mY2 < screenY4 + distance) {
                resize = true;
                dragSourceX = x + image.getWidth() * scalex / 2;
                dragSourceY = -y + image.getHeight() * scaley / 2;
                dragAnchorX = x - image.getWidth() * scalex / 2;
                dragAnchorY = -y - image.getHeight() * scaley / 2;
            }
            if (mX2 > screenX5 - distance && mX2 < screenX5 + distance && mY2 > screenY5 - distance && mY2 < screenY5 + distance) {
                rotate = true;
                dragSourceX = x + image.getWidth() * scalex / 2;
                dragSourceY = -y;
                rotateAngleOffset = 0;
            }
            if (mX2 > screenX6 - distance && mX2 < screenX6 + distance && mY2 > screenY6 - distance && mY2 < screenY6 + distance) {
                rotate = true;
                dragSourceX = x - image.getWidth() * scalex / 2;
                dragSourceY = -y;
                rotateAngleOffset = 180;
            }
            if (mX2 > screenX7 - distance && mX2 < screenX7 + distance && mY2 > screenY7 - distance && mY2 < screenY7 + distance) {
                resize = true;
                dragSourceX = x - image.getWidth() * scalex / 2;
                dragSourceY = -y + image.getHeight() * scaley / 2;
                dragAnchorX = x + image.getWidth() * scalex / 2;
                dragAnchorY = -y - image.getHeight() * scaley / 2;
            }
            if (mX2 > screenX8 - distance && mX2 < screenX8 + distance && mY2 > screenY8 - distance && mY2 < screenY8 + distance) {
                resize = true;
                dragSourceX = x + image.getWidth() * scalex / 2;
                dragSourceY = -y - image.getHeight() * scaley / 2;
                dragAnchorX = x - image.getWidth() * scalex / 2;
                dragAnchorY = -y + image.getHeight() * scaley / 2;
            }

            if (resize) {
                Point2D dragSourceRotated = EditorObject.rotate(new Point2D(dragSourceX, dragSourceY), -Math.toRadians(rotation), new Point2D(x, -y));
                Point2D dragAnchorRotated = EditorObject.rotate(new Point2D(dragAnchorX, dragAnchorY), -Math.toRadians(rotation), new Point2D(x, -y));

                resizeSettings.setInitialSourceX(dragSourceRotated.getX());
                resizeSettings.setInitialSourceY(dragSourceRotated.getY());
                resizeSettings.setAnchorX(dragAnchorRotated.getX());
                resizeSettings.setAnchorY(dragAnchorRotated.getY());
                resizeSettings.setInitialScaleX(scalex);
                resizeSettings.setInitialScaleY(scaley);

                return resizeSettings;
            }
            if (rotate) {
                Point2D dragSourceRotated = EditorObject.rotate(new Point2D(dragSourceX, dragSourceY), -Math.toRadians(rotation), new Point2D(x, -y));

                rotateSettings.setInitialSourceX(dragSourceRotated.getX());
                rotateSettings.setInitialSourceY(dragSourceRotated.getY());
                rotateSettings.setRotateAngleOffset(rotateAngleOffset);

                return rotateSettings;
            }
        }
        return new DragSettings(DragSettings.NONE);
    }

    @Override
    public void resizeFromMouse(double mouseX, double mouseY, double resizeDragSourceX, double resizeDragSourceY, double resizeDragAnchorX, double resizeDragAnchorY){

        double rotation = getDouble("rotation");

        Point2D center = new Point2D((mouseX + resizeDragAnchorX) / 2, (mouseY + resizeDragAnchorY) / 2);

        Point2D rotatedSource = EditorObject.rotate(new Point2D(resizeDragSourceX, resizeDragSourceY), Math.toRadians(rotation), center);
        Point2D rotatedReal = EditorObject.rotate(new Point2D(mouseX, mouseY), Math.toRadians(rotation), center);
        Point2D rotatedAnchor = EditorObject.rotate(new Point2D(resizeDragAnchorX, resizeDragAnchorY), Math.toRadians(rotation), center);

        double originalWidth = image.getWidth();
        double originalHeight = image.getHeight();

        double newWidth;
        double newHeight;

        boolean negativeX = Main.getDragSettings().getInitialScaleX() < 0;
        if (rotatedAnchor.getX() < rotatedSource.getX()) {
            negativeX = !negativeX;
        }

        if (negativeX) {
            newWidth = rotatedAnchor.getX() - rotatedReal.getX();
        } else {
            newWidth = rotatedReal.getX() - rotatedAnchor.getX();
        }

        boolean negativeY = Main.getDragSettings().getInitialScaleY() < 0;
        if (rotatedAnchor.getY() < rotatedSource.getY()) {
            negativeY = !negativeY;
        }

        if (negativeY) {
            newHeight = rotatedReal.getY() - rotatedAnchor.getY();
        } else {
            newHeight = rotatedAnchor.getY() - rotatedReal.getY();
        }

        double newScaleX = newWidth / originalWidth;
        double newScaleY = newHeight / originalHeight;

        setAttribute("scalex", -newScaleX);
        setAttribute("scaley", newScaleY);
        setAttribute("x", center.getX());
        setAttribute("y", -center.getY());
    }

    @Override
    public void rotateFromMouse(double mouseX, double mouseY, double rotateAngleOffset){
        double x2 = getDouble("x");
        double y2 = -getDouble("y");

        if (getParent() instanceof Compositegeom){
            x2 += getParent().getDouble("x");
            y2 -= getParent().getDouble("y");
        }

        double rotation2 = Math.toDegrees(Renderer.angleTo(new Point2D(mouseX, mouseY), new Point2D(x2, y2)));
        setAttribute("rotation", rotation2 - rotateAngleOffset);
    }

}
