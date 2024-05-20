package com.WooGLEFX.Functions;

import com.WooGLEFX.Engine.FX.FXEditorButtons;
import com.WooGLEFX.Engine.FX.FXMenu;
import com.WooGLEFX.EditorObjects._Ball;
import com.WooGLEFX.File.FileManager;
import com.WooGLEFX.File.ResourceManagers.GlobalResourceManager;
import com.WooGLEFX.File.ResourceManagers.BallManager;
import com.WooGLEFX.Engine.GUI.Alarms;
import com.WooGLEFX.EditorObjects.EditorObject;
import com.WooGLEFX.Structures.GameVersion;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;

public class GameResourceManager {

    private static final Logger logger = LoggerFactory.getLogger(GameResourceManager.class);


    /** Reloads all the resources in a World of Goo version. */
    public static void reloadWorldOfGoo(GameVersion version) {

        PaletteManager.clear();

        try {
            FileManager.readWOGdirs();
        } catch (ParserConfigurationException | SAXException | IOException e) {
            Alarms.errorMessage(e);
        }

        BallManager.getImportedBalls().removeIf(ball -> ball.getVersion() == version);

        LevelResourceManager.importGameResources(version);

        for (int i = 0; i < PaletteManager.getPaletteBalls().size(); i++) {

            String ballName = PaletteManager.getPaletteBalls().get(i);
            GameVersion ballVersion = PaletteManager.getPaletteVersions().get(i);

            logger.debug(ballName + ", " + ballVersion);

            if (ballVersion == version) {

                try {
                    _Ball ball = FileManager.openBall(ballName, ballVersion);

                    for (EditorObject resrc : FileManager.commonBallResrcData) {
                        GlobalResourceManager.addResource(resrc, ballVersion);
                    }

                    if (ball != null) {
                        ball.makeImages(ballVersion);
                        ball.setVersion(ballVersion);
                        BallManager.getImportedBalls().add(ball);
                    }
                } catch (ParserConfigurationException | SAXException | IOException e) {
                    Alarms.errorMessage(e);
                }
            }
        }
    }


    /** Changes the location of a World of Goo version. */
    public static boolean changeWorldOfGooDirectory(GameVersion version) {
        FileChooser findWorldOfGoo = new FileChooser();
        findWorldOfGoo.getExtensionFilters()
                .add(new FileChooser.ExtensionFilter("World of Goo executable", "WorldOfGoo.exe"));
        File worldOfGoo = findWorldOfGoo.showOpenDialog(new Stage());
        if (worldOfGoo == null) {
            return false;
        } else {
            if (version == GameVersion.OLD) {
                FileManager.setOldWOGdir(worldOfGoo.getParent());
                try {
                    FileManager.saveProperties();
                } catch (IOException e) {
                    Alarms.errorMessage(e);
                }
                reloadWorldOfGoo(GameVersion.OLD);
            } else {
                FileManager.setNewWOGdir(worldOfGoo.getParent() + "\\game");
                try {
                    FileManager.saveProperties();
                } catch (IOException e) {
                    Alarms.errorMessage(e);
                }
                reloadWorldOfGoo(GameVersion.NEW);
            }
            if(FXEditorButtons.getOldGooballsToolbar() != null) {
                FXEditorButtons.getOldGooballsToolbar().getItems().clear();
            }
            if(FXEditorButtons.getNewGooballsToolbar() != null) {
                FXEditorButtons.getNewGooballsToolbar().getItems().clear();
            }
            FXEditorButtons.addBallsTo();
            FXEditorButtons.updateAllButtons();
            FXMenu.updateAllButtons();
            return true;
        }
    }




}
