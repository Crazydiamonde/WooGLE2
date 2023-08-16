package com.WorldOfGoo.Ball;

import com.WooGLEFX.Structures.EditorObject;
import com.WooGLEFX.Structures.InputField;
import com.WooGLEFX.Structures.SimpleStructures.MetaEditorAttribute;

public class Ball extends EditorObject {
    public Ball(EditorObject _parent) {
        super(_parent);
        addAttribute("name", "", InputField.ANY, true);
        addAttribute("static", "", InputField.ANY, false);
        addAttribute("shape", "circle,36,0.25", InputField.ANY, true);
        addAttribute("mass", "", InputField.ANY, true);
        addAttribute("towermass", "", InputField.ANY, false);
        addAttribute("strands", "", InputField.ANY, true);
        addAttribute("walkspeed", "", InputField.ANY, true);
        addAttribute("climbspeed", "", InputField.ANY, false);
        addAttribute("speedvariance", "", InputField.ANY, true);
        addAttribute("draggable", "", InputField.ANY, false);
        addAttribute("detachable", "", InputField.ANY, false);
        addAttribute("autobounds", "", InputField.ANY, false);
        addAttribute("grumpy", "", InputField.ANY, false);
        addAttribute("suckable", "", InputField.ANY, false);
        addAttribute("sticky", "", InputField.ANY, false);
        addAttribute("stuckattachment", "", InputField.ANY, false);
        addAttribute("invulnerable", "", InputField.ANY, false);
        addAttribute("antigrav", "", InputField.ANY, false);
        addAttribute("dampening", "", InputField.ANY, false);
        addAttribute("collideattached", "", InputField.ANY, false);
        addAttribute("statescales", "", InputField.ANY, false);
        addAttribute("jump", "", InputField.ANY, false);
        addAttribute("maxattachspeed", "", InputField.ANY, false);
        addAttribute("jumponwakeup", "", InputField.ANY, false);
        addAttribute("staticwhensleeping", "", InputField.ANY, false);
        addAttribute("collidewithattached", "", InputField.ANY, false);
        addAttribute("climber", "", InputField.ANY, false);
        addAttribute("material", "", InputField.ANY, false);
        addAttribute("contains", "", InputField.ANY, false);
        addAttribute("popsound", "", InputField.ANY, false);
        addAttribute("popduration", "", InputField.ANY, false);
        addAttribute("popparticles", "", InputField.ANY, false);
        addAttribute("isbehindstrands", "", InputField.ANY, false);
        addAttribute("wakedist", "", InputField.ANY, false);
        addAttribute("blinkcolor", "", InputField.ANY, false);
        addAttribute("hideeyes", "", InputField.ANY, false);
        addAttribute("alwayslookatmouse", "", InputField.ANY, false);
        addAttribute("autoboundsunattached", "", InputField.ANY, false);
        addAttribute("burntime", "", InputField.ANY, false);
        addAttribute("detonateforce", "", InputField.ANY, false);
        addAttribute("detonateradius", "", InputField.ANY, false);
        addAttribute("walkforce", "", InputField.ANY, false);
        addAttribute("fling", "", InputField.ANY, false);
        addAttribute("explosionparticles", "", InputField.ANY, false);
        addAttribute("dragmass", "", InputField.ANY, false);
        addAttribute("autodisable", "", InputField.ANY, false);
        addAttribute("hingedrag", "", InputField.ANY, false);
        addAttribute("attenuationselect", "", InputField.ANY, false);
        addAttribute("attenuationdeselect", "", InputField.ANY, false);
        addAttribute("attenuationdrop", "", InputField.ANY, false);
        addAttribute("attenuationdrag", "", InputField.ANY, false);
        addAttribute("stacking", "", InputField.ANY, false);
        addAttribute("stickyattached", "", InputField.ANY, false);
        addAttribute("stickyunattached", "", InputField.ANY, false);
        addAttribute("fallingattachment", "", InputField.ANY, false);
        addAttribute("spawn", "", InputField.ANY, false);
        addAttribute("autoattach", "", InputField.ANY, false);
        addAttribute("isantigravunattached", "", InputField.ANY, false);
        addAttribute("popdelay", "", InputField.ANY, false);
        addAttribute("distantsounds", "", InputField.ANY, false);
        addAttribute("flammable", "", InputField.ANY, false);
    }
}
