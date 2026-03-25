package com.woogleFX.assets.wog1.particle;

import com.woogleFX.assets.AssetError;
import com.woogleFX.assets.HasBackground;
import com.woogleFX.editorObjects.EditorObject;
import com.woogleFX.assets.Asset;
import com.woogleFX.editorObjects.objectCreators.ObjectCreator;
import com.woogleFX.engine.AssetManager;
import com.woogleFX.engine.fx.FXCanvas;
import com.woogleFX.engine.fx.hierarchy.FXHierarchy;
import com.woogleFX.engine.gui.AssetSelector;
import com.woogleFX.file.FileManager;
import com.woogleFX.file.fileImport.EditorObjectXMLReader;
import com.woogleFX.file.resourceManagers.BaseGameResources;
import com.woogleFX.assets.GameVersion;
import com.worldOfGoo.particle.effects;
import com.worldOfGoo.resrc.Resources;
import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;

import java.io.File;
import java.util.*;

public class WOG1Particle extends Asset implements HasBackground {

    public WOG1Particle(GameVersion version, EditorObject particle, Resources resources) {
        super(version, resources, null);

        this.particle = particle;

        // TODO: make ambient particle effects somehow only play in the camera range

    }

    // ==========================================================================
    //  Special particle-specific things
    // ==========================================================================

    private EditorObject particle; // can change from particleeffect to ambientparticleeffect
    public EditorObject getParticle() {
        return particle;
    }
    public void setParticle(EditorObject particle) {
        this.particle = particle;
    }

    private Color backgroundColor = new Color(0.75, 0.75, 0.75, 1.0);
    @Override public Color getBackgroundColor() {
        return backgroundColor;
    }
    @Override public void setBackgroundColor(Color backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    // ==========================================================================
    //  Asset selector
    // ==========================================================================

    public static final AssetSelector<WOG1Particle> assetSelector = new AssetSelector<>("Particle") {

        @Override
        public List<String> getItems(GameVersion version) {

            List<String> items = new ArrayList<>();

            String suffix = (version == GameVersion.VERSION_WOG1_OLD) ? ".xml.bin" : ".xml";

            File effectsF = new File(FileManager.getGameDir(version) +
                    "/properties/fx" + suffix);
            effects effectsObject = EditorObjectXMLReader.readEditorObject(
                    "com.worldOfGoo.particle", version, effectsF, effects.class);
            assert effectsObject != null;

            for (EditorObject child : effectsObject.getChildren()) {
                items.add(child.getAttribute("name").stringValue());
            }

            return items;
        }

        @Override
        public boolean isOriginal(String item, GameVersion version) {
            return BaseGameResources.PARTICLE_FX.get(version).contains(item);
        }

        @Override
        protected WOG1Particle secretNewInstance(String name, GameVersion version) {
            com.worldOfGoo.particle.particleeffect particle = ObjectCreator.create(com.worldOfGoo.particle.particleeffect.class, null, version);
            Resources resources = ObjectCreator.create(Resources.class, null, version);
            WOG1Particle particle1 = new WOG1Particle(version, particle, resources);
            particle1.setName(name);
            return particle1;
        }

        @Override
        protected WOG1Particle secretOpenInstance(File file, String name, GameVersion version) {
            return WOG1ParticleOpener.openParticle(file, name, version);
        }

        @Override
        protected FileChooser.ExtensionFilter getCustomExtensionFilter(GameVersion version) {
            // this can never happen?
            // TODO: add support for custom fx.xmls, just in case
            return null;
        }

        @Override
        protected File getDefaultFileForName(String name, GameVersion version) {
            String suffix = (version == GameVersion.VERSION_WOG1_OLD) ? ".xml.bin" : ".xml";
            return new File(FileManager.getGameDir(version) + "/properties/fx" + suffix);
        }

        @Override
        protected String getNameFromFile(File file, GameVersion version) {
            // impossible!!
            return null;
        }

    };

    // ==========================================================================
    //  Common asset functions
    // ==========================================================================

    @Override
    public EditorObject getDefaultParent(Class<? extends EditorObject> toAdd) {
        String pckg = toAdd.getPackage().getName();
        if (pckg.endsWith("particle")) return particle;
        else if (pckg.endsWith("resrc")) return getResources();
        else return null;
    }

    @Override
    public ArrayList<Node> getGUIElements() {
        return WOG1ParticleGUI.getGUIElements();
    }

    @Override
    public List<EditorObject> getObjects() {
        List<EditorObject> objects = new ArrayList<>();
        particle.addAllChildren(objects);
        return Collections.unmodifiableList(objects);
    }

    @Override
    public boolean isBaseGame() {
        return BaseGameResources.PARTICLE_FX.get(getVersion()).contains(getName());
    }

    @Override
    public boolean isScaleTooFar(double scaleX, double scaleY) {
        return scaleX > 10 || scaleY > 10 || scaleX < 0.1 || scaleY < 0.1;
    }

    @Override
    public void resetCamera() {
        setOffsetX(FXCanvas.getCanvas().getWidth() / 2);
        setOffsetY(FXCanvas.getCanvas().getHeight() / 2);
        setZoom(1);
    }

    @Override
    public List<AssetError> verify() {
        return new ArrayList<>();
    }

    // ==========================================================================
    //  Asset management
    // ==========================================================================

    @Override
    public boolean save(File file) {
        return WOG1ParticleWriter.saveParticleAsXML(this, file.getPath(), getVersion());
    }

    @Override
    public void load() {
        super.load();
        for (EditorObject object : getObjects()) object.onLoaded(this);
    }

    @Override
    public void export(boolean includeAddinInfo) {
        // TODO: export WOG1Particle
    }

    @Override
    public void delete() {
        // TODO: delete WOG1Particle
    }

    @Override
    public Asset clone(String name) {
        // TODO: clone WOG1Particle
        return null;
    }

    // ==========================================================================
    //  Tabs
    // ==========================================================================

    private final Tab particleTab = new Tab("Particle");
    private final Tab resourcesTab = new Tab("Resources");

    @Override
    public List<Tab> getTabs() {
        return List.of(particleTab, resourcesTab);
    }

    @Override
    public void onSetTab(Tab tab) {
        if (this != AssetManager.getAsset()) return;

        EditorObject rootObject;
        if (tab == particleTab) rootObject = particle;
        else rootObject = getResources();

        assert rootObject != null;

        FXHierarchy.getHierarchy().setRoot(rootObject.getTreeItem());
        FXHierarchy.getHierarchy().refresh();
        FXHierarchy.getHierarchy().getRoot().setExpanded(true);
        FXHierarchy.getHierarchy().setShowRoot(true);
    }

    @Override
    public Tab getTabForObject(EditorObject editorObject) {
        String pckg = editorObject.getClass().getPackage().getName();
        if (pckg.endsWith("particle")) return particleTab;
        else if (pckg.endsWith("resrc")) return resourcesTab;
        else return null;
    }

}
