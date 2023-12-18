package com.WooGLEFX.Engine.Collision;

public class RectangleCollision {

    public static boolean solidRectangleCollision(double pointX, double pointY, double rectCenterX, double rectCenterY, double rectWidth, double rectHeight) {
        return pointX > rectCenterX - rectWidth / 2 &&
                pointX < rectCenterX + rectWidth / 2 &&
                pointY > rectCenterY - rectHeight / 2 &&
                pointY < rectCenterY + rectHeight / 2;
    }

    public static boolean hollowRectangleCollision(double pointX, double pointY, double rectCenterX, double rectCenterY, double rectWidthOuter, double rectHeightOuter, double rectWidthInner, double rectHeightInner) {
        return solidRectangleCollision(pointX, pointY, rectCenterX, rectCenterY, rectWidthOuter, rectHeightOuter) &&
                !solidRectangleCollision(pointX, pointY, rectCenterX, rectCenterY, rectWidthInner, rectHeightInner);
    }

}
