package com.WorldOfGoo.Scene;

import java.util.ArrayList;

import com.WooGLEFX.EditorObjects.objectcomponents.ObjectComponent;
import com.WooGLEFX.EditorObjects.ParticleGraphicsInstance;
import com.WooGLEFX.EditorObjects.EditorObject;
import com.WooGLEFX.EditorObjects.InputField;
import com.WooGLEFX.File.ResourceManagers.ParticleManager;
import com.WooGLEFX.Functions.LevelManager;
import com.WooGLEFX.Structures.SimpleStructures.MetaEditorAttribute;
import com.WooGLEFX.Structures.SimpleStructures.Position;
import com.WooGLEFX.Structures.WorldLevel;
import com.WorldOfGoo.Particle.Ambientparticleeffect;
import com.WorldOfGoo.Particle.Particleeffect;
import com.WorldOfGoo.Particle._Particle;
import javafx.scene.image.Image;

public class Particles extends EditorObject {

    private static double lerp(double a, double b, double m) {
        return a * m + b * (1 - m);
    }


    private final ArrayList<ArrayList<ParticleGraphicsInstance>> drawing = new ArrayList<>();
    private final ArrayList<Integer> counts = new ArrayList<>();


    public Particles(EditorObject _parent) {
        super(_parent, "particles", "scene\\particles");

        addAttribute("effect",  InputField.PARTICLES)                      .assertRequired();
        addAttribute("depth",   InputField.NUMBER)  .setDefaultValue("-20").assertRequired();
        addAttribute("pos",     InputField.POSITION).setDefaultValue("0,0");
        addAttribute("pretick", InputField.NUMBER)  .setDefaultValue("0");
        addAttribute("enabled", InputField.FLAG);



        setMetaAttributes(MetaEditorAttribute.parse("effect,pos,depth,pretick,enabled,"));

    }


    public void frameUpdate() {

        WorldLevel level = LevelManager.getLevel();
        if (level == null) return;

        String effect = getAttribute("effect").stringValue();

        if (!level.getVisibilitySettings().isShowParticles()) return;

        for (EditorObject obj : ParticleManager.getParticles()) {
            if (obj instanceof Particleeffect || obj instanceof Ambientparticleeffect) {
                String name = obj.getAttribute("name").stringValue();
                if (name.equals(effect)) updateParticle(obj);
            }
        }

    }


    public void updateParticle(EditorObject obj) {

        ArrayList<_Particle> particles = new ArrayList<>();
        if (obj instanceof Particleeffect) particles = ((Particleeffect) obj).getParticles();
        if (obj instanceof Ambientparticleeffect) particles = ((Ambientparticleeffect) obj).getParticles();

        int i = 0;
        for (int i2 = particles.size() - 1; i2 >= 0; i2--) {
            updateParticleObject(particles.get(i2), i, obj);
            i++;
        }

    }


    public void updateParticleObject(_Particle particle, int i, EditorObject obj) {

        if (counts.size() == i) counts.add(0);
        counts.set(i, counts.get(i) + 1);
        if (drawing.size() == i) drawing.add(new ArrayList<>());

        boolean shouldCreateNewInstance = false;
        if (obj instanceof Particleeffect)
            shouldCreateNewInstance = counts.get(i) > obj.getAttribute("rate").doubleValue() * 50;
        if (obj instanceof Ambientparticleeffect)
            shouldCreateNewInstance = drawing.get(i).size() < obj.getAttribute("maxparticles").doubleValue();
        if (shouldCreateNewInstance) createNewParticleGraphicsInstance(i, obj, particle);

        for (ParticleGraphicsInstance particleGraphicsInstance : drawing.get(i).toArray(new ParticleGraphicsInstance[0])) {
            if (particleGraphicsInstance.update()) {
                drawing.get(i).remove(particleGraphicsInstance);
                removeObjectPosition(particleGraphicsInstance.getObjectPosition());
            }
        }

        /*
        ParticleGraphicsInstance toBeRemoved = null;
        for (ParticleGraphicsInstance particleGraphicsInstance : drawing.get(i)) {
            if (drawParticleInstance(graphicsContext, particleGraphicsInstance, particle)) {
                toBeRemoved = particleGraphicsInstance;
            }
        }

        if (toBeRemoved != null) drawing.get(i).remove(toBeRemoved);

         */

    }


