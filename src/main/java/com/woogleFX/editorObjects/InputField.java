package com.woogleFX.editorObjects;

import java.io.File;

import com.woogleFX.file.FileManager;
import com.woogleFX.file.resourceManagers.AnimationManager;
import com.woogleFX.functions.LevelManager;
import com.woogleFX.file.resourceManagers.ParticleManager;
import com.woogleFX.structures.GameVersion;
import com.woogleFX.structures.simpleStructures.Color;
import com.woogleFX.structures.simpleStructures.Position;
import com.worldOfGoo.level.BallInstance;
import com.worldOfGoo.particle.Ambientparticleeffect;
import com.worldOfGoo.particle.Particleeffect;
import com.worldOfGoo.resrc.ResrcImage;
import com.worldOfGoo.resrc.SetDefaults;
import com.worldOfGoo.resrc.Sound;
import com.worldOfGoo.scene.Circle;
import com.worldOfGoo.scene.Compositegeom;
import com.worldOfGoo.scene.Rectangle;

public enum InputField {

    ANY,
    ANY_REQUIRED,
    NUMBER,
    NUMBER_NON_NEGATIVE,
    NUMBER_POSITIVE,
    NUMBER_ZERO_TO_ONE_INCLUSIVE,
    NUMBER_ZERO_TO_ONE_EXCLUSIVE,
    POSITION,
    IMAGE,
    IMAGE_REQUIRED,
    COLOR,
    ANIMATION,
    FLAG,
    RANGE,
    MATERIAL,
    TAG,
    GEOMETRY,
    TEXT,
    PARTICLES,
    BALL,
    OCD_TYPE,
    IMAGE_TYPE,
    GOOBALL_ID,
    UNIQUE_GOOBALL_ID,
    IMAGE_PATH,
    SOUND_PATH,
    FONT,
    CONTEXT;

    public static boolean verify(EditorObject object, InputField type, String potential) {

        if (type == null) return true;

        if (potential.isEmpty()) return !(type == ANY_REQUIRED || type == IMAGE_REQUIRED);

        switch (type) {
            case NUMBER -> {
                try {
                    Double.parseDouble(potential);
                    return true;
                } catch (NumberFormatException e) {
                    return false;
                }
            }
            case NUMBER_POSITIVE -> {
                try {
                    return Double.parseDouble(potential) > 0;
                } catch (NumberFormatException e) {
                    return false;
                }
            }
            case NUMBER_NON_NEGATIVE -> {
                try {
                    return Double.parseDouble(potential) >= 0;
                } catch (NumberFormatException e) {
                    return false;
                }
            }
            case ANY, ANY_REQUIRED -> {
                return true;
            }
            case POSITION -> {
                try {
                    Position.parse(potential);
                    return true;
                } catch (NumberFormatException e) {
                    return false;
                }
            }
            case IMAGE, IMAGE_REQUIRED -> {
                EditorObject resourceManifest = LevelManager.getLevel().getResrcObject();
                for (EditorObject resrc : LevelManager.getLevel().getResrc()) {

                    if (resrc instanceof ResrcImage resrcImage) {

                        int resrcIndex = resourceManifest.getChildren().indexOf(resrcImage);

                        String idprefix = "";
                        for (int i = resrcIndex - 1; i >= 0; i--) {
                            EditorObject editorObject = resourceManifest.getChildren().get(i);
                            if (editorObject instanceof SetDefaults setDefaults) {
                                idprefix = setDefaults.getAttribute("idprefix").stringValue();
                                break;
                            }
                        }

                        if (resrcImage.getAttribute("id").stringValue().equals(idprefix + potential)) {
                            return true;
                        }

                    }
                }
                return false;
            }
            case COLOR -> {
                try {
                    Color.parse(potential);
                    return true;
                } catch (Exception e) {
                    return false;
                }
            }
            case ANIMATION -> {
                return AnimationManager.hasAnimation(potential);
            }
            case FLAG -> {
                return potential.equals("true") || potential.equals("false");
            }
            case BALL -> {
                String path = LevelManager.getVersion() == GameVersion.NEW ? FileManager.getNewWOGdir() : FileManager.getOldWOGdir();
                for (File ballFile : new File(path + "\\res\\balls").listFiles()) {
                    if (ballFile.getName().equals(potential)) {
                        return true;
                    }
                }
                return false;
            }
            case TEXT -> {
                return true;
            }
            case PARTICLES -> {
                for (EditorObject particle : ParticleManager.getParticles()) {
                    if ((particle instanceof Particleeffect || particle instanceof Ambientparticleeffect) && particle.getAttribute("name").stringValue().equals(potential)) {
                        return true;
                    }
                }
                return false;
            }
            case MATERIAL -> {
                return true;
            }
            case TAG -> {
                return true;
            }
            case OCD_TYPE -> {
                return potential.equals("balls") || potential.equals("moves") || potential.equals("time");
            }
            case GOOBALL_ID -> {
                for (EditorObject editorObject : LevelManager.getLevel().getLevel()) {
                    if (editorObject instanceof BallInstance && editorObject.getAttribute("id").stringValue().equals(potential)) {
                        return true;
                    }
                }
                return false;
            }
            case UNIQUE_GOOBALL_ID -> {
                for (EditorObject editorObject : LevelManager.getLevel().getLevel()) {
                    if (editorObject instanceof BallInstance && editorObject != object && editorObject.getAttribute("id").stringValue().equals(potential)) {
                        return false;
                    }
                }
                return true;
            }
            case GEOMETRY -> {
                for (EditorObject editorObject : LevelManager.getLevel().getScene()) {
                    if (editorObject instanceof Rectangle || editorObject instanceof Circle || editorObject instanceof Compositegeom) {
                        if (editorObject.getAttribute("id").stringValue().equals(potential)) {
                            return true;
                        }
                    }
                }
                return false;
            }
            case CONTEXT -> {
                return potential.equals("screen");
            }
            case FONT -> {
                return true;
            }
            case IMAGE_PATH -> {
                String imageGameDir = LevelManager.getLevel().getVersion() == GameVersion.OLD ? FileManager.getOldWOGdir() : FileManager.getNewWOGdir();
                if (object instanceof ResrcImage resrcImage) {
                    return new File(imageGameDir + "\\" + resrcImage.getAdjustedPath() + ".png").exists();
                } else return false;
            }
            case SOUND_PATH -> {
                String soundGameDir = LevelManager.getLevel().getVersion() == GameVersion.OLD ? FileManager.getOldWOGdir() : FileManager.getNewWOGdir();
                if (object instanceof Sound sound) {
                    return new File(soundGameDir + "\\" + sound.getAdjustedPath() + ".ogg").exists();
                } else return false;
            }
            default -> {
                return false;
            }
        }
    }


    public static double getRange(String range, double randomPercentage) {
        double min;
        double max;
        if (range.contains(",")){
            min = Double.parseDouble(range.substring(0, range.indexOf(",")));
            max = Double.parseDouble(range.substring(range.indexOf(",") + 1));
        } else {
            min = Double.parseDouble(range);
            max = min;
        }
        return (max - min) * randomPercentage + min;
    }

}
