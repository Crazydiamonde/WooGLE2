package com.WooGLEFX.Engine;

import com.WooGLEFX.EditorObjects.EditorObject;
import com.WooGLEFX.EditorObjects.ParticleUtility;
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

        if (LevelManager.getLevel() != null) {
            for (EditorObject editorObject : LevelManager.getLevel().getScene()) {
                if (editorObject instanceof Particles particles) {
                    ParticleUtility.frameUpdate(particles, particles.getCounts(), particles.getDrawing());
                }
            }
            for (EditorObject editorObject : LevelManager.getLevel().getLevel()) {
                if (editorObject instanceof Fire fire) {
                    ParticleUtility.frameUpdate(fire, fire.getCounts(), fire.getDrawing());
                }
            }
        }

        Renderer.draw();

    }

}