package com.woogleFX.assets.wog1.animation;

import com.woogleFX.editorObjects.objectCreators.ObjectCreator;
import com.woogleFX.engine.gui.alarms.ErrorAlarm;
import com.woogleFX.file.FileManager;
import com.woogleFX.file.fileImport.PositionableInputStream;
import com.woogleFX.assets.GameVersion;
import com.worldOfGoo.anim.Animation;
import com.worldOfGoo.anim.Keyframe;
import com.worldOfGoo.resrc.Resources;
import com.worldOfGoo.text.strings;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

/** Reads an entire animation from a World of Goo 1 .anim.binltl or .anim.binuni file. */
public class WOG1AnimationOpener {

    private static int readPointer(PositionableInputStream input, boolean isBinuni) {
        int ptr = input.readInt();
        if (isBinuni) input.readInt();
        return ptr;
    }

    /** Reads a keyframe from input.
     * @param input The input stream.
     * @param keyframe The keyframe to read data to.
     * @param type The type of the keyframe. */
    private static int readKeyframe(PositionableInputStream input, Keyframe keyframe, String type) {

        float x = input.readFloat();
        float y = input.readFloat();
        float angle = input.readFloat();
        int alpha = input.readInt();
        int color = input.readInt();
        int nextFrameIndex = input.readInt();
        int soundStringIndex = input.readInt();
        int interpolation = input.readInt();

        switch (type) {

            case "xForm0" -> {
                keyframe.setAttribute("scaleX", x);
                keyframe.setAttribute("scaleY", y);
            }

            case "xForm1" ->
                keyframe.setAttribute("angle", angle);

            case "xForm2" -> {
                keyframe.setAttribute("x", x);
                keyframe.setAttribute("y", y);
            }

            case "alpha" ->
                keyframe.setAttribute("alpha", alpha);

            case "color" ->
                keyframe.setAttribute("color", color);

            case "sound" ->
                keyframe.setAttribute("soundStringIndex", soundStringIndex);

        }

        keyframe.setAttribute("interpolation", interpolation);

        return nextFrameIndex;

    }


