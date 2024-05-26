package com.woogleFX.editorObjects;

import com.woogleFX.structures.GameVersion;

import java.util.ArrayList;

public class _Font {

    private final String name;
    public String getName() {
        return name;
    }


    private final GameVersion version;
    public GameVersion getVersion() {
        return version;
    }


    public _Font(String name, GameVersion version) {
        this.name = name;
        this.version = version;
    }


    private final ArrayList<Layer> layers = new ArrayList<>();
    public ArrayList<Layer> getLayers() {
        return layers;
    }
    public Layer getLayer(String id) {
        for (Layer layer : layers) if (layer.getId().equals(id)) return layer;
        return null;
    }
    public void addLayer(Layer layer) {
        layers.add(layer);
    }


    private double defaultPointSize;
    public double getDefaultPointSize() {
        return defaultPointSize;
    }
    public void setDefaultPointSize(double defaultPointSize) {
        this.defaultPointSize = defaultPointSize;
    }

}
