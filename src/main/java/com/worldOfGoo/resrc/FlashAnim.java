package com.worldOfGoo.resrc;

import com.woogleFX.editorObjects.EditorObject;
import com.woogleFX.gameData.animation.SimpleBinAnimation;
import com.woogleFX.assets.GameVersion;

public class FlashAnim extends EditorObject implements ResourceInterface {

    private SimpleBinAnimation animation;
    public SimpleBinAnimation getAnimation() {
        return animation;
    }
    public void setAnimation(SimpleBinAnimation animation) {
        this.animation = animation;
    }

    private SetDefaults setDefaults;
    public SetDefaults getSetDefaults() {
        return setDefaults;
    }
    public void setSetDefaults(SetDefaults setDefaults) {
        this.setDefaults = setDefaults;
    }


    public String getAdjustedID() {
        if (setDefaults == null) return getAttribute("id").stringValue();
        else return setDefaults.getAttribute("idprefix").stringValue() + getAttribute("id").stringValue();
    }


    public String getAdjustedPath() {
        if (setDefaults == null || setDefaults.getAttribute("path").stringValue().equals("./")) return getAttribute("path").stringValue();
        else return setDefaults.getAttribute("path").stringValue() + getAttribute("path").stringValue();
    }


    public FlashAnim(EditorObject _parent, GameVersion version) {
        super(_parent, version);
    }

    @Override
    public String getName() {
        return getAttribute("id").stringValue();
    }

}
