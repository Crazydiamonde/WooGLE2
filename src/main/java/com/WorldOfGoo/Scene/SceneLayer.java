package com.WorldOfGoo.Scene;

import java.io.FileNotFoundException;

import com.WooGLEFX.Functions.LevelManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.WooGLEFX.Engine.Main;
import com.WooGLEFX.Engine.Renderer;
import com.WooGLEFX.File.GlobalResourceManager;
import com.WooGLEFX.Structures.EditorObject;
import com.WooGLEFX.Structures.InputField;
import com.WooGLEFX.Structures.SimpleStructures.Color;
import com.WooGLEFX.Structures.SimpleStructures.DragSettings;
import com.WooGLEFX.Structures.SimpleStructures.Keyframe;
import com.WooGLEFX.Structures.SimpleStructures.MetaEditorAttribute;
import com.WooGLEFX.Structures.SimpleStructures.WoGAnimation;

import javafx.beans.value.ChangeListener;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.transform.Affine;

public class SceneLayer extends EditorObject {

    private static final Logger logger = LoggerFactory.getLogger(SceneLayer.class);


    private double animx = 0;
    private double animy = 0;
    private double animrotation = 0;
    private double animscalex = 1;
    private double animscaley = 1;

    private Image image;

    public SceneLayer(EditorObject _parent) {
        super(_parent);
        setRealName("SceneLayer");
        addAttribute("id", "", InputField.ANY, false);
        addAttribute("name", "", InputField.ANY, false);
        addAttribute("depth", "0", InputField.NUMBER, true);
        addAttribute("x", "0", InputField.NUMBER, true);
        addAttribute("y", "0", InputField.NUMBER, true);
        addAttribute("scalex", "1", InputField.NUMBER, false);
        addAttribute("scaley", "1", InputField.NUMBER, false);
        addAttribute("rotation", "0", InputField.NUMBER, false);
        addAttribute("alpha", "1", InputField.NUMBER, false);
        addAttribute("colorize", "255,255,255", InputField.COLOR, false);
        addAttribute("image", "", InputField.IMAGE_REQUIRED, true);
        addAttribute("tilex", "false", InputField.FLAG, false);
        addAttribute("tiley", "false", InputField.FLAG, false);
        addAttribute("tilecountx", "0", InputField.NUMBER, false);
        addAttribute("tilecounty", "0", InputField.NUMBER, false);
        addAttribute("anim", "", InputField.ANIMATION, false);
        addAttribute("animspeed", "1", InputField.NUMBER, false);
        addAttribute("animdelay", "0", InputField.NUMBER, false);
        addAttribute("animloop", "false", InputField.FLAG, false);
        addAttribute("anchor", "", InputField.ANY, false); //geometry specific?
        addAttribute("context", "screen", InputField.ANY, false); //"screen" is the only option?
        setNameAttribute(getAttribute2("name"));
        setMetaAttributes(MetaEditorAttribute.parse("id,name,x,y,scalex,scaley,rotation,Image<image,depth,tilex,tiley,tilecountx,tilecounty,alpha,colorize,anchor,context>?Anim<anim,animspeed,animdelay,animloop>"));
    }

    public static Image colorize(Image image, Color colorize) {
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

        return writableImage;
    }

    @Override
    public void update() {
        if (!getAttribute("image").equals("")) {
            try {
                image = GlobalResourceManager.getImage(getAttribute("image"), getLevel().getVersion());
                Color color = Color.parse(getAttribute("colorize"));
                image = colorize(image, color);
            } catch (FileNotFoundException e) {
                image = null;
                if (!Main.failedResources.contains("From SceneLayer: \"" + getAttribute("image") + "\" (version " + LevelManager.getLevel().getVersion() + ")")) {
                    Main.failedResources.add("From SceneLayer: \"" + getAttribute("image") + "\" (version " + LevelManager.getLevel().getVersion() + ")");
                }
            }
        }

        ChangeListener<String> wizard = (observable, oldValue, newValue) -> {
            logger.trace("Image changed from " + oldValue + " to " + newValue);
            try {
                image = GlobalResourceManager.getImage(getAttribute("image"), LevelManager.getLevel().getVersion());
                Color color = Color.parse(getAttribute("colorize"));
                image = colorize(image, color);

            } catch (FileNotFoundException e) {
                image = null;
                if (!Main.failedResources.contains("From SceneLayer: \"" + getAttribute("image") + "\" (version " + LevelManager.getLevel().getVersion() + ")")) {
                    Main.failedResources.add("From SceneLayer: \"" + getAttribute("image") + "\" (version " + LevelManager.getLevel().getVersion() + ")");
                }
            }
        };

        setChangeListener("image", wizard);
        setChangeListener("colorize", wizard);
    }

