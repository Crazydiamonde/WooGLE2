package com.woogleFX.editorObjects;

import com.woogleFX.editorObjects.objectComponents.ObjectComponent;
import javafx.geometry.Point2D;

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

    private Point2D[] originalPositions;
    public Point2D[] getOriginalPositions() {
        return originalPositions;
    }
    public void setOriginalPositions(Point2D[] originalPositions) {
        this.originalPositions = originalPositions;
    }


    private Point2D[] originalSizes;
    public Point2D[] getOriginalSizes() {
        return originalSizes;
    }
    public void setOriginalSizes(Point2D[] originalSizes) {
        this.originalSizes = originalSizes;
    }


    private double[] originalRotations;
    public double[] getOriginalRotations() {
        return originalRotations;
    }
    public void setOriginalRotations(double[] originalRotations) {
        this.originalRotations = originalRotations;
    }


    private Point2D anchor;
    public Point2D getAnchor() {
        return anchor;
    }
    public void setAnchor(Point2D anchor) {
        this.anchor = anchor;
    }


    private Point2D initialSource;
    public Point2D getInitialSource() {
        return initialSource;
    }
    public void setInitialSource(Point2D initialSource) {
        this.initialSource = initialSource;
    }


    private Point2D initialScale = new Point2D(1, 1);
    public Point2D getInitialScale() {
        return initialScale;
    }
    public void setInitialScale(Point2D initialScale) {
        this.initialScale = initialScale;
    }


    private double rotateAngleOffset;
    public double getRotateAngleOffset() {
        return rotateAngleOffset;
    }
    public void setRotateAngleOffset(double rotateAngleOffset) {
        this.rotateAngleOffset = rotateAngleOffset;
    }


    private double opacity = 1.0;
    public double getOpacity() {
        return opacity;
    }
    public void setOpacity(double opacity) {
        this.opacity = opacity;
    }

}
