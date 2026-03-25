package com.woogleFX.gameData.font;

public class FontList extends FontData {

    private final FontData[] data;
    public FontData[] getData() {
        return data;
    }


    public FontList(FontData[] data) {
        this.data = data;
    }


    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (FontData fontData : data) {
            stringBuilder.append(", ").append(fontData.toString());
        }
        if (data.length == 0) return "[]";
        return '[' + stringBuilder.substring(2) + ']';
    }

}
