package com.worldOfGoo.particle;

import com.woogleFX.gameData.particle.ParticleManager;
import com.woogleFX.editorObjects.EditorObject;
import com.woogleFX.editorObjects.attributes.InputField;
import com.woogleFX.gameData.level.GameVersion;

import java.util.ArrayList;

public class Ambientparticleeffect extends EditorObject {

    private ArrayList<_Particle> particles = new ArrayList<>();
    public ArrayList<_Particle> getParticles() {
        return particles;
    }
    public void setParticles(ArrayList<_Particle> particles) {
        this.particles = particles;
    }


    public Ambientparticleeffect(EditorObject _parent, GameVersion version) {
        super(_parent, "ambientparticleeffect", version);

        addAttribute("name", InputField.NUMBER).setDefaultValue("0").assertRequired();
        addAttribute("maxparticles", InputField.NUMBER).setDefaultValue("0").assertRequired();
        addAttribute("rate", InputField.NUMBER).setDefaultValue("0").assertRequired();
        addAttribute("margin", InputField.NUMBER).setDefaultValue("0").assertRequired();

    }


    @Override
    public void update() {
        for (EditorObject thing : ParticleManager.getParticles()) {
            if (thing instanceof _Particle && thing.getParent().getAttribute("name").stringValue().equals(getAttribute("name").stringValue())) {
                particles.add((_Particle) thing);
            }
        }
    }

}
