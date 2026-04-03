package com.worldOfGoo2.util;

import com.woogleFX.assets.wog2.WOG2Item.WOG2Item;
import com.woogleFX.engine.gui.alarms.ErrorAlarm;
import com.woogleFX.file.resourceManagers.ResourceManager;
import com.woogleFX.assets.GameVersion;
import com.worldOfGoo2.items._2_Item;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ItemHelper {

    public static final Map<Integer, String> itemTypeMap = new HashMap<>();
    public static final Map<String, String> itemNameMap = new HashMap<>();

    public static final Map<String, String> terrainTypeNameMap = new HashMap<>();

    // Hardcoded item types
    static {
        //itemTypeMap.put(0, "Invalid");
        itemTypeMap.put(1, "Generic");
        itemTypeMap.put(2, "PipeInLiquid");
        itemTypeMap.put(3, "Connection");
        itemTypeMap.put(4, "PipeInBalls");
        itemTypeMap.put(5, "Tilable");
        itemTypeMap.put(6, "GodRayArea");
        itemTypeMap.put(7, "LinearForceField");
        itemTypeMap.put(8, "RadialForceField");
        itemTypeMap.put(9, "Pool");
        itemTypeMap.put(10, "RangeEndCriteria");
        itemTypeMap.put(11, "Torch");
        itemTypeMap.put(12, "TerrainAttrib");
        itemTypeMap.put(13, "Water");
        itemTypeMap.put(14, "Swing");
        itemTypeMap.put(15, "Fireball");
        itemTypeMap.put(16, "Gear");
        itemTypeMap.put(17, "Debris");
        itemTypeMap.put(18, "Rope");
        itemTypeMap.put(19, "FireworksBoat");
        itemTypeMap.put(20, "JellyCubesPipe");
        itemTypeMap.put(21, "Piston");
        itemTypeMap.put(22, "Decoration");
        itemTypeMap.put(23, "LevelExit");
        itemTypeMap.put(24, "TimedFade");
        itemTypeMap.put(25, "SignPainterSign");
        itemTypeMap.put(26, "TerrainHide");
        itemTypeMap.put(27, "TimedAnimation");
        itemTypeMap.put(28, "SelectAnimation");
        itemTypeMap.put(29, "Button");
        itemTypeMap.put(30, "Drain");
        itemTypeMap.put(31, "Winch");
        itemTypeMap.put(32, "Squiddy");
        itemTypeMap.put(33, "PinToJelly");
        itemTypeMap.put(34, "BallDebris");
        itemTypeMap.put(35, "RailConstraint");
        itemTypeMap.put(36, "SatelliteDish");
        itemTypeMap.put(38, "CameraControl");
        itemTypeMap.put(39, "CameraEOL");
        itemTypeMap.put(40, "Island2");
        itemTypeMap.put(41, "ParticleEffectInLevel");
        itemTypeMap.put(42, "Eye");
        itemTypeMap.put(43, "AmbientSoundArea");
        itemTypeMap.put(44, "AutoBoundsArea");
        itemTypeMap.put(45, "Rocket");
        itemTypeMap.put(46, "LevelText");
        itemTypeMap.put(47, "Tentacle");
        itemTypeMap.put(48, "Overlay");
        itemTypeMap.put(49, "OCDFlag");
        itemTypeMap.put(50, "AnglerHook");
    }


    public static String getItemActualName(String itemUUID) {

        if (itemNameMap.containsKey(itemUUID)) return itemNameMap.get(itemUUID);

        _2_Item item;
        try {
            WOG2Item _2item = WOG2Item.assetSelector.openInstance(itemUUID, GameVersion.VERSION_WOG2);
            if (_2item == null) {
                return "(None)";
            }
            item = _2item.getItem();
        } catch (IOException e) {
            ErrorAlarm.show(e); // ugh
            item = null;
        }
        if (item == null) return "(None)";
        String actualName = item.getAttribute("name").stringValue();
        itemNameMap.put(itemUUID, actualName);
        return actualName;

    }


    public static String getTerrainTypeActualName(String itemUUID) {

        if (terrainTypeNameMap.containsKey(itemUUID)) return terrainTypeNameMap.get(itemUUID);

        try {
            ResourceManager.findTerrainTypes(null, GameVersion.VERSION_WOG2);
            return terrainTypeNameMap.getOrDefault(itemUUID, "(None)");
        } catch (FileNotFoundException ignored) {
            return "";
        }

    }
}
