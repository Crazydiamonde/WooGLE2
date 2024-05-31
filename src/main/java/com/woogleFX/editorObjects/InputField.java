package com.woogleFX.editorObjects;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;

import com.woogleFX.file.BaseGameResources;
import com.woogleFX.file.FileManager;
import com.woogleFX.file.resourceManagers.AnimationManager;
import com.woogleFX.file.resourceManagers.ResourceManager;
import com.woogleFX.functions.LevelManager;
import com.woogleFX.file.resourceManagers.ParticleManager;
import com.woogleFX.structures.simpleStructures.Color;
import com.woogleFX.structures.simpleStructures.Position;
import com.worldOfGoo.level.BallInstance;
import com.worldOfGoo.particle.Ambientparticleeffect;
import com.worldOfGoo.particle.Particleeffect;
import com.worldOfGoo.resrc.ResrcImage;
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

            case ANY, ANY_REQUIRED -> {
                return true;
            }

            case NUMBER, NUMBER_POSITIVE, NUMBER_NON_NEGATIVE, POSITION, COLOR, FLAG -> {
                return verifyDataType(type, potential);
            }

            case GOOBALL_ID, UNIQUE_GOOBALL_ID, IMAGE, IMAGE_REQUIRED, GEOMETRY -> {
                return verifyLevelObject(object, type, potential);
            }

            case ANIMATION, TEXT, BALL, PARTICLES, MATERIAL, TAG, FONT -> {
                return verifyResource(object, type, potential);
            }

            case IMAGE_PATH, SOUND_PATH -> {
                return verifyFilePath(object, type, potential);
            }

            case OCD_TYPE, CONTEXT -> {
                return verifyGameValue(type, potential);
            }

            default -> {
                return false;
            }

        }
    }


    private static boolean verifyDataType(InputField type, String potential) {

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

            case POSITION -> {
                try {
                    Position.parse(potential);
                    return true;
                } catch (NumberFormatException e) {
                    return false;
                }
            }

            case COLOR -> {
                try {
                    Color.parse(potential);
                    return true;
                } catch (Exception e) {
                    return false;
                }
            }

            case FLAG -> {
                return potential.equals("true") || potential.equals("false");
            }

            default -> {
                return false;
            }

        }

    }


    private static boolean verifyLevelObject(EditorObject object, InputField type, String potential) {

        switch (type) {

            case GOOBALL_ID -> {
                for (EditorObject ball : LevelManager.getLevel().getLevel())
                    if (ball instanceof BallInstance &&
                            ball.getAttribute("id").stringValue().equals(potential)) return true;
                return false;
            }

            case UNIQUE_GOOBALL_ID -> {
                for (EditorObject ball : LevelManager.getLevel().getLevel())
                    if (ball instanceof BallInstance && ball != object &&
                            ball.getAttribute("id").stringValue().equals(potential)) return false;
                return true;
            }

            case IMAGE, IMAGE_REQUIRED -> {
                for (EditorObject resrc : LevelManager.getLevel().getResrc())
                    if (resrc instanceof ResrcImage image &&
                            image.getAttribute("id").stringValue().equals(potential)) return true;
                return false;
            }

            case GEOMETRY -> {
                for (EditorObject editorObject : LevelManager.getLevel().getScene()) {
                    if (editorObject instanceof Rectangle ||
                        editorObject instanceof Circle ||
                        editorObject instanceof Compositegeom) {
                        if (editorObject.getAttribute("id").stringValue().equals(potential)) {
                            return true;
                        }
                    }
                }
                return false;
            }

            default -> {
                return false;
            }

        }

    }


    private static boolean verifyResource(EditorObject object, InputField type, String potential) {

        switch (type) {

            case ANIMATION -> {
                return AnimationManager.hasAnimation(potential);
            }

            case BALL -> {
                String dir = FileManager.getGameDir(object.getVersion());
                File[] ballFiles = new File(dir + "\\res\\balls").listFiles();
                if (ballFiles == null) return false;
                for (File ballFile : ballFiles) {
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
                    if ((particle instanceof Particleeffect || particle instanceof Ambientparticleeffect) &&
                            particle.getAttribute("name").stringValue().equals(potential)) {
                        return true;
                    }
                }
                return false;
            }

            case MATERIAL -> {
                try {
                    ResourceManager.getMaterial(null, potential, object.getVersion());
                    return true;
                } catch (FileNotFoundException ignored) {
                    return false;
                }
            }

            case TAG -> {
                return Arrays.stream(potential.split(",")).allMatch(BaseGameResources.TAGS::contains);
            }

            case FONT -> {
                try {
                    ResourceManager.getFont(null, potential, object.getVersion());
                    return true;
                } catch (FileNotFoundException ignored) {
                    return false;
                }
            }

            default -> {
                return false;
            }

        }

    }


    private static boolean verifyFilePath(EditorObject object, InputField type, String potential) {

        switch (type) {

            case IMAGE_PATH -> {
                if (object instanceof ResrcImage resrcImage) {
                    String path = resrcImage.getAttribute("path").stringValue();
                    String adjustedPath = resrcImage.getAdjustedPath();
                    String setDefaultsPart = adjustedPath.substring(0, adjustedPath.length() - path.length());
                    String dir = FileManager.getGameDir(resrcImage.getVersion());
                    return new File(dir + "\\" + setDefaultsPart + potential + ".png").exists();
                } else return false;
            }

            case SOUND_PATH -> {
                if (object instanceof Sound sound) {
                    String path = sound.getAttribute("path").stringValue();
                    String adjustedPath = sound.getAdjustedPath();
                    String setDefaultsPart = adjustedPath.substring(0, adjustedPath.length() - path.length());
                    String dir = FileManager.getGameDir(sound.getVersion());
                    return new File(dir + "\\" + setDefaultsPart + potential + ".ogg").exists();
                } else return false;
            }

            default -> {
                return false;
            }

        }

    }


    private static boolean verifyGameValue(InputField type, String potential) {

        switch (type) {

            case OCD_TYPE -> {
                return potential.equals("balls") || potential.equals("moves") || potential.equals("time");
            }

            case CONTEXT -> {
                return potential.equals("screen");
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
