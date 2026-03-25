package com.worldOfGoo.movie;

import com.woogleFX.assets.wog1.movie.WOG1Movie;
import com.woogleFX.editorObjects.EditorObject;
import com.woogleFX.assets.GameVersion;

public class Movie extends EditorObject {

    public Movie(EditorObject parent, GameVersion version) {
        super(parent, version);
    }

    @Override
    public void frameUpdate(double deltaTime) {
        super.frameUpdate(deltaTime);

        WOG1Movie movie = (WOG1Movie) getAsset();
        if (movie.isPlaying()) movie.setTime(movie.getTime() + deltaTime);

    }
}
