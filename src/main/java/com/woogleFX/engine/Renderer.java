package com.woogleFX.engine;

import java.util.ArrayList;
import java.util.List;

import com.woogleFX.editorObjects.objectComponents.ObjectComponent;
import com.woogleFX.engine.fx.FXCanvas;
import com.woogleFX.functions.LevelManager;
import com.woogleFX.editorObjects.EditorObject;
import com.woogleFX.structures.WorldLevel;

import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Paint;
import javafx.scene.transform.Affine;

public class Renderer {

    public static final Paint selectionOutline = Paint.valueOf("000000FF");
    public static final Paint selectionOutline2 = Paint.valueOf("FFFFFFFF");
    public static final Paint noLevel = Paint.valueOf("A0A0A0FF");

    public static Affine t;

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
            if (level.getVisibilitySettings().isShowSceneBGColor()) {
                canvas.getGraphicsContext2D().setFill(Paint.valueOf(level.getSceneObject().getAttribute("backgroundcolor").colorValue().toHexRGBA()));
                canvas.getGraphicsContext2D().fillRect(-5000000, -5000000, 10000000, 10000000);
            } else {
                canvas.getGraphicsContext2D().clearRect(-5000000, -5000000, 10000000, 10000000);
            }
            canvas.getGraphicsContext2D().clearRect(-5000000, -5000000, 10000000, 10000000);
            drawLevelToCanvas(level, canvas);

        } else {
            clear(canvas);
        }

    }


    private static void addObjectPositionToListByDepth(ArrayList<ObjectComponent> objectComponents,
                                                       ObjectComponent objectComponent) {

        int i = 0;
        while (i < objectComponents.size() && objectComponents.get(i).getDepth() <= objectComponent.getDepth()) {
            i++;
        }

        objectComponents.add(i, objectComponent);

    }


    private static void recursiveGetAllObjectsInList(ArrayList<EditorObject> editorObjects,
                                                     EditorObject editorObject) {

        editorObjects.add(editorObject);

        for (EditorObject child : editorObject.getChildren()) {
            recursiveGetAllObjectsInList(editorObjects, child);
        }

    }


    private static void addAllObjectPositionsToList(ArrayList<ObjectComponent> objectComponents,
                                                    EditorObject editorObject) {

        ArrayList<EditorObject> allObjects = new ArrayList<>();
        recursiveGetAllObjectsInList(allObjects, editorObject);

        for (EditorObject object : allObjects) {
            for (ObjectComponent objectComponent : object.getObjectComponents()) {
                addObjectPositionToListByDepth(objectComponents, objectComponent);
            }
        }

    }


    public static ArrayList<ObjectComponent> orderObjectPositionsByDepth(WorldLevel worldLevel) {

        // TODO fix rendering order when undoing / pasting

        ArrayList<ObjectComponent> objectComponents = new ArrayList<>();

        addAllObjectPositionsToList(objectComponents, worldLevel.getLevelObject());
        addAllObjectPositionsToList(objectComponents, worldLevel.getSceneObject());
        addAllObjectPositionsToList(objectComponents, worldLevel.getResourcesObject());
        addAllObjectPositionsToList(objectComponents, worldLevel.getAddinObject());
        addAllObjectPositionsToList(objectComponents, worldLevel.getTextObject());

        return objectComponents;

    }


    public static void drawLevelToCanvas(WorldLevel worldLevel, Canvas canvas) {

        GraphicsContext graphicsContext = canvas.getGraphicsContext2D();

        ArrayList<ObjectComponent> objectPositionsOrderedByDepth = orderObjectPositionsByDepth(worldLevel);

        if (SelectionManager.getStrand1Gooball() != null) {
            double gameRelativeX = (SelectionManager.getMouseX() - worldLevel.getOffsetX()) / worldLevel.getZoom();
            double gameRelativeY = (SelectionManager.getMouseY() - worldLevel.getOffsetY()) / worldLevel.getZoom();
            addObjectPositionToListByDepth(objectPositionsOrderedByDepth, EffectsManager.getPlacingStrand(SelectionManager.getStrand1Gooball(), gameRelativeX, gameRelativeY));
        }

        for (ObjectComponent objectComponent : objectPositionsOrderedByDepth) {

            if (!objectComponent.isVisible()) continue;

            graphicsContext.save();

            boolean selected = SelectionManager.getSelected() != null && List.of(SelectionManager.getSelected().getObjectComponents()).contains(objectComponent);

            objectComponent.draw(graphicsContext, selected);

            graphicsContext.restore();

        }

    }

}
