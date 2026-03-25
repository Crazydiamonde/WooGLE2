package com.woogleFX.engine.undoHandling.userActions;

import com.woogleFX.engine.gui.alarms.ErrorAlarm;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class CreateFileAction extends UserAction {

    private final String source;
    private final String path;
    public CreateFileAction(String source, String path) {
        super(null);
        this.source = source;
        this.path = path;
    }


    @Override
    public UserAction getInverse() {
        return new DestroyFileAction(source, path);
    }


    @Override
    public void execute() {
        try {
            Files.copy(Path.of(source), Path.of(path), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            ErrorAlarm.show(e);
        }
    }

}
