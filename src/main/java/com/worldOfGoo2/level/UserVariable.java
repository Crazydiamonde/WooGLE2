package com.worldOfGoo2.level;

import com.woogleFX.editorObjects.EditorObject;
import com.woogleFX.assets.GameVersion;

public class UserVariable extends EditorObject {

    public UserVariable(EditorObject parent, GameVersion version) {
        super(parent, version);
    }


    @Override
    public String getName() {

        if (((_2_Level_Item)getParent()).getItem().getChildren("userVariables").size() <= (getParent().getChildren("userVariables").indexOf(this))) return "";
        return ((_2_Level_Item)getParent()).getItem().getChildren("userVariables").get(getParent().getChildren("userVariables").indexOf(this)).getAttribute("name").stringValue();

    }

}
