package com.worldOfGoo.particle;

import com.woogleFX.gameData.particle.ParticleManager;
import com.woogleFX.editorObjects.EditorObject;
import com.woogleFX.editorObjects.attributes.InputField;
import com.woogleFX.gameData.level.GameVersion;
import com.woogleFX.editorObjects.attributes.MetaEditorAttribute;

import java.util.ArrayList;

public class Particleeffect extends EditorObject {

    private ArrayList<_Particle> particles = new ArrayList<>();
    public ArrayList<_Particle> getParticles() {
        return particles;
    }
    public void setParticles(ArrayList<_Particle> particles) {
        this.particles = particles;
    }


    public Particleeffect(EditorObject _parent, GameVersion version) {
        super(_parent, "particleeffect", version);

        addAttribute("name", InputField.ANY);
        addAttribute("maxparticles", InputField.NUMBER).setDefaultValue("0");
        addAttribute("rate", InputField.NUMBER).setDefaultValue("0.25");
        addAttribute("margin", InputField.NUMBER).setDefaultValue("0");

        setMetaAttributes(MetaEditorAttribute.parse("name,maxparticles,rate"));

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
