package com.worldOfGoo2.anim;

import com.woogleFX.editorObjects.EditorObject;
import com.woogleFX.editorObjects.objectCreators.ObjectCreator;
import com.woogleFX.gameData.animation.SimpleBinAnimation;
import com.woogleFX.assets.GameVersion;

public class State extends EditorObject {

    public State(EditorObject parent, GameVersion version) {
        super(parent, version);
    }

    public void loadFromAnimation(SimpleBinAnimation.SimpleBinAnimationState simpleBinAnimationState,
                                  SimpleBinAnimation animation) {

        setAttribute("globalIdHash", simpleBinAnimationState.globalIdHash);
        setAttribute("localIdHash", simpleBinAnimationState.localIdHash);

        int groupOffset = simpleBinAnimationState.groupOffset;
        SimpleBinAnimation.SimpleBinAnimationGroup simpleBinAnimationGroup = animation.groups[groupOffset];

        setAttribute("startingFrame", simpleBinAnimationGroup.startingFrame);
        setAttribute("duration", simpleBinAnimationGroup.duration);

        int sectionOffset = simpleBinAnimationGroup.sectionOffset;
        int sectionLength = simpleBinAnimationGroup.sectionLength;
        for (int sectionIndex = sectionOffset; sectionIndex < sectionOffset + sectionLength; sectionIndex++) {
            SimpleBinAnimation.SimpleBinAnimationSection simpleBinAnimationSection = animation.sections[sectionIndex];
            Section section = ObjectCreator.create(Section.class, this, "", GameVersion.VERSION_WOG2);
            section.loadFromAnimation(simpleBinAnimationSection, animation);
        }

    }

}
