package com.woogleFX.editorObjects.attributes;

import com.woogleFX.assets.AssetLoader;
import com.woogleFX.assets.wog1.animation.WOG1Animation;
import com.woogleFX.assets.wog1.ball.WOG1Ball;
import com.woogleFX.assets.wog1.level.WOG1Level;
import com.woogleFX.assets.wog1.particle.WOG1Particle;
import com.woogleFX.editorObjects.EditorObject;
import com.woogleFX.file.FileManager;
import com.woogleFX.file.resourceManagers.ResourceManager;
import com.worldOfGoo.ball.part;
import com.worldOfGoo.level.BallInstance;
import com.worldOfGoo.resrc.Image;
import com.worldOfGoo.resrc.ResourceInterface;
import com.worldOfGoo.resrc.Sound;
import com.worldOfGoo.scene.circle;
import com.worldOfGoo.scene.compositegeom;
import com.worldOfGoo.scene.rectangle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public enum InputField {

    // World of Goo 1

    // Data types
    STRING {
        public boolean verify(EditorObject object, String s, boolean required) {
            return true;
        }
    },
    BOOLEAN {
        public boolean verify(EditorObject object, String s, boolean required) {
            return true;
        }
    },

    _1_NUMBER {
        public boolean verify(EditorObject object, String s, boolean required) {
            return true;
        }
    },
    _1_LIST_NUMBER {
        public boolean verify(EditorObject object, String s, boolean required) {
            return true;
        }
    },
    _1_BALL_CONTAINS {
        public boolean verify(EditorObject object, String s, boolean required) {
            return true;
        }
    },
    _1_BALL_SPAWN {
        public boolean verify(EditorObject object, String s, boolean required) {
            return true;
        }
    },
    _1_ATTENUATION {
        public boolean verify(EditorObject object, String s, boolean required) {
            return true;
        }
    },
    _1_INTEGER_NON_NEGATIVE {
        public boolean verify(EditorObject object, String s, boolean required) {
            return true;
        }
    },
    _1_NUMBER_NON_NEGATIVE {
        public boolean verify(EditorObject object, String s, boolean required) {
            return true;
        }
    },
    _1_NUMBER_POSITIVE {
        public boolean verify(EditorObject object, String s, boolean required) {
            return true;
        }
    },
    _1_POSITION {
        public boolean verify(EditorObject object, String s, boolean required) {
            return true;
        }
    },
    _1_COLOR {
        public boolean verify(EditorObject object, String s, boolean required) {
            return true;
        }
    },
    FILE_PATH {
        public boolean verify(EditorObject object, String s, boolean required) {
            if (!(object instanceof ResourceInterface resourceInterface)) return false;
            String path = (resourceInterface.getSetDefaults() == null ? "" : resourceInterface.getSetDefaults().getAttribute("path").stringValue());
            if (path.equals("./")) path = "";
            Path parentPath = Path.of(FileManager.getGameDir(object.getVersion()) + "/" + path + s + ".temp").getParent();
            if (!Files.exists(parentPath)) return false;
            File[] children = new File(parentPath.toString()).listFiles();
            if (children == null) return false;
            for (File child : children) if (child.isFile()) {
                String formattedPath = child.getPath().substring(FileManager.getGameDir(object.getVersion()).length(), child.getPath().lastIndexOf('.'));
                if (formattedPath.startsWith("\\."))
                    formattedPath = formattedPath.substring(2);
                if (formattedPath.startsWith("\\"))
                    formattedPath = formattedPath.substring(1);
                formattedPath = formattedPath.replace('\\', '/');
                if (formattedPath.equals(path + s))
                    return true;
            }
            return false;
        }
        public String[] getPossibleValues(EditorAttribute attribute) {
            List<String> possibleValues = new ArrayList<>();
            if (!(attribute.getObject() instanceof ResourceInterface resourceInterface)) return new String[0];
            String path = (resourceInterface.getSetDefaults() == null ? "" : resourceInterface.getSetDefaults().getAttribute("path").stringValue());
            if (path.equals("./")) path = "";
            File[] files = attribute.getObject().getAsset().getFile().listFiles();
            if (files == null) return new String[0];
            for (File child : files) {
                if (attribute.getObject() instanceof Image) {
                    try {
                        if (ImageIO.read(child) == null) continue;
                    } catch (IOException e) {
                        continue;
                    }
                }
                String childName = child.getPath().substring(FileManager.getGameDir(attribute.getObject().getVersion()).length() + 1, child.getPath().lastIndexOf("."));
                childName = childName.replace('\\', '/');
                possibleValues.add(childName.substring(path.length()));
            }
            return possibleValues.toArray(String[]::new);
        }
        public void onDoubleClick(EditorObject object, String s) {
            if (!(object instanceof ResourceInterface resourceInterface)) return;
            String path = (resourceInterface.getSetDefaults() == null ? "" : resourceInterface.getSetDefaults().getAttribute("path").stringValue());
            if (path.equals("./")) path = "";
            Path parentPath = Path.of(FileManager.getGameDir(object.getVersion()) + "/" + path + s + ".temp").getParent();
            if (!Files.exists(parentPath)) return;
            File[] children = new File(parentPath.toString()).listFiles();
            if (children == null) return;
            for (File child : children) if (child.isFile()) {
                String formattedPath = child.getPath().substring(FileManager.getGameDir(object.getVersion()).length(), child.getPath().lastIndexOf('.'));
                if (formattedPath.startsWith("\\."))
                    formattedPath = formattedPath.substring(2);
                if (formattedPath.startsWith("\\"))
                    formattedPath = formattedPath.substring(1);
                formattedPath = formattedPath.replace('\\', '/');
                if (formattedPath.equals(path + s)) {
                    try {
                        Desktop.getDesktop().browse(child.getParentFile().toURI());
                    } catch (IOException e) {
                        logger.error("", e);
                    }
                }
            }
        }
        public boolean hasSpecialClickBehavior(EditorObject object, String s) {
            return verify(object, s, true);
        }
    },


    _1_PART {
        public boolean verify(EditorObject object, String s, boolean required) {
            return true;
        }
        public void onDoubleClick(EditorObject object, String s) {
            if (!(object.getAsset() instanceof WOG1Ball ball)) return;
            for (EditorObject object1 : ball.getBall().getChildren()) {
                if (object1 instanceof part && object1.getAttribute("name").stringValue().equals(s.split(",")[0])) {
                    ball.setSelectedLoudly(new EditorObject[]{ object1 });
                    return;
                }
            }
        }
        public boolean hasSpecialClickBehavior(EditorObject object, String s) {
            if (!(object.getAsset() instanceof WOG1Ball ball)) return false;
            for (String s_ : s.split(",")) {
                boolean ok = false;
                for (EditorObject object1 : ball.getBall().getChildren()) {
                    if (object1 instanceof part && object1.getAttribute("name").stringValue().equals(s_)) {
                        ok = true;
                        break;
                    }
                }
                if (!ok) return false;
            }
            return true;
        }
    },

    // Level objects
    _1_GOOBALL_ID {
        public boolean verify(EditorObject object, String s, boolean required) {
            if (!(object.getAsset() instanceof WOG1Level level)) return false;
            for (EditorObject object1 : level.getLevel().getChildren()) {
                if (object1 instanceof BallInstance && object1.getAttribute("id").stringValue().equals(s)) {
                    return true;
                }
            }
            return false;
        }
        public void onDoubleClick(EditorObject object, String s) {
            if (!(object.getAsset() instanceof WOG1Level level)) return;
            for (EditorObject object1 : level.getLevel().getChildren()) {
                if (object1 instanceof BallInstance && object1.getAttribute("id").stringValue().equals(s)) {
                    level.setSelectedLoudly(new EditorObject[]{ object1 });
                    return;
                }
            }
        }
        public boolean hasSpecialClickBehavior(EditorObject object, String s) {
            return true;
        }
    },
    _1_UNIQUE_GOOBALL_ID {
        public boolean verify(EditorObject object, String s, boolean required) {
            return true;
        }
    },
    _1_IMAGE {
        public boolean verify(EditorObject object, String s, boolean required) {
            if ((s == null || s.isEmpty()) && !required) return true;
            if (s == null) return false;
            for (String s_ : s.split(",")) {
                if (ResourceManager.getImage(object.getAsset().getResources(), s_, object.getVersion()) == null) {
                    return false;
                }
            }
            return true;
        }
        public String[] getPossibleValues(EditorAttribute attribute) {
            List<String> possibleValues = new ArrayList<>();
            for (EditorObject resource : attribute.getObject().getAsset().getResources().getChildren()) {
                if (resource instanceof Image image) {
                    possibleValues.add(image.getAdjustedID());
                }
            }
            return possibleValues.toArray(String[]::new);
        }
        public void onDoubleClick(EditorObject object, String s) {
            ArrayList<EditorObject> toSelect = new ArrayList<>();
            for (String s_ : s.split(",")) {
                for (EditorObject resource : object.getAsset().getResources().getChildren()) {
                    if (resource instanceof Image image && image.getAdjustedID().equals(s_)) {
                        toSelect.add(resource);
                        break;
                    }
                }
            }
            object.getAsset().setSelectedLoudly(toSelect.toArray(EditorObject[]::new));
        }
        public boolean hasSpecialClickBehavior(EditorObject object, String s) {
            for (String s_ : s.split(",")) {
                for (EditorObject resource : object.getAsset().getResources().getChildren()) {
                    if (resource instanceof Image image && image.getAdjustedID().equals(s_)) {
                        return true;
                    }
                }
            }
            return false;
        }
    },
    _1_SOUND {
        public boolean verify(EditorObject object, String s, boolean required) {
            if (s == null || s.isEmpty()) return true;
            for (String s_ : s.split(",")) {
                if (ResourceManager.findResource(object.getAsset().getResources(), s_, object.getVersion()) == null) {
                    return false;
                }
            }
            return true;
        }
        public String[] getPossibleValues(EditorAttribute attribute) {
            List<String> possibleValues = new ArrayList<>();
            for (EditorObject resource : attribute.getObject().getAsset().getResources().getChildren()) {
                if (resource instanceof Sound sound) {
                    possibleValues.add(sound.getAdjustedID());
                }
            }
            return possibleValues.toArray(String[]::new);
        }
        public void onDoubleClick(EditorObject object, String s) {
            ArrayList<EditorObject> toSelect = new ArrayList<>();
            for (String s_ : s.split(",")) {
                for (EditorObject resource : object.getAsset().getResources().getChildren()) {
                    if (resource instanceof Sound sound && sound.getAdjustedID().equals(s_)) {
                        toSelect.add(resource);
                        break;
                    }
                }
            }
            object.getAsset().setSelectedLoudly(toSelect.toArray(EditorObject[]::new));
        }
        public boolean hasSpecialClickBehavior(EditorObject object, String s) {
            if (object.getAsset() == null) return false; // ??????
            for (String s_ : s.split(",")) {
                for (EditorObject resource : object.getAsset().getResources().getChildren()) {
                    if (resource instanceof Sound sound && sound.getAdjustedID().equals(s_)) {
                        return true;
                    }
                }
            }
            return false;
        }
    },
    _1_GEOMETRY {
        public boolean verify(EditorObject object, String s, boolean required) {
            if ((s == null || s.isEmpty()) && !required) return true;
            if (!(object.getAsset() instanceof WOG1Level level)) return false;
            for (EditorObject object1 : level.getScene().getChildren()) {
                if ((object1 instanceof rectangle || object1 instanceof circle || object1 instanceof compositegeom) && object1.getAttribute("id").stringValue().equals(s)) {
                    return true;
                }
            }
            return false;
        }
        public void onDoubleClick(EditorObject object, String s) {
            if (!(object.getAsset() instanceof WOG1Level level)) return;
            for (EditorObject object1 : level.getScene().getChildren()) {
                if ((object1 instanceof rectangle || object1 instanceof circle || object1 instanceof compositegeom) && object1.getAttribute("id").stringValue().equals(s)) {
                    level.setSelectedLoudly(new EditorObject[]{ object1 });
                    return;
                }
            }
        }
        public boolean hasSpecialClickBehavior(EditorObject object, String s) {
            return true;
        }
    },


    _1_ANIMATION {
        public boolean verify(EditorObject object, String s, boolean required) {
            if ((s == null || s.isEmpty()) && !required) return true;
            return WOG1Animation.assetSelector.getItems(object.getVersion()).contains(s);
        }
        public String[] getPossibleValues(EditorAttribute attribute) {
            return WOG1Animation.assetSelector.getItems(attribute.getObject().getVersion()).toArray(String[]::new);
        }
        public boolean hasSpecialClickBehavior(EditorObject object, String s) {
            return true;
        }
        public void onDoubleClick(EditorObject object, String s) {
            if (WOG1Animation.assetSelector.getItems(object.getVersion()).contains(s))
                AssetLoader.openAsset(WOG1Animation.assetSelector, null, s, object.getVersion());
        }
    },
    _1_RANGE {
        public boolean verify(EditorObject object, String s, boolean required) {
            return true;
        }
    },
    _1_BALL_SHAPE {
        public boolean verify(EditorObject object, String s, boolean required) {
            return true;
        }
    },
    _1_MATERIAL {
        public boolean verify(EditorObject object, String s, boolean required) {
            return true;
        }
    },
    _1_TAG {
        public boolean verify(EditorObject object, String s, boolean required) {
            return true;
        }
    },
    _1_TEXT {
        public boolean verify(EditorObject object, String s, boolean required) {
            for (EditorObject object1 : object.getAsset().getStrings().getChildren()) {
                if (object1.getAttribute("id").stringValue().equals(s)) {
                    return true;
                }
            }
            return false;
        }
        public void onDoubleClick(EditorObject object, String s) {
            for (EditorObject object1 : object.getAsset().getStrings().getChildren()) {
                if (object1.getAttribute("id").stringValue().equals(s)) {
                    object.getAsset().setSelectedLoudly(new EditorObject[]{ object1 });
                    return;
                }
            }
        }
        public boolean hasSpecialClickBehavior(EditorObject object, String s) {
            return verify(object, s, true);
        }
    },
    _1_PARTICLES {
        public boolean verify(EditorObject object, String s, boolean required) {
            if ((s == null || s.isEmpty()) && !required) return true;
            return WOG1Particle.assetSelector.getItems(object.getVersion()).contains(s);
        }
        public String[] getPossibleValues(EditorAttribute attribute) {
            return WOG1Particle.assetSelector.getItems(attribute.getObject().getVersion()).toArray(String[]::new);
        }
        public boolean hasSpecialClickBehavior(EditorObject object, String s) {
            return true;
        }
        public void onDoubleClick(EditorObject object, String s) {
            if (WOG1Particle.assetSelector.getItems(object.getVersion()).contains(s))
                AssetLoader.openAsset(WOG1Particle.assetSelector, null, s, object.getVersion());
        }
    },
    _1_BALL {
        public boolean verify(EditorObject object, String s, boolean required) {
            if ((s == null || s.isEmpty()) && !required) return true;
            return WOG1Ball.assetSelector.getItems(object.getVersion()).contains(s);
        }
        public String[] getPossibleValues(EditorAttribute attribute) {
            return WOG1Ball.assetSelector.getItems(attribute.getObject().getVersion()).toArray(String[]::new);
        }
        public void onDoubleClick(EditorObject object, String s) {
            if (WOG1Ball.assetSelector.getItems(object.getVersion()).contains(s))
                AssetLoader.openAsset(WOG1Ball.assetSelector, null, s, object.getVersion());
        }
        public boolean hasSpecialClickBehavior(EditorObject object, String s) {
            return true;
        }
    },
    _1_OCD_TYPE {
        public boolean verify(EditorObject object, String s, boolean required) {
            return true;
        }
    },
    _1_IMAGE_TYPE {
        public boolean verify(EditorObject object, String s, boolean required) {
            return true;
        }
    },
    _1_IMAGE_PATH {
        public boolean verify(EditorObject object, String s, boolean required) {
            return true;
        }
    },
    _1_SOUND_PATH {
        public boolean verify(EditorObject object, String s, boolean required) {
            return true;
        }
    },
    _1_FONT {
        public boolean verify(EditorObject object, String s, boolean required) {
            return true;
        }
    },
    _1_CONTEXT {
        public boolean verify(EditorObject object, String s, boolean required) {
            return true;
        }
    },
    _1_PIPE_TYPE {
        public boolean verify(EditorObject object, String s, boolean required) {
            return true;
        }
    },
    _1_ASPECT {
        public boolean verify(EditorObject object, String s, boolean required) {
            return true;
        }
    },

    NUMBER {
        public boolean verify(EditorObject object, String s, boolean required) {
            return true;
        }
    },
    _2_LEVEL_TYPE {
        public boolean verify(EditorObject object, String s, boolean required) {
            return true;
        }
    },
    _2_UUID {
        public boolean verify(EditorObject object, String s, boolean required) {
            return true;
        }
    },
    _2_UID {
        public boolean verify(EditorObject object, String s, boolean required) {
            return true;
        }
    },
    _2_ISLAND_ID {
        public boolean verify(EditorObject object, String s, boolean required) {
            return true;
        }
    },
    _2_OBJECT {
        public boolean verify(EditorObject object, String s, boolean required) {
            return true;
        }
    },
    _2_LIST_STRING {
        public boolean verify(EditorObject object, String s, boolean required) {
            return true;
        }
    },
    _2_LIST_NUMBER {
        public boolean verify(EditorObject object, String s, boolean required) {
            return true;
        }
    },
    _2_BALL_TYPE {
        public boolean verify(EditorObject object, String s, boolean required) {
            return true;
        }
    },
    _2_BALL_TYPE_USERVAR {
        public boolean verify(EditorObject object, String s, boolean required) {
            return true;
        }
    },
    _2_TERRAIN_GROUP_TYPE_INDEX {
        public boolean verify(EditorObject object, String s, boolean required) {
            return true;
        }
    },
    _2_TERRAIN_GROUP {
        public boolean verify(EditorObject object, String s, boolean required) {
            return true;
        }
    },
    _2_ITEM_TYPE {
        public boolean verify(EditorObject object, String s, boolean required) {
            return true;
        }
    },
    _2_SKIN {
        public boolean verify(EditorObject object, String s, boolean required) {
            return true;
        }
    },
    _2_GAME_LEVEL {
        public boolean verify(EditorObject object, String s, boolean required) {
            return true;
        }
    },
    _2_SOUND_ID {
        public boolean verify(EditorObject object, String s, boolean required) {
            return true;
        }
    },
    _2_MUSIC_ID {
        public boolean verify(EditorObject object, String s, boolean required) {
            return true;
        }
    },
    _2_AMBIENCE_ID {
        public boolean verify(EditorObject object, String s, boolean required) {
            return true;
        }
    },
    _2_BALL_UID {
        public boolean verify(EditorObject object, String s, boolean required) {
            return true;
        }
    },
    _2_STRAND_TYPE {
        public boolean verify(EditorObject object, String s, boolean required) {
            return true;
        }
    },
    _2_ENVIRONMENT_ID {
        public boolean verify(EditorObject object, String s, boolean required) {
            return true;
        }
    },
    _2_BACKGROUND_ID {
        public boolean verify(EditorObject object, String s, boolean required) {
            return true;
        }
    },
    _2_LIQUID_TYPE {
        public boolean verify(EditorObject object, String s, boolean required) {
            return true;
        }
    },
    _2_PARTICLE_EFFECT_NAME {
        public boolean verify(EditorObject object, String s, boolean required) {
            return true;
        }
    },
    _2_COLLISION_GROUP {
        public boolean verify(EditorObject object, String s, boolean required) {
            return true;
        }
    },

    _2_CHILD {
        public boolean verify(EditorObject object, String s, boolean required) {
            return true;
        }
    },
    _2_CHILD_HIDDEN {
        public boolean verify(EditorObject object, String s, boolean required) {
            return true;
        }
    },
    _2_LIST_CHILD {
        public boolean verify(EditorObject object, String s, boolean required) {
            return true;
        }
    },
    _2_LIST_CHILD_HIDDEN {
        public boolean verify(EditorObject object, String s, boolean required) {
            return true;
        }
    },

    _2_TERRAIN_GROUP_TYPE {
        public boolean verify(EditorObject object, String s, boolean required) {
            return true;
        }
    };

    private static final Logger logger = LoggerFactory.getLogger(InputField.class);


    public abstract boolean verify(EditorObject object, String s, boolean required);

    public void onDoubleClick(EditorObject object, String s) {

    }
    public boolean hasSpecialClickBehavior(EditorObject object, String s) {
        return false;
    }

    public String[] getPossibleValues(EditorAttribute attribute) {
        return new String[0];
    }


    public static double getRange(String range, double randomPercentage) {
        double min;
        double max;
        if (range.contains(",")) {
            min = Double.parseDouble(range.substring(0, range.indexOf(",")));
            max = Double.parseDouble(range.substring(range.indexOf(",") + 1).replace(",", ""));
        } else {
            min = Double.parseDouble(range);
            max = min;
        }
        return (max - min) * randomPercentage + min;
    }

}
