package com.WooGLEFX.Structures.SimpleStructures;

import com.WooGLEFX.Structures.SimpleStructures.Keyframe;

import java.util.ArrayList;

public class WoGAnimation {

    private Keyframe[][] transformFrames;
    private Keyframe[] alphaFrames;
    private Keyframe[] colorFrames;
    private Keyframe[] soundFrames;

    private float[] frameTimes;

    public float[] getFrameTimes() {
        return frameTimes;
    }

    public void setFrameTimes(float[] frameTimes) {
        this.frameTimes = frameTimes;
    }

    private String name;

    public Keyframe[][] getTransformFrames() {
        return transformFrames;
    }

    public void setTransformFrames(Keyframe[][] transformFrames) {
        this.transformFrames = transformFrames;
    }

    public Keyframe[] getAlphaFrames() {
        return alphaFrames;
    }

    public void setAlphaFrames(Keyframe[] alphaFrames) {
        this.alphaFrames = alphaFrames;
    }

    public Keyframe[] getColorFrames() {
        return colorFrames;
    }

    public void setColorFrames(Keyframe[] colorFrames) {
        this.colorFrames = colorFrames;
    }

    public Keyframe[] getSoundFrames() {
        return soundFrames;
    }

    public void setSoundFrames(Keyframe[] soundFrames) {
        this.soundFrames = soundFrames;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public WoGAnimation(ArrayList<ArrayList<Keyframe>> transformFrames, ArrayList<Keyframe> alphaFrames, ArrayList<Keyframe> colorFrames, ArrayList<Keyframe> soundFrames, String name, float[] frameTimes){
        this.name = name;
        //System.out.println(transformFrames);
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