package com.woogleFX.editorObjects.splineGeom;

import com.woogleFX.editorObjects.EditorObject;
import com.woogleFX.editorObjects.objectCreators.ObjectCreator;
import com.woogleFX.engine.LevelManager;
import com.woogleFX.engine.undoHandling.UndoManager;
import com.woogleFX.engine.undoHandling.userActions.DeleteSplinePointAction;
import com.woogleFX.engine.undoHandling.userActions.ObjectCreationAction;
import com.woogleFX.engine.undoHandling.userActions.UserAction;
import javafx.geometry.Point2D;

import java.awt.geom.QuadCurve2D;
import java.util.ArrayList;
import java.util.List;

public class SplineGeometryPlacer {

    private static final double TOLERANCE = 0.001;


    private static int getStepCount(double length) {
        return (int)(length * 20 + 10);
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


    private record Bounds(double minX, double minY, double maxX, double maxY) {

    }


    public static double getSplineSegmentLength(QuadCurve2D splineSegment) {

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


    public static Point2D getPointOnSplineSegment(QuadCurve2D splineSegment, double s) {

        double x1 = splineSegment.getX1();
        double y1 = splineSegment.getY1();
        double x2 = splineSegment.getCtrlX();
        double y2 = splineSegment.getCtrlY();
        double x3 = splineSegment.getX2();
        double y3 = splineSegment.getY2();

        double x = s*s*(x1 - 2*x2 + x3) + s*(2*x2 - 2*x1) + x1;
        double y = s*s*(y1 - 2*y2 + y3) + s*(2*y2 - 2*y1) + y1;

        return new Point2D(x, y);

    }


    private static double getForwardAngleOnSplineSegment(QuadCurve2D splineSegment, double s) {

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

        return Math.atan2(2*C*s+D, 2*A*s+B);

    }


    private static double getInwardAngleOnSplineSegment(QuadCurve2D splineSegment, double s) {

        double forwardAngle = getForwardAngleOnSplineSegment(splineSegment, s);

        Point2D splinePoint = getPointOnSplineSegment(splineSegment, s);

        double x1 = splinePoint.getX() + 0.000001 * Math.cos(forwardAngle + Math.PI/2) - 0.000001 * Math.sin(forwardAngle + Math.PI/2);
        double y1 = splinePoint.getY() + 0.000001 * Math.sin(forwardAngle + Math.PI/2) + 0.000001 * Math.cos(forwardAngle + Math.PI/2);

        if (isPointInsideSpline(x1, y1)) return forwardAngle + Math.PI/2;
        else return forwardAngle - Math.PI/2;

    }


    private static Bounds getSplineBounds() {

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

        return new Bounds(minX, minY, maxX, maxY);

    }


    private static double getSplineSegmentSatisfaction(QuadCurve2D splineSegment, ArrayList<Geometry> geometryList) {

        int numSteps = getStepCount(getSplineSegmentLength(splineSegment) * 4);

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

                    if (Math.abs(rotatedDeltaX) < width/2 + TOLERANCE && Math.abs(rotatedDeltaY) < height/2 + TOLERANCE) {
                        satisfiedPointCount++;
                        break;
                    }

                }

            }

        }

