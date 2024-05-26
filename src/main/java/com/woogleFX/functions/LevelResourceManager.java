package com.woogleFX.functions;

import com.woogleFX.editorObjects.objectCreators.ObjectCreator;
import com.woogleFX.engine.fx.FXStage;
import com.woogleFX.engine.SelectionManager;
import com.woogleFX.file.FileManager;
import com.woogleFX.editorObjects.objectCreators.ObjectAdder;
import com.woogleFX.file.resourceManagers.ResourceManager;
import com.woogleFX.functions.undoHandling.UndoManager;
import com.woogleFX.engine.gui.Alarms;
import com.woogleFX.editorObjects.EditorObject;
import com.woogleFX.functions.undoHandling.userActions.ObjectDestructionAction;
import com.woogleFX.structures.GameVersion;
import com.woogleFX.functions.undoHandling.userActions.AttributeChangeAction;
import com.woogleFX.functions.undoHandling.userActions.CreateFileAction;
import com.woogleFX.functions.undoHandling.userActions.ObjectCreationAction;
import com.woogleFX.structures.WorldLevel;
import com.worldOfGoo.level.Loopsound;
import com.worldOfGoo.level.Music;
import com.worldOfGoo.resrc.Font;
import com.worldOfGoo.resrc.ResrcImage;
import com.worldOfGoo.resrc.Sound;
import javafx.stage.FileChooser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class LevelResourceManager {

    private static final Logger logger = LoggerFactory.getLogger(LevelLoader.class);


    public static void deleteResource(WorldLevel level, String file) {
        String path = new File(file).getName();
        String startPath = level.getVersion() == GameVersion.NEW ? FileManager.getNewWOGdir() : FileManager.getOldWOGdir();
        File levelFile = new File(startPath + "\\res\\levels\\" + level.getLevelName());
        File[] levelChildren = levelFile.listFiles();
        if (levelChildren != null) {
            for (File resourceFile : levelChildren) {
                if (resourceFile.getName().split("\\.")[0].equals(path)) {
                    // noinspection ResultOfMethodCallIgnored
                    resourceFile.delete();
                }
            }
        }
    }


    public static void updateLevelResources(WorldLevel level) {
        StringBuilder failedToLoad = new StringBuilder();

        /* Loop through all the images in the level's resources */
        for (EditorObject editorObject : level.getResrc()) {
            if (editorObject instanceof ResrcImage resrcImage) {
                if (!ResourceManager.updateResource(resrcImage, level.getVersion())) {
                    failedToLoad.append(editorObject.getAttribute("id").stringValue()).append("\n");
                    logger.error("Failed to load resource: " + editorObject.getAttribute("id").stringValue());
                }
            }
        }

        /* Update every object in the level */
        /* I hope this doesn't break anything */
        for (EditorObject editorObject : level.getScene()) {
            editorObject.update();
        }
        for (EditorObject editorObject : level.getLevel()) {
            editorObject.update();
        }

        if (!failedToLoad.toString().isEmpty()) {
            Alarms.loadingResourcesError(failedToLoad.toString());
        }

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




    /** Creates a new text resource in the given level. */
    public static void newTextResource(WorldLevel level) {

        EditorObject newTextObject = ObjectCreator.create("string", level.getTextObject());
        ObjectAdder.fixString(newTextObject);

        level.getText().add(newTextObject);

        SelectionManager.setSelected(newTextObject);

        int childIndex = level.getTextObject().getChildren().indexOf(newTextObject);
        UndoManager.registerChange(new ObjectCreationAction(newTextObject, childIndex));

    }


    private static boolean isResourceUsed(EditorObject resource, WorldLevel level) {
        String resourceID = resource.getAttribute("id").stringValue();
        for (EditorObject object : level.getScene()) if (isResourceUsed_SingleObject(resourceID, object)) return true;
        for (EditorObject object : level.getLevel()) if (isResourceUsed_SingleObject(resourceID, object)) return true;
        return false;
    }


    private static boolean isResourceUsed_SingleObject(String resourceID, EditorObject editorObject) {
        return Arrays.stream(editorObject.getAttributes()).anyMatch(
                attribute -> !attribute.stringValue().isEmpty() && attribute.stringValue().equals(resourceID));
    }


    /** Removes any unused resources in the given level. */
    public static void cleanLevelResources(WorldLevel level) {

        ArrayList<EditorObject> unused = new ArrayList<>();

        for (EditorObject editorObject : level.getResrc()) {
            if (editorObject instanceof ResrcImage || editorObject instanceof Sound || editorObject instanceof Font) {
                if (!isResourceUsed(editorObject, level)) unused.add(editorObject);
            }
        }

        if (!unused.isEmpty()) Alarms.confirmCleanResourcesMessage(level, unused);

    }


    public static void confirmedCleanLevelResources(WorldLevel level, ArrayList<EditorObject> toClean) {
        ArrayList<ObjectDestructionAction> objectDestructionActions = new ArrayList<>();
        for (EditorObject object : toClean) {
            int childIndex = object.getParent().getChildren().indexOf(object);
            objectDestructionActions.add(new ObjectDestructionAction(object, childIndex));
            ObjectManager.deleteItem(level, object, false);
        }
        UndoManager.registerChange(objectDestructionActions.toArray(new ObjectDestructionAction[0]));
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

        /*
         * If resrcFile is not already present in res\music, copy resrcFile into
         * res\music.
         */

        String dir = level.getVersion() == GameVersion.OLD ? FileManager.getOldWOGdir() : FileManager.getNewWOGdir();

        /* copy file */
        String normalizedFilename = resrcFile.getName().split("\\.")[0].replace(' ', '_');
        String soundPath = "res/levels/" + level.getLevelName() + "/" + normalizedFilename;
        if (!new File(dir + "\\res\\music\\" + resrcFile.getName()).exists()) {
            try {
                if (level.getVersion() == GameVersion.OLD) {
                    Files.copy(resrcFile.toPath(),
                            Paths.get(FileManager.getOldWOGdir() + "\\res\\music\\" + resrcFile.getName()));
                } else if (level.getVersion() == GameVersion.NEW) {
                    Files.copy(resrcFile.toPath(),
                            Paths.get(FileManager.getNewWOGdir() + "\\res\\music\\" + resrcFile.getName()));
                }
            } catch (Exception e) {
                Alarms.errorMessage(e);
            }
        } else {
            soundPath = "res/music/" + normalizedFilename;
        }

        /*
         * Add a new sound resource with a default ID and path leading to resrcFile in
         * res\music.
         */
        String soundResourceName = "SOUND_LEVEL_" + level.getLevelName().toUpperCase() + "_" + normalizedFilename.toUpperCase();
        EditorObject soundResourceObject = ObjectCreator.create("Sound", null);

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

        /*
         * If resrcFile is not already present in res\music, copy resrcFile into
         * res\music.
         */

        String dir = level.getVersion() == GameVersion.OLD ? FileManager.getOldWOGdir() : FileManager.getNewWOGdir();

        /* copy file */
        String normalizedFilename = resrcFile.getName().split("\\.")[0].replace(' ', '_');
        String soundPath = "res/levels/" + level.getLevelName() + "/" + normalizedFilename;
        if (!new File(dir + "\\res\\music\\" + resrcFile.getName()).exists()) {
            try {
                if (level.getVersion() == GameVersion.OLD) {
                    Files.copy(resrcFile.toPath(),
                            Paths.get(FileManager.getOldWOGdir() + "\\res\\sounds\\" + resrcFile.getName()));
                } else if (level.getVersion() == GameVersion.NEW) {
                    Files.copy(resrcFile.toPath(),
                            Paths.get(FileManager.getNewWOGdir() + "\\res\\sounds\\" + resrcFile.getName()));
                }
            } catch (Exception e) {
                Alarms.errorMessage(e);
            }
        } else {
            soundPath = "res/sounds/" + normalizedFilename;
        }

        /*
         * Add a new sound resource with a default ID and path leading to resrcFile in
         * res\music.
         */
        String soundResourceName = "SOUND_LEVEL_" + level.getLevelName().toUpperCase() + "_"
                + normalizedFilename.toUpperCase();
        EditorObject soundResourceObject = ObjectCreator.create("Sound", null);

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
