package com.woogleFX.editorObjects.objectCreators;

import com.woogleFX.editorObjects.EditorObject;

public class BlankObjectGenerator {

    public static EditorObject generateBlankAddinObject(String levelName) {
        EditorObject addin = ObjectCreator.create("Addin_addin", null);
        ObjectCreator.create("Addin_id", addin);
        ObjectCreator.create("Addin_name", addin);
        ObjectCreator.create("Addin_type", addin);
        ObjectCreator.create("Addin_version", addin);
        ObjectCreator.create("Addin_description", addin);
        ObjectCreator.create("Addin_author", addin);
        EditorObject levels = ObjectCreator.create("Addin_levels", addin);
        EditorObject level = ObjectCreator.create("Addin_level", levels);
        EditorObject addinLevelDir = ObjectCreator.create("Addin_dir", level);
        ObjectCreator.create("Addin_wtf_name", level);
        ObjectCreator.create("Addin_subtitle", level);
        ObjectCreator.create("Addin_ocd", level);

        addinLevelDir.setAttribute("value", levelName);

        return addin;
    }


    public static EditorObject generateBlankTextObject() {
        return ObjectCreator.create("strings", null);
    }

}
