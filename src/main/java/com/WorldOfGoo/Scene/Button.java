package com.WorldOfGoo.Scene;

import com.WooGLEFX.File.GlobalResourceManager;
import com.WooGLEFX.Engine.Main;
import com.WooGLEFX.Engine.Renderer;
import com.WooGLEFX.GUI.Alarms;
import com.WooGLEFX.Structures.*;
import com.WooGLEFX.Structures.SimpleStructures.Color;
import com.WooGLEFX.Structures.SimpleStructures.DragSettings;
import com.WooGLEFX.Structures.SimpleStructures.MetaEditorAttribute;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.transform.Affine;

import java.io.FileNotFoundException;

public class Button extends EditorObject {

    private Image image;

    public Button(EditorObject _parent) {
        super(_parent);
        setRealName("button");
        addAttribute("id", "", InputField.ANY, true);
        addAttribute("depth", "0", InputField.NUMBER, true);
        addAttribute("x", "0", InputField.NUMBER, true);
        addAttribute("y", "0", InputField.NUMBER, true);
        addAttribute("scalex", "1", InputField.NUMBER, true);
        addAttribute("scaley", "1", InputField.NUMBER, true);
        addAttribute("rotation", "0", InputField.NUMBER, true);
        addAttribute("anchor", "", InputField.ANY, false);
        addAttribute("alpha", "1", InputField.ANY, true);
        addAttribute("colorize", "255,255,255", InputField.ANY, true);
        addAttribute("up", "", InputField.IMAGE, false);
        addAttribute("over", "", InputField.IMAGE, false);
        addAttribute("down", "", InputField.IMAGE, false);
        addAttribute("downover", "", InputField.IMAGE, false);
        addAttribute("armed", "", InputField.IMAGE, false);
        addAttribute("downarmed", "", InputField.IMAGE, false);
        addAttribute("disabled", "", InputField.IMAGE, false);
        addAttribute("downdisabled", "", InputField.IMAGE, false);
        addAttribute("latch", "", InputField.FLAG, false);
        addAttribute("font", "", InputField.ANY, false);
        addAttribute("text", "", InputField.ANY, false);
        addAttribute("onclick", "", InputField.ANY, false);
        addAttribute("onmouseenter", "", InputField.ANY, false);
        addAttribute("onmouseexit", "", InputField.ANY, false);
        addAttribute("textcolorup", "", InputField.ANY, false);
        addAttribute("textcolorupover", "", InputField.ANY, false);
        addAttribute("textcoloruparmed", "", InputField.ANY, false);
        addAttribute("textcolorupdisabled", "", InputField.ANY, false);
        addAttribute("textcolordown", "", InputField.ANY, false);
        addAttribute("textcolordownover", "", InputField.ANY, false);
        addAttribute("textcolordownarmed", "", InputField.ANY, false);
        addAttribute("textcolordowndisabled", "", InputField.ANY, false);
        addAttribute("tooltip", "", InputField.ANY, false);
        addAttribute("overlay", "", InputField.ANY, false);
        addAttribute("screenspace", "", InputField.ANY, false);
        addAttribute("context", "", InputField.ANY, false);
        setNameAttribute(getAttribute2("id"));

        setMetaAttributes(MetaEditorAttribute.parse("id,center,anchor,depth,alpha,rotation,scale,colorize,?Graphics<up,over,down,downover,armed,downarmed,disabled,downdisabled>latch,?Events<onclick,onmouseenter,onmouseexit>?Text<font,text,tooltip,textcolorup,textcolorupover,textcoloruparmed,textcolorupdisabled,textcolordown,textcolordownover,textcolordownarmed,textcolordowndisabled>overlay,screenspace,context,"));
    }

