package com.WorldOfGoo.Particle;

import com.WooGLEFX.Functions.ParticleManager;
import com.WooGLEFX.Structures.EditorObject;
import com.WooGLEFX.Structures.InputField;

import java.util.ArrayList;

public class Ambientparticleeffect extends EditorObject {

    private ArrayList<_Particle> particles = new ArrayList<>();
    public ArrayList<_Particle> getParticles() {
        return particles;
    }
    public void setParticles(ArrayList<_Particle> particles) {
        this.particles = particles;
    }


    public Ambientparticleeffect(EditorObject _parent) {
        super(_parent, "ambientparticleeffect");

        addAttribute("name", InputField.NUMBER).setDefaultValue("0").assertRequired();
        addAttribute("maxparticles", InputField.NUMBER).setDefaultValue("0").assertRequired();
        addAttribute("rate", InputField.NUMBER).setDefaultValue("0").assertRequired();
        addAttribute("margin", InputField.NUMBER).setDefaultValue("0").assertRequired();

    }


    @Override
    public void update() {
        for (EditorObject thing : ParticleManager.getParticles()) {
            if (thing instanceof _Particle && thing.getParent().getAttribute("name").equals(getAttribute("name"))) {
                particles.add((_Particle) thing);
            }
        }
    }

}
