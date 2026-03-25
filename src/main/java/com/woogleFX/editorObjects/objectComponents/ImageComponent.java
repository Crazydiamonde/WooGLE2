package com.woogleFX.editorObjects.objectComponents;

import com.woogleFX.editorObjects.EditorObject;
import com.woogleFX.editorObjects.ObjectUtil;
import com.woogleFX.editorObjects.objectComponents.generic.BoundedProperty;
import com.woogleFX.editorObjects.objectComponents.generic.RotatableProperty;
import com.woogleFX.engine.renderer.Renderer;
import com.woogleFX.engine.AssetManager;
import com.woogleFX.editorObjects.DragSettings;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.*;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.transform.Affine;

/** Represents an image component in any object. */
public abstract class ImageComponent extends ObjectComponent implements RotatableProperty, BoundedProperty {

    public ImageComponent(EditorObject editorObject) {
        super(editorObject);
    }

    /** Returns this component's horizontal scale. */
    public double getScaleX() {
        return 1;
    }

    /** Sets this component's horizontal scale.
     * @param scaleX This component's horizontal scale. */
    public void setScaleX(double scaleX) {

    }

    /** Returns this component's vertical scale. */
    public double getScaleY() {
        return 1;
    }

    /** Sets this component's vertical scale.
     * @param scaleY This component's vertical scale. */
    public void setScaleY(double scaleY) {

    }

    /** Returns if this image is additive. */
    public boolean isAdditive() {
        return false;
    }

    /** Returns this component's image. */
    public abstract Image getImage();

    /** Returns if this image is from a geometry object. */
    public boolean isGeometryImage() {
        return false;
    }

    public Color getColorize() {
        return Color.WHITE;
    }

    public double getAlpha() {
        return 1.0;
    }

    @Override
    public double getWidth() {
        Image image = getImage();
        if (image == null) return 0;
        return image.getWidth() * getScaleX();
    }

    @Override
    public void setWidth(double width) {
        Image image = getImage();
        if (image == null) return;
        setScaleX(width / image.getWidth());
    }

    @Override
    public double getHeight() {
        Image image = getImage();
        if (image == null) return 0;
        return image.getHeight() * getScaleY();
    }

    @Override
    public void setHeight(double height) {
        Image image = getImage();
        if (image == null) return;
        setScaleY(height / image.getHeight());
    }

    @Override
    public void draw(GraphicsContext graphicsContext) {

        Image image = getImage();
        if (image == null) return;

        double x = getX();
        double y = getY();
        double rotation = getRotation();
        double width = image.getWidth() * getScaleX();
        double height = image.getHeight() * getScaleY();

        double offsetX = AssetManager.getAsset().getOffsetX();
        double offsetY = AssetManager.getAsset().getOffsetY();
        double zoom = AssetManager.getAsset().getZoom();

        // TODO: add some way of previewing depth
        //double depth = getDepth();
        //double depthFactor = Math.exp(-0.01 * Math.abs(depth));
        //double canvasWidth = FXContainers.getSplitPane().getDividers().get(0).getPosition() * FXContainers.getSplitPane().getWidth();
        //double canvasHeight = FXContainers.getSplitPane().getHeight();
        //offsetX = x + (offsetX + canvasWidth / 2 - x) * depthFactor - canvasWidth / 2;
        //offsetY = y + (offsetY + canvasHeight / 2 - y) * depthFactor - canvasHeight / 2;

        graphicsContext.save();

        Affine t = graphicsContext.getTransform();
        t.appendTranslation(offsetX, offsetY);
        t.appendScale(zoom, zoom);
        t.appendRotation(Math.toDegrees(rotation), x, y);
        graphicsContext.setTransform(t);

        graphicsContext.setGlobalAlpha(getAlpha());

        if (isAdditive()) graphicsContext.setGlobalBlendMode(BlendMode.ADD);

        //Blend blend = new Blend(BlendMode.MULTIPLY);
        //blend.setBottomInput(new ColorAdjust(0.0, 1.0, 0.0, 0.0));
        //blend.setTopInput(new ColorInput(x - width / 2.0, y - height / 2.0, width, height, getColorize()));
        //graphicsContext.setEffect(blend);

        graphicsContext.drawImage(image, x - width / 2.0, y - height / 2.0, width, height);

        graphicsContext.restore();

    }

