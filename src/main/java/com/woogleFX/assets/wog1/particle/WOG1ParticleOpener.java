package com.woogleFX.assets.wog1.particle;

import com.woogleFX.editorObjects.EditorObject;
import com.woogleFX.editorObjects.objectCreators.ObjectCreator;
import com.woogleFX.file.FileManager;
import com.woogleFX.assets.GameVersion;
import com.woogleFX.file.fileImport.EditorObjectXMLReader;
import com.woogleFX.file.resourceManagers.LevelResourceManager;
import com.worldOfGoo.particle.ParticleManifest;
import com.worldOfGoo.particle.effects;
import com.worldOfGoo.resrc.Resources;

import java.io.File;

public class WOG1ParticleOpener {

    public static WOG1Particle openParticle(File file, String name, GameVersion version) {
        // TODO: make use of file

        String suffix = (version == GameVersion.VERSION_WOG1_OLD) ? ".xml.bin" : ".xml";

        File effectsF = new File(FileManager.getGameDir(version) +
                "/properties/fx" + suffix);
        effects effectsObject = EditorObjectXMLReader.readEditorObject(
                "com.worldOfGoo.particle", version, effectsF, effects.class);
        if (effectsObject == null) return null;

        for (EditorObject child : effectsObject.getChildren()) {
            if (child.getAttribute("name").stringValue().equals(name)) {

                ParticleManifest particleManifest = ObjectCreator.create(ParticleManifest.class, null, version);
                child.setParent(particleManifest);

                // TODO: resources
                Resources resources = ObjectCreator.create(Resources.class, null, version);
                WOG1Particle wog1Particle = new WOG1Particle(version, child, resources);
                wog1Particle.load();
                LevelResourceManager.loadAssetResourcesFromGlobalFiles(wog1Particle);
                return wog1Particle;

            }
        }

        return null;

    }

}
