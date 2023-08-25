package com.WorldOfGoo.Particle;

import com.WooGLEFX.GUI.Alarms;
import com.WooGLEFX.Structures.EditorObject;
import com.WooGLEFX.File.GlobalResourceManager;
import com.WooGLEFX.Structures.InputField;
import com.WooGLEFX.Engine.Main;
import javafx.scene.image.Image;

import java.util.ArrayList;

public class _Particle extends EditorObject {

    private ArrayList<Image> images = new ArrayList<>();

    public ArrayList<Image> getImages() {
        return images;
    }

    public void setImages(ArrayList<Image> images) {
        this.images = images;
    }

    private ArrayList<Axialsinoffset> axialsinoffsets = new ArrayList<>();

    private double version;

    public double getVersion() {
        return version;
    }

    public _Particle(EditorObject _parent) {
        super(_parent);
        addAttribute("image", "", InputField.ANY, true);
        addAttribute("rotspeed", "0", InputField.RANGE, true);
        addAttribute("rotation", "0", InputField.RANGE, true);
        addAttribute("directed", "false", InputField.FLAG, true);
        addAttribute("scale", "1", InputField.RANGE, true);
        addAttribute("finalscale", "1", InputField.RANGE, true);
        addAttribute("fade", "false", InputField.FLAG, true);
        addAttribute("additive", "false", InputField.FLAG, true);
        addAttribute("lifespan", "1", InputField.RANGE, true);
        addAttribute("speed", "0", InputField.RANGE, true);
        addAttribute("movedir", "0", InputField.NUMBER, true);
        addAttribute("movedirvar", "0", InputField.NUMBER, true);
        addAttribute("acceleration", "0,0", InputField.POSITION, true);
    }

    public void update(double version) throws Exception {
        this.version = version;
        for (EditorObject thing : Main.getParticles()) {
            if (thing instanceof Axialsinoffset && thing.getParent() == this) {
                axialsinoffsets.add((Axialsinoffset)thing);
            }
        }
        String mogus = getString("image");
        while (mogus.contains(",")) {
            images.add(GlobalResourceManager.getImage(mogus.substring(0, mogus.indexOf(",")), version));
            mogus = mogus.substring(mogus.indexOf(",") + 1);
        }
        images.add(GlobalResourceManager.getImage(mogus, version));
    }
}
