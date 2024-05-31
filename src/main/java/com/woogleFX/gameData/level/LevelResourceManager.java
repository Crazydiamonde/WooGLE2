package com.woogleFX.gameData.level;

import com.woogleFX.editorObjects.objectCreators.ObjectCreator;
import com.woogleFX.file.FileManager;
import com.woogleFX.editorObjects.objectCreators.ObjectAdder;
import com.woogleFX.file.resourceManagers.ResourceManager;
import com.woogleFX.editorObjects.ObjectManager;
import com.woogleFX.engine.undoHandling.UndoManager;
import com.woogleFX.engine.gui.Alarms;
import com.woogleFX.editorObjects.EditorObject;
import com.woogleFX.engine.undoHandling.userActions.ObjectDestructionAction;
import com.woogleFX.engine.undoHandling.userActions.ObjectCreationAction;
import com.woogleFX.gameData.level.levelOpening.LevelLoader;
import com.worldOfGoo.resrc.Font;
import com.worldOfGoo.resrc.ResrcImage;
import com.worldOfGoo.resrc.Sound;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.*;

public class LevelResourceManager {

    private static final Logger logger = LoggerFactory.getLogger(LevelLoader.class);


    public static void deleteResource(_Level level, String file) {
        String path = new File(file).getName();
        String startPath = FileManager.getGameDir(level.getVersion());
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


    public static void updateLevelResources(_Level level) {
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


    /** Creates a new text resource in the given level. */
    public static void newTextResource(_Level level) {

        EditorObject newTextObject = ObjectCreator.create("string", level.getTextObject(), level.getVersion());
        ObjectAdder.fixString(newTextObject);

        level.getText().add(newTextObject);

        level.setSelected(new EditorObject[]{ newTextObject });

        int childIndex = level.getTextObject().getChildren().indexOf(newTextObject);
        UndoManager.registerChange(new ObjectCreationAction(newTextObject, childIndex));

    }


    private static boolean isResourceUsed(EditorObject resource, _Level level) {
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
    public static void cleanLevelResources(_Level level) {

        ArrayList<EditorObject> unused = new ArrayList<>();

        for (EditorObject editorObject : level.getResrc()) {
            if (editorObject instanceof ResrcImage || editorObject instanceof Sound || editorObject instanceof Font) {
                if (!isResourceUsed(editorObject, level)) unused.add(editorObject);
            }
        }

        if (!unused.isEmpty()) Alarms.confirmCleanResourcesMessage(level, unused);

    }


    public static void confirmedCleanLevelResources(_Level level, ArrayList<EditorObject> toClean) {
        ArrayList<ObjectDestructionAction> objectDestructionActions = new ArrayList<>();
        for (EditorObject object : toClean) {
            int childIndex = object.getParent().getChildren().indexOf(object);
            objectDestructionActions.add(new ObjectDestructionAction(object, childIndex));
            ObjectManager.deleteItem(level, object, false);
        }
        UndoManager.registerChange(objectDestructionActions.toArray(new ObjectDestructionAction[0]));
    }

}
