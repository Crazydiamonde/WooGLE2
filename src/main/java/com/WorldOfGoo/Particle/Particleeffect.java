package com.WorldOfGoo.Particle;

import com.WooGLEFX.Structures.EditorObject;
import com.WooGLEFX.Structures.InputField;
import com.WooGLEFX.Engine.Main;
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

    public Particleeffect(EditorObject _parent){
        super(_parent);
        addAttribute("name", "", InputField.ANY, false);
        addAttribute("maxparticles", "0", InputField.NUMBER, false);
        addAttribute("rate", "0.25", InputField.NUMBER, false);
        setMetaAttributes(MetaEditorAttribute.parse("name,maxparticles,rate"));
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
