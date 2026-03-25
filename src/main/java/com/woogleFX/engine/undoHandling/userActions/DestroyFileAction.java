package com.woogleFX.engine.undoHandling.userActions;

import com.woogleFX.engine.gui.alarms.ErrorAlarm;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class DestroyFileAction extends UserAction {

    private final String source;
    private final String path;
    public DestroyFileAction(String source, String path) {
        super(null);
        this.source = source;
        this.path = path;
    }


    @Override
    public UserAction getInverse() {
        return new CreateFileAction(source, path);
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
