package com.WooGLEFX.EditorObjects.ObjectCollision;

import com.WooGLEFX.EditorObjects.ObjectPosition;
import com.WooGLEFX.EditorObjects.ObjectUtil;
import com.WooGLEFX.Structures.SimpleStructures.DragSettings;
import javafx.geometry.Point2D;
import javafx.scene.image.Image;

public class ImageCollider {

    public static DragSettings mouseIntersection(ObjectPosition objectPosition, double mouseX, double mouseY) {

        Image image = objectPosition.getImage();
        if (image == null) return DragSettings.NULL;

        double x = objectPosition.getX();
        double y = objectPosition.getY();
        double rotation = objectPosition.getRotation();
        double width = objectPosition.getWidth();
        double height = objectPosition.getHeight();

        Point2D rotated = ObjectUtil.rotate(new Point2D(mouseX, mouseY), -rotation, new Point2D(x, y));

        double mX = rotated.getX();
        double mY = rotated.getY();

        if (mX > x - width / 2 && mX < x + width / 2 && mY > y - height / 2 && mY < y + height / 2) {
            double goodX = (mX - (x - width / 2)) / (width / image.getWidth());
            double goodY = (mY - (y - height / 2)) / (height / image.getHeight());
            int pixel = image.getPixelReader().getArgb((int) goodX, (int) goodY);
            if (pixel >> 24 != 0) {
                DragSettings dragSettings = new DragSettings(objectPosition.isDraggable() ? DragSettings.MOVE : DragSettings.NONE);
                dragSettings.setInitialSourceX(mouseX - x);
                dragSettings.setInitialSourceY(mouseY - y);
                dragSettings.setObjectPosition(objectPosition);
                return dragSettings;
            }
        }

        return DragSettings.NULL;

    }


    public static DragSettings mouseIntersectingCorners(ObjectPosition objectPosition, double mouseX, double mouseY) {

        if (objectPosition.getImage() == null) return DragSettings.NULL;

        DragSettings dragSettings = RectangleCollider.mouseIntersectingCorners(objectPosition, mouseX, mouseY);
        dragSettings.setObjectPosition(objectPosition);
        return dragSettings;

    }

}
