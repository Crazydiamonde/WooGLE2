package com.worldOfGoo.level;

import com.woogleFX.assets.Asset;
import com.woogleFX.assets.wog1.particle.WOG1Particle;
import com.woogleFX.editorObjects.EditorObject;
import com.woogleFX.editorObjects.objectComponents.CircleComponent;
import com.woogleFX.editorObjects.objectComponents.ObjectComponent;
import com.woogleFX.engine.renderer.Depth;
import com.woogleFX.engine.AssetManager;
import com.woogleFX.assets.GameVersion;
import com.worldOfGoo.particle.ParticleSpawner;
import com.worldOfGoo.particle.particle;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

import java.io.IOException;
import java.util.ArrayList;

public class fire extends EditorObject implements ParticleSpawner {

    private EditorObject particleEffect;


    public fire(EditorObject _parent, GameVersion version) {
        super(_parent, version);
    }

    @Override
    public void frameUpdate(double deltaTime) {
        if (particleEffect != null) for (EditorObject child : particleEffect.getChildren()) {
            if (child instanceof particle particle) {
                particle.secretFrameUpdate(deltaTime, this);
            }
        }
    }

    @Override
    public void onLoaded(Asset asset) {
        super.onLoaded(asset);

        addObjectComponent(new CircleComponent(this) {
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
                return 3;
            }
            public Paint getBorderColor() {
                return new Color(1.0, 0.25, 0.0, 1.0);
            }
            public double getDepth() {
                return Depth.GEOMETRY;
            }
            public Paint getColor() {
                return new Color(1.0, 0.25, 0.0, 0.1);
            }
            public boolean isEdgeOnly() {
                return false;
            }
            public boolean isVisible() {
                return AssetManager.getVisibility("geometry") != 0;
            }
        });

        String effectName = getAttribute("particles").stringValue();
        WOG1Particle wog1Particle;
        try {
            wog1Particle = WOG1Particle.assetSelector.openInstance(effectName, getVersion());
        } catch (IOException e) {
            wog1Particle = null;
        }
        if (wog1Particle != null) particleEffect = wog1Particle.getParticle();

    }

    @Override
    public String getName() {
        return getAttribute("particles").stringValue();
    }

    private final ArrayList<Double> creationTimes = new ArrayList<>();
    private final ArrayList<Double> creationPercentages = new ArrayList<>();
    @Override
    public ArrayList<Double> getCreationTimes() {
        return creationTimes;
    }
    @Override
    public ArrayList<Double> getCreationPercentages() {
        return creationPercentages;
    }
    @Override
    public double getX() {
        return getAttribute("x").doubleValue();
    }
    @Override
    public double getY() {
        return -getAttribute("y").doubleValue();
    }
    @Override
    public double getDepth() {
        return getAttribute("depth").doubleValue();
    }
    @Override
    public boolean isVisible() {
        return AssetManager.getVisibility("geometry") != 0;
    }

    private final ArrayList<ObjectComponent> particleObjectComponents = new ArrayList<>();
    @Override
    public ArrayList<ObjectComponent> getParticleObjectComponents() {
        return particleObjectComponents;
    }

}
