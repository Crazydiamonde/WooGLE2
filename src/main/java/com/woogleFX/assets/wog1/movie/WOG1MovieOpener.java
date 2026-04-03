package com.woogleFX.assets.wog1.movie;

import com.woogleFX.assets.wog1.animation.WOG1AnimationOpener;
import com.woogleFX.editorObjects.EditorObject;
import com.woogleFX.editorObjects.objectCreators.ObjectCreator;
import com.woogleFX.engine.gui.alarms.ErrorAlarm;
import com.woogleFX.assets.GameVersion;
import com.woogleFX.file.fileImport.EditorObjectXMLReader;
import com.woogleFX.file.fileImport.PositionableInputStream;
import com.worldOfGoo.movie.Actor;
import com.worldOfGoo.movie.Movie;
import com.worldOfGoo.resrc.ResourceManifest;
import com.worldOfGoo.resrc.Resources;
import com.worldOfGoo.text.strings;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;

public class WOG1MovieOpener {

    private static int readPointer(PositionableInputStream input, boolean isBinuni) {
        int ptr = input.readInt();
        if (isBinuni) input.readInt();
        return ptr;
    }


    private static String getString(byte[] stringTable, int index) {
        StringBuilder stringBuilder = new StringBuilder();
        for (; stringTable[index] != 0; index++) {
            stringBuilder.append((char)stringTable[index]);
        }
        return stringBuilder.toString();
    }


    private static void readActor(PositionableInputStream input, Actor actor, byte[] stringTable) {
        int actorType = input.readInt();
        int mImageStringIndex = input.readInt();
        int mLabelTextStringIndex = input.readInt();
        int mFontStringIndex = input.readInt();
        float mLabelMaxWidth = input.readFloat();
        float mLabelWrapWidth = input.readFloat();
        int mLabelJustification = input.readInt();
        float mDepth = input.readFloat();

        actor.setAttribute("type", actorType);
        actor.setAttribute("image", getString(stringTable, mImageStringIndex));
        actor.setAttribute("label", getString(stringTable, mLabelTextStringIndex));
        actor.setAttribute("font", getString(stringTable, mFontStringIndex));
        actor.setAttribute("labelMaxWidth", mLabelMaxWidth);
        actor.setAttribute("labelWrapWidth", mLabelWrapWidth);
        actor.setAttribute("labelJustification", mLabelJustification);
        actor.setAttribute("depth", mDepth);

    }


    private static Movie readMovieFromBytes(byte[] data, GameVersion version) {

        PositionableInputStream input = new PositionableInputStream(data);

        boolean isBinuni = (version != GameVersion.VERSION_WOG1_OLD);

        if (isBinuni) input.setPtr(8);

        Movie movie = new Movie(null, version);

        float length = input.readFloat();
        int numActors = input.readInt();
        int toActors = readPointer(input, isBinuni);
        int toAnimsPtr = readPointer(input, isBinuni);
        int toStringTable = readPointer(input, isBinuni);
        movie.setAttribute("length", length);

        input.setPtr(toStringTable);
        byte[] stringTable = input.read(data.length - toStringTable);
        input.setPtr(toActors);
        Actor[] actors = new Actor[numActors];
        for (int actorI = 0; actorI < numActors; actorI++) {
            Actor actor = ObjectCreator.create(Actor.class, movie, version);
            actors[actorI] = actor;
            readActor(input, actor, stringTable);
        }

        input.setPtr(toAnimsPtr);
        int[] animPtrs = new int[numActors];
        for (int animationI = 0; animationI < numActors; animationI++) {
            animPtrs[animationI] = readPointer(input, isBinuni);
        }
        for (int animationI = 0; animationI < numActors; animationI++) {
            input.setPtr(animPtrs[animationI]);
            actors[animationI].setAnimation(WOG1AnimationOpener.readAnimationFromBytes(input, animPtrs[animationI], version));
            actors[animationI].getAnimation().setParent(actors[animationI]);
        }

        return movie;

    }



    public static WOG1Movie openMovie(File file, GameVersion version) {

        byte[] bytes;
        try {
            bytes = Files.readAllBytes(file.toPath());
        } catch (IOException e) {
            ErrorAlarm.show(e);
            return null;
        }


        ArrayList<EditorObject> objects = new ArrayList<>();

        Movie animation = readMovieFromBytes(bytes, version);

        animation.addAllChildren(objects);

        String resrcSuffix = (version == GameVersion.VERSION_WOG1_OLD) ? ".resrc.bin" : ".resrc";
        File resrcF = new File(file.getPath().substring(0, file.getPath().length() - resrcSuffix.length() - 3) + resrcSuffix);
        ResourceManifest resourceManifest;
        try {
            resourceManifest = EditorObjectXMLReader.readEditorObject(
                    version, resrcF, ResourceManifest.class);
        } catch (IOException e) {
            ErrorAlarm.show(e);
            return null;
        }
        assert resourceManifest != null;
        Resources resources = (Resources) resourceManifest.getChildren().get(0);

        strings strings = ObjectCreator.create(strings.class, null, version);

        return new WOG1Movie(version, animation, resources, strings);

    }

}