    @Override
    public void update() {
        setNameAttribute(getAttribute2("name"));
        try {
            image = GlobalResourceManager.getImage(getAttribute("up"), Main.getLevel().getVersion());
        } catch (FileNotFoundException e) {
            Main.failedResources.add("\"" + getAttribute("up") + "\" (version " + Main.getLevel().getVersion() + ")");
        }

        ChangeListener<String> wizard = (observable, oldValue, newValue) -> {
            System.out.println("Image changed from " + oldValue + " to " + newValue);
            try {
                image = GlobalResourceManager.getImage(getAttribute("up"), Main.getLevel().getVersion());

                Color colorize = Color.parse(getAttribute("colorize"));
                double rScale = colorize.getR() / 255;
                double gScale = colorize.getG() / 255;
                double bScale = colorize.getB() / 255;

                WritableImage writableImage = new WritableImage((int)image.getWidth(), (int)image.getHeight());
                PixelWriter pixelWriter = writableImage.getPixelWriter();

                int[] pixelBuffer = new int[(int)image.getWidth() * (int)image.getHeight()];

                for (int x = 0; x < image.getWidth() - 1; x++) {
                    for (int y = 0; y < image.getHeight() - 1; y++) {

                        long pixel = (image.getPixelReader().getArgb(x, y));

                        if (pixel < 0) {
                            pixel += 4294967296L;
                        }

                        // AAAAAAAA RRRRRRRR GGGGGGGG BBBBBBBB

                        int pixelA = (int)(pixel / (2 << 23));
                        int pixelR = (int)((pixel % (2 << 23)) / (2 << 15));
                        int pixelG = (int)((pixel % (2 << 15)) / (2 << 7));
                        int pixelB = (int)(pixel % (2 << 7));

                        int scaledR = (int)(pixelR * rScale);
                        int scaledG = (int)(pixelG * gScale);
                        int scaledB = (int)(pixelB * bScale);

                        pixelBuffer[y * (int)image.getWidth() + x] = (pixelA * (2 << 23)) + (scaledR * (2 << 15)) + (scaledG * (2 << 7)) + scaledB;
                    }
                }

                pixelWriter.setPixels(0, 0, (int)image.getWidth(), (int)image.getHeight(), PixelFormat.getIntArgbInstance(), pixelBuffer, 0, (int)image.getWidth());

                image = writableImage;

            } catch (FileNotFoundException e) {
                Main.failedResources.add("\"" + getAttribute("up") + "\" (version " + Main.getLevel().getVersion() + ")");
            }
        };

        setChangeListener("up", wizard);
        setChangeListener("colorize", wizard);
    }

    @Override
    public DragSettings mouseIntersection(double mX2, double mY2) {

        if (Main.getLevel().isShowGraphics() && image != null) {

            double x2 = getDouble("x");
            double y2 = getDouble("y");

            double rotation2 = getDouble("rotation");

            double scaleX = getDouble("scalex");
            double scaleY = getDouble("scaley");

            Point2D rotated = rotate(new Point2D(mX2, mY2), Math.toRadians(rotation2), new Point2D(x2, -y2));

            double mX = rotated.getX();
            double mY = rotated.getY();

            if (mX > x2 - image.getWidth() * scaleX / 2 && mX < x2 + image.getWidth() * scaleX / 2 && mY > -y2 - image.getHeight() * scaleY / 2 && mY < -y2 + image.getHeight() * scaleY / 2) {
                double goodX = (mX - (x2 - image.getWidth() * scaleX / 2)) / scaleX;
                double goodY = (mY - (-y2 - image.getHeight() * scaleY / 2)) / scaleY;
                int pixel = image.getPixelReader().getArgb((int) goodX, (int) goodY);
                if (pixel >> 24 != 0) {
                    DragSettings dragSettings = new DragSettings(DragSettings.MOVE);
                    dragSettings.setInitialSourceX(mX2 - x2);
                    dragSettings.setInitialSourceY(mY2 + y2);
                    return dragSettings;
                }
            }
        }
        return new DragSettings(DragSettings.NONE);
    }

