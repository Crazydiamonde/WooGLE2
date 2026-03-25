package com.woogleFX.file.resourceManagers;

import com.woogleFX.editorObjects.EditorObject;
import com.woogleFX.assets.Asset;
import com.woogleFX.editorObjects.ObjectUtil;
import com.woogleFX.editorObjects.ResourceUser;
import com.woogleFX.editorObjects.attributes.EditorAttribute;
import com.woogleFX.editorObjects.attributes.InputField;
import com.woogleFX.engine.gui.alarms.ConfirmCleanResourcesAlarm;
import com.woogleFX.engine.gui.alarms.LoadingResourcesAlarm;
import com.woogleFX.file.FileManager;
import com.woogleFX.file.resourceManagers.ResourceManager;
import com.woogleFX.editorObjects.ObjectManager;
import com.woogleFX.engine.undoHandling.UndoManager;
import com.woogleFX.engine.undoHandling.userActions.ObjectDestructionAction;
import com.woogleFX.assets.AssetLoader;
import com.worldOfGoo.resrc.ResourceInterface;
import com.worldOfGoo.resrc.font;
import com.worldOfGoo.resrc.Image;
import com.worldOfGoo.resrc.Sound;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.*;

public class LevelResourceManager {

    private static final Logger logger = LoggerFactory.getLogger(AssetLoader.class);


    public static void deleteResource(Asset level, String file) {
        String path = new File(file).getName();
        String startPath = FileManager.getGameDir(level.getVersion());
        File levelFile = new File(startPath + "/res/levels/" + level.getName());
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


    public static void updateLevelResources(Asset level) {
        StringBuilder failedToLoad = new StringBuilder();

        /* Loop through all the images in the level's resources */
        for (EditorObject EditorObject : level.getResources().getChildren()) {
            if (EditorObject instanceof Image image) {
                if (!ResourceManager.updateResource(image, level.getVersion())) {
                    failedToLoad.append(EditorObject.getAttribute("id").stringValue()).append("\n");
                    logger.error("Failed to load resource: " + EditorObject.getAttribute("id").stringValue());
                }
            }
        }

        /* Update every object in the level */
        /* I hope this doesn't break anything */
        for (EditorObject EditorObject : level.getObjects()) {
            EditorObject.update();
        }

        if (!failedToLoad.toString().isEmpty()) {
            LoadingResourcesAlarm.show(failedToLoad.toString());
        }

    }


    /** Creates a new text resource in the given level. */
    public static void newTextResource(Asset level) {

        // TODO:
        /*
        EditorObject newTextObject = ObjectCreator.create(string.class, ((WOG1Level)level).getObject("textstrings"), level.getVersion());
        ObjectAdder.fixString(newTextObject);

        level.getObjects().add(newTextObject);

        level.setSelected(new EditorObject[]{ newTextObject });

        int childIndex = ((WOG1Level)level).getObject("strings").getChildren().indexOf(newTextObject);
        UndoManager.registerChange(new ObjectCreationAction(newTextObject, childIndex, childIndex));

         */

    }


    private static boolean isResourceUsed(EditorObject resource, Asset level) {
        String resourceID = resource.getAttribute("id").stringValue();
        for (EditorObject object : level.getObjects()) if (isResourceUsed_SingleObject(resourceID, object)) return true;
        return false;
    }


    private static boolean isResourceUsed_SingleObject(String resourceID, EditorObject EditorObject) {
        return Arrays.stream(EditorObject.getAttributes()).anyMatch(
                attribute -> !attribute.stringValue().isEmpty() && attribute.stringValue().equals(resourceID));
    }


    /** Removes any unused resources in the given level. */
    public static void cleanLevelResources(Asset level) {

        ArrayList<EditorObject> unused = new ArrayList<>();

        for (EditorObject EditorObject : level.getResources().getChildren()) {
            if (EditorObject instanceof Image || EditorObject instanceof Sound || EditorObject instanceof font) {
                if (!isResourceUsed(EditorObject, level)) unused.add(EditorObject);
            }
        }

        if (!unused.isEmpty()) ConfirmCleanResourcesAlarm.show(level, unused);

    }


    public static void confirmedCleanLevelResources(Asset level, ArrayList<EditorObject> toClean) {
        ArrayList<ObjectDestructionAction> objectDestructionActions = new ArrayList<>();
        for (EditorObject object : toClean) {
            objectDestructionActions.add(new ObjectDestructionAction(object));
            ObjectManager.deleteItem(level, object);
        }
        UndoManager.registerChange(objectDestructionActions.toArray(new ObjectDestructionAction[0]));
    }


    public static void loadAssetResourcesFromGlobalFiles(Asset asset) {

        for (EditorObject editorObject : asset.getObjects()) {
            if (editorObject instanceof ResourceUser resourceUser) {
                for (ResourceInterface resourceInterface : resourceUser.getUsedResources()) {
                    ObjectUtil.deepClone((EditorObject) resourceInterface, asset.getResources());
                }
            }
        }

    }

}
