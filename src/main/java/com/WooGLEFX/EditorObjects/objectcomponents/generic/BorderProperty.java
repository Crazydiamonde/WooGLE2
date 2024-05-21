package com.WooGLEFX.EditorObjects.objectcomponents.generic;

import javafx.scene.paint.Paint;

/** Represents any object component with a border (ex. Circle, Rectangle). */
public interface BorderProperty {

    /** Returns the width of the border, in pixels. */
    double getEdgeSize();


    /** Returns the color of the border. */
    Paint getBorderColor();


    /** Returns if only the border should be used for collision. */
    boolean isEdgeOnly();

}
