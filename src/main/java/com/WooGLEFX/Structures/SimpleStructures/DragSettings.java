package com.WooGLEFX.Structures.SimpleStructures;

public class DragSettings {

    public static final int NONE = -1;
    public static final int MOVE = 0;
    public static final int RESIZE = 1;
    public static final int ROTATE = 2;
    public static final int SETANCHOR = 3;

    private final int type;

    public int getType() {
        return type;
    }

    public DragSettings(int type) {
        this.type = type;
    }

    private double anchorX;
    private double anchorY;
    private double initialSourceX;
    private double initialSourceY;
    private double initialScaleX;
    private double initialScaleY;
    private double rotateAngleOffset;
    private boolean draggingImage = false;

    public double getAnchorX() {
        return anchorX;
    }

    public void setAnchorX(double anchorX) {
        this.anchorX = anchorX;
    }

    public double getAnchorY() {
        return anchorY;
    }

    public void setAnchorY(double anchorY) {
        this.anchorY = anchorY;
    }

    public double getInitialSourceX() {
        return initialSourceX;
    }

    public void setInitialSourceX(double initialSourceX) {
        this.initialSourceX = initialSourceX;
    }

    public double getInitialSourceY() {
        return initialSourceY;
    }

    public void setInitialSourceY(double initialSourceY) {
        this.initialSourceY = initialSourceY;
    }

    public double getInitialScaleX() {
        return initialScaleX;
    }

    public void setInitialScaleX(double initialScaleX) {
        this.initialScaleX = initialScaleX;
    }

    public double getInitialScaleY() {
        return initialScaleY;
    }

    public void setInitialScaleY(double initialScaleY) {
        this.initialScaleY = initialScaleY;
    }

    public double getRotateAngleOffset() {
        return rotateAngleOffset;
    }

    public void setRotateAngleOffset(double rotateAngleOffset) {
        this.rotateAngleOffset = rotateAngleOffset;
    }

    public boolean isDraggingImage() {
        return draggingImage;
    }

    public void setDraggingImage(boolean draggingImage) {
        this.draggingImage = draggingImage;
    }
}
