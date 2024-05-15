package com.WooGLEFX.Functions;

import com.WooGLEFX.Engine.FX.FXCanvas;
import com.WooGLEFX.Engine.FX.FXHierarchy;
import com.WooGLEFX.Engine.FX.FXPropertiesView;
import com.WooGLEFX.Engine.Main;
import com.WooGLEFX.Engine.SelectionManager;
import com.WooGLEFX.GUI.Alarms;
import com.WooGLEFX.Structures.EditorAttribute;
import com.WooGLEFX.Structures.EditorObject;
import com.WooGLEFX.Structures.UserActions.ObjectCreationAction;
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
        obj.setLevel(LevelManager.getLevel());
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
                String id = ball.getAttribute("id");
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
                String id = string.getAttribute("id");
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
        BallInstance obj = (BallInstance) EditorObject.create("BallInstance", parent);
        obj.setAttribute("type", type);
        obj.setAttribute("x", FXCanvas.getScreenCenter().getX());
        obj.setAttribute("y", -FXCanvas.getScreenCenter().getY());
        fixGooball(obj);
        obj.setRealName("BallInstance");
        LevelManager.getLevel().getLevel().add(obj);
        addAnything(obj, parent);
    }

    public static void addLine(EditorObject parent) {
        Line obj = (Line) EditorObject.create("line", parent);
        obj.setAttribute("anchor", FXCanvas.getScreenCenter().getX() + "," + FXCanvas.getScreenCenter().getY());
        obj.setRealName("line");
        LevelManager.getLevel().getScene().add(obj);
        addAnything(obj, parent);
    }

    public static void addRectangle(EditorObject parent) {
        Rectangle obj = (Rectangle) EditorObject.create("rectangle", parent);
        obj.setAttribute("x", FXCanvas.getScreenCenter().getX());
        obj.setAttribute("y", -FXCanvas.getScreenCenter().getY());
        obj.setAttribute("static", true);
        obj.setRealName("rectangle");
        LevelManager.getLevel().getScene().add(obj);
        addAnything(obj, parent);
    }

    public static void addCircle(EditorObject parent) {
        Circle obj = (Circle) EditorObject.create("circle", parent);
        obj.setAttribute("x", FXCanvas.getScreenCenter().getX());
        obj.setAttribute("y", -FXCanvas.getScreenCenter().getY());
        obj.setRealName("circle");
        obj.setAttribute("static", true);
        LevelManager.getLevel().getScene().add(obj);
        addAnything(obj, parent);
    }

    public static SceneLayer addSceneLayer(EditorObject parent) {
        SceneLayer obj = (SceneLayer) EditorObject.create("SceneLayer", parent);
        obj.setAttribute("x", FXCanvas.getScreenCenter().getX());
        obj.setAttribute("y", -FXCanvas.getScreenCenter().getY());
        obj.setRealName("SceneLayer");
        LevelManager.getLevel().getScene().add(obj);
        addAnything(obj, parent);
        return obj;
    }

    public static void addCompositegeom(EditorObject parent) {
        Compositegeom obj = (Compositegeom) EditorObject.create("compositegeom", parent);
        obj.setAttribute("x", FXCanvas.getScreenCenter().getX());
        obj.setAttribute("y", -FXCanvas.getScreenCenter().getY());
        obj.setRealName("compositegeom");
        obj.setAttribute("static", true);
        LevelManager.getLevel().getScene().add(obj);
        addAnything(obj, parent);
    }

    public static void addHinge(EditorObject parent) {
        Hinge obj = (Hinge) EditorObject.create("hinge", parent);
        obj.setAttribute("anchor", FXCanvas.getScreenCenter().getX() + "," + FXCanvas.getScreenCenter().getY());
        obj.setRealName("hinge");
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
                        .abs(levelexit.getPosition("pos").getX() - LevelManager.getLevel().getSceneObject().getDouble("minx"));
                double distanceToRight = Math
                        .abs(levelexit.getPosition("pos").getX() - LevelManager.getLevel().getSceneObject().getDouble("maxx"));
                double distanceToTop = Math
                        .abs(levelexit.getPosition("pos").getY() - LevelManager.getLevel().getSceneObject().getDouble("miny"));
                double distanceToBottom = Math
                        .abs(levelexit.getPosition("pos").getY() - LevelManager.getLevel().getSceneObject().getDouble("maxy"));

                Point2D closestPoint;
                if (distanceToLeft <= distanceToRight && distanceToLeft <= distanceToTop
                        && distanceToLeft <= distanceToBottom) {
                    closestPoint = new Point2D(LevelManager.getLevel().getSceneObject().getDouble("minx"),
                            levelexit.getPosition("pos").getY());
                } else if (distanceToRight <= distanceToTop && distanceToRight <= distanceToBottom) {
                    closestPoint = new Point2D(LevelManager.getLevel().getSceneObject().getDouble("maxx"),
                            levelexit.getPosition("pos").getY());
                } else if (distanceToTop <= distanceToBottom) {
                    closestPoint = new Point2D(levelexit.getPosition("pos").getX(),
                            LevelManager.getLevel().getSceneObject().getDouble("miny"));
                } else {
                    closestPoint = new Point2D(levelexit.getPosition("pos").getX(),
                            LevelManager.getLevel().getSceneObject().getDouble("maxy"));
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
                EditorObject pipe = EditorObject.create("pipe", LevelManager.getLevel().getLevelObject());
                EditorObject vertex1 = EditorObject.create("Vertex", pipe);
                vertex1.setAttribute("x", levelexit.getPosition("pos").getX());
                vertex1.setAttribute("y", levelexit.getPosition("pos").getY());
                EditorObject vertex2 = EditorObject.create("Vertex", pipe);
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
        Vertex obj = (Vertex) EditorObject.create("Vertex", pipe);
        obj.setPrevious(previous);
        obj.setAttribute("x", FXCanvas.getScreenCenter().getX());
        obj.setAttribute("y", -FXCanvas.getScreenCenter().getY());
        obj.setRealName("Vertex");
        LevelManager.getLevel().getLevel().add(obj);
        addAnything(obj, parent);
    }

    public static void addFire(EditorObject parent) {
        Fire obj = (Fire) EditorObject.create("fire", parent);
        obj.setAttribute("x", FXCanvas.getScreenCenter().getX());
        obj.setAttribute("y", -FXCanvas.getScreenCenter().getY());
        obj.setRealName("fire");
        LevelManager.getLevel().getLevel().add(obj);
        addAnything(obj, parent);
    }

    public static void addLinearForcefield(EditorObject parent) {
        Linearforcefield obj = (Linearforcefield) EditorObject.create("linearforcefield",
                parent);
        obj.setAttribute("center", FXCanvas.getScreenCenter().getX() + "," + FXCanvas.getScreenCenter().getY());
        obj.setRealName("linearforcefield");
        LevelManager.getLevel().getScene().add(obj);
        addAnything(obj, parent);
    }

    public static void addRadialForcefield(EditorObject parent) {
        Radialforcefield obj = (Radialforcefield) EditorObject.create("radialforcefield",
                parent);
        obj.setAttribute("center", FXCanvas.getScreenCenter().getX() + "," + FXCanvas.getScreenCenter().getY());
        obj.setRealName("radialforcefield");
        LevelManager.getLevel().getScene().add(obj);
        addAnything(obj, parent);
    }

    public static void addParticle(EditorObject parent) {
        Particles obj = (Particles) EditorObject.create("particles", parent);
        obj.setAttribute("pos", FXCanvas.getScreenCenter().getX() + "," + -FXCanvas.getScreenCenter().getY());
        obj.setRealName("particles");
        LevelManager.getLevel().getScene().add(obj);
        addAnything(obj, parent);
    }

    public static void addSign(EditorObject parent) {
        Signpost obj = (Signpost) EditorObject.create("signpost", parent);
        obj.setAttribute("x", FXCanvas.getScreenCenter().getX());
        obj.setAttribute("y", -FXCanvas.getScreenCenter().getY());
        obj.setRealName("signpost");
        LevelManager.getLevel().getLevel().add(obj);
        addAnything(obj, parent);
    }

    public static void addString(EditorObject parent) {
        TextString obj = (TextString) EditorObject.create("string", parent);
        obj.setAttribute("id", "TEXT_" + LevelManager.getLevel().getLevelName().toUpperCase() + "_STR0");
        obj.setAttribute("text", "");
        fixString(obj);
        obj.setRealName("string");
        LevelManager.getLevel().getText().add(obj);
        addAnything(obj, parent);
    }

    public static void addResrcImage(EditorObject parent) {
        ResrcImage obj = (ResrcImage) EditorObject.create("Image", parent);
        obj.setAttribute("id", "IMAGE_SCENE_" + LevelManager.getLevel().getLevelName().toUpperCase() + "_IMG0");
        obj.setAttribute("path", "");
        obj.setAttribute("REALid", "IMAGE_SCENE_" + LevelManager.getLevel().getLevelName().toUpperCase() + "_IMG0");
        obj.setAttribute("REALpath", "");
        obj.setRealName("Image");
        LevelManager.getLevel().getResources().add(obj);
        addAnything(obj, parent);
    }

    public static void addSound(EditorObject parent) {
        Sound obj = (Sound) EditorObject.create("Sound", parent);
        obj.setAttribute("id", "SOUND_LEVEL_" + LevelManager.getLevel().getLevelName().toUpperCase() + "_SND0");
        obj.setAttribute("path", "");
        obj.setAttribute("REALid", "SOUND_LEVEL_" + LevelManager.getLevel().getLevelName().toUpperCase() + "_SND0");
        obj.setAttribute("REALpath", "");
        obj.setRealName("Sound");
        LevelManager.getLevel().getResources().add(obj);
        addAnything(obj, parent);
    }

    public static void addSetDefaults(EditorObject parent) {
        SetDefaults obj = (SetDefaults) EditorObject.create("SetDefaults", parent);
        obj.setAttribute("path", "./");
        obj.setAttribute("idprefix", "");
        obj.setRealName("SetDefaults");
        LevelManager.getLevel().getLevel().add(obj);
        addAnything(obj, parent);
    }

    public static void addLabel(EditorObject parent) {
        Label obj = (Label) EditorObject.create("label", parent);
        obj.setAttribute("x", FXCanvas.getScreenCenter().getX());
        obj.setAttribute("y", -FXCanvas.getScreenCenter().getY());
        obj.setRealName("label");
        LevelManager.getLevel().getScene().add(obj);
        addAnything(obj, parent);
    }

    public static void addStrand(EditorObject parent) {
        Strand obj = (Strand) EditorObject.create("Strand", parent);
        obj.setRealName("Strand");
        LevelManager.getLevel().getLevel().add(obj);
        addAnything(obj, parent);
    }

    public static void addCamera(EditorObject parent) {
        Camera obj = (Camera) EditorObject.create("camera", parent);
        obj.setAttribute("endpos", FXCanvas.getScreenCenter().getX() + "," + FXCanvas.getScreenCenter().getY());
        obj.setRealName("camera");
        LevelManager.getLevel().getLevel().add(obj);
        addAnything(obj, parent);
    }

    public static void addPoi(EditorObject parent) {
        Poi obj = (Poi) EditorObject.create("poi", parent);
        obj.setAttribute("pos", FXCanvas.getScreenCenter().getX() + "," + FXCanvas.getScreenCenter().getY());
        obj.setRealName("poi");
        LevelManager.getLevel().getLevel().add(obj);
        addAnything(obj, parent);
    }

    public static void addMusic(EditorObject parent) {
        Music obj = (Music) EditorObject.create("music", parent);
        obj.setRealName("music");
        LevelManager.getLevel().getLevel().add(obj);
        addAnything(obj, parent);
        // possibly rework music logic?
    }

    public static void addLoopsound(EditorObject parent) {
        Loopsound obj = (Loopsound) EditorObject.create("loopsound", parent);
        obj.setRealName("loopsound");
        LevelManager.getLevel().getLevel().add(obj);
        addAnything(obj, parent);
    }

    public static void addButton(EditorObject parent) {
        Button obj = (Button) EditorObject.create("button", parent);
        obj.setAttribute("x", FXCanvas.getScreenCenter().getX());
        obj.setAttribute("y", -FXCanvas.getScreenCenter().getY());
        obj.setRealName("button");
        LevelManager.getLevel().getScene().add(obj);
        addAnything(obj, parent);
    }

    public static void addButtongroup(EditorObject parent) {
        Buttongroup obj = (Buttongroup) EditorObject.create("buttongroup", parent);
        obj.setRealName("buttongroup");
        LevelManager.getLevel().getScene().add(obj);
        addAnything(obj, parent);
    }

    public static void addMotor(EditorObject parent) {
        Motor obj = (Motor) EditorObject.create("motor", parent);
        obj.setRealName("motor");
        LevelManager.getLevel().getScene().add(obj);
        addAnything(obj, parent);
    }

    public static void addSlider(EditorObject parent) {
        Slider obj = (Slider) EditorObject.create("slider", parent);
        obj.setRealName("slider");
        LevelManager.getLevel().getScene().add(obj);
        addAnything(obj, parent);
    }

    public static void addEndoncollision(EditorObject parent) {
        Endoncollision obj = (Endoncollision) EditorObject.create("endoncollision", parent);
        obj.setRealName("endoncollision");
        LevelManager.getLevel().getLevel().add(obj);
        addAnything(obj, parent);
    }

    public static void addEndonnogeom(EditorObject parent) {
        Endonnogeom obj = (Endonnogeom) EditorObject.create("endonnogeom", parent);
        obj.setRealName("endonnogeom");
        LevelManager.getLevel().getLevel().add(obj);
        addAnything(obj, parent);
    }

    public static void addEndonmessage(EditorObject parent) {
        Endonmessage obj = (Endonmessage) EditorObject.create("endonmessage", parent);
        obj.setRealName("endonmessage");
        LevelManager.getLevel().getLevel().add(obj);
        addAnything(obj, parent);
    }

    public static void addTargetheight(EditorObject parent) {
        Targetheight obj = (Targetheight) EditorObject.create("targetheight", parent);
        obj.setAttribute("pos", FXCanvas.getScreenCenter().getX() + ", " + -FXCanvas.getScreenCenter().getY());
        LevelManager.getLevel().getLevel().add(obj);
        addAnything(obj, parent);
    }

    public static void addLevelexit(EditorObject parent) {
        Levelexit obj = (Levelexit) EditorObject.create("levelexit", parent);
        obj.setAttribute("y", -FXCanvas.getScreenCenter().getY());
        LevelManager.getLevel().getLevel().add(obj);
        addAnything(obj, parent);
    }

    public static void addPipe(EditorObject parent) {
        Pipe obj = (Pipe) EditorObject.create("pipe", parent);
        obj.setRealName("endonmessage");
        LevelManager.getLevel().getLevel().add(obj);
        addAnything(obj, parent);
    }

}
