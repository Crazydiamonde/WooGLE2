package com.woogleFX.editorObjects.splineGeom;

import com.woogleFX.editorObjects.EditorObject;
import com.woogleFX.editorObjects.objectCreators.ObjectCreator;
import com.woogleFX.engine.LevelManager;
import com.woogleFX.engine.undoHandling.UndoManager;
import com.woogleFX.engine.undoHandling.userActions.ObjectCreationAction;
import com.woogleFX.engine.undoHandling.userActions.UserAction;

import java.awt.geom.Point2D;
import java.awt.geom.QuadCurve2D;
import java.util.ArrayList;
import java.util.List;

public class SplineGeometryPlacer {

    private static final double STEPS_PER_SPLINE_SEGMENT_LENGTH = 0.0125;

    private static final double TOLERANCE = 2.0;


    private static int getStepCount(double length) {
        return (int)(Math.sqrt(length) * 2.0);
    }


    private static class Geometry {

    }


    private static class Circle extends Geometry {

        private final double x;
        private final double y;
        private final double radius;
        public Circle(double x, double y, double radius) {
            this.x = x;
            this.y = y;
            this.radius = radius;
        }

    }


    private static class Rectangle extends Geometry {

        private final double x;
        private final double y;
        private final double width;
        private final double height;
        private final double rotation;
        public Rectangle(double x, double y, double width, double height, double rotation) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.rotation = rotation;
        }

    }


    private static double getSplineSegmentLength(QuadCurve2D splineSegment) {

        double x1 = splineSegment.getX1();
        double y1 = splineSegment.getY1();
        double x2 = splineSegment.getCtrlX();
        double y2 = splineSegment.getCtrlY();
        double x3 = splineSegment.getX2();
        double y3 = splineSegment.getY2();

        double A = 2*x1 - 4*x2 + 2*x3;
        double B = 2*x2 - 2*x1;
        double C = 2*y1 - 4*y2 + 2*y3;
        double D = 2*y2 - 2*y1;

        double a = A*A + C*C;
        double b = 2*A+B + 2*C*D;
        double c = B*B + D*D;

        double m1 = 2 * a + b;
        double m2 = Math.sqrt(a+b+c);
        double m3 = (2*Math.sqrt(a*c)+b)/(2*Math.sqrt(a)*m2+m1);

        double exp1 = (m1*m2-b*Math.sqrt(c))/(4*a);
        double exp2 = ((b*b-4*a*c)*Math.log(m3))/(8*Math.pow(a,1.5));

        if (a == 0 || exp1 != exp1 || exp2 != exp2 || Math.abs(exp1) > 1000000 || Math.abs(exp2) > 1000000) return Math.hypot(x3 - x1, y3 - y1);

        return exp1 + exp2;

    }


    private static Point2D getPointOnSplineSegment(QuadCurve2D splineSegment, double s) {

        double x1 = splineSegment.getX1();
        double y1 = splineSegment.getY1();
        double x2 = splineSegment.getCtrlX();
        double y2 = splineSegment.getCtrlY();
        double x3 = splineSegment.getX2();
        double y3 = splineSegment.getY2();

        double x = s*s*(x1 - 2*x2 + x3) + s*(2*x2 - 2*x1) + x1;
        double y = s*s*(y1 - 2*y2 + y3) + s*(2*y2 - 2*y1) + y1;

        return new Point2D.Double(x, y);

    }


    private static double getSplineSegmentSatisfaction(QuadCurve2D splineSegment, ArrayList<Geometry> geometryList) {

        int numSteps = getStepCount(getSplineSegmentLength(splineSegment));

        double satisfiedPointCount = 0;

        for (int stepIndex = 0; stepIndex < numSteps; stepIndex++) {

            double s = (double) stepIndex / numSteps;
            Point2D stepPoint = getPointOnSplineSegment(splineSegment, s);

            for (Geometry geometry : geometryList) {

                if (geometry instanceof Circle circle) {

                    double x = circle.x;
                    double y = circle.y;
                    double r = circle.radius;

                    double distance = Math.hypot(stepPoint.getX() - x, stepPoint.getY() - y);
                    if (Math.abs(distance - r) < TOLERANCE) {
                        satisfiedPointCount++;
                        break;
                    }

                } else if (geometry instanceof Rectangle rectangle) {

                    double x = rectangle.x;
                    double y = rectangle.y;
                    double width = rectangle.width;
                    double height = rectangle.height;
                    double rotation = rectangle.rotation;

                    double deltaX = stepPoint.getX() - x;
                    double deltaY = stepPoint.getY() - y;

                    double rotatedDeltaX = deltaX * Math.cos(-rotation) - deltaY * Math.sin(-rotation);
                    double rotatedDeltaY = deltaY * Math.cos(-rotation) + deltaX * Math.sin(-rotation);

                    if (Math.abs(rotatedDeltaX) < width/2 + TOLERANCE && Math.abs(rotatedDeltaY) < height/2 + TOLERANCE &&
                            (Math.abs(Math.abs(rotatedDeltaX) - width/2) < TOLERANCE || Math.abs(Math.abs(rotatedDeltaY) - height/2) < TOLERANCE)) {
                        satisfiedPointCount++;
                        break;
                    }

                }

            }

        }

        return satisfiedPointCount;

    }


    private static boolean isPointInsideSpline(double x, double y) {

        int splineIntersections = 0;

        int splineSegmentCount = SplineManager.getQuadCurveCount();
        for (int splineSegmentIndex = 0; splineSegmentIndex < splineSegmentCount; splineSegmentIndex++) {

            QuadCurve2D splineSegment = SplineManager.getQuadCurve(splineSegmentIndex);

            double y1 = splineSegment.getY1();
            double y2 = splineSegment.getCtrlY();
            double y3 = splineSegment.getY2();

            double A = y1 - 2 * y2 + y3;
            double B = 2 * y2 - 2 * y1;
            double C = y1 - y;

            if (A == 0) A = 0.00000000001;

            double disc = B * B - 4 * A * C;

            if (disc <= 0) continue;

            for (int j : new int[]{-1, 1}) {

                double s = (-B + j * Math.sqrt(disc)) / (2 * A);

                if (s >= 0 && s < 1 && getPointOnSplineSegment(splineSegment, s).getX() > x) splineIntersections++;

            }

        }

        return splineIntersections % 2 == 1;

    }


    private static boolean isRectangleInsideSpline(double x, double y, double w, double h, double r) {

        double x1 = x + w/2 * Math.cos(r) - h/2 * Math.sin(r);
        double y1 = y + h/2 * Math.cos(r) + w/2 * Math.sin(r);

        double x2 = x - w/2 * Math.cos(r) - h/2 * Math.sin(r);
        double y2 = y + h/2 * Math.cos(r) - w/2 * Math.sin(r);

        double x3 = x + w/2 * Math.cos(r) + h/2 * Math.sin(r);
        double y3 = y - h/2 * Math.cos(r) + w/2 * Math.sin(r);

        double x4 = x - w/2 * Math.cos(r) + h/2 * Math.sin(r);
        double y4 = y - h/2 * Math.cos(r) - w/2 * Math.sin(r);

        return
            isPointInsideSpline(x1, y1) &&
            isPointInsideSpline(x2, y2) &&
            isPointInsideSpline(x3, y3) &&
            isPointInsideSpline(x4, y4);

    }


    public static void fillCurrentSplineWithGeometry() {

        ArrayList<Geometry> allGeometryList = new ArrayList<>();

        int splineSegmentCount = SplineManager.getQuadCurveCount();

        double minX = Double.POSITIVE_INFINITY;
        double minY = Double.POSITIVE_INFINITY;
        double maxX = Double.NEGATIVE_INFINITY;
        double maxY = Double.NEGATIVE_INFINITY;

        for (int splineSegmentIndex = 0; splineSegmentIndex < splineSegmentCount; splineSegmentIndex++) {

            QuadCurve2D splineSegment = SplineManager.getQuadCurve(splineSegmentIndex);

            double x1 = splineSegment.getX1();
            double y1 = splineSegment.getY1();
            double x2 = splineSegment.getCtrlX();
            double y2 = splineSegment.getCtrlY();
            double x3 = splineSegment.getX2();
            double y3 = splineSegment.getY2();

            double A = x1 - 2*x2 + x3;
            double B = 2*x2 - 2*x1;
            double C = y1 - 2*y2 + y3;
            double D = 2*y2 - 2*y1;

            for (double extremeXS : new double[]{0, -B/(2*A), 1}) {
                if (extremeXS < 0 || extremeXS > 1) continue;
                Point2D point2D = getPointOnSplineSegment(splineSegment, extremeXS);
                if (point2D.getX() < minX) minX = point2D.getX();
                if (point2D.getX() > maxX) maxX = point2D.getX();
            }

            for (double extremeYS : new double[]{0, -D/(2*C), 1}) {
                if (extremeYS < 0 || extremeYS > 1) continue;
                Point2D point2D = getPointOnSplineSegment(splineSegment, extremeYS);
                if (point2D.getY() < minY) minY = point2D.getY();
                if (point2D.getY() > maxY) maxY = point2D.getY();
            }

        }

        double[][] maxRadii = new double[(int)(maxY - minY + 2)][(int)(maxX - minX + 2)];

        for (int y = (int) minY; y <= maxY; y++) {
            for (int x = (int) minX; x <= maxX; x++) {

                if (!isPointInsideSpline(x, y)) continue;

                double minRadius = Double.POSITIVE_INFINITY;

                for (int splineSegmentIndex = 0; splineSegmentIndex < splineSegmentCount; splineSegmentIndex++) {

                    QuadCurve2D splineSegment = SplineManager.getQuadCurve(splineSegmentIndex);

                    int numSteps = getStepCount(getSplineSegmentLength(splineSegment));

                    for (int stepIndex = 0; stepIndex < numSteps; stepIndex++) {

                        double s = (double) stepIndex / numSteps;
                        Point2D stepPoint = getPointOnSplineSegment(splineSegment, s);

                        double distance = Math.hypot(stepPoint.getX() - x, stepPoint.getY() - y);
                        if (distance < minRadius) minRadius = distance;

                    }

                }

                maxRadii[y - (int)minY][x - (int)minX] = minRadius;

            }
        }

        for (int splineSegmentIndex = 0; splineSegmentIndex < splineSegmentCount; splineSegmentIndex++) {

            QuadCurve2D splineSegment = SplineManager.getQuadCurve(splineSegmentIndex);

            double x1 = splineSegment.getX1();
            double y1 = splineSegment.getY1();
            double x2 = splineSegment.getCtrlX();
            double y2 = splineSegment.getCtrlY();
            double x3 = splineSegment.getX2();
            double y3 = splineSegment.getY2();

            double A = x1 - 2*x2 + x3;
            double B = 2*x2 - 2*x1;
            double C = y1 - 2*y2 + y3;
            double D = 2*y2 - 2*y1;

            int numSteps = getStepCount(getSplineSegmentLength(splineSegment));

            Circle[] circles = new Circle[numSteps];

            for (int stepIndex = 0; stepIndex < numSteps; stepIndex++) {

                double bestSatisfaction = Double.NEGATIVE_INFINITY;
                Circle bestCircle = null;

                double s = (double) stepIndex / numSteps;
                Point2D stepPoint = getPointOnSplineSegment(splineSegment, s);

                double theta = -Math.PI/2 + Math.atan2(2*C*s+D, 2*A*s+B);

                for (int radius = 16; ; radius++) {

                    double circleX = stepPoint.getX() + radius * Math.cos(theta);
                    double circleY = stepPoint.getY() + radius * Math.sin(theta);

                    if (circleX < minX || circleY < minY || circleX >= maxX || circleY >= maxY) break;

                    if (maxRadii[(int)(circleY - minY)][(int)(circleX - minX)] < radius) continue;

                    Circle circle = new Circle(circleX, circleY, radius);
                    double satisfaction = getSplineSegmentSatisfaction(splineSegment, new ArrayList<>(List.of(circle)));

                    if (satisfaction > bestSatisfaction - 4) {
                        bestSatisfaction = satisfaction;
                        bestCircle = circle;
                    }

                }

                circles[stepIndex] = bestCircle;

            }


            Rectangle[] rectangles = new Rectangle[numSteps];

            for (int stepIndex = 0; stepIndex < numSteps; stepIndex++) {

                double bestSatisfaction = Double.NEGATIVE_INFINITY;
                Rectangle bestRectangle = null;

                double s = (double) stepIndex / numSteps;
                Point2D stepPoint = getPointOnSplineSegment(splineSegment, s);

                double theta = -Math.PI/2 + Math.atan2(2*C*s+D, 2*A*s+B);

                for (int h = 8; h < 100; h++) {

                    double w = h * 4;

                    double rectangleX = stepPoint.getX() + (h/2.0) * Math.cos(theta);
                    double rectangleY = stepPoint.getY() + (h/2.0) * Math.sin(theta);

                    if (!isRectangleInsideSpline(rectangleX, rectangleY, w, h, Math.PI/2 + theta)) continue;

                    Rectangle rectangle = new Rectangle(rectangleX, rectangleY, w, h, Math.PI/2 + theta);
                    double satisfaction = getSplineSegmentSatisfaction(splineSegment, new ArrayList<>(List.of(rectangle)));

                    if (satisfaction > bestSatisfaction - 4) {
                        bestSatisfaction = satisfaction;
                        bestRectangle = rectangle;
                    }

                }

                rectangles[stepIndex] = bestRectangle;

            }


            double currentSatisfaction = Double.NEGATIVE_INFINITY;

            while (true) {

                Geometry bestGeometry = null;

                for (int stepIndex = 0; stepIndex < numSteps; stepIndex++) {

                    Circle newCircle = circles[stepIndex];
                    if (newCircle == null) continue;

                    ArrayList<Geometry> newGeometry = new ArrayList<>(allGeometryList);
                    newGeometry.add(newCircle);

                    double newSatisfaction = getSplineSegmentSatisfaction(splineSegment, newGeometry);

                    if (newSatisfaction > currentSatisfaction) {
                        currentSatisfaction = newSatisfaction;
                        bestGeometry = newCircle;
                    }

                }

                for (int stepIndex = 0; stepIndex < numSteps; stepIndex++) {

                    Rectangle newRectangle = rectangles[stepIndex];
                    if (newRectangle == null) continue;

                    ArrayList<Geometry> newGeometry = new ArrayList<>(allGeometryList);
                    newGeometry.add(newRectangle);

                    double newSatisfaction = getSplineSegmentSatisfaction(splineSegment, newGeometry);

                    if (newSatisfaction > currentSatisfaction) {
                        currentSatisfaction = newSatisfaction;
                        bestGeometry = newRectangle;
                    }

                }

                if (bestGeometry == null) break;

                allGeometryList.add(bestGeometry);

            }

        }

        ArrayList<UserAction> undoBuilder = new ArrayList<>();

        for (Geometry geometry : allGeometryList) {

            EditorObject geometryObject;

            if (geometry instanceof Circle circle) {

                geometryObject = ObjectCreator.create("circle", LevelManager.getLevel().getSceneObject(), LevelManager.getLevel().getVersion());
                geometryObject.setAttribute("x", circle.x);
                geometryObject.setAttribute("y", -circle.y);
                geometryObject.setAttribute("radius", circle.radius);

            } else if (geometry instanceof Rectangle rectangle) {

                geometryObject = ObjectCreator.create("rectangle", LevelManager.getLevel().getSceneObject(), LevelManager.getLevel().getVersion());
                geometryObject.setAttribute("x", rectangle.x);
                geometryObject.setAttribute("y", -rectangle.y);
                geometryObject.setAttribute("width", rectangle.width);
                geometryObject.setAttribute("height", rectangle.height);
                geometryObject.setAttribute("rotation", -rectangle.rotation);

            } else continue;

            LevelManager.getLevel().getScene().add(geometryObject);

            undoBuilder.add(new ObjectCreationAction(geometryObject, LevelManager.getLevel().getSceneObject().getChildren().indexOf(geometryObject)));

        }

        ArrayList<UserAction> reverse = new ArrayList<>();
        for (int i = undoBuilder.size() - 1; i >= 0; i--) reverse.add(undoBuilder.get(i));
        UndoManager.registerChange(reverse.toArray(new UserAction[0]));

    }

}
