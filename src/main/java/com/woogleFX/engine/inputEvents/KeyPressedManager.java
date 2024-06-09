package com.woogleFX.engine.inputEvents;

import com.woogleFX.editorObjects.ObjectManager;
import com.woogleFX.editorObjects.clipboardHandling.ClipboardManager;
import com.woogleFX.editorObjects.splineGeom.SplineGeometryPlacer;
import com.woogleFX.editorObjects.splineGeom.SplineManager;
import com.woogleFX.engine.LevelManager;
import com.woogleFX.engine.SelectionManager;
import com.woogleFX.engine.undoHandling.UndoManager;
import com.woogleFX.engine.undoHandling.userActions.DeleteSplinePointAction;
import com.woogleFX.engine.undoHandling.userActions.UserAction;
import com.woogleFX.gameData.level.levelSaving.LevelUpdater;
import javafx.geometry.Point2D;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import java.awt.geom.QuadCurve2D;
import java.util.ArrayList;

public class KeyPressedManager {

    public static void keyPressed(KeyEvent event) {
        if (event.getCode() == KeyCode.ESCAPE) {
            if (SelectionManager.getMode() == SelectionManager.GEOMETRY) {
                ArrayList<UserAction> undoBuilder = new ArrayList<>();

                for (int i = SplineManager.getPointCount() - 1; i >= 0; i -= 3) {
                    Point2D p0 = i == 0 ? SplineManager.getSplinePoint(i) : SplineManager.getSplinePoint(i - 2);
                    Point2D p1 =  i == 0 ? SplineManager.getSplinePoint(i) : SplineManager.getSplinePoint(i - 1);
                    Point2D p2 = SplineManager.getSplinePoint(i);
                    undoBuilder.add(new DeleteSplinePointAction(p0.getX(), p0.getY(), p1.getX(), p1.getY(), p2.getX(), p2.getY(), i));
                }

                UndoManager.registerChange(undoBuilder.toArray(new UserAction[0]));
                SplineManager.clear();
            }
        }
        if (event.getCode() == KeyCode.DELETE) {
            if (SelectionManager.getMode() == SelectionManager.GEOMETRY) {
                ArrayList<UserAction> undoBuilder = new ArrayList<>();
                if (SplineManager.getSelected1() != -1) {
                    if (SplineManager.getSelected1() == 0) {
                        Point2D p2 = SplineManager.getSplinePoint(0);
                        undoBuilder.add(new DeleteSplinePointAction(0, 0, 0, 0, p2.getX(), p2.getY(), 0));
                        SplineManager.removePoint(SplineManager.getSelected1());
                    } else if (SplineManager.getSelected1() == SplineManager.getPointCount() - 1) {
                        Point2D p0 = SplineManager.getSplinePoint(SplineManager.getSelected1() - 2);
                        Point2D p1 = SplineManager.getSplinePoint(SplineManager.getSelected1() - 1);
                        Point2D p2 = SplineManager.getSplinePoint(SplineManager.getSelected1());
                        undoBuilder.add(new DeleteSplinePointAction(p0.getX(), p0.getY(), p1.getX(), p1.getY(), p2.getX(), p2.getY(), SplineManager.getSelected1()));
                        SplineManager.removePoint(SplineManager.getPointCount() - 1);
                    } else {
                        Point2D p0 = SplineManager.getSplinePoint(SplineManager.getSelected1() - 1);
                        Point2D p1 = SplineManager.getSplinePoint(SplineManager.getSelected1());
                        Point2D p2 = SplineManager.getSplinePoint(SplineManager.getSelected1() + 1);
                        undoBuilder.add(new DeleteSplinePointAction(p0.getX(), p0.getY(), p1.getX(), p1.getY(), p2.getX(), p2.getY(), SplineManager.getSelected1()));
                        SplineManager.removePoint(SplineManager.getSelected1());
                    }
                }
                if (SplineManager.getSelected2() != -1) {
                    if (SplineManager.getSelected2() == 0) {
                        Point2D p2 = SplineManager.getSplinePoint(0);
                        undoBuilder.add(new DeleteSplinePointAction(0, 0, 0, 0, p2.getX(), p2.getY(), 0));
                        SplineManager.removePoint(SplineManager.getSelected2());
                    } else if (SplineManager.getSelected2() == SplineManager.getPointCount() - 1) {
                        Point2D p0 = SplineManager.getSplinePoint(SplineManager.getSelected2() - 2);
                        Point2D p1 = SplineManager.getSplinePoint(SplineManager.getSelected2() - 1);
                        Point2D p2 = SplineManager.getSplinePoint(SplineManager.getSelected2());
                        undoBuilder.add(new DeleteSplinePointAction(p0.getX(), p0.getY(), p1.getX(), p1.getY(), p2.getX(), p2.getY(), SplineManager.getSelected2()));
                        SplineManager.removePoint(SplineManager.getPointCount() - 1);
                    } else {
                        Point2D p0 = SplineManager.getSplinePoint(SplineManager.getSelected2() - 1);
                        Point2D p1 = SplineManager.getSplinePoint(SplineManager.getSelected2());
                        Point2D p2 = SplineManager.getSplinePoint(SplineManager.getSelected2() + 1);
                        undoBuilder.add(new DeleteSplinePointAction(p0.getX(), p0.getY(), p1.getX(), p1.getY(), p2.getX(), p2.getY(), SplineManager.getSelected2()));
                        SplineManager.removePoint(SplineManager.getSelected2());
                    }
                }
                UndoManager.registerChange(undoBuilder.toArray(new UserAction[0]));
            } else if (SelectionManager.getMode() == SelectionManager.SELECTION) {
                ObjectManager.delete(LevelManager.getLevel());
            }
        }
        if (event.isControlDown()) {
            if (event.getCode() == KeyCode.S) {
                LevelUpdater.saveLevel(LevelManager.getLevel());
            }
            if (event.getCode() == KeyCode.Z) {
                if (event.isShiftDown()) UndoManager.redo();
                else UndoManager.undo();
            }
            if (event.getCode() == KeyCode.X) {
                ClipboardManager.cut();
            }
            if (event.getCode() == KeyCode.C) {
                ClipboardManager.copy();
            }
            if (event.getCode() == KeyCode.V) {
                ClipboardManager.paste();
            }
        }
        if (event.getCode() == KeyCode.ENTER) {
            if (SelectionManager.getMode() == SelectionManager.GEOMETRY) {
                QuadCurve2D first = SplineManager.getQuadCurve(0);
                QuadCurve2D last = SplineManager.getQuadCurve(SplineManager.getQuadCurveCount() - 1);
                double xAvg = (first.getX1() + last.getX2()) / 2;
                double yAvg = (first.getY1() + last.getY2()) / 2;
                SplineManager.addPoint(last.getX2(), last.getY2(), xAvg, yAvg, first.getX1(), first.getY1(), SplineManager.getPointCount() + 2);
                SplineGeometryPlacer.fillCurrentSplineWithGeometry();
                SelectionManager.selectionMode();
                SplineManager.clear();
            }
        }
    }

}
