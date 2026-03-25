package com.woogleFX.editorObjects.objectCreators;

import com.woogleFX.assets.Asset;
import com.woogleFX.editorObjects.EditorObject;
import com.woogleFX.editorObjects.objectComponents.ObjectComponent;
import com.woogleFX.engine.fx.FXCanvas;
import com.woogleFX.engine.fx.hierarchy.FXHierarchy;
import com.woogleFX.engine.fx.propertiesView.FXPropertiesView;
import com.woogleFX.engine.SelectionManager;
import com.woogleFX.engine.AssetManager;
import com.woogleFX.editorObjects.ObjectManager;
import com.woogleFX.editorObjects._2_Positionable;
import com.woogleFX.engine.undoHandling.UndoManager;
import com.woogleFX.engine.undoHandling.userActions.ObjectCreationAction;
import com.woogleFX.assets.GameVersion;
import com.woogleFX.assets.wog1.level.WOG1Level;
import com.worldOfGoo.level.*;
import com.worldOfGoo.resrc.Image;
import com.worldOfGoo.resrc.SetDefaults;
import com.worldOfGoo.resrc.Sound;
import com.worldOfGoo.scene.*;
import com.worldOfGoo.text.string;
import com.worldOfGoo2.level.*;
import com.worldOfGoo2.misc.Point;
import javafx.geometry.Point2D;

import java.util.*;

public class ObjectAdder {


    public static void autoPipe(WOG1Level level) {
        // TODO: add undo events for the whole pipe

        /* Identify the level exit. If there is none, don't auto pipe. */
        for (EditorObject EditorObject : level.getObjects().toArray(new EditorObject[0])) {
            if (EditorObject instanceof levelexit levelexit) {

                /* Calculate the point closest to the scene from the level exit. */
                double distanceToLeft = Math
                        .abs(levelexit.getAttribute("pos").positionValue().getX() - level.getScene().getAttribute("minx").doubleValue());
                double distanceToRight = Math
                        .abs(levelexit.getAttribute("pos").positionValue().getX() - level.getScene().getAttribute("maxx").doubleValue());
                double distanceToTop = Math
                        .abs(levelexit.getAttribute("pos").positionValue().getY() - level.getScene().getAttribute("miny").doubleValue());
                double distanceToBottom = Math
                        .abs(levelexit.getAttribute("pos").positionValue().getY() - level.getScene().getAttribute("maxy").doubleValue());

                Point2D closestPoint;
                if (distanceToLeft <= distanceToRight && distanceToLeft <= distanceToTop
                        && distanceToLeft <= distanceToBottom) {
                    closestPoint = new Point2D(level.getScene().getAttribute("minx").doubleValue(),
                            levelexit.getAttribute("pos").positionValue().getY());
                } else if (distanceToRight <= distanceToTop && distanceToRight <= distanceToBottom) {
                    closestPoint = new Point2D(level.getScene().getAttribute("maxx").doubleValue(),
                            levelexit.getAttribute("pos").positionValue().getY());
                } else if (distanceToTop <= distanceToBottom) {
                    closestPoint = new Point2D(levelexit.getAttribute("pos").positionValue().getX(),
                            level.getScene().getAttribute("miny").doubleValue());
                } else {
                    closestPoint = new Point2D(levelexit.getAttribute("pos").positionValue().getX(),
                            level.getScene().getAttribute("maxy").doubleValue());
                }

                /* Delete the old pipe. */
                for (EditorObject maybePipe : level.getObjects().toArray(new EditorObject[0])) {
                    if (maybePipe instanceof pipe) {
                        ObjectManager.deleteItem(level, maybePipe);
                    }
                }

                // Create a pipe with a vertex at the level exit and at the scene intersection.

                EditorObject pipe = ObjectCreator.create(com.worldOfGoo.level.pipe.class, level.getLevel(), level.getVersion());

                EditorObject vertex1 = ObjectCreator.create(Vertex.class, pipe, level.getVersion());
                vertex1.setAttribute("x", levelexit.getAttribute("pos").positionValue().getX());
                vertex1.setAttribute("y", levelexit.getAttribute("pos").positionValue().getY());
                vertex1.onLoaded(level);

                EditorObject vertex2 = ObjectCreator.create(Vertex.class, pipe, level.getVersion());
                vertex2.setAttribute("x", closestPoint.getX());
                vertex2.setAttribute("y", closestPoint.getY());
                vertex2.onLoaded(level);

                pipe.onLoaded(level);

            }
        }
    }

