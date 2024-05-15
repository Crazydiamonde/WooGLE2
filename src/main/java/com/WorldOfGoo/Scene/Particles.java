package com.WorldOfGoo.Scene;

import java.util.ArrayList;

import com.WooGLEFX.EditorObjects.Components.ObjectPosition;
import com.WooGLEFX.EditorObjects.ParticleGraphicsInstance;
import com.WooGLEFX.Engine.Renderer;
import com.WooGLEFX.Engine.SelectionManager;
import com.WooGLEFX.Functions.LevelManager;
import com.WooGLEFX.Functions.ParticleManager;
import com.WooGLEFX.Structures.EditorObject;
import com.WooGLEFX.Structures.InputField;
import com.WooGLEFX.Structures.SimpleStructures.DragSettings;
import com.WooGLEFX.Structures.SimpleStructures.MetaEditorAttribute;
import com.WooGLEFX.Structures.SimpleStructures.Position;
import com.WorldOfGoo.Level.Fire;
import com.WorldOfGoo.Particle.Ambientparticleeffect;
import com.WorldOfGoo.Particle.Particleeffect;
import com.WorldOfGoo.Particle._Particle;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.BlendMode;
import javafx.scene.image.Image;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.transform.Affine;

public class Particles extends EditorObject {

    private final ArrayList<ArrayList<ParticleGraphicsInstance>> drawing = new ArrayList<>();


    public Particles(EditorObject _parent) {
        super(_parent);
        setRealName("particles");

        addAttribute("effect",  InputField.PARTICLES)                      .assertRequired();
        addAttribute("depth",   InputField.NUMBER)  .setDefaultValue("-20").assertRequired();
        addAttribute("pos",     InputField.POSITION).setDefaultValue("0,0");
        addAttribute("pretick", InputField.NUMBER)  .setDefaultValue("0");
        addAttribute("enabled", InputField.FLAG);

        addObjectPosition(new ObjectPosition(ObjectPosition.RECTANGLE_HOLLOW) {
            public double getX() {
                return getPosition("pos").getX();
            }
            public void setX(double x) {
                setAttribute("pos", x + "," + -getY());
            }
            public double getY() {
                return -getPosition("pos").getY();
            }
            public void setY(double y) {
                setAttribute("pos", getX() + "," + -y);
            }

            // TODO label width and height

        });

        setNameAttribute(getAttribute2("effect"));
        setMetaAttributes(MetaEditorAttribute.parse("effect,pos,depth,pretick,enabled,"));

    }

    @Override
    public void update() {
        setNameAttribute(getAttribute2("effect"));
    }

    private final ArrayList<Integer> counts = new ArrayList<>();

    // private Point2D lineIntersection(double x1, double y1, double m1, double x2, double y2, double m2) {
    //     double x = (m1 * x1 - m2 * x2 + y2 - y1) / (m1 - m2);
    //     return new Point2D(x, m1 * (x - x1) + y1);
    // }

    private double lerp(double a, double b, double m) {
        return a * m + b * (1 - m);
    }

    public void draw(GraphicsContext graphicsContext, GraphicsContext imageGraphicsContext) {
        if (LevelManager.getLevel().isShowParticles()) {
            for (EditorObject obj : ParticleManager.getParticles()) {
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
                            if ((obj instanceof Particleeffect && counts.get(i) > obj.getDouble("rate") * 50) || (obj instanceof Ambientparticleeffect && drawing.get(i).size() < obj.getDouble("maxparticles"))) {
                                counts.set(i, 0);

                                double scale = InputField.getRange(particle.getAttribute("scale"), Math.random());
                                double finalscale = InputField.getRange(particle.getAttribute("finalscale"), Math.random());
                                double absvar = InputField.getRange(particle.getAttribute("movedirvar"), Math.random());
                                double rotation = InputField.getRange(particle.getAttribute("movedir"), Math.random()) + InputField.getRange(-absvar + "," + absvar, randomMovedir);
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

                                    double minX = LevelManager.getLevel().getSceneObject().getDouble("minx");
                                    double minY = -LevelManager.getLevel().getSceneObject().getDouble("miny");
                                    double maxX = LevelManager.getLevel().getSceneObject().getDouble("maxx");
                                    double maxY = -LevelManager.getLevel().getSceneObject().getDouble("maxy");

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

                                if (drawing.get(i).size() > obj.getDouble("maxparticles")) {
                                    drawing.get(i).remove(0);
                                }
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

        if (!(getParent() instanceof Fire) && getLevel() != null) {
            Font font = Font.font("Arial", FontWeight.BOLD, 30 * getLevel().getZoom());

            Position pos = getPosition("pos");
            double screenX2 = pos.getX() * getLevel().getZoom() + getLevel().getOffsetX();
            double screenY2 = -pos.getY() * getLevel().getZoom() + getLevel().getOffsetY();

            Text text = new Text(getString("effect"));
            text.setFont(font);
            double width = text.getLayoutBounds().getWidth();
            double height = text.getLayoutBounds().getHeight();

            graphicsContext.setFill(Renderer.particleLabels);
            graphicsContext.setFont(font);
            graphicsContext.fillText(getString("effect"), screenX2 - width / 2, screenY2 - height / 2);

            if (this == SelectionManager.getSelected()) {

                graphicsContext.setStroke(Renderer.selectionOutline2);
                graphicsContext.setLineWidth(1);
                graphicsContext.setLineDashes(3);
                graphicsContext.setLineDashOffset(0);
                graphicsContext.strokeRect(screenX2 - width / 2, screenY2 - height * 1.25, width, height);
                graphicsContext.setStroke(Renderer.selectionOutline);
                graphicsContext.setLineWidth(1);
                graphicsContext.setLineDashOffset(3);
                graphicsContext.strokeRect(screenX2 - width / 2, screenY2 - height * 1.25, width, height);
                graphicsContext.setLineDashes(0);
                graphicsContext.restore();

            }

        }

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


    @Override
    public boolean isVisible() {
        return LevelManager.getLevel().isShowParticles();
    }

}