    @Override
    public void drawSelectionOutline(GraphicsContext graphicsContext) {

        Image image = getImage();
        if (image == null) return;

        double x = getX();
        double y = getY();
        double rotation = getRotation();
        double width = image.getWidth() * getScaleX();
        double height = image.getHeight() * getScaleY();

        double offsetX = AssetManager.getAsset().getOffsetX();
        double offsetY = AssetManager.getAsset().getOffsetY();
        double zoom = AssetManager.getAsset().getZoom();

        Point2D topLeft = ObjectUtil.rotate(new Point2D(x - width / 2, y - height / 2), rotation, new Point2D(x, y));
        topLeft = topLeft.multiply(zoom).add(offsetX, offsetY);

        Point2D bottomRight = ObjectUtil.rotate(new Point2D(x + width / 2, y + height / 2), rotation, new Point2D(x, y));
        bottomRight = bottomRight.multiply(zoom).add(offsetX, offsetY);

        Point2D left = ObjectUtil.rotate(new Point2D(x - width / 2, y), rotation, new Point2D(x, y));
        left = left.multiply(zoom).add(offsetX, offsetY);

        Point2D right = ObjectUtil.rotate(new Point2D(x + width / 2, y), rotation, new Point2D(x, y));
        right = right.multiply(zoom).add(offsetX, offsetY);

        Point2D bottomLeft = ObjectUtil.rotate(new Point2D(x - width / 2, y + height / 2), rotation, new Point2D(x, y));
        bottomLeft = bottomLeft.multiply(zoom).add(offsetX, offsetY);

        Point2D topRight = ObjectUtil.rotate(new Point2D(x + width / 2, y - height / 2), rotation, new Point2D(x, y));
        topRight = topRight.multiply(zoom).add(offsetX, offsetY);

        double screenX2 = x * zoom + offsetX;
        double screenY2 = y * zoom + offsetY;

        graphicsContext.setLineWidth(1);
        if (isResizable()) {

            graphicsContext.setStroke(Renderer.selectionOutline);
            if (isGeometryImage()) {
                graphicsContext.fillRect(topLeft.getX() - 4, topLeft.getY() - 4, 8, 8);
                graphicsContext.fillRect(bottomRight.getX() - 4, bottomRight.getY() - 4, 8, 8);
                graphicsContext.fillRect(bottomLeft.getX() - 4, bottomLeft.getY() - 4, 8, 8);
                graphicsContext.fillRect(topRight.getX() - 4, topRight.getY() - 4, 8, 8);
                graphicsContext.setStroke(Renderer.selectionOutline2);
            }
            graphicsContext.strokeRect(topLeft.getX() - 4, topLeft.getY() - 4, 8, 8);
            graphicsContext.strokeRect(bottomRight.getX() - 4, bottomRight.getY() - 4, 8, 8);
            graphicsContext.strokeRect(bottomLeft.getX() - 4, bottomLeft.getY() - 4, 8, 8);
            graphicsContext.strokeRect(topRight.getX() - 4, topRight.getY() - 4, 8, 8);

        }

        if (isRotatable()) {

            graphicsContext.setStroke(Renderer.selectionOutline);
            if (isGeometryImage()) {
                graphicsContext.fillOval(left.getX() - 4, left.getY() - 4, 8, 8);
                graphicsContext.fillOval(right.getX() - 4, right.getY() - 4, 8, 8);
                graphicsContext.setStroke(Renderer.selectionOutline2);
            }
            graphicsContext.strokeOval(left.getX() - 4, left.getY() - 4, 8, 8);
            graphicsContext.strokeOval(right.getX() - 4, right.getY() - 4, 8, 8);

        }

        Affine t2 = graphicsContext.getTransform();
        t2.appendRotation(Math.toDegrees(rotation), screenX2, screenY2);
        graphicsContext.setTransform(t2);

        double absWidth = Math.abs(width);
        double absHeight = Math.abs(height);

        graphicsContext.setStroke(Renderer.selectionOutline2);
        graphicsContext.setLineWidth(1);
        graphicsContext.setLineDashes(3);
        graphicsContext.setLineDashOffset(0);
        graphicsContext.strokeRect(screenX2 - absWidth * zoom / 2, screenY2 - absHeight * zoom / 2, absWidth * zoom, absHeight * zoom);

        graphicsContext.setStroke(Renderer.selectionOutline);
        graphicsContext.setLineWidth(1);
        graphicsContext.setLineDashOffset(3);
        graphicsContext.strokeRect(screenX2 - absWidth * zoom / 2, screenY2 - absHeight * zoom / 2, absWidth * zoom, absHeight * zoom);
        graphicsContext.setLineDashes(0);

    }

