package com.WooGLEFX.EditorObjects;

import com.WooGLEFX.Engine.Collision.RectangleCollision;
import com.WooGLEFX.Engine.Main;
import com.WooGLEFX.Engine.Renderer;
import com.WooGLEFX.Structures.SimpleStructures.DragSettings;
import com.WooGLEFX.Structures.EditorObject;
import com.WooGLEFX.Structures.SimpleStructures.Position;
import com.WorldOfGoo.Scene.Compositegeom;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.transform.Affine;

import java.io.FileNotFoundException;

public class GeometryImage {

    private final EditorObject parent;
    private Image image;

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public GeometryImage(EditorObject parent) {
        this.parent = parent;
    }

    public void draw(GraphicsContext graphicsContext, GraphicsContext imageGraphicsContext) throws FileNotFoundException {
        if (Main.getLevel().isShowGraphics()) {
            image = parent.getImage("image");

            Position imagePos;
            if (parent.getAttribute("imagepos").equals("")) {
                imagePos = new Position(parent.getDouble("x"), parent.getDouble("y"));
            } else {
                imagePos = parent.getPosition("imagepos");
            }
            double x = imagePos.getX();
            double y = imagePos.getY();
            if (parent.getParent() instanceof Compositegeom) {
                x += parent.getParent().getDouble("x");
                y += parent.getParent().getDouble("y");
            }

            double rotation = Math.toDegrees(parent.getDouble("imagerot"));

            Position imageScale = Position.parse(parent.getAttribute("imagescale"));
            double scaleX = imageScale.getX();
            double scaleY = imageScale.getY();

            double imgWidth = image.getWidth() * scaleX;
            double imgHeight = image.getHeight() * scaleY;

            Affine t = imageGraphicsContext.getTransform();
            t.appendRotation(-rotation, x, -y);
            imageGraphicsContext.setTransform(t);

            imageGraphicsContext.drawImage(image, x - imgWidth / 2.0, -y - imgHeight / 2.0, imgWidth, imgHeight);

            if (parent == Main.getSelected()) {

                Point2D rotated2 = EditorObject.rotate(new Point2D(x - image.getWidth() * scaleX / 2, -y - image.getHeight() * scaleY / 2), -Math.toRadians(rotation), new Point2D(x, -y));
                Point2D rotated3 = EditorObject.rotate(new Point2D(x + image.getWidth() * scaleX / 2, -y + image.getHeight() * scaleY / 2), -Math.toRadians(rotation), new Point2D(x, -y));

                Point2D rotated4 = EditorObject.rotate(new Point2D(x - image.getWidth() * scaleX / 2, -y), -Math.toRadians(rotation), new Point2D(x, -y));
                Point2D rotated5 = EditorObject.rotate(new Point2D(x + image.getWidth() * scaleX / 2, -y), -Math.toRadians(rotation), new Point2D(x, -y));

                Point2D rotated6 = EditorObject.rotate(new Point2D(x - image.getWidth() * scaleX / 2, -y + image.getHeight() * scaleY / 2), -Math.toRadians(rotation), new Point2D(x, -y));
                Point2D rotated7 = EditorObject.rotate(new Point2D(x + image.getWidth() * scaleX / 2, -y - image.getHeight() * scaleY / 2), -Math.toRadians(rotation), new Point2D(x, -y));

                double screenX2 = x * Main.getLevel().getZoom() + Main.getLevel().getOffsetX();
                double screenY2 = -y * Main.getLevel().getZoom() + Main.getLevel().getOffsetY();

                graphicsContext.setStroke(Renderer.selectionOutline);

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

                graphicsContext.setLineWidth(1);
                graphicsContext.setFill(Renderer.selectionOutline);
                graphicsContext.fillRect(screenX3 - 4, screenY3 - 4, 8, 8);
                graphicsContext.fillRect(screenX4 - 4, screenY4 - 4, 8, 8);
                graphicsContext.fillRect(screenX7 - 4, screenY7 - 4, 8, 8);
                graphicsContext.fillRect(screenX8 - 4, screenY8 - 4, 8, 8);
                graphicsContext.fillOval(screenX5 - 4, screenY5 - 4, 8, 8);
                graphicsContext.fillOval(screenX6 - 4, screenY6 - 4, 8, 8);
                graphicsContext.setStroke(Renderer.selectionOutline2);
                graphicsContext.strokeRect(screenX3 - 4, screenY3 - 4, 8, 8);
                graphicsContext.strokeRect(screenX4 - 4, screenY4 - 4, 8, 8);
                graphicsContext.strokeRect(screenX7 - 4, screenY7 - 4, 8, 8);
                graphicsContext.strokeRect(screenX8 - 4, screenY8 - 4, 8, 8);
                graphicsContext.strokeOval(screenX5 - 4, screenY5 - 4, 8, 8);
                graphicsContext.strokeOval(screenX6 - 4, screenY6 - 4, 8, 8);

                Affine t2 = graphicsContext.getTransform();
                t2.appendRotation(-rotation, screenX2, screenY2);
                graphicsContext.setTransform(t2);

                graphicsContext.setStroke(Renderer.selectionOutline);
                graphicsContext.strokeRect(screenX2 - image.getWidth() * scaleX * Main.getLevel().getZoom() / 2, screenY2 - image.getHeight() * scaleY * Main.getLevel().getZoom() / 2, image.getWidth() * scaleX * Main.getLevel().getZoom(), image.getHeight() * scaleY * Main.getLevel().getZoom());

            }
        }
    }

