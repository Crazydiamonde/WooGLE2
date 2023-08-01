package com.WorldOfGoo.Particle;

import com.WooGLEFX.Structures.EditorObject;
import com.WooGLEFX.Structures.InputField;
import com.WooGLEFX.Engine.Main;

import java.util.ArrayList;

public class Ambientparticleeffect extends EditorObject {

    private ArrayList<_Particle> particles = new ArrayList<>();

    public ArrayList<_Particle> getParticles() {
        return particles;
    }

    public void setParticles(ArrayList<_Particle> particles) {
        this.particles = particles;
    }

    public Ambientparticleeffect(EditorObject _parent){
        super(_parent);
        addAttribute("name", "0", InputField.NUMBER, true);
        addAttribute("maxparticles", "0", InputField.NUMBER, true);
        addAttribute("rate", "0.25", InputField.NUMBER, true);
        addAttribute("margin", "0", InputField.NUMBER, true);
    }

    @Override
    public void update(){
        for (EditorObject thing : Main.getParticles()) {
            if (thing instanceof _Particle && thing.getParent() == this) {
                particles.add((_Particle) thing);
            }
        }
    }
}
