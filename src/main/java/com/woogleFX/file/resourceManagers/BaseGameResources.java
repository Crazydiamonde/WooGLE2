package com.woogleFX.file.resourceManagers;

import com.woogleFX.engine.gui.alarms.ErrorAlarm;
import com.woogleFX.file.FileManager;
import com.woogleFX.assets.GameVersion;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class BaseGameResources {

    public static final Map<GameVersion, Set<String>> GOO_BALL_TYPES = new HashMap<>();
    public static final Map<GameVersion, Set<String>> ANIMATIONS = new HashMap<>();
    public static final Map<GameVersion, Set<String>> MOVIE = new HashMap<>();
    public static final Map<GameVersion, Set<String>> LEVELS = new HashMap<>();
    public static final Map<GameVersion, Set<String>> IMAGES = new HashMap<>();
    public static final Map<GameVersion, Set<String>> SOUNDS = new HashMap<>();
    public static final Map<GameVersion, Set<String>> PARTICLE_FX = new HashMap<>();
    public static final Set<String> TAGS = new HashSet<>(List.of(
            "ballbuster", "deadly", "detaching",
            "geomkiller", "mostlydeadly", "nodrag",
            "stopsign", "unwalkable", "walkable"
    ));


    public static void loadFileIntoSet(String file, Set<String> set) {
        try {
            String contents = Files.readString(Path.of(file));
            if (contents.isEmpty()) return;
            String[] each = contents.split("\n");
            String[] each2 = new String[each.length];
            for (int i = 0; i < each.length; i++) each2[i] = each[i].substring(0, each[i].length() - 1);
            set.addAll(List.of(each2));
        } catch (IOException e) {
            ErrorAlarm.show("Could not load " + file);
        }
    }


    public static void init() {

        for (GameVersion gameVersion : GameVersion.values()) {

            String prefix = FileManager.getEditorLocation() + "/BaseGameResources/";

            GOO_BALL_TYPES.put(gameVersion, new HashSet<>());
            ANIMATIONS.put(gameVersion, new HashSet<>());
            IMAGES.put(gameVersion, new HashSet<>());
            LEVELS.put(gameVersion, new HashSet<>());
            MOVIE.put(gameVersion, new HashSet<>());
            PARTICLE_FX.put(gameVersion, new HashSet<>());
            SOUNDS.put(gameVersion, new HashSet<>());

            loadFileIntoSet(prefix + gameVersion + "/GooBallTypes.txt", GOO_BALL_TYPES.get(gameVersion));
            loadFileIntoSet(prefix + gameVersion + "/Animations.txt", ANIMATIONS.get(gameVersion));
            loadFileIntoSet(prefix + gameVersion + "/Images.txt", IMAGES.get(gameVersion));
            loadFileIntoSet(prefix + gameVersion + "/Levels.txt", LEVELS.get(gameVersion));
            loadFileIntoSet(prefix + gameVersion + "/Movies.txt", MOVIE.get(gameVersion));
            loadFileIntoSet(prefix + gameVersion + "/ParticleFX.txt", PARTICLE_FX.get(gameVersion));
            loadFileIntoSet(prefix + gameVersion + "/Sounds.txt", SOUNDS.get(gameVersion));

        }

    }


    private static String normalize(String path) {
        while (path.startsWith("./")) path = path.substring(2);
        return path;
    }

    public static boolean containsImage(String path, GameVersion version) {
        String normalized = normalize(path);
        return IMAGES.get(version).contains(normalized) || IMAGES.get(version).contains(normalized + "@2x");
    }

    public static boolean containsSound(String path, GameVersion version) {
        return SOUNDS.get(version).contains(normalize(path));
    }

}
