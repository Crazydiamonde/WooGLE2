package com.WooGLEFX.EditorObjects.ObjectCollision;

import com.WooGLEFX.EditorObjects.Components.ObjectPosition;
import com.WooGLEFX.EditorObjects.ObjectUtil;
import com.WooGLEFX.Functions.LevelManager;
import com.WooGLEFX.Structures.SimpleStructures.DragSettings;
import javafx.geometry.Point2D;

public class RectangleCollider {

    public static DragSettings mouseIntersection(ObjectPosition objectPosition, double mouseX, double mouseY, double edge) {

        double x = objectPosition.getX();
        double y = objectPosition.getY();
        double rotation = objectPosition.getRotation();
        double width = objectPosition.getWidth() - edge;
        double height = objectPosition.getHeight() - edge;

        Point2D rotated = ObjectUtil.rotate(new Point2D(mouseX, mouseY), -rotation, new Point2D(x, y));

        double mX = rotated.getX();
        double mY = rotated.getY();

        if (mX < x - width / 2 ||
            mX > x + width / 2 ||
            mY < y - height / 2 ||
            mY > y + height / 2) return DragSettings.NULL;

        DragSettings dragSettings = new DragSettings(
                objectPosition.isDraggable() ? DragSettings.MOVE : DragSettings.NONE);
        dragSettings.setInitialSourceX(mouseX - x);
        dragSettings.setInitialSourceY(mouseY - y);
        dragSettings.setObjectPosition(objectPosition);
        return dragSettings;

    }


    public static DragSettings mouseIntersectingCorners(ObjectPosition objectPosition, double mouseX, double mouseY) {

        double x = objectPosition.getX();
        double y = objectPosition.getY();
        double rotation = objectPosition.getRotation();
        double width = objectPosition.getWidth();
        double height = objectPosition.getHeight();

        Point2D center = new Point2D(x, y);

        Point2D right = new Point2D(x + width / 2, y);
        right = ObjectUtil.rotate(right, rotation, center);
        right = new Point2D(right.getX(), right.getY());

        Point2D left = new Point2D(x - width / 2, y);
        left = ObjectUtil.rotate(left, rotation, center);
        left = new Point2D(left.getX(), left.getY());

        Point2D topRight = new Point2D(x + width / 2, y + height / 2);
        topRight = ObjectUtil.rotate(topRight, rotation, center);

        Point2D topLeft = new Point2D(x - width / 2, y + height / 2);
        topLeft = ObjectUtil.rotate(topLeft, rotation, center);

        Point2D bottomLeft = new Point2D(x - width / 2, y - height / 2);
        bottomLeft = ObjectUtil.rotate(bottomLeft, rotation, center);

        Point2D bottomRight = new Point2D(x + width / 2, y - height / 2);
        bottomRight = ObjectUtil.rotate(bottomRight, rotation, center);

        double distance = 4 / LevelManager.getLevel().getZoom();

        DragSettings resizeSettings = new DragSettings(DragSettings.RESIZE);
        resizeSettings.setObjectPosition(objectPosition);

        DragSettings rotateSettings = new DragSettings(DragSettings.ROTATE);
        rotateSettings.setObjectPosition(objectPosition);

        double dragSourceX = 0;
        double dragSourceY = 0;
        double dragAnchorX = 0;
        double dragAnchorY = 0;
        double rotateAngleOffset = 0;

        boolean resize = false;
        boolean rotate = false;

        if (objectPosition.isResizable() && mouseX > topLeft.getX() - distance && mouseX < topLeft.getX() + distance && mouseY > topLeft.getY() - distance && mouseY < topLeft.getY() + distance) {
            resize  = true;
            dragSourceX = x - width / 2;
            dragSourceY = y + height / 2;
            dragAnchorX = x + width / 2;
            dragAnchorY = y - height / 2;
        }
        if (objectPosition.isResizable() && mouseX > topRight.getX() - distance && mouseX < topRight.getX() + distance && mouseY > topRight.getY() - distance && mouseY < topRight.getY() + distance) {
            resize  = true;
            dragSourceX = x + width / 2;
            dragSourceY = y + height / 2;
            dragAnchorX = x - width / 2;
            dragAnchorY = y - height / 2;
        }
        if (objectPosition.isResizable() && mouseX > bottomLeft.getX() - distance && mouseX < bottomLeft.getX() + distance && mouseY > bottomLeft.getY() - distance && mouseY < bottomLeft.getY() + distance) {
            resize  = true;
            dragSourceX = x - width / 2;
            dragSourceY = y - height / 2;
            dragAnchorX = x + width / 2;
            dragAnchorY = y + height / 2;
        }
        if (objectPosition.isResizable() && mouseX > bottomRight.getX() - distance && mouseX < bottomRight.getX() + distance && mouseY > bottomRight.getY() - distance && mouseY < bottomRight.getY() + distance) {
            resize  = true;
            dragSourceX = x + width / 2;
            dragSourceY = y - height / 2;
            dragAnchorX = x - width / 2;
            dragAnchorY = y + height / 2;
        }

        if (objectPosition.isRotatable() && mouseX > left.getX() - distance && mouseX < left.getX() + distance && mouseY > left.getY() - distance && mouseY < left.getY() + distance) {
            rotate = true;
            dragSourceX = x - width / 2;
            dragSourceY = y;
            rotateAngleOffset = rotation;
        }

        if (objectPosition.isRotatable() && mouseX > right.getX() - distance && mouseX < right.getX() + distance && mouseY > right.getY() - distance && mouseY < right.getY() + distance) {
            rotate = true;
            dragSourceX = x + width / 2;
            dragSourceY = y;
            rotateAngleOffset = rotation;
        }

        if (resize) {
            Point2D dragSourceRotated = ObjectUtil.rotate(new Point2D(dragSourceX, dragSourceY), rotation, new Point2D(x, y));
            Point2D dragAnchorRotated = ObjectUtil.rotate(new Point2D(dragAnchorX, dragAnchorY), rotation, new Point2D(x, y));
            resizeSettings.setInitialSourceX(dragSourceRotated.getX());
            resizeSettings.setInitialSourceY(dragSourceRotated.getY());
            resizeSettings.setAnchorX(dragAnchorRotated.getX());
            resizeSettings.setAnchorY(dragAnchorRotated.getY());
            return resizeSettings;
        }

        if (rotate) {
            Point2D dragSourceRotated = ObjectUtil.rotate(new Point2D(dragSourceX, dragSourceY), rotation, new Point2D(x, y));
            rotateSettings.setInitialSourceX(dragSourceRotated.getX());
            rotateSettings.setInitialSourceY(dragSourceRotated.getY());
            rotateSettings.setRotateAngleOffset(rotateAngleOffset);
            return rotateSettings;
        }

        return DragSettings.NULL;

    }


    public static DragSettings mouseEdgeIntersection(ObjectPosition objectPosition, double mouseX, double mouseY) {

        DragSettings inside = mouseIntersection(objectPosition, mouseX, mouseY, objectPosition.getEdgeSize() * 4);
        if (inside != DragSettings.NULL) return DragSettings.NULL;

        return mouseIntersection(objectPosition, mouseX, mouseY, 0);

    }

}
