package com.woogleFX.editorObjects.splineGeom;

import com.woogleFX.engine.LevelManager;
import com.woogleFX.engine.SelectionManager;
import com.woogleFX.gameData.level._Level;
import javafx.geometry.Point2D;

import java.awt.geom.QuadCurve2D;

public class SplineManager {

    /**  */
    private static Point2D[] spline = new Point2D[0];
    public static int getQuadCurveCount() {
        return ((spline.length - 1) / 3) * 2;
    }
    public static int getPointCount() {
        return spline.length;
    }
    public static Point2D getSplinePoint(int i) {
        return spline[i];
    }
    public static void setSplinePoint(int i, double x, double y) {
        spline[i] = new Point2D(x, y);
    }
    public static QuadCurve2D getQuadCurve(int i) {

        Point2D sp1 = spline[(i / 2) * 3];
        double x1 = sp1.getX();
        double y1 = sp1.getY();

        Point2D ctrl1 = spline[(i / 2) * 3 + 1];
        double ctrlX1 = ctrl1.getX();
        double ctrlY1 = ctrl1.getY();

        Point2D ctrl2 = spline[(i / 2) * 3 + 2];
        double ctrlX2 = ctrl2.getX();
        double ctrlY2 = ctrl2.getY();

        double x2 = (ctrlX1 + ctrlX2) / 2;
        double y2 = (ctrlY1 + ctrlY2) / 2;

        Point2D sp2 = spline[(i / 2) * 3 + 3];
        double x3 = sp2.getX();
        double y3 = sp2.getY();

        if (i % 2 == 0) return new QuadCurve2D.Double(x1, y1, ctrlX1, ctrlY1, x2, y2);
        else            return new QuadCurve2D.Double(x2, y2, ctrlX2, ctrlY2, x3, y3);
    }
    public static void clear() {
        spline = new Point2D[0];
        selected1 = -1;
        selected2 = -1;
    }


    public static void addPoint(double x1, double y1, double x2, double y2, double x3, double y3, int i) {
        if (spline.length == 0) {
            spline = new Point2D[]{new Point2D(x3, y3)};
            return;
        }
        Point2D[] newSpline = new Point2D[spline.length + 3];
        if (i == spline.length + 2) {
            System.arraycopy(spline, 0, newSpline, 0, i - 2);
            newSpline[i - 2] = new Point2D(x1, y1);
            newSpline[i - 1] = new Point2D(x2, y2);
            newSpline[i] = new Point2D(x3, y3);
        } else {
            System.arraycopy(spline, 0, newSpline, 0, i - 1);
            newSpline[i - 1] = new Point2D(x1, y1);
            newSpline[i] = new Point2D(x2, y2);
            newSpline[i + 1] = new Point2D(x3, y3);
            System.arraycopy(spline, i - 1, newSpline, i + 2, spline.length - i + 1);
        }
        spline = newSpline;
    }


    public static void removePoint(int i) {
        if (selected1 == i - 1) selected1 = -1;
        if (selected1 == i) selected1 = -1;
        if (selected1 == i + 1) selected1 = -1;
        if (selected2 == i - 1) selected2 = -1;
        if (selected2 == i) selected2 = -1;
        if (selected2 == i + 1) selected2 = -1;
        if (spline.length == 1) {
            spline = new Point2D[0];
            return;
        }
        Point2D[] newSpline = new Point2D[spline.length - 3];
        if (i == spline.length - 1) {
            System.arraycopy(spline, 0, newSpline, 0, i - 2);
        } else {
            System.arraycopy(spline, 0, newSpline, 0, i - 1);
            System.arraycopy(spline, i + 2, newSpline, i - 1, spline.length - i - 2);
        }
        spline = newSpline;
    }


    private static int selected1;
    public static int getSelected1() {
        return selected1;
    }

    private static double selected1OriginalX;
    public static double getSelected1OriginalX() {
        return selected1OriginalX;
    }

    private static double selected1OriginalY;
    public static double getSelected1OriginalY() {
        return selected1OriginalY;
    }


    private static int selected2;
    public static int getSelected2() {
        return selected2;
    }

    private static double selected2OriginalX;
    public static double getSelected2OriginalX() {
        return selected2OriginalX;
    }

    private static double selected2OriginalY;
    public static double getSelected2OriginalY() {
        return selected2OriginalY;
    }


    private static double selectedDistance;
    public static double getSelectedDistance() {
        return selectedDistance;
    }

    private static double offsetX;
    public static double getOffsetX() {
        return offsetX;
    }

    private static double offsetY;
    public static double getOffsetY() {
        return offsetY;
    }


    public static void select(int i, double distance) {

        if (i == -1) {
            selected1 = -1;
            selected2 = -1;
            return;
        }

        if (distance < 0.7) {
            selected1 = i;
            selected1OriginalX = spline[i].getX();
            selected1OriginalY = spline[i].getY();
        } else selected1 = -1;

        if (distance > 0.3) {
            selected2 = i + 1;
            selected2OriginalX = spline[i + 1].getX();
            selected2OriginalY = spline[i + 1].getY();
        } else selected2 = -1;

        selectedDistance = distance;

        _Level level = LevelManager.getLevel();
        double gameRelativeMouseX = (SelectionManager.getMouseX() - level.getOffsetX()) / level.getZoom();
        double gameRelativeMouseY = (SelectionManager.getMouseY() - level.getOffsetY()) / level.getZoom();
        offsetX = gameRelativeMouseX;
        offsetY = gameRelativeMouseY;

    }

}
