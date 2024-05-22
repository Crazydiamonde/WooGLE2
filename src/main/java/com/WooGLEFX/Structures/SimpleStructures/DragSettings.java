package com.WooGLEFX.Structures.SimpleStructures;

import com.WooGLEFX.EditorObjects.objectcomponents.ObjectComponent;

public class DragSettings {

    public static final int NONE = -2;
    public static final int MOVE = 0;
    public static final int RESIZE = 1;
    public static final int ROTATE = 2;
    public static final int SETANCHOR = 3;


    public static final DragSettings NULL = new DragSettings(-1, null);


    private final int type;
    public int getType() {
        return type;
    }


    private final ObjectComponent objectComponent;
    public ObjectComponent getObjectComponent() {
        return objectComponent;
    }


    public DragSettings(int type, ObjectComponent objectComponent) {
        this.type = type;
        this.objectComponent = objectComponent;
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


    private double rotateAngleOffset;
    public double getRotateAngleOffset() {
        return rotateAngleOffset;
    }
    public void setRotateAngleOffset(double rotateAngleOffset) {
        this.rotateAngleOffset = rotateAngleOffset;
    }

}