    public void createNewParticleGraphicsInstance(int i, EditorObject obj, _Particle particle) {

        counts.set(i, 0);

        double scale = InputField.getRange(particle.getAttribute("scale").stringValue(), Math.random());
        double finalscale = InputField.getRange(particle.getAttribute("finalscale").stringValue(), Math.random());
        double absvar = InputField.getRange(particle.getAttribute("movedirvar").stringValue(), Math.random());
        double rotation = InputField.getRange(particle.getAttribute("movedir").stringValue(), Math.random()) + InputField.getRange(-absvar + "," + absvar, Math.random());
        double rotspeed = InputField.getRange(particle.getAttribute("rotspeed").stringValue(), Math.random());
        double speed = InputField.getRange(particle.getAttribute("speed").stringValue(), Math.random());
        double lifespan = InputField.getRange(particle.getAttribute("lifespan").stringValue(), Math.random());
        Position acceleration = Position.parse(particle.getAttribute("acceleration").stringValue());
        boolean fade = particle.getAttribute("fade").booleanValue();
        boolean additive = particle.getAttribute("additive").booleanValue();

        double visualrotation;
        boolean directed = particle.getAttribute("directed").booleanValue();
        if (directed) {
            visualrotation = rotation;
            visualrotation -= 90;
        } else {
            visualrotation = InputField.getRange(particle.getAttribute("rotation").stringValue(), Math.random());
        }

        Position pos;
        if (obj instanceof Particleeffect) {
            pos = getAttribute("pos").positionValue();
        } else {
            double dy = Math.sin(Math.toRadians(rotation));
            double dx = Math.cos(Math.toRadians(rotation));

            double minX = LevelManager.getLevel().getSceneObject().getAttribute("minx").doubleValue();
            double minY = -LevelManager.getLevel().getSceneObject().getAttribute("miny").doubleValue();
            double maxX = LevelManager.getLevel().getSceneObject().getAttribute("maxx").doubleValue();
            double maxY = -LevelManager.getLevel().getSceneObject().getAttribute("maxy").doubleValue();

            double horizontalWeight = Math.abs(dx);

            if (dx > 0) {
                if (dy > 0) {
                    if (Math.random() > horizontalWeight) {
                        pos = new Position(lerp(minX, maxX, Math.random()), minY);
                    } else {
                        pos = new Position(minX, lerp(minY, maxY, Math.random()));
                    }
                } else {
                    if (Math.random() > horizontalWeight) {
                        pos = new Position(lerp(minX, maxX, Math.random()), maxY);
                    } else {
                        pos = new Position(minX, lerp(minY, maxY, Math.random()));
                    }
                }
            } else {
                if (dy > 0) {
                    if (Math.random() > horizontalWeight) {
                        pos = new Position(lerp(minX, maxX, Math.random()), minY);
                    } else {
                        pos = new Position(maxX, lerp(minY, maxY, Math.random()));
                    }
                } else {
                    if (Math.random() > horizontalWeight) {
                        pos = new Position(lerp(minX, maxX, Math.random()), maxY);
                    } else {
                        pos = new Position(maxX, lerp(minY, maxY, Math.random()));
                    }
                }
            }
        }

        if (obj instanceof Ambientparticleeffect) {
            pos = new Position(pos.getX(), -pos.getY());
            lifespan = -1;
        }

        Image image = particle.getImages().get(0);

        ParticleGraphicsInstance particleGraphicsInstance = new ParticleGraphicsInstance(pos, scale, finalscale, visualrotation, rotation, rotspeed, speed, acceleration, lifespan, fade, additive, image, directed, getAttribute("depth").doubleValue());

        drawing.get(i).add(particleGraphicsInstance);

        if (drawing.get(i).size() > obj.getAttribute("maxparticles").doubleValue()) {
            removeObjectPosition(drawing.get(i).get(0).getObjectPosition());
            drawing.get(i).remove(0);
        }

        addObjectComponent(particleGraphicsInstance.getObjectPosition());

    }


    @Override
    public String getName() {
        return getAttribute("effect").stringValue();
    }



    // private Point2D lineIntersection(double x1, double y1, double m1, double x2, double y2, double m2) {
    //     double x = (m1 * x1 - m2 * x2 + y2 - y1) / (m1 - m2);
    //     return new Point2D(x, m1 * (x - x1) + y1);
    // }

    /*
    public void draw(GraphicsContext graphicsContext, GraphicsContext imageGraphicsContext) {


    }

    public DragSettings mouseIntersection(double mX2, double mY2) {
        if (LevelManager.getLevel().isShowParticles()) {
            Position pos = getPosition("pos");

            double x2 = pos.getX();
            double y2 = -pos.getY();

            Font font = Font.font("Arial", FontWeight.BOLD, 30);
            Text text = new Text(getString("effect"));
            text.setFont(font);
            double width1 = text.getLayoutBounds().getWidth();
            double height1 = text.getLayoutBounds().getHeight();

            if (width1 != 0 && height1 != 0) {
                if (mX2 > x2 - width1 / 2 && mX2 < x2 + width1 / 2 && mY2 > y2 - height1 * 1.25 && mY2 < y2 - height1 * 0.25) {
                    DragSettings dragSettings = new DragSettings(DragSettings.MOVE);
                    dragSettings.setInitialSourceX(mX2 - pos.getX());
                    dragSettings.setInitialSourceY(mY2 + pos.getY());
                    return dragSettings;
                }
            }
        }
        return DragSettings.NULL;
    }

     */

}
