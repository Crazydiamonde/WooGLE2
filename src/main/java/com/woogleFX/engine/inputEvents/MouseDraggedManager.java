package com.woogleFX.engine.inputEvents;

import com.woogleFX.editorObjects.objectFunctions.ObjectDrag;
import com.woogleFX.editorObjects.objectFunctions.ObjectResize;
import com.woogleFX.editorObjects.objectFunctions.ObjectRotate;
import com.woogleFX.editorObjects.objectFunctions.ObjectSetAnchor;
import com.woogleFX.editorObjects.splineGeom.SplineManager;
import com.woogleFX.engine.fx.FXCanvas;
import com.woogleFX.engine.fx.FXPropertiesView;
import com.woogleFX.engine.renderer.Renderer;
import com.woogleFX.engine.SelectionManager;
import com.woogleFX.engine.LevelManager;
import com.woogleFX.editorObjects.DragSettings;
import com.woogleFX.gameData.level._Level;
import javafx.geometry.Point2D;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.transform.Affine;

public class MouseDraggedManager {

    /** Called whenever the mouse is dragged.*/
    public static void eventMouseDragged(MouseEvent event) {

        _Level level = LevelManager.getLevel();
        if (level == null) return;

        SelectionManager.setMouseX(event.getX());
        SelectionManager.setMouseY(event.getY() - FXCanvas.getMouseYOffset());

        // Calculate game-relative mouse coordinates.
        double gameRelativeMouseX = (SelectionManager.getMouseX() - level.getOffsetX()) / level.getZoom();
        double gameRelativeMouseY = (SelectionManager.getMouseY() - level.getOffsetY()) / level.getZoom();

        if (SelectionManager.getMode() == SelectionManager.GEOMETRY) {
            if (SplineManager.getSelected1() != -1 || SplineManager.getSelected2() != -1) {
                double dX = (gameRelativeMouseX - SplineManager.getOffsetX());
                double dY = (gameRelativeMouseY - SplineManager.getOffsetY());
                double s = SplineManager.getSelectedDistance();
                double weight = s == 0 ? 1 : 0.75 / (3*s - 9*s*s + 12*s*s*s - 6*s*s*s*s);
                double w1 = s == 0 ? 1 : weight * (1 - s);
                double w2 = s == 0 ? 1 : weight * s;
                if (SplineManager.getSelected1() != -1) {
                    double x1 = SplineManager.getSelected1OriginalX();
                    double y1 = SplineManager.getSelected1OriginalY();
                    double lastX = SplineManager.getSplinePoint(SplineManager.getSelected1()).getX();
                    double lastY = SplineManager.getSplinePoint(SplineManager.getSelected1()).getY();
                    SplineManager.setSplinePoint(SplineManager.getSelected1(), x1 + dX * w1, y1 + dY * w1);
                    if (SplineManager.getSelected1() % 3 == 0) {
                        if (SplineManager.getSelected1() != 0) {
                            Point2D ctrl1 = SplineManager.getSplinePoint(SplineManager.getSelected1() - 1);
                            SplineManager.setSplinePoint(SplineManager.getSelected1() - 1, ctrl1.getX() + x1 + dX * w1 - lastX, ctrl1.getY() + y1 + dY * w1 - lastY);
                        }
                        if (SplineManager.getSelected1() != SplineManager.getPointCount() + 1) {
                            Point2D ctrl2 = SplineManager.getSplinePoint(SplineManager.getSelected1() + 1);
                            SplineManager.setSplinePoint(SplineManager.getSelected1() + 1, ctrl2.getX() + x1 + dX * w1 - lastX, ctrl2.getY() + y1 + dY * w1 - lastY);
                        }
                    } else if (event.isShiftDown()) {
                        int i1 = SplineManager.getSelected1() + (SplineManager.getSelected1() % 3 == 1 ? -1 : 1);
                        int i2 = SplineManager.getSelected1() + (SplineManager.getSelected1() % 3 == 1 ? -2 : 2);
                        if (i1 != 0 && i1 != SplineManager.getPointCount() - 1) {
                            Point2D mainPoint = SplineManager.getSplinePoint(i1);
                            SplineManager.setSplinePoint(i2, mainPoint.getX() + (mainPoint.getX() - x1 - dX * w1), mainPoint.getY() + (mainPoint.getY() - y1 - dY * w1));
                        }
                    }
                }
                if (SplineManager.getSelected2() != -1) {
                    double x2 = SplineManager.getSelected2OriginalX();
                    double y2 = SplineManager.getSelected2OriginalY();
                    double lastX = SplineManager.getSplinePoint(SplineManager.getSelected2()).getX();
                    double lastY = SplineManager.getSplinePoint(SplineManager.getSelected2()).getY();
                    SplineManager.setSplinePoint(SplineManager.getSelected2(), x2 + dX * w2, y2 + dY * w2);
                    if (SplineManager.getSelected2() % 3 == 0) {
                        if (SplineManager.getSelected2() != 0) {
                            Point2D ctrl1 = SplineManager.getSplinePoint(SplineManager.getSelected2() - 1);
                            SplineManager.setSplinePoint(SplineManager.getSelected2() - 1, ctrl1.getX() + x2 + dX * w2 - lastX, ctrl1.getY() + y2 + dY * w2 - lastY);
                        }
                        if (SplineManager.getSelected2() != SplineManager.getPointCount() + 1) {
                            Point2D ctrl2 = SplineManager.getSplinePoint(SplineManager.getSelected2() + 1);
                            SplineManager.setSplinePoint(SplineManager.getSelected2() + 1, ctrl2.getX() + x2 + dX * w2 - lastX, ctrl2.getY() + y2 + dY * w2 - lastY);
                        }
                    } else if (event.isShiftDown()) {
                        int i1 = SplineManager.getSelected2() + (SplineManager.getSelected2() % 3 == 1 ? -1 : 1);
                        int i2 = SplineManager.getSelected2() + (SplineManager.getSelected2() % 3 == 1 ? -2 : 2);
                        if (i1 != 0 && i1 != SplineManager.getPointCount() - 1) {
                            Point2D mainPoint = SplineManager.getSplinePoint(i1);
                            SplineManager.setSplinePoint(i2, mainPoint.getX() + (mainPoint.getX() - x2 - dX * w2), mainPoint.getY() + (mainPoint.getY() - y2 - dY * w2));
                        }
                    }
                }
            }
        }

        if (level.getSelected().length != 0 && SelectionManager.getDragSettings() != null) {

            // Update the selected object according to what kind of operation is being performed.
            switch (SelectionManager.getDragSettings().getType()) {
                case DragSettings.MOVE -> ObjectDrag.dragFromMouse(gameRelativeMouseX, gameRelativeMouseY, SelectionManager.getDragSettings().getInitialSourceX(), SelectionManager.getDragSettings().getInitialSourceY());
                case DragSettings.RESIZE -> ObjectResize.resizeFromMouse(gameRelativeMouseX, gameRelativeMouseY, SelectionManager.getDragSettings().getInitialSourceX(), SelectionManager.getDragSettings().getInitialSourceY(), SelectionManager.getDragSettings().getAnchorX(), SelectionManager.getDragSettings().getAnchorY(), SelectionManager.getDragSettings().getInitialScaleX(), SelectionManager.getDragSettings().getInitialScaleY());
                case DragSettings.ROTATE -> ObjectRotate.rotateFromMouse(gameRelativeMouseX, gameRelativeMouseY, SelectionManager.getDragSettings().getInitialSourceX(), SelectionManager.getDragSettings().getInitialSourceY(), SelectionManager.getDragSettings().getRotateAngleOffset());
                case DragSettings.SETANCHOR -> ObjectSetAnchor.setAnchor(gameRelativeMouseX, gameRelativeMouseY, SelectionManager.getDragSettings().getInitialSourceX(), SelectionManager.getDragSettings().getInitialSourceY());
            }

            FXPropertiesView.getPropertiesView().refresh();
        }

        if (event.getButton() == MouseButton.SECONDARY) {
            // Pan the canvas according to the mouse's movement.
            level.setOffsetX(level.getOffsetX() + event.getScreenX() - SelectionManager.getMouseStartX());
            level.setOffsetY(level.getOffsetY() + event.getScreenY() - SelectionManager.getMouseStartY());
            SelectionManager.setMouseStartX(event.getScreenX());
            SelectionManager.setMouseStartY(event.getScreenY());

            // Apply the transformation to the canvas.
            Renderer.t = new Affine();
            Renderer.t.appendTranslation(level.getOffsetX(), level.getOffsetY());
            Renderer.t.appendScale(level.getZoom(), level.getZoom());

            // Redraw the canvas.
            Renderer.drawLevelToCanvas(level, FXCanvas.getCanvas());
        }

    }

}
