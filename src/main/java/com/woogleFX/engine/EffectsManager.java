package com.woogleFX.engine;

import com.woogleFX.editorObjects.EditorObject;
import com.woogleFX.editorObjects.objectComponents.ImageComponent;
import com.woogleFX.editorObjects.objectComponents.ObjectComponent;
import com.woogleFX.file.FileManager;
import com.woogleFX.file.resourceManagers.ResourceManager;
import com.woogleFX.structures.GameVersion;
import javafx.geometry.Point2D;
import javafx.scene.image.Image;

import java.io.FileNotFoundException;

public class EffectsManager {

    public static final String strandImageID = "IMAGE_BALL_GENERIC_ARM_INACTIVE";

    public static ObjectComponent getPlacingStrand(EditorObject goo1, double mouseX, double mouseY) {

        Image strandImage;

        try {
            if (!FileManager.getGameDir(GameVersion.OLD).isEmpty()) strandImage = ResourceManager.getImage(null, strandImageID, GameVersion.NEW);
            else strandImage = ResourceManager.getImage(null, strandImageID, GameVersion.OLD);
        } catch (FileNotFoundException e) {
            return null;
        }

        return new ImageComponent() {
            public double getX() {
                double x1 = goo1.getAttribute("x").doubleValue();
                return (x1 + mouseX) / 2;
            }
            public double getY() {
                double y1 = -goo1.getAttribute("y").doubleValue();
                return (y1 + mouseY) / 2;
            }
            public double getRotation() {

                double x1 = goo1.getAttribute("x").doubleValue();
                double y1 = -goo1.getAttribute("y").doubleValue();

                return Math.PI / 2 + Renderer.angleTo(new Point2D(x1, y1), new Point2D(mouseX, mouseY));

            }
            public double getScaleX() {
                return 0.15;
            }
            public double getScaleY() {

                double x1 = goo1.getAttribute("x").doubleValue();
                double y1 = -goo1.getAttribute("y").doubleValue();

                return Math.hypot(mouseX - x1, mouseY - y1) / strandImage.getHeight();

            }
            public Image getImage() {
                return strandImage;
            }
            public double getDepth() {
                return 0.00000001;
            }
            public boolean isDraggable() {
                return false;
            }
            public boolean isResizable() {
                return false;
            }
            public boolean isRotatable() {
                return false;
            }
        };

    }

}
