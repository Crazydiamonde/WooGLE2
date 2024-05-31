package com.worldOfGoo.scene;

import java.util.ArrayList;

import com.woogleFX.gameData.particle.ParticleGraphicsInstance;
import com.woogleFX.editorObjects.EditorObject;
import com.woogleFX.editorObjects.attributes.InputField;
import com.woogleFX.gameData.level.GameVersion;
import com.woogleFX.editorObjects.attributes.MetaEditorAttribute;

public class Particles extends EditorObject {

    private final ArrayList<ArrayList<ParticleGraphicsInstance>> drawing = new ArrayList<>();
    public ArrayList<ArrayList<ParticleGraphicsInstance>> getDrawing() {
        return drawing;
    }


    private final ArrayList<Double> counts = new ArrayList<>();
    public ArrayList<Double> getCounts() {
        return counts;
    }


    public Particles(EditorObject _parent, GameVersion version) {
        super(_parent, "particles", version);

        addAttribute("effect",  InputField.PARTICLES)                      .assertRequired();
        addAttribute("depth",   InputField.NUMBER)  .setDefaultValue("-20").assertRequired();
        addAttribute("pos",     InputField.POSITION).setDefaultValue("0,0");
        addAttribute("pretick", InputField.NUMBER)  .setDefaultValue("0");
        addAttribute("enabled", InputField.FLAG);

        setMetaAttributes(MetaEditorAttribute.parse("effect,pos,depth,pretick,enabled,"));

    }


    @Override
    public String getName() {
        return getAttribute("effect").stringValue();
    }

}
