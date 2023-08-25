package com.WooGLEFX.EditorObjects;

import com.WooGLEFX.Engine.EditorWindow;
import com.WooGLEFX.Engine.Main;
import com.WooGLEFX.Structures.SimpleStructures.Position;

public class ParticleGraphicsInstance {

    private final Position originalPos;
    private Position pos;
    private double scale;
    private final double initialScale;
    private final double finalscale;
    private final Position acceleration;
    private final double rotation;
    private double displayRotation;
    private final double initialDisplayRotation;
    private final double rotspeed;
    private final double speed;
    private final double lifespan;
    private final boolean fade;
    private double alpha;
    private final double creationTimestamp;
    private final boolean additive;

    public boolean getAdditive() {
        return additive;
    }

    public Position getPos() {
        return pos;
    }

    public void setPos(Position pos) {
        this.pos = pos;
    }

    public double getScale() {
        return scale;
    }

    public void setScale(double scale) {
        this.scale = scale;
    }

    public double getDisplayRotation() {
        return displayRotation;
    }

    public void setDisplayRotation(double displayRotation) {
        this.displayRotation = displayRotation;
    }

    public double getAlpha() {
        return alpha;
    }

    public ParticleGraphicsInstance(Position pos, double scale, double finalscale, double visualrotation, double rotation, double rotspeed, double speed, Position acceleration, double lifespan, boolean fade, boolean additive) {
        this.originalPos = new Position(pos.getX(), pos.getY());
        this.pos = pos;
        this.scale = scale;
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
        this.creationTimestamp = EditorWindow.getTimeElapsed();
    }

    public boolean update(){
        double angle = Math.toRadians(this.rotation);

        double dt = EditorWindow.getTimeElapsed() - this.creationTimestamp;

        this.pos.setX(this.originalPos.getX() + (this.speed * Math.cos(angle) + 0.5 * this.acceleration.getX() * dt * 50) * dt * 50);
        this.pos.setY(this.originalPos.getY() + (this.speed * Math.sin(angle) + 0.5 * this.acceleration.getY() * dt * 50) * dt * 50);
        if (lifespan == -1) {
            if (pos.getX() < Main.getLevel().getSceneObject().getDouble("minx") ||
                    pos.getY() < Main.getLevel().getSceneObject().getDouble("miny") ||
                    pos.getX() > Main.getLevel().getSceneObject().getDouble("maxx") ||
                    pos.getY() > Main.getLevel().getSceneObject().getDouble("maxy")) {
                return true;
            }
        } else if (dt > this.lifespan) {
            return true;
        }
        this.displayRotation = this.initialDisplayRotation + Math.toDegrees(rotspeed * dt);
        if (lifespan != -1) {
            double thing = dt / this.lifespan;
            this.scale = this.initialScale * (1 - thing) + this.finalscale * thing;
            if (fade) {
                this.alpha = 1 - thing;
            }
        }
        return false;
    }

}
