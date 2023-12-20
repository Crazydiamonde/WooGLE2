package com.WooGLEFX.Engine;

import javafx.scene.control.Tooltip;

public class DelayedTooltip extends Tooltip {
    // Tooltip with a shorter delay than the default
    public DelayedTooltip(String text) {
        super(text);
        setShowDelay(javafx.util.Duration.millis(150));
    }
}
