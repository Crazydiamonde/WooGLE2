package com.worldOfGoo.ball;

import com.woogleFX.editorObjects.EditorObject;
import com.woogleFX.editorObjects.attributes.InputField;
import com.woogleFX.gameData.level.GameVersion;

public class Ball extends EditorObject {

    public Ball(EditorObject _parent, GameVersion version) {
        super(_parent, "ball", version);

        addAttribute("name",                 InputField.ANY)                                  .assertRequired();
        addAttribute("static",               InputField.ANY);
        addAttribute("shape",                InputField.ANY).setDefaultValue("circle,36,0.25").assertRequired();
        addAttribute("mass",                 InputField.ANY)                                  .assertRequired();
        addAttribute("towermass",            InputField.ANY);
        addAttribute("strands",              InputField.ANY)                                  .assertRequired();
        addAttribute("walkspeed",            InputField.ANY)                                  .assertRequired();
        addAttribute("climbspeed",           InputField.ANY);
        addAttribute("speedvariance",        InputField.ANY)                                  .assertRequired();
        addAttribute("draggable",            InputField.ANY);
        addAttribute("detachable",           InputField.ANY);
        addAttribute("autobounds",           InputField.ANY);
        addAttribute("grumpy",               InputField.ANY);
        addAttribute("suckable",             InputField.ANY);
        addAttribute("sticky",               InputField.ANY);
        addAttribute("stuckattachment",      InputField.ANY);
        addAttribute("invulnerable",         InputField.ANY);
        addAttribute("antigrav",             InputField.ANY);
        addAttribute("dampening",            InputField.ANY);
        addAttribute("collideattached",      InputField.ANY);
        addAttribute("statescales",          InputField.ANY);
        addAttribute("jump",                 InputField.ANY);
        addAttribute("maxattachspeed",       InputField.ANY);
        addAttribute("jumponwakeup",         InputField.ANY);
        addAttribute("staticwhensleeping",   InputField.ANY);
        addAttribute("collidewithattached",  InputField.ANY);
        addAttribute("climber",              InputField.ANY);
        addAttribute("material",             InputField.ANY);
        addAttribute("contains",             InputField.ANY);
        addAttribute("popsound",             InputField.ANY);
        addAttribute("popduration",          InputField.ANY);
        addAttribute("popparticles",         InputField.ANY);
        addAttribute("isbehindstrands",      InputField.ANY);
        addAttribute("wakedist",             InputField.ANY);
        addAttribute("blinkcolor",           InputField.ANY);
        addAttribute("hideeyes",             InputField.ANY);
        addAttribute("alwayslookatmouse",    InputField.ANY);
        addAttribute("autoboundsunattached", InputField.ANY);
        addAttribute("burntime",             InputField.ANY);
        addAttribute("detonateforce",        InputField.ANY);
        addAttribute("detonateradius",       InputField.ANY);
        addAttribute("walkforce",            InputField.ANY);
        addAttribute("fling",                InputField.ANY);
        addAttribute("explosionparticles",   InputField.ANY);
        addAttribute("dragmass",             InputField.ANY);
        addAttribute("autodisable",          InputField.ANY);
        addAttribute("hingedrag",            InputField.ANY);
        addAttribute("attenuationselect",    InputField.ANY);
        addAttribute("attenuationdeselect",  InputField.ANY);
        addAttribute("attenuationdrop",      InputField.ANY);
        addAttribute("attenuationdrag",      InputField.ANY);
        addAttribute("stacking",             InputField.ANY);
        addAttribute("stickyattached",       InputField.ANY);
        addAttribute("stickyunattached",     InputField.ANY);
        addAttribute("fallingattachment",    InputField.ANY);
        addAttribute("spawn",                InputField.ANY);
        addAttribute("autoattach",           InputField.ANY);
        addAttribute("isantigravattached",   InputField.FLAG);
        addAttribute("isantigravunattached", InputField.FLAG);
        addAttribute("popdelay",             InputField.ANY);
        addAttribute("distantsounds",        InputField.ANY);
        addAttribute("flammable",            InputField.ANY);

    }

}
