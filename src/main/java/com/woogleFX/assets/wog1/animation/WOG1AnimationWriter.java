package com.woogleFX.assets.wog1.animation;

import com.woogleFX.assets.GameVersion;
import com.woogleFX.editorObjects.EditorObject;
import com.woogleFX.engine.gui.alarms.ErrorAlarm;
import com.woogleFX.file.fileImport.PositionableInputStream;
import com.worldOfGoo.anim.Animation;
import javafx.util.Pair;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class WOG1AnimationWriter {

    private static void writeInt(ByteArrayOutputStream output, int i) throws IOException {
        output.write(ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(i).array());
    }


    private static void writeFloat(ByteArrayOutputStream output, float i) throws IOException {
        output.write(ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putFloat(i).array());
    }


    private static void writePointer(ByteArrayOutputStream output, int ptr, boolean isBinuni) throws IOException {
        output.write(ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(ptr).array());
        if (isBinuni) output.write(ByteBuffer.allocate(4).array());
    }


    private static void writeKeyframe(ByteArrayOutputStream output, Number a, Number b, String type, int interpolation, int nextFrameIndex) throws IOException {

        float x = 0.0f;
        float y = 0.0f;
        float angle = 0.0f;
        int alpha = 255;
        int color = 0xFFFFFFFF;
        int soundStringIndex = 0;

        if (type.equals("xForm2")) x = a.floatValue();
        if (type.equals("xForm2")) y = b.floatValue();
        if (type.equals("xForm0")) x = a.floatValue();
        if (type.equals("xForm0")) y = b.floatValue();
        if (type.equals("xForm1")) angle = a.floatValue();
        if (type.equals("alpha")) alpha = a.intValue();
        if (type.equals("color")) color = a.intValue();
        if (type.equals("sound")) soundStringIndex = a.intValue();

        writeFloat(output, x);
        writeFloat(output, y);
        writeFloat(output, angle);
        writeInt(output, alpha);
        writeInt(output, color);
        writeInt(output, nextFrameIndex);
        writeInt(output, soundStringIndex);
        writeInt(output, interpolation);

    }


    public static void writeAnimationToBytes(ByteArrayOutputStream output, Animation animation, int bytesOffset, GameVersion version) throws IOException {

        // Get unique frames
        Map<Float, Pair<Number, Integer>> positionXMap = new HashMap<>();
        Map<Float, Pair<Number, Integer>> positionYMap = new HashMap<>();
        Map<Float, Pair<Number, Integer>> scaleXMap = new HashMap<>();
        Map<Float, Pair<Number, Integer>> scaleYMap = new HashMap<>();
        Map<Float, Pair<Number, Integer>> angleMap = new HashMap<>();
        Map<Float, Pair<Number, Integer>> alphaMap = new HashMap<>();
        Map<Float, Pair<Number, Integer>> colorMap = new HashMap<>();
        Map<Float, Pair<Number, Integer>> soundStringIndexMap = new HashMap<>();

        ArrayList<Double> frameTimesList = new ArrayList<>();
        for (EditorObject keyframe : animation.getChildren()) {
            double time = keyframe.getAttribute("time").doubleValue();
            if (!frameTimesList.contains(time)) frameTimesList.add(time);

            int interpolation = keyframe.getAttribute("interpolation").intValue();

            if (!keyframe.getAttribute("x").actualValue().isEmpty()) positionXMap.put((float)time,
                    new Pair<>(keyframe.getAttribute("x").doubleValue(), interpolation));

            if (!keyframe.getAttribute("y").actualValue().isEmpty()) positionYMap.put((float)time,
                    new Pair<>(keyframe.getAttribute("y").doubleValue(), interpolation));

            if (!keyframe.getAttribute("scaleX").actualValue().isEmpty()) scaleXMap.put((float)time,
                    new Pair<>(keyframe.getAttribute("scaleX").doubleValue(), interpolation));

            if (!keyframe.getAttribute("scaleY").actualValue().isEmpty()) scaleYMap.put((float)time,
                    new Pair<>(keyframe.getAttribute("scaleY").doubleValue(), interpolation));

            if (!keyframe.getAttribute("angle").actualValue().isEmpty()) angleMap.put((float)time,
                    new Pair<>(keyframe.getAttribute("angle").doubleValue(), interpolation));

            if (!keyframe.getAttribute("alpha").actualValue().isEmpty()) alphaMap.put((float)time,
                    new Pair<>(keyframe.getAttribute("alpha").intValue(), interpolation));

            if (!keyframe.getAttribute("color").actualValue().isEmpty()) colorMap.put((float)time,
                    new Pair<>(keyframe.getAttribute("color").intValue(), interpolation));

            if (!keyframe.getAttribute("soundStringIndex").actualValue().isEmpty()) soundStringIndexMap.put((float)time,
                    new Pair<>(keyframe.getAttribute("soundStringIndex").intValue(), interpolation));

        }

        frameTimesList.sort(Double::compareTo);

        int numFrames = frameTimesList.size();
        float[] frameTimes = new float[numFrames];
        for (int i = 0; i < numFrames; i++) frameTimes[i] = (float) frameTimesList.get(i).doubleValue();


        int numPositions = positionXMap.size();
        int numScales = scaleXMap.size();
        int numRotations = angleMap.size();

        int numAlphas = alphaMap.size();
        int numColors = colorMap.size();
        int numSounds = soundStringIndexMap.size();

        ArrayList<Integer> transformTypesList = new ArrayList<>();
        if (numScales > 0) transformTypesList.add(0);
        if (numRotations > 0) transformTypesList.add(1);
        if (numPositions > 0) transformTypesList.add(2);
        int numTransforms = transformTypesList.size();
        int[] transformTypes = new int[numTransforms];
        for (int i = 0; i < numTransforms; i++) transformTypes[i] = transformTypesList.get(i);


        boolean isBinuni = (version != GameVersion.VERSION_WOG1_OLD);
        int headerSize = (isBinuni) ? 8 : 0;
        int ptrSize = (isBinuni) ? 8 : 4;

        if (isBinuni) output.write("BINUNIAN".getBytes());


        writeInt(output, (numColors > 0) ? 1 : 0);
        writeInt(output, (numAlphas > 0) ? 1 : 0);
        writeInt(output, (numSounds > 0) ? 1 : 0);
        writeInt(output, (numTransforms > 0) ? 1 : 0);
        writeInt(output, numTransforms);
        writeInt(output, numFrames);

        int toTransformTypes = headerSize + 24 + 7 * ptrSize;
        int toFrameTimes = toTransformTypes + numTransforms * 4;
        int toXFormFrames = toFrameTimes + numFrames * 4;
        int toAlphaFrames = toXFormFrames + numTransforms * ptrSize;
        int toColorFrames = toAlphaFrames + numFrames * ptrSize;
        int toSoundFrames = toColorFrames + numFrames * ptrSize;
        int toStringTable = toSoundFrames + numFrames * ptrSize;

        int toXFormFrames2 = toStringTable + ptrSize;
        int toAlphaFrames2 = toXFormFrames2 + numTransforms * numFrames * ptrSize + numTransforms * numFrames * 32;
        int toColorFrames2 = toAlphaFrames2 + numFrames * 32;
        int toSoundFrames2 = toColorFrames2 + numFrames * 32;

        writePointer(output,  bytesOffset + toTransformTypes, isBinuni);
        writePointer(output,  bytesOffset + toFrameTimes, isBinuni);
        writePointer(output,  bytesOffset + toXFormFrames, isBinuni);
        writePointer(output,  bytesOffset + toAlphaFrames, isBinuni);
        writePointer(output,  bytesOffset + toColorFrames, isBinuni);
        writePointer(output,  bytesOffset + toSoundFrames, isBinuni);
        writePointer(output,  bytesOffset + toStringTable, isBinuni);

        // Write transform types
        for (int transform : transformTypes) {
            writeInt(output, transform);
        }

        // Write frame times
        for (float frameTime : frameTimes) {
            writeFloat(output, frameTime);
        }

        // Write x form frame pointer pointers
        for (int i = 0; i < numTransforms; i++) {
            writePointer(output, bytesOffset + toXFormFrames2 + i * numFrames * ptrSize, isBinuni);
        }

        // Write alpha frame pointers
        for (int i = 0; i < numFrames; i++) {
            writePointer(output, bytesOffset + toAlphaFrames2 + i * 32, isBinuni);
        }

        // Write color frame pointers
        for (int i = 0; i < numFrames; i++) {
            writePointer(output, bytesOffset + toColorFrames2 + i * 32, isBinuni);
        }

        // Write sound frame pointers
        for (int i = 0; i < numFrames; i++) {
            writePointer(output, bytesOffset + toSoundFrames2 + i * 32, isBinuni);
        }

        // Write string table pointer
        writePointer(output, bytesOffset + 0, isBinuni); // TODO: export string table

        // Write x form frame pointers
        for (int i = 0; i < numTransforms; i++) {
            for (int j = 0; j < numFrames; j++) {
                writePointer(output, bytesOffset + toXFormFrames2 + numTransforms * numFrames * ptrSize + i * numFrames * 32 + j * 32, isBinuni);
            }
        }

        // Write x form frames
        for (int transformType : transformTypes) {
            if (transformType == 0) {
                writeKeyframes(output, frameTimes, scaleXMap, scaleYMap, "xForm0");
            } else if (transformType == 1) {
                writeKeyframes(output, frameTimes, angleMap, null, "xForm1");
            } else if (transformType == 2) {
                writeKeyframes(output, frameTimes, positionXMap, positionYMap, "xForm2");
            }
        }

        // Write alpha frames
        writeKeyframes(output, frameTimes, alphaMap, null, "alpha");

        // Write color frames
        writeKeyframes(output, frameTimes, colorMap, null, "color");

        // Write sound frames
        writeKeyframes(output, frameTimes, soundStringIndexMap, null, "sound");

    }


    private static void writeKeyframes(
            ByteArrayOutputStream output, float[] frameTimes,
            Map<Float, Pair<Number, Integer>> map1, Map<Float, Pair<Number, Integer>> map2, String type) throws IOException {

        int[] frames = new int[map1.size()];
        int j = 0;
        for (int i = 0; i < frameTimes.length; i++) {
            if (map1.containsKey(frameTimes[i])) {
                frames[j] = i;
                j++;
            }
        }
        int i = 0;
        for (float frameTime : frameTimes) {
            if (map1.containsKey(frameTime)) {
                writeKeyframe(output, map1.get(frameTime).getKey(), (map2 == null ? 0 : map2.get(frameTime).getKey()), type, map1.get(frameTime).getValue(), (i == frames.length - 1 ? -1 : frames[i + 1]));
                i++;
            } else {
                writeKeyframe(output, 0, 0, "", 0, -1);
            }
        }

    }


    public static boolean saveAnimation(WOG1Animation animation, String outputPathString, GameVersion version) {

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try {
            writeAnimationToBytes(output, animation.getAnimation(), 0, version);
            WOG1AnimationOpener.readAnimationFromBytes(new PositionableInputStream(output.toByteArray()), 0, version);
            Files.write(Path.of(outputPathString), output.toByteArray());
            return true;
        } catch (IOException e) {
            ErrorAlarm.show(e);
            return false;
        }

    }

}
