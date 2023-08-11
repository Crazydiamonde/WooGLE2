package com.WorldOfGoo.Level;

import com.WooGLEFX.Structures.EditorAttribute;
import com.WooGLEFX.Structures.EditorObject;
import com.WooGLEFX.Structures.InputField;
import com.WooGLEFX.Structures.SimpleStructures.MetaEditorAttribute;

public class Level extends EditorObject {
    public Level(EditorObject _parent) {
        super(_parent);
        setRealName("level");
        addAttribute("ballsrequired", "", InputField.NUMBER, true);
        addAttribute("letterboxed", "false", InputField.FLAG, true);
        addAttribute("visualdebug", "false", InputField.FLAG, true);
        addAttribute("autobounds", "false", InputField.FLAG, true);
        addAttribute("textcolor", "255,255,255", InputField.COLOR, true);
        addAttribute("texteffects", "", InputField.FLAG, false);
        addAttribute("timebugprobability", "", InputField.NUMBER, true);
        addAttribute("strandgeom", "false", InputField.FLAG, false);
        addAttribute("allowskip", "true", InputField.FLAG, false);
        addAttribute("cursor1color", "", InputField.COLOR, false);
        addAttribute("cursor2color", "", InputField.COLOR, false);
        addAttribute("cursor3color", "", InputField.COLOR, false);
        addAttribute("cursor4color", "", InputField.COLOR, false);
        addAttribute("retrytime", "", InputField.NUMBER, false);
        addAttribute("zoomoutlimit", "", InputField.NUMBER, false);
        setNameAttribute(new EditorAttribute(this, "", "", "", new InputField("", InputField.NULL), false));
        setMetaAttributes(MetaEditorAttribute.parse("ballsrequired,timebugprobability,textcolor,retrytime,zoomoutlimit,?Flags<letterboxed,visualdebug,autobounds,strandgeom,allowskip,texteffects>?Cursor Colors<cursor1color,cursor2color,cursor3color,cursor4color>"));
    }

    @Override
    public String[] getPossibleChildren() {
        return new String[]{"BallInstance", "Strand", "camera", "endoncollision", "endonmessage", "endonnogeom", "fire", "levelexit", "loopsound", "music", "pipe", "signpost", "targetheight"};
    }

}
