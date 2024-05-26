package com.woogleFX.file.font;

public record FontCommand(String id, FontData[] args) {

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder("[FontCommand] " + id);
        for (FontData data : args) stringBuilder.append(" ").append(data.toString());
        return stringBuilder.toString();
    }

}
