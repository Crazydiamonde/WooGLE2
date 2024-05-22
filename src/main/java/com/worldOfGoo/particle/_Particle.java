package com.worldOfGoo.particle;

import java.util.ArrayList;

import com.woogleFX.file.resourceManagers.GlobalResourceManager;
import com.woogleFX.file.resourceManagers.ParticleManager;
import com.woogleFX.editorObjects.EditorObject;
import com.woogleFX.structures.GameVersion;
import com.woogleFX.editorObjects.InputField;

import javafx.scene.image.Image;

public class _Particle extends EditorObject {

    private final ArrayList<Image> images = new ArrayList<>();
    public ArrayList<Image> getImages() {
        return images;
    }


    private final ArrayList<Axialsinoffset> axialsinoffsets = new ArrayList<>();


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
        axialsinoffsets.clear();
        for (EditorObject thing : ParticleManager.getParticles()) {
            if (thing instanceof Axialsinoffset && thing.getParent() == this) {
                axialsinoffsets.add((Axialsinoffset)thing);
            }
        }
        images.clear();
        String mogus = getAttribute("image").stringValue();
        while (mogus.contains(",")) {
            images.add(GlobalResourceManager.getImage(mogus.substring(0, mogus.indexOf(",")), version));
            mogus = mogus.substring(mogus.indexOf(",") + 1);
        }
        images.add(GlobalResourceManager.getImage(mogus, version));
    }

}
