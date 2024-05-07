package com.WooGLEFX.Engine;

import com.WooGLEFX.Functions.AnimationManager;
import javafx.animation.AnimationTimer;

public class EditorWindow extends AnimationTimer {
    static float timeElapsed = 0;

    public static float getTimeElapsed() {
        return timeElapsed;
    }

    private static long timeStarted = -1;

    @Override
    public void handle(long now) {
        if (timeStarted == -1) {
            timeStarted = now;
        }

        timeElapsed = (now - timeStarted) / 1000000000f;

        AnimationManager.updateAnimations(timeElapsed);
        Renderer.draw();

    }
}