    @Override
    public DragSettings mouseIntersection(double mouseX, double mouseY) {

        Image image = getImage();
        if (image == null) return DragSettings.NULL;

        double x = getX();
        double y = getY();
        double rotation = getRotation();
        double width = image.getWidth() * Math.abs(getScaleX());
        double height = image.getHeight() * Math.abs(getScaleY());

        Point2D rotated = ObjectUtil.rotate(new Point2D(mouseX, mouseY), -rotation, new Point2D(x, y));

        double mX = rotated.getX();
        double mY = rotated.getY();

        if (mX > x - width / 2 && mX < x + width / 2 && mY > y - height / 2 && mY < y + height / 2) {
            double goodX = (mX - (x - width / 2)) / (width / image.getWidth());
            double goodY = (mY - (y - height / 2)) / (height / image.getHeight());
            long pixel = image.getPixelReader().getArgb((int)goodX, (int)goodY);
            if ((pixel & 0xFF000000L) * getAlpha() > 0x20000000L) {
                DragSettings dragSettings = new DragSettings(isDraggable() ? DragSettings.MOVE : DragSettings.NONE, this);
                dragSettings.setInitialSource(new Point2D(mouseX - x, mouseY - y));
                dragSettings.setOpacity(((pixel & 0xFF000000L) >> 24) / 255.0 * getAlpha());
                return dragSettings;
            }
        }

        return DragSettings.NULL;

    }


