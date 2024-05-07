package com.WooGLEFX.Functions;

import com.WooGLEFX.Engine.FX.FXCreator;
import com.WooGLEFX.Engine.Main;
import com.WooGLEFX.EditorObjects._Ball;
import com.WooGLEFX.File.FileManager;
import com.WooGLEFX.File.GlobalResourceManager;
import com.WooGLEFX.GUI.Alarms;
import com.WooGLEFX.Structures.EditorObject;
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
    public static void reloadWorldOfGoo(double version) {

        FileManager.getPaletteBalls().clear();
        FileManager.getPaletteVersions().clear();

        try {
            FileManager.readWOGdirs();
        } catch (ParserConfigurationException | SAXException | IOException e) {
            Alarms.errorMessage(e);
        }

        Main.getImportedBalls().removeIf(ball -> ball.getVersion() == version);

        LevelResourceManager.importGameResources(version);

        for (int i = 0; i < FileManager.getPaletteBalls().size(); i++) {

            String ballName = FileManager.getPaletteBalls().get(i);
            double ballVersion = FileManager.getPaletteVersions().get(i);

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
                        Main.getImportedBalls().add(ball);
                    }
                } catch (ParserConfigurationException | SAXException | IOException e) {
                    Alarms.errorMessage(e);
                }
            }
        }
    }


    /** Changes the location of a World of Goo version. */
    public static boolean changeWorldOfGooDirectory(double version) {
        FileChooser findWorldOfGoo = new FileChooser();
        findWorldOfGoo.getExtensionFilters()
                .add(new FileChooser.ExtensionFilter("World of Goo executable", "WorldOfGoo.exe"));
        File worldOfGoo = findWorldOfGoo.showOpenDialog(new Stage());
        if (worldOfGoo == null) {
            return false;
        } else {
            if (version == 1.3) {
                FileManager.setOldWOGdir(worldOfGoo.getParent());
                try {
                    FileManager.saveProperties();
                } catch (IOException e) {
                    Alarms.errorMessage(e);
                }
                reloadWorldOfGoo(1.3);
                FXCreator.buttonOpenOld.setDisable(false);
                FXCreator.buttonNewOld.setDisable(false);
                FXCreator.newLevelOldItem.setDisable(false);
                FXCreator.openLevelOldItem.setDisable(false);
                if (LevelManager.getLevel() != null) {
                    FXCreator.buttonSave.setDisable(false);
                    FXCreator.cloneLevelItem.setDisable(false);
                    FXCreator.saveLevelItem.setDisable(false);
                }
            } else {
                FileManager.setNewWOGdir(worldOfGoo.getParent() + "\\game");
                try {
                    FileManager.saveProperties();
                } catch (IOException e) {
                    Alarms.errorMessage(e);
                }
                reloadWorldOfGoo(1.5);
                FXCreator.buttonOpenNew.setDisable(false);
                FXCreator.buttonNewNew.setDisable(false);
                FXCreator.newLevelNewItem.setDisable(false);
                FXCreator.openLevelNewItem.setDisable(false);
                if (LevelManager.getLevel() != null) {
                    FXCreator.buttonClone.setDisable(false);
                    FXCreator.buttonSave.setDisable(false);
                    FXCreator.cloneLevelItem.setDisable(false);
                    FXCreator.saveLevelItem.setDisable(false);
                }
            }
            if(FXCreator.getOldGooballsToolbar() != null) {
                FXCreator.getOldGooballsToolbar().getItems().clear();
            }
            if(FXCreator.getNewGooballsToolbar() != null) {
                FXCreator.getNewGooballsToolbar().getItems().clear();
            }
            FXCreator.addBallsTo();
            return true;
        }
    }




}
