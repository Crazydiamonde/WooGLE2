package com.woogleFX.engine.renderer;

import com.woogleFX.editorObjects.EditorObject;
import com.woogleFX.editorObjects.objectComponents.ImageComponent;
import com.woogleFX.editorObjects.objectComponents.ObjectComponent;
import com.woogleFX.editorObjects.splineGeom.SplineManager;
import com.woogleFX.engine.LevelManager;
import com.woogleFX.file.FileManager;
import com.woogleFX.file.resourceManagers.ResourceManager;
import com.woogleFX.gameData.level.GameVersion;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.transform.Affine;

import java.awt.geom.QuadCurve2D;
import java.io.FileNotFoundException;

public class EffectsManager {

    public static final String strandImageID = "IMAGE_BALL_GENERIC_ARM_INACTIVE";

    public static ObjectComponent getPlacingStrand(EditorObject goo1, double mouseX, double mouseY) {

        Image strandImage;

        try {
            if (!FileManager.getGameDir(GameVersion.NEW).isEmpty())
                strandImage = ResourceManager.getImage(null, strandImageID, GameVersion.NEW);
            else strandImage = ResourceManager.getImage(null, strandImageID, GameVersion.OLD);
        } catch (FileNotFoundException e) {
            return null;
        }

        return new ImageComponent() {
            public double getX() {
                double x1 = goo1.getAttribute("x").doubleValue();
                return (x1 + mouseX) / 2;
            }
            public double getY() {
                double y1 = -goo1.getAttribute("y").doubleValue();
                return (y1 + mouseY) / 2;
            }
            public double getRotation() {

                double x1 = goo1.getAttribute("x").doubleValue();
                double y1 = -goo1.getAttribute("y").doubleValue();

                return Math.PI / 2 + Renderer.angleTo(new Point2D(x1, y1), new Point2D(mouseX, mouseY));

            }
            public double getScaleX() {
                return 0.15;
            }
            public double getScaleY() {

                double x1 = goo1.getAttribute("x").doubleValue();
                double y1 = -goo1.getAttribute("y").doubleValue();

                return Math.hypot(mouseX - x1, mouseY - y1) / strandImage.getHeight();

            }
            public Image getImage() {
                return strandImage;
            }
            public double getDepth() {
                return 0.00000001;
            }
            public boolean isDraggable() {
                return false;
            }
            public boolean isResizable() {
                return false;
            }
            public boolean isRotatable() {
                return false;
            }
        };

    }


    public static void renderCurrentSpline(GraphicsContext graphicsContext) {

        graphicsContext.save();

        double offsetX = LevelManager.getLevel().getOffsetX();
        double offsetY = LevelManager.getLevel().getOffsetY();
        double zoom = LevelManager.getLevel().getZoom();

        Affine t = graphicsContext.getTransform();
        t.appendTranslation(offsetX, offsetY);
        t.appendScale(zoom, zoom);
        graphicsContext.setTransform(t);

        double width = 6 / zoom;

        graphicsContext.setLineWidth(width * zoom);
        if (SplineManager.getPointCount() > 1) {

            graphicsContext.beginPath();

            QuadCurve2D firstSegment = SplineManager.getQuadCurve(0);
            graphicsContext.moveTo(firstSegment.getX1(), firstSegment.getY1());

            int splineSegmentCount = SplineManager.getQuadCurveCount();
            for (int i = 0; i < splineSegmentCount; i++) {
                QuadCurve2D splineSegment = SplineManager.getQuadCurve(i);
                graphicsContext.quadraticCurveTo(
                        splineSegment.getCtrlX(), splineSegment.getCtrlY(),
                        splineSegment.getX2(), splineSegment.getY2()
                );
            }

            graphicsContext.setStroke(new Color(0.0, 0.25, 1.0, 1.0));
            graphicsContext.stroke();

            graphicsContext.setFill(new Color(0.0, 0.25, 1.0, 0.25));
            graphicsContext.fill();

            graphicsContext.setStroke(Renderer.selectionOutline);
            graphicsContext.setLineWidth(width * zoom / 2);
            for (int i = 0; i < splineSegmentCount; i++) {

                Point2D sp1 = SplineManager.getSplinePoint((i / 2) * 3);
                double x1 = sp1.getX();
                double y1 = sp1.getY();

                Point2D ctrl1 = SplineManager.getSplinePoint((i / 2) * 3 + 1);
                double ctrlX1 = ctrl1.getX();
                double ctrlY1 = ctrl1.getY();

                Point2D ctrl2 = SplineManager.getSplinePoint((i / 2) * 3 + 2);
                double ctrlX2 = ctrl2.getX();
                double ctrlY2 = ctrl2.getY();

                double x2 = (ctrlX1 + ctrlX2) / 2;
                double y2 = (ctrlY1 + ctrlY2) / 2;

                Point2D sp2 = SplineManager.getSplinePoint((i / 2) * 3 + 3);
                double x3 = sp2.getX();
                double y3 = sp2.getY();

                graphicsContext.strokeLine(x1, y1, ctrlX1, ctrlY1);
                graphicsContext.strokeLine(ctrlX2, ctrlY2, x3, y3);

            }

        }

        graphicsContext.setStroke(Renderer.selectionOutline);
        graphicsContext.setLineWidth(width);

        for (int i = 0; i < SplineManager.getPointCount(); i++) {
            Point2D splinePoint = SplineManager.getSplinePoint(i);
            graphicsContext.strokeOval(splinePoint.getX() - width/2, splinePoint.getY() - width/2, width, width);
            if (i == SplineManager.getSelected1() || i == SplineManager.getSelected2()) {
                graphicsContext.setLineWidth(1 / zoom);
                graphicsContext.strokeRect(splinePoint.getX() - width/2 - 4/zoom, splinePoint.getY() - width/2 - 4/zoom, width + 8 / zoom, width + 8 / zoom);
                graphicsContext.setLineWidth(width);
            }
        }

        graphicsContext.restore();

    }

}
