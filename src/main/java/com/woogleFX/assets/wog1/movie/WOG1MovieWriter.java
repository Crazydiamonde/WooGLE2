package com.woogleFX.assets.wog1.movie;

import com.woogleFX.assets.GameVersion;
import com.woogleFX.assets.wog1.animation.WOG1AnimationOpener;
import com.woogleFX.assets.wog1.animation.WOG1AnimationWriter;
import com.woogleFX.editorObjects.EditorObject;
import com.woogleFX.engine.gui.alarms.ErrorAlarm;
import com.woogleFX.file.fileImport.PositionableInputStream;
import com.worldOfGoo.movie.Actor;
import com.worldOfGoo.movie.Movie;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

public class WOG1MovieWriter {

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


    private static void writeMovieToBytes(ByteArrayOutputStream output, Movie movie, GameVersion version, boolean isBinuni) throws IOException {

        // struct BinMovie
        // {
        //     +0x00:       float length;
        //     +0x04:       int numActors;
        //     +0x08:       BinActor *pActors;
        //     +0x0c:       BinImageAnimation **ppAnims;
        //     +0x10:       const char *pStringTable;
        // };

        if (isBinuni) {
            // write header
            output.write("BINUNIMO".getBytes());
        }

        // write length
        writeFloat(output, (float)movie.getAttribute("length").doubleValue());

        // write num actors
        // this will correspond exactly with how many children the movie object has
        writeInt(output, movie.getChildren().size());

        int ptrLength = isBinuni ? 8 : 4;

        // write *pActors
        // this will be after 4 pointers + anim ptrs have been listed
        if (isBinuni) {
            // header (8) + length (4) + num actors (4) + 4 pointers (4 x 8 = 32) = 48 + anim ptrs
            writeInt(output, 48 + ptrLength * movie.getChildren().size());
        } else {
            // length (4) + num actors (4) + 4 pointers (4 x 4 = 16) = 24 + anim ptrs
            writeInt(output, 24 + ptrLength * movie.getChildren().size());
        }

        // write **ppAnims
        // i think we can actually put this wherever? i'm gonna put it right after these
        if (isBinuni) {
            // header (8) + length (4) + num actors (4) + 3 pointers (3 x 8 = 24) = 40
            writeInt(output, 40);
        } else {
            // length (4) + num actors (4) + 3 pointers (3 x 4 = 12) = 20
            writeInt(output, 20);
        }

        // write *pStringTable
        // TODO:
        writePointer(output, 0, isBinuni); // placeholder

        // write *pAnims
        ArrayList<Integer> animationOutputLengths = new ArrayList<>();
        for (EditorObject actor : movie.getChildren()) {
            // this should always be true but i dont trust my own reasoning enough to assert that
            if (!(actor instanceof Actor _actor)) continue;
            ByteArrayOutputStream output2 = new ByteArrayOutputStream();
            WOG1AnimationWriter.writeAnimationToBytes(output2, _actor.getAnimation(), 0, version);
            animationOutputLengths.add(output2.size());
        }

        int c = 0;
        for (int i = 0; i < movie.getChildren().size(); i++) {
            if (isBinuni) {
                writeInt(output, 56 + movie.getChildren().size() * 32 + c);
            } else {
                writeInt(output, 20 + movie.getChildren().size() * 32 + c);
            }
            c += animationOutputLengths.get(i);
            // i have NO IDEA if this will work :)
        }

        // write the actors
        for (EditorObject actor : movie.getChildren()) {

            // struct BinActor
            // {
            //     +0x00:       ActorType mType;
            //     +0x04:       int mImageStrIdx;
            //     +0x08:       int mLabelTextStrIdx;
            //     +0x0c:       int mFontStrIdx;
            //     +0x10:       float mLabelMaxWidth;
            //     +0x14:       float mLabelWrapWidth;
            //     +0x18:       AlignmentH mLabelJustification;
            //     +0x1c:       float mDepth;
            // };

            // write mImageStrIdx
            writeInt(output, 0); // TODO: get the index by looking at the string table and the "image" attribute

            // write mLabelTextStrIdx
            writeInt(output, actor.getAttribute("label").intValue());

            // write mFontStrIdx
            writeInt(output, actor.getAttribute("font").intValue());

            // write mLabelMaxWidth
            writeInt(output, actor.getAttribute("labelMaxWidth").intValue());

            // write mLabelWrapWidth
            writeInt(output, actor.getAttribute("labelWrapWidth").intValue());

            // write mLabelJustification
            writeInt(output, actor.getAttribute("labelJustification").intValue());

            // write mDepth
            writeFloat(output, (float)actor.getAttribute("depth").doubleValue());

        }

        // write the animations
        // there's one animation for each actor
        int i = (isBinuni ? 56 : 20) + movie.getChildren().size() * 32;
        for (EditorObject actor : movie.getChildren()) {
            // this should always be true but i dont trust my own reasoning enough to assert that
            if (!(actor instanceof Actor _actor)) continue;
            ByteArrayOutputStream output2 = new ByteArrayOutputStream();
            WOG1AnimationWriter.writeAnimationToBytes(output2, _actor.getAnimation(), i, version);
            i += output2.size();
        }

    }


    public static boolean saveMovie(WOG1Movie movie, String outputPathString, GameVersion version) {

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try {
            writeMovieToBytes(output, movie.getMovie(), version, version == GameVersion.VERSION_WOG1_NEW);
            WOG1AnimationOpener.readAnimationFromBytes(new PositionableInputStream(output.toByteArray()), 0, version);
            Files.write(Path.of(outputPathString), output.toByteArray());
            return true;
        } catch (IOException e) {
            ErrorAlarm.show(e);
            return false;
        }

    }

}
