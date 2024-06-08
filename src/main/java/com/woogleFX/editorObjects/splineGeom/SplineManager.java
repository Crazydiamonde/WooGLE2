package com.woogleFX.editorObjects.splineGeom;

import com.woogleFX.engine.LevelManager;
import com.woogleFX.engine.SelectionManager;
import com.woogleFX.gameData.level._Level;
import javafx.geometry.Point2D;

import java.awt.geom.QuadCurve2D;
import java.util.ArrayList;

public class SplineManager {

    /**  */
    private static final ArrayList<QuadCurve2D> spline = new ArrayList<>();
    public static int getQuadCurveCount() {
        return spline.size();
    }
    public static QuadCurve2D getQuadCurve(int i) {
        return spline.get(i);
    }
    public static void clear() {
        spline.clear();
        splineInitialPoint = null;
        splineControlPoint = null;
    }


    private static Point2D splineInitialPoint = null;
    public static Point2D getSplineInitialPoint() {
        return splineInitialPoint;
    }
    public static void setSplineInitialPoint(Point2D splineInitialPoint) {
        SplineManager.splineInitialPoint = splineInitialPoint;
    }


    private static Point2D splineControlPoint = null;
    public static Point2D getSplineControlPoint() {
        return splineControlPoint;
    }
    public static void setSplineControlPoint(Point2D splineControlPoint) {
        SplineManager.splineControlPoint = splineControlPoint;
    }


    public static void addPoint(double x, double y) {
        if (splineInitialPoint == null) splineInitialPoint = new Point2D(x, y);
        else if (splineControlPoint == null) splineControlPoint = new Point2D(x, y);
        else {
            Point2D initialPoint;
            if (spline.isEmpty()) {
                initialPoint = splineInitialPoint;
            } else {
                QuadCurve2D lastSplineSegment = spline.get(spline.size() - 1);
                initialPoint = new Point2D(lastSplineSegment.getX2(), lastSplineSegment.getY2());
            }
            spline.add(new QuadCurve2D.Double(
                    initialPoint.getX(), initialPoint.getY(),
                    splineControlPoint.getX(), splineControlPoint.getY(),
                    x, y)
            );
            splineControlPoint = null;
        }
    }


    public static void addPolygonPoint(double x, double y) {
        if (splineInitialPoint == null) splineInitialPoint = new Point2D(x, y);
        else {
            Point2D initialPoint;
            if (spline.isEmpty()) {
                initialPoint = splineInitialPoint;
            } else {
                QuadCurve2D lastSplineSegment = spline.get(spline.size() - 1);
                initialPoint = new Point2D(lastSplineSegment.getX2(), lastSplineSegment.getY2());
            }
            Point2D middlePoint = new Point2D((initialPoint.getX() + x) / 2, (initialPoint.getY() + y) / 2);
            spline.add(new QuadCurve2D.Double(
                    initialPoint.getX(), initialPoint.getY(),
                    middlePoint.getX(), middlePoint.getY(),
                    x, y)
            );
            splineControlPoint = null;
        }
    }


    private static QuadCurve2D selectedCurve;
    public static QuadCurve2D getSelectedCurve() {
        return selectedCurve;
    }

    private static int selectedPoint;
    public static int getSelectedPoint() {
        return selectedPoint;
    }

    private static double offsetX;
    public static double getOffsetX() {
        return offsetX;
    }

    private static double offsetY;
    public static double getOffsetY() {
        return offsetY;
    }

    public static void select(QuadCurve2D _selectedCurve, int _selectedPoint) {
        selectedCurve = _selectedCurve;
        selectedPoint = _selectedPoint;
        Point2D point;
        if (selectedCurve != null) {
            point = switch (selectedPoint) {
                case 0 -> new Point2D(selectedCurve.getX1(), selectedCurve.getY1());
                case 1 -> new Point2D(selectedCurve.getCtrlX(), selectedCurve.getCtrlY());
                case 2 -> new Point2D(selectedCurve.getX2(), selectedCurve.getY2());
                default -> null;
            };
        } else {
            if (selectedPoint == 0) point = splineInitialPoint;
            else if (selectedPoint == 1) point = splineControlPoint;
            else point = null;
        }
        if (point != null) {
            _Level level = LevelManager.getLevel();
            double gameRelativeMouseX = (SelectionManager.getMouseX() - level.getOffsetX()) / level.getZoom();
            double gameRelativeMouseY = (SelectionManager.getMouseY() - level.getOffsetY()) / level.getZoom();
            offsetX = point.getX() - gameRelativeMouseX;
            offsetY = point.getY() - gameRelativeMouseY;
        }
    }

}