    public static EditorObject addObject(Class<? extends EditorObject> tClass, EditorObject parent, boolean centered) {

        if (parent == null) parent = AssetManager.getAsset().getDefaultParent(tClass);

        EditorObject obj = ObjectCreator.create(tClass, parent, AssetManager.getAsset().getVersion());
        adjustObject(obj);

        addAnything(obj);

        obj.onLoaded(parent.getAsset());
        if (centered) {
            objectToCameraCenter(obj);
        }

        return obj;

    }


    public static EditorObject addObject2(Class<? extends EditorObject> name, String typeID, EditorObject parent) {

        Asset level = AssetManager.getAsset();
        if (level == null) return null;

        EditorObject obj = ObjectCreator.create(name, parent, typeID, level.getVersion());

        obj.createRequiredChildren();

        if (obj instanceof _2_Level_TerrainGroup) {
            EditorObject point = ObjectCreator.create(Point.class, obj, "textureOffset", GameVersion.VERSION_WOG2);
            point.setAttribute("x", 0);
            point.setAttribute("y", 0);
        } else if (obj instanceof _2_Positionable positionable) {
            positionable.createPosition();
            
            if (obj instanceof _2_Level_Item) {
                EditorObject scale = ObjectCreator.create(Point.class, obj, "scale", GameVersion.VERSION_WOG2);
                scale.setAttribute("x", 1);
                scale.setAttribute("y", 1);
            }
        } else if (obj instanceof CameraKeyFrame) {
            EditorObject position = ObjectCreator.create(Point.class, obj, "position", GameVersion.VERSION_WOG2);
            position.setAttribute("x", 0);
            position.setAttribute("y", 0);
        }

        addAnything(obj);

        FXHierarchy.getHierarchy().getSelectionModel().clearSelection();
        FXHierarchy.getHierarchy().getSelectionModel().select(obj.getTreeItem());

        return obj;

    }


    public static void addAnything(EditorObject obj) {
        adjustObject(obj);

        FXHierarchy.getHierarchy().getSelectionModel().select(obj.getTreeItem());
        obj.onLoaded(AssetManager.getAsset());
        obj.update();
        AssetManager.getAsset().setSelectedComponents(obj.getObjectComponents());
        FXPropertiesView.changeTableView(new EditorObject[]{obj});

        if (obj instanceof _2_Level_BallInstance) {
            fixGooBall(obj);
        }

        UndoManager.registerChange(new ObjectCreationAction(obj));
    }

    /**
     * Changes the id attribute of a BallInstance to give it a unique ID.
     * IDs are given in the form of "goo[number]".
     *
     * @param obj The BallInstance to modify.
     */
    public static void fixGooBall(EditorObject obj) {

        if (obj.getVersion() == GameVersion.VERSION_WOG1_OLD || obj.getVersion() == GameVersion.VERSION_WOG1_NEW) {

            // Create an array to store which id numbers are already taken by BallInstances.
            boolean[] taken = new boolean[AssetManager.getAsset().getObjects().size()];

            // Loop over all BallInstances in the level.
            for (EditorObject ball : AssetManager.getAsset().getObjects()) {
                if (ball instanceof BallInstance) {

                    // Check if the ball's ID is "goo[number]".
                    // If it is, flag that number as already taken.
                    String id = ball.getAttribute("id").stringValue();
                    if (id.length() > 3 && id.startsWith("goo")) {
                        try {
                            taken[Integer.parseInt(id.substring(3))] = true;
                        } catch (Exception ignored) {
                        }
                    }
                }
            }

            // Find the smallest available number to use as an ID and set the ball's ID
            // attribute accordingly.
            int count = 0;
            while (taken[count]) {
                count++;
            }
            obj.setAttribute("id", "goo" + count);

        } else {

            // Create an array to store which id numbers are already taken by BallInstances.
            Set<String> taken = new HashSet<>();

            // Loop over all BallInstances in the level.
            for (EditorObject ball : AssetManager.getAsset().getObjects()) {
                if (ball instanceof _2_Level_BallInstance && ball != obj) {

                    // Check if the ball's ID is "goo[number]".
                    // If it is, flag that number as already taken.
                    String id = ball.getAttribute("uid").stringValue();
                    taken.add(id);

                }
            }

            // Find the smallest available number to use as an ID and set the ball's ID
            // attribute accordingly.
            int count = 1;
            if (!taken.contains(obj.getAttribute("uid").stringValue()) && !obj.getAttribute("uid").stringValue().equals("0")) return;
            while (taken.contains(String.valueOf(count))) {
                count++;
            }
            obj.setAttribute("uid", count);

        }

    }

