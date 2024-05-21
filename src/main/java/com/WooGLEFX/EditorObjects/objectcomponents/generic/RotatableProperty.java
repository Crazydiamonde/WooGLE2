package com.WooGLEFX.EditorObjects.objectcomponents.generic;

/** Represents any object component which can be rotated (ex. Rectangle, BallInstance). */
public interface RotatableProperty {

    /** Returns this component's rotation, in radians. */
    default double getRotation() {
        return 0;
    }


    /** Sets this component's rotation.
     * @param rotation The new rotation, in radians. */
    default void setRotation(double rotation) {

    }

}
