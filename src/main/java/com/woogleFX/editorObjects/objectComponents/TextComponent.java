package com.woogleFX.editorObjects.objectComponents;

import com.woogleFX.editorObjects.EditorObject;
import com.woogleFX.editorObjects.ObjectUtil;
import com.woogleFX.editorObjects.objectComponents.generic.ColoredProperty;
import com.woogleFX.engine.renderer.Renderer;
import com.woogleFX.gameData.font.Layer;
import com.woogleFX.gameData.font._Font;
import com.woogleFX.editorObjects.objectComponents.generic.RotatableProperty;
import com.woogleFX.engine.AssetManager;
import com.woogleFX.editorObjects.DragSettings;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.transform.Affine;

import java.util.ArrayList;

public abstract class TextComponent extends ObjectComponent implements RotatableProperty, ColoredProperty {

    public TextComponent(EditorObject editorObject) {
        super(editorObject);
    }


    public abstract _Font getFont();
    public void setFont(_Font font) {

    }

    public Paint getBorderColor() {
        return Color.BLACK;
    }

    public Font getOtherFont() {
        return null;
    }


    public abstract String getText();
    public void setText(String text) {

    }


    public double getScale() {
        return 1.0;
    }
    public void setScale(double scale) {

    }


    public double getAlpha() {
        return 1.0;
    }


    public boolean isCentered() {
        return false;
    }

    private record DrawableChar(Image image, double x, double y, double w, double h) {

    }

