package com.WooGLEFX.EditorObjects;

import com.WooGLEFX.EditorObjects.objectcomponents.ImageComponent;
import com.WooGLEFX.EditorObjects.objectcomponents.ObjectComponent;
import com.WooGLEFX.Engine.EditorWindow;
import com.WooGLEFX.Functions.LevelManager;
import com.WooGLEFX.Structures.SimpleStructures.Position;
import javafx.scene.image.Image;

public class ParticleGraphicsInstance {

    private final Position originalPos;
    private final double initialScale;
    private final double finalscale;
    private final Position acceleration;
    private final double rotation;
    private final double initialDisplayRotation;
    private final double rotspeed;
    private final double speed;
    private final double lifespan;
    private final boolean fade;
    private double alpha;
    private final double creationTimestamp;
    private final boolean additive;
    private final boolean directed;

    public boolean getAdditive() {
        return additive;
    }

    public double getAlpha() {
        return alpha;
    }


    private final ObjectComponent objectComponent;
    public ObjectComponent getObjectPosition() {
        return objectComponent;
    }


    public ParticleGraphicsInstance(Position pos, double scale, double finalscale, double visualrotation, double rotation, double rotspeed, double speed, Position acceleration, double lifespan, boolean fade, boolean additive, Image image, boolean directed, double depth) {
        this.originalPos = new Position(pos.getX(), pos.getY());
        this.initialScale = scale;
        this.finalscale = finalscale;
        this.rotation = rotation;
        this.rotspeed = rotspeed;
        this.initialDisplayRotation = visualrotation;
        this.speed = speed;
        this.acceleration = acceleration;
        this.lifespan = lifespan;
        this.fade = fade;
        this.additive = additive;
        this.alpha = 1;
        this.directed = directed;
        this.creationTimestamp = EditorWindow.getTimeElapsed();

        this.objectComponent = new ImageComponent() {
            public double getX() {
                double dt = EditorWindow.getTimeElapsed() - creationTimestamp;
                double initialX = originalPos.getX();
                double initialVelocityX = speed * Math.cos(Math.toRadians(-rotation));
                double accelerationX = acceleration.getX();
                return initialX + (initialVelocityX + 0.5 * accelerationX * dt * 50) * dt * 50;
            }
            public double getY() {
                double dt = EditorWindow.getTimeElapsed() - creationTimestamp;
                double initialY = -originalPos.getY();
                double initialVelocityY = speed * Math.sin(Math.toRadians(-rotation));
                double accelerationY = -acceleration.getY();
                return initialY + (initialVelocityY + 0.5 * accelerationY * dt * 50) * dt * 50;
            }
            public double getRotation() {
                if (directed) {
                    double initialVelocityX = speed * Math.cos(Math.toRadians(-rotation));
                    double initialVelocityY = speed * Math.sin(Math.toRadians(-rotation));
                    double accelerationX = acceleration.getX();
                    double accelerationY = -acceleration.getY();
                    double dt = EditorWindow.getTimeElapsed() - creationTimestamp;
                    double velocityX = initialVelocityX + accelerationX * dt * 50;
                    double velocityY = initialVelocityY + accelerationY * dt * 50;
                    return Math.atan2(velocityY, velocityX) - 90;
                } else {
                    double dt = EditorWindow.getTimeElapsed() - creationTimestamp;
                    return Math.toRadians(-initialDisplayRotation) - rotspeed * dt;
                }
            }
            public double getScaleX() {
                if (lifespan != -1) {
                    double dt = EditorWindow.getTimeElapsed() - creationTimestamp;
                    double amtElapsed = dt / lifespan;
                    return initialScale * (1 - amtElapsed) + finalscale * amtElapsed;
                } else {
                    return initialScale;
                }
            }
            public double getScaleY() {
                if (lifespan != -1) {
                    double dt = EditorWindow.getTimeElapsed() - creationTimestamp;
                    double amtElapsed = dt / lifespan;
                    return initialScale * (1 - amtElapsed) + finalscale * amtElapsed;
                } else {
                    return initialScale;
                }
            }
            public Image getImage() {
                return image;
            }
            public double getDepth() {
                return depth;
            }
            public boolean isAdditive() {
                return additive;
            }
            public double getAlpha() {
                if (lifespan != -1 && fade) {
                    double dt = EditorWindow.getTimeElapsed() - creationTimestamp;
                    return 1 - dt / lifespan;
                }
                return 1.0;
            }
            public boolean isVisible() {
                return LevelManager.getLevel().getVisibilitySettings().isShowParticles();
            }
            public boolean isSelectable() {
                return false;
            }
            public boolean isDraggable() {
                return false;
            }
            public boolean isResizable() {
                return false;
            }
            public boolean isRotatable() {
                return false;
            }
        };

    }

    public boolean update() {
        double angle = Math.toRadians(this.rotation);

        double dt = EditorWindow.getTimeElapsed() - this.creationTimestamp;

        double x = this.originalPos.getX() + (this.speed * Math.cos(angle) + 0.5 * this.acceleration.getX() * dt * 50) * dt * 50;
        double y = this.originalPos.getY() + (this.speed * Math.sin(angle) + 0.5 * this.acceleration.getY() * dt * 50) * dt * 50;
        if (lifespan == -1) {
            if (x < LevelManager.getLevel().getSceneObject().getAttribute("minx").doubleValue() ||
                    y < LevelManager.getLevel().getSceneObject().getAttribute("miny").doubleValue() ||
                    x > LevelManager.getLevel().getSceneObject().getAttribute("maxx").doubleValue() ||
                    y > LevelManager.getLevel().getSceneObject().getAttribute("maxy").doubleValue()) {
                return true;
            }
        } else if (dt > this.lifespan) {
            return true;
        }
        if (lifespan != -1) {
            double thing = dt / this.lifespan;
            //this.scale = this.initialScale * (1 - thing) + this.finalscale * thing;
            if (fade) {
                this.alpha = 1 - thing;
            }
        }
        return false;
    }

}
