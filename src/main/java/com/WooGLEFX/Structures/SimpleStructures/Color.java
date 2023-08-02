package com.WooGLEFX.Structures.SimpleStructures;

public class Color {
    private final double a;
    private final double r;
    private final double g;
    private final double b;

    public double getA() {
        return a;
    }


    public double getR() {
        return r;
    }

    public double getG() {
        return g;
    }

    public double getB() {
        return b;
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
            double first = Double.parseDouble(input.substring(0, input.indexOf(",")));
            if (count == 2) {
                String after = input.substring(input.indexOf(",") + 1);
                return new Color(first,
                        Double.parseDouble(after.substring(0, after.indexOf(","))),
                        Double.parseDouble(after.substring(after.indexOf(",") + 1)));
            } else if (count == 3) {
                String after = input.substring(input.indexOf(",") + 1);
                String after2 = after.substring(after.indexOf(",") + 1);
                return new Color(first,
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
