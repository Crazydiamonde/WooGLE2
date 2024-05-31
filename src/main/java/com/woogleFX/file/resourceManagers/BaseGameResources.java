package com.woogleFX.file.resourceManagers;

import com.woogleFX.engine.gui.Alarms;
import com.woogleFX.file.FileManager;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import java.util.HashSet;

public class BaseGameResources {

    public static final Set<String> GOO_BALL_TYPES = new HashSet<>();
    public static final Set<String> LEVELS = new HashSet<>();
    public static final Set<String> IMAGES = new HashSet<>();
    public static final Set<String> SOUNDS = new HashSet<>();
    public static final Set<String> PARTICLE_FX = new HashSet<>();
    public static final Set<String> TAGS = new HashSet<>(List.of(
            "ballbuster", "deadly", "detaching",
            "geomkiller", "mostlydeadly", "nodrag",
            "stopsign", "unwalkable", "walkable"
    ));


    private static void loadFileIntoSet(String file, Set<String> set) {
        try {
            String contents = Files.readString(Path.of(file));
            String[] each = contents.split("\n");
            String[] each2 = new String[each.length];
            for (int i = 0; i < each.length; i++) each2[i] = each[i].substring(0, each[i].length() - 1);
            set.addAll(List.of(each2));
        } catch (IOException e) {
            Alarms.errorMessage("Could not load " + file);
        }
    }


    public static void init() {

        String prefix = FileManager.getEditorLocation() + "\\BaseGameResources\\";

        loadFileIntoSet(prefix + "GooBallTypes.txt", GOO_BALL_TYPES);
        loadFileIntoSet(prefix + "Images.txt", IMAGES);
        loadFileIntoSet(prefix + "Levels.txt", LEVELS);
        loadFileIntoSet(prefix + "ParticleFX.txt", PARTICLE_FX);
        loadFileIntoSet(prefix + "Sounds.txt", SOUNDS);

    }


    private static String normalize(String path) {
        while (path.startsWith("./")) path = path.substring(2);
        return path;
    }

    public static boolean containsImage(String path) {
        String normalized = normalize(path);
        return IMAGES.contains(normalized) || IMAGES.contains(normalized + "@2x");
    }

    public static boolean containsSound(String path) {
        return SOUNDS.contains(normalize(path));
    }

}