        return satisfiedPointCount;

    }


    private static boolean isPointInsideSpline(double _x, double _y) {

        Bounds splineBounds = getSplineBounds();

        double width = splineBounds.maxX - splineBounds.minX;
        double height = splineBounds.maxY - splineBounds.minY;

        double size = Math.max(width, height);

        double x = _x * size + splineBounds.minX;
        double y = _y * size + splineBounds.minY;

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

            if (Math.abs(A) < 0.01) {
                double x1 = splineSegment.getX1();
                double x3 = splineSegment.getX2();

                if (y < Math.min(y1, y3) || y > Math.max(y1, y3)) continue;

                double deltaX = x3 - x1;
                if (Math.abs(deltaX) < 0.01) {
                    if (x1 > x) splineIntersections++;
                    continue;
                }
                double deltaY = y3 - y1;
                if (x1 + (y - y1) * deltaX / deltaY > x) splineIntersections++;
                continue;
            }

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

        for (double s = 0; s < 1; s += 0.025) {

            double x1 = x + w/2 * Math.cos(r) - h*s/2 * Math.sin(r);
            double y1 = y + h*s/2 * Math.cos(r) + w/2 * Math.sin(r);

            double x2 = x - w*s/2 * Math.cos(r) - h/2 * Math.sin(r);
            double y2 = y + h/2 * Math.cos(r) - w*s/2 * Math.sin(r);

            double x3 = x + w*s/2 * Math.cos(r) + h/2 * Math.sin(r);
            double y3 = y - h/2 * Math.cos(r) + w*s/2 * Math.sin(r);

            double x4 = x - w/2 * Math.cos(r) + h*s/2 * Math.sin(r);
            double y4 = y - h*s/2 * Math.cos(r) - w/2 * Math.sin(r);

            if (
                !isPointInsideSpline(x1, y1) ||
                !isPointInsideSpline(x2, y2) ||
                !isPointInsideSpline(x3, y3) ||
                !isPointInsideSpline(x4, y4)
            ) return false;

        }

        return true;

    }


    private static boolean isCircleInsideSpline(double x, double y, double r) {

        for (double theta = 0; theta < 6.283; theta += 0.001) {

            if (!isPointInsideSpline(x + r * Math.cos(theta), y + r * Math.sin(theta))) return false;

        }

        return true;

    }


    private static int numCompleted;
    private static synchronized void addCompletion() {
        numCompleted++;
    }


    public static void fillCurrentSplineWithGeometry() {

        ArrayList<ArrayList<Geometry>> allGeometryList = new ArrayList<>();

        int splineSegmentCount = SplineManager.getQuadCurveCount();

        Bounds splineBounds = getSplineBounds();

        double width = splineBounds.maxX - splineBounds.minX;
        double height = splineBounds.maxY - splineBounds.minY;

        double size = Math.max(width, height);

        numCompleted = 0;

        for (int splineSegmentIndex = 0; splineSegmentIndex < splineSegmentCount; splineSegmentIndex++) {
            allGeometryList.add(new ArrayList<>());
            int finalSplineSegmentIndex = splineSegmentIndex;
            QuadCurve2D splineSegment = SplineManager.getQuadCurve(finalSplineSegmentIndex);

            double x1 = splineSegment.getX1();
            double y1 = splineSegment.getY1();
            double x2 = splineSegment.getCtrlX();
            double y2 = splineSegment.getCtrlY();
            double x3 = splineSegment.getX2();
            double y3 = splineSegment.getY2();

            QuadCurve2D newSplineSegment = new QuadCurve2D.Double(
                    (x1 - splineBounds.minX) / size,
                    (y1 - splineBounds.minY) / size,
                    (x2 - splineBounds.minX) / size,
                    (y2 - splineBounds.minY) / size,
                    (x3 - splineBounds.minX) / size,
                    (y3 - splineBounds.minY) / size
            );

            new Thread(() -> fillSplineSegmentWithGeometry(newSplineSegment, allGeometryList.get(finalSplineSegmentIndex))).start();
        }

        while (numCompleted < splineSegmentCount) Thread.onSpinWait();

        ArrayList<Geometry> separated = new ArrayList<>();
        for (ArrayList<Geometry> list : allGeometryList) separated.addAll(list);

        /*
        for (int splineSegmentIndex = 0; splineSegmentIndex < splineSegmentCount; splineSegmentIndex++) {
            allGeometryList.add(new ArrayList<>());
            QuadCurve2D splineSegment = SplineManager.getQuadCurve(splineSegmentIndex);

            double x1 = splineSegment.getX1();
            double y1 = splineSegment.getY1();
            double x2 = splineSegment.getCtrlX();
            double y2 = splineSegment.getCtrlY();
            double x3 = splineSegment.getX2();
            double y3 = splineSegment.getY2();

            QuadCurve2D newSplineSegment = new QuadCurve2D.Double(
                    (x1 - splineBounds.minX) / size,
                    (y1 - splineBounds.minY) / size,
                    (x2 - splineBounds.minX) / size,
                    (y2 - splineBounds.minY) / size,
                    (x3 - splineBounds.minX) / size,
                    (y3 - splineBounds.minY) / size
            );

            int stepSize = getStepCount(getSplineSegmentLength(newSplineSegment));
            for (int i = 0; i < stepSize; i++) {
                double s = (double) i / stepSize;
                separated.add(new Circle(getPointOnSplineSegment(newSplineSegment, s).getX(), getPointOnSplineSegment(newSplineSegment, s).getY(), 0.001));
            }

        }
         */

        addGeometryAsEditorObjects(separated, splineBounds);

    }


    private static void fillSplineSegmentWithGeometry(QuadCurve2D splineSegment, ArrayList<Geometry> allGeometryList) {

        int numSteps = getStepCount(getSplineSegmentLength(splineSegment));

        Circle[] circles = getCircles(splineSegment);

        Rectangle[] rectangles = getRectangles(splineSegment);

        double currentSatisfaction = 0;

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

        addCompletion();

    }


    private static Circle[] getCircles(QuadCurve2D splineSegment) {

        int numSteps = getStepCount(getSplineSegmentLength(splineSegment));

        Circle[] circles = new Circle[numSteps];

        for (int stepIndex = 0; stepIndex < numSteps; stepIndex++) {

            double bestSatisfaction = 0;
            Circle bestCircle = null;

            double s = (double) stepIndex / numSteps;
            Point2D stepPoint = getPointOnSplineSegment(splineSegment, s);

            double theta = getInwardAngleOnSplineSegment(splineSegment, s);

            for (double radius = 0.01; radius < 1; radius += 0.01) {

                double circleX = stepPoint.getX() + (radius + 0.0001) * Math.cos(theta);
                double circleY = stepPoint.getY() + (radius + 0.0001) * Math.sin(theta);


                if (circleX < 0 || circleY < 0 || circleX >= 1 || circleY >= 1) continue;

                if (!isCircleInsideSpline(circleX, circleY, radius)) break;

                Circle circle = new Circle(circleX, circleY, radius);
                double satisfaction = getSplineSegmentSatisfaction(splineSegment, new ArrayList<>(List.of(circle)));

                if (satisfaction > bestSatisfaction) {
                    bestSatisfaction = satisfaction;
                    bestCircle = circle;
                }

            }

            circles[stepIndex] = bestCircle;

        }

        return circles;

    }


    private static Rectangle[] getRectangles(QuadCurve2D splineSegment) {

        int numSteps = getStepCount(getSplineSegmentLength(splineSegment));

        Rectangle[] rectangles = new Rectangle[numSteps];

        for (int stepIndex = 0; stepIndex < numSteps; stepIndex++) {

            double bestSatisfaction = 0;
            Rectangle bestRectangle = null;

            double s = (double) stepIndex / numSteps;
            Point2D stepPoint = getPointOnSplineSegment(splineSegment, s);

            double forwardAngle = getForwardAngleOnSplineSegment(splineSegment, s);
            double theta = getInwardAngleOnSplineSegment(splineSegment, s);

            for (double h = 0.01; h < 0.333; h += 0.01) {

                double w = h * 3;

                double rectangleX = stepPoint.getX() + (h/2.0 + 0.000000001) * Math.cos(theta);
                double rectangleY = stepPoint.getY() + (h/2.0 + 0.000000001) * Math.sin(theta);

                if (!isRectangleInsideSpline(rectangleX, rectangleY, w, h, forwardAngle)) {
                    break;
                }

                Rectangle rectangle = new Rectangle(rectangleX, rectangleY, w, h, forwardAngle);
                double satisfaction = getSplineSegmentSatisfaction(splineSegment, new ArrayList<>(List.of(rectangle)));

                if (satisfaction > bestSatisfaction) {
                    bestSatisfaction = satisfaction;
                    bestRectangle = rectangle;
                }

            }

            rectangles[stepIndex] = bestRectangle;

        }

        return rectangles;

    }


    private static void addGeometryAsEditorObjects(ArrayList<Geometry> geometryList, Bounds splineBounds) {

        double width = splineBounds.maxX - splineBounds.minX;
        double height = splineBounds.maxY - splineBounds.minY;

        double size = Math.max(width, height);

        ArrayList<UserAction> undoBuilder = new ArrayList<>();

        for (int i = SplineManager.getPointCount() - 4; i >= 0; i -= 3) {
            Point2D p0 = i == 0 ? SplineManager.getSplinePoint(i) : SplineManager.getSplinePoint(i - 2);
            Point2D p1 =  i == 0 ? SplineManager.getSplinePoint(i) : SplineManager.getSplinePoint(i - 1);
            Point2D p2 = SplineManager.getSplinePoint(i);
            undoBuilder.add(new DeleteSplinePointAction(p0.getX(), p0.getY(), p1.getX(), p1.getY(), p2.getX(), p2.getY(), i));
        }

        EditorObject compositegeom = ObjectCreator.create("compositegeom", LevelManager.getLevel().getSceneObject(), LevelManager.getLevel().getVersion());
        compositegeom.setAttribute("x", splineBounds.minX + width / 2);
        compositegeom.setAttribute("y", -splineBounds.minY - height / 2);
        LevelManager.getLevel().getScene().add(compositegeom);

        undoBuilder.add(new ObjectCreationAction(compositegeom, LevelManager.getLevel().getSceneObject().getChildren().indexOf(compositegeom)));

        for (Geometry geometry : geometryList) {

            EditorObject geometryObject;

            if (geometry instanceof Circle circle) {

                geometryObject = ObjectCreator.create("circle", compositegeom, LevelManager.getLevel().getVersion());
                geometryObject.setAttribute("x", circle.x * size + splineBounds.minX - compositegeom.getAttribute("x").doubleValue());
                geometryObject.setAttribute("y", -circle.y * size - splineBounds.minY - compositegeom.getAttribute("y").doubleValue());
                geometryObject.setAttribute("radius", circle.radius * size);

            } else if (geometry instanceof Rectangle rectangle) {

                geometryObject = ObjectCreator.create("rectangle", compositegeom, LevelManager.getLevel().getVersion());
                geometryObject.setAttribute("x", rectangle.x * size + splineBounds.minX - compositegeom.getAttribute("x").doubleValue());
                geometryObject.setAttribute("y", -rectangle.y * size - splineBounds.minY - compositegeom.getAttribute("y").doubleValue());
                geometryObject.setAttribute("width", rectangle.width * size);
                geometryObject.setAttribute("height", rectangle.height * size);
                geometryObject.setAttribute("rotation", -rectangle.rotation);

            } else continue;

            LevelManager.getLevel().getScene().add(geometryObject);

            undoBuilder.add(new ObjectCreationAction(geometryObject, compositegeom.getChildren().indexOf(geometryObject)));

        }

        UndoManager.registerChange(undoBuilder.toArray(new UserAction[0]));

        SplineManager.clear();

    }


}