    /**
     * Changes the id attribute of a text string to give it a unique ID.
     * IDs are given in the form of "TEXT_[level name]_STR[number]".
     *
     * @param obj The string to modify.
     */
    public static void fixString(EditorObject obj) {

        WOG1Level level = (WOG1Level) AssetManager.getAsset();

        // Create an array to store which id numbers are already taken by strings.
        Set<Integer> taken = new HashSet<>();

        // Loop over all text strings in the level.
        for (EditorObject string : level.getObjects()) {
            if (string instanceof com.worldOfGoo.text.string) {

                // Check if the string's ID is "TEXT_[level name]_STR[number]".
                // If it is, flag that number as already taken.
                String id = string.getAttribute("id").stringValue();
                if (id.length() > 9 + level.getName().length()
                        && id.startsWith("TEXT_" + level.getName().toUpperCase() + "_STR")) {
                    try {
                        taken.add(Integer.parseInt(id.substring(9 + level.getName().length())));
                    } catch (Exception ignored) {
                    }
                }
            }
        }

        // Find the smallest available number to use as an ID and set the string's ID
        // attribute accordingly.
        int count = 0;
        while (taken.contains(count)) {
            count++;
        }
        obj.setAttribute("id", "TEXT_" + level.getName().toUpperCase() + "_STR" + count);
    }


    public static void adjustObjectLocation(EditorObject object) {

        Asset level = AssetManager.getAsset();

        // Create the object at the mouse position
        double objectX = (SelectionManager.getMouseX() - level.getOffsetX()) / level.getZoom();
        double objectY = (SelectionManager.getMouseY() - level.getOffsetY()) / level.getZoom();

        if (object.getObjectComponents().length > 0) {
            ObjectComponent objectComponent = object.getObjectComponents()[0];
            objectComponent.setX(objectX);
            objectComponent.setY(objectY);
        }

    }


    public static void objectToCameraCenter(EditorObject object) {

        Asset level = AssetManager.getAsset();

        // Create the object at the center of the screen
        double objectX = (-level.getOffsetX() + FXCanvas.getCanvas().getWidth() / 2) / level.getZoom();
        double objectY = (-level.getOffsetY() + FXCanvas.getCanvas().getHeight() / 2) / level.getZoom();

        if (object.getObjectComponents().length > 0) {
            ObjectComponent objectComponent = object.getObjectComponents()[0];
            objectComponent.setX(objectX);
            objectComponent.setY(objectY);
        }

    }


    private static void adjustObject(EditorObject object) {

        adjustObjectLocation(object);

        if (object instanceof rectangle rectangle) {
            rectangle.setAttribute("static", true);
        } else if (object instanceof circle circle) {
            circle.setAttribute("static", true);
        } else if (object instanceof compositegeom compositegeom) {
            compositegeom.setAttribute("static", true);
        } else if (object instanceof string string) {
            string.setAttribute("id", "TEXT_" + AssetManager.getAsset().getName().toUpperCase() + "_STR0");
            string.setAttribute("text", "");
            fixString(string);
        } else if (object instanceof Image image) {
            image.setAttribute("id", "IMAGE_SCENE_" + AssetManager.getAsset().getName().toUpperCase() + "_IMG0");
            image.setAttribute("path", "");
        } else if (object instanceof Sound sound) {
            sound.setAttribute("id", "SOUND_LEVEL_" + AssetManager.getAsset().getName().toUpperCase() + "_SND0");
            sound.setAttribute("path", "");
        } else if (object instanceof SetDefaults setDefaults) {
            setDefaults.setAttribute("path", "./");
            setDefaults.setAttribute("idprefix", "");
        } else if (object instanceof linearforcefield linearforcefield) {
            linearforcefield.setAttribute("width", 75);
            linearforcefield.setAttribute("height", 75);
        }

    }

}
