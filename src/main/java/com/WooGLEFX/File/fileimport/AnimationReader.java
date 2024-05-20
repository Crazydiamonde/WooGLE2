package com.WooGLEFX.File.fileimport;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.WooGLEFX.Structures.SimpleStructures.Keyframe;
import com.WooGLEFX.Structures.SimpleStructures.WoGAnimation;

public class AnimationReader {

    private static final Logger logger = LoggerFactory.getLogger(AnimationReader.class);

    private static final boolean debug = false;

    public static byte[] subsection(byte[] input, int i1, int i2){
        byte[] output = new byte[i2 - i1];
        if (i2 - i1 >= 0) System.arraycopy(input, i1, output, 0, i2 - i1);
        return output;
    }

    public static int intFromBytes(byte[] input){
        int output = 0;
        for (int i = input.length - 1; i >= 0; i--){
            output *= 256;
            if (input[i] >= 0) {
                output += input[i];
            } else {
                output += input[i] + 256;
            }
        }
        return output;
    }

    public static WoGAnimation readBinuni(byte[] binuni, String name) {

        if (debug) {
            logger.debug("Opening binuni animation " + name);
        }

        byte[] HEADER = subsection(binuni, 0, 8);
        byte[] HAS_COLOR = subsection(binuni, 8, 12);
        byte[] HAS_ALPHA = subsection(binuni, 12, 16);
        byte[] HAS_SOUND = subsection(binuni, 16, 20);
        byte[] HAS_TRANSFORM = subsection(binuni, 20, 24);
        byte[] NUM_TRANSFORM = subsection(binuni, 24, 28);
        byte[] NUM_FRAMES = subsection(binuni, 28, 32);
        byte[] TRANSFORM_TYPES_OFFSET = subsection(binuni, 32, 40);
        byte[] FRAME_TIMES_OFFSET = subsection(binuni, 40, 48);
        byte[] X_FORM_FRAMES_OFFSET = subsection(binuni, 48, 56);
        byte[] ALPHA_FRAMES_OFFSET = subsection(binuni, 56, 64);
        byte[] COLOR_FRAMES_OFFSET = subsection(binuni, 64, 72);
        byte[] SOUND_FRAMES_OFFSET = subsection(binuni, 72, 80);
        byte[] STRING_TABLE_OFFSET = subsection(binuni, 80, 88);
        byte[] TRANSFORM_TYPES = subsection(binuni, intFromBytes(TRANSFORM_TYPES_OFFSET), intFromBytes(FRAME_TIMES_OFFSET));
        byte[] FRAME_TIMES = subsection(binuni, intFromBytes(FRAME_TIMES_OFFSET), intFromBytes(X_FORM_FRAMES_OFFSET));
        byte[] X_FORM_FRAMES = subsection(binuni, intFromBytes(X_FORM_FRAMES_OFFSET) + intFromBytes(NUM_TRANSFORM) * 8, intFromBytes(ALPHA_FRAMES_OFFSET));
        byte[] ALPHA_FRAMES = subsection(binuni, intFromBytes(ALPHA_FRAMES_OFFSET), intFromBytes(COLOR_FRAMES_OFFSET));
        byte[] COLOR_FRAMES = subsection(binuni, intFromBytes(COLOR_FRAMES_OFFSET), intFromBytes(SOUND_FRAMES_OFFSET));
        byte[] SOUND_FRAMES = subsection(binuni, intFromBytes(SOUND_FRAMES_OFFSET), intFromBytes(STRING_TABLE_OFFSET));
        //byte[] STRING_TABLE = subsection(binuni, intFromBytes(STRING_TABLE_OFFSET), intFromBytes(STRING_TABLE_OFFSET) + 8);

        StringBuilder header = new StringBuilder();
        for (int i = 0; i < 8; i++){
            header.append((char) HEADER[i]);
        }

        boolean hasColor = (HAS_COLOR[0] > 0);
        boolean hasAlpha = (HAS_ALPHA[0] > 0);
        boolean hasSound = (HAS_SOUND[0] > 0);
        boolean hasTransform = (HAS_TRANSFORM[0] > 0);

        int numTransform = intFromBytes(NUM_TRANSFORM);
        int numFrames = intFromBytes(NUM_FRAMES);

        int[] transformTypes = new int[numTransform];
        for (int i = 0; i < numTransform; i++){
            transformTypes[i] = intFromBytes(subsection(TRANSFORM_TYPES, i * 4, (i + 1) * 4));
        }

        float[] frameTimes = new float[numFrames];
        for (int i = 0; i < numFrames; i++){
            frameTimes[i] = ByteBuffer.wrap(subsection(FRAME_TIMES, i * 4, (i + 1) * 4)).order(ByteOrder.LITTLE_ENDIAN).getFloat();
        }

        int[][] xFormFrames = new int[numTransform][numFrames];

        for (int i = 0; i < numTransform; i++){
            for (int j = 0; j < numFrames; j++){
                xFormFrames[i][j] = ByteBuffer.wrap(subsection(X_FORM_FRAMES, (i * numFrames + j) * 8, (i * numFrames + j + 1) * 8)).order(ByteOrder.LITTLE_ENDIAN).getInt();
            }
        }

        int[] alphaFrames = new int[numFrames];
        for (int i = 0; i < numFrames; i++){
            alphaFrames[i] = intFromBytes(subsection(ALPHA_FRAMES, i * 8, (i + 1) * 8));
        }

        int[] colorFrames = new int[numFrames];
        for (int i = 0; i < numFrames; i++){
            colorFrames[i] = intFromBytes(subsection(COLOR_FRAMES, i * 8, (i + 1) * 8));
        }

        int[] soundFrames = new int[numFrames];
        for (int i = 0; i < numFrames; i++){
            soundFrames[i] = intFromBytes(subsection(SOUND_FRAMES, i * 8, (i + 1) * 8));
        }

        if (debug) {
            logger.debug("Header: " + header);

            logger.debug("Has color: " + hasColor);
            logger.debug("Has alpha: " + hasAlpha);
            logger.debug("Has sound: " + hasSound);
            logger.debug("Has transform: " + hasTransform);
            logger.debug("Transform count: " + numTransform);
            logger.debug("Frame count: " + numFrames);

            logger.debug("Transform types:");
            for (int type : transformTypes) {
                switch(type){
                    case 0 -> logger.debug(" (Scale)");
                    case 1 -> logger.debug(" (Rotate)");
                    case 2 -> logger.debug(" (Translate)");
                }
            }
        }

        if (debug) {
            logger.debug("Frame times: " + Arrays.toString(frameTimes));
            logger.debug("X form frames:");
            for (int[] xFrame : xFormFrames) {
                logger.debug(Arrays.toString(xFrame));
                for (int i1 = 0; i1 < xFrame.length; i1++) {
                    if (i1 < xFrame.length - 1) {
                        logger.debug(Arrays.toString(subsection(binuni, xFrame[i1], xFrame[i1 + 1])));
                    } else {
                        logger.debug(Arrays.toString(subsection(binuni, xFrame[i1], xFrame[i1 + 1])));
                    }
                }
            }
            logger.debug("Alpha frames: " + Arrays.toString(alphaFrames));
            logger.debug("Color frames: " + Arrays.toString(colorFrames));
            logger.debug("Sound frames: " + Arrays.toString(soundFrames));
        }


        //logger.debug("X FORM FRAMES");
        ArrayList<ArrayList<Keyframe>> keyframes1 = new ArrayList<>();
        for (int i = 0; i < 3; i++){
            keyframes1.add(new ArrayList<>());
        }
        for (int i = 0; i < transformTypes.length; i++){
            int[] xFrames = xFormFrames[i];
            for (int xFrame : xFrames) {
                if (xFrame == 0) {
                    keyframes1.get(transformTypes[i]).add(null);
                } else {
                    Keyframe test = new Keyframe(subsection(binuni, xFrame, xFrame + 32));
                    keyframes1.get(transformTypes[i]).add(test);
                }
            }
        }

        //logger.debug("ALPHA FRAMES");
        ArrayList<Keyframe> keyframes2 = new ArrayList<>();
        for (int alphaFrame : alphaFrames) {
            if (alphaFrame != 0) {
                Keyframe test = new Keyframe(subsection(binuni, alphaFrame, alphaFrame + 32));
                keyframes2.add(test);
            }
        }

        //logger.debug("COLOR FRAMES");
        ArrayList<Keyframe> keyframes3 = new ArrayList<>();
        for (int colorFrame : colorFrames) {
            if (colorFrame != 0) {
                Keyframe test = new Keyframe(subsection(binuni, colorFrame, colorFrame + 32));
                keyframes3.add(test);
            }
        }

        //logger.debug("SOUND FRAMES");
        ArrayList<Keyframe> keyframes4 = new ArrayList<>();
        for (int soundFrame : soundFrames) {
            if (soundFrame != 0) {
                Keyframe test = new Keyframe(subsection(binuni, soundFrame, soundFrame + 32));
                keyframes4.add(test);
            }
        }

        WoGAnimation animation = new WoGAnimation(keyframes1, keyframes2, keyframes3, keyframes4, name, frameTimes);

        if (debug) {
            for (int i2 = 0; i2 < (binuni.length - intFromBytes(STRING_TABLE_OFFSET)) / 4 - 2; i2++) {
                int i = i2 + 1;
                //logger.debug(Arrays.toString(subsection(binuni, i * 4 + intFromBytes(STRING_TABLE_OFFSET), (i + 1) * 4 + intFromBytes(STRING_TABLE_OFFSET))));
                System.out.print("Index " + i2 + " (" + (i * 4 + intFromBytes(STRING_TABLE_OFFSET)) + ") (");
                switch (i2 % 8) {
                    case 0 -> System.out.print("x");
                    case 1 -> System.out.print("y");
                    case 2 -> System.out.print("rotation");
                    case 3 -> System.out.print("alpha");
                    case 4 -> System.out.print("color");
                    case 5 -> System.out.print("next frame");
                    case 6 -> System.out.print("soundStrIdx");
                    case 7 -> System.out.print("interpolation type");
                }
                if (Math.abs(ByteBuffer.wrap(subsection(binuni, i * 4 + intFromBytes(STRING_TABLE_OFFSET), (i + 1) * 4 + intFromBytes(STRING_TABLE_OFFSET))).order(ByteOrder.LITTLE_ENDIAN).getInt()) > 999) {
                    logger.debug("): " + ByteBuffer.wrap(subsection(binuni, i * 4 + intFromBytes(STRING_TABLE_OFFSET), (i + 1) * 4 + intFromBytes(STRING_TABLE_OFFSET))).order(ByteOrder.LITTLE_ENDIAN).getFloat());
                } else {
                    logger.debug("): " + ByteBuffer.wrap(subsection(binuni, i * 4 + intFromBytes(STRING_TABLE_OFFSET), (i + 1) * 4 + intFromBytes(STRING_TABLE_OFFSET))).order(ByteOrder.LITTLE_ENDIAN).getInt());
                }
            }
        }


        return animation;


    }