    @Override
    public void draw(GraphicsContext graphicsContext, GraphicsContext imageGraphicsContext){

        if (Main.getLevel().isShowGraphics() && image != null) {

            if (getAttribute("up").equals("") || image == null) {
                return;
            }

            double x = getDouble("x");
            double y = getDouble("y");

            double rotation = getDouble("rotation");

            double scalex = getDouble("scalex");
            double scaley = getDouble("scaley");

            imageGraphicsContext.save();
            Affine t = imageGraphicsContext.getTransform();
            //t.appendScale(scaleX, scaleY, screenX, screenY);
            t.appendRotation(-rotation, x, -y);
            imageGraphicsContext.setTransform(t);

            imageGraphicsContext.setGlobalAlpha(getDouble("alpha"));

            imageGraphicsContext.drawImage(image, x - image.getWidth() * scalex / 2.0, -y - image.getHeight() * scaley / 2.0, image.getWidth() * scalex, image.getHeight() * scaley);

            imageGraphicsContext.restore();

            if (this == Main.getSelected()) {

                Point2D rotated2 = EditorObject.rotate(new Point2D(x - image.getWidth() * scalex / 2, -y - image.getHeight() * scaley / 2), -Math.toRadians(rotation), new Point2D(x, -y));
                Point2D rotated3 = EditorObject.rotate(new Point2D(x + image.getWidth() * scalex / 2, -y + image.getHeight() * scaley / 2), -Math.toRadians(rotation), new Point2D(x, -y));

                Point2D rotated4 = EditorObject.rotate(new Point2D(x - image.getWidth() * scalex / 2, -y), -Math.toRadians(rotation), new Point2D(x, -y));
                Point2D rotated5 = EditorObject.rotate(new Point2D(x + image.getWidth() * scalex / 2, -y), -Math.toRadians(rotation), new Point2D(x, -y));

                Point2D rotated6 = EditorObject.rotate(new Point2D(x - image.getWidth() * scalex / 2, -y + image.getHeight() * scaley / 2), -Math.toRadians(rotation), new Point2D(x, -y));
                Point2D rotated7 = EditorObject.rotate(new Point2D(x + image.getWidth() * scalex / 2, -y - image.getHeight() * scaley / 2), -Math.toRadians(rotation), new Point2D(x, -y));

                double screenX2 = (x) * Main.getLevel().getZoom() + Main.getLevel().getOffsetX();
                double screenY2 = (-y) * Main.getLevel().getZoom() + Main.getLevel().getOffsetY();

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
    public DragSettings mouseIntersectingCorners(double mX2, double mY2) {
        if (Main.getLevel().isShowGraphics() && image != null) {
            double x2 = getDouble("x");
            double y2 = getDouble("y");

            double rotation2 = getDouble("rotation");

            double scaleX = getDouble("scalex");
            double scaleY = getDouble("scaley");

            //image = EditorObject.rotateImageByDegrees(image, -rotation);

            Point2D rotated2 = EditorObject.rotate(new Point2D(x2 - image.getWidth() * scaleX / 2, -y2 - image.getHeight() * scaleY / 2), -Math.toRadians(rotation2), new Point2D(x2, -y2));
            Point2D rotated3 = EditorObject.rotate(new Point2D(x2 + image.getWidth() * scaleX / 2, -y2 + image.getHeight() * scaleY / 2), -Math.toRadians(rotation2), new Point2D(x2, -y2));

            Point2D rotated4 = EditorObject.rotate(new Point2D(x2 - image.getWidth() * scaleX / 2, -y2), -Math.toRadians(rotation2), new Point2D(x2, -y2));
            Point2D rotated5 = EditorObject.rotate(new Point2D(x2 + image.getWidth() * scaleX / 2, -y2), -Math.toRadians(rotation2), new Point2D(x2, -y2));

            Point2D rotated6 = EditorObject.rotate(new Point2D(x2 - image.getWidth() * scaleX / 2, -y2 + image.getHeight() * scaleY / 2), -Math.toRadians(rotation2), new Point2D(x2, -y2));
            Point2D rotated7 = EditorObject.rotate(new Point2D(x2 + image.getWidth() * scaleX / 2, -y2 - image.getHeight() * scaleY / 2), -Math.toRadians(rotation2), new Point2D(x2, -y2));

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
            resizeSettings.setInitialScaleX(scaleX);
            resizeSettings.setInitialScaleY(scaleY);

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
                dragSourceX = x2 - image.getWidth() * scaleX / 2;
                dragSourceY = -y2 - image.getHeight() * scaleY / 2;
                dragAnchorX = x2 + image.getWidth() * scaleX / 2;
                dragAnchorY = -y2 + image.getHeight() * scaleY / 2;
            }
            if (mX2 > screenX4 - distance && mX2 < screenX4 + distance && mY2 > screenY4 - distance && mY2 < screenY4 + distance) {
                resize = true;
                dragSourceX = x2 + image.getWidth() * scaleX / 2;
                dragSourceY = -y2 + image.getHeight() * scaleY / 2;
                dragAnchorX = x2 - image.getWidth() * scaleX / 2;
                dragAnchorY = -y2 - image.getHeight() * scaleY / 2;
            }
            if (mX2 > screenX5 - distance && mX2 < screenX5 + distance && mY2 > screenY5 - distance && mY2 < screenY5 + distance) {
                rotate = true;
                dragSourceX = x2 - image.getWidth() * scaleX / 2;
                dragSourceY = -y2;
                rotateAngleOffset = 0;
            }
            if (mX2 > screenX6 - distance && mX2 < screenX6 + distance && mY2 > screenY6 - distance && mY2 < screenY6 + distance) {
                rotate = true;
                dragSourceX = x2 + image.getWidth() * scaleX / 2;
                dragSourceY = -y2;
                rotateAngleOffset = 180;
            }
            if (mX2 > screenX7 - distance && mX2 < screenX7 + distance && mY2 > screenY7 - distance && mY2 < screenY7 + distance) {
                resize = true;
                dragSourceX = x2 - image.getWidth() * scaleX / 2;
                dragSourceY = -y2 + image.getHeight() * scaleY / 2;
                dragAnchorX = x2 + image.getWidth() * scaleX / 2;
                dragAnchorY = -y2 - image.getHeight() * scaleY / 2;
            }
            if (mX2 > screenX8 - distance && mX2 < screenX8 + distance && mY2 > screenY8 - distance && mY2 < screenY8 + distance) {
                resize = true;
                dragSourceX = x2 + image.getWidth() * scaleX / 2;
                dragSourceY = -y2 - image.getHeight() * scaleY / 2;
                dragAnchorX = x2 - image.getWidth() * scaleX / 2;
                dragAnchorY = -y2 + image.getHeight() * scaleY / 2;
            }

            if (resize) {
                Point2D dragSourceRotated = EditorObject.rotate(new Point2D(dragSourceX, dragSourceY), -Math.toRadians(rotation2), new Point2D(x2, -y2));
                Point2D dragAnchorRotated = EditorObject.rotate(new Point2D(dragAnchorX, dragAnchorY), -Math.toRadians(rotation2), new Point2D(x2, -y2));
                resizeSettings.setInitialSourceX(dragSourceRotated.getX());
                resizeSettings.setInitialSourceY(dragSourceRotated.getY());
                resizeSettings.setAnchorX(dragAnchorRotated.getX());
                resizeSettings.setAnchorY(dragAnchorRotated.getY());
                return resizeSettings;
            }

            if (rotate) {
                Point2D dragSourceRotated = EditorObject.rotate(new Point2D(dragSourceX, dragSourceY), -Math.toRadians(rotation2), new Point2D(x2, -y2));
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

        double rotation = Double.parseDouble(getAttribute("rotation"));

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

        setAttribute("scalex", newScaleX);
        setAttribute("scaley", newScaleY);
        setAttribute("x", center.getX());
        setAttribute("y", -center.getY());
    }

    @Override
    public void rotateFromMouse(double mouseX, double mouseY, double rotateAngleOffset){
        double x2 = Double.parseDouble(getAttribute("x"));
        double y2 = -Double.parseDouble(getAttribute("y"));

        double rotation2 = Math.toDegrees(Renderer.angleTo(new Point2D(mouseX, mouseY), new Point2D(x2, y2)));
        setAttribute("rotation", rotation2 + rotateAngleOffset);
    }
}