    @Override
    public DragSettings mouseIntersection(double mX2, double mY2) {

        if (LevelManager.getLevel().isShowGraphics() && image != null) {

            double x2 = getDouble("x");
            double y2 = getDouble("y");

            double rotation2 = getDouble("rotation");

            double scaleX = getDouble("scalex");
            double scaleY = getDouble("scaley");

            double rotation = rotation2;
            if (LevelManager.getLevel().isShowAnimations()) {
                rotation += animrotation;
            }

            double scalex = scaleX;
            double scaley = scaleY;
            if (LevelManager.getLevel().isShowAnimations()) {
                scalex *= animscalex;
                scaley *= animscaley;
            }

            Point2D rotated2 = EditorObject.rotate(new Point2D(animx, animy), -Math.toRadians(rotation2), Point2D.ZERO);

            double x = x2;
            double y = y2;
            if (LevelManager.getLevel().isShowAnimations()) {
                x += rotated2.getX();
                y -= rotated2.getY();
            }

            Point2D rotated = rotate(new Point2D(mX2, mY2), Math.toRadians(rotation), new Point2D(x, -y));

            double mX = rotated.getX();
            double mY = rotated.getY();

            double minX = x - image.getWidth() * scalex / 2;
            double maxX = x + image.getWidth() * scalex / 2;
            double minY = -y - image.getHeight() * scaley / 2;
            double maxY = -y + image.getHeight() * scaley / 2;

            if ((mX > minX && mX < maxX || mX > maxX && mX < minX) && (mY > minY && mY < maxY || mY > maxY && mY < minY)) {
                double goodX = (mX - (x - image.getWidth() * scalex / 2)) / scalex;
                double goodY = (mY - (-y - image.getHeight() * scaley / 2)) / scaley;
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

        if (LevelManager.getLevel().isShowGraphics() && image != null) {

            if (getAttribute("image").equals("") || image == null) {
                return;
            }

            double x = getDouble("x");
            double y = getDouble("y");

            double rotation = getDouble("rotation");

            double scalex = getDouble("scalex");
            double scaley = getDouble("scaley");

            double rotation2 = rotation;
            if (LevelManager.getLevel().isShowAnimations()) {
                rotation2 += animrotation;
            }

            double scaleX = scalex;
            double scaleY = scaley;
            if (LevelManager.getLevel().isShowAnimations()) {
                scaleX *= animscalex;
                scaleY *= animscaley;
            }

            Point2D rotated = EditorObject.rotate(new Point2D(animx, animy), -Math.toRadians(rotation2), Point2D.ZERO);

            double x2 = x;
            double y2 = y;
            if (LevelManager.getLevel().isShowAnimations()) {
                x2 += rotated.getX();
                y2 -= rotated.getY();
            }

            imageGraphicsContext.save();
            Affine t = imageGraphicsContext.getTransform();
            //t.appendScale(scaleX, scaleY, screenX, screenY);
            t.appendRotation(-rotation2, x2, -y2);
            imageGraphicsContext.setTransform(t);

            imageGraphicsContext.setGlobalAlpha(getDouble("alpha"));

            imageGraphicsContext.drawImage(image, x2 - image.getWidth() * scaleX / 2.0, -y2 - image.getHeight() * scaleY / 2.0, image.getWidth() * scaleX, image.getHeight() * scaleY);

            imageGraphicsContext.restore();

            if (this == Main.getSelected()) {

                Point2D rotated2 = EditorObject.rotate(new Point2D(x2 - image.getWidth() * scalex / 2, -y2 - image.getHeight() * scaley / 2), -Math.toRadians(rotation2), new Point2D(x2, -y2));
                Point2D rotated3 = EditorObject.rotate(new Point2D(x2 + image.getWidth() * scalex / 2, -y2 + image.getHeight() * scaley / 2), -Math.toRadians(rotation2), new Point2D(x2, -y2));

                Point2D rotated4 = EditorObject.rotate(new Point2D(x2 - image.getWidth() * scalex / 2, -y2), -Math.toRadians(rotation2), new Point2D(x2, -y2));
                Point2D rotated5 = EditorObject.rotate(new Point2D(x2 + image.getWidth() * scalex / 2, -y2), -Math.toRadians(rotation2), new Point2D(x2, -y2));

                Point2D rotated6 = EditorObject.rotate(new Point2D(x2 - image.getWidth() * scalex / 2, -y2 + image.getHeight() * scaley / 2), -Math.toRadians(rotation2), new Point2D(x2, -y2));
                Point2D rotated7 = EditorObject.rotate(new Point2D(x2 + image.getWidth() * scalex / 2, -y2 - image.getHeight() * scaley / 2), -Math.toRadians(rotation2), new Point2D(x2, -y2));

                double screenX2 = (x2) * LevelManager.getLevel().getZoom() + LevelManager.getLevel().getOffsetX();
                double screenY2 = (-y2) * LevelManager.getLevel().getZoom() + LevelManager.getLevel().getOffsetY();

                graphicsContext.save();
                Affine t2 = graphicsContext.getTransform();
                t2.appendRotation(-rotation2, screenX2, screenY2);
                graphicsContext.setTransform(t2);

                graphicsContext.setStroke(Renderer.selectionOutline);
                graphicsContext.setLineWidth(1);
                graphicsContext.setLineDashes(3);

                double screenX3 = (rotated2.getX()) * LevelManager.getLevel().getZoom() + LevelManager.getLevel().getOffsetX();
                double screenY3 = (rotated2.getY()) * LevelManager.getLevel().getZoom() + LevelManager.getLevel().getOffsetY();

                double screenX4 = (rotated3.getX()) * LevelManager.getLevel().getZoom() + LevelManager.getLevel().getOffsetX();
                double screenY4 = (rotated3.getY()) * LevelManager.getLevel().getZoom() + LevelManager.getLevel().getOffsetY();

                double screenX5 = (rotated4.getX()) * LevelManager.getLevel().getZoom() + LevelManager.getLevel().getOffsetX();
                double screenY5 = (rotated4.getY()) * LevelManager.getLevel().getZoom() + LevelManager.getLevel().getOffsetY();

                double screenX6 = (rotated5.getX()) * LevelManager.getLevel().getZoom() + LevelManager.getLevel().getOffsetX();
                double screenY6 = (rotated5.getY()) * LevelManager.getLevel().getZoom() + LevelManager.getLevel().getOffsetY();

                double screenX7 = (rotated6.getX()) * LevelManager.getLevel().getZoom() + LevelManager.getLevel().getOffsetX();
                double screenY7 = (rotated6.getY()) * LevelManager.getLevel().getZoom() + LevelManager.getLevel().getOffsetY();

                double screenX8 = (rotated7.getX()) * LevelManager.getLevel().getZoom() + LevelManager.getLevel().getOffsetX();
                double screenY8 = (rotated7.getY()) * LevelManager.getLevel().getZoom() + LevelManager.getLevel().getOffsetY();

                graphicsContext.setStroke(Renderer.selectionOutline2);
                graphicsContext.setLineWidth(1);
                graphicsContext.setLineDashes(3);
                graphicsContext.setLineDashOffset(0);
                graphicsContext.strokeRect(screenX2 - image.getWidth() * Math.abs(scalex) * LevelManager.getLevel().getZoom() / 2, screenY2 - image.getHeight() * Math.abs(scaley) * LevelManager.getLevel().getZoom() / 2, image.getWidth() * Math.abs(scalex) * LevelManager.getLevel().getZoom(), image.getHeight() * Math.abs(scaley) * LevelManager.getLevel().getZoom());
                graphicsContext.setStroke(Renderer.selectionOutline);
                graphicsContext.setLineWidth(1);
                graphicsContext.setLineDashOffset(3);
                graphicsContext.strokeRect(screenX2 - image.getWidth() * Math.abs(scalex) * LevelManager.getLevel().getZoom() / 2, screenY2 - image.getHeight() * Math.abs(scaley) * LevelManager.getLevel().getZoom() / 2, image.getWidth() * Math.abs(scalex) * LevelManager.getLevel().getZoom(), image.getHeight() * Math.abs(scaley) * LevelManager.getLevel().getZoom());
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
        if (LevelManager.getLevel().isShowGraphics() && image != null) {
            double x2 = getDouble("x");
            double y2 = getDouble("y");

            double rotation2 = getDouble("rotation");

            double scaleX = getDouble("scalex");
            double scaleY = getDouble("scaley");

            double rotation = rotation2;
            if (LevelManager.getLevel().isShowAnimations()) {
                rotation += animrotation;
            }

            double scalex = scaleX;
            double scaley = scaleY;
            if (LevelManager.getLevel().isShowAnimations()) {
                scalex *= animscalex;
                scaley *= animscaley;
            }

            Point2D rotated = EditorObject.rotate(new Point2D(animx, animy), -Math.toRadians(rotation2), Point2D.ZERO);

            double x = x2;
            double y = y2;
            if (LevelManager.getLevel().isShowAnimations()) {
                x += rotated.getX();
                y -= rotated.getY();
            }

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

            double distance = 4 / LevelManager.getLevel().getZoom();

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
                // Upper left corner
                resize = true;
                dragSourceX = x - image.getWidth() * scaleX / 2;
                dragSourceY = -y - image.getHeight() * scaleY / 2;
                dragAnchorX = x + image.getWidth() * scaleX / 2;
                dragAnchorY = -y + image.getHeight() * scaleY / 2;
            }
            if (mX2 > screenX4 - distance && mX2 < screenX4 + distance && mY2 > screenY4 - distance && mY2 < screenY4 + distance) {
                // Lower right corner
                resize = true;
                dragSourceX = x + image.getWidth() * scaleX / 2;
                dragSourceY = -y + image.getHeight() * scaleY / 2;
                dragAnchorX = x - image.getWidth() * scaleX / 2;
                dragAnchorY = -y - image.getHeight() * scaleY / 2;
                if (scaleX < 0) {
                    dragSourceX *= -1;
                }
            }
            if (mX2 > screenX5 - distance && mX2 < screenX5 + distance && mY2 > screenY5 - distance && mY2 < screenY5 + distance) {
                // Left side
                rotate = true;
                dragSourceX = x - image.getWidth() * scalex / 2;
                dragSourceY = -y;
                rotateAngleOffset = 0;
            }
            if (mX2 > screenX6 - distance && mX2 < screenX6 + distance && mY2 > screenY6 - distance && mY2 < screenY6 + distance) {
                // Right side
                rotate = true;
                dragSourceX = x + image.getWidth() * scaleX / 2;
                dragSourceY = -y;
                rotateAngleOffset = 180;
            }
            if (mX2 > screenX7 - distance && mX2 < screenX7 + distance && mY2 > screenY7 - distance && mY2 < screenY7 + distance) {
                // Lower left corner
                resize = true;
                dragSourceX = x - image.getWidth() * scaleX / 2;
                dragSourceY = -y + image.getHeight() * scaleY / 2;
                dragAnchorX = x + image.getWidth() * scaleX / 2;
                dragAnchorY = -y - image.getHeight() * scaleY / 2;
            }
            if (mX2 > screenX8 - distance && mX2 < screenX8 + distance && mY2 > screenY8 - distance && mY2 < screenY8 + distance) {
                // Upper right corner
                resize = true;
                dragSourceX = x + image.getWidth() * scaleX / 2;
                dragSourceY = -y - image.getHeight() * scaleY / 2;
                dragAnchorX = x - image.getWidth() * scaleX / 2;
                dragAnchorY = -y + image.getHeight() * scaleY / 2;
                // if (scaleX < 0) {
                //     dragSourceX *= -1;
                // }
            }

            if (resize) {
                Point2D dragSourceRotated = EditorObject.rotate(new Point2D(dragSourceX, dragSourceY), -Math.toRadians(rotation), new Point2D(x, -y));
                Point2D dragAnchorRotated = EditorObject.rotate(new Point2D(dragAnchorX, dragAnchorY), -Math.toRadians(rotation), new Point2D(x, -y));
                resizeSettings.setInitialSourceX(dragSourceRotated.getX());
                resizeSettings.setInitialSourceY(dragSourceRotated.getY());
                resizeSettings.setAnchorX(dragAnchorRotated.getX());
                resizeSettings.setAnchorY(dragAnchorRotated.getY());
                return resizeSettings;
            }

            if (rotate) {
                Point2D dragSourceRotated = EditorObject.rotate(new Point2D(dragSourceX, dragSourceY), -Math.toRadians(rotation), new Point2D(x, -y));
                rotateSettings.setInitialSourceX(dragSourceRotated.getX());
                rotateSettings.setInitialSourceY(dragSourceRotated.getY());
                if (scaleX < 0) {
                    rotateAngleOffset += 180;
                }
                rotateSettings.setRotateAngleOffset(rotateAngleOffset);
                return rotateSettings;
            }
        }
        return new DragSettings(DragSettings.NONE);
    }


    private static double lerp(double a, double b, double c) {
        return a + (b - a) * c;
    }


    public void updateWithAnimation(WoGAnimation animation, float timer){
        double animspeed = getDouble("animspeed");
        double animdelay = getDouble("animdelay");
        double goodTimer = (timer * animspeed - animdelay);
        if (goodTimer >= 0) {
            goodTimer %= animation.getFrameTimes()[animation.getFrameTimes().length - 1];
        } else {
            while (goodTimer < 0){
                goodTimer += animation.getFrameTimes()[animation.getFrameTimes().length - 1];
            }
        }
        for (int i2 = 0; i2 < 3; i2++) {
            if (animation.getTransformFrames()[i2].length > 0) {
                int i = 0;
                for (int i3 = 0; i3 < animation.getFrameTimes().length; i3++){
                    if (goodTimer < animation.getFrameTimes()[i3] && animation.getTransformFrames()[i2][i3] != null){
                        break;
                    } else if (animation.getTransformFrames()[i2][i3] != null){
                        i = i3;
                    }
                }
                Keyframe currentFrame = animation.getTransformFrames()[i2][i];
                Keyframe nextFrame;
                int nextIndex = currentFrame.getNextFrameIndex();
                if (currentFrame.getNextFrameIndex() == -1){
                    nextIndex = 0;
                    nextFrame = currentFrame;
                } else {
                    nextFrame = animation.getTransformFrames()[i2][currentFrame.getNextFrameIndex()];
                }
                float timerInterpolateValue = reverseInterpolate(animation.getFrameTimes()[i], animation.getFrameTimes()[nextIndex], (float)goodTimer);
                if (i2 == 0) {
                    animscalex = lerp(currentFrame.getX(), nextFrame.getX(), timerInterpolateValue);
                    animscaley = lerp(currentFrame.getY(), nextFrame.getY(), timerInterpolateValue);
                } else if (i2 == 1) {
                    animrotation = lerp(currentFrame.getAngle(), nextFrame.getAngle(), timerInterpolateValue);
                } else {
                    animx = lerp(currentFrame.getX(), nextFrame.getX(), timerInterpolateValue);
                    animy = lerp(currentFrame.getY(), nextFrame.getY(), timerInterpolateValue);
                }
            }
        }
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

        double scaleX = getDouble("scalex");
        // if (scaleX < 0) {
        //     rotation += 180;
        // }
        if (rotatedAnchor.getX() > rotatedSource.getX()) {
            if (scaleX < 0 && mouseX < rotatedReal.getX()) {
                newWidth = rotatedReal.getX() - rotatedAnchor.getX();
            } else {
                newWidth = rotatedAnchor.getX() - rotatedReal.getX();
            }
        } else {
            if (scaleX < 0 && mouseX < rotatedReal.getX()) {
                newWidth = rotatedAnchor.getX() - rotatedReal.getX();
            } else {
                newWidth = rotatedReal.getX() - rotatedAnchor.getX();
            }
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
        double x2 = getDouble("x");
        double y2 = -getDouble("y");

        if (getParent() instanceof Compositegeom){
            x2 += getParent().getDouble("x");
            y2 -= getParent().getDouble("y");
        }

        double rotation2 = Math.toDegrees(Renderer.angleTo(new Point2D(mouseX, mouseY), new Point2D(x2, y2)));
        setAttribute("rotation", rotation2 + rotateAngleOffset);
    }
}
