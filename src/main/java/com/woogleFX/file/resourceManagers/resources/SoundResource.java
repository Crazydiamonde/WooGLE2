package com.woogleFX.file.resourceManagers.resources;

import javafx.scene.image.Image;

public class SoundResource extends Resource {

    private Image sound;
    public Image getSound() {
        return sound;
    }
    public void setSound(Image sound) {
        this.sound = sound;
    }


    private String path;
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
