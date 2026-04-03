package com.worldOfGoo2.level;

import com.woogleFX.assets.wog2.WOG2TerrainType.WOG2TerrainTypeOpener;
import com.woogleFX.editorObjects.EditorObject;
import com.woogleFX.assets.Asset;
import com.woogleFX.assets.wog2.WOG2TerrainType.WOG2TerrainType;
import com.woogleFX.editorObjects.attributes.AttributeAdapter;
import com.woogleFX.editorObjects.attributes.EditorAttribute;
import com.woogleFX.editorObjects.attributes.InputField;
import com.woogleFX.editorObjects.objectComponents.TerrainMeshComponent;
import com.woogleFX.engine.AssetManager;
import com.woogleFX.engine.fx.propertiesView.FXPropertiesView;
import com.woogleFX.assets.GameVersion;
import com.woogleFX.assets.wog2.WOG2Level.WOG2Level;
import com.woogleFX.engine.gui.alarms.ErrorAlarm;
import com.woogleFX.file.resourceManagers.ResourceManager;
import com.worldOfGoo2.terrain._2_Terrain_TerrainType;
import com.worldOfGoo2.util.ItemHelper;
import javafx.scene.image.Image;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class _2_Level_TerrainGroup extends EditorObject {

    /** A list of all the BallInstances that have this terrain group.
     * This should always reflect the current state of the level. */
    private final ArrayList<_2_Level_BallInstance> balls = new ArrayList<>();
    public void addBall(_2_Level_BallInstance ballInstance) {
        balls.add(ballInstance);
    }
    public void removeBall(_2_Level_BallInstance ballInstance) {
        balls.remove(ballInstance);
    }

    
    public _2_Level_TerrainGroup(EditorObject parent, GameVersion version) {
        super(parent, version);

        addAttributeAdapter("textureOffset", AttributeAdapter.pointAttributeAdapter(this, "textureOffset", "textureOffset"));

        addAttributeAdapter("typeUuid", new AttributeAdapter("type") {

            private final EditorAttribute temp = new EditorAttribute("type", InputField._2_TERRAIN_GROUP_TYPE, _2_Level_TerrainGroup.this).assertRequired();

            @Override
            public EditorAttribute getValue() {

                if (getAttribute2("typeUuid").stringValue().isEmpty()) return temp;
                temp.setValue(ItemHelper.getTerrainTypeActualName(getAttribute2("typeUuid").stringValue()));
                return temp;

            }

            @Override
            public void setValue(String value) {
                _2_Terrain_TerrainType terrainType;
                try {
                    terrainType = WOG2TerrainType.assetSelector.openInstance(value, getVersion()).getTerrainType();
                    if (terrainType == null) return;
                } catch (IOException e) {
                    ErrorAlarm.show(e);
                    return;
                }
                temp.setValue(value);
                setAttribute2("typeUuid", terrainType.getAttribute("uuid").stringValue());
                if (Arrays.stream(getObjectComponents()).anyMatch(e -> AssetManager.getAsset().isSelected(e))) {
                    FXPropertiesView.changeTableView(AssetManager.getAsset().getSelectedObjects());
                }
                update();
            }

        });

    }

    @Override
    public String getName() {
        if (AssetManager.getAsset() instanceof WOG2Level level) {
            for (int i = 0; i < level.getLevel().getChildren("terrainGroups").size(); i++) {
                if (level.getLevel().getChildren("terrainGroups").get(i) == this) {
                    return i + ", " + this.getAttribute("type").stringValue();
                }
            }
        }
        return "";
    }

    @Override
    public void onLoaded(Asset asset) {
        super.onLoaded(asset);

        EditorObject textureOffset = getChildren("textureOffset").get(0);
        textureOffset.getAttribute("x").addChangeListener((observable, oldValue, newValue) ->
                setAttribute2("textureOffset", newValue + "," + getAttribute2("textureOffset").positionValue().getY()));
        textureOffset.getAttribute("y").addChangeListener((observable, oldValue, newValue) ->
                setAttribute2("textureOffset", getAttribute2("textureOffset").positionValue().getX() + "," + newValue));
        setAttribute2("textureOffset", textureOffset.getAttribute("x").stringValue() + "," + textureOffset.getAttribute("y").stringValue());

        getAttribute("type").addChangeListener((observable, oldValue, newValue) -> update());

    }

    private boolean ignoreUpdates = true;
    public void stopIgnoringUpdates() {
        ignoreUpdates = false;
    }


    private Image image = null;

    @Override
    public void update() {

        if (ignoreUpdates) return;

        balls.clear();

        int thisIndex = ((WOG2Level)AssetManager.getAsset()).getLevel().getChildren("terrainGroups").indexOf(this);
        for (EditorObject ball : ((WOG2Level)AssetManager.getAsset()).getLevel().getChildren("balls")) {
            if (ball.getAttribute("terrainGroup").intValue() == thisIndex) {
                addBall((_2_Level_BallInstance) ball);
                ((_2_Level_BallInstance) ball).setCurrentGroup(this);
            }
        }

        clearObjectComponents();

        try {
            if (WOG2TerrainType.assetSelector.openInstance(getAttribute("typeUuid").stringValue(), getVersion()) != null)
                addObjectComponent(new TerrainMeshComponent(this, getStrands(), balls.toArray(_2_Level_BallInstance[]::new)) {
                    // TODO2: put stuff inside
                    @Override
                    public Image getImage() {
                        return image;
                    }
                });
        } catch (IOException ignored) {

        }

        image = ResourceManager.getImage(null, WOG2TerrainTypeOpener.openTerrainType(getAttribute("typeUuid").stringValue(), GameVersion.VERSION_WOG2).getTerrainType().getChild("baseSettings").getChild("image").getAttribute("imageId").stringValue(), GameVersion.VERSION_WOG2);

    }

    private Strand[] getStrands() {
        ArrayList<Strand> strands = new ArrayList<>();

        WOG2Level level = ((WOG2Level) AssetManager.getAsset());
        for (EditorObject object : level.getLevel().getChildren("strands")) {
            Strand strand = (Strand)object;
            _2_Level_BallInstance ballInstance = strand.getGoo1();
            
            if (ballInstance != null && ballInstance.getCurrentGroup() == _2_Level_TerrainGroup.this && ballInstance.containsStrand(strand)) {
                strands.add(strand);
            }
        }
        
        return strands.toArray(Strand[]::new);
    }
}
