package com.WooGLEFX.Structures.SimpleStructures;

public class Color {
    private double a;
    private double r;
    private double g;
    private double b;

    public double getA() {
        return a;
    }

    public void setA(double a) {
        this.a = a;
    }


    public double getR() {
        return r;
    }

    public void setR(double r) {
        this.r = r;
    }

    public double getG() {
        return g;
    }

    public void setG(double g) {
        this.g = g;
    }

    public double getB() {
        return b;
    }

    public void setB(double b) {
        this.b = b;
    }

    public Color(double r, double g, double b){
        this.a = -1;
        this.r = r;
        this.g = g;
        this.b = b;
    }

    public Color(double a, double r, double g, double b){
        this.a = a;
        this.r = r;
        this.g = g;
        this.b = b;
    }

    public static Color parse(String input){
        try {
            int count = 0;
            for (int i = 0; i < input.length(); i++) {
                if (input.charAt(i) == ',') {
                    count++;
                }
            }
            if (count == 2) {
                String after = input.substring(input.indexOf(",") + 1);
                return new Color(Double.parseDouble(input.substring(0, input.indexOf(","))),
                        Double.parseDouble(after.substring(0, after.indexOf(","))),
                        Double.parseDouble(after.substring(after.indexOf(",") + 1)));
            } else if (count == 3) {
                String after = input.substring(input.indexOf(",") + 1);
                String after2 = after.substring(after.indexOf(",") + 1);
                return new Color(Double.parseDouble(input.substring(0, input.indexOf(","))),
                        Double.parseDouble(after.substring(0, after.indexOf(","))),
                        Double.parseDouble(after2.substring(0, after2.indexOf(","))),
                        Double.parseDouble(after2.substring(after2.indexOf(",") + 1)));
            } else {
                throw new NumberFormatException();
            }
        } catch (Exception e){
            throw new NumberFormatException();
        }
    }
}
