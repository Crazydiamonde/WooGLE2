package com.woogleFX.file.font;

public class FontKeyword extends FontData {

    private final String keyword;
    public String getKeyword() {
        return keyword;
    }


    public FontKeyword(String keyword) {
        this.keyword = keyword;
    }


    @Override
    public String toString() {
        return keyword;
    }

}
