package com.WooGLEFX.Engine.Collision;

public class CircleCollision {

    public static boolean solidCircleCollision(double pointX, double pointY, double circleCenterX, double circleCenterY, double circleRadius) {
        return (pointX - circleCenterX) * (pointX - circleCenterX) + (pointY - circleCenterY) * (pointY - circleCenterY) < circleRadius * circleRadius;
    }

    public static boolean hollowCircleCollision(double pointX, double pointY, double circleCenterX, double circleCenterY, double circleOuterRadius, double circleInnerRadius) {
        return solidCircleCollision(pointX, pointY, circleCenterX, circleCenterY, circleOuterRadius) &&
                !solidCircleCollision(pointX, pointY, circleCenterX, circleCenterY, circleInnerRadius);
    }
}
