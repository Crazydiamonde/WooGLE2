package com.woogleFX.file.font;

public class FontDouble extends FontData {

    private final double data;
    public double getData() {
        return data;
    }


    public FontDouble(double data) {
        this.data = data;
    }


    @Override
    public String toString() {
        return String.valueOf(data);
    }

}
