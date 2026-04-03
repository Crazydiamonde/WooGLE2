package com.worldOfGoo2.anim;

import com.woogleFX.editorObjects.EditorObject;
import com.woogleFX.editorObjects.objectCreators.ObjectCreator;
import com.woogleFX.gameData.animation.SimpleBinAnimation;
import com.woogleFX.assets.GameVersion;

public class Section extends EditorObject {

    public Section(EditorObject parent, GameVersion version) {
        super(parent, version);
    }


    public void loadFromAnimation(SimpleBinAnimation.SimpleBinAnimationSection simpleBinAnimationSection,
                                  SimpleBinAnimation animation) {

        setAttribute("type", simpleBinAnimationSection.type);

        int elementOffset = simpleBinAnimationSection.elementOffset;
        int elementLength = simpleBinAnimationSection.elementLength;
        for (int elementIndex = elementOffset; elementIndex < elementOffset + elementLength; elementIndex++) {
            SimpleBinAnimation.SimpleBinAnimationElement simpleBinAnimationElement = animation.elements[elementIndex];
            int index = simpleBinAnimationElement.offset;
            if (simpleBinAnimationElement.type == 1) {
                SimpleBinAnimation.SimpleBinAnimationKeyframe simpleBinAnimationKeyframe = animation.keyframes[index];
                Keyframe keyframe = ObjectCreator.create(Keyframe.class, this, "", GameVersion.VERSION_WOG2);
                keyframe.loadFromAnimation(simpleBinAnimationKeyframe, animation);
            }
            if (simpleBinAnimationElement.type == 2) {
                SimpleBinAnimation.SimpleBinAnimationPart simpleBinAnimationPart = animation.parts[index];
                Part part = ObjectCreator.create(Part.class, this, "", GameVersion.VERSION_WOG2);
                part.loadFromAnimation(simpleBinAnimationPart, animation);
            }
        }

    }

}
