package com.WooGLEFX.EditorObjects;

import com.WooGLEFX.File.ResourceManagers.ParticleManager;
import com.WooGLEFX.Functions.LevelManager;
import com.WooGLEFX.Structures.SimpleStructures.Position;
import com.WooGLEFX.Structures.WorldLevel;
import com.WorldOfGoo.Level.Fire;
import com.WorldOfGoo.Particle.Ambientparticleeffect;
import com.WorldOfGoo.Particle.Particleeffect;
import com.WorldOfGoo.Particle._Particle;
import com.WorldOfGoo.Scene.Particles;
import javafx.scene.image.Image;

import java.util.ArrayList;

public class ParticleUtility {

    private static double lerp(double a, double b, double m) {
        return a * m + b * (1 - m);
    }


    public static void frameUpdate(EditorObject particleObject, ArrayList<Integer> counts, ArrayList<ArrayList<ParticleGraphicsInstance>> drawing) {

        WorldLevel level = LevelManager.getLevel();
        if (level == null) return;

        String effect = "";
        if (particleObject instanceof Particles) effect = particleObject.getAttribute("effect").stringValue();
        if (particleObject instanceof Fire) effect = particleObject.getAttribute("particles").stringValue();

        if (!level.getVisibilitySettings().isShowParticles()) return;

        for (EditorObject obj : ParticleManager.getParticles()) {
            if (obj instanceof Particleeffect || obj instanceof Ambientparticleeffect) {
                String name = obj.getAttribute("name").stringValue();
                if (name.equals(effect)) updateParticle(obj, particleObject, counts, drawing);
            }
        }

    }


    public static void updateParticle(EditorObject obj, EditorObject particleObject, ArrayList<Integer> counts, ArrayList<ArrayList<ParticleGraphicsInstance>> drawing) {

        ArrayList<_Particle> particles = new ArrayList<>();
        if (obj instanceof Particleeffect) particles = ((Particleeffect) obj).getParticles();
        if (obj instanceof Ambientparticleeffect) particles = ((Ambientparticleeffect) obj).getParticles();

        int i = 0;
        for (int i2 = particles.size() - 1; i2 >= 0; i2--) {
            updateParticleObject(particles.get(i2), i, obj, particleObject, counts, drawing);
            i++;
        }

    }


    public static void updateParticleObject(_Particle particle, int i, EditorObject obj, EditorObject particleObject, ArrayList<Integer> counts, ArrayList<ArrayList<ParticleGraphicsInstance>> drawing) {

        if (counts.size() == i) counts.add(0);
        counts.set(i, counts.get(i) + 1);
        if (drawing.size() == i) drawing.add(new ArrayList<>());

        boolean shouldCreateNewInstance = false;
        if (obj instanceof Particleeffect)
            shouldCreateNewInstance = counts.get(i) > 1 / obj.getAttribute("rate").doubleValue();
        if (obj instanceof Ambientparticleeffect)
            shouldCreateNewInstance = drawing.get(i).size() < obj.getAttribute("maxparticles").doubleValue();
        if (shouldCreateNewInstance) createNewParticleGraphicsInstance(i, obj, particle, particleObject, counts, drawing);

        for (ParticleGraphicsInstance particleGraphicsInstance : drawing.get(i).toArray(new ParticleGraphicsInstance[0])) {
            if (particleGraphicsInstance.update()) {
                drawing.get(i).remove(particleGraphicsInstance);
                particleObject.removeObjectPosition(particleGraphicsInstance.getObjectPosition());
            }
        }

    }


    public static void createNewParticleGraphicsInstance(int i, EditorObject obj, _Particle particle, EditorObject particleObject, ArrayList<Integer> counts, ArrayList<ArrayList<ParticleGraphicsInstance>> drawing) {

        counts.set(i, 0);

        double scale = InputField.getRange(particle.getAttribute("scale").stringValue(), Math.random());
        double finalscale = InputField.getRange(particle.getAttribute("finalscale").stringValue(), Math.random());
        double absvar = InputField.getRange(particle.getAttribute("movedirvar").stringValue(), Math.random());
        double rotation = InputField.getRange(particle.getAttribute("movedir").stringValue(), Math.random()) + InputField.getRange(-absvar + "," + absvar, Math.random());
        double visualRotation = InputField.getRange(particle.getAttribute("movedir").stringValue(), Math.random());
        double rotspeed = InputField.getRange(particle.getAttribute("rotspeed").stringValue(), Math.random());
        double speed = InputField.getRange(particle.getAttribute("speed").stringValue(), Math.random());
        double lifespan = InputField.getRange(particle.getAttribute("lifespan").stringValue(), Math.random());
        Position acceleration = Position.parse(particle.getAttribute("acceleration").stringValue());
        boolean fade = particle.getAttribute("fade").booleanValue();
        boolean additive = particle.getAttribute("additive").booleanValue();
        boolean directed = particle.getAttribute("directed").booleanValue();

        Position pos = new Position(0,0);
        if (obj instanceof Particleeffect) {
            if (particleObject instanceof Particles) {
                pos = particleObject.getAttribute("pos").positionValue();
            }
            if (particleObject instanceof Fire) {
                pos = new Position(particleObject.getAttribute("x").doubleValue(), particleObject.getAttribute("y").doubleValue());
            }
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

        ParticleGraphicsInstance particleGraphicsInstance = new ParticleGraphicsInstance(pos, scale, finalscale, visualRotation, rotation, rotspeed, speed, acceleration, lifespan, fade, additive, image, directed, particleObject.getAttribute("depth").doubleValue());

        drawing.get(i).add(particleGraphicsInstance);

        if (drawing.get(i).size() > obj.getAttribute("maxparticles").doubleValue()) {
            particleObject.removeObjectPosition(drawing.get(i).get(0).getObjectPosition());
            drawing.get(i).remove(0);
        }

        particleObject.addObjectComponent(particleGraphicsInstance.getObjectPosition());

    }

}
