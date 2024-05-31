package com.woogleFX.engine.undoHandling.userActions;

import com.woogleFX.engine.gui.Alarms;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class CreateFileAction extends UserAction {

    private final String path;
    private final byte[] contents;
    public CreateFileAction(String path, byte[] contents) {
        super(null);
        this.path = path;
        this.contents = contents;
    }


    @Override
    public UserAction getInverse() {
        return new DestroyFileAction(path, contents);
    }


    @Override
    public void execute() {
        try {
            Path actualPath = Path.of(path);
            Files.createFile(actualPath);
            Files.write(actualPath, contents);
        } catch (IOException e) {
            Alarms.errorMessage(e);
        }
    }

}
