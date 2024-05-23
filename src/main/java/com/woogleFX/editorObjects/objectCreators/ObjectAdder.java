package com.woogleFX.editorObjects.objectCreators;

import com.woogleFX.engine.fx.FXCanvas;
import com.woogleFX.engine.fx.FXHierarchy;
import com.woogleFX.engine.fx.FXPropertiesView;
import com.woogleFX.engine.SelectionManager;
import com.woogleFX.functions.LevelManager;
import com.woogleFX.functions.ObjectManager;
import com.woogleFX.functions.undoHandling.UndoManager;
import com.woogleFX.engine.gui.Alarms;
import com.woogleFX.editorObjects.EditorObject;
import com.woogleFX.functions.undoHandling.userActions.ObjectCreationAction;
import com.woogleFX.structures.WorldLevel;
import com.worldOfGoo.level.*;
import com.worldOfGoo.resrc.ResourceManifest;
import com.worldOfGoo.resrc.ResrcImage;
import com.worldOfGoo.resrc.SetDefaults;
import com.worldOfGoo.resrc.Sound;
import com.worldOfGoo.scene.*;
import com.worldOfGoo.text.TextString;
import javafx.geometry.Point2D;

public class ObjectAdder {


    public static void autoPipe() {
        // TODO add undo events for the whole pipe

        /* Identify the level exit. If there is none, don't auto pipe. */
        for (EditorObject editorObject : LevelManager.getLevel().getLevel().toArray(new EditorObject[0])) {
            if (editorObject instanceof Levelexit levelexit) {

                /* Calculate the point closest to the scene from the level exit. */
                double distanceToLeft = Math
                        .abs(levelexit.getAttribute("pos").positionValue().getX() - LevelManager.getLevel().getSceneObject().getAttribute("minx").doubleValue());
                double distanceToRight = Math
                        .abs(levelexit.getAttribute("pos").positionValue().getX() - LevelManager.getLevel().getSceneObject().getAttribute("maxx").doubleValue());
                double distanceToTop = Math
                        .abs(levelexit.getAttribute("pos").positionValue().getY() - LevelManager.getLevel().getSceneObject().getAttribute("miny").doubleValue());
                double distanceToBottom = Math
                        .abs(levelexit.getAttribute("pos").positionValue().getY() - LevelManager.getLevel().getSceneObject().getAttribute("maxy").doubleValue());

                Point2D closestPoint;
                if (distanceToLeft <= distanceToRight && distanceToLeft <= distanceToTop
                        && distanceToLeft <= distanceToBottom) {
                    closestPoint = new Point2D(LevelManager.getLevel().getSceneObject().getAttribute("minx").doubleValue(),
                            levelexit.getAttribute("pos").positionValue().getY());
                } else if (distanceToRight <= distanceToTop && distanceToRight <= distanceToBottom) {
                    closestPoint = new Point2D(LevelManager.getLevel().getSceneObject().getAttribute("maxx").doubleValue(),
                            levelexit.getAttribute("pos").positionValue().getY());
                } else if (distanceToTop <= distanceToBottom) {
                    closestPoint = new Point2D(levelexit.getAttribute("pos").positionValue().getX(),
                            LevelManager.getLevel().getSceneObject().getAttribute("miny").doubleValue());
                } else {
                    closestPoint = new Point2D(levelexit.getAttribute("pos").positionValue().getX(),
                            LevelManager.getLevel().getSceneObject().getAttribute("maxy").doubleValue());
                }

                /* Delete the old pipe. */
                for (EditorObject maybePipe : LevelManager.getLevel().getLevel().toArray(new EditorObject[0])) {
                    if (maybePipe instanceof Pipe) {
                        ObjectManager.deleteItem(LevelManager.getLevel(), maybePipe, false);
                    }
                }

                /*
                 * Create a pipe with a vertex at the level exit and at the scene intersection.
                 */
                EditorObject pipe = ObjectCreator.create("pipe", LevelManager.getLevel().getLevelObject());
                EditorObject vertex1 = ObjectCreator.create("Vertex", pipe);
                vertex1.setAttribute("x", levelexit.getAttribute("pos").positionValue().getX());
                vertex1.setAttribute("y", levelexit.getAttribute("pos").positionValue().getY());
                EditorObject vertex2 = ObjectCreator.create("Vertex", pipe);
                vertex2.setAttribute("x", closestPoint.getX());
                vertex2.setAttribute("y", closestPoint.getY());
                ((Vertex) vertex2).setPrevious((Vertex) vertex1);

                LevelManager.getLevel().getLevel().add(pipe);
                LevelManager.getLevel().getLevel().add(vertex1);
                LevelManager.getLevel().getLevel().add(vertex2);

            }
        }
    }


