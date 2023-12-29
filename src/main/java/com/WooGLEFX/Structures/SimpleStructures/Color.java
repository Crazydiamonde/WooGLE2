package com.WooGLEFX.Structures.SimpleStructures;

import java.util.HexFormat;

public class Color {
    private final int a;
    private final int r;
    private final int g;
    private final int b;

    public double getA() {
        return a;
    }


    public int getR() {
        return r;
    }

    public int getG() {
        return g;
    }

    public int getB() {
        return b;
    }

    public Color(int r, int g, int b){
        this.a = 255;
        this.r = r;
        this.g = g;
        this.b = b;
    }

    public Color(int a, int r, int g, int b){
        this.a = a;
        this.r = r;
        this.g = g;
        this.b = b;
    }

    public static Color parse(String input){
        // Remove all spaces from the string
        input = input.replaceAll("\\s+","");
        try {
            int count = 0;
            for (int i = 0; i < input.length(); i++) {
                if (input.charAt(i) == ',') {
                    count++;
                }
            }
            int first = Integer.parseInt(input.substring(0, input.indexOf(",")));
            if (count == 2) {
                String after = input.substring(input.indexOf(",") + 1);
                return new Color(first,
                        Integer.parseInt(after.substring(0, after.indexOf(","))),
                        Integer.parseInt(after.substring(after.indexOf(",") + 1)));
            } else if (count == 3) {
                String after = input.substring(input.indexOf(",") + 1);
                String after2 = after.substring(after.indexOf(",") + 1);
                return new Color(first,
                        Integer.parseInt(after.substring(0, after.indexOf(","))),
                        Integer.parseInt(after2.substring(0, after2.indexOf(","))),
                        Integer.parseInt(after2.substring(after2.indexOf(",") + 1)));
            } else {
                throw new NumberFormatException("Invalid color format: " + input);
            }
        } catch (Exception e){
            throw e;
        }
    }

    public String toHexRGBA() {
        return HexFormat.of().toHexDigits(r * (2 << 23) + g * (2 << 15) + b * (2 << 7) + a);
    }
}
