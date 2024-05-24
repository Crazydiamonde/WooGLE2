package com.woogleFX.functions;

import com.woogleFX.editorObjects.objectCreators.ObjectCreator;
import com.woogleFX.engine.fx.FXStage;
import com.woogleFX.engine.SelectionManager;
import com.woogleFX.file.fileImport.AnimationReader;
import com.woogleFX.file.BaseGameResources;
import com.woogleFX.file.FileManager;
import com.woogleFX.file.resourceManagers.GlobalResourceManager;
import com.woogleFX.editorObjects.objectCreators.ObjectAdder;
import com.woogleFX.file.resourceManagers.AnimationManager;
import com.woogleFX.file.resourceManagers.ParticleManager;
import com.woogleFX.functions.undoHandling.UndoManager;
import com.woogleFX.engine.gui.Alarms;
import com.woogleFX.editorObjects.EditorAttribute;
import com.woogleFX.editorObjects.EditorObject;
import com.woogleFX.structures.GameVersion;
import com.woogleFX.functions.undoHandling.userActions.AttributeChangeAction;
import com.woogleFX.functions.undoHandling.userActions.ImportResourceAction;
import com.woogleFX.functions.undoHandling.userActions.ObjectCreationAction;
import com.woogleFX.structures.WorldLevel;
import com.worldOfGoo.level.Loopsound;
import com.worldOfGoo.level.Music;
import com.worldOfGoo.particle._Particle;
import com.worldOfGoo.resrc.ResrcImage;
import com.worldOfGoo.resrc.Sound;
import com.worldOfGoo.text.TextString;
import javafx.stage.FileChooser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import javax.imageio.ImageIO;
import javax.xml.parsers.ParserConfigurationException;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
        for (EditorObject editorObject : level.getResources()) {
            if (editorObject instanceof ResrcImage) {
                /*
                 * Ask the global resource manager to update the images to whatever's in the
                 * level file
                 */
                try {
                    GlobalResourceManager.updateResource(editorObject.getAttribute("id").stringValue(), level.getVersion());
                } catch (FileNotFoundException e) {
                    failedToLoad.append(editorObject.getAttribute("id")).append("\n");
                    logger.error("", e);
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
                importImage(level, resrcFile);
            }
        }

    }

    public static void importImage(WorldLevel level, File resrcFile) {
        try {
            BufferedImage image = ImageIO.read(resrcFile);
            String normalizedFilename = resrcFile.getName().split("\\.")[0].replace(' ', '_');
            String path = "";
            if (level.getVersion() == GameVersion.OLD) {
                path = FileManager.getOldWOGdir();
                // If file with this name already exists, rename it
                if (new File(FileManager.getOldWOGdir() + "\\res\\levels\\" + level.getLevelName() + "\\"
                        + normalizedFilename + ".png").exists()) {
                    int i = 1;
                    while (new File(FileManager.getOldWOGdir() + "\\res\\levels\\" + level.getLevelName() + "\\"
                            + normalizedFilename + "_" + i + ".png").exists()) {
                        i++;
                    }
                    normalizedFilename += "_" + i;
                }
            } else if (level.getVersion() == GameVersion.NEW) {
                path = FileManager.getNewWOGdir();
                // If file with this name already exists, rename it
                if (new File(FileManager.getNewWOGdir() + "\\res\\levels\\" + level.getLevelName() + "\\"
                        + normalizedFilename + ".png").exists()) {
                    int i = 1;
                    while (new File(FileManager.getNewWOGdir() + "\\res\\levels\\" + level.getLevelName() + "\\"
                            + normalizedFilename + "_" + i + ".png").exists()) {
                        i++;
                    }
                    normalizedFilename += "_" + i;
                }
            }

            String imgPath = resrcFile.getPath();
            if (level.getVersion() == GameVersion.OLD) {
                if (resrcFile.getPath().contains(FileManager.getOldWOGdir())) {
                    imgPath = resrcFile.getPath().replace(FileManager.getOldWOGdir(), "");
                }
            } else if (level.getVersion() == GameVersion.NEW) {
                if (resrcFile.getPath().contains(FileManager.getNewWOGdir())) {
                    imgPath = resrcFile.getPath().replace(FileManager.getNewWOGdir(), "");
                }
            }

            String rescTestPath = imgPath.replace("\\", "/");
            String resourcePath = ("res\\levels\\" + level.getLevelName() + "\\" + normalizedFilename).replace("\\", "/");
            if (rescTestPath.startsWith("/")) {
                rescTestPath = rescTestPath.substring(1);
            }
            if (rescTestPath.endsWith(".png")) {
                rescTestPath = rescTestPath.substring(0, rescTestPath.length() - 4);
            }
            if (level.getVersion() == GameVersion.NEW && rescTestPath.endsWith("@2x")) {
                // Strip @2x suffix, since this is handled transparently by the game already
                rescTestPath = rescTestPath.substring(0, rescTestPath.length() - 3);
            }
            if (!BaseGameResources.containsImage(rescTestPath)) {
                ImageIO.write(image, "png", new File(path + "\\res\\levels\\" + level.getLevelName() + "\\"
                        + normalizedFilename + ".png"));
                imgPath = path + "\\res\\levels\\" + level.getLevelName() + "\\" + normalizedFilename
                        + ".png";
            } else {
                imgPath = resourcePath = rescTestPath;
                if (level.getVersion() == GameVersion.NEW && normalizedFilename.endsWith("@2x")) {
                    // Strip @2x suffix from here too
                    normalizedFilename = normalizedFilename.substring(0, normalizedFilename.length() - 3);
                }
            }

            String imageResourceName = "IMAGE_SCENE_" + level.getLevelName().toUpperCase() + "_"
                    + normalizedFilename.toUpperCase();
            EditorObject imageResourceObject = ObjectCreator.create("Image", null);

            imageResourceObject.setAttribute("id", imageResourceName);
            imageResourceObject.setAttribute("path", resourcePath);

            int whereToPlaceResource = 0;
            int count = 0;
            for (EditorObject resourceThing : level.getResourcesObject().getChildren().get(0).getChildren()) {
                count++;
                if (resourceThing instanceof ResrcImage) {
                    whereToPlaceResource = count;
                }
            }

            level.getResources().add(imageResourceObject);
            imageResourceObject.setParent(level.getResourcesObject().getChildren().get(0), whereToPlaceResource);

            UndoManager.registerChange(new ImportResourceAction(imageResourceObject, imgPath));

            GlobalResourceManager.addResource(imageResourceObject, level.getVersion());

            // Add a new SceneLayer with this image
            EditorObject layer = ObjectAdder.addObject("SceneLayer");
            if (layer != null) layer.setAttribute("image", imageResourceName);

        } catch (IOException e) {
            Alarms.errorMessage(e);
        }
    }

    public static void newTextResource(WorldLevel level) {
        EditorObject newTextObject = ObjectCreator.create("string", level.getTextObject());
        ObjectAdder.fixString(newTextObject);
        level.getText().add(newTextObject);
        SelectionManager.setSelected(newTextObject);
        UndoManager.registerChange(
                new ObjectCreationAction(newTextObject, level.getTextObject().getChildren().indexOf(newTextObject)));
    }

    public static void cleanLevelResources(WorldLevel level) {
        ArrayList<EditorObject> possiblyUnusedResources = new ArrayList<>();

        for (EditorObject editorObject : level.getResources()) {
            if (editorObject instanceof ResrcImage || editorObject instanceof Sound) {
                possiblyUnusedResources.add(editorObject);
            }
        }

        for (EditorObject editorObject : level.getScene()) {
            for (EditorAttribute attribute : editorObject.getAttributes()) {
                if (!attribute.stringValue().isEmpty()) {
                    for (EditorObject resourceObject : possiblyUnusedResources) {
                        if (resourceObject.getAttribute("id").stringValue().equals(attribute.stringValue())) {
                            possiblyUnusedResources.remove(resourceObject);
                            break;
                        }
                    }
                }
            }
        }

        for (EditorObject editorObject : level.getLevel()) {
            for (EditorAttribute attribute : editorObject.getAttributes()) {
                if (!attribute.stringValue().isEmpty()) {
                    for (EditorObject resourceObject : possiblyUnusedResources) {
                        if (resourceObject.getAttribute("id").stringValue().equals(attribute.stringValue())) {
                            possiblyUnusedResources.remove(resourceObject);
                            break;
                        }
                    }
                }
            }
        }

        if (!possiblyUnusedResources.isEmpty()) {
            Alarms.confirmCleanResourcesMessage(possiblyUnusedResources);
        }

    }

    public static void confirmedCleanLevelResources(ArrayList<EditorObject> toClean) {
        // TODO possibly add an undo event for this
        for (EditorObject object : toClean) {
            ObjectManager.deleteItem(LevelManager.getLevel(), object, false);
        }
    }

    public static void importMusic(WorldLevel level) {
        FileChooser fileChooser = new FileChooser();
        String wogDir = level.getVersion() == GameVersion.OLD ? FileManager.getOldWOGdir() : FileManager.getNewWOGdir();
        fileChooser.setInitialDirectory(new File(wogDir + "\\res\\music"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("OGG sound file", "*.ogg"));

        File resrcFile = fileChooser.showOpenDialog(FXStage.getStage());

        if (resrcFile != null) {
            importMusic(level, resrcFile, true);
        }
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
        for (EditorObject resourceThing : level.getResourcesObject().getChildren().get(0).getChildren()) {
            count++;
            if (resourceThing instanceof Sound) {
                whereToPlaceResource = count;
            }
        }

        level.getResources().add(soundResourceObject);
        soundResourceObject.setParent(level.getResourcesObject().getChildren().get(0), whereToPlaceResource);

        /* If a music object already exists, change its sound attribute. */
        for (EditorObject music : level.getLevel()) {
            if (music instanceof Music) {
                String oldID = music.getAttribute("id").stringValue();
                music.setAttribute("id", soundResourceName);
                UndoManager.registerChange(new ImportResourceAction(soundResourceObject, resrcFile.getPath()),
                        new AttributeChangeAction(music.getAttribute("id"), oldID, soundResourceName));
                return;
            }
        }

        /* Otherwise, create a new music object set to the sound resource's ID. */
        EditorObject musicObject = ObjectCreator.create("music", level.getLevelObject());
        musicObject.setAttribute("id", soundResourceName);
        level.getLevel().add(musicObject);
        UndoManager.registerChange(new ImportResourceAction(soundResourceObject, resrcFile.getPath()),
                new ObjectCreationAction(soundResourceObject, whereToPlaceResource),
                new ObjectCreationAction(musicObject, level.getLevel().size() - 1));

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
        for (EditorObject resourceThing : level.getResourcesObject().getChildren().get(0).getChildren()) {
            count++;
            if (resourceThing instanceof Sound) {
                whereToPlaceResource = count;
            }
        }

        level.getResources().add(soundResourceObject);
        soundResourceObject.setParent(level.getResourcesObject().getChildren().get(0), whereToPlaceResource);

        /* If a music object already exists, change its sound attribute. */
        for (EditorObject music : level.getLevel()) {
            if (music instanceof Loopsound) {
                String oldID = music.getAttribute("id").stringValue();
                music.setAttribute("id", soundResourceName);
                UndoManager.registerChange(new ImportResourceAction(soundResourceObject, resrcFile.getPath()),
                        new AttributeChangeAction(music.getAttribute("id"), oldID, soundResourceName));
                return;
            }
        }

        /* Otherwise, create a new music object set to the sound resource's ID. */
        EditorObject musicObject = ObjectCreator.create("loopsound", level.getLevelObject());
        musicObject.setAttribute("id", soundResourceName);
        level.getLevel().add(musicObject);
        UndoManager.registerChange(new ImportResourceAction(soundResourceObject, resrcFile.getPath()),
                new ObjectCreationAction(soundResourceObject, whereToPlaceResource),
                new ObjectCreationAction(musicObject, level.getLevel().size() - 1));

    }


    public static void importGameResources(GameVersion version) {
        ArrayList<String> allFailedResources = new ArrayList<>();

        // Open resources.xml for 1.3
        // This takes forever to finish
        if (version == GameVersion.OLD && FileManager.hasOldWOG()) {
            try {
                ArrayList<EditorObject> resources = FileManager.openResources(GameVersion.OLD);
                if (resources != null) {
                    for (EditorObject resrc : resources) {
                        if (resrc instanceof ResrcImage) {
                            GlobalResourceManager.addResource(resrc, GameVersion.OLD);
                        }
                    }
                }
            } catch (ParserConfigurationException | SAXException | IOException e) {
                Alarms.errorMessage(e);
            }
        }

        // Open resources.xml for 1.5
        // This happens instantly
        if (version == GameVersion.NEW && FileManager.hasNewWOG()) {
            try {
                ArrayList<EditorObject> resources = FileManager.openResources(GameVersion.NEW);
                if (resources != null) {
                    for (EditorObject resrc : resources) {
                        if (resrc instanceof ResrcImage) {
                            GlobalResourceManager.addResource(resrc, GameVersion.NEW);
                        }
                    }
                }
            } catch (ParserConfigurationException | SAXException | IOException e) {
                Alarms.errorMessage(e);
            }
        }

        if (version == GameVersion.OLD && FileManager.hasOldWOG()) {
            try {
                FileManager.openParticles(GameVersion.OLD);
                ArrayList<EditorObject> particles2 = FileManager.commonBallData;
                ParticleManager.getParticles().addAll(particles2);
                for (EditorObject particle : particles2) {
                    try {
                        if (particle instanceof _Particle) {
                            ((_Particle) particle).update(GameVersion.OLD);
                        } else {
                            particle.update();
                        }
                    } catch (Exception e) {
                        allFailedResources
                                .add("Particle: " + particle.getParent().getAttribute("name") + " (version 1.3)");
                    }
                }
            } catch (ParserConfigurationException | SAXException | IOException e) {
                Alarms.errorMessage(e);
            }
        }

        if (version == GameVersion.NEW && FileManager.hasNewWOG()) {
            try {
                FileManager.openParticles(GameVersion.NEW);
                ArrayList<EditorObject> particles2 = FileManager.commonBallData;
                ParticleManager.getParticles().addAll(particles2);
                for (EditorObject particle : particles2) {
                    try {
                        if (particle instanceof _Particle) {
                            ((_Particle) particle).update(GameVersion.NEW);
                        } else {
                            particle.update();
                        }
                    } catch (Exception e) {
                        allFailedResources
                                .add("Particle: " + particle.getParent().getAttribute("name") + " (version 1.5)");
                    }
                }
            } catch (ParserConfigurationException | SAXException | IOException e) {
                Alarms.errorMessage(e);
            }
        }

        // Load particle names, remove duplicates, and sort them alphabetically
        Set<String> particleNames = new HashSet<>();
        ParticleManager.getParticles().stream()
                .filter(particle -> particle.attributeExists("name"))
                .forEach(particle -> particleNames.add(particle.getAttribute("name").stringValue()));
        ParticleManager.getSortedParticleNames().clear();
        ParticleManager.getSortedParticleNames().addAll(particleNames);
        ParticleManager.getSortedParticleNames().sort(String::compareToIgnoreCase);

        // Load all animations from the game files
        try {
            if (version == GameVersion.OLD && FileManager.hasOldWOG()) {
                File bruh1 = new File(FileManager.getOldWOGdir() + "\\res\\anim");
                File[] animationsArray = bruh1.listFiles();
                if (animationsArray != null) {
                    for (File second : animationsArray) {
                        if (!second.getName().substring(second.getName().lastIndexOf(".")).equals(".binltl64")) {
                            try (FileInputStream test2 = new FileInputStream(second)) {
                                byte[] allBytes = test2.readAllBytes();
                                AnimationManager.getAnimations().add(AnimationReader.readBinltl(allBytes, second.getName()));
                            } catch (Exception e) {
                                allFailedResources.add("Animation: " + second.getName() + " (version 1.3)");
                            }
                        }
                    }
                }
            }
            if (version == GameVersion.NEW && FileManager.hasNewWOG()) {
                File bruh2 = new File(FileManager.getNewWOGdir() + "\\res\\anim");
                File[] animationsArray = bruh2.listFiles();
                if (animationsArray != null) {
                    for (File second : animationsArray) {
                        try (FileInputStream test2 = new FileInputStream(second)) {
                            byte[] allBytes = test2.readAllBytes();
                            AnimationManager.getAnimations().add(AnimationReader.readBinuni(allBytes, second.getName()));
                        } catch (Exception e) {
                            allFailedResources.add("Animation: " + second.getName() + " (version 1.5)");
                        }
                    }
                }
            }
        } catch (Exception e) {
            Alarms.errorMessage(e);
        }

        if (version == GameVersion.OLD && FileManager.hasOldWOG()) {
            try {
                ArrayList<EditorObject> textList = FileManager.openText(GameVersion.OLD);
                if (textList != null) {
                    for (EditorObject text : textList) {
                        if (text instanceof TextString) {
                            GlobalResourceManager.addResource(text, GameVersion.OLD);
                        }
                    }
                }
            } catch (ParserConfigurationException | SAXException | IOException e) {
                Alarms.errorMessage(e);
            }
        }
        if (version == GameVersion.NEW && FileManager.hasNewWOG()) {
            try {
                ArrayList<EditorObject> textList = FileManager.openText(GameVersion.NEW);
                if (textList != null) {
                    for (EditorObject text : textList) {
                        if (text instanceof TextString) {
                            GlobalResourceManager.addResource(text, GameVersion.NEW);
                        }
                    }
                }
            } catch (ParserConfigurationException | SAXException | IOException e) {
                Alarms.errorMessage(e);
            }
        }

        if (!allFailedResources.isEmpty()) {
            StringBuilder fullError = new StringBuilder();
            for (String resource : allFailedResources) {
                fullError.append("\n").append(resource);
            }
            Alarms.loadingInitialResourcesError(fullError.substring(1));
        }
    }

}
