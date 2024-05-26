package com.woogleFX.functions;

import com.woogleFX.editorObjects.EditorObject;
import com.woogleFX.editorObjects.objectCreators.ObjectAdder;
import com.woogleFX.editorObjects.objectCreators.ObjectCreator;
import com.woogleFX.engine.fx.FXHierarchy;
import com.woogleFX.file.BaseGameResources;
import com.woogleFX.file.FileManager;
import com.woogleFX.functions.undoHandling.UndoManager;
import com.woogleFX.functions.undoHandling.userActions.CreateFileAction;
import com.woogleFX.functions.undoHandling.userActions.ObjectCreationAction;
import com.woogleFX.functions.undoHandling.userActions.UserAction;
import com.woogleFX.structures.GameVersion;
import com.woogleFX.structures.WorldLevel;
import com.worldOfGoo.resrc.ResrcImage;
import javafx.scene.image.Image;

import javax.imageio.ImageIO;
import javax.imageio.stream.FileImageInputStream;
import javax.imageio.stream.ImageInputStream;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;

public class LevelResourceImporter {

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
                new ObjectCreationAction(imageResourceObject, imageResourceObject.getParent().getChildren().indexOf(imageResourceObject))
                //new CreateFileAction(dir + "\\" + path + ".png", Files.readAllBytes(resrcFile.toPath()))
        );

    }

}
