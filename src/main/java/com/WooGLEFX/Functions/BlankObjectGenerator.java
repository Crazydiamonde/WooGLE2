package com.WooGLEFX.Functions;

import com.WooGLEFX.Structures.EditorAttribute;
import com.WooGLEFX.Structures.EditorObject;

public class BlankObjectGenerator {

    public static EditorObject generateBlankAddinObject(String levelName) {
        EditorObject addin = EditorObject.create("Addin_addin", new EditorAttribute[0], null);
        EditorObject.create("Addin_id", new EditorAttribute[0], addin);
        EditorObject.create("Addin_name", new EditorAttribute[0], addin);
        EditorObject.create("Addin_type", new EditorAttribute[0], addin);
        EditorObject.create("Addin_version", new EditorAttribute[0], addin);
        EditorObject.create("Addin_description", new EditorAttribute[0], addin);
        EditorObject.create("Addin_author", new EditorAttribute[0], addin);
        EditorObject levels = EditorObject.create("Addin_levels", new EditorAttribute[0], addin);
        EditorObject level = EditorObject.create("Addin_level", new EditorAttribute[0], levels);
        EditorObject addinLevelDir = EditorObject.create("Addin_dir", new EditorAttribute[0], level);
        EditorObject addinLevelName = EditorObject.create("Addin_wtf_name", new EditorAttribute[0], level);
        EditorObject.create("Addin_subtitle", new EditorAttribute[0], level);
        EditorObject.create("Addin_ocd", new EditorAttribute[0], level);

        addinLevelDir.setAttribute("value", levelName);

        return addin;
    }


    public static EditorObject generateBlankTextObject() {
        return EditorObject.create("strings", new EditorAttribute[0], null);
    }

}
