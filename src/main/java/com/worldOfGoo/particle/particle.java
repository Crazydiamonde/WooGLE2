package com.worldOfGoo.particle;

import java.util.ArrayList;
import java.util.List;

import com.woogleFX.editorObjects.EditorObject;
import com.woogleFX.assets.Asset;
import com.woogleFX.editorObjects.ResourceUser;
import com.woogleFX.editorObjects.objectComponents.ImageComponent;
import com.woogleFX.editorObjects.objectComponents.ObjectComponent;
import com.woogleFX.engine.AssetManager;
import com.woogleFX.engine.gui.EditorWindow;
import com.woogleFX.file.resourceManagers.ResourceManager;
import com.woogleFX.assets.GameVersion;
import com.woogleFX.editorObjects.attributes.InputField;

import com.worldOfGoo.resrc.ResourceInterface;
import com.worldOfGoo.scene.particles;
import com.worldOfGoo.scene.scene;
import javafx.scene.image.Image;

public class particle extends EditorObject implements ResourceUser {

    private final ArrayList<Image> images = new ArrayList<>();
    public ArrayList<Image> getImages() {
        return images;
    }


    private int total = 0;


    // TODO: implement these into the particle system. To be honest, I don't even know what these do
    private final ArrayList<axialsinoffset> axialsinoffsets = new ArrayList<>();


    public particle(EditorObject _parent, GameVersion version) {
        super(_parent, version);
    }


