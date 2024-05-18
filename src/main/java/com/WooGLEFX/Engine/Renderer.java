package com.WooGLEFX.Engine;

import java.util.ArrayList;
import java.util.List;

import com.WooGLEFX.EditorObjects.Components.ObjectPosition;
import com.WooGLEFX.EditorObjects.ObjectDetection.Draw;
import com.WooGLEFX.Engine.FX.FXCanvas;
import com.WooGLEFX.Functions.LevelManager;
import com.WooGLEFX.Structures.EditorObject;
import com.WooGLEFX.Structures.WorldLevel;

import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.paint.Stop;
import javafx.scene.transform.Affine;

public class Renderer {

    public static final Paint selectionOutline = Paint.valueOf("000000FF");
    public static final Paint selectionOutline2 = Paint.valueOf("FFFFFFFF");
    public static final Paint noLevel = Paint.valueOf("A0A0A0FF");
    //public static final Paint particleLabels = Paint.valueOf("A81CFF");


    public static final double GEOMETRY = 10000.0;




    public static Affine t;

    public static final Stop[] stops = new Stop[] { new Stop(0, javafx.scene.paint.Color.valueOf("802000FF")), new Stop(1, Color.valueOf("FFC040FF")) };

    public static double angleTo(Point2D p1, Point2D p2) {
        return Math.atan2(p2.getY() - p1.getY(), p2.getX() - p1.getX());
    }


    public static void clear(Canvas canvas) {
        canvas.getGraphicsContext2D().setFill(noLevel);
        canvas.getGraphicsContext2D().fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }


    public static void draw() {

        WorldLevel level = LevelManager.getLevel();
        Canvas canvas = FXCanvas.getCanvas();

        if (level != null) {
            if (level.isShowSceneBGColor()) {
                canvas.getGraphicsContext2D()
                        .setFill(Paint.valueOf(level.getSceneObject().getAttribute("backgroundcolor").colorValue().toHexRGBA()));
                canvas.getGraphicsContext2D().fillRect(-5000000, -5000000, 10000000, 10000000);
            } else {
                canvas.getGraphicsContext2D().clearRect(-5000000, -5000000, 10000000, 10000000);
            }
            canvas.getGraphicsContext2D().clearRect(-5000000, -5000000, 10000000, 10000000);
            Renderer.drawLevelToCanvas(level, canvas);
        } else {
            Renderer.clear(canvas);
        }

    }


    private static void addObjectPositionToListByDepth(ArrayList<ObjectPosition> objectPositions,
                                                       ObjectPosition objectPosition) {

        int i = 0;
        while (i < objectPositions.size() && objectPositions.get(i).getDepth() < objectPosition.getDepth()) {
            i++;
        }

        objectPositions.add(i, objectPosition);

    }


    private static void addAllObjectPositionsToList(ArrayList<ObjectPosition> objectPositions,
                                                    EditorObject editorObject) {

        for (ObjectPosition objectPosition : editorObject.getObjectPositions()) {
            addObjectPositionToListByDepth(objectPositions, objectPosition);
        }

        for (EditorObject child : editorObject.getChildren()) {
            addAllObjectPositionsToList(objectPositions, child);
        }

    }


    public static ArrayList<ObjectPosition> orderObjectPositionsByDepth(WorldLevel worldLevel) {

        ArrayList<ObjectPosition> objectPositions = new ArrayList<>();

        addAllObjectPositionsToList(objectPositions, worldLevel.getLevelObject());
        addAllObjectPositionsToList(objectPositions, worldLevel.getSceneObject());
        addAllObjectPositionsToList(objectPositions, worldLevel.getResourcesObject());
        addAllObjectPositionsToList(objectPositions, worldLevel.getAddinObject());
        addAllObjectPositionsToList(objectPositions, worldLevel.getTextObject());

        return objectPositions;

    }



    public static void drawLevelToCanvas(WorldLevel worldLevel, Canvas canvas) {

        GraphicsContext graphicsContext = canvas.getGraphicsContext2D();

        ArrayList<ObjectPosition> objectPositionsOrderedByDepth = orderObjectPositionsByDepth(worldLevel);

        for (ObjectPosition objectPosition : objectPositionsOrderedByDepth) {

            graphicsContext.save();

            boolean selected = SelectionManager.getSelected() != null && List.of(SelectionManager.getSelected().getObjectPositions()).contains(objectPosition);

            Draw.forPosition(graphicsContext, objectPosition, selected);

        }

    }

}
