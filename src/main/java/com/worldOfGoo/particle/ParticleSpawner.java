package com.worldOfGoo.particle;

import com.woogleFX.editorObjects.objectComponents.ObjectComponent;

import java.util.ArrayList;

public interface ParticleSpawner {

    ArrayList<Double> getCreationTimes();
    ArrayList<Double> getCreationPercentages();
    ArrayList<ObjectComponent> getParticleObjectComponents();
    default double getX() {
        return 0;
    }
    default double getY() {
        return 0;
    }
    default boolean isVisible() {
        return true;
    }
    default double getDepth() {
        return 0;
    }

}
