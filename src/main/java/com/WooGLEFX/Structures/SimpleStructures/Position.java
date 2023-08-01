package com.WooGLEFX.Structures.SimpleStructures;

public class Position {

    private double x;
    private double y;

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public Position(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public static Position parse(String str){
        try {
            return new Position(Double.parseDouble(str.substring(0, str.indexOf(','))),
                    Double.parseDouble(str.substring(str.indexOf(',') + 1)));
        } catch (Exception e) {
            throw new NumberFormatException();
        }
    }
}
