package com.WooGLEFX.Structures;

public abstract class GameVersion {

    private final double version;

    public GameVersion(double version) {
        this.version = version;
    }


    public static GameVersion OLD = new GameVersion(1.3) {
    };


    public static GameVersion NEW = new GameVersion(1.5) {
    };

}