    public void dragFromMouse(double mouseX, double mouseY, double dragSourceX, double dragSourceY) {
        double x = mouseX - dragSourceX;
        double y = dragSourceY - mouseY;
        parent.setAttribute("imagepos", x + "," + y);
    }

    public DragSettings mouseIntersection(double mX2, double mY2) {

        Position imagePos;
        if (parent.getAttribute("imagepos").equals("")) {
            imagePos = new Position(parent.getDouble("x"), parent.getDouble("y"));
        } else {
            imagePos = parent.getPosition("imagepos");
        }
        double x = imagePos.getX();
        double y = imagePos.getY();
        if (parent.getParent() instanceof Compositegeom) {
            x += parent.getParent().getDouble("x");
            y += parent.getParent().getDouble("y");
        }

        double rotation = Math.toDegrees(parent.getDouble("imagerot"));

        Position imageScale = Position.parse(parent.getAttribute("imagescale"));
        double scalex = imageScale.getX();
        double scaley = imageScale.getY();

        Point2D rotated = EditorObject.rotate(new Point2D(mX2, mY2), Math.toRadians(rotation), new Point2D(x, -y));

        double mX = rotated.getX();
        double mY = rotated.getY();

        if (RectangleCollision.solidRectangleCollision(mX, mY, x, -y, image.getWidth() * scalex, image.getHeight() * scaley)) {
            double goodX = (mX - (x - image.getWidth() * scalex / 2)) / scalex;
            double goodY = (mY - (-y - image.getHeight() * scaley / 2)) / scaley;
            int pixel = image.getPixelReader().getArgb((int) goodX, (int) goodY);
            if (pixel >> 24 != 0) {
                DragSettings dragSettings = new DragSettings(DragSettings.MOVE);
                dragSettings.setInitialSourceX(mX2 - x);
                dragSettings.setInitialSourceY(mY2 + y);
                dragSettings.setDraggingImage(true);
                return dragSettings;
            }
        }
        return new DragSettings(DragSettings.NONE);
    }

