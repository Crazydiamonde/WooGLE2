package com.woogleFX.gameData.level;

import com.woogleFX.engine.fx.FXLevelSelectPane;
import com.woogleFX.engine.fx.FXStage;
import com.woogleFX.engine.gui.Alarms;
import com.woogleFX.gameData.level.LevelTab;
import javafx.scene.control.TabPane;

public class LevelCloser {

    public static void resumeLevelClosing() {
        TabPane levelSelectPane = FXLevelSelectPane.getLevelSelectPane();
        if (levelSelectPane.getTabs().isEmpty()) {
            FXStage.getStage().close();
        } else {
            try {
                LevelTab levelTab = (LevelTab) levelSelectPane.getTabs().get(levelSelectPane.getTabs().size() - 1);
                if (levelTab.getLevel().getEditingStatus() == LevelTab.UNSAVED_CHANGES) {
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
