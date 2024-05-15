package com.WooGLEFX.Functions;

import com.WooGLEFX.Engine.FX.FXContainers;
import com.WooGLEFX.Engine.FX.FXLevelSelectPane;
import com.WooGLEFX.GUI.Alarms;
import com.WooGLEFX.Structures.SimpleStructures.LevelTab;
import com.WooGLEFX.Structures.WorldLevel;
import javafx.scene.control.TabPane;

public class LevelCloser {

    public static void resumeLevelClosing() {
        TabPane levelSelectPane = FXLevelSelectPane.getLevelSelectPane();
        if (levelSelectPane.getTabs().size() == 0) {
            FXContainers.getStage().close();
        } else {
            try {
                LevelTab levelTab = (LevelTab) levelSelectPane.getTabs().get(levelSelectPane.getTabs().size() - 1);
                if (levelTab.getLevel().getEditingStatus() == WorldLevel.UNSAVED_CHANGES) {
                    Alarms.closeTabMessage2(levelTab, levelTab.getLevel());
                } else {
                    levelTab.getTabPane().getTabs().remove(levelTab);
                    resumeLevelClosing();
                }
            } catch (Exception e) {
                Alarms.errorMessage(e);
            }
        }
    }

}