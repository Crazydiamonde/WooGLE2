package com.WooGLEFX.EditorObjects.ObjectCreators;

import com.WooGLEFX.Engine.FX.FXCanvas;
import com.WooGLEFX.Engine.FX.FXHierarchy;
import com.WooGLEFX.Engine.FX.FXPropertiesView;
import com.WooGLEFX.Engine.SelectionManager;
import com.WooGLEFX.Functions.LevelManager;
import com.WooGLEFX.Functions.ObjectManager;
import com.WooGLEFX.Functions.UndoHandling.UndoManager;
import com.WooGLEFX.Engine.GUI.Alarms;
import com.WooGLEFX.EditorObjects.EditorObject;
import com.WooGLEFX.Functions.UndoHandling.UserActions.ObjectCreationAction;
import com.WorldOfGoo.Level.*;
import com.WorldOfGoo.Resrc.ResrcImage;
import com.WorldOfGoo.Resrc.SetDefaults;
import com.WorldOfGoo.Resrc.Sound;
import com.WorldOfGoo.Scene.*;
import com.WorldOfGoo.Text.TextString;
import javafx.geometry.Point2D;

public class ObjectAdder {

    public static void addAnything(EditorObject obj, EditorObject parent) {
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



    public static void addBall(EditorObject parent, String type) {
        BallInstance obj = (BallInstance) ObjectCreator.create("BallInstance", parent);
        obj.setAttribute("type", type);
        obj.setAttribute("x", FXCanvas.getScreenCenter().getX());
        obj.setAttribute("y", -FXCanvas.getScreenCenter().getY());
        fixGooball(obj);
        LevelManager.getLevel().getLevel().add(obj);
        addAnything(obj, parent);
    }

    public static void addLine(EditorObject parent) {
        Line obj = (Line) ObjectCreator.create("line", parent);
        obj.setAttribute("anchor", FXCanvas.getScreenCenter().getX() + "," + FXCanvas.getScreenCenter().getY());
        LevelManager.getLevel().getScene().add(obj);
        addAnything(obj, parent);
    }

    public static void addRectangle(EditorObject parent) {
        Rectangle obj = (Rectangle) ObjectCreator.create("rectangle", parent);
        obj.setAttribute("x", FXCanvas.getScreenCenter().getX());
        obj.setAttribute("y", -FXCanvas.getScreenCenter().getY());
        obj.setAttribute("static", true);
        LevelManager.getLevel().getScene().add(obj);
        addAnything(obj, parent);
    }

    public static void addCircle(EditorObject parent) {
        Circle obj = (Circle) ObjectCreator.create("circle", parent);
        obj.setAttribute("x", FXCanvas.getScreenCenter().getX());
        obj.setAttribute("y", -FXCanvas.getScreenCenter().getY());
        obj.setAttribute("static", true);
        LevelManager.getLevel().getScene().add(obj);
        addAnything(obj, parent);
    }

    public static SceneLayer addSceneLayer(EditorObject parent) {
        SceneLayer obj = (SceneLayer) ObjectCreator.create("SceneLayer", parent);
        obj.setAttribute("x", FXCanvas.getScreenCenter().getX());
        obj.setAttribute("y", -FXCanvas.getScreenCenter().getY());
        LevelManager.getLevel().getScene().add(obj);
        addAnything(obj, parent);
        return obj;
    }

    public static void addCompositegeom(EditorObject parent) {
        Compositegeom obj = (Compositegeom) ObjectCreator.create("compositegeom", parent);
        obj.setAttribute("x", FXCanvas.getScreenCenter().getX());
        obj.setAttribute("y", -FXCanvas.getScreenCenter().getY());
        obj.setAttribute("static", true);
        LevelManager.getLevel().getScene().add(obj);
        addAnything(obj, parent);
    }

    public static void addHinge(EditorObject parent) {
        Hinge obj = (Hinge) ObjectCreator.create("hinge", parent);
        obj.setAttribute("anchor", FXCanvas.getScreenCenter().getX() + "," + FXCanvas.getScreenCenter().getY());
        LevelManager.getLevel().getScene().add(obj);
        addAnything(obj, parent);
    }

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

    public static void addPipeVertex(EditorObject parent) {
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
        for (EditorObject child : pipe.getChildren()) {
            if (child instanceof Vertex) {
                previous = (Vertex) child;
            }
        }
        Vertex obj = (Vertex) ObjectCreator.create("Vertex", pipe);
        obj.setPrevious(previous);
        obj.setAttribute("x", FXCanvas.getScreenCenter().getX());
        obj.setAttribute("y", -FXCanvas.getScreenCenter().getY());
        LevelManager.getLevel().getLevel().add(obj);
        addAnything(obj, parent);
    }

    public static void addFire(EditorObject parent) {
        Fire obj = (Fire) ObjectCreator.create("fire", parent);
        obj.setAttribute("x", FXCanvas.getScreenCenter().getX());
        obj.setAttribute("y", -FXCanvas.getScreenCenter().getY());
        LevelManager.getLevel().getLevel().add(obj);
        addAnything(obj, parent);
    }

    public static void addLinearForcefield(EditorObject parent) {
        Linearforcefield obj = (Linearforcefield) ObjectCreator.create("linearforcefield",
                parent);
        obj.setAttribute("center", FXCanvas.getScreenCenter().getX() + "," + FXCanvas.getScreenCenter().getY());
        LevelManager.getLevel().getScene().add(obj);
        addAnything(obj, parent);
    }

    public static void addRadialForcefield(EditorObject parent) {
        Radialforcefield obj = (Radialforcefield) ObjectCreator.create("radialforcefield",
                parent);
        obj.setAttribute("center", FXCanvas.getScreenCenter().getX() + "," + FXCanvas.getScreenCenter().getY());
        LevelManager.getLevel().getScene().add(obj);
        addAnything(obj, parent);
    }

    public static void addParticle(EditorObject parent) {
        Particles obj = (Particles) ObjectCreator.create("particles", parent);
        obj.setAttribute("pos", FXCanvas.getScreenCenter().getX() + "," + -FXCanvas.getScreenCenter().getY());
        LevelManager.getLevel().getScene().add(obj);
        addAnything(obj, parent);
    }

    public static void addSign(EditorObject parent) {
        Signpost obj = (Signpost) ObjectCreator.create("signpost", parent);
        obj.setAttribute("x", FXCanvas.getScreenCenter().getX());
        obj.setAttribute("y", -FXCanvas.getScreenCenter().getY());
        LevelManager.getLevel().getLevel().add(obj);
        addAnything(obj, parent);
    }

    public static void addString(EditorObject parent) {
        TextString obj = (TextString) ObjectCreator.create("string", parent);
        obj.setAttribute("id", "TEXT_" + LevelManager.getLevel().getLevelName().toUpperCase() + "_STR0");
        obj.setAttribute("text", "");
        fixString(obj);
        LevelManager.getLevel().getText().add(obj);
        addAnything(obj, parent);
    }

    public static void addResrcImage(EditorObject parent) {
        ResrcImage obj = (ResrcImage) ObjectCreator.create("Image", parent);
        obj.setAttribute("id", "IMAGE_SCENE_" + LevelManager.getLevel().getLevelName().toUpperCase() + "_IMG0");
        obj.setAttribute("path", "");
        LevelManager.getLevel().getResources().add(obj);
        addAnything(obj, parent);
    }

    public static void addSound(EditorObject parent) {
        Sound obj = (Sound) ObjectCreator.create("Sound", parent);
        obj.setAttribute("id", "SOUND_LEVEL_" + LevelManager.getLevel().getLevelName().toUpperCase() + "_SND0");
        obj.setAttribute("path", "");
        LevelManager.getLevel().getResources().add(obj);
        addAnything(obj, parent);
    }

    public static void addSetDefaults(EditorObject parent) {
        SetDefaults obj = (SetDefaults) ObjectCreator.create("SetDefaults", parent);
        obj.setAttribute("path", "./");
        obj.setAttribute("idprefix", "");
        LevelManager.getLevel().getLevel().add(obj);
        addAnything(obj, parent);
    }

    public static void addLabel(EditorObject parent) {
        Label obj = (Label) ObjectCreator.create("label", parent);
        obj.setAttribute("x", FXCanvas.getScreenCenter().getX());
        obj.setAttribute("y", -FXCanvas.getScreenCenter().getY());
        LevelManager.getLevel().getScene().add(obj);
        addAnything(obj, parent);
    }

    public static void addStrand(EditorObject parent) {
        Strand obj = (Strand) ObjectCreator.create("Strand", parent);
        LevelManager.getLevel().getLevel().add(obj);
        addAnything(obj, parent);
    }

    public static void addCamera(EditorObject parent) {
        Camera obj = (Camera) ObjectCreator.create("camera", parent);
        obj.setAttribute("endpos", FXCanvas.getScreenCenter().getX() + "," + FXCanvas.getScreenCenter().getY());
        LevelManager.getLevel().getLevel().add(obj);
        addAnything(obj, parent);
    }

    public static void addPoi(EditorObject parent) {
        Poi obj = (Poi) ObjectCreator.create("poi", parent);
        obj.setAttribute("pos", FXCanvas.getScreenCenter().getX() + "," + FXCanvas.getScreenCenter().getY());
        LevelManager.getLevel().getLevel().add(obj);
        addAnything(obj, parent);
    }

    public static void addMusic(EditorObject parent) {
        Music obj = (Music) ObjectCreator.create("music", parent);
        LevelManager.getLevel().getLevel().add(obj);
        addAnything(obj, parent);
        // possibly rework music logic?
    }

    public static void addLoopsound(EditorObject parent) {
        Loopsound obj = (Loopsound) ObjectCreator.create("loopsound", parent);
        LevelManager.getLevel().getLevel().add(obj);
        addAnything(obj, parent);
    }

    public static void addButton(EditorObject parent) {
        Button obj = (Button) ObjectCreator.create("button", parent);
        obj.setAttribute("x", FXCanvas.getScreenCenter().getX());
        obj.setAttribute("y", -FXCanvas.getScreenCenter().getY());
        LevelManager.getLevel().getScene().add(obj);
        addAnything(obj, parent);
    }

    public static void addButtongroup(EditorObject parent) {
        Buttongroup obj = (Buttongroup) ObjectCreator.create("buttongroup", parent);
        LevelManager.getLevel().getScene().add(obj);
        addAnything(obj, parent);
    }

    public static void addMotor(EditorObject parent) {
        Motor obj = (Motor) ObjectCreator.create("motor", parent);
        LevelManager.getLevel().getScene().add(obj);
        addAnything(obj, parent);
    }

    public static void addSlider(EditorObject parent) {
        Slider obj = (Slider) ObjectCreator.create("slider", parent);
        LevelManager.getLevel().getScene().add(obj);
        addAnything(obj, parent);
    }

    public static void addEndoncollision(EditorObject parent) {
        Endoncollision obj = (Endoncollision) ObjectCreator.create("endoncollision", parent);
        LevelManager.getLevel().getLevel().add(obj);
        addAnything(obj, parent);
    }

    public static void addEndonnogeom(EditorObject parent) {
        Endonnogeom obj = (Endonnogeom) ObjectCreator.create("endonnogeom", parent);
        LevelManager.getLevel().getLevel().add(obj);
        addAnything(obj, parent);
    }

    public static void addEndonmessage(EditorObject parent) {
        Endonmessage obj = (Endonmessage) ObjectCreator.create("endonmessage", parent);
        LevelManager.getLevel().getLevel().add(obj);
        addAnything(obj, parent);
    }

    public static void addTargetheight(EditorObject parent) {
        Targetheight obj = (Targetheight) ObjectCreator.create("targetheight", parent);
        obj.setAttribute("pos", FXCanvas.getScreenCenter().getX() + ", " + -FXCanvas.getScreenCenter().getY());
        LevelManager.getLevel().getLevel().add(obj);
        addAnything(obj, parent);
    }

    public static void addLevelexit(EditorObject parent) {
        Levelexit obj = (Levelexit) ObjectCreator.create("levelexit", parent);
        obj.setAttribute("y", -FXCanvas.getScreenCenter().getY());
        LevelManager.getLevel().getLevel().add(obj);
        addAnything(obj, parent);
    }

    public static void addPipe(EditorObject parent) {
        Pipe obj = (Pipe) ObjectCreator.create("pipe", parent);
        LevelManager.getLevel().getLevel().add(obj);
        addAnything(obj, parent);
    }

}
