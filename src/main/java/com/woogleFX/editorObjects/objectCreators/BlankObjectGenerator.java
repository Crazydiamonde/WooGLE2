package com.woogleFX.editorObjects.objectCreators;

import com.woogleFX.assets.Asset;
import com.woogleFX.editorObjects.EditorObject;
import com.woogleFX.assets.GameVersion;
import com.worldOfGoo.addin.*;
import com.worldOfGoo.text.strings;

public class BlankObjectGenerator {

    public static addin generateBlankAddinObject(Asset asset) {
        GameVersion version = asset.getVersion();
        addin addin = ObjectCreator.create(com.worldOfGoo.addin.addin.class, null, version);
        ObjectCreator.create(id.class, addin, version);
        ObjectCreator.create(name.class, addin, version);
        ObjectCreator.create(type.class, addin, version);
        ObjectCreator.create(com.worldOfGoo.addin.version.class, addin, version);
        ObjectCreator.create(description.class, addin, version);
        ObjectCreator.create(author.class, addin, version);
        EditorObject levels = ObjectCreator.create(com.worldOfGoo.addin.levels.class, addin, version);
        EditorObject level = ObjectCreator.create(com.worldOfGoo.addin.level.class, levels, version);
        EditorObject addinLevelDir = ObjectCreator.create(dir.class, level, version);
        ObjectCreator.create(_name.class, level, version);
        ObjectCreator.create(subtitle.class, level, version);
        ObjectCreator.create(ocd.class, level, version);

        addinLevelDir.setAttribute("value", asset.getName());

        if (version == GameVersion.VERSION_WOG2) {
            addin.setAttribute("spec-version", "2.0");
        }

        return addin;
    }


    public static EditorObject generateBlankTextObject(GameVersion version) {
        return ObjectCreator.create(strings.class, null, version);
    }

}
