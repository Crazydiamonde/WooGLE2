package com.worldOfGoo2.util;

import com.woogleFX.editorObjects.EditorObject;
import com.woogleFX.editorObjects.ImageUtility;
import com.woogleFX.assets.GameVersion;
import com.worldOfGoo2.environments.Environment;
import com.worldOfGoo2.environments.Layer;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.effect.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

import java.math.BigInteger;
import java.util.ArrayList;

public class EnvironmentHelper {

    public static Image singleFromEnvironment(Environment environment) {

        Canvas canvas = new Canvas();

        canvas.setWidth(256);
        canvas.setHeight(256);

        //canvas.setWidth(boundsTopRight.getAttribute("x").doubleValue() - boundsBottomLeft.getAttribute("x").doubleValue());
        //canvas.setHeight(boundsTopRight.getAttribute("y").doubleValue() - boundsBottomLeft.getAttribute("y").doubleValue());

        ArrayList<Layer> layers = new ArrayList<>();
        for (EditorObject part : environment.getChildren()) if (part instanceof Layer layer) {
            layers.add(layer);
        }
        layers.sort((o1, o2) -> {
            var d1 = o1.getAttribute("depth").doubleValue();
            var d2 = o2.getAttribute("depth").doubleValue();
            return Double.compare(d1, d2);
        });

        var gc = canvas.getGraphicsContext2D();

        var monochrome = new ColorAdjust();
        monochrome.setSaturation(-1.0);
        var params = new SnapshotParameters();
        params.setFill(javafx.scene.paint.Color.TRANSPARENT);

        for (Layer layer : layers) {
            /*
            if (layer.getAttribute("foreground").booleanValue()) {
                continue;
            }
            */

            var color = new BigInteger(layer.getAttribute("color").stringValue()).toString(16);
            Image image = layer.getAttribute("imageName").imageValue(null, GameVersion.VERSION_WOG2);

            var jfxColor = hex2ARGB(color);

            image = ImageUtility.colorize(image,
                    new com.woogleFX.editorObjects.attributes.dataTypes.Color(jfxColor.getOpacity(), jfxColor.getRed(), jfxColor.getGreen(), jfxColor.getBlue()));

            ImageView view = getEffectsView(image, monochrome, color);

            System.out.println(environment.getAttribute("id").stringValue());
            int blendMode = layer.getAttribute("blendingType").intValue();


            gc.save();

            gc.setGlobalAlpha(jfxColor.getOpacity());
            var blend = new Blend();

            switch(blendMode) {
                case 3: { // ADD
                    gc.setGlobalBlendMode(BlendMode.ADD);

                    blend.setMode(BlendMode.ADD);
                    gc.setEffect(blend);
                }
                default: {
                    gc.setGlobalBlendMode(BlendMode.SRC_OVER);
                }
            }

            WritableImage snapshot = new WritableImage((int)view.getFitWidth(), (int)view.getFitHeight());
            view.snapshot(params, snapshot);
            gc.drawImage(snapshot, 0, 0, canvas.getWidth(), canvas.getHeight());

            gc.setGlobalBlendMode(BlendMode.SRC_OVER);
            gc.setEffect(null);

            gc.restore();

        }

        WritableImage writableImage = new WritableImage((int)canvas.getWidth(), (int)canvas.getHeight());
        canvas.snapshot(null, writableImage);

        return writableImage;
    }

    private static ImageView getEffectsView(Image image, ColorAdjust monochrome, String color) {
        ImageView view = new ImageView();
        view.setFitWidth(image.getWidth());
        view.setFitHeight(image.getHeight());
        view.setImage(image);

        return view;
    }

    public static javafx.scene.paint.Color hex2ARGB(String h) {
        try {
            if (h.length() < 8) {
                var r = Integer.valueOf(h.substring(0, 2), 16);
                var g = Integer.valueOf(h.substring(2, 4), 16);
                var b = Integer.valueOf(h.substring(4, 6), 16);
                return new Color(r / 255d, g / 255d, b / 255d, 1);
            }
            var a = Integer.valueOf(h.substring(0, 2), 16);
            var r = Integer.valueOf(h.substring(2, 4), 16);
            var g = Integer.valueOf(h.substring(4, 6), 16);
            var b = Integer.valueOf(h.substring(6, 8), 16);
            return new Color(r / 255d, g / 255d, b / 255d, a / 255d);
        } catch (Exception e) { // If a color is invalid we'll just use white
            System.out.println("Error while loading color " + h);
            return new Color(1, 1, 1,1);
        }
    }

}
