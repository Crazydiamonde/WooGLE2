package com.worldOfGoo.level;

import com.woogleFX.editorObjects.EditorObject;
import com.woogleFX.editorObjects.InputField;
import com.woogleFX.structures.simpleStructures.MetaEditorAttribute;

public class Level extends EditorObject {

    public Level(EditorObject _parent) {
        super(_parent, "level", "level\\level");

        addAttribute("ballsrequired",      InputField.NUMBER)                               .assertRequired();
        addAttribute("letterboxed",        InputField.FLAG)  .setDefaultValue("false")      .assertRequired();
        addAttribute("visualdebug",        InputField.FLAG)  .setDefaultValue("false")      .assertRequired();
        addAttribute("autobounds",         InputField.FLAG)  .setDefaultValue("false")      .assertRequired();
        addAttribute("textcolor",          InputField.COLOR) .setDefaultValue("255,255,255").assertRequired();
        addAttribute("texteffects",        InputField.FLAG);
        addAttribute("timebugprobability", InputField.NUMBER)                               .assertRequired();
        addAttribute("strandgeom",         InputField.FLAG)  .setDefaultValue("false");
        addAttribute("allowskip",          InputField.FLAG)  .setDefaultValue("true");
        addAttribute("cursor1color",       InputField.COLOR);
        addAttribute("cursor2color",       InputField.COLOR);
        addAttribute("cursor3color",       InputField.COLOR);
        addAttribute("cursor4color",       InputField.COLOR);
        addAttribute("retrytime",          InputField.NUMBER);
        addAttribute("zoomoutlimit",       InputField.NUMBER);

        setMetaAttributes(MetaEditorAttribute.parse("ballsrequired,timebugprobability,textcolor,retrytime,zoomoutlimit,?Flags<letterboxed,visualdebug,autobounds,strandgeom,allowskip,texteffects>?Cursor Colors<cursor1color,cursor2color,cursor3color,cursor4color>"));

    }


    @Override
    public String[] getPossibleChildren() {
        return new String[]{"BallInstance", "Strand", "camera", "endoncollision", "endonmessage", "endonnogeom", "fire", "levelexit", "loopsound", "music", "pipe", "signpost", "targetheight"};
    }

}
