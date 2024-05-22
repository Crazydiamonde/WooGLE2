package com.woogleFX.structures.simpleStructures;

import java.util.ArrayList;

public class WoGAnimation {

    private final Keyframe[][] transformFrames;
    private final Keyframe[] alphaFrames;
    private final Keyframe[] colorFrames;
    private final Keyframe[] soundFrames;

    private final float[] frameTimes;

    public float[] getFrameTimes() {
        return frameTimes;
    }

    private String name;

    public Keyframe[][] getTransformFrames() {
        return transformFrames;
    }

    public Keyframe[] getAlphaFrames() {
        return alphaFrames;
    }

    public Keyframe[] getColorFrames() {
        return colorFrames;
    }

    public Keyframe[] getSoundFrames() {
        return soundFrames;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public WoGAnimation(ArrayList<ArrayList<Keyframe>> transformFrames, ArrayList<Keyframe> alphaFrames, ArrayList<Keyframe> colorFrames, ArrayList<Keyframe> soundFrames, String name, float[] frameTimes){
        this.name = name;
        this.transformFrames = new Keyframe[transformFrames.size()][transformFrames.get(0).size()];
        int i = 0;
        for (ArrayList<Keyframe> frames : transformFrames){
            this.transformFrames[i] = frames.toArray(new Keyframe[0]);
            i++;
        }
        this.alphaFrames = alphaFrames.toArray(new Keyframe[0]);
        this.colorFrames = colorFrames.toArray(new Keyframe[0]);
        this.soundFrames = soundFrames.toArray(new Keyframe[0]);
        this.frameTimes = frameTimes;
    }

}