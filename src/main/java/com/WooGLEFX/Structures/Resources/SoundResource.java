package com.WooGLEFX.Structures.Resources;

import javafx.scene.image.Image;

//TODO make this a thing
public class SoundResource extends Resource {

    private Image sound;
    private String path;

    public Image getSound() {
        return sound;
    }

    public void setSound(Image sound) {
        this.sound = sound;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public SoundResource(String id, String path, Image sound) {
        super(id);
        this.sound = sound;
        this.path = path;
    }
}
