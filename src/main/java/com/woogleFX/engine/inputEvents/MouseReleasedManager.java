package com.woogleFX.engine.inputEvents;

import com.woogleFX.editorObjects.objectCreators.ObjectCreator;
import com.woogleFX.editorObjects.objectComponents.ObjectComponent;
import com.woogleFX.editorObjects.splineGeom.SplineGeometryPlacer;
import com.woogleFX.editorObjects.splineGeom.SplineManager;
import com.woogleFX.engine.fx.FXCanvas;
import com.woogleFX.engine.fx.FXContainers;
import com.woogleFX.engine.fx.FXScene;
import com.woogleFX.engine.SelectionManager;
import com.woogleFX.engine.fx.hierarchy.HierarchyManager;
import com.woogleFX.engine.LevelManager;
import com.woogleFX.editorObjects.objectCreators.ObjectAdder;
import com.woogleFX.engine.undoHandling.UndoManager;
import com.woogleFX.editorObjects.attributes.EditorAttribute;
import com.woogleFX.editorObjects.EditorObject;
import com.woogleFX.editorObjects.DragSettings;
import com.woogleFX.engine.undoHandling.userActions.AttributeChangeAction;
import com.woogleFX.engine.undoHandling.userActions.CreateSplinePointAction;
import com.woogleFX.engine.undoHandling.userActions.MoveSplinePointAction;
import com.woogleFX.engine.undoHandling.userActions.UserAction;
import com.woogleFX.gameData.level._Level;
import com.worldOfGoo.level.BallInstance;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.control.SplitPane;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

import java.util.ArrayList;

public class MouseReleasedManager {

    /** Called whenever the mouse is released. */
    public static void eventMouseReleased(MouseEvent event) {

        if (HierarchyManager.getDragSourceRow() != null) HierarchyManager.getDragSourceRow().setId("notDragTarget");

        // If the mouse was released inside the editor window:
        if (event.getButton() == MouseButton.PRIMARY) {
            primaryMouseButton(event);
        } else if (event.getButton() == MouseButton.SECONDARY) {
            FXScene.getScene().setCursor(Cursor.DEFAULT);
        }
    }


