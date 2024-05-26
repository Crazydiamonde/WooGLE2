package com.woogleFX.file.resourceManagers;

import com.woogleFX.editorObjects._Font;

import java.util.ArrayList;

public class FontManager {

    private static final ArrayList<_Font> importedFonts = new ArrayList<>();
    public static ArrayList<_Font> getImportedFonts() {
        return importedFonts;
    }

}
