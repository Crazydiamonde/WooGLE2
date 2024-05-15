package com.WorldOfGoo.Particle;

import com.WooGLEFX.Functions.ParticleManager;
import com.WooGLEFX.Structures.EditorObject;
import com.WooGLEFX.Structures.InputField;
import com.WooGLEFX.Structures.SimpleStructures.MetaEditorAttribute;

import java.util.ArrayList;

public class Particleeffect extends EditorObject {

    private ArrayList<_Particle> particles = new ArrayList<>();

    public ArrayList<_Particle> getParticles() {
        return particles;
    }

    public void setParticles(ArrayList<_Particle> particles) {
        this.particles = particles;
    }


    public Particleeffect(EditorObject _parent) {
        super(_parent);

        addAttribute("name", InputField.ANY);
        addAttribute("maxparticles", InputField.NUMBER).setDefaultValue("0");
        addAttribute("rate", InputField.NUMBER).setDefaultValue("0.25");

        setMetaAttributes(MetaEditorAttribute.parse("name,maxparticles,rate"));

    }


    @Override
    public void update(){
        for (EditorObject thing : ParticleManager.getParticles()) {
            if (thing instanceof _Particle && thing.getParent().getAttribute("name").equals(getAttribute("name"))) {
                particles.add((_Particle) thing);
            }
        }
    }
}
