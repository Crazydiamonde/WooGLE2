package com.woogleFX.editorObjects.objectComponents;

import com.woogleFX.editorObjects.Layer;
import com.woogleFX.editorObjects._Font;
import com.woogleFX.editorObjects.objectComponents.generic.RotatableProperty;
import com.woogleFX.functions.LevelManager;
import com.woogleFX.structures.simpleStructures.DragSettings;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.transform.Affine;

public abstract class TextComponent extends ObjectComponent implements RotatableProperty {

    public abstract _Font getFont();
    public void setFont(_Font font) {

    }


    public abstract String getText();
    public void setText(String text) {

    }


    public double getScale() {
        return 1.0;
    }
    public void setScale(double scale) {

    }


    @Override
    public void draw(GraphicsContext graphicsContext, boolean selected) {

        _Font font = getFont();
        if (font == null) return;

        String text = getText();
        if (text == null) return;

        double x = getX();
        double y = getY();
        double rotation = getRotation();
        double labelScale = getScale();


        double offsetX = LevelManager.getLevel().getOffsetX();
        double offsetY = LevelManager.getLevel().getOffsetY();
        double zoom = LevelManager.getLevel().getZoom();

        graphicsContext.save();

        Affine t = graphicsContext.getTransform();
        t.appendTranslation(offsetX, offsetY);
        t.appendScale(zoom, zoom);
        t.appendRotation(Math.toDegrees(rotation), x, y);
        graphicsContext.setTransform(t);

        for (Layer layer : font.getLayers()) {

            String id = layer.getId();
            double ascent = layer.getAscent();
            double ascentPadding = layer.getAscentPadding();
            double spacing = layer.getSpacing();
            double pointSize = layer.getPointSize();
            double layerScale = layer.getScale();
            double lineSpacingOffset = layer.getLineSpacingOffset();

            int accumulatedSpacing = 0;

            int i = 0;
            for (char c : text.toCharArray()) {

                double charOffsetX;
                double charOffsetY;
                if (layer.hasOffset(c)) {
                    charOffsetX = layer.getOffset(c)[0];
                    charOffsetY = layer.getOffset(c)[1];
                } else {
                    charOffsetX = 0;
                    charOffsetY = 0;
                }
                double kerning;
                if (i != text.length() - 1) {

                    String kerningPair = c + String.valueOf(text.charAt(i + 1));

                    // Check for kerning
                    if (layer.hasKerning(kerningPair)) {
                        kerning = layer.getKerning(kerningPair);
                    } else kerning = 0;

                } else kerning = 0;
                double width = layer.getWidth(c);
                Image image = layer.getImage(c);

                double scale = layerScale * labelScale;

                if (image != null) graphicsContext.drawImage(image, x + (charOffsetX + accumulatedSpacing) * scale, y + (charOffsetY + ascent + ascentPadding) * scale, image.getWidth() * scale, image.getHeight() * scale);

                accumulatedSpacing += (int)(width + spacing + kerning);


                i++;

            }


        }

        graphicsContext.restore();

    }

    @Override
    public DragSettings mouseIntersection(double mouseX, double mouseY) {
        return DragSettings.NULL;
    }

    @Override
    public DragSettings mouseIntersectingCorners(double mouseX, double mouseY) {
        return DragSettings.NULL;
    }

}
