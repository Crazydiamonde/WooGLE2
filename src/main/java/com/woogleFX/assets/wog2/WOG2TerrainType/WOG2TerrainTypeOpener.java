package com.woogleFX.assets.wog2.WOG2TerrainType;

import com.woogleFX.editorObjects.EditorObject;
import com.woogleFX.file.FileManager;
import com.woogleFX.file.fileImport.ObjectGOOParser;
import com.woogleFX.assets.GameVersion;
import com.worldOfGoo2.terrain._2_Terrain_Collection;
import com.worldOfGoo2.terrain._2_Terrain_TerrainType;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Stack;

public class WOG2TerrainTypeOpener {

    public static WOG2TerrainType openTerrainType(String name, GameVersion version) {

        File itemFile = new File(FileManager.getGameDir(GameVersion.VERSION_WOG2) + "/res/terrain/terrain.wog2");
        _2_Terrain_Collection terrainType2;
        try {
            terrainType2 = ObjectGOOParser.read(_2_Terrain_Collection.class, Files.readString(itemFile.toPath()), "terrain");
        } catch (IOException e) {
            return null;
        }
        Stack<EditorObject> stack = new Stack<>();
        stack.addAll(terrainType2.getChildren());
        while (!stack.empty()) {
            EditorObject item = stack.pop();
            if (item instanceof _2_Terrain_TerrainType terrainType1 && terrainType1.getAttribute("name").stringValue().equals(name)) {
                ArrayList<EditorObject> objects = new ArrayList<>();
                objects.add(terrainType1);
                return new WOG2TerrainType(version, objects, new ArrayList<>());
            }
            item.update();
            stack.addAll(item.getChildren());
        }
        return null;

    }

}
