package com.WooGLEFX.Functions;

import com.WooGLEFX.Structures.EditorAttribute;
import com.WooGLEFX.Structures.EditorObject;

public class BlankObjectGenerator {

    public static EditorObject generateBlankAddinObject(String levelName) {
        EditorObject addin = EditorObject.create("Addin_addin", null);
        EditorObject.create("Addin_id", addin);
        EditorObject.create("Addin_name", addin);
        EditorObject.create("Addin_type", addin);
        EditorObject.create("Addin_version", addin);
        EditorObject.create("Addin_description", addin);
        EditorObject.create("Addin_author", addin);
        EditorObject levels = EditorObject.create("Addin_levels", addin);
        EditorObject level = EditorObject.create("Addin_level", levels);
        EditorObject addinLevelDir = EditorObject.create("Addin_dir", level);
        EditorObject addinLevelName = EditorObject.create("Addin_wtf_name", level);
        EditorObject.create("Addin_subtitle", level);
        EditorObject.create("Addin_ocd", level);

        addinLevelDir.setAttribute("value", levelName);

        return addin;
    }


    public static EditorObject generateBlankTextObject() {
        return EditorObject.create("strings", null);
    }

}
