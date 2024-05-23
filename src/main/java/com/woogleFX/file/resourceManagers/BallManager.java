package com.woogleFX.file.resourceManagers;

import com.woogleFX.editorObjects._Ball;
import com.woogleFX.file.FileManager;
import com.woogleFX.file.fileExport.BallWriter;
import com.woogleFX.engine.gui.Alarms;
import com.woogleFX.engine.gui.BallSelector;
import com.woogleFX.structures.GameVersion;
import javafx.stage.Stage;

import java.util.ArrayList;

public class BallManager {

    private static final ArrayList<_Ball> importedBalls = new ArrayList<>();
    public static ArrayList<_Ball> getImportedBalls() {
        return importedBalls;
    }


    public static void saveBallInVersion(GameVersion oldVersion, GameVersion newVersion) {
        new BallSelector(oldVersion, newVersion).start(new Stage());
    }


    public static void saveBallInVersion(String ball, GameVersion oldVersion, GameVersion newVersion) {

        try {

            _Ball _ball = FileManager.openBall(ball, oldVersion);
            if (_ball == null) return;

            String dir = newVersion == GameVersion.OLD ? FileManager.getOldWOGdir() : FileManager.getNewWOGdir();

            BallWriter.exportBallAsXML(_ball, dir + "\\res\\balls\\" + ball, newVersion, false);

        } catch (Exception e) {
            Alarms.errorMessage(e);
        }

    }

}