    public static WoGAnimation readBinltl(byte[] binuni, String name) {

        if (debug) {
            logger.debug("Opening binltl animation " + name);
        }

        byte[] HAS_COLOR = subsection(binuni, 0, 4);
        byte[] HAS_ALPHA = subsection(binuni, 4, 8);
        byte[] HAS_SOUND = subsection(binuni, 8, 12);
        byte[] HAS_TRANSFORM = subsection(binuni, 12, 16);
        byte[] NUM_TRANSFORM = subsection(binuni, 16, 20);
        byte[] NUM_FRAMES = subsection(binuni, 20, 24);
        byte[] TRANSFORM_TYPES_OFFSET = subsection(binuni, 24, 28);
        byte[] FRAME_TIMES_OFFSET = subsection(binuni, 28, 32);
        byte[] X_FORM_FRAMES_OFFSET = subsection(binuni, 32, 36);
        byte[] ALPHA_FRAMES_OFFSET = subsection(binuni, 36, 40);
        byte[] COLOR_FRAMES_OFFSET = subsection(binuni, 40, 44);
        byte[] SOUND_FRAMES_OFFSET = subsection(binuni, 44, 48);
        byte[] STRING_TABLE_OFFSET = subsection(binuni, 48, 52);

        byte[] TRANSFORM_TYPES = subsection(binuni, intFromBytes(TRANSFORM_TYPES_OFFSET), intFromBytes(FRAME_TIMES_OFFSET));
        byte[] FRAME_TIMES = subsection(binuni, intFromBytes(FRAME_TIMES_OFFSET), intFromBytes(X_FORM_FRAMES_OFFSET));
        byte[] X_FORM_FRAMES = subsection(binuni, intFromBytes(X_FORM_FRAMES_OFFSET) + intFromBytes(NUM_TRANSFORM) * 4, intFromBytes(ALPHA_FRAMES_OFFSET));
        byte[] ALPHA_FRAMES = subsection(binuni, intFromBytes(ALPHA_FRAMES_OFFSET), intFromBytes(COLOR_FRAMES_OFFSET));
        byte[] COLOR_FRAMES = subsection(binuni, intFromBytes(COLOR_FRAMES_OFFSET), intFromBytes(SOUND_FRAMES_OFFSET));
        byte[] SOUND_FRAMES = subsection(binuni, intFromBytes(SOUND_FRAMES_OFFSET), intFromBytes(STRING_TABLE_OFFSET));
        //byte[] STRING_TABLE = subsection(binuni, intFromBytes(STRING_TABLE_OFFSET), intFromBytes(STRING_TABLE_OFFSET) + 4);

        boolean hasColor = (HAS_COLOR[0] > 0);
        boolean hasAlpha = (HAS_ALPHA[0] > 0);
        boolean hasSound = (HAS_SOUND[0] > 0);
        boolean hasTransform = (HAS_TRANSFORM[0] > 0);

        int numTransform = intFromBytes(NUM_TRANSFORM);
        int numFrames = intFromBytes(NUM_FRAMES);

        int[] transformTypes = new int[numTransform];
        for (int i = 0; i < numTransform; i++){
            transformTypes[i] = intFromBytes(subsection(TRANSFORM_TYPES, i * 4, (i + 1) * 4));
        }

        float[] frameTimes = new float[numFrames];
        for (int i = 0; i < numFrames; i++){
            frameTimes[i] = ByteBuffer.wrap(subsection(FRAME_TIMES, i * 4, (i + 1) * 4)).order(ByteOrder.LITTLE_ENDIAN).getFloat();
        }

        int[][] xFormFrames = new int[numTransform][numFrames];

        for (int i = 0; i < numTransform; i++){
            for (int j = 0; j < numFrames; j++){
                xFormFrames[i][j] = ByteBuffer.wrap(subsection(X_FORM_FRAMES, (i * numFrames + j) * 4, (i * numFrames + j + 1) * 4)).order(ByteOrder.LITTLE_ENDIAN).getInt();
            }
        }

        int[] alphaFrames = new int[numFrames];
        for (int i = 0; i < numFrames; i++){
            alphaFrames[i] = intFromBytes(subsection(ALPHA_FRAMES, i * 4, (i + 1) * 4));
        }

        int[] colorFrames = new int[numFrames];
        for (int i = 0; i < numFrames; i++){
            colorFrames[i] = intFromBytes(subsection(COLOR_FRAMES, i * 4, (i + 1) * 4));
        }

        int[] soundFrames = new int[numFrames];
        for (int i = 0; i < numFrames; i++){
            soundFrames[i] = intFromBytes(subsection(SOUND_FRAMES, i * 4, (i + 1) * 4));
        }

        if (debug) {

            logger.debug("Has color: " + hasColor);
            logger.debug("Has alpha: " + hasAlpha);
            logger.debug("Has sound: " + hasSound);
            logger.debug("Has transform: " + hasTransform);
            logger.debug("Transform count: " + numTransform);
            logger.debug("Frame count: " + numFrames);

            logger.debug("Transform types:");
            for (int type : transformTypes) {
                switch (type) {
                    case 0 -> logger.debug("0 (Scale)");
                    case 1 -> logger.debug("1 (Rotate)");
                    case 2 -> logger.debug("2 (Translate)");
                }
            }

            logger.debug(Arrays.toString(X_FORM_FRAMES));
            logger.debug("Frame times: " + Arrays.toString(frameTimes));
            logger.debug("X form frames:");
            for (int[] xFrame : xFormFrames) {
                logger.debug(Arrays.toString(xFrame));
                for (int i1 = 0; i1 < xFrame.length - 1; i1++) {
                    if (xFrame[i1 + 1] != 0) {
                        logger.debug(Arrays.toString(subsection(binuni, xFrame[i1], xFrame[i1 + 1])));
                    }
                }
            }

            logger.debug("Alpha frames: " + Arrays.toString(alphaFrames));
            logger.debug("Color frames: " + Arrays.toString(colorFrames));
            logger.debug("Sound frames: " + Arrays.toString(soundFrames));

        }


        //logger.debug("X FORM FRAMES");
        ArrayList<ArrayList<Keyframe>> keyframes1 = new ArrayList<>();
        for (int i = 0; i < 3; i++){
            keyframes1.add(new ArrayList<>());
        }
        for (int i = 0; i < transformTypes.length; i++){
            int[] xFrames = xFormFrames[i];
            for (int xFrame : xFrames) {
                if (xFrame == 0) {
                    keyframes1.get(transformTypes[i]).add(null);
                } else {
                    Keyframe test = new Keyframe(subsection(binuni, xFrame, xFrame + 32));
                    keyframes1.get(transformTypes[i]).add(test);
                }
            }
        }

        //logger.debug("ALPHA FRAMES");
        ArrayList<Keyframe> keyframes2 = new ArrayList<>();
        for (int alphaFrame : alphaFrames) {
            if (alphaFrame != 0) {
                Keyframe test = new Keyframe(subsection(binuni, alphaFrame, alphaFrame + 32));
                keyframes2.add(test);
            }
        }

        //logger.debug("COLOR FRAMES");
        ArrayList<Keyframe> keyframes3 = new ArrayList<>();
        for (int colorFrame : colorFrames) {
            if (colorFrame != 0) {
                Keyframe test = new Keyframe(subsection(binuni, colorFrame, colorFrame + 32));
                keyframes3.add(test);
            }
        }

        //logger.debug("SOUND FRAMES");
        ArrayList<Keyframe> keyframes4 = new ArrayList<>();
        for (int soundFrame : soundFrames) {
            if (soundFrame != 0) {
                Keyframe test = new Keyframe(subsection(binuni, soundFrame, soundFrame + 32));
                keyframes4.add(test);
            }
        }

        WoGAnimation animation = new WoGAnimation(keyframes1, keyframes2, keyframes3, keyframes4, name, frameTimes);

        if (debug) {
            for (int i2 = 0; i2 < (binuni.length - intFromBytes(STRING_TABLE_OFFSET)) / 4 - 2; i2++) {
                int i = i2 + 1;
                //logger.debug(Arrays.toString(subsection(binuni, i * 4 + intFromBytes(STRING_TABLE_OFFSET), (i + 1) * 4 + intFromBytes(STRING_TABLE_OFFSET))));
                System.out.print("Index " + i2 + " (" + (i * 4 + intFromBytes(STRING_TABLE_OFFSET)) + ") (");
                switch (i2 % 8) {
                    case 0 -> System.out.print("x");
                    case 1 -> System.out.print("y");
                    case 2 -> System.out.print("rotation");
                    case 3 -> System.out.print("alpha");
                    case 4 -> System.out.print("color");
                    case 5 -> System.out.print("next frame");
                    case 6 -> System.out.print("soundStrIdx");
                    case 7 -> System.out.print("interpolation type");
                }
                ByteBuffer byteBuffer = ByteBuffer.wrap(subsection(binuni, i * 4 + intFromBytes(STRING_TABLE_OFFSET), (i + 1) * 4 + intFromBytes(STRING_TABLE_OFFSET))).order(ByteOrder.LITTLE_ENDIAN);
                switch (i2 % 8) {
                    case 0, 1, 2 -> logger.debug("): " + byteBuffer.getFloat());
                    case 3, 4, 5, 6, 7 -> logger.debug("): " + byteBuffer.getInt());
                }
            }
        }


        return animation;


    }

}
