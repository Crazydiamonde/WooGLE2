package com.woogleFX.editorObjects.objectCreators;

import com.woogleFX.editorObjects.EditorObject;
import com.woogleFX.gameData.level.GameVersion;

public class BlankObjectGenerator {

    public static EditorObject generateBlankAddinObject(String levelName, GameVersion version) {
        EditorObject addin = ObjectCreator.create("Addin_addin", null, version);
        ObjectCreator.create("Addin_id", addin, version);
        ObjectCreator.create("Addin_name", addin, version);
        ObjectCreator.create("Addin_type", addin, version);
        ObjectCreator.create("Addin_version", addin, version);
        ObjectCreator.create("Addin_description", addin, version);
        ObjectCreator.create("Addin_author", addin, version);
        EditorObject levels = ObjectCreator.create("Addin_levels", addin, version);
        EditorObject level = ObjectCreator.create("Addin_level", levels, version);
        EditorObject addinLevelDir = ObjectCreator.create("Addin_dir", level, version);
        ObjectCreator.create("Addin_wtf_name", level, version);
        ObjectCreator.create("Addin_subtitle", level, version);
        ObjectCreator.create("Addin_ocd", level, version);

        addinLevelDir.setAttribute("value", levelName);

        return addin;
    }


    public static EditorObject generateBlankTextObject(GameVersion version) {
        return ObjectCreator.create("strings", null, version);
    }

}