    @Override
    public void onLoaded(Asset asset) {
        super.onLoaded(asset);
        try {
            update(getVersion());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void update(GameVersion version) throws Exception {
        // TODO: handle particle resources and allow users to add new ones in the particle. that would be awesome

        axialsinoffsets.clear();
        for (EditorObject EditorObject : getChildren()) {
            if (EditorObject instanceof axialsinoffset axialsinoffset) {
                axialsinoffsets.add(axialsinoffset);
            }
        }

        images.clear();
        for (String image : getAttribute("image").listValue()) {
            images.add(ResourceManager.getImage(null, image, version));
        }

    }

    public void reset(ParticleSpawner parent) {
        if (!(parent instanceof EditorObject editorObject)) return;
        for (ObjectComponent objectComponent : parent.getParticleObjectComponents()) {
            editorObject.removeObjectComponent(objectComponent);
        }
        parent.getParticleObjectComponents().clear();
        parent.getCreationTimes().clear();
        parent.getCreationPercentages().clear();
        total = 0;
    }


    public void secretFrameUpdate(double deltaTime, ParticleSpawner parent) {

        quotaTime += deltaTime;

        double rate;

        if (getParent() instanceof particleeffect) {
            rate = getParent().getAttribute("rate").doubleValue();
        } else {
            rate = 0;
        }

        int maxParticles = getParent().getAttribute("maxparticles").intValue() * getParent().getChildren().size();

        if (getParent() instanceof particleeffect) {

            while (rate == 0 ? total < maxParticles : quotaTime > 0.02 / rate) {

                if (rate != 0) quotaTime -= 0.02 / rate;

                createNewParticleComponent(parent);

            }

        } else {

            while (parent.getParticleObjectComponents().size() < maxParticles) {

                createNewParticleComponent(parent);

            }

        }

        if (!(parent instanceof EditorObject editorObject)) return;

        for (int i = parent.getParticleObjectComponents().size() - 1; i >= 0; i--) {
            if (i >= parent.getCreationTimes().size()) {
                editorObject.removeObjectComponent(parent.getParticleObjectComponents().get(i));
                parent.getParticleObjectComponents().remove(i);
            } else {

                boolean shouldDelete = getParent() instanceof particleeffect && EditorWindow.getTimeElapsed() - parent.getCreationTimes().get(i) > InputField.getRange(getAttribute("lifespan").stringValue(), parent.getCreationPercentages().get(i));
                if (!shouldDelete && getParent() instanceof ambientparticleeffect) {
                    if (parent instanceof particles particles) {
                        EditorObject scene = particles.getParent();
                        ObjectComponent objectComponent = parent.getParticleObjectComponents().get(i);
                        if (objectComponent.getX() < scene.getAttribute("minx").doubleValue() ||
                                objectComponent.getY() < -scene.getAttribute("maxy").doubleValue() ||
                                objectComponent.getX() > scene.getAttribute("maxx").doubleValue() ||
                                objectComponent.getY() > -scene.getAttribute("miny").doubleValue()) shouldDelete = true;
                    } else {
                        ObjectComponent objectComponent = parent.getParticleObjectComponents().get(i);
                        if (objectComponent.getX() < -500 ||
                                objectComponent.getY() < -500 ||
                                objectComponent.getX() > 500 ||
                                objectComponent.getY() > 500) shouldDelete = true;
                    }
                }
                if (shouldDelete) {
                    editorObject.removeObjectComponent(parent.getParticleObjectComponents().get(i));
                    parent.getParticleObjectComponents().remove(i);
                    parent.getCreationTimes().remove(i);
                    parent.getCreationPercentages().remove(i);
                }
            }
        }

        if (parent.getCreationTimes().size() > parent.getParticleObjectComponents().size()) {
            for (int i = parent.getCreationTimes().size() - 1; i >= parent.getParticleObjectComponents().size(); i--) {
                parent.getCreationTimes().remove(i);
                parent.getCreationPercentages().remove(i);
            }
        }

    }


    private double quotaTime = 0;




    private void createNewParticleComponent(ParticleSpawner parent) {

        if (images.isEmpty()) return;
        Image image = images.get((int)(Math.random() * images.size()));

        if (!(parent instanceof EditorObject editorObject)) return;

        double creationTimestamp = EditorWindow.getTimeElapsed();
        double lifespanPercentage = Math.random();

        int maxParticles = getParent().getAttribute("maxparticles").intValue() * getParent().getChildren().size();
        if (maxParticles == 0) return; // what the fuck?

        if (getParent() instanceof particleeffect && parent.getCreationTimes().size() >= maxParticles) {
            editorObject.removeObjectComponent(parent.getParticleObjectComponents().get(0));
            parent.getParticleObjectComponents().remove(parent.getParticleObjectComponents().remove(0));
            parent.getCreationTimes().remove(0);
            parent.getCreationPercentages().remove(0);
        }
        parent.getCreationTimes().add(creationTimestamp);
        parent.getCreationPercentages().add(lifespanPercentage);
        total++;

        double speedPercentage = Math.random();
        double scalePercentage = Math.random();
        double moveDirVarPercentage = 2 * Math.random() - 1;

        double initialX;
        double initialY;
        if (getParent() instanceof ambientparticleeffect) {
            if (editorObject.getParent() instanceof scene scene) {

                double initialAngle = -Math.toRadians(getAttribute("movedir").doubleValue() + moveDirVarPercentage * getAttribute("movedirvar").doubleValue());

                if (initialAngle > Math.PI) initialAngle -= 2 * Math.PI;
                if (initialAngle < -Math.PI) initialAngle += 2 * Math.PI;

                double minx = scene.getAttribute("minx").doubleValue();
                double miny = -scene.getAttribute("maxy").doubleValue();
                double maxx = scene.getAttribute("maxx").doubleValue();
                double maxy = -scene.getAttribute("miny").doubleValue();

                double width = maxx - minx;
                double height = maxy - miny;

                if (Math.random() > Math.abs(height * Math.cos(initialAngle)) / (Math.abs(height * Math.cos(initialAngle)) + Math.abs(width * Math.sin(initialAngle)))) {
                    // horizontal edge
                    initialX = minx + Math.random() * (maxx - minx);
                    initialY = (Math.sin(initialAngle)) > 0 ? miny : maxy;
                } else {
                    // vertical edge
                    initialX = (Math.cos(initialAngle) > 0) ? minx : maxx;
                    initialY = miny + Math.random() * (maxy - miny);
                }

            } else {

                double initialAngle = -Math.toRadians(getAttribute("movedir").doubleValue() + moveDirVarPercentage * getAttribute("movedirvar").doubleValue());

                if (initialAngle > Math.PI) initialAngle -= 2 * Math.PI;
                if (initialAngle < -Math.PI) initialAngle += 2 * Math.PI;

                if (Math.random() > Math.abs(Math.cos(initialAngle)) / (Math.abs(Math.cos(initialAngle)) + Math.abs(Math.sin(initialAngle)))) {
                    // horizontal edge
                    initialX = -500 + Math.random() * 1000;
                    initialY = (Math.sin(initialAngle)) > 0 ? -500 : 500;
                } else {
                    // vertical edge
                    initialX = (Math.cos(initialAngle) > 0) ? -500 : 500;
                    initialY = -500 + Math.random() * 1000;
                }
            }
        } else {
            initialX = parent.getX();
            initialY = parent.getY();
        }

        ImageComponent imageComponent = new ImageComponent(editorObject) {
            public double getX() {
                double speed = InputField.getRange(getAttribute("speed").stringValue(), speedPercentage);
                double dt = EditorWindow.getTimeElapsed() - creationTimestamp;
                double initialVelocityX = speed * Math.cos(Math.toRadians(-getAttribute("movedir").doubleValue() + moveDirVarPercentage * getAttribute("movedirvar").doubleValue()));
                double accelerationX = getAttribute("acceleration").positionValue().getX();
                return initialX + (initialVelocityX + 0.5 * accelerationX * dt * 50) * dt * 50;
            }
            public double getY() {
                double speed = InputField.getRange(getAttribute("speed").stringValue(), speedPercentage);
                double dt = EditorWindow.getTimeElapsed() - creationTimestamp;
                double initialVelocityY = speed * Math.sin(Math.toRadians(-getAttribute("movedir").doubleValue() + moveDirVarPercentage * getAttribute("movedirvar").doubleValue()));
                double accelerationY = -getAttribute("acceleration").positionValue().getY();
                return initialY + (initialVelocityY + 0.5 * accelerationY * dt * 50) * dt * 50;
            }
            public double getRotation() {
                if (getAttribute("directed").booleanValue()) {
                    double speed = InputField.getRange(getAttribute("speed").stringValue(), speedPercentage);
                    double initialVelocityX = speed * Math.cos(Math.toRadians(-getAttribute("movedir").doubleValue() + moveDirVarPercentage * getAttribute("movedirvar").doubleValue()));
                    double initialVelocityY = speed * Math.sin(Math.toRadians(-getAttribute("movedir").doubleValue() + moveDirVarPercentage * getAttribute("movedirvar").doubleValue()));
                    double accelerationX = getAttribute("acceleration").positionValue().getX();
                    double accelerationY = -getAttribute("acceleration").positionValue().getY();
                    double dt = EditorWindow.getTimeElapsed() - creationTimestamp;
                    double velocityX = initialVelocityX + accelerationX * dt * 50;
                    double velocityY = initialVelocityY + accelerationY * dt * 50;
                    return Math.atan2(velocityY, velocityX) + Math.PI / 2;
                } else {
                    double dt = EditorWindow.getTimeElapsed() - creationTimestamp;
                    return Math.toRadians(-0) - InputField.getRange(getAttribute("rotspeed").stringValue(), 0) * dt;
                }
            }
            public double getScaleX() {
                double lifespan = InputField.getRange(getAttribute("lifespan").stringValue(), lifespanPercentage);
                double scale = InputField.getRange(getAttribute("scale").stringValue(), scalePercentage);
                if (getAttribute("finalscale").actualValue().isEmpty()) return scale;
                double finalscale = InputField.getRange(getAttribute("finalscale").stringValue(), scalePercentage);
                if (lifespan != -1) {
                    double dt = EditorWindow.getTimeElapsed() - creationTimestamp;
                    double amtElapsed = dt / lifespan;
                    return scale * (1 - amtElapsed) + finalscale * amtElapsed;
                } else {
                    return scale;
                }
            }
            public double getScaleY() {
                double lifespan = InputField.getRange(getAttribute("lifespan").stringValue(), lifespanPercentage);
                double scale = InputField.getRange(getAttribute("scale").stringValue(), scalePercentage);
                if (getAttribute("finalscale").actualValue().isEmpty()) return scale;
                double finalscale = InputField.getRange(getAttribute("finalscale").stringValue(), scalePercentage);
                if (lifespan != -1) {
                    double dt = EditorWindow.getTimeElapsed() - creationTimestamp;
                    double amtElapsed = dt / lifespan;
                    return scale * (1 - amtElapsed) + finalscale * amtElapsed;
                } else {
                    return scale;
                }
            }
            public Image getImage() {
                return image;
            }
            public double getDepth() {
                return parent.getDepth();
            }
            public boolean isAdditive() {
                return getAttribute("additive").booleanValue();
            }
            public double getAlpha() {
                double lifespan = InputField.getRange(getAttribute("lifespan").stringValue(), lifespanPercentage);
                if (lifespan != -1 && getAttribute("fade").booleanValue()) {
                    double dt = EditorWindow.getTimeElapsed() - creationTimestamp;
                    return 1 - dt / lifespan;
                }
                return 1.0;
            }
            public boolean isVisible() {
                return AssetManager.getVisibility("particles") == 1;
            }
            public boolean isSelectable() {
                return false;
            }
        };

        parent.getParticleObjectComponents().add(imageComponent);
        editorObject.addObjectComponent(imageComponent);

    }

    @Override
    public List<ResourceInterface> getUsedResources() {
        List<ResourceInterface> images = new ArrayList<>();
        for (String imageString : getAttribute("image").listValue()) {
            ResourceInterface resource = ResourceManager.findResource(null, imageString, getVersion());
            if (resource != null) images.add(resource);
        }
        return images;
    }

}
