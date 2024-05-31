package com.woogleFX.gameData.font;

import javafx.scene.image.Image;

import java.util.HashMap;
import java.util.Map;

public class Layer {

    private final String id;
    public String getId() {
        return id;
    }
    public Layer(String id) {
        this.id = id;
    }


    private Image image;
    public Image getImage() {
        return image;
    }
    public void setImage(Image image) {
        this.image = image;
    }


    private double ascent;
    public double getAscent() {
        return ascent;
    }
    public void setAscent(double ascent) {
        this.ascent = ascent;
    }


    private double ascentPadding;
    public double getAscentPadding() {
        return ascentPadding;
    }
    public void setAscentPadding(double ascentPadding) {
        this.ascentPadding = ascentPadding;
    }


    private double lineSpacingOffset;
    public double getLineSpacingOffset() {
        return lineSpacingOffset;
    }
    public void setLineSpacingOffset(double lineSpacingOffset) {
        this.lineSpacingOffset = lineSpacingOffset;
    }


    private double pointSize;
    public double getPointSize() {
        return pointSize;
    }
    public void setPointSize(double pointSize) {
        this.pointSize = pointSize;
    }


    private double spacing;
    public double getSpacing() {
        return spacing;
    }
    public void setSpacing(double spacing) {
        this.spacing = spacing;
    }


    private double scale = 1;
    public double getScale() {
        return scale;
    }
    public void setScale(double scale) {
        this.scale = scale;
    }


    private final Map<Character, Image> imageMap = new HashMap<>();
    public Image getImage(char c) {
        return imageMap.get(c);
    }
    public void mapImage(char c, Image image) {
        imageMap.put(c, image);
    }


    private final Map<Character, int[]> offsetMap = new HashMap<>();
    public int[] getOffset(char c) {
        return offsetMap.get(c);
    }
    public boolean hasOffset(char c) {
        return offsetMap.containsKey(c);
    }
    public void mapOffset(char c, int[] offset) {
        offsetMap.put(c, offset);
    }


    private final Map<Character, Double> widthMap = new HashMap<>();
    public double getWidth(char c) {
        return widthMap.get(c);
    }
    public void mapWidth(char c, double width) {
        widthMap.put(c, width);
    }


    private final Map<String, Integer> kerningMap = new HashMap<>();
    public double getKerning(String s) {
        return kerningMap.get(s);
    }
    public boolean hasKerning(String s) {
        return kerningMap.containsKey(s);
    }
    public void mapKerning(String s, int kerning) {
        kerningMap.put(s, kerning);
    }

}
