package com.woogleFX.file.resourceManagers.resources;

import javafx.scene.image.Image;

public class ImageResource extends Resource {

    private Image image;
    private String path;

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public ImageResource(String id, String path, Image image) {
        super(id);
        this.image = image;
        this.path = path;
    }
}