    public static EditorObject addObject(String name) {

        WorldLevel level = LevelManager.getLevel();
        if (level == null) return null;

        EditorObject parent = switch (name) {

            case "linearforcefield", "radialforcefield", "particles",
                    "SceneLayer", "buttongroup", "button", "circle",
                    "rectangle", "hinge", "compositegeom", "label",
                    "line", "motor", "slider" -> level.getSceneObject();

            case "BallInstance", "Strand", "camera", "poi", "music",
                    "loopsound", "endoncollision", "endonnogeom",
                    "endonmessage", "targetheight", "fire", "levelexit",
                    "pipe", "signpost", "Vertex" -> level.getLevelObject();

            case "resrcimage", "sound", "setdefaults" ->
                    level.getResourcesObject().getChildren().get(0);

            case "textstring" -> level.getTextObject();

            default -> throw new RuntimeException("Invalid object " + name);

        };

        EditorObject obj = ObjectCreator.create(name, parent);
        adjustObject(obj, parent);

        if (parent instanceof Scene) level.getScene().add(obj);
        else if (parent instanceof Level) level.getLevel().add(obj);
        else if (parent instanceof ResourceManifest) level.getResources().add(obj);

        addAnything(obj, parent);

        return obj;
    }


    public static void addAnything(EditorObject obj, EditorObject parent) {
        adjustObject(obj, parent);
        // obj.setTreeItem(new TreeItem<>(obj));
        // parent.getTreeItem().getChildren().add(obj.getTreeItem());
        FXHierarchy.getHierarchy().scrollTo(FXHierarchy.getHierarchy().getRow(obj.getTreeItem()));
        FXHierarchy.getHierarchy().getSelectionModel().select(FXHierarchy.getHierarchy().getRow(obj.getTreeItem()));
        obj.update();
        SelectionManager.setSelected(obj);
        FXPropertiesView.changeTableView(LevelManager.getLevel().getSelected());

        UndoManager.registerChange(new ObjectCreationAction(obj,
                FXHierarchy.getHierarchy().getRow(obj.getTreeItem()) - FXHierarchy.getHierarchy().getRow(obj.getParent().getTreeItem())));
        LevelManager.getLevel().redoActions.clear();
    }

