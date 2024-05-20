package com.WooGLEFX.File.export;

import com.WooGLEFX.EditorObjects._Ball;
import com.WooGLEFX.File.BaseGameResources;
import com.WooGLEFX.File.FileManager;
import com.WooGLEFX.GUI.Alarms;
import com.WooGLEFX.Structures.EditorObject;
import com.WooGLEFX.Structures.GameVersion;
import com.WooGLEFX.Structures.WorldLevel;
import com.WorldOfGoo.Resrc.ResrcImage;
import com.WorldOfGoo.Resrc.Sound;
import javafx.embed.swing.SwingFXUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class GoomodExporter {

    public static void exportGoomod(File file, WorldLevel level, ArrayList<_Ball> balls, boolean includeAddinInfo) {
        String start = level.getVersion() == GameVersion.OLD ? FileManager.getOldWOGdir() : FileManager.getNewWOGdir();
        // First things first, delete the goomod folder if it exists
        if (Files.exists(Path.of(start + "\\res\\levels\\" + level.getLevelName() + "\\goomod"))) {
            try {
                Files.walk(Path.of(start + "\\res\\levels\\" + level.getLevelName() + "\\goomod"))
                        .sorted(java.util.Comparator.reverseOrder())
                        .map(Path::toFile)
                        .forEach(File::delete);
            } catch (Exception e) {
                Alarms.errorMessage(e);
            }
        }
        // Delete the goomod zip if it exists
        if (Files.exists(Path.of(start + "\\res\\levels\\" + level.getLevelName() + "\\goomod.zip"))) {
            try {
                Files.delete(Path.of(start + "\\res\\levels\\" + level.getLevelName() + "\\goomod.zip"));
            } catch (Exception e) {
                Alarms.errorMessage(e);
            }
        }
        try {
            LevelWriter.saveAsXML(level, start + "\\res\\levels\\" + level.getLevelName() + "\\goomod\\compile\\res\\levels\\" + level.getLevelName(), level.getVersion(), true, includeAddinInfo);
        } catch (Exception e) {
            Alarms.errorMessage(e);
        }
        for (EditorObject resource : level.getResources()) {
            String resourceNameStripped = resource.getAttribute("path").stringValue();
            if (resourceNameStripped != null && resourceNameStripped.contains("/")) {
                resourceNameStripped = resourceNameStripped.substring(0, resourceNameStripped.lastIndexOf("/"));
            }
            if (resource instanceof ResrcImage) {
                if (BaseGameResources.containsImage(resource.getAttribute("path").stringValue())) {
                    // Skip if base game image
                    continue;
                }
                if (!Files.exists(Path.of(start + "\\res\\levels\\" + level.getLevelName() + "\\goomod\\override\\" + resourceNameStripped))) {
                    try {
                        Files.createDirectories(Path.of(start + "\\res\\levels\\" + level.getLevelName() + "\\goomod\\override\\" + resourceNameStripped));
                    } catch (Exception e) {
                        Alarms.errorMessage(e);
                    }
                }
                try {
                    BufferedImage oldImage = SwingFXUtils.fromFXImage(resource.getAttribute("id").imageValue(level.getVersion()), null);
                    File newImageFile = new File(start + "\\res\\levels\\" + level.getLevelName() + "\\goomod\\override\\" + resource.getAttribute("path") + ".png");
                    ImageIO.write(oldImage, "png", newImageFile);
                } catch (Exception e) {
                    Alarms.errorMessage(e);
                }
            } else if (resource instanceof Sound) {
                if (BaseGameResources.containsSound(resource.getAttribute("path").stringValue())) {
                    // Skip if base game sound
                    continue;
                }
                if (!Files.exists(Path.of(start + "\\res\\levels\\" + level.getLevelName() + "\\goomod\\override\\" + resourceNameStripped))) {
                    try {
                        Files.createDirectories(Path.of(start + "\\res\\levels\\" + level.getLevelName() + "\\goomod\\override\\" + resourceNameStripped));
                    } catch (Exception e) {
                        Alarms.errorMessage(e);
                    }
                }
                try {
                    Files.copy(
                            Path.of(start + "\\" + resource.getAttribute("path") + ".ogg"),
                            Path.of(start + "\\res\\levels\\" + level.getLevelName() + "\\goomod\\override\\" + resource.getAttribute("path") + ".ogg"),
                            StandardCopyOption.REPLACE_EXISTING
                    );
                } catch (Exception e) {
                    Alarms.errorMessage(e);
                }
            }
        }

        // Keep track of exported balls
        HashSet<String> exportedBalls = new HashSet<>();
        for (_Ball ball : balls) {
            try {
                String ballName = ball.getObjects().get(0).getAttribute("name").stringValue();
                if (exportedBalls.contains(ballName)) {
                    // Skip if already exported
                    continue;
                }
                if (BaseGameResources.GOO_BALL_TYPES.contains(ballName)) {
                    // Skip if base game ball
                    continue;
                }
                BallWriter.exportBallAsXML(ball, start + "\\res\\levels\\" + level.getLevelName() + "\\goomod\\compile\\res\\balls\\" + ballName, level.getVersion(), true);
                for (EditorObject resource : ball.getResources()) {
                    if (resource instanceof ResrcImage) {
                        String realpath = resource.getAttribute("path").stringValue();
                        if (BaseGameResources.containsImage(realpath)) {
                            // Skip if base game image
                            continue;
                        }
                        // Create subfolder if required
                        if (realpath.contains("/")) {
                            realpath = realpath.substring(0, realpath.lastIndexOf("/"));
                        }
                        Path subfolder = Path.of(start + "\\res\\levels\\" + level.getLevelName() + "\\goomod\\override\\" + realpath);
                        if (!Files.exists(subfolder)) {
                            Files.createDirectories(subfolder);
                        }
                        BufferedImage oldImage = SwingFXUtils.fromFXImage(resource.getAttribute("id").imageValue(level.getVersion()), null);
                        File newImageFile = new File(start + "\\res\\levels\\" + level.getLevelName() + "\\goomod\\override\\" + resource.getAttribute("path") + ".png");
                        ImageIO.write(oldImage, "png", newImageFile);
                    }
                }
                exportedBalls.add(ballName);
            } catch (Exception e) {
                Alarms.errorMessage(e);
            }
        }

        try {
            new ZipUtility().zip(new ArrayList<>(List.of(new File(start + "\\res\\levels\\" + level.getLevelName() + "\\goomod").listFiles())), start + "\\res\\levels\\" + level.getLevelName() + "\\goomod.zip");
            File srcGoomod = new File(start + "\\res\\levels\\" + level.getLevelName() + "\\goomod.zip");
            Files.move(
                    srcGoomod.toPath(),
                    file.toPath(),
                    StandardCopyOption.REPLACE_EXISTING
            );
        } catch (Exception e) {
            Alarms.errorMessage(e);
        }
    }

}
