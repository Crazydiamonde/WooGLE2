package com.worldOfGoo.ball;

import com.woogleFX.editorObjects.EditorObject;
import com.woogleFX.editorObjects.InputField;
import com.woogleFX.structures.GameVersion;

public class BallStrand extends EditorObject {

    public BallStrand(EditorObject _parent, GameVersion version) {
        super(_parent, "strand", version);

        addAttribute("type",           InputField.ANY)                     .assertRequired();
        addAttribute("image",          InputField.IMAGE)                   .assertRequired();
        addAttribute("inactiveimage",  InputField.ANY)                     .assertRequired();
        addAttribute("springconstmin", InputField.ANY)                     .assertRequired();
        addAttribute("springconstmax", InputField.ANY)                     .assertRequired();
        addAttribute("dampfac",        InputField.ANY)                     .assertRequired();
        addAttribute("maxlen2",        InputField.ANY).setDefaultValue("0").assertRequired();
        addAttribute("maxlen1",        InputField.ANY);
        addAttribute("maxforce",       InputField.ANY)                     .assertRequired();
        addAttribute("minlen",         InputField.ANY)                     .assertRequired();
        addAttribute("walkable",       InputField.ANY);
        addAttribute("thickness",      InputField.NUMBER)                  .setDefaultValue("24");
        addAttribute("ignitedelay",    InputField.ANY);
        addAttribute("burnspeed",      InputField.ANY);
        addAttribute("fireparticles",  InputField.ANY);
        addAttribute("burntimage",     InputField.ANY);
        addAttribute("geom",           InputField.ANY);
        addAttribute("shrinklen",      InputField.ANY);
        addAttribute("rope",           InputField.ANY);

    }

}
