package com.worldOfGoo2.anim;

import com.woogleFX.editorObjects.EditorObject;
import com.woogleFX.assets.GameVersion;
import com.woogleFX.gameData.animation.SimpleBinAnimation;

public class Part extends EditorObject {

    public Part(EditorObject parent, GameVersion version) {
        super(parent, version);
    }

    public void loadFromAnimation(SimpleBinAnimation.SimpleBinAnimationPart simpleBinAnimationPart,
                                  SimpleBinAnimation animation) {

        setAttribute("image", animation.stringTable[animation.imageStringTableIndices[simpleBinAnimationPart.imageIndex]]);

    }

}
