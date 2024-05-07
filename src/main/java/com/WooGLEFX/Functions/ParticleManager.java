package com.WooGLEFX.Functions;

import com.WooGLEFX.Structures.EditorObject;

import java.util.ArrayList;
import java.util.List;

public class ParticleManager {

    private static final List<EditorObject> particles = new ArrayList<>();
    public static List<EditorObject> getParticles() {
        return particles;
    }


    private static final List<String> sortedParticleNames = new ArrayList<>();
    public static List<String> getSortedParticleNames() {
        return sortedParticleNames;
    }




}
