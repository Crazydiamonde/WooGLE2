package com.worldOfGoo.scene;

import com.woogleFX.assets.Asset;
import com.woogleFX.assets.wog1.particle.WOG1Particle;
import com.woogleFX.editorObjects.EditorObject;
import com.woogleFX.editorObjects.objectComponents.CircleComponent;
import com.woogleFX.editorObjects.objectComponents.ObjectComponent;
import com.woogleFX.editorObjects.objectComponents.TextComponent;
import com.woogleFX.engine.AssetManager;
import com.woogleFX.engine.renderer.Depth;
import com.woogleFX.gameData.font._Font;
import com.woogleFX.assets.GameVersion;
import com.worldOfGoo.particle.ParticleSpawner;
import com.worldOfGoo.particle.particle;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;

import java.util.ArrayList;

public class particles extends EditorObject implements ParticleSpawner {

    private EditorObject particleEffect;
    public EditorObject getParticleEffect() {
        return particleEffect;
    }


    public particles(EditorObject _parent, GameVersion version) {
        super(_parent, version);
    }

    @Override
    public void onLoaded(Asset asset) {
        super.onLoaded(asset);

        addObjectComponent(new TextComponent(this) {

            public double getRotation() {
                return -Math.PI / 4;
            }

            public Paint getColor() {
                return new Color(1.0, 0, 1.0, 1.0);
            }

            public Paint getBorderColor() {
                return new Color(1.0, 0, 1.0, 1.0);
            }

            public _Font getFont() {
                return null;
            }

            public Font getOtherFont() {
                return new Font(40);
            }

            public String getText() {
                return getAttribute("effect").stringValue();
            }

            public double getX() {
                return getAttribute("pos").positionValue().getX() + 20;
            }

            public void setX(double x) {
                setAttribute("pos", (x - 20) + "," + getAttribute("pos").positionValue().getY());
            }

            public double getY() {
                return -getAttribute("pos").positionValue().getY() - 10;
            }

            public void setY(double y) {
                setAttribute("pos", getAttribute("pos").positionValue().getX() + ", " + (-y - 10));
            }

            public double getDepth() {
                return Depth.MECHANICS;
            }

            public boolean isVisible() {
                return AssetManager.getVisibility("particles") == 1 && AssetManager.getVisibility("labels") == 1;
            }

        });


        addObjectComponent(new CircleComponent(this) {
            @Override
            public double getRadius() {
                return 10;
            }

            public double getX() {
                return getAttribute("pos").positionValue().getX();
            }

            public void setX(double x) {
                setAttribute("pos", x + "," + getAttribute("pos").positionValue().getY());
            }

            public double getY() {
                return -getAttribute("pos").positionValue().getY();
            }

            public void setY(double y) {
                setAttribute("pos", getAttribute("pos").positionValue().getX() + ", " + -y);
            }

            public double getDepth() {
                return Depth.MECHANICS;
            }

            @Override
            public double getEdgeSize() {
                return 4;
            }

            @Override
            public Paint getBorderColor() {
                return new Color(1.0, 0, 1.0, 1.0);
            }

            @Override
            public boolean isEdgeOnly() {
                return false;
            }

            @Override
            public Paint getColor() {
                return new Color(1.0, 0, 1.0, 0.5);
            }

            public boolean isVisible() {
                return AssetManager.getVisibility("particles") == 1;
            }

            public boolean isResizable() {
                return false;
            }

            public boolean isRotatable() {
                return false;
            }

        });

        String effectName = getAttribute("effect").stringValue();
        WOG1Particle wog1Particle = WOG1Particle.assetSelector.openInstance(effectName, getVersion());
        if (wog1Particle != null) particleEffect = wog1Particle.getParticle();

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
    public String getName() {
        return getAttribute("effect").stringValue();
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
        return getAttribute("pos").positionValue().getX();
    }

    @Override
    public double getY() {
        return -getAttribute("pos").positionValue().getY();
    }

    @Override
    public double getDepth() {
        return getAttribute("depth").doubleValue();
    }

    private final ArrayList<ObjectComponent> particleObjectComponents = new ArrayList<>();
    @Override
    public ArrayList<ObjectComponent> getParticleObjectComponents() {
        return particleObjectComponents;
    }
}
