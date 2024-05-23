package com.woogleFX.functions.undoHandling.userActions;

import com.woogleFX.editorObjects.EditorObject;

public class ImportResourceAction extends com.woogleFX.functions.undoHandling.userActions.UserAction {

    private final String path;
    public String getPath() {
        return path;
    }


    public ImportResourceAction(EditorObject object, String path) {
        super(object);
        this.path = path;
    }

}
