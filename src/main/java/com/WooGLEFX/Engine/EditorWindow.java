package com.WooGLEFX.Engine;

import com.WooGLEFX.EditorObjects.EditorObject;
import com.WooGLEFX.File.ResourceManagers.AnimationManager;
import com.WooGLEFX.Functions.LevelManager;
import com.WorldOfGoo.Level.Fire;
import com.WorldOfGoo.Scene.Particles;
import javafx.animation.AnimationTimer;

public class EditorWindow extends AnimationTimer {

    private static float timeElapsed = 0;
    public static float getTimeElapsed() {
        return timeElapsed;
    }


    private static long timeStarted = -1;


    @Override
    public void handle(long now) {

        if (timeStarted == -1) timeStarted = now;
        timeElapsed = (now - timeStarted) / 1000000000f;

        AnimationManager.updateAnimations(timeElapsed);

        if (LevelManager.getLevel() != null) for (EditorObject editorObject : LevelManager.getLevel().getScene()) {
            if (editorObject instanceof Particles particles) {
                particles.frameUpdate();
            }
            if (editorObject instanceof Fire fire) {
                for (EditorObject child : fire.getChildren()) {
                    if (child instanceof Particles particles) {
                        particles.frameUpdate();
                    }
                }
            }
        }

        Renderer.draw();

    }

}