package com.WooGLEFX.Structures;

import java.io.File;

import com.WooGLEFX.Engine.Main;
import com.WooGLEFX.File.FileManager;
import com.WooGLEFX.Functions.AnimationManager;
import com.WooGLEFX.Functions.LevelManager;
import com.WooGLEFX.Functions.ParticleManager;
import com.WooGLEFX.Structures.SimpleStructures.Color;
import com.WooGLEFX.Structures.SimpleStructures.Position;
import com.WorldOfGoo.Level.BallInstance;
import com.WorldOfGoo.Particle.Ambientparticleeffect;
import com.WorldOfGoo.Particle.Particleeffect;
import com.WorldOfGoo.Scene.Circle;
import com.WorldOfGoo.Scene.Compositegeom;
import com.WorldOfGoo.Scene.Rectangle;

public class InputField {

    public static final int NULL = -1;
    public static final int ANY = 0;
    public static final int ANY_REQUIRED = 1;
    public static final int NUMBER = 2;
    public static final int NUMBER_NON_NEGATIVE = 3;
    public static final int NUMBER_POSITIVE = 4;
    public static final int NUMBER_ZERO_TO_ONE_INCLUSIVE = 5;
    public static final int NUMBER_ZERO_TO_ONE_EXCLUSIVE = 6;
    public static final int POSITION = 7;
    public static final int IMAGE = 8;
    public static final int IMAGE_REQUIRED = 9;
    public static final int COLOR = 10;
    public static final int ANIMATION = 11;
    public static final int FLAG = 12;
    public static final int RANGE = 13;
    public static final int MATERIAL = 14;
    public static final int TAG = 15;
    public static final int GEOMETRY = 16;
    public static final int TEXT = 17;
    public static final int PARTICLES = 18;
    public static final int BALL = 19;
    public static final int OCD_TYPE = 20;
    public static final int IMAGE_TYPE = 21;
    public static final int GOOBALL_ID = 21;
    public static final int UNIQUE_GOOBALL_ID = 22;
    public static final int IMAGE_PATH = 23;
    public static final int SOUND_PATH = 24;
    //TODO make imagepath and soundpath actual field types and make verifying them possible which is hard because of setdefaults

    public boolean required;

    public boolean requiredInSave = false;

    private final String name;
    private final int type;

    public String getName() {
        return name;
    }

    public int getType() {
        return type;
    }

    public InputField(String name, int type) {
        this.name = name;
        this.type = type;
        this.required = false;
    }

    public InputField(String name, int type, boolean required) {
        this.name = name;
        this.type = type;
        this.required = required;
    }

    public InputField(boolean requiredInSave, String name, int type) {
        this.name = name;
        this.type = type;
        this.requiredInSave = requiredInSave;
    }

    public boolean verify(EditorObject object, String potential) {
        if (potential.equals("")){
            return !required && !(type == ANY_REQUIRED || type == IMAGE_REQUIRED);
        }
        switch (this.type) {
            case NUMBER:
                try {
                    Double.parseDouble(potential);
                    return true;
                } catch (NumberFormatException e) {
                    return false;
                }
            case NUMBER_POSITIVE:
                try {
                    return Double.parseDouble(potential) > 0;
                } catch (NumberFormatException e) {
                    return false;
                }
            case NUMBER_NON_NEGATIVE:
                try {
                    return Double.parseDouble(potential) >= 0;
                } catch (NumberFormatException e) {
                    return false;
                }
            case ANY, ANY_REQUIRED:
                return true;
            case POSITION:
                try {
                    Position.parse(potential);
                    return true;
                } catch (NumberFormatException e) {
                    return false;
                }
            case IMAGE, IMAGE_REQUIRED:
                for (EditorObject resrc : object.getLevel().getResources()) {
                    if (resrc.getAttribute("id") != null && resrc.getAttribute("id").equals(potential)) {
                        return true;
                    }
                }
                return false;
            case COLOR:
                try {
                    Color.parse(potential);
                    return true;
                } catch (NumberFormatException e) {
                    return false;
                }
            case ANIMATION:
                return AnimationManager.hasAnimation(potential);
            case FLAG:
                return potential.equals("true") || potential.equals("false");
            case BALL:
                String path = object.getLevel().getVersion() == 1.5 ? FileManager.getNewWOGdir() : FileManager.getOldWOGdir();
                for (File ballFile : new File(path + "\\res\\balls").listFiles()) {
                    if (ballFile.getName().equals(potential)) {
                        return true;
                    }
                }
                return false;
            case TEXT:
                return true;
            case PARTICLES:
                for (EditorObject particle : ParticleManager.getParticles()) {
                    if ((particle instanceof Particleeffect || particle instanceof Ambientparticleeffect) && particle.getAttribute("name").equals(potential)) {
                        return true;
                    }
                }
                return false;
            case MATERIAL:
                return true;
            case TAG:
                return true;
            case OCD_TYPE:
                return potential.equals("balls") || potential.equals("moves") || potential.equals("time");
            case GOOBALL_ID:
                for (EditorObject editorObject : LevelManager.getLevel().getLevel()) {
                    if (editorObject instanceof BallInstance && editorObject.getAttribute("id").equals(potential)) {
                        return true;
                    }
                }
                return false;
            case UNIQUE_GOOBALL_ID:
                for (EditorObject editorObject : LevelManager.getLevel().getLevel()) {
                    if (editorObject instanceof BallInstance && editorObject != object && editorObject.getAttribute("id").equals(potential)) {
                        return false;
                    }
                }
                return true;
            case GEOMETRY:
                for (EditorObject editorObject : LevelManager.getLevel().getScene()) {
                    if (editorObject instanceof Rectangle || editorObject instanceof Circle || editorObject instanceof Compositegeom) {
                        if (editorObject.getAttribute("id").equals(potential)) {
                            return true;
                        }
                    }
                }
                return false;
            default:
                return false;
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