    /**
     * Changes the id attribute of a BallInstance to give it a unique ID.
     * IDs are given in the form of "goo[number]".
     *
     * @param obj The BallInstance to modify.
     */
    public static void fixGooball(EditorObject obj) {

        // Create an array to store which id numbers are already taken by BallInstances.
        boolean[] taken = new boolean[LevelManager.getLevel().getLevel().size()];

        // Loop over all BallInstances in the LevelManager.getLevel().
        for (EditorObject ball : LevelManager.getLevel().getLevel()) {
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
    }

    /**
     * Changes the id attribute of a text string to give it a unique ID.
     * IDs are given in the form of "TEXT_[level name]_STR[number]".
     *
     * @param obj The string to modify.
     */
    public static void fixString(EditorObject obj) {

        // Create an array to store which id numbers are already taken by strings.
        boolean[] taken = new boolean[LevelManager.getLevel().getText().size()];

        // Loop over all text strings in the LevelManager.getLevel().
        for (EditorObject string : LevelManager.getLevel().getText()) {
            if (string instanceof TextString) {

                // Check if the string's ID is "TEXT_[level name]_STR[number]".
                // If it is, flag that number as already taken.
                String id = string.getAttribute("id").stringValue();
                if (id.length() > 9 + LevelManager.getLevel().getLevelName().length()
                        && id.startsWith("TEXT_" + LevelManager.getLevel().getLevelName().toUpperCase() + "_STR")) {
                    try {
                        taken[Integer.parseInt(id.substring(9 + LevelManager.getLevel().getLevelName().length()))] = true;
                    } catch (Exception ignored) {
                    }
                }
            }
        }

        // Find the smallest available number to use as an ID and set the string's ID
        // attribute accordingly.
        int count = 0;
        while (taken[count]) {
            count++;
        }
        obj.setAttribute("id", "TEXT_" + LevelManager.getLevel().getLevelName().toUpperCase() + "_STR" + count);
    }


    private static void adjustObject(EditorObject object, EditorObject parent) {
        if (object instanceof BallInstance ballInstance) {
            ballInstance.setAttribute("x", FXCanvas.getScreenCenter().getX());
            ballInstance.setAttribute("y", -FXCanvas.getScreenCenter().getY());
            fixGooball(object);
        } else if (object instanceof Line line) {
            line.setAttribute("anchor", FXCanvas.getScreenCenter().getX() + "," + FXCanvas.getScreenCenter().getY());
        } else if (object instanceof Rectangle rectangle) {
            rectangle.setAttribute("x", FXCanvas.getScreenCenter().getX());
            rectangle.setAttribute("y", -FXCanvas.getScreenCenter().getY());
            rectangle.setAttribute("static", true);
        } else if (object instanceof Circle circle) {
            circle.setAttribute("x", FXCanvas.getScreenCenter().getX());
            circle.setAttribute("y", -FXCanvas.getScreenCenter().getY());
            circle.setAttribute("static", true);
        } else if (object instanceof SceneLayer sceneLayer) {
            sceneLayer.setAttribute("x", FXCanvas.getScreenCenter().getX());
            sceneLayer.setAttribute("y", -FXCanvas.getScreenCenter().getY());
        } else if (object instanceof Compositegeom compositegeom) {
            compositegeom.setAttribute("x", FXCanvas.getScreenCenter().getX());
            compositegeom.setAttribute("y", -FXCanvas.getScreenCenter().getY());
            compositegeom.setAttribute("static", true);
        } else if (object instanceof Hinge hinge) {
            hinge.setAttribute("anchor", FXCanvas.getScreenCenter().getX() + "," + FXCanvas.getScreenCenter().getY());
        } else if (object instanceof Vertex vertex) {
            // get the previous vertex before this one
            EditorObject pipe = null;
            if (parent instanceof Pipe) {
                pipe = parent;
            } else {
                for (EditorObject child : parent.getChildren()) {
                    if (child instanceof Pipe) {
                        pipe = child;
                        break;
                    }
                }
                if (pipe == null) {
                    Alarms.errorMessage("You must create a pipe to add the vertex to.");
                }
            }
            Vertex previous = null;
            if (pipe != null) for (EditorObject child : pipe.getChildren()) {
                if (child instanceof Vertex) {
                    previous = (Vertex) child;
                }
            }
            vertex.setPrevious(previous);
            vertex.setAttribute("x", FXCanvas.getScreenCenter().getX());
            vertex.setAttribute("y", -FXCanvas.getScreenCenter().getY());

        } else if (object instanceof Fire fire) {
            fire.setAttribute("x", FXCanvas.getScreenCenter().getX());
            fire.setAttribute("y", -FXCanvas.getScreenCenter().getY());
        } else if (object instanceof Linearforcefield linearforcefield) {
            linearforcefield.setAttribute("center", FXCanvas.getScreenCenter().getX() + "," + FXCanvas.getScreenCenter().getY());
        } else if (object instanceof Radialforcefield radialforcefield) {
            radialforcefield.setAttribute("center", FXCanvas.getScreenCenter().getX() + "," + FXCanvas.getScreenCenter().getY());
        } else if (object instanceof Particles particles) {
            particles.setAttribute("pos", FXCanvas.getScreenCenter().getX() + "," + -FXCanvas.getScreenCenter().getY());
        } else if (object instanceof Signpost signpost) {
            signpost.setAttribute("x", FXCanvas.getScreenCenter().getX());
            signpost.setAttribute("y", -FXCanvas.getScreenCenter().getY());
        } else if (object instanceof TextString textString) {
            textString.setAttribute("id", "TEXT_" + LevelManager.getLevel().getLevelName().toUpperCase() + "_STR0");
            textString.setAttribute("text", "");
            fixString(textString);
        } else if (object instanceof ResrcImage resrcImage) {
            resrcImage.setAttribute("id", "IMAGE_SCENE_" + LevelManager.getLevel().getLevelName().toUpperCase() + "_IMG0");
            resrcImage.setAttribute("path", "");
        } else if (object instanceof Sound sound) {
            sound.setAttribute("id", "SOUND_LEVEL_" + LevelManager.getLevel().getLevelName().toUpperCase() + "_SND0");
            sound.setAttribute("path", "");
        } else if (object instanceof SetDefaults setDefaults) {
            setDefaults.setAttribute("path", "./");
            setDefaults.setAttribute("idprefix", "");
        } else if (object instanceof Label label) {
            label.setAttribute("x", FXCanvas.getScreenCenter().getX());
            label.setAttribute("y", -FXCanvas.getScreenCenter().getY());
        } else if (object instanceof Camera camera) {
            camera.setAttribute("endpos", FXCanvas.getScreenCenter().getX() + "," + FXCanvas.getScreenCenter().getY());
        } else if (object instanceof Poi poi) {
            poi.setAttribute("pos", FXCanvas.getScreenCenter().getX() + "," + FXCanvas.getScreenCenter().getY());
        } else if (object instanceof Button button) {
            button.setAttribute("x", FXCanvas.getScreenCenter().getX());
            button.setAttribute("y", -FXCanvas.getScreenCenter().getY());
        } else if (object instanceof Targetheight targetheight) {
            targetheight.setAttribute("pos", FXCanvas.getScreenCenter().getX() + ", " + -FXCanvas.getScreenCenter().getY());
        } else if (object instanceof Levelexit levelexit) {
            levelexit.setAttribute("y", -FXCanvas.getScreenCenter().getY());
        }

    }

}
