package com.woogleFX.engine.renderer;

import java.util.ArrayList;
import java.util.List;

import com.woogleFX.editorObjects.objectComponents.ObjectComponent;
import com.woogleFX.engine.SelectionManager;
import com.woogleFX.engine.fx.FXCanvas;
import com.woogleFX.engine.LevelManager;
import com.woogleFX.editorObjects.EditorObject;
import com.woogleFX.gameData.level._Level;

import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
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

        _Level level = LevelManager.getLevel();
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

            if (SelectionManager.getMode() == SelectionManager.GEOMETRY) {
                EffectsManager.renderCurrentSpline(canvas.getGraphicsContext2D());
            }

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


    public static ArrayList<ObjectComponent> orderObjectPositionsByDepth(_Level level) {

        ArrayList<ObjectComponent> objectComponents = new ArrayList<>();

        addAllObjectPositionsToList(objectComponents, level.getLevelObject());
        addAllObjectPositionsToList(objectComponents, level.getSceneObject());
        addAllObjectPositionsToList(objectComponents, level.getResrcObject());
        addAllObjectPositionsToList(objectComponents, level.getAddinObject());
        addAllObjectPositionsToList(objectComponents, level.getTextObject());

        return objectComponents;

    }


    public static void drawLevelToCanvas(_Level level, Canvas canvas) {

        GraphicsContext graphicsContext = canvas.getGraphicsContext2D();

        ArrayList<ObjectComponent> objectPositionsOrderedByDepth = orderObjectPositionsByDepth(level);

        if (SelectionManager.getStrand1Gooball() != null) {
            double gameRelativeX = (SelectionManager.getMouseX() - level.getOffsetX()) / level.getZoom();
            double gameRelativeY = (SelectionManager.getMouseY() - level.getOffsetY()) / level.getZoom();
            addObjectPositionToListByDepth(objectPositionsOrderedByDepth, EffectsManager.getPlacingStrand(SelectionManager.getStrand1Gooball(), gameRelativeX, gameRelativeY));
        }

        for (ObjectComponent objectComponent : objectPositionsOrderedByDepth) {

            if (!objectComponent.isVisible()) continue;

            graphicsContext.save();

            boolean selected = false;
            for (EditorObject selectedObject : level.getSelected()) {
                if (List.of(selectedObject.getObjectComponents()).contains(objectComponent)) {
                    selected = true;
                    break;
                }
            }

            objectComponent.draw(graphicsContext, selected);

            // This part is necessary for additive + low opacity rendering to work correctly. :)
            // ex. GPU bitspew particles in Graphics Processing Unit will have a white background without this
            // TODO: figure out why this has any effect and come up with a better solution
            graphicsContext.setGlobalAlpha(1.0);
            graphicsContext.setFill(Color.TRANSPARENT);
            graphicsContext.fillRect(0, 0, 1, 1);

            graphicsContext.restore();

        }

    }

}
