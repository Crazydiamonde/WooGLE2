package com.woogleFX.functions.undoHandling.userActions;

import com.woogleFX.editorObjects.EditorObject;
import com.woogleFX.functions.LevelManager;
import com.woogleFX.functions.LevelResourceManager;
import com.woogleFX.functions.ObjectManager;

public class ImportResourceAction extends UserAction {

    private final String path;
    public ImportResourceAction(EditorObject object, String path) {
        super(object);
        this.path = path;
    }


    @Override
    public UserAction getInverse() {
        return null;
    }


    @Override
    public void execute() {
        LevelResourceManager.deleteResource(LevelManager.getLevel(),
                getObject().getAttribute("path").stringValue());
        ObjectManager.deleteItem(LevelManager.getLevel(), getObject(), false);
    }

}
