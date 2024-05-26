package com.woogleFX.file.font;

public class FontString extends FontData {

    private final String data;
    public String getData() {
        return data;
    }


    public FontString(String data) {
        this.data = data;
    }


    @Override
    public String toString() {
        return '\"' + data + '\"';
    }

}