    @Override
    public DragSettings mouseIntersectingCorners(double mouseX, double mouseY) {

        Image image = getImage();
        if (image == null) return DragSettings.NULL;

        double x = getX();
        double y = getY();
        double rotation = getRotation();
        double width = image.getWidth() * Math.abs(getScaleX());
        double height = image.getHeight() * Math.abs(getScaleY());

        Point2D center = new Point2D(x, y);

        Point2D right = new Point2D(x + width / 2, y);
        right = ObjectUtil.rotate(right, rotation, center);
        right = new Point2D(right.getX(), right.getY());

        Point2D left = new Point2D(x - width / 2, y);
        left = ObjectUtil.rotate(left, rotation, center);
        left = new Point2D(left.getX(), left.getY());

        Point2D topRight = new Point2D(x + width / 2, y + height / 2);
        topRight = ObjectUtil.rotate(topRight, rotation, center);

        Point2D topLeft = new Point2D(x - width / 2, y + height / 2);
        topLeft = ObjectUtil.rotate(topLeft, rotation, center);

        Point2D bottomLeft = new Point2D(x - width / 2, y - height / 2);
        bottomLeft = ObjectUtil.rotate(bottomLeft, rotation, center);

        Point2D bottomRight = new Point2D(x + width / 2, y - height / 2);
        bottomRight = ObjectUtil.rotate(bottomRight, rotation, center);

        double distance = 4 / AssetManager.getAsset().getZoom();

        DragSettings resizeSettings = new DragSettings(DragSettings.RESIZE, this);

        DragSettings rotateSettings = new DragSettings(DragSettings.ROTATE, this);

        double dragSourceX = 0;
        double dragSourceY = 0;
        double dragAnchorX = 0;
        double dragAnchorY = 0;
        double rotateAngleOffset = 0;

        boolean resize = false;
        boolean rotate = false;

        if (isResizable() && mouseX > topLeft.getX() - distance && mouseX < topLeft.getX() + distance && mouseY > topLeft.getY() - distance && mouseY < topLeft.getY() + distance) {
            resize  = true;
            dragSourceX = x - width / 2;
            dragSourceY = y + height / 2;
            dragAnchorX = x + width / 2;
            dragAnchorY = y - height / 2;
        }
        if (isResizable() && mouseX > topRight.getX() - distance && mouseX < topRight.getX() + distance && mouseY > topRight.getY() - distance && mouseY < topRight.getY() + distance) {
            resize  = true;
            dragSourceX = x + width / 2;
            dragSourceY = y + height / 2;
            dragAnchorX = x - width / 2;
            dragAnchorY = y - height / 2;
        }
        if (isResizable() && mouseX > bottomLeft.getX() - distance && mouseX < bottomLeft.getX() + distance && mouseY > bottomLeft.getY() - distance && mouseY < bottomLeft.getY() + distance) {
            resize  = true;
            dragSourceX = x - width / 2;
            dragSourceY = y - height / 2;
            dragAnchorX = x + width / 2;
            dragAnchorY = y + height / 2;
        }
        if (isResizable() && mouseX > bottomRight.getX() - distance && mouseX < bottomRight.getX() + distance && mouseY > bottomRight.getY() - distance && mouseY < bottomRight.getY() + distance) {
            resize  = true;
            dragSourceX = x + width / 2;
            dragSourceY = y - height / 2;
            dragAnchorX = x - width / 2;
            dragAnchorY = y + height / 2;
        }

        if (isRotatable() && mouseX > left.getX() - distance && mouseX < left.getX() + distance && mouseY > left.getY() - distance && mouseY < left.getY() + distance) {
            rotate = true;
            dragSourceX = x - width / 2;
            dragSourceY = y;
            dragAnchorX = x;
            dragAnchorY = y;
            rotateAngleOffset = rotation;
        }

        if (isRotatable() && mouseX > right.getX() - distance && mouseX < right.getX() + distance && mouseY > right.getY() - distance && mouseY < right.getY() + distance) {
            rotate = true;
            dragSourceX = x + width / 2;
            dragSourceY = y;
            dragAnchorX = x;
            dragAnchorY = y;
            rotateAngleOffset = rotation;
        }

        if (resize) {
            Point2D dragSourceRotated = ObjectUtil.rotate(new Point2D(dragSourceX, dragSourceY), rotation, new Point2D(x, y));
            Point2D dragAnchorRotated = ObjectUtil.rotate(new Point2D(dragAnchorX, dragAnchorY), rotation, new Point2D(x, y));
            resizeSettings.setInitialSource(dragSourceRotated);
            resizeSettings.setAnchor(dragAnchorRotated);
            resizeSettings.setInitialScale(new Point2D(getScaleX(), getScaleY()));
            return resizeSettings;
        }

        if (rotate) {
            Point2D dragSourceRotated = ObjectUtil.rotate(new Point2D(dragSourceX, dragSourceY), rotation, new Point2D(x, y));
            rotateSettings.setInitialSource(dragSourceRotated);
            rotateSettings.setAnchor(new Point2D(dragAnchorX, dragAnchorY));
            rotateSettings.setRotateAngleOffset(rotateAngleOffset);
            return rotateSettings;
        }

        return DragSettings.NULL;

    }

}
