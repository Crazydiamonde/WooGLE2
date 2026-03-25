package com.worldOfGoo.particle;

import com.woogleFX.assets.wog1.particle.WOG1Particle;
import com.woogleFX.editorObjects.EditorObject;
import com.woogleFX.assets.Asset;
import com.woogleFX.assets.GameVersion;
import com.woogleFX.editorObjects.objectComponents.ObjectComponent;
import com.woogleFX.editorObjects.objectCreators.ObjectCreator;
import com.woogleFX.engine.fx.assetSelectPane.FXAssetSelectPane;
import com.woogleFX.engine.fx.hierarchy.FXHierarchy;
import com.woogleFX.engine.fx.hierarchy.FXHierarchySwitcherButtons;
import com.woogleFX.engine.fx.menu.FXMenu;

import java.util.ArrayList;
import java.util.List;

public class particleeffect extends EditorObject implements ParticleSpawner {

    public particleeffect(EditorObject _parent, GameVersion version) {
        super(_parent, version);
    }

    @Override
    public void onLoaded(Asset asset) {
        super.onLoaded(asset);
        update();
    }

    @Override
    public void frameUpdate(double deltaTime) {
        for (EditorObject child : getChildren()) {
            if (child instanceof particle particle) {
                particle.secretFrameUpdate(deltaTime, this);
            }
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public Class<? extends EditorObject>[] getPossibleChildren() {
        return new Class[] { particle.class };
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

    private final ArrayList<ObjectComponent> particleObjectComponents = new ArrayList<>();
    @Override
    public ArrayList<ObjectComponent> getParticleObjectComponents() {
        return particleObjectComponents;
    }


    @Override
    public List<FXMenu.EditorMenuItem> getAdditionalContextMenuItems() {

        FXMenu.EditorMenuItem convertToAmbientParticleEffectItem = new FXMenu.EditorMenuItem() {
            @Override
            public void updateDisabled() {
                setDisable(false);
            }
        };
        convertToAmbientParticleEffectItem.setText("Convert to ambientparticleeffect (cannot be undone)");

        convertToAmbientParticleEffectItem.setOnAction(actionEvent -> {

            // Convert this to a particleeffect

            // Create the particleeffect
            ambientparticleeffect convertedEffect =
                    ObjectCreator.create(ambientparticleeffect.class, null, getVersion());

            convertedEffect.setAttribute("name", getAttribute("name").stringValue());
            convertedEffect.setAttribute("maxparticles", getAttribute("maxparticles").stringValue());

            for (EditorObject child : getChildren()) {
                child.setParent(convertedEffect);
            }

            convertedEffect.onLoaded(getAsset());

            // Replace this with the non-ambient one
            WOG1Particle parentAsset = (WOG1Particle) getAsset();
            parentAsset.setParticle(convertedEffect);

            // Refresh the particle asset's tabs
            parentAsset.onSetTab(FXHierarchySwitcherButtons.getHierarchySwitcherButtons().getTabs().get(0));
            FXHierarchy.getHierarchy().refresh();
            parentAsset.setSelectedLoudly(new EditorObject[]{ convertedEffect });

        });

        return List.of(convertToAmbientParticleEffectItem);

    }

}
