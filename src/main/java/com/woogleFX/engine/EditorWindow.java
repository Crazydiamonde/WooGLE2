package com.woogleFX.engine;

import com.woogleFX.editorObjects.EditorObject;
import com.woogleFX.editorObjects.ParticleUtility;
import com.woogleFX.file.resourceManagers.AnimationManager;
import com.woogleFX.functions.LevelManager;
import com.worldOfGoo.level.Fire;
import com.worldOfGoo.scene.Particles;
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

        AnimationManager.updateAnimations(timeElapsed);

        if (LevelManager.getLevel() != null) {
            for (EditorObject editorObject : LevelManager.getLevel().getScene()) {
                if (editorObject instanceof Particles particles) {
                    ParticleUtility.frameUpdate(particles, particles.getCounts(), particles.getDrawing(), timeStep);
                }
            }
            for (EditorObject editorObject : LevelManager.getLevel().getLevel()) {
                if (editorObject instanceof Fire fire) {
                    ParticleUtility.frameUpdate(fire, fire.getCounts(), fire.getDrawing(), timeStep);
                }
            }
        }

        Renderer.draw();

    }

}