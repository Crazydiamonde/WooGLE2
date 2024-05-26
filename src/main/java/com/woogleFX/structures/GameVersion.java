package com.woogleFX.structures;

public enum GameVersion {

    OLD, NEW;


    @Override
    public String toString() {
        if (this == OLD) return "1.3";
        if (this == NEW) return "1.5";
        return null;
    }

}
