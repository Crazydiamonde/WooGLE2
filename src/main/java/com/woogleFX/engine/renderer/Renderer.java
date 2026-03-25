package com.woogleFX.engine.renderer;

import java.util.ArrayList;

import com.woogleFX.assets.*;
import com.woogleFX.assets.wog1.ball.WOG1Ball;
import com.woogleFX.editorObjects.EditorObject;
import com.woogleFX.editorObjects.objectComponents.ObjectComponent;
import com.woogleFX.engine.SelectionManager;
import com.woogleFX.engine.fx.FXCanvas;
import com.woogleFX.engine.AssetManager;

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

        Asset level = AssetManager.getAsset();
        Canvas canvas = FXCanvas.getCanvas();

        if (level != null) {
            canvas.getGraphicsContext2D().clearRect(-5000000, -5000000, 10000000, 10000000);
            if (level instanceof HasBackground hasBackground) {
                canvas.getGraphicsContext2D().setFill(hasBackground.getBackgroundColor());
                canvas.getGraphicsContext2D().fillRect(-5000000, -5000000, 10000000, 10000000);
            }
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

        if (objectComponent == null || !objectComponent.isVisible()) return;

        int i = 0;
        //while (i < objectComponents.size() && objectComponents.get(i).getDepth() <= objectComponent.getDepth()) {
        //    i++;
        //}

        objectComponents.add(i, objectComponent);

    }


    public static ArrayList<ObjectComponent> orderObjectPositionsByDepth(Asset level) {

        ArrayList<ObjectComponent> objectComponents = new ArrayList<>();

        for (EditorObject editorObject : level.getObjects()) {
            for (ObjectComponent objectComponent : editorObject.getObjectComponents()) {
                addObjectPositionToListByDepth(objectComponents, objectComponent);
            }
        }

        if (SelectionManager.getStrand1Gooball() != null) {
            double gameRelativeX = (SelectionManager.getMouseX() - level.getOffsetX()) / level.getZoom();
            double gameRelativeY = (SelectionManager.getMouseY() - level.getOffsetY()) / level.getZoom();
            addObjectPositionToListByDepth(objectComponents, EffectsManager.getPlacingStrand(SelectionManager.getStrand1Gooball(), gameRelativeX, gameRelativeY));
        }

        objectComponents.sort((o1, o2) -> {
            if (o2.getGlobalLayer() != o1.getGlobalLayer())
                return o2.getGlobalLayer().ordinal() - o1.getGlobalLayer().ordinal();
            else return (int)Math.signum(o2.getDepth() - o1.getDepth());
        });

        return objectComponents;

    }


    public static void drawLevelToCanvas(Asset level, Canvas canvas) {

        GraphicsContext graphicsContext = canvas.getGraphicsContext2D();

        ArrayList<ObjectComponent> objectPositionsOrderedByDepth = orderObjectPositionsByDepth(level);

        for (int i = objectPositionsOrderedByDepth.size() - 1; i >= 0; i--) {
            ObjectComponent objectComponent = objectPositionsOrderedByDepth.get(i);

            if (!objectComponent.isVisible()) continue;

            graphicsContext.save();

            objectComponent.draw(graphicsContext);

            // This part is necessary for additive + low opacity rendering to work correctly. :)
            // ex. GPU bitspew particles in Graphics Processing Unit will have a white background without this
            // TODO: figure out why this has any effect and come up with a better solution
            graphicsContext.setGlobalAlpha(1.0);
            graphicsContext.setFill(Color.TRANSPARENT);
            graphicsContext.fillRect(0, 0, 1, 1);

            graphicsContext.restore();

        }

        for (ObjectComponent objectComponent : level.getSelectedComponents()) {

            if (!objectComponent.isVisible()) continue;

            graphicsContext.save();
            objectComponent.drawSelectionOutline(graphicsContext);
            graphicsContext.restore();
        }

    }

}
