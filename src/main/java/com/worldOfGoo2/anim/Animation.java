package com.worldOfGoo2.anim;

import com.woogleFX.editorObjects.EditorObject;
import com.woogleFX.assets.Asset;
import com.woogleFX.editorObjects.objectCreators.ObjectCreator;
import com.woogleFX.gameData.animation.SimpleBinAnimation;
import com.woogleFX.assets.GameVersion;
import com.worldOfGoo2.util.BinAnimationHelper;

public class Animation extends EditorObject {

    public Animation(EditorObject parent, GameVersion version) {
        super(parent, version);
    }


    private SimpleBinAnimation simpleBinAnimation;


    public void loadFromAnimation(SimpleBinAnimation animation) {

        this.simpleBinAnimation = animation;

        setAttribute("fps", animation.fps);

        for (SimpleBinAnimation.SimpleBinAnimationState simpleBinAnimationState : animation.states) {

            State state = ObjectCreator.create(State.class, this, "", GameVersion.VERSION_WOG2);
            state.loadFromAnimation(simpleBinAnimationState, animation);

        }

    }


    public void onLoaded(Asset asset) {
        super.onLoaded(asset);

        BinAnimationHelper.addBinAnimationAsObjectPositions(this, simpleBinAnimation, "", new BinAnimationHelper.BinAnimationInterface() {
            @Override
            public double getX() {
                return 0;
            }

            @Override
            public void setX(double x) {

            }

            @Override
            public double getY() {
                return 0;
            }

            @Override
            public void setY(double y) {

            }

            @Override
            public double getScaleX() {
                return 1.0;
            }

            @Override
            public void setScaleX(double scaleX) {

            }

            @Override
            public double getScaleY() {
                return 1.0;
            }

            @Override
            public void setScaleY(double scaleY) {

            }

            @Override
            public double getRotation() {
                return 0;
            }

            @Override
            public void setRotation(double rotation) {

            }

            @Override
            public double getDepth() {
                return 0;
            }
        });

    }

}
