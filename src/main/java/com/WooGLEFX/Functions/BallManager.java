package com.WooGLEFX.Functions;

import com.WooGLEFX.EditorObjects._Ball;
import com.WooGLEFX.File.FileManager;
import com.WooGLEFX.File.LevelExporter;
import com.WooGLEFX.GUI.Alarms;
import com.WooGLEFX.GUI.BallSelector;
import javafx.stage.Stage;

import java.util.ArrayList;

public class BallManager {

    private static final ArrayList<_Ball> importedBalls = new ArrayList<>();
    public static ArrayList<_Ball> getImportedBalls() {
        return importedBalls;
    }



    public static void saveBallInVersion(double oldVersion, double newVersion) {
        new BallSelector(oldVersion, newVersion).start(new Stage());
    }

    public static void saveBallInVersion(String ball, double oldVersion, double newVersion) {

        try {

            _Ball ballObject = FileManager.openBall(ball, oldVersion);

            if (ballObject == null) return;

            if (newVersion == 1.3) {
                LevelExporter.exportBallAsXML(ballObject,
                        FileManager.getOldWOGdir() + "\\res\\balls\\" + ball,
                        1.3, false);
            } else if (newVersion == 1.5) {
                LevelExporter.exportBallAsXML(ballObject,
                        FileManager.getNewWOGdir() + "\\res\\balls\\" + ball,
                        1.5, false);
            }
        } catch (Exception e) {
            Alarms.errorMessage(e);
        }
    }

}