    public DragSettings mouseIntersectingCorners(double mX2, double mY2) {
        if (Main.getLevel().isShowGraphics() && image != null) {

            Position imagePos;
            if (parent.getAttribute("imagepos").equals("")) {
                imagePos = new Position(parent.getDouble("x"), parent.getDouble("y"));
            } else {
                imagePos = parent.getPosition("imagepos");
            }
            double x = imagePos.getX();
            double y = imagePos.getY();
            if (parent.getParent() instanceof Compositegeom) {
                x += parent.getParent().getDouble("x");
                y += parent.getParent().getDouble("y");
            }

            double rotation = Math.toDegrees(parent.getDouble("imagerot"));

            Position imageScale = Position.parse(parent.getAttribute("imagescale"));
            double scalex = imageScale.getX();
            double scaley = imageScale.getY();

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
                dragSourceX = x - image.getWidth() * scalex / 2;
                dragSourceY = -y;
                rotateAngleOffset = 0;
            }
            if (mX2 > screenX6 - distance && mX2 < screenX6 + distance && mY2 > screenY6 - distance && mY2 < screenY6 + distance) {
                rotate = true;
                dragSourceX = x + image.getWidth() * scalex / 2;
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
                resizeSettings.setDraggingImage(true);
                return resizeSettings;
            }

            if (rotate) {
                Point2D dragSourceRotated = EditorObject.rotate(new Point2D(dragSourceX, dragSourceY), -Math.toRadians(rotation), new Point2D(x, -y));
                rotateSettings.setInitialSourceX(dragSourceRotated.getX());
                rotateSettings.setInitialSourceY(dragSourceRotated.getY());
                if (scalex < 0) {
                    rotateAngleOffset += 180;
                }
                rotateSettings.setRotateAngleOffset(rotateAngleOffset);
                rotateSettings.setDraggingImage(true);
                return rotateSettings;
            }
        }
        return new DragSettings(DragSettings.NONE);
    }

    public void resizeFromMouse(double mouseX, double mouseY, double resizeDragSourceX, double resizeDragSourceY, double resizeDragAnchorX, double resizeDragAnchorY){

        double rotation = Math.toDegrees(Double.parseDouble(parent.getAttribute("imagerot")));

        Point2D center = new Point2D((mouseX + resizeDragAnchorX) / 2, (mouseY + resizeDragAnchorY) / 2);

        Point2D rotatedSource = EditorObject.rotate(new Point2D(resizeDragSourceX, resizeDragSourceY), Math.toRadians(rotation), center);
        Point2D rotatedReal = EditorObject.rotate(new Point2D(mouseX, mouseY), Math.toRadians(rotation), center);
        Point2D rotatedAnchor = EditorObject.rotate(new Point2D(resizeDragAnchorX, resizeDragAnchorY), Math.toRadians(rotation), center);

        double originalWidth = image.getWidth();
        double originalHeight = image.getHeight();

        double newWidth;
        double newHeight;

        if (rotatedAnchor.getX() > rotatedSource.getX()){
            newWidth = rotatedAnchor.getX() - rotatedReal.getX();
        } else {
            newWidth = rotatedReal.getX() - rotatedAnchor.getX();
        }

        if (rotatedAnchor.getY() > rotatedSource.getY()){
            newHeight = rotatedAnchor.getY() - rotatedReal.getY();
        } else {
            newHeight = rotatedReal.getY() - rotatedAnchor.getY();
        }

        double newScaleX = newWidth / originalWidth;
        double newScaleY = newHeight / originalHeight;

        parent.setAttribute("imagescale", newScaleX + "," + newScaleY);
        parent.setAttribute("imagepos", center.getX() + "," + -center.getY());
    }

    public void rotateFromMouse(double mouseX, double mouseY, double rotateAngleOffset){

        Position imagePos;
        if (parent.getAttribute("imagepos").equals("")) {
            imagePos = new Position(parent.getDouble("x"), parent.getDouble("y"));
        } else {
            imagePos = parent.getPosition("imagepos");
        }
        double x2 = imagePos.getX();
        double y2 = imagePos.getY();
        if (parent.getParent() instanceof Compositegeom) {
            x2 += parent.getParent().getDouble("x");
            y2 += parent.getParent().getDouble("y");
        }

        double rotation2 = Renderer.angleTo(new Point2D(mouseX, mouseY), new Point2D(x2, -y2));
        parent.setAttribute("imagerot", rotation2 + Math.toRadians(rotateAngleOffset));
    }
}
