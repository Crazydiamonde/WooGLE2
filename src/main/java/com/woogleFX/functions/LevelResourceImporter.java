package com.woogleFX.functions;

import com.woogleFX.editorObjects.EditorObject;
import com.woogleFX.editorObjects.objectCreators.ObjectAdder;
import com.woogleFX.editorObjects.objectCreators.ObjectCreator;
import com.woogleFX.engine.fx.FXHierarchy;
import com.woogleFX.engine.fx.FXStage;
import com.woogleFX.engine.gui.Alarms;
import com.woogleFX.file.BaseGameResources;
import com.woogleFX.file.FileManager;
import com.woogleFX.functions.undoHandling.UndoManager;
import com.woogleFX.functions.undoHandling.userActions.AttributeChangeAction;
import com.woogleFX.functions.undoHandling.userActions.CreateFileAction;
import com.woogleFX.functions.undoHandling.userActions.ObjectCreationAction;
import com.woogleFX.structures.GameVersion;
import com.woogleFX.structures.WorldLevel;
import com.worldOfGoo.level.Loopsound;
import com.worldOfGoo.level.Music;
import com.worldOfGoo.resrc.ResrcImage;
import com.worldOfGoo.resrc.Sound;
import javafx.stage.FileChooser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class LevelResourceImporter {

    private static final Logger logger = LoggerFactory.getLogger(LevelResourceImporter.class);

    /** Modifies an image path to match those in the game. */
    private static String cleanImagePath(String path, GameVersion version) {

        path = path.replace("\\", "/");
        if (path.startsWith("/")) path = path.substring(1);
        if (path.endsWith(".png")) path = path.substring(0, path.length() - 4);

        if (version == GameVersion.NEW && path.endsWith("@2x")) {
            // Strip @2x suffix, since this is handled transparently by the game already
            path = path.substring(0, path.length() - 3);
        }

        return path;

    }


    private static void addResourceObjectToLevel(WorldLevel level, EditorObject object, String id, String path) {

        assert object != null;
        object.setAttribute("id", id);
        object.setAttribute("path", path);
        level.getResrc().add(object);

        int whereToPlaceResource = 0;
        int count = 0;
        for (EditorObject editorObject : level.getResrcObject().getChildren().get(0).getChildren()) {
            count++;
            if (editorObject instanceof ResrcImage) {
                whereToPlaceResource = count;
            }
        }

        object.setParent(level.getResrcObject().getChildren().get(0), whereToPlaceResource);

        FXHierarchy.getHierarchy().refresh();

    }


    public static void importImages(WorldLevel level) {
        FileChooser fileChooser = new FileChooser();
        String wogDir = level.getVersion() == GameVersion.OLD ? FileManager.getOldWOGdir() : FileManager.getNewWOGdir();
        fileChooser.setInitialDirectory(new File(wogDir + "\\res\\images\\"));
        List<File> resrcFiles = fileChooser.showOpenMultipleDialog(FXStage.getStage());

        if (resrcFiles != null && !resrcFiles.isEmpty()) {
            for (File resrcFile : resrcFiles) {
                try {
                    LevelResourceImporter.importImage(level, resrcFile);
                } catch (IOException e) {
                    logger.error("", e);
                }
            }
        }

    }


    /** Imports the given image file into the given level. */
    public static void importImage(WorldLevel level, File resrcFile) throws IOException {

        BufferedImage image;
        try {
            InputStream in = new FileInputStream(resrcFile);
            image = ImageIO.read(in);
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        String normalizedFilename = resrcFile.getName().split("\\.")[0].replace(' ', '_');

        String dir = level.getVersion() == GameVersion.OLD ? FileManager.getOldWOGdir() : FileManager.getNewWOGdir();
        String toLevel = "res\\levels\\" + level.getLevelName();
        String pathBase = dir + "\\" + toLevel;

        // If a file with this name already exists, rename it by adding the smallest available number to its name.
        if (new File(pathBase + "\\" + normalizedFilename + ".png").exists()) {
            int i = 1;
            while (new File(pathBase + "\\" + normalizedFilename + "_" + i + ".png").exists()) i++;
            normalizedFilename += "_" + i;
        }

        String imgPath = resrcFile.getPath().replace(dir, "");

        String path;

        String baseGameEquivalentPath = cleanImagePath(imgPath, level.getVersion());
        if (BaseGameResources.containsImage(baseGameEquivalentPath)) {
            path = baseGameEquivalentPath;
            if (level.getVersion() == GameVersion.NEW && normalizedFilename.endsWith("@2x")) {
                // Strip @2x suffix from here too
                normalizedFilename = normalizedFilename.substring(0, normalizedFilename.length() - 3);
            }
        } else {
            // If this resource isn't from the base game, save the actual image file in the level folder.
            OutputStream outputStream = new FileOutputStream(pathBase + "\\" + normalizedFilename + ".png");
            ImageIO.write(image, "png", outputStream);
            outputStream.close();
            path = toLevel + "\\" + normalizedFilename;
        }

        String id = "IMAGE_SCENE_" + level.getLevelName().toUpperCase() + "_" + normalizedFilename.toUpperCase();

        EditorObject imageResourceObject = ObjectCreator.create("Image", null);
        assert imageResourceObject != null;
        addResourceObjectToLevel(level, imageResourceObject, id, path);

        // Add a new SceneLayer with this image.
        EditorObject sceneLayer = ObjectAdder.addObject("SceneLayer");
        sceneLayer.setAttribute("image", id);

        UndoManager.registerChange(
                new ObjectCreationAction(sceneLayer, sceneLayer.getParent().getChildren().indexOf(sceneLayer)),
                new ObjectCreationAction(imageResourceObject, imageResourceObject.getParent().getChildren().indexOf(imageResourceObject)),
                new CreateFileAction(dir + "\\" + path + ".png", Files.readAllBytes(resrcFile.toPath()))
        );

    }


    public static void importMusic(WorldLevel level) {
        FileChooser fileChooser = new FileChooser();
        String wogDir = level.getVersion() == GameVersion.OLD ? FileManager.getOldWOGdir() : FileManager.getNewWOGdir();
        fileChooser.setInitialDirectory(new File(wogDir + "\\res\\music"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("OGG sound file", "*.ogg"));

        File resrcFile = fileChooser.showOpenDialog(FXStage.getStage());

        if (resrcFile != null) importMusic(level, resrcFile, true);
    }


    public static void importMusic(WorldLevel level, File resrcFile, boolean fromUser) {

         // If resrcFile is not already present in res\music, copy resrcFile into res\music.

        String dir = level.getVersion() == GameVersion.OLD ? FileManager.getOldWOGdir() : FileManager.getNewWOGdir();

        /* copy file */
        String normalizedFilename = resrcFile.getName().split("\\.")[0].replace(' ', '_');
        String soundPath = "res/levels/" + level.getLevelName() + "/" + normalizedFilename;
        if (!new File(dir + "\\res\\music\\" + resrcFile.getName()).exists()) {
            try {
                Files.copy(resrcFile.toPath(), Paths.get(dir + "\\res\\music\\" + resrcFile.getName()));
            } catch (IOException e) {
                Alarms.errorMessage(e);
            }
        } else {
            soundPath = "res/music/" + normalizedFilename;
        }

        // Add a new sound resource with a default ID and path leading to resrcFile in res\music.
        String soundResourceName = "SOUND_LEVEL_" + level.getLevelName().toUpperCase() + "_" + normalizedFilename.toUpperCase();
        EditorObject soundResourceObject = ObjectCreator.create("Sound", null);
        assert soundResourceObject != null;
        soundResourceObject.setAttribute("id", soundResourceName);
        soundResourceObject.setAttribute("path", soundPath);

        int whereToPlaceResource = 0;
        int count = 0;
        for (EditorObject resourceThing : level.getResrcObject().getChildren().get(0).getChildren()) {
            count++;
            if (resourceThing instanceof Sound) {
                whereToPlaceResource = count;
            }
        }

        level.getResrc().add(soundResourceObject);
        soundResourceObject.setParent(level.getResrcObject().getChildren().get(0), whereToPlaceResource);

        /* If a music object already exists, change its sound attribute. */
        for (EditorObject music : level.getLevel()) {
            if (music instanceof Music) {
                String oldID = music.getAttribute("id").stringValue();
                music.setAttribute("id", soundResourceName);
                try {
                    UndoManager.registerChange(new CreateFileAction(resrcFile.getPath(), Files.readAllBytes(resrcFile.toPath())),
                            new AttributeChangeAction(music.getAttribute("id"), oldID, soundResourceName));
                } catch (IOException ignored) {

                }
                return;
            }
        }

        /* Otherwise, create a new music object set to the sound resource's ID. */
        EditorObject musicObject = ObjectCreator.create("music", level.getLevelObject());
        musicObject.setAttribute("id", soundResourceName);
        level.getLevel().add(musicObject);
        try {
            UndoManager.registerChange(new CreateFileAction(resrcFile.getPath(), Files.readAllBytes(resrcFile.toPath())),
                    new ObjectCreationAction(soundResourceObject, whereToPlaceResource),
                    new ObjectCreationAction(musicObject, level.getLevel().size() - 1));
        } catch (IOException ignored) {

        }

    }


    public static void importLoopsound(WorldLevel level) {
        FileChooser fileChooser = new FileChooser();
        String wogDir = level.getVersion() == GameVersion.OLD ? FileManager.getOldWOGdir() : FileManager.getNewWOGdir();
        fileChooser.setInitialDirectory(new File(wogDir + "\\res\\sounds"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("OGG sound file", "*.ogg"));

        File resrcFile = fileChooser.showOpenDialog(FXStage.getStage());

        if (resrcFile != null) {
            importLoopsound(level, resrcFile, true);
        }
    }


    public static void importLoopsound(WorldLevel level, File resrcFile, boolean fromUser) {

        // If resrcFile is not already present in res\music, copy resrcFile into res\sounds.

        String dir = level.getVersion() == GameVersion.OLD ? FileManager.getOldWOGdir() : FileManager.getNewWOGdir();

        /* copy file */
        String normalizedFilename = resrcFile.getName().split("\\.")[0].replace(' ', '_');
        String soundPath = "res/levels/" + level.getLevelName() + "/" + normalizedFilename;
        if (!new File(dir + "\\res\\sounds\\" + resrcFile.getName()).exists()) {
            try {
                Files.copy(resrcFile.toPath(), Paths.get(dir + "\\res\\sounds\\" + resrcFile.getName()));
            } catch (Exception e) {
                Alarms.errorMessage(e);
            }
        } else {
            soundPath = "res/sounds/" + normalizedFilename;
        }

        // Add a new sound resource with a default ID and path leading to resrcFile inres\sounds.
        String soundResourceName = "SOUND_LEVEL_" + level.getLevelName().toUpperCase() + "_" + normalizedFilename.toUpperCase();
        EditorObject soundResourceObject = ObjectCreator.create("Sound", null);
        assert soundResourceObject != null;
        soundResourceObject.setAttribute("id", soundResourceName);
        soundResourceObject.setAttribute("path", soundPath);

        int whereToPlaceResource = 0;
        int count = 0;
        for (EditorObject resourceThing : level.getResrcObject().getChildren().get(0).getChildren()) {
            count++;
            if (resourceThing instanceof Sound) {
                whereToPlaceResource = count;
            }
        }

        level.getResrc().add(soundResourceObject);
        soundResourceObject.setParent(level.getResrcObject().getChildren().get(0), whereToPlaceResource);

        /* If a music object already exists, change its sound attribute. */
        for (EditorObject music : level.getLevel()) {
            if (music instanceof Loopsound) {
                String oldID = music.getAttribute("id").stringValue();
                music.setAttribute("id", soundResourceName);
                try {
                    UndoManager.registerChange(new CreateFileAction(resrcFile.getPath(), Files.readAllBytes(resrcFile.toPath())),
                            new AttributeChangeAction(music.getAttribute("id"), oldID, soundResourceName));
                } catch (IOException ignored) {

                }
                return;
            }
        }

        /* Otherwise, create a new music object set to the sound resource's ID. */
        EditorObject musicObject = ObjectCreator.create("loopsound", level.getLevelObject());
        musicObject.setAttribute("id", soundResourceName);
        level.getLevel().add(musicObject);
        try {
            UndoManager.registerChange(new CreateFileAction(resrcFile.getPath(), Files.readAllBytes(resrcFile.toPath())),
                    new ObjectCreationAction(soundResourceObject, whereToPlaceResource),
                    new ObjectCreationAction(musicObject, level.getLevel().size() - 1));
        } catch (IOException ignored) {

        }

    }

}
