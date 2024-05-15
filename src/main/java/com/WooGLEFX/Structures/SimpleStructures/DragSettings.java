package com.WooGLEFX.Structures.SimpleStructures;

import com.WooGLEFX.EditorObjects.Components.ObjectPosition;

public class DragSettings {

    public static final int MOVE = 0;
    public static final int RESIZE = 1;
    public static final int ROTATE = 2;
    public static final int SETANCHOR = 3;


    public static final DragSettings NULL = new DragSettings(-1);


    private final int type;
    public int getType() {
        return type;
    }
    public DragSettings(int type) {
        this.type = type;
    }


    private double anchorX;
    public double getAnchorX() {
        return anchorX;
    }
    public void setAnchorX(double anchorX) {
        this.anchorX = anchorX;
    }


    private double anchorY;
    public double getAnchorY() {
        return anchorY;
    }
    public void setAnchorY(double anchorY) {
        this.anchorY = anchorY;
    }


    private double initialSourceX;
    public double getInitialSourceX() {
        return initialSourceX;
    }
    public void setInitialSourceX(double initialSourceX) {
        this.initialSourceX = initialSourceX;
    }


    private double initialSourceY;
    public double getInitialSourceY() {
        return initialSourceY;
    }
    public void setInitialSourceY(double initialSourceY) {
        this.initialSourceY = initialSourceY;
    }


    private double initialScaleX;
    public double getInitialScaleX() {
        return initialScaleX;
    }
    public void setInitialScaleX(double initialScaleX) {
        this.initialScaleX = initialScaleX;
    }


    private double initialScaleY;
    public double getInitialScaleY() {
        return initialScaleY;
    }
    public void setInitialScaleY(double initialScaleY) {
        this.initialScaleY = initialScaleY;
    }


    private double rotateAngleOffset;
    public double getRotateAngleOffset() {
        return rotateAngleOffset;
    }
    public void setRotateAngleOffset(double rotateAngleOffset) {
        this.rotateAngleOffset = rotateAngleOffset;
    }


    private ObjectPosition objectPosition;
    public ObjectPosition getObjectPosition() {
        return objectPosition;
    }
    public void setObjectPosition(ObjectPosition objectPosition) {
        this.objectPosition = objectPosition;
    }

}
