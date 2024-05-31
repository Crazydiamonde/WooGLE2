package com.woogleFX.file.fileExport;

import com.woogleFX.gameData.ball.BallWriter;
import com.woogleFX.gameData.ball._Ball;
import com.woogleFX.file.resourceManagers.BaseGameResources;
import com.woogleFX.file.FileManager;
import com.woogleFX.editorObjects.EditorObject;
import com.woogleFX.gameData.level.GameVersion;
import com.woogleFX.gameData.level.LevelWriter;
import com.woogleFX.gameData.level._Level;
import com.worldOfGoo.resrc.ResrcImage;
import com.worldOfGoo.resrc.Sound;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.stream.Stream;

public class GoomodExporter {

    /** Deletes any existing goomod files for a level to make room for the new ones.
     * @param levelDir The directory of the level. */
    private static void clearAlreadyExistingGoomodData(String levelDir) throws IOException {

        // Delete the goomod folder if it exists
        Path goomodFolderPath = Path.of(levelDir + "\\goomod");
        if (Files.exists(goomodFolderPath)) try (Stream<Path> deleteStream = Files.walk(goomodFolderPath)) {
            // noinspection ResultOfMethodCallIgnored
            deleteStream.sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);
        }

        // Delete the goomod zip if it exists
        Path goomodZipPath = Path.of(levelDir + "\\goomod.zip");
        if (Files.exists(goomodZipPath)) Files.delete(goomodZipPath);

    }


    /** Exports a goomod containing the given levels and balls to the given file.
     * @param file The file at which to save the goomod.
     * @param levels The levels to put in the goomod.
     * @param balls The balls to put in the goomod.
     * @param includeAddinInfo Whether the goomod should include addin info. */
    public static void exportGoomod(File file, ArrayList<_Level> levels, ArrayList<_Ball> balls,
                                    GameVersion version, boolean includeAddinInfo) throws IOException {

        String dir = FileManager.getGameDir(version);

        // Store all the data in the first provided level while making the goomod
        String levelDir = dir + "\\res\\levels\\" + levels.get(0).getLevelName();

        clearAlreadyExistingGoomodData(levelDir);

        for (_Level level : levels) addLevelToGoomod(level, dir, levelDir, includeAddinInfo);

        // Keep track of exported balls
        HashSet<String> exportedBalls = new HashSet<>();
        for (_Ball ball : balls) {

            String ballName = ball.getObjects().get(0).getAttribute("name").stringValue();
            
            // Skip if in the base game
            if (BaseGameResources.GOO_BALL_TYPES.contains(ballName)) continue;

            // Skip if already exported
            if (exportedBalls.contains(ballName)) continue;
            exportedBalls.add(ballName);

            addBallToGoomod(ball, dir, levelDir);

        }

        File[] files = new File(levelDir + "\\goomod").listFiles();
        assert files != null;

        new ZipUtility().zip(new ArrayList<>(List.of(files)), levelDir + "\\goomod.zip");

        File srcGoomod = new File(levelDir + "\\goomod.zip");
        Files.move(srcGoomod.toPath(), file.toPath(), StandardCopyOption.REPLACE_EXISTING);

    }


    private static void addResourceToGoomod(EditorObject resource, String dir, String levelDir) throws IOException {

        if (!(resource instanceof ResrcImage || resource instanceof Sound)) return;


        String notSetDefaultedPath = resource.getAttribute("path").stringValue();

        String path;
        String extension;
        if (resource instanceof ResrcImage resrcImage) {
            extension = ".png";
            path = resrcImage.getAdjustedPath();
            // Skip if base game image
            if (BaseGameResources.containsImage(notSetDefaultedPath)) return;
        } else {
            path = ((Sound)resource).getAdjustedPath();
            extension = ".ogg";
            // Skip if base game sound
            if (BaseGameResources.containsSound(notSetDefaultedPath)) return;
        }

        Path resourcePath = Path.of(levelDir + "\\goomod\\override\\" + path);

        if (!Files.exists(resourcePath)) Files.createDirectories(resourcePath);

        Path sourcePath = Path.of(dir + "\\" + path + extension);
        Path destinationPath = Path.of(levelDir + "\\goomod\\override\\" + path + extension);
        Files.copy(sourcePath, destinationPath, StandardCopyOption.REPLACE_EXISTING);

    }


    private static void addLevelToGoomod(_Level level, String dir, String levelDir,
                                         boolean includeAddinInfo) throws IOException {

        String levelDirectory = levelDir + "\\goomod\\compile\\res\\levels\\" + level.getLevelName();
        LevelWriter.saveAsXML(level, levelDirectory, level.getVersion(), true, includeAddinInfo);

        // Copy resources to the goomod directory
        for (EditorObject resource : level.getResrc()) addResourceToGoomod(resource, dir, levelDir);

    }


    private static void addBallToGoomod(_Ball ball, String dir, String levelDir) throws IOException {

        String ballName = ball.getObjects().get(0).getAttribute("name").stringValue();
        String ballDirectory = levelDir + "\\goomod\\compile\\res\\balls\\" + ballName;
        BallWriter.saveAsXML(ball, ballDirectory, ball.getVersion(), true);

        // Copy resources to the goomod directory
        for (EditorObject resource : ball.getResources()) addResourceToGoomod(resource, dir, levelDir);

    }

}
