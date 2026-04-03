package com.worldOfGoo2.level;

import com.woogleFX.assets.Asset;
import com.woogleFX.assets.wog2.WOG2Level.WOG2Level;
import com.woogleFX.editorObjects.EditorObject;
import com.woogleFX.assets.GameVersion;

public class TerrainBall extends EditorObject {

    public TerrainBall(EditorObject parent, GameVersion version) {
        super(parent, version);
    }

    @Override
    public void onLoaded(Asset asset) {
        super.onLoaded(asset);

        int terrainGroup = getAttribute("group").intValue();

        if (terrainGroup != -1) {
            // ((WOG2Level)getAsset()).getLevel().getChildren("terrainGroups").get(terrainGroup).update();
        }

    }
}
