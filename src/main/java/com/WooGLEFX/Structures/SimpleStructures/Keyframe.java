package com.WooGLEFX.Structures.SimpleStructures;

import com.WooGLEFX.File.fileimport.AnimationReader;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class Keyframe {
    private final float x;
    private final float y;
    private final float angle;
    private final int alpha;
    private final int color;
    private final int nextFrameIndex;
    private final int soundStrIdx;
    private final int interpolation;

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getAngle() {
        return angle;
    }

    public int getAlpha() {
        return alpha;
    }

    public int getColor() {
        return color;
    }

    public int getNextFrameIndex() {
        return nextFrameIndex;
    }

    public int getSoundStrIdx() {
        return soundStrIdx;
    }

    public int getInterpolation() {
        return interpolation;
    }

    public Keyframe(byte[] data){
        this.x = ByteBuffer.wrap(AnimationReader.subsection(data, 0, 4)).order(ByteOrder.LITTLE_ENDIAN).getFloat();
        this.y = ByteBuffer.wrap(AnimationReader.subsection(data, 4, 8)).order(ByteOrder.LITTLE_ENDIAN).getFloat();
        this.angle = ByteBuffer.wrap(AnimationReader.subsection(data, 8, 12)).order(ByteOrder.LITTLE_ENDIAN).getFloat();
        this.alpha = ByteBuffer.wrap(AnimationReader.subsection(data, 12, 16)).order(ByteOrder.LITTLE_ENDIAN).getInt();
        this.color = ByteBuffer.wrap(AnimationReader.subsection(data, 16, 20)).order(ByteOrder.LITTLE_ENDIAN).getInt();
        this.nextFrameIndex = ByteBuffer.wrap(AnimationReader.subsection(data, 20, 24)).order(ByteOrder.LITTLE_ENDIAN).getInt();
        this.soundStrIdx = ByteBuffer.wrap(AnimationReader.subsection(data, 24, 28)).order(ByteOrder.LITTLE_ENDIAN).getInt();
        this.interpolation = ByteBuffer.wrap(AnimationReader.subsection(data, 28, 32)).order(ByteOrder.LITTLE_ENDIAN).getInt();
    }

    @Override
    public String toString(){
        String out = "Keyframe(";
        if (this.x != -1){
            out += "x=" + this.x + " ";
        }
        if (this.y != -1){
            out += "y=" + this.y + " ";
        }
        if (this.angle != -1){
            out += "angle=" + this.angle + " ";
        }
        if (this.alpha != -1){
            out += "alpha=" + this.alpha + " ";
        }
        if (this.color != -1){
            out += "color=" + this.color + " ";
        }
        if (this.nextFrameIndex != -1){
            out += "nextFrameIndex=" + this.nextFrameIndex + " ";
        }
        if (this.soundStrIdx != -1){
            out += "soundStrIdx=" + this.soundStrIdx + " ";
        }if (this.interpolation != -1){
            out += "interpolation=" + this.interpolation + ")";
        }
        return out;
    }

}