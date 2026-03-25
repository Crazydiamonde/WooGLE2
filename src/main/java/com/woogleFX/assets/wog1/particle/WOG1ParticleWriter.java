package com.woogleFX.assets.wog1.particle;

import com.woogleFX.assets.GameVersion;
import com.woogleFX.editorObjects.EditorObject;
import com.woogleFX.engine.gui.alarms.ErrorAlarm;
import com.woogleFX.file.aesEncryption.AESBinFormat;
import com.woogleFX.file.fileExport.XMLUtility;
import com.woogleFX.file.fileImport.EditorObjectXMLReader;
import com.worldOfGoo.particle.ambientparticleeffect;
import com.worldOfGoo.particle.effects;
import com.worldOfGoo.particle.particleeffect;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

public class WOG1ParticleWriter {

    public static boolean saveParticleAsXML(WOG1Particle particle, String outputPathString, GameVersion version) {

        effects effects = EditorObjectXMLReader.readEditorObject("com.worldOfGoo.particle", version, new File(outputPathString), effects.class);
        if (effects == null) return false;

        // Load the particles into an ArrayList of just the particleeffect objects,
        // add the one we just made,
        // and then export everything to fx.xml again.

        ArrayList<EditorObject> particleeffectObjects = new ArrayList<>();
        boolean seenEffectAlready = false;
        for (EditorObject object : effects.getChildren()) {
            if (object instanceof particleeffect || object instanceof ambientparticleeffect) {
                if (object.getAttribute("name").stringValue().equals(particle.getParticle().getAttribute("name").stringValue())) {
                    seenEffectAlready = true;
                    particleeffectObjects.add(particle.getParticle());
                } else {
                    particleeffectObjects.add(object);
                }
            }
        }
        if (!seenEffectAlready) {
            // If the effect isn't already in fx.xml, add it to the end of the list.
            particleeffectObjects.add(particle.getParticle());
        }

        // Save these back to the file.
        StringBuilder export = new StringBuilder("<effects>\n\n");

        for (EditorObject object : particleeffectObjects) {
            XMLUtility.recursiveXMLExport(export, object, 0);
            export.append("\n\n");
        }

        export.append("\n</effects>");


        try {
            if (version == GameVersion.VERSION_WOG1_OLD) {
                AESBinFormat.encodeFile(new File(outputPathString), export.toString().getBytes());
            } else {
                Files.writeString(Path.of(outputPathString), export.toString());
            }
        } catch (IOException e) {
            ErrorAlarm.show(e);
            return false;
        }

        return true;

    }

}
