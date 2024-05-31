package com.woogleFX.editorObjects.objectComponents;

import com.woogleFX.editorObjects.DragSettings;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;

/** The base for any object component.
 * Has a position, depth, and visibility options. */
public abstract class ObjectComponent {

    public static boolean rectangleIntersection(double mouseX, double mouseY, Point2D point, double distance) {

        return mouseX > point.getX() - distance &&
                mouseX < point.getX() + distance &&
                mouseY > point.getY() - distance &&
                mouseY < point.getY() + distance;

    }


    /** Returns this component's X position. */
    public abstract double getX();


    /** Sets this component's X position.
     * @param x This component's X position. */
    public void setX(double x) {

    }


    /** Returns this component's Y position. */
    public abstract double getY();


    /** Sets this component's Y position.
     * @param y This component's Y position. */
    public void setY(double y) {

    }


    /** Returns this component's depth.
     * Components with higher depth are considered above those with lower depth. */
    public abstract double getDepth();


    /** Returns whether this component is visible. */
    public boolean isVisible() {
        return true;
    }


    /** Returns whether this component can be selected. */
    public boolean isSelectable() {
        return true;
    }


    /** Returns whether this component can be dragged. */
    public boolean isDraggable() {
        return true;
    }


    /** Returns whether this component can be resized. */
    public boolean isResizable() {
        return true;
    }


    /** Returns whether this component can be rotated. */
    public boolean isRotatable() {
        return true;
    }


    public abstract void draw(GraphicsContext graphicsContext, boolean selected);


    public abstract DragSettings mouseIntersection(double mouseX, double mouseY);


    public abstract DragSettings mouseIntersectingCorners(double mouseX, double mouseY);

}
