package com.woogleFX.file.resourceManagers;

import com.woogleFX.assets.wog1.level.WOG1Level;
import com.woogleFX.editorObjects.EditorObject;
import com.woogleFX.assets.Asset;
import com.woogleFX.editorObjects.objectCreators.ObjectAdder;
import com.woogleFX.editorObjects.objectCreators.ObjectCreator;
import com.woogleFX.engine.fx.hierarchy.FXHierarchy;
import com.woogleFX.engine.fx.FXStage;
import com.woogleFX.engine.gui.alarms.ErrorAlarm;
import com.woogleFX.engine.undoHandling.userActions.UserAction;
import com.woogleFX.file.FileManager;
import com.woogleFX.engine.undoHandling.UndoManager;
import com.woogleFX.engine.undoHandling.userActions.AttributeChangeAction;
import com.woogleFX.engine.undoHandling.userActions.CreateFileAction;
import com.woogleFX.engine.undoHandling.userActions.ObjectCreationAction;
import com.woogleFX.assets.GameVersion;
import com.worldOfGoo.level.loopsound;
import com.worldOfGoo.level.music;
import com.worldOfGoo.resrc.Image;
import com.worldOfGoo.resrc.Sound;
import com.worldOfGoo.scene.SceneLayer;
import javafx.stage.FileChooser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class LevelResourceImporter {

    private static final Logger logger = LoggerFactory.getLogger(LevelResourceImporter.class);

    /** Modifies an image path to match those in the game. */
    private static String cleanResourcePath(String path, GameVersion version, String extensionToRemove) {

        path = path.replace("/", "/");
        if (path.startsWith("/")) path = path.substring(1);
        if (path.endsWith(extensionToRemove)) path = path.substring(0, path.length() - 4);

        if (version == GameVersion.VERSION_WOG1_NEW && path.endsWith("@2x")) {
            // Strip @2x suffix, since this is handled transparently by the game already
            path = path.substring(0, path.length() - 3);
        }

        return path;

    }


    private static void addResourceObjectToLevel(Asset level, EditorObject object, String id, String path) {





        // If the asset has a SetDefaults with actual values,
        // create a new SetDefaults with default values
        // and put the new resource below that.
        // Otherwise, just put it at the end of the asset's resources.




        assert object != null;
        object.setAttribute("id", id);
        object.setAttribute("path", path);

        int whereToPlaceResource = 0;
        int count = 0;
        for (EditorObject EditorObject : level.getResources().getChildren()) {
            count++;
            if (EditorObject instanceof Image) {
                whereToPlaceResource = count;
            }
        }

        object.setParent(level.getResources(), whereToPlaceResource, whereToPlaceResource);

        FXHierarchy.getHierarchy().refresh();

    }


    public static void importResources(Asset asset) {
        FileChooser fileChooser = new FileChooser();
        String wogDir = FileManager.getGameDir(asset.getVersion());
        fileChooser.setInitialDirectory(new File(wogDir + "/res/images/"));
        List<File> resrcFiles = fileChooser.showOpenMultipleDialog(FXStage.getStage());
        if (resrcFiles == null) return;

        for (File resrcFile : resrcFiles) {
            try {
                importImage(asset, resrcFile);
            } catch (IOException e) {
                logger.error("", e);
            }
        }

    }


    /** Imports the given image file into the given level. */
    public static void importImage(Asset level, File resrcFile) throws IOException {

        // Read in the image.
        BufferedImage image;
        try {
            InputStream in = new FileInputStream(resrcFile);
            image = ImageIO.read(in);
            in.close();
        } catch (IOException e) {
            logger.error("", e);
            return;
        }

        String normalizedFilename = resrcFile.getName().split("/.")[0].replace(' ', '_');

        String wogDir = FileManager.getGameDir(level.getVersion());
        String pathBase = cleanResourcePath(level.getFile().getPath().substring(FileManager.getGameDir(level.getVersion()).length()), level.getVersion(), ".png");

        // If a file with this name already exists, rename it by adding the smallest available number to its name.
        if (new File(pathBase + "/" + normalizedFilename + ".png").exists()) {
            int i = 1;
            while (new File(pathBase + "/" + normalizedFilename + "_" + i + ".png").exists()) i++;
            normalizedFilename += "_" + i;
        }

        String imgPath = resrcFile.getPath().replace(wogDir, "");
        String baseGameEquivalentPath = cleanResourcePath(imgPath, level.getVersion(), ".png");

        String path;

        // Make sure the image isn't already in the base game.
        boolean needToCreate = !BaseGameResources.containsImage(baseGameEquivalentPath, level.getVersion());

        if (!needToCreate) {
            path = baseGameEquivalentPath;
            if (level.getVersion() == GameVersion.VERSION_WOG1_NEW && normalizedFilename.endsWith("@2x")) {
                // Strip @2x suffix from here too
                normalizedFilename = normalizedFilename.substring(0, normalizedFilename.length() - 3);
            }
        } else {
            // If this resource isn't from the base game, save the actual image file in the level folder.
            OutputStream outputStream = new FileOutputStream(wogDir + pathBase + "/" + normalizedFilename);
            ImageIO.write(image, "png", outputStream);
            outputStream.close();
            path = pathBase + "/" + normalizedFilename;
        }

        String id = "IMAGE_SCENE_" + level.getName().toUpperCase() + "_" +
                normalizedFilename.substring(0, normalizedFilename.lastIndexOf('.')).toUpperCase();

        // Create the actual resource object.
        EditorObject imageResourceObject = ObjectCreator.create(Image.class, null, level.getVersion());
        imageResourceObject.onLoaded(level);
        addResourceObjectToLevel(level, imageResourceObject, id, path.substring(0, path.lastIndexOf('.')));

        // If applicable, add a new SceneLayer with this image.
        if (level instanceof WOG1Level wog1Level) {
            EditorObject sceneLayer = ObjectAdder.addObject(SceneLayer.class, wog1Level.getScene(), true);
            sceneLayer.setAttribute("image", id);
        }

        // Register the creation of this SceneLayer as a user action.
        List<UserAction> userActions = new ArrayList<>();
        userActions.add(new ObjectCreationAction(imageResourceObject));
        if (needToCreate) userActions.add(new CreateFileAction(wogDir + "/" + path, resrcFile.getPath()));
        UndoManager.registerChange(userActions.toArray(UserAction[]::new));

        // Refresh to let any previously invalid objects update.
        FXHierarchy.getHierarchy().refresh();

    }


    public static void importMusic(Asset level) {
        FileChooser fileChooser = new FileChooser();
        String wogDir = FileManager.getGameDir(level.getVersion());
        fileChooser.setInitialDirectory(new File(wogDir + "/res/music"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("OGG sound file", "*.ogg"));

        File resrcFile = fileChooser.showOpenDialog(FXStage.getStage());

        if (resrcFile != null) importMusic(level, resrcFile, true);
    }


    public static void importMusic(Asset level, File resrcFile, boolean fromUser) {

         // If resrcFile is not already present in res/music, copy resrcFile into res/music.

        String dir = FileManager.getGameDir(level.getVersion());

        if (!(level instanceof WOG1Level wog1Level)) return;

        // Copy the file.
        String normalizedFilename = resrcFile.getName().split("/.")[0].replace(' ', '_');
        String soundPath = "res/levels/" + level.getName() + "/" + normalizedFilename;
        if (!new File(dir + "/res/music/" + resrcFile.getName()).exists()) {
            try {
                Files.copy(resrcFile.toPath(), Paths.get(dir + "/res/music/" + resrcFile.getName()));
            } catch (IOException e) {
                ErrorAlarm.show(e);
            }
        } else {
            soundPath = "res/music/" + normalizedFilename;
        }

        soundPath = cleanResourcePath(soundPath, level.getVersion(), ".ogg");

        // Add a new sound resource with a default ID and path leading to resrcFile in res\music.
        String soundResourceName = "SOUND_LEVEL_" + level.getName().toUpperCase() + "_" + new File(soundPath).getName().toUpperCase();
        EditorObject soundResourceObject = ObjectCreator.create(Sound.class, null, level.getVersion());
        soundResourceObject.setAttribute("id", soundResourceName);
        soundResourceObject.setAttribute("path", soundPath);

        int whereToPlaceResource = 0;
        int count = 0;
        for (EditorObject resourceThing : level.getResources().getChildren()) {
            count++;
            if (resourceThing instanceof Sound) {
                whereToPlaceResource = count;
            }
        }

        level.getResources().getChildren().add(soundResourceObject);
        soundResourceObject.setParent(level.getResources(), whereToPlaceResource, whereToPlaceResource);

        // If a music object already exists, change its sound attribute.
        for (EditorObject music : level.getObjects()) {
            if (music instanceof music) {
                String oldID = music.getAttribute("id").stringValue();
                music.setAttribute("id", soundResourceName);
                UndoManager.registerChange(new CreateFileAction(resrcFile.getPath(), soundPath),
                        new AttributeChangeAction(music.getAttribute("id"), oldID, soundResourceName));
                return;
            }
        }

        // Otherwise, create a new music object set to the sound resource's ID.
        EditorObject musicObject = ObjectCreator.create(music.class, wog1Level.getLevel(), level.getVersion());
        musicObject.setAttribute("id", soundResourceName);
        musicObject.onLoaded(wog1Level);
        UndoManager.registerChange(new CreateFileAction(resrcFile.getPath(), soundPath),
                new ObjectCreationAction(soundResourceObject),
                new ObjectCreationAction(musicObject, wog1Level.getLevel(), wog1Level.getLevel().getChildren().size(), wog1Level.getLevel().getTreeItem().getChildren().size()));

        // Refresh to let any previously invalid objects update.
        FXHierarchy.getHierarchy().refresh();

    }


    public static void importLoopsound(Asset level) {
        FileChooser fileChooser = new FileChooser();
        String wogDir = FileManager.getGameDir(level.getVersion());
        fileChooser.setInitialDirectory(new File(wogDir + "/res/sounds"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("OGG sound file", "*.ogg"));

        File resrcFile = fileChooser.showOpenDialog(FXStage.getStage());

        if (resrcFile != null) {
            importLoopsound(level, resrcFile, true);
        }
    }


    public static void importLoopsound(Asset level, File resrcFile, boolean fromUser) {

        // If resrcFile is not already present in res/sounds, copy resrcFile into res/sounds.

        String dir = FileManager.getGameDir(level.getVersion());

        /* copy file */
        String normalizedFilename = resrcFile.getName().split("/.")[0].replace(' ', '_');
        String soundPath = "res/levels/" + level.getName() + "/" + normalizedFilename;
        if (!new File(dir + "/res/sounds/" + resrcFile.getName()).exists()) {
            try {
                Files.copy(resrcFile.toPath(), Paths.get(dir + "/res/sounds/" + resrcFile.getName()));
            } catch (Exception e) {
                ErrorAlarm.show(e);
            }
        } else {
            soundPath = "res/sounds/" + normalizedFilename;
        }

        // Add a new sound resource with a default ID and path leading to resrcFile inres\sounds.
        String soundResourceName = "SOUND_LEVEL_" + level.getName().toUpperCase() + "_" + normalizedFilename.toUpperCase();
        EditorObject soundResourceObject = ObjectCreator.create(Sound.class, null, level.getVersion());
        assert soundResourceObject != null;
        soundResourceObject.setAttribute("id", soundResourceName);
        soundResourceObject.setAttribute("path", soundPath);

        int whereToPlaceResource = 0;
        int count = 0;
        for (EditorObject resourceThing : level.getResources().getChildren()) {
            count++;
            if (resourceThing instanceof Sound) {
                whereToPlaceResource = count;
            }
        }

        level.getResources().getChildren().add(soundResourceObject);
        // TODO:
        soundResourceObject.setParent(level.getResources(), whereToPlaceResource, whereToPlaceResource);

        /* If a music object already exists, change its sound attribute. */
        for (EditorObject music : level.getObjects()) {
            if (music instanceof loopsound) {
                String oldID = music.getAttribute("id").stringValue();
                music.setAttribute("id", soundResourceName);
                UndoManager.registerChange(new CreateFileAction(resrcFile.getPath(), resrcFile.getPath()),
                        new AttributeChangeAction(music.getAttribute("id"), oldID, soundResourceName));
                return;
            }
        }

        /* Otherwise, create a new music object set to the sound resource's ID. */
        // TODO:
        //EditorObject musicObject = ObjectCreator.create(loopsound.class, ((WOG1Level)level).getObject("level"), level.getVersion());
        //musicObject.setAttribute("id", soundResourceName);
        //level.getObjects().add(musicObject);
        UndoManager.registerChange(new CreateFileAction(resrcFile.getPath(), resrcFile.getPath()),
                new ObjectCreationAction(soundResourceObject));
                //new ObjectCreationAction(musicObject, ((WOG1Level)level).getObject("level").getChildren().size(), ((WOG1Level)level).getObject("level").getTreeItem().getChildren().size()));

    }

}