    public static Animation readAnimationFromBytes(PositionableInputStream input, int ptrOffset, GameVersion version) {

        boolean isBinuni = (version != GameVersion.VERSION_WOG1_OLD);

        if (isBinuni) input.setPtr(ptrOffset + 8);

        Animation animation = new Animation(null, version);

        boolean mHasColor = input.readInt() == 1;
        boolean mHasAlpha = input.readInt() == 1;
        boolean mHasSound = input.readInt() == 1;
        boolean mHasTransform = input.readInt() == 1;
        int mNumTransforms = input.readInt();
        int mNumFrames = input.readInt();

        int toTransformTypes = readPointer(input, isBinuni);
        int toFrameTimes = readPointer(input, isBinuni);
        int toXFormFrames = readPointer(input, isBinuni);
        int toAlphaFrames = readPointer(input, isBinuni);
        int toColorFrames = readPointer(input, isBinuni);
        int toSoundFrames = readPointer(input, isBinuni);
        int toStringTable = readPointer(input, isBinuni);

        input.setPtr(ptrOffset + toTransformTypes);
        int[] mTransformTypes = new int[mNumTransforms];
        for (int transformI = 0; transformI < mNumTransforms; transformI++) {
            mTransformTypes[transformI] = input.readInt();
        }

        input.setPtr(ptrOffset + toFrameTimes);
        float[] mFrameTimes = new float[mNumFrames];
        for (int frameI = 0; frameI < mNumFrames; frameI++) {
            mFrameTimes[frameI] = input.readFloat();
        }

        input.setPtr(ptrOffset + toXFormFrames);
        int[] mXFormFramePtrPtrs = new int[mNumTransforms];
        for (int transformI = 0; transformI < mNumTransforms; transformI++) {
            mXFormFramePtrPtrs[transformI] = readPointer(input, isBinuni);
        }

        input.setPtr(ptrOffset + toAlphaFrames);
        int[] mAlphaFramePtrs = new int[mNumFrames];
        for (int frameI = 0; frameI < mNumFrames; frameI++) {
            mAlphaFramePtrs[frameI] = readPointer(input, isBinuni);
        }

        input.setPtr(ptrOffset + toColorFrames);
        int[] mColorFramePtrs = new int[mNumFrames];
        for (int frameI = 0; frameI < mNumFrames; frameI++) {
            mColorFramePtrs[frameI] = readPointer(input, isBinuni);
        }

        input.setPtr(ptrOffset + toSoundFrames);
        int[] mSoundFramePtrs = new int[mNumFrames];
        for (int frameI = 0; frameI < mNumFrames; frameI++) {
            mSoundFramePtrs[frameI] = readPointer(input, isBinuni);
        }

        int[][] mXFormFramePtrs = new int[mNumTransforms][mNumFrames];
        for (int transformI = 0; transformI < mNumTransforms; transformI++) {
            input.setPtr(ptrOffset + mXFormFramePtrPtrs[transformI]);
            for (int frameI = 0; frameI < mNumFrames; frameI++) {
                mXFormFramePtrs[transformI][frameI] = readPointer(input, isBinuni);
            }
        }

        Keyframe[] keyframes = new Keyframe[mNumFrames];
        for (int frameI = 0; frameI < mNumFrames; frameI++) {
            keyframes[frameI] = ObjectCreator.create(Keyframe.class, animation, version);
            keyframes[frameI].setAttribute("time", mFrameTimes[frameI]);
        }

        if (mHasTransform) for (int transformI = 0; transformI < mNumTransforms; transformI++) {
            int nextkeyframe = 0;
            while (nextkeyframe != -1) {
                input.setPtr(ptrOffset + mXFormFramePtrs[transformI][nextkeyframe]);
                nextkeyframe = readKeyframe(input, keyframes[nextkeyframe], "xForm" + mTransformTypes[transformI]);
            }
        }

        if (mHasAlpha) {
            int nextkeyframe = 0;
            while (nextkeyframe != -1) {
                input.setPtr(ptrOffset + mAlphaFramePtrs[nextkeyframe]);
                nextkeyframe = readKeyframe(input, keyframes[nextkeyframe], "alpha");
            }
        }

        if (mHasColor) {
            int nextkeyframe = 0;
            while (nextkeyframe != -1) {
                input.setPtr(ptrOffset + mColorFramePtrs[nextkeyframe]);
                nextkeyframe = readKeyframe(input, keyframes[nextkeyframe], "color");
            }
        }

        if (mHasSound) {
            int nextkeyframe = 0;
            while (nextkeyframe != -1) {
                input.setPtr(ptrOffset + mSoundFramePtrs[nextkeyframe]);
                nextkeyframe = readKeyframe(input, keyframes[nextkeyframe], "sound");
            }
        }

        return animation;

    }


    public static WOG1Animation openAnimation(File file, GameVersion version) {

        byte[] bytes;
        try {
            bytes = Files.readAllBytes(file.toPath());
        } catch (IOException e) {
            ErrorAlarm.show(e);
            return null;
        }

        Animation animation = readAnimationFromBytes(new PositionableInputStream(bytes), 0, version);

        // TODO: initialize strings for WOG1Animation
        strings strings = ObjectCreator.create(strings.class, null, version);

        return new WOG1Animation(version, animation, null, strings);

    }


    public static List<String> getAnimationNames(GameVersion version) {
        List<String> items = new ArrayList<>();
        File[] animations = new File(FileManager.getGameDir(version) + "/res/anim").listFiles();
        if (animations == null) return items;
        for (File file : animations) {
            String fileName = file.getName();
            // Remove last 12 chars for .anim.binltl or .anim.binuni
            items.add(fileName.substring(0, fileName.length() - 12));
        }
        return items;
    }


    /** Creates a new, empty animation. */
    public static WOG1Animation newAnimation(String name, GameVersion version) {

        // parameter 'name' is redundant, should remove this later!

        // create a blank animation object
        Animation animation = new Animation(null, version);

        // create a blank resources object
        Resources resources = new Resources(null, version);

        // create a blank strings object
        strings strings = new strings(null, version);

        return new WOG1Animation(version, animation, resources, strings);

    }

}
