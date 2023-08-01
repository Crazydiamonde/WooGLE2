package com.WooGLEFX.Structures.SimpleStructures;

import com.WooGLEFX.Structures.WorldLevel;
import javafx.scene.control.Tab;

public class LevelTab extends Tab {

    private final WorldLevel level;

    public WorldLevel getLevel() {
        return level;
    }

    public LevelTab(String text, WorldLevel level) {
        super(text);
        this.level = level;
    }
}
