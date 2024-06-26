package com.woogleFX.engine.undoHandling.userActions;

import com.woogleFX.engine.gui.alarms.ErrorAlarm;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class DestroyFileAction extends UserAction {

    private final String path;
    private final byte[] contents;
    public DestroyFileAction(String path, byte[] contents) {
        super(null);
        this.path = path;
        this.contents = contents;
    }


    @Override
    public UserAction getInverse() {
        return new CreateFileAction(path, contents);
    }


    @Override
    public void execute() {
        try {
            Files.delete(Path.of(path));
        } catch (IOException e) {
            ErrorAlarm.show(e);
        }
    }

}
