package com.woogleFX.file.fileExport;

import com.woogleFX.editorObjects.EditorObject;
import com.woogleFX.assets.Asset;
import com.woogleFX.assets.wog1.ball.WOG1Ball;
import com.woogleFX.assets.GameVersion;
import com.woogleFX.engine.gui.alarms.ErrorAlarm;
import com.woogleFX.file.FileManager;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class GoomodExporter {

    public static void export(Asset asset, boolean includeAddinInfo) {

        FileChooser whereToExport = new FileChooser();
        whereToExport.setInitialDirectory(asset.getFile());
        whereToExport.setInitialFileName(asset.getName() + ".goomod");
        FileChooser.ExtensionFilter goomodFilter = new FileChooser.ExtensionFilter("World of Goo mod (*.goomod)", "*.goomod");
        whereToExport.getExtensionFilters().add(goomodFilter);
        File destination = whereToExport.showSaveDialog(new Stage());

        List<File> files = new ArrayList<>();

        Stack<Asset> recursiveDependencyStack = new Stack<>();
        recursiveDependencyStack.add(asset);

        while (!recursiveDependencyStack.isEmpty()) {
            Asset dependency = recursiveDependencyStack.pop();
            files.add(dependency.getFile());
            recursiveDependencyStack.addAll(dependency.getAllCurrentDependencies());
            // TODO: give a dialog for including dependencies
        }

        try {
            new ZipUtility().zip(new File(FileManager.getGameDir(asset.getVersion())), files, destination.getPath());
        } catch (IOException e) {
            ErrorAlarm.show(e);
        }

    }


    /** Deletes any existing goomod files for a level to make room for the new ones.
     * @param levelDir The directory of the level. */
    private static void clearAlreadyExistingGoomodData(String levelDir) throws IOException {

        /*
        // Delete the goomod folder if it exists
        Path goomodFolderPath = Path.of(levelDir + "/goomod");
        if (Files.exists(goomodFolderPath)) try (Stream<Path> deleteStream = Files.walk(goomodFolderPath)) {
            // noinspection ResultOfMethodCallIgnored
            deleteStream.sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);
        }

        // Delete the goomod zip if it exists
        Path goomodZipPath = Path.of(levelDir + "/goomod.zip");
        if (Files.exists(goomodZipPath)) Files.delete(goomodZipPath);


         */
    }


    /** Exports a goomod containing the given levels and balls to the given file.
     * @param file The file at which to save the goomod.
     * @param levels The levels to put in the goomod.
     * @param balls The balls to put in the goomod.
     * @param includeAddinInfo Whether the goomod should include addin info. */
    public static void exportGoomod(File file, ArrayList<Asset> levels, ArrayList<WOG1Ball> balls,
                                    GameVersion version, boolean includeAddinInfo) throws IOException {

        /*
        String dir = FileManager.getGameDir(version);

        // Store all the data in the first provided level while making the goomod
        String levelDir = dir + "/res/levels/" + levels.get(0).getName();

        clearAlreadyExistingGoomodData(levelDir);

        for (Asset level : levels) addLevelToGoomod(level, dir, levelDir, includeAddinInfo);

        // Keep track of exported balls
        HashSet<String> exportedBalls = new HashSet<>();
        for (WOG1Ball ball : balls) {

            String ballName = ball.getBall().getAttribute("name").stringValue();
            
            // Skip if in the base game
            // TODO:
            //if (BaseGameResources.GOO_BALL_TYPES.get(version).contains(ballName)) continue;

            // Skip if already exported
            if (exportedBalls.contains(ballName)) continue;
            exportedBalls.add(ballName);

            addBallToGoomod(ball, dir, levelDir);

        }

        File[] files = new File(levelDir + "/goomod").listFiles();
        assert files != null;

        new ZipUtility().zip(new ArrayList<>(List.of(files)), levelDir + "/goomod.zip");

        File srcGoomod = new File(levelDir + "/goomod.zip");
        Files.move(srcGoomod.toPath(), file.toPath(), StandardCopyOption.REPLACE_EXISTING);


         */
    }


    private static void addResourceToGoomod(EditorObject resource, String dir, String levelDir) throws IOException {

        /*
        if (!(resource instanceof Image || resource instanceof Sound)) return;


        String notSetDefaultedPath = resource.getAttribute("path").stringValue();

        String path;
        String extension;
        if (resource instanceof Image image) {
            extension = ".png";
            path = image.getAdjustedPath();
            // Skip if base game image
            if (BaseGameResources.containsImage(notSetDefaultedPath, resource.getVersion())) return;
        } else {
            path = ((Sound)resource).getAdjustedPath();
            extension = ".ogg";
            // Skip if base game sound
            if (BaseGameResources.containsSound(notSetDefaultedPath, resource.getVersion())) return;
        }

        Path resourcePath = Path.of(levelDir + "/goomod/override/" + path);

        if (!Files.exists(resourcePath)) Files.createDirectories(resourcePath);

        Path sourcePath = Path.of(dir + "/" + path + extension);
        Path destinationPath = Path.of(levelDir + "/goomod/override/" + path + extension);
        Files.copy(sourcePath, destinationPath, StandardCopyOption.REPLACE_EXISTING);


         */
    }


    private static void addLevelToGoomod(Asset level, String dir, String levelDir,
                                         boolean includeAddinInfo) throws IOException {
/*
        String levelDirectory = levelDir + "/goomod/compile/res/levels/" + level.getName();
        LevelWriter.saveAsXML(level, levelDirectory, level.getVersion(), true, includeAddinInfo);

        // Copy resources to the goomod directory
        for (EditorObject resource : level.getResources()) addResourceToGoomod(resource, dir, levelDir);


 */
    }


    private static void addBallToGoomod(WOG1Ball ball, String dir, String levelDir) throws IOException {
/*
        String ballName = ball.getBall().getAttribute("name").stringValue();
        String ballDirectory = levelDir + "/goomod/compile/res/balls/" + ballName;
        BallWriter.saveAsXML(ball, ballDirectory, ball.getVersion(), true);

        // Copy resources to the goomod directory
        for (EditorObject resource : ball.getResources()) addResourceToGoomod(resource, dir, levelDir);


 */
    }

}