    private ArrayList<DrawableChar> getDrawable() {

        ArrayList<DrawableChar> chars = new ArrayList<>();

        _Font font = getFont();

        String text = getText();
        if (text == null) return chars;

        double x = getX();
        double y = getY();
        double labelScale = getScale();

        for (Layer layer : font.getLayers()) {

            String id = layer.getId();
            double ascent = layer.getAscent();
            double ascentPadding = layer.getAscentPadding();
            double spacing = layer.getSpacing();
            double pointSize = layer.getPointSize();
            double layerScale = layer.getScale();
            double lineSpacingOffset = layer.getLineSpacingOffset();

            // ascent = 34
            // ascentPadding = 3
            // spacing = 1.2
            // pointSize = 26
            // layerScale = 1
            // lineSpacingOffset = 0

            int accumulatedSpacing = 0;

            int spaces = 1;
            for (char c : text.toCharArray()) if (c == '|') spaces++;

            int i = 0;
            ArrayList<Double> centeredOffsets = new ArrayList<>();
            for (char c : text.toCharArray()) {

                if (c == '|') {

                    if (isCentered()) {
                        centeredOffsets.add(-accumulatedSpacing / 2.0);
                    } else {
                        centeredOffsets.add(0.0);
                    }

                    accumulatedSpacing = 0;

                    continue;
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

                accumulatedSpacing += (int) (width + spacing + kerning);
            }
            if (isCentered()) {
                centeredOffsets.add(-accumulatedSpacing / 2.0);
            } else {
                centeredOffsets.add(0.0);
            }

            accumulatedSpacing = 0;

            double spacingY = 0;

            double maxHeight = 0;

            i = 0;
            int j = 0;
            for (char c : text.toCharArray()) {

                if (c == '|') {

                    accumulatedSpacing = 0;

                    spacingY += (ascent + ascentPadding + lineSpacingOffset) * layerScale * labelScale;

                    j++;

                    continue;
                }

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

                if (image != null) {
                    chars.add(new DrawableChar(image, x + (centeredOffsets.get(j) + charOffsetX + accumulatedSpacing) * scale, y + spacingY + (charOffsetY - (ascent + ascentPadding) * spaces / 2) * scale, image.getWidth() * scale, image.getHeight() * scale));
                    if (image.getHeight() > maxHeight) maxHeight = image.getHeight();
                }

                accumulatedSpacing += (int)(width + spacing + kerning);

                i++;

            }

        }

        return chars;

    }


    @Override
    public void draw(GraphicsContext graphicsContext) {

        double x = getX();
        double y = getY();
        double rotation = getRotation();
        double labelScale = getScale();

        _Font font = getFont();
        Font otherFont = getOtherFont();

        double offsetX = AssetManager.getAsset().getOffsetX();
        double offsetY = AssetManager.getAsset().getOffsetY();
        double zoom = AssetManager.getAsset().getZoom();

        graphicsContext.save();

        Affine t = graphicsContext.getTransform();
        t.appendTranslation(offsetX, offsetY);
        t.appendScale(zoom, zoom);
        t.appendRotation(Math.toDegrees(rotation), x, y);
        graphicsContext.setTransform(t);

        graphicsContext.setGlobalAlpha(getAlpha());

        if (font == null) {

            if (otherFont != null) {

                graphicsContext.setFont(getOtherFont());
                graphicsContext.setFill(getColor());
                graphicsContext.setStroke(getBorderColor());
                graphicsContext.setLineWidth(0.06 * getOtherFont().getSize());
                graphicsContext.strokeText(getText(), x, y);
                graphicsContext.fillText(getText(), x, y);

            }

        } else {

            ArrayList<DrawableChar> chars = getDrawable();

            for (DrawableChar drawableChar : chars) {

                graphicsContext.drawImage(drawableChar.image, drawableChar.x, drawableChar.y, drawableChar.w, drawableChar.h);

            }

        }

        graphicsContext.restore();

    }

    @Override
    public void drawSelectionOutline(GraphicsContext graphicsContext) {

        double _x = getX();
        double _y = getY();
        double rotation = getRotation();
        double labelScale = getScale();

        _Font font = getFont();
        Font otherFont = getOtherFont();

        double offsetX = AssetManager.getAsset().getOffsetX();
        double offsetY = AssetManager.getAsset().getOffsetY();
        double zoom = AssetManager.getAsset().getZoom();

        graphicsContext.save();

        Affine t = graphicsContext.getTransform();
        t.appendTranslation(offsetX, offsetY);
        t.appendScale(zoom, zoom);
        t.appendRotation(Math.toDegrees(rotation), _x, _y);
        graphicsContext.setTransform(t);

        graphicsContext.setGlobalAlpha(getAlpha());

        if (font == null) {

            if (otherFont != null) {

                Text text = new Text(getText());
                text.setFont(getOtherFont());

                double minX = getX() + text.getLayoutBounds().getMinX();
                double minY = getY() + text.getLayoutBounds().getMinY();
                double maxX = getX() + text.getLayoutBounds().getMaxX();
                double maxY = getY() + text.getLayoutBounds().getMaxY();

                graphicsContext.setStroke(Renderer.selectionOutline2);
                graphicsContext.setLineWidth(1 / zoom);
                graphicsContext.setLineDashes(3 / zoom);
                graphicsContext.setLineDashOffset(0);
                graphicsContext.strokeRect(minX, minY, maxX - minX, maxY - minY);

                graphicsContext.setStroke(Renderer.selectionOutline);
                graphicsContext.setLineDashOffset(3 / zoom);
                graphicsContext.strokeRect(minX, minY, maxX - minX, maxY - minY);

            }

        } else {

            double minX = Double.POSITIVE_INFINITY;
            double maxX = Double.NEGATIVE_INFINITY;
            double minY = Double.POSITIVE_INFINITY;
            double maxY = Double.NEGATIVE_INFINITY;

            ArrayList<DrawableChar> chars = getDrawable();

            for (DrawableChar drawableChar : chars) {

                Image image = drawableChar.image;
                if (image == null) return;

                if (drawableChar.x < minX) minX = drawableChar.x;
                if (drawableChar.x + drawableChar.w > maxX) maxX = drawableChar.x + drawableChar.w;
                if (drawableChar.y < minY) minY = drawableChar.y;
                if (drawableChar.y + drawableChar.h > maxY) maxY = drawableChar.y + drawableChar.h;

            }

            graphicsContext.setStroke(Renderer.selectionOutline2);
            graphicsContext.setLineWidth(1 / zoom);
            graphicsContext.setLineDashes(3 / zoom);
            graphicsContext.setLineDashOffset(0);
            graphicsContext.strokeRect(minX, minY, maxX - minX, maxY - minY);

            graphicsContext.setStroke(Renderer.selectionOutline);
            graphicsContext.setLineDashOffset(3 / zoom);
            graphicsContext.strokeRect(minX, minY, maxX - minX, maxY - minY);

        }

        graphicsContext.restore();

    }

    @Override
    public DragSettings mouseIntersection(double mouseX, double mouseY) {

        double rotation = getRotation();
        double labelScale = getScale();

        _Font font = getFont();
        Font otherFont = getOtherFont();

        if (font == null) {

            if (otherFont != null) {

                // TODO: this causes huge performance issues

                /*
                Text text = new Text(getText());
                text.setFont(getOtherFont());
                WritableImage image = new WritableImage((int)text.getLayoutBounds().getWidth(), (int)text.getLayoutBounds().getHeight());
                Canvas canvas = new Canvas(image.getWidth(), image.getHeight());
                GraphicsContext graphicsContext1 = canvas.getGraphicsContext2D();
                graphicsContext1.setFont(getOtherFont());
                graphicsContext1.setLineWidth(0.06 * getOtherFont().getSize());
                graphicsContext1.strokeText(getText(), 0, canvas.getHeight() * 0.825);
                graphicsContext1.fillText(getText(), 0, canvas.getHeight() * 0.825);
                canvas.snapshot(null, image);

                double width = text.getLayoutBounds().getWidth();
                double height = text.getLayoutBounds().getHeight();
                double x = getX() + width / 2;
                double y = getY() - height * 0.325;

                Point2D rotated = ObjectUtil.rotate(new Point2D(mouseX, mouseY), -rotation, new Point2D(getX(), getY()));

                double mX = rotated.getX();
                double mY = rotated.getY();

                if (mX > x - width / 2 && mX < x + width / 2 && mY > y - height / 2 && mY < y + height / 2) {
                    double goodX = (mX - (x - width / 2)) / (width / image.getWidth());
                    double goodY = (mY - (y - height / 2)) / (height / image.getHeight());
                    long pixel = image.getPixelReader().getArgb((int)goodX, (int)goodY);
                    if ((pixel & 0x00FF0000L) < 0x00FF0000L) {
                        DragSettings dragSettings = new DragSettings(isDraggable() ? DragSettings.MOVE : DragSettings.NONE, this);
                        dragSettings.setInitialSource(new Point2D(mouseX - getX(), mouseY - getY()));
                        return dragSettings;
                    }
                }

                 */

            }

        } else {

            ArrayList<DrawableChar> chars = getDrawable();

            for (DrawableChar drawableChar : chars) {

                Image image = drawableChar.image;
                if (image == null) continue;


                double width = drawableChar.w;
                double height = drawableChar.h;

                Point2D rotated = ObjectUtil.rotate(new Point2D(mouseX, mouseY), -rotation, new Point2D(drawableChar.x, drawableChar.y));

                double mX = rotated.getX();
                double mY = rotated.getY();

                if (mX > drawableChar.x && mX < drawableChar.x + width && mY > drawableChar.y && mY < drawableChar.y + height) {
                    double goodX = (mX - drawableChar.x) / (width / image.getWidth());
                    double goodY = (mY - drawableChar.y) / (height / image.getHeight());
                    long pixel = image.getPixelReader().getArgb((int) goodX, (int) goodY);
                    if ((pixel & 0xFF000000L) * getAlpha() > 0x20000000L) {
                        DragSettings dragSettings = new DragSettings(isDraggable() ? DragSettings.MOVE : DragSettings.NONE, this);
                        dragSettings.setInitialSource(new Point2D(mouseX - getX(), mouseY - getY()));
                        dragSettings.setOpacity(((pixel & 0xFF000000L) >> 24) / 255.0 * getAlpha());
                        return dragSettings;
                    }
                }

            }

        }

        return DragSettings.NULL;

    }

    @Override
    public DragSettings mouseIntersectingCorners(double mouseX, double mouseY) {
        return DragSettings.NULL;
    }

}
