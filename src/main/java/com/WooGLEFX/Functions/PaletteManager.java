package com.WooGLEFX.Functions;

import com.WooGLEFX.Structures.GameVersion;

import java.util.ArrayList;

public class PaletteManager {

    private static final ArrayList<String> paletteBalls = new ArrayList<>();
    public static ArrayList<String> getPaletteBalls() {
        return paletteBalls;
    }
    public static void addPaletteBall(String paletteBall) {
        paletteBalls.add(paletteBall);
    }


    private static final ArrayList<GameVersion> paletteVersions = new ArrayList<>();
    public static ArrayList<GameVersion> getPaletteVersions() {
        return paletteVersions;
    }
    public static void addPaletteVersion(GameVersion paletteVersion) {
        paletteVersions.add(paletteVersion);
    }


    public static void clear() {
        paletteBalls.clear();
        paletteVersions.clear();
    }

}
