package com.woogleFX.editorObjects;

import com.woogleFX.structures.simpleStructures.Color;
import javafx.scene.image.Image;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;

public class ImageUtility {

    public static Image colorize(Image image, Color colorize) {

        double rScale = colorize.getR() / 255.0;
        double gScale = colorize.getG() / 255.0;
        double bScale = colorize.getB() / 255.0;

        WritableImage writableImage = new WritableImage((int)image.getWidth(), (int)image.getHeight());
        PixelWriter pixelWriter = writableImage.getPixelWriter();

        int[] pixelBuffer = new int[(int)image.getWidth() * (int)image.getHeight()];

        for (int x = 0; x < image.getWidth() - 1; x++) {
            for (int y = 0; y < image.getHeight() - 1; y++) {

                long pixel = (image.getPixelReader().getArgb(x, y));

                if (pixel < 0) {
                    pixel += (1L << 32);
                }

                int pixelA = (int)(pixel / (2 << 23));
                int pixelR = (int)((pixel % (2 << 23)) / (2 << 15));
                int pixelG = (int)((pixel % (2 << 15)) / (2 << 7));
                int pixelB = (int)(pixel % (2 << 7));

                int scaledR = (int)(pixelR * rScale);
                int scaledG = (int)(pixelG * gScale);
                int scaledB = (int)(pixelB * bScale);

                pixelBuffer[y * (int)image.getWidth() + x] = (pixelA * (2 << 23)) + (scaledR * (2 << 15)) + (scaledG * (2 << 7)) + scaledB;

            }
        }

        pixelWriter.setPixels(0, 0, (int)image.getWidth(), (int)image.getHeight(), PixelFormat.getIntArgbInstance(), pixelBuffer, 0, (int)image.getWidth());

        return writableImage;

    }

}
