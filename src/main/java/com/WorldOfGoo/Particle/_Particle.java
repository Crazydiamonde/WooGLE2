package com.WorldOfGoo.Particle;

import java.util.ArrayList;

import com.WooGLEFX.File.ResourceManagers.GlobalResourceManager;
import com.WooGLEFX.File.ResourceManagers.ParticleManager;
import com.WooGLEFX.EditorObjects.EditorObject;
import com.WooGLEFX.Structures.GameVersion;
import com.WooGLEFX.EditorObjects.InputField;

import javafx.scene.image.Image;

public class _Particle extends EditorObject {

    private ArrayList<Image> images = new ArrayList<>();
    public ArrayList<Image> getImages() {
        return images;
    }
    public void setImages(ArrayList<Image> images) {
        this.images = images;
    }


    private ArrayList<Axialsinoffset> axialsinoffsets = new ArrayList<>();


    private GameVersion version;
    public GameVersion getVersion() {
        return version;
    }


    public _Particle(EditorObject _parent) {
        super(_parent, "particle");

        addAttribute("image",        InputField.ANY)                              .assertRequired();
        addAttribute("rotspeed",     InputField.RANGE)   .setDefaultValue("0")    .assertRequired();
        addAttribute("rotation",     InputField.RANGE)   .setDefaultValue("0")    .assertRequired();
        addAttribute("directed",     InputField.FLAG)    .setDefaultValue("false").assertRequired();
        addAttribute("scale",        InputField.RANGE)   .setDefaultValue("1")    .assertRequired();
        addAttribute("finalscale",   InputField.RANGE)   .setDefaultValue("1")    .assertRequired();
        addAttribute("fade",         InputField.FLAG)    .setDefaultValue("false").assertRequired();
        addAttribute("additive",     InputField.FLAG)    .setDefaultValue("false").assertRequired();
        addAttribute("lifespan",     InputField.RANGE)   .setDefaultValue("1")    .assertRequired();
        addAttribute("dampening",    InputField.NUMBER)  .setDefaultValue("0")    .assertRequired();
        addAttribute("speed",        InputField.RANGE)   .setDefaultValue("0")    .assertRequired();
        addAttribute("movedir",      InputField.NUMBER)  .setDefaultValue("0")    .assertRequired();
        addAttribute("movedirvar",   InputField.NUMBER)  .setDefaultValue("0")    .assertRequired();
        addAttribute("acceleration", InputField.POSITION).setDefaultValue("0,0") .assertRequired();

    }


    public void update(GameVersion version) throws Exception {
        this.version = version;
        for (EditorObject thing : ParticleManager.getParticles()) {
            if (thing instanceof Axialsinoffset && thing.getParent() == this) {
                axialsinoffsets.add((Axialsinoffset)thing);
            }
        }
        String mogus = getAttribute("image").stringValue();
        while (mogus.contains(",")) {
            images.add(GlobalResourceManager.getImage(mogus.substring(0, mogus.indexOf(",")), version));
            mogus = mogus.substring(mogus.indexOf(",") + 1);
        }
        images.add(GlobalResourceManager.getImage(mogus, version));
    }
}
