package com.woogleFX.engine.gui;

import com.woogleFX.editorObjects.EditorObject;
import com.woogleFX.engine.renderer.Renderer;
import com.woogleFX.engine.AssetManager;
import javafx.animation.AnimationTimer;

public class EditorWindow extends AnimationTimer {

    private static float timeElapsed = 0;
    public static float getTimeElapsed() {
        return timeElapsed;
    }


    private static long timeStarted = -1;


    private static long currentTime = -1;


    @Override
    public void handle(long now) {

        double timeStep = (now - currentTime) / 1000000000d;
        currentTime = now;

        if (timeStarted == -1) timeStarted = now;
        timeElapsed = (now - timeStarted) / 1000000000f;

        if (AssetManager.getAsset() != null) for (EditorObject editorObject : AssetManager.getAsset().getObjects()) {
            editorObject.frameUpdate(timeStep);
        }

        Renderer.draw();

    }

}