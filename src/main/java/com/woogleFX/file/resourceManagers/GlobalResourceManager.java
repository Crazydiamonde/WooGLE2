package com.woogleFX.file.resourceManagers;

import com.woogleFX.editorObjects.EditorObject;
import com.woogleFX.engine.gui.alarms.ErrorAlarm;
import com.woogleFX.engine.gui.alarms.LoadingResourcesAlarm;
import com.woogleFX.file.FileManager;
import com.woogleFX.gameData.animation.AnimationManager;
import com.woogleFX.gameData.animation.AnimationReader;
import com.woogleFX.gameData.level.GameVersion;
import com.woogleFX.gameData.particle.ParticleManager;
import com.worldOfGoo.resrc.Material;
import com.worldOfGoo.particle._Particle;
import com.worldOfGoo.resrc.Font;
import com.worldOfGoo.resrc.ResrcImage;
import com.worldOfGoo.resrc.SetDefaults;
import com.worldOfGoo.resrc.Sound;
import com.worldOfGoo.text.TextString;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/** Stores global resources (those specified in properties/resources.xml). */
public class GlobalResourceManager {

    private static final ArrayList<EditorObject> oldResources = new ArrayList<>();
    public static ArrayList<EditorObject> getOldResources() {
        return oldResources;
    }


    private static final ArrayList<EditorObject> newResources = new ArrayList<>();
    public static ArrayList<EditorObject> getNewResources() {
        return newResources;
    }


    private static final ArrayList<String> allFailedResources = new ArrayList<>();


    public static void init() {

        allFailedResources.clear();

        oldResources.clear();
        if (!FileManager.getGameDir(GameVersion.OLD).isEmpty()) {
            openResources(GameVersion.OLD);
            openParticles(GameVersion.OLD);
            openAnimations(GameVersion.OLD);
            openText(GameVersion.OLD);
            openMaterials(GameVersion.OLD);
        }

        newResources.clear();
        if (!FileManager.getGameDir(GameVersion.NEW).isEmpty()) {
            openResources(GameVersion.NEW);
            openParticles(GameVersion.NEW);
            openAnimations(GameVersion.NEW);
            openText(GameVersion.NEW);
            openMaterials(GameVersion.NEW);
        }

        // Load particle names, remove duplicates, and sort them alphabetically
        Set<String> particleNames = new HashSet<>();
        ParticleManager.getParticles().stream()
                .filter(particle -> particle.attributeExists("name"))
                .forEach(particle -> particleNames.add(particle.getAttribute("name").stringValue()));
        ParticleManager.getSortedParticleNames().clear();
        ParticleManager.getSortedParticleNames().addAll(particleNames);
        ParticleManager.getSortedParticleNames().sort(String::compareToIgnoreCase);

        if (!allFailedResources.isEmpty()) {
            StringBuilder fullError = new StringBuilder();
            for (String resource : allFailedResources) {
                fullError.append("\n").append(resource);
            }
            LoadingResourcesAlarm.showInitial(fullError.substring(1));
        }

    }


    private static void openResources(GameVersion version) {

        ArrayList<EditorObject> toAddTo = version == GameVersion.OLD ? oldResources : newResources;

        ArrayList<EditorObject> resources;
        try {
            resources = FileManager.openResources(version);
        } catch (ParserConfigurationException | SAXException | IOException e) {
            ErrorAlarm.show(e);
            return;
        }

        SetDefaults currentSetDefaults = null;

        for (EditorObject editorObject : resources) {

            if (editorObject instanceof SetDefaults setDefaults) {
                currentSetDefaults = setDefaults;
            }

            else if (editorObject instanceof ResrcImage resrcImage) {
                resrcImage.setSetDefaults(currentSetDefaults);
                toAddTo.add(resrcImage);
            } else if (editorObject instanceof Sound sound) {
                sound.setSetDefaults(currentSetDefaults);
                toAddTo.add(sound);
            } else if (editorObject instanceof Font font) {
                font.setSetDefaults(currentSetDefaults);
                toAddTo.add(font);
            }

        }

    }


    private static void openParticles(GameVersion version) {
        ArrayList<EditorObject> particles2;
        try {
            particles2 = FileManager.openParticles(version);
        } catch (ParserConfigurationException | SAXException | IOException e) {
            ErrorAlarm.show(e);
            return;
        }

        ParticleManager.getParticles().addAll(particles2);
        for (EditorObject particle : particles2) {
            try {
                if (particle instanceof _Particle _particle) {
                    _particle.update(version);
                } else {
                    particle.update();
                }
            } catch (Exception e) {
                allFailedResources.add("Particle: " + particle.getParent().getAttribute("name").stringValue() + " (version " + version + ")");
            }
        }

    }


    private static void openAnimations(GameVersion version) {

        File animationsDirectory = new File(FileManager.getGameDir(version) + "\\res\\anim");
        File[] animationsArray = animationsDirectory.listFiles();
        if (animationsArray == null) return;

        for (File second : animationsArray) {
            if (version == GameVersion.NEW || !second.getName().substring(second.getName().lastIndexOf(".")).equals(".binltl64")) {
                try (FileInputStream test2 = new FileInputStream(second)) {
                    byte[] allBytes = test2.readAllBytes();
                    if (version == GameVersion.OLD) {
                        AnimationManager.getAnimations().add(AnimationReader.readBinltl(allBytes, second.getName()));
                    } else if (version == GameVersion.NEW) {
                        AnimationManager.getAnimations().add(AnimationReader.readBinuni(allBytes, second.getName()));
                    }
                } catch (Exception e) {
                    allFailedResources.add("Animation: " + second.getName() + " (version " + version + ")");
                }
            }
        }

    }


    private static void openText(GameVersion version) {

        ArrayList<EditorObject> toAddTo = version == GameVersion.OLD ? oldResources : newResources;

        ArrayList<EditorObject> textList;
        try {
            textList = FileManager.openText(version);
        } catch (ParserConfigurationException | SAXException | IOException e) {
            ErrorAlarm.show(e);
            return;
        }

        for (EditorObject text : textList) {
            if (text instanceof TextString) {
                toAddTo.add(text);
            }
        }

    }


    private static void openMaterials(GameVersion version) {

        ArrayList<EditorObject> toAddTo = version == GameVersion.OLD ? oldResources : newResources;

        ArrayList<EditorObject> materialList;
        try {
            materialList = FileManager.openMaterials(version);
        } catch (ParserConfigurationException | SAXException | IOException e) {
            ErrorAlarm.show(e);
            return;
        }

        for (EditorObject material : materialList) {
            if (material instanceof Material) {
                toAddTo.add(material);
            }
        }

    }

}
