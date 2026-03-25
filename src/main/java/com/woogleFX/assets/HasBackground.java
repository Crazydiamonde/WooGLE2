package com.woogleFX.assets;

import javafx.scene.paint.Color;

public interface HasBackground {

    Color getBackgroundColor();
    default void setBackgroundColor(Color backgroundColor) {
    }

}
