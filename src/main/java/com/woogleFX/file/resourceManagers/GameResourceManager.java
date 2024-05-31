package com.woogleFX.file.resourceManagers;

import com.woogleFX.engine.fx.FXEditorButtons;
import com.woogleFX.engine.fx.FXMenu;
import com.woogleFX.file.FileManager;
import com.woogleFX.engine.gui.Alarms;
import com.woogleFX.gameData.ball.PaletteManager;
import com.woogleFX.gameData.level.GameVersion;
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

    }


    /** Changes the location of a World of Goo version. */
    public static boolean changeWorldOfGooDirectory(GameVersion version, boolean editorJustLaunched) {
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
            if (FXEditorButtons.getOldGooballsToolbar() != null) {
                FXEditorButtons.getOldGooballsToolbar().getItems().clear();
            }
            if (FXEditorButtons.getNewGooballsToolbar() != null) {
                FXEditorButtons.getNewGooballsToolbar().getItems().clear();
            }
            if (!editorJustLaunched) FXEditorButtons.addBallsTo();
            if (!editorJustLaunched) FXEditorButtons.updateAllButtons();
            if (!editorJustLaunched) FXMenu.updateAllButtons();
            return true;
        }
    }




}
