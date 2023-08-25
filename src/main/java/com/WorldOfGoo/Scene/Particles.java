package com.WorldOfGoo.Scene;

import com.WooGLEFX.File.FileManager;
import com.WooGLEFX.Engine.Main;
import com.WooGLEFX.EditorObjects.ParticleGraphicsInstance;
import com.WooGLEFX.Structures.EditorObject;
import com.WooGLEFX.Structures.InputField;
import com.WooGLEFX.Structures.SimpleStructures.MetaEditorAttribute;
import com.WooGLEFX.Structures.SimpleStructures.Position;
import com.WorldOfGoo.Particle.Ambientparticleeffect;
import com.WorldOfGoo.Particle.Particleeffect;
import com.WorldOfGoo.Particle._Particle;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.BlendMode;
import javafx.scene.image.Image;
import javafx.scene.transform.Affine;

import java.util.ArrayList;

public class Particles extends EditorObject {

    private ArrayList<ArrayList<ParticleGraphicsInstance>> drawing = new ArrayList<>();

    private Particleeffect particleeffect;
    private Ambientparticleeffect ambientparticleeffect;

    public Particles(EditorObject _parent) {
        super(_parent);
        setRealName("particles");
        addAttribute("effect", "", InputField.ANY, true);
        addAttribute("depth", "-20", InputField.NUMBER, true);
        addAttribute("pos", "0,0", InputField.POSITION, false);
        addAttribute("pretick", "0", InputField.NUMBER, false);
        addAttribute("enabled", "true", InputField.FLAG, false);
        setNameAttribute(getAttribute2("effect"));
        setMetaAttributes(MetaEditorAttribute.parse("effect,pos,depth,pretick,enabled,"));
    }

    @Override
    public void update() {
        setNameAttribute(getAttribute2("effect"));
    }

    private ArrayList<Integer> counts = new ArrayList<>();

    private Point2D lineIntersection(double x1, double y1, double m1, double x2, double y2, double m2) {
        double x = (m1 * x1 - m2 * x2 + y2 - y1) / (m1 - m2);
        return new Point2D(x, m1 * (x - x1) + y1);
    }

    private double lerp(double a, double b, double m) {
        return a * m + b * (1 - m);
    }

    @Override
    public void draw(GraphicsContext graphicsContext, GraphicsContext imageGraphicsContext) {
        if (Main.getLevel().isShowParticles()) {
            for (EditorObject obj : Main.getParticles()) {
                if (obj instanceof Particleeffect || obj instanceof Ambientparticleeffect) {
                    if (obj.getAttribute("name").equals(getAttribute("effect"))) {

                        ArrayList<_Particle> particles = new ArrayList<>();
                        if (obj instanceof Particleeffect) {
                            particles = ((Particleeffect) obj).getParticles();
                        }
                        if (obj instanceof Ambientparticleeffect) {
                            particles = ((Ambientparticleeffect) obj).getParticles();
                        }

                        double randomMovedir = Math.random();

                        int i = 0;
                        for (int i2 = particles.size() - 1; i2 >= 0; i2--) {
                            if (counts.size() == i) {
                                counts.add(0);
                            }
                            counts.set(i, counts.get(i) + 1);
                            _Particle particle = particles.get(i2);
                            if (drawing.size() == i) {
                                drawing.add(new ArrayList<>());
                            }
                            if (drawing.get(i).size() < obj.getDouble("maxparticles") && counts.get(i) > 1 / obj.getDouble("rate")) {
                                counts.set(i, 0);

                                double scale = InputField.getRange(particle.getAttribute("scale"), Math.random());
                                double finalscale = InputField.getRange(particle.getAttribute("finalscale"), Math.random());
                                double absvar = particle.getDouble("movedirvar");
                                double rotation = particle.getDouble("movedir") + InputField.getRange(-absvar + "," + absvar, randomMovedir);
                                double rotspeed = InputField.getRange(particle.getAttribute("rotspeed"), Math.random());
                                double speed = InputField.getRange(particle.getAttribute("speed"), Math.random());
                                double lifespan = InputField.getRange(particle.getAttribute("lifespan"), Math.random());
                                Position acceleration = Position.parse(particle.getAttribute("acceleration"));
                                boolean fade = particle.getBoolean("fade");
                                boolean additive = Boolean.parseBoolean(particle.getAttribute("additive"));

                                double visualrotation;
                                if (particle.getBoolean("directed")) {
                                    visualrotation = rotation;
                                    visualrotation -= 90;
                                } else {
                                    visualrotation = Math.toDegrees(InputField.getRange(particle.getString("rotation"), Math.random()));
                                }

                                Position pos;
                                if (obj instanceof Particleeffect) {
                                    pos = Position.parse(getAttribute("pos"));
                                } else {
                                    double dy = Math.sin(Math.toRadians(rotation));
                                    double dx = Math.cos(Math.toRadians(rotation));

                                    double minX = Main.getLevel().getSceneObject().getDouble("minx");
                                    double minY = -Main.getLevel().getSceneObject().getDouble("miny");
                                    double maxX = Main.getLevel().getSceneObject().getDouble("maxx");
                                    double maxY = -Main.getLevel().getSceneObject().getDouble("maxy");

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

                                drawing.get(i).add(new ParticleGraphicsInstance(pos, scale, finalscale, visualrotation, rotation, rotspeed, speed, acceleration, lifespan, fade, additive));
                            }

                            ParticleGraphicsInstance toBeRemoved = null;
                            for (ParticleGraphicsInstance particleGraphicsInstance : drawing.get(i)) {
                                Image img = particle.getImages().get(0);
                                if (img != null) {
                                    double scale = particleGraphicsInstance.getScale();
                                    double rotation = particleGraphicsInstance.getDisplayRotation();
                                    imageGraphicsContext.save();
                                    imageGraphicsContext.setGlobalAlpha(particleGraphicsInstance.getAlpha());
                                    Affine t = imageGraphicsContext.getTransform();
                                    t.appendRotation(rotation, particleGraphicsInstance.getPos().getX(), -particleGraphicsInstance.getPos().getY());
                                    imageGraphicsContext.setTransform(t);
                                    if (particleGraphicsInstance.getAdditive()) {
                                        //TODO fix particles drawing with high opacity so that this can go back to ADD
                                        imageGraphicsContext.setGlobalBlendMode(BlendMode.SRC_OVER);
                                    }
                                    imageGraphicsContext.drawImage(img, particleGraphicsInstance.getPos().getX() - img.getWidth() * scale / 2, -particleGraphicsInstance.getPos().getY() - img.getHeight() * scale / 2, img.getWidth() * scale, img.getHeight() * scale);
                                    if (particleGraphicsInstance.update()) {
                                        toBeRemoved = particleGraphicsInstance;
                                    }
                                    imageGraphicsContext.restore();
                                }
                            }
                            if (toBeRemoved != null) {
                                drawing.get(i).remove(toBeRemoved);
                            }
                            i++;
                        }
                    }
                }
            }
        }
    }
}
