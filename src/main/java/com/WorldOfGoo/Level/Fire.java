package com.WorldOfGoo.Level;

import com.WooGLEFX.EditorObjects.Components.ObjectPosition;
import com.WooGLEFX.Functions.LevelManager;
import com.WooGLEFX.Functions.ParticleManager;
import com.WooGLEFX.Structures.EditorObject;
import com.WooGLEFX.Structures.InputField;
import com.WooGLEFX.Structures.SimpleStructures.MetaEditorAttribute;
import com.WorldOfGoo.Scene.Particles;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

public class Fire extends EditorObject {

    private Particles particleEffect;


    public Fire(EditorObject _parent) {
        super(_parent);
        setRealName("fire");

        addAttribute("depth",     InputField.NUMBER)   .setDefaultValue("0") .assertRequired();
        addAttribute("particles", InputField.PARTICLES)                      .assertRequired();
        addAttribute("x",         InputField.NUMBER)   .setDefaultValue("0") .assertRequired();
        addAttribute("y",         InputField.NUMBER)   .setDefaultValue("0") .assertRequired();
        addAttribute("radius",    InputField.NUMBER)   .setDefaultValue("50").assertRequired();

        addObjectPosition(new ObjectPosition(ObjectPosition.CIRCLE) {
            public double getX() {
                return getDouble("x");
            }
            public void setX(double x) {
                setAttribute("x", x);
            }
            public double getY() {
                return -getDouble("y");
            }
            public void setY(double y) {
                setAttribute("y", -y);
            }
            public double getRadius() {
                return getDouble("radius");
            }
            public void setRadius(double radius) {
                setAttribute("radius", radius);
            }
            public double getEdgeSize() {
                return 0.5;
            }
            public Paint getBorderColor() {
                return new Color(1.0, 0.25, 0.0, 1.0);
            }
            public Paint getFillColor() {
                return new Color(1.0, 0.25, 0.0, 0.1);
            }
            public boolean isVisible() {
                return LevelManager.getLevel().isShowGeometry();
            }
        });

        setNameAttribute(getAttribute2("particles"));
        setMetaAttributes(MetaEditorAttribute.parse("x,y,radius,particles,depth,"));

    }


    private Particles findParticleFx() {
        for (EditorObject particle : ParticleManager.getParticles()) {
            if (particle.attributeExists("name") && particle.getAttribute("name").equals(getAttribute("particles"))) {
                particleEffect = new Particles(this);
                getChildren().remove(particleEffect);
                particleEffect.setAttribute("pos", getAttribute("x") + "," + getAttribute("y"));
                particleEffect.setAttribute("depth", getAttribute("depth"));
                particleEffect.setAttribute("effect", getAttribute("particles"));
                particleEffect.update();
                return particleEffect;
            }
        }
        return null;
    }


    @Override
    public void update() {
        findParticleFx();
        setChangeListener("particles", (observableValue, s, t1) -> {
            Particles found = findParticleFx();
            if (found == null) {
                particleEffect = null;
            }
        });
        setChangeListener("x", (observableValue, s, t1) -> {
            if (particleEffect != null) {
                particleEffect.setAttribute("pos", getAttribute("x") + "," + getAttribute("y"));
            }
        });
        setChangeListener("y", (observableValue, s, t1) -> {
            if (particleEffect != null) {
                particleEffect.setAttribute("pos", getAttribute("x") + "," + getAttribute("y"));
            }
        });
    }

}