    private static void primaryMouseButton(MouseEvent event) {

        _Level level = LevelManager.getLevel();
        if (level == null) return;

        SplitPane splitPane = FXContainers.getSplitPane();
        double canvasWidth = splitPane.getDividerPositions()[0] * splitPane.getWidth();
        if (event.getX() > canvasWidth) return;

        // Record the changes made to the selected object.
        // Clear all possible redos if changes have been made.
        if (level.getSelected() == SelectionManager.getOldSelected() && SelectionManager.getOldAttributes() != null) {

            ArrayList<AttributeChangeAction> attributeChangeActions = new ArrayList<>();

            for (int i = 0; i < level.getSelected().length; i++) {

                EditorObject selected = level.getSelected()[i];
                EditorAttribute[] oldAttributes = SelectionManager.getOldAttributes()[i];

                for (EditorAttribute attribute : selected.getAttributes()) {
                    for (EditorAttribute oldAttribute : oldAttributes) {
                        if (attribute.getName().equals(oldAttribute.getName()) && !attribute.stringValue().equals(oldAttribute.stringValue())) {
                            attributeChangeActions.add(new AttributeChangeAction(attribute, oldAttribute.stringValue(), attribute.stringValue()));
                        }
                    }
                }

            }

            if (!attributeChangeActions.isEmpty()) UndoManager.registerChange(attributeChangeActions.toArray(new AttributeChangeAction[0]));

        }

        // Reset the cursor's appearance.
        //FXScene.getScene().setCursor(Cursor.DEFAULT);

        // Clear all drag settings now that the mouse has been released.
        SelectionManager.setDragSettings(DragSettings.NULL);
        // If we have started placing a strand, attempt to complete the strand.
        if (SelectionManager.getMode() == SelectionManager.STRAND && SelectionManager.getStrand1Gooball() != null) {
            for (EditorObject ball : level.getLevel().toArray(new EditorObject[0])) {
                if (ball instanceof BallInstance ballInstance) {
                    if (ball != SelectionManager.getStrand1Gooball()) {

                        double mouseX = (event.getX() - level.getOffsetX()) / level.getZoom();
                        double mouseY = (event.getY() - FXCanvas.getMouseYOffset() - level.getOffsetY()) / level.getZoom();

                        for (ObjectComponent objectComponent : ballInstance.getObjectComponents()) {
                            if (!objectComponent.isVisible()) continue;
                            if (objectComponent.mouseIntersection(mouseX, mouseY) != DragSettings.NULL) {

                                EditorObject strand = ObjectCreator.create("Strand", level.getLevelObject(), level.getVersion());
                                if (strand == null) continue;

                                strand.setAttribute("gb1", SelectionManager.getStrand1Gooball().getAttribute("id").stringValue());
                                strand.setAttribute("gb2", ball.getAttribute("id").stringValue());

                                level.getLevel().add(strand);
                                ObjectAdder.addAnything(strand);

                                SelectionManager.getStrand1Gooball().update();
                                ball.update();

                                break;

                            }
                        }
                    }
                }
            }
            SelectionManager.setStrand1Gooball(null);
        }

        if (SelectionManager.getMode() == SelectionManager.GEOMETRY) {

            if (event.getY() < FXCanvas.getMouseYOffset()) return;

            if (SplineManager.getSelected1() != -1 || SplineManager.getSelected2() != -1) {

                ArrayList<UserAction> userActions = new ArrayList<>();

                boolean moved = false;

                if (SplineManager.getSelected1() != -1) {

                    Point2D p1 = SplineManager.getSplinePoint(SplineManager.getSelected1());
                    if (p1.getX() != SplineManager.getSelected1OriginalX() || p1.getY() != SplineManager.getSelected1OriginalY()) {
                        moved = true;
                        userActions.add(new MoveSplinePointAction(
                                SplineManager.getSelected1OriginalX(), SplineManager.getSelected1OriginalY(),
                                p1.getX(), p1.getY(),
                                SplineManager.getSelected1()
                        ));
                        if (SplineManager.getSelected1() % 3 == 0) {
                            if (SplineManager.getSelected1() != 0) {
                                Point2D ctrl1 = SplineManager.getSplinePoint(SplineManager.getSelected1() - 1);
                                userActions.add(new MoveSplinePointAction(
                                        ctrl1.getX() - p1.getX() + SplineManager.getSelected1OriginalX(),
                                        ctrl1.getY() - p1.getY() + SplineManager.getSelected1OriginalY(),
                                        ctrl1.getX(), ctrl1.getY(),
                                        SplineManager.getSelected1() - 1
                                ));
                            }
                            if (SplineManager.getSelected1() != SplineManager.getPointCount() - 1) {
                                Point2D ctrl2 = SplineManager.getSplinePoint(SplineManager.getSelected1() + 1);
                                userActions.add(new MoveSplinePointAction(
                                        ctrl2.getX() - p1.getX() + SplineManager.getSelected1OriginalX(),
                                        ctrl2.getY() - p1.getY() + SplineManager.getSelected1OriginalY(),
                                        ctrl2.getX(), ctrl2.getY(),
                                        SplineManager.getSelected1() + 1
                                ));
                            }
                        } else {
                            if (SplineManager.getSelected1() % 3 == 1 && SplineManager.getSelected1() != 1) {
                                Point2D reverseCtrl1 = SplineManager.getSplinePoint(SplineManager.getSelected1() - 2);
                                userActions.add(new MoveSplinePointAction(
                                        SplineManager.getSelected1ReverseOriginalX(),
                                        SplineManager.getSelected1ReverseOriginalY(),
                                        reverseCtrl1.getX(), reverseCtrl1.getY(),
                                        SplineManager.getSelected1() - 2
                                ));
                            } else if (SplineManager.getSelected1() % 3 == 2 && SplineManager.getSelected1() != SplineManager.getPointCount() - 2) {
                                Point2D reverseCtrl1 = SplineManager.getSplinePoint(SplineManager.getSelected1() + 2);
                                userActions.add(new MoveSplinePointAction(
                                        SplineManager.getSelected1ReverseOriginalX(),
                                        SplineManager.getSelected1ReverseOriginalY(),
                                        reverseCtrl1.getX(), reverseCtrl1.getY(),
                                        SplineManager.getSelected1() + 2
                                ));
                            }
                        }
                    }

                }

                if (SplineManager.getSelected2() != -1) {

                    Point2D p2 = SplineManager.getSplinePoint(SplineManager.getSelected2());
                    if (p2.getX() != SplineManager.getSelected2OriginalX() || p2.getY() != SplineManager.getSelected2OriginalY()) {
                        moved = true;
                        userActions.add(new MoveSplinePointAction(
                                SplineManager.getSelected2OriginalX(), SplineManager.getSelected2OriginalY(),
                                p2.getX(), p2.getY(),
                                SplineManager.getSelected2()
                        ));
                        if (SplineManager.getSelected2() % 3 == 0) {
                            if (SplineManager.getSelected2() != 0) {
                                Point2D ctrl1 = SplineManager.getSplinePoint(SplineManager.getSelected2() - 1);
                                userActions.add(new MoveSplinePointAction(
                                        ctrl1.getX() - p2.getX() + SplineManager.getSelected2OriginalX(),
                                        ctrl1.getY() - p2.getY() + SplineManager.getSelected2OriginalY(),
                                        ctrl1.getX(), ctrl1.getY(),
                                        SplineManager.getSelected2() - 1
                                ));
                            }
                            if (SplineManager.getSelected2() != SplineManager.getPointCount() - 1) {
                                Point2D ctrl2 = SplineManager.getSplinePoint(SplineManager.getSelected2() + 1);
                                userActions.add(new MoveSplinePointAction(
                                        ctrl2.getX() - p2.getX() + SplineManager.getSelected2OriginalX(),
                                        ctrl2.getY() - p2.getY() + SplineManager.getSelected2OriginalY(),
                                        ctrl2.getX(), ctrl2.getY(),
                                        SplineManager.getSelected2() + 1
                                ));
                            }
                        } else {
                            if (SplineManager.getSelected2() % 3 == 1 && SplineManager.getSelected2() != 1) {
                                Point2D reverseCtrl1 = SplineManager.getSplinePoint(SplineManager.getSelected2() - 2);
                                userActions.add(new MoveSplinePointAction(
                                        SplineManager.getSelected2ReverseOriginalX(),
                                        SplineManager.getSelected2ReverseOriginalY(),
                                        reverseCtrl1.getX(), reverseCtrl1.getY(),
                                        SplineManager.getSelected2() - 2
                                ));
                            } else if (SplineManager.getSelected2() % 3 == 2 && SplineManager.getSelected2() != SplineManager.getPointCount() - 2) {
                                Point2D reverseCtrl1 = SplineManager.getSplinePoint(SplineManager.getSelected2() + 2);
                                userActions.add(new MoveSplinePointAction(
                                        SplineManager.getSelected2ReverseOriginalX(),
                                        SplineManager.getSelected2ReverseOriginalY(),
                                        reverseCtrl1.getX(), reverseCtrl1.getY(),
                                        SplineManager.getSelected2() + 2
                                ));
                            }
                        }
                    }

                }

                if (!moved) {

                    int selected1 = SplineManager.getSelected1() != -1 ? SplineManager.getSelected1() : SplineManager.getSelected2();
                    double amt = SplineManager.getSelectedDistance();
                    if (amt != 0) {
                        Point2D p2D = SplineGeometryPlacer.getPointOnSplineSegment(SplineManager.getQuadCurve(selected1 / 3 * 2 + (int)(amt * 2)), amt < 0.5 ? 2 * amt : 2 * amt - 1);
                        SplineManager.addPoint(p2D.getX(), p2D.getY(), p2D.getX(), p2D.getY(), p2D.getX(), p2D.getY(), selected1 + 2);

                        userActions.add(new CreateSplinePointAction(p2D.getX(), p2D.getY(), p2D.getX(), p2D.getY(), p2D.getX(), p2D.getY(), selected1 + 2));
                    }

                }

                if (!userActions.isEmpty()) UndoManager.registerChange(userActions.toArray(new UserAction[0]));

            }

        }

    }

}
