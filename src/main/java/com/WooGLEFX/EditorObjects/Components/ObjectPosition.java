package com.WooGLEFX.EditorObjects.Components;

import javafx.scene.paint.Paint;

public class ObjectPosition {

    public static final int POINT = 0;
    public static final int RECTANGLE = 1;
    public static final int RECTANGLE_HOLLOW = 2;
    public static final int CIRCLE = 3;
    public static final int CIRCLE_HOLLOW = 4;
    public static final int ANCHOR = 5;
    public static final int IMAGE = 6;
    public static final int LINE = 7;


    private final int id;
    public int getId() {
        return id;
    }
    public ObjectPosition(int id) {
        this.id = id;
    }


    public double getX() {
        return 0;
    }
    public void setX(double x) {

    }


    public double getY() {
        return 0;
    }
    public void setY(double y) {

    }


    public double getAnchorX() {
        return 0;
    }
    public double getAnchorY() {
        return 0;
    }
    public void setAnchor(double anchorX, double anchorY) {

    }


    public double getRotation() {
        return 0;
    }
    public void setRotation(double rotation) {

    }


    public double getWidth() {
        return 0;
    }
    public void setWidth(double width) {

    }


    public double getHeight() {
        return 0;
    }
    public void setHeight(double height) {

    }


    public double getRadius() {
        return 0;
    }
    public void setRadius(double radius) {

    }


    public double getEdgeSize() {
        return 0;
    }


    public Paint getBorderColor() {
        return null;
    }
    public Paint getFillColor() {
        return null;
    }

}
