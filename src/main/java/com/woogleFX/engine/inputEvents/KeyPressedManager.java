package com.woogleFX.engine.inputEvents;

import com.woogleFX.editorObjects.ObjectManager;
import com.woogleFX.editorObjects.clipboardHandling.ClipboardManager;
import com.woogleFX.editorObjects.splineGeom.SplineGeometryPlacer;
import com.woogleFX.editorObjects.splineGeom.SplineManager;
import com.woogleFX.engine.LevelManager;
import com.woogleFX.engine.SelectionManager;
import com.woogleFX.engine.undoHandling.UndoManager;
import com.woogleFX.gameData.level.levelSaving.LevelUpdater;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import java.awt.geom.QuadCurve2D;

public class KeyPressedManager {

    public static void keyPressed(KeyEvent event) {
        event.consume();
        if (event.getCode() == KeyCode.DELETE) {
            ObjectManager.delete(LevelManager.getLevel());
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
                SplineManager.addPoint((first.getX1() + last.getX2()) / 2, (first.getY1() + last.getY2()) / 2);
                SplineManager.addPoint(first.getX1(), first.getY1());
                SplineGeometryPlacer.fillCurrentSplineWithGeometry();
                SplineManager.clear();
            }
        }
    }

}
