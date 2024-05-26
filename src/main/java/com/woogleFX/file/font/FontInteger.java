package com.woogleFX.file.font;

public class FontInteger extends FontData {

    private final int data;
    public int getData() {
        return data;
    }


    public FontInteger(int data) {
        this.data = data;
    }


    @Override
    public String toString() {
        return String.valueOf(data);
    }

}
