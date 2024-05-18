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
        super(_parent, "fire");

        addAttribute("depth",     InputField.NUMBER)   .setDefaultValue("0") .assertRequired();
        addAttribute("particles", InputField.PARTICLES)                      .assertRequired();
        addAttribute("x",         InputField.NUMBER)   .setDefaultValue("0") .assertRequired();
        addAttribute("y",         InputField.NUMBER)   .setDefaultValue("0") .assertRequired();
        addAttribute("radius",    InputField.NUMBER)   .setDefaultValue("50").assertRequired();

        addObjectPosition(new ObjectPosition(ObjectPosition.CIRCLE) {
            public double getX() {
                return getAttribute("x").doubleValue();
            }
            public void setX(double x) {
                setAttribute("x", x);
            }
            public double getY() {
                return -getAttribute("y").doubleValue();
            }
            public void setY(double y) {
                setAttribute("y", -y);
            }
            public double getRadius() {
                return getAttribute("radius").doubleValue();
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
                return LevelManager.getLevel().getShowGeometry() != 0;
            }
        });

        setMetaAttributes(MetaEditorAttribute.parse("x,y,radius,particles,depth,"));

        getAttribute("particles").addChangeListener((observableValue, s, t1) -> particleEffect = findParticleFx());
        getAttribute("x").addChangeListener((observableValue, s, t1) -> adjustParticleEffectPosition());
        getAttribute("y").addChangeListener((observableValue, s, t1) -> adjustParticleEffectPosition());

    }


    @Override
    public String getName() {
        return getAttribute("particles").stringValue();
    }


    private void adjustParticleEffectPosition() {
        double x = getAttribute("x").doubleValue();
        double y = getAttribute("y").doubleValue();
        if (particleEffect != null) particleEffect.setAttribute("pos", x + "," + y);
    }


    private Particles findParticleFx() {

        String particles = getAttribute("particles").stringValue();

        for (EditorObject object : ParticleManager.getParticles()) if (object instanceof Particles particle) {

            String name = particle.getAttribute("name").stringValue();
            if (!name.equals(particles)) continue;

            Particles particleEffect = new Particles(this);
            getChildren().remove(particleEffect);

            double x = getAttribute("x").doubleValue();
            double y = getAttribute("y").doubleValue();
            particleEffect.setAttribute("pos", x + "," + y);
            particleEffect.setAttribute("depth", getAttribute("depth").doubleValue());
            particleEffect.setAttribute("effect", particles);
            particleEffect.update();
            return particleEffect;

        }

        return null;

    }


    @Override
    public void update() {
        particleEffect = findParticleFx();
    }

}